package com.netcracker.controller.tableController;

import com.netcracker.authentication.AuthenticationUser;
import com.netcracker.converter.CarMapper;
import com.netcracker.converter.CustomerMapper;
import com.netcracker.converter.OrderMapper;
import com.netcracker.converter.RepairOrderMapper;
import com.netcracker.dto.CarDTO;
import com.netcracker.dto.CustomerDTO;
import com.netcracker.dto.OrderDTO;
import com.netcracker.dto.RepairOrderDTO;
import com.netcracker.entity.Car;
import com.netcracker.entity.Customer;
import com.netcracker.entity.Order;
import com.netcracker.entity.RepairOrder;
import com.netcracker.exception.ForbiddenException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.pojoServices.CurrentOrdersDTO;
import com.netcracker.pojoServices.PaidOrdersDTO;
import com.netcracker.repository.CarRepository;
import com.netcracker.repository.CustomerRepository;
import com.netcracker.repository.OrderRepository;
import com.netcracker.repository.RepairOrderRepository;
import com.netcracker.repository.filtering.CustomerRepositoryForFilterQuery;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static com.netcracker.constants.OneDay.ONE_DAY;

@RestController
public class CustomerController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RepairOrderRepository repairOrderRepository;

    @Autowired
    private CustomerRepositoryForFilterQuery customerRepositoryForFilterQuery;

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private RepairOrderMapper repairOrderMapper;

    @Autowired
    private AuthenticationUser authenticationUser;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers(@RequestParam(name = "id", required = false) List<Integer> idList, @RequestParam(name = "firstName", required = false, defaultValue = "") List<String> firstName,
                                                             @RequestParam(name = "lastName", required = false, defaultValue = "") List<String> lastName, @RequestParam(name = "areaOfLiving", required = false, defaultValue = "") List<String> areaOfLiving,
                                                             @RequestParam(name = "discount", required = false) List<Integer> discount, @RequestParam(name = "passportNumber", required = false) List<Integer> passportNumber,
                                                             @RequestParam(name = "phoneNumber", required = false) List<Integer> phoneNumber, @RequestParam(name = "username", required = false, defaultValue = "") List<String> username,
                                                             @RequestParam(name = "password", required = false, defaultValue = "") List<String> password, @RequestParam(name = "roleId", required = false) List<Integer> roleId,
                                                             @RequestParam(name = "enabled", required = false) Boolean enabled) throws ForbiddenException {
        Customer loggedInCustomer = authenticationUser.getAuthenticationUser();
        authenticationUser.checkOnForbiddenAccess(idList);
        if(loggedInCustomer.getRoleId() == 2 && idList == null) {
            idList = new ArrayList<>();
            idList.add(loggedInCustomer.getId());
        }

        List<Customer> customers = customerRepositoryForFilterQuery.queryFunction(idList, firstName, lastName, areaOfLiving, discount, passportNumber, phoneNumber,
                                                                                  username, password, roleId, enabled);
        List<CustomerDTO> customersDTO = customerMapper.toCustomerDTOs(customers);
        return ResponseEntity.ok().body(customersDTO);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable(value = "id") Integer customerId)
            throws ResourceNotFoundException, ForbiddenException {
        authenticationUser.checkOnForbiddenAccess(customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer was not found with id: " + customerId));
        CustomerDTO customerDTO = customerMapper.toCustomerDTO(customer);
        return ResponseEntity.ok().body(customerDTO);
    }

    @GetMapping("/customers/{id}/cars")
    public ResponseEntity<List<CarDTO>> getAllCarsOfCernainCustomer(@PathVariable(value = "id") Integer customerId)
            throws ResourceNotFoundException, ForbiddenException {
        authenticationUser.checkOnForbiddenAccess(customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer was not found with id: " + customerId));

        List<Car> cars = carRepository.findAllCarsOfCernainCustomer(customerId);
        List<CarDTO> carsDTO = carMapper.toCarDTOs(cars);
        return ResponseEntity.ok().body(carsDTO);
    }

    @GetMapping("/customers/{id}/repairOrders")
    public ResponseEntity<List<RepairOrderDTO>> getAllRepairOrdersOfCernainCustomer(@PathVariable(value = "id") Integer customerId)
            throws ResourceNotFoundException, ForbiddenException {
        authenticationUser.checkOnForbiddenAccess(customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer was not found with id: " + customerId));

        List<RepairOrder> repairOrders = repairOrderRepository.findAllRepairOrdersOfCernainCustomer(customerId);
        List<RepairOrderDTO> repairOrdersDTO = repairOrderMapper.toRepairOrderDTOs(repairOrders);
        return ResponseEntity.ok().body(repairOrdersDTO);
    }

    @GetMapping("/customers/{id}/paidOrders")
    public ResponseEntity<PaidOrdersDTO> getPaidOrders(@PathVariable(value = "id") Integer customerId)
            throws ResourceNotFoundException, ForbiddenException {
        double totalPrice = 0;
        authenticationUser.checkOnForbiddenAccess(customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer was not found with id: " + customerId));

        List<Order> orders = orderRepository.findPaidOrders(customerId);
        List<OrderDTO> ordersDTO = orderMapper.toOrderDTOs(orders);
        //считаем сумму уплаченных денег по всем заказам
        for (Order order : orders) {
            totalPrice +=  order.getOrderPrice();
        }
        PaidOrdersDTO paidOrdersDTO = PaidOrdersDTO.create(totalPrice, ordersDTO);
        return ResponseEntity.ok().body(paidOrdersDTO);
    }

    @GetMapping("/customers/{id}/currentOrders")
    public ResponseEntity<CurrentOrdersDTO> getCurrentOrders(@PathVariable(value = "id") Integer customerId)
            throws ResourceNotFoundException, ForbiddenException {
        double debtPrice = 0;
        int daysOfRent = 0;
        double discount = 0;
        Date currentDate = new Date(new java.util.Date().getTime());
        authenticationUser.checkOnForbiddenAccess(customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer was not found with id: " + customerId));

        List<Order> orders = orderRepository.findCurrentOrders(customer.getId());
        List<OrderDTO> ordersDTO = orderMapper.toOrderDTOs(orders);
        //проверка скидки клиента
        if(customer.getDiscount() != null) {
            discount = (double) customer.getDiscount() / 100;
        }
        //устанавливаем цену аренды автомобиля на текущую дату
        for (OrderDTO orderDTO : ordersDTO) {
            daysOfRent =(int)((currentDate.getTime() - orderDTO.getStartDay().getTime())  / ONE_DAY);
            Car car = carRepository.findById(orderDTO.getCarId())
                    .orElseThrow(() -> new ResourceNotFoundException("Car was not found with id: " + orderDTO.getCarId()));
            orderDTO.setOrderPrice(car.getCost() * daysOfRent - (discount * (car.getCost() * daysOfRent)));
        }
        //считаем сумму задолжности по всем текущим заказам
        for (OrderDTO orderDTO : ordersDTO) {
            debtPrice += orderDTO.getOrderPrice();
        }
        CurrentOrdersDTO currentOrdersDTO = CurrentOrdersDTO.create(debtPrice, ordersDTO);
        return ResponseEntity.ok().body(currentOrdersDTO);
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTOToSave)
            throws MethodArgumentNotValidException, HttpMessageNotReadableException,
            DataIntegrityViolationException, HibernateException {
        Customer customerToSave = customerMapper.toCustomer(customerDTOToSave);
        customerToSave.setPassword(bCryptPasswordEncoder.encode(customerToSave.getPassword()));
        final Customer createdCustomer = customerRepository.save(customerToSave);
        final CustomerDTO createdCustomerDTO = customerMapper.toCustomerDTO(createdCustomer);
        return ResponseEntity.ok().body(createdCustomerDTO);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> deleteCustomer(@PathVariable(value = "id") Integer customerId)
            throws ResourceNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer was not found with id: " + customerId));
        customerRepository.delete(customer);
        return new ResponseEntity<CustomerDTO>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable(value = "id") Integer customerId, @Valid @RequestBody CustomerDTO newCustomerDTO)
            throws ResourceNotFoundException, MethodArgumentNotValidException, HttpMessageNotReadableException,
            HibernateException, DataIntegrityViolationException, ForbiddenException {
        authenticationUser.checkOnForbiddenAccess(customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer was not found with id: " + customerId));
        Customer newCustomer = customerMapper.toCustomer(newCustomerDTO);

        customer.setAreaOfLiving(newCustomer.getAreaOfLiving());
        customer.setDiscount(newCustomer.getDiscount());
        customer.setLastName(newCustomer.getLastName());
        customer.setFirstName(newCustomer.getFirstName());
        customer.setPassportNumber(newCustomer.getPassportNumber());
        customer.setPhoneNumber(newCustomer.getPhoneNumber());
        customer.setUsername(newCustomer.getUsername());
        customer.setPassword(bCryptPasswordEncoder.encode(newCustomer.getPassword()));
        customer.setEnabled(newCustomer.getEnabled());
        customer.setRoleId(newCustomer.getRoleId());
        final Customer updatedCustomer = customerRepository.save(customer);
        final CustomerDTO updatedCustomerDTO = customerMapper.toCustomerDTO(updatedCustomer);
        return ResponseEntity.ok().body(updatedCustomerDTO);
    }

}