package com.netcracker.controller.tableController;

import com.netcracker.authentication.AuthenticationUser;
import com.netcracker.converter.RepairOrderMapper;
import com.netcracker.dto.RepairOrderDTO;
import com.netcracker.entity.Car;
import com.netcracker.entity.Customer;
import com.netcracker.entity.Order;
import com.netcracker.entity.RepairOrder;
import com.netcracker.entity.enums.RepairStatus;
import com.netcracker.exception.BadRequestException;
import com.netcracker.exception.ForbiddenException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.pojoServices.pojoForRepairService.HttpDtoFromRepairService;
import com.netcracker.pojoServices.pojoForRepairService.HttpDtoToRepairService;
import com.netcracker.pojoServices.pojoForRepairService.HttpDtoToFinishFromRepairService;
import com.netcracker.repository.CarRepository;
import com.netcracker.repository.CustomerRepository;
import com.netcracker.repository.OrderRepository;
import com.netcracker.repository.RepairOrderRepository;
import com.netcracker.repository.filtering.RepairRepositoryForFilterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.netcracker.constants.OneDay.ONE_DAY;

@RestController
public class RepairOrderController {

    @Autowired
    private HttpHeaders headers;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RepairOrderMapper repairOrderMapper;

    @Autowired
    private AuthenticationUser authenticationUser;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RepairOrderRepository repairOrderRepository;

    @Autowired
    private RepairRepositoryForFilterQuery repairRepositoryForFilterQuery;

    @Value("${app.endpoint}")
    private String urlService2;

    @GetMapping("/repairOrders")
    public ResponseEntity<List<RepairOrderDTO>> getAllRepairOrders(@RequestParam(name = "id", required = false) List<Integer> id, @RequestParam(name = "carId", required = false) List<Integer> carId,
                                                                   @RequestParam(name = "customerId", required = false) List<Integer> customerId, @RequestParam(name = "idOfExternalTable", required = false) List<Integer> repairIdExternal,
                                                                   @RequestParam(name = "price", required = false) List<Integer> price, @RequestParam(name = "repairStatus", required = false, defaultValue = "") List<RepairStatus> repairStatus,
                                                                   @RequestParam(name = "startRepairDay", required = false) List<Date> startRepairDay, @RequestParam(name = "endRepairDay", required = false) List<Date> endRepairDay) throws ForbiddenException {
        Customer loggedInCustomer = authenticationUser.getAuthenticationUser();
        authenticationUser.checkOnForbiddenAccess(customerId);
        if(loggedInCustomer.getRoleId() == 2 && customerId == null) {
            customerId = new ArrayList<>();
            customerId.add(loggedInCustomer.getId());
        }

        List<RepairOrder> repairOrders = repairRepositoryForFilterQuery.queryFunction(id, carId, customerId, repairIdExternal, price, repairStatus, startRepairDay, endRepairDay);
        List<RepairOrderDTO> repairOrdersDTO = repairOrderMapper.toRepairOrderDTOs(repairOrders);
        return ResponseEntity.ok().body(repairOrdersDTO);
    }

    @GetMapping("/repairOrders/{id}")
    public ResponseEntity<RepairOrderDTO> getRepairOrderById(@PathVariable(value = "id") Integer repairOrderId)
            throws ResourceNotFoundException, ForbiddenException {
        RepairOrder repairOrder = repairOrderRepository.findById(repairOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order was not found with id: " + repairOrderId));

        authenticationUser.checkOnForbiddenAccess(repairOrder);
        RepairOrderDTO repairOrderDTO = repairOrderMapper.toRepairOrderDTO(repairOrder);
        return ResponseEntity.ok().body(repairOrderDTO);
    }

    @GetMapping("/repairOrders/{id}/updateStatus")
    public ResponseEntity<RepairOrderDTO> updateStatusOfRepairOrder(@PathVariable(value = "id") Integer repairOrderId)
            throws ResourceNotFoundException, BadRequestException, ForbiddenException {
        RepairOrder repairOrder = repairOrderRepository.findById(repairOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order was not found with id: " + repairOrderId));
        authenticationUser.checkOnForbiddenAccess(repairOrder);
        if(repairOrder.getRepairIdExternal() == null) {
            throw new BadRequestException("Repair service has not accepted this repair order yet");
        }

        //получение DTO из второго сервиса и изменение параметров в своей таблице
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HttpDtoToFinishFromRepairService> entity = new HttpEntity<>(headers);
        ResponseEntity<HttpDtoToFinishFromRepairService> httpDtoFromService2 = restTemplate
                .exchange(urlService2 + "/" + repairOrder.getRepairIdExternal(), HttpMethod.GET, entity, HttpDtoToFinishFromRepairService.class);
        if(httpDtoFromService2.getStatusCode().value() == 200 && httpDtoFromService2.getBody().getPrice() != null) {
            repairOrder.setPrice(httpDtoFromService2.getBody().getPrice());
            repairOrder.setRepairStatus(RepairStatus.FINISHED);
        }
        final RepairOrder updatedRepairOrder = repairOrderRepository.save(repairOrder);
        final RepairOrderDTO updatedrepairOrderDTO = repairOrderMapper.toRepairOrderDTO(updatedRepairOrder);
        return ResponseEntity.ok().body(updatedrepairOrderDTO);
    }

    @PostMapping("/repairOrders")
    public ResponseEntity<RepairOrderDTO> createRepairOrder(@Valid @RequestBody RepairOrderDTO repairOrderDTOToSave)
            throws MethodArgumentNotValidException, HttpMessageNotReadableException,
            BadRequestException, ResourceNotFoundException {
        if(repairOrderDTOToSave.getId() != null && repairOrderRepository.findById(repairOrderDTOToSave.getId()).isPresent()) {
            throw new BadRequestException("Such id already exists");
        }
        if(!repairOrderDTOToSave.getRepairStatus().equals(RepairStatus.PENDING)) {
            throw new BadRequestException("Repair status should be PENDING");
        }
        //сохраняем во внутреннюю БД
        RepairOrder repairOrderToSave = repairOrderMapper.toRepairOrder(repairOrderDTOToSave);
        checkIfThisCarAlreadyInRepair(repairOrderToSave);
        checkIfThisCarHasUnfinishedRentOrder(repairOrderToSave);
        final RepairOrder createdRepairOrder = repairOrderRepository.save(repairOrderToSave);

        //создание DTO и отправка на второй сервис
        HttpDtoToRepairService postDTOFromService1 = HttpDtoToRepairService
                .create(createdRepairOrder.getId(), createdRepairOrder.getStartRepairDay());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HttpDtoToRepairService> request = new HttpEntity<>(postDTOFromService1, headers);
        ResponseEntity<HttpDtoFromRepairService> response = restTemplate
                .exchange(urlService2, HttpMethod.POST, request, HttpDtoFromRepairService.class);

        //обработка ответа со второго сервиса
        if (response.getStatusCode().value() == 200) {
            createdRepairOrder.setEndRepairDay(response.getBody().getEndDay());
            createdRepairOrder.setRepairIdExternal(response.getBody().getIdExternal());
            createdRepairOrder.setRepairStatus(RepairStatus.IN_PROGRESS);
            repairOrderRepository.save(createdRepairOrder);
        }

        final RepairOrderDTO createdRepairOrderDTO = repairOrderMapper.toRepairOrderDTO(createdRepairOrder);
        return ResponseEntity.ok().body(createdRepairOrderDTO);
    }

    @PutMapping("/repairOrders/{id}/finished")
    public ResponseEntity<RepairOrderDTO> finishStatusOfRepairOrder(@PathVariable(value = "id") Integer orderRepairId,
             @Valid @RequestBody HttpDtoToFinishFromRepairService httpDtoFromService2)
            throws ResourceNotFoundException {
        RepairOrder repairOrder = repairOrderRepository.findById(orderRepairId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order was not found with id: " + orderRepairId));
        if(httpDtoFromService2.getPrice() != null) {
            repairOrder.setPrice(httpDtoFromService2.getPrice());
            repairOrder.setRepairStatus(RepairStatus.FINISHED);
        }
        final RepairOrder updatedRepairOrder = repairOrderRepository.save(repairOrder);
        final RepairOrderDTO updatedRepairOrderDTO = repairOrderMapper.toRepairOrderDTO(updatedRepairOrder);
        return ResponseEntity.ok().body(updatedRepairOrderDTO);
    }

    @DeleteMapping("/repairOrders/{id}")
    public ResponseEntity<RepairOrderDTO> deleteRepairOrder(@PathVariable(value = "id") Integer repairOrderId)
            throws ResourceNotFoundException {
        RepairOrder repairOrder = repairOrderRepository.findById(repairOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order was not found with id: " + repairOrderId));
        repairOrderRepository.delete(repairOrder);
        return new ResponseEntity<RepairOrderDTO>(HttpStatus.NO_CONTENT);
    }

    private void checkIfThisCarAlreadyInRepair(RepairOrder repairOrderToSave) throws BadRequestException, ResourceNotFoundException {
        //проверка на то, что машина уже сломалась и в сервисе
        Car car = carRepository.findById(repairOrderToSave.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car was not found with id: " + repairOrderToSave.getCarId()));
        if (!car.getRepairOrders().isEmpty()) {
            for (RepairOrder repairOrder : car.getRepairOrders()) {
                if(!repairOrder.getRepairStatus().equals(RepairStatus.FINISHED)) {
                    throw new BadRequestException("This car is already in repair");
                }
            }
        }
    }

    private void checkIfThisCarHasUnfinishedRentOrder(RepairOrder repairOrderToSave) throws BadRequestException, ResourceNotFoundException {
        Car car = carRepository.findById(repairOrderToSave.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car was not found with id: " + repairOrderToSave.getCarId()));
        //если заказ на аренду этой машины существует, то заканчиваем заказ датой поломки
        if(!car.getOrders().isEmpty() && car.getOrderIdThatIsNotEnded() != null) {
            Order order = orderRepository.findById(car.getOrderIdThatIsNotEnded())
                    .orElseThrow(() -> new ResourceNotFoundException("Order was not found with id: " + car.getOrderIdThatIsNotEnded()));
            Customer customer = customerRepository.findById(order.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer was not found with id: " + order.getCustomerId()));
            if(repairOrderToSave.getStartRepairDay().before(order.getStartDay())) {
                throw new BadRequestException("Start day of repair cannot be earlier then start day of rent: " + new SimpleDateFormat("dd.MM.yyyy").format(order.getStartDay()));
            }

            //в случае закрытия заказа на аренду считаем стоимость заказа с учетом скидки
            double discount = 0;
            int daysOfRent =(int)((repairOrderToSave.getStartRepairDay().getTime() - order.getStartDay().getTime())  / ONE_DAY);
            if(customer.getDiscount() != null) {
                discount = (double) customer.getDiscount() / 100;
            }
            order.setEndDay(repairOrderToSave.getStartRepairDay());
            order.setOrderPrice(car.getCost() * daysOfRent - (discount * (car.getCost() * daysOfRent)));
            repairOrderToSave.setCustomerId(order.getCustomerId());
        }
    }

}
