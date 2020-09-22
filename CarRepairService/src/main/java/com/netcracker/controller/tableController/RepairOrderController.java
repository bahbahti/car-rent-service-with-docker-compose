package com.netcracker.controller.tableController;

import com.netcracker.entity.RepairOrder;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.pojoServices.pojoForRepairService.HttpDtoFromRentService;
import com.netcracker.pojoServices.pojoForRepairService.HttpDtoToFinishRepairOnRentService;
import com.netcracker.pojoServices.pojoForRepairService.HttpDtoToRentService;
import com.netcracker.repository.RepairOrderRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
public class RepairOrderController {

    @Autowired
    private RepairOrderRepository repairOrderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpHeaders headers;

    @Autowired
    private TaskScheduler taskScheduler;

    private List<String> listOfMasters = Arrays.asList("Alex", "Bob", "John", "James", "Chris", "Paul",
            "Serj", "Leo", "Howard", "Tony");

    private List<String> listOfBrokenDetails = Arrays.asList("Windshield", "Bumper", "Fender", "Headlight",
            "Hood", "Wheel", "Tire", "Transmission");

    private int randomPrice;

    private int randomMaster;

    private int randomBrokenDetail;

    private int randomValueForDateRepair;

    private final static int ONE_DAY = 24*60*60*1000;

    @Value("${app.endpoint}")
    private String urlService1;

    @GetMapping("/repairService/{id}")
    public ResponseEntity<HttpDtoToFinishRepairOnRentService> getPriceOfRepairOrderById(@PathVariable(value = "id") Integer repairOrderId)
            throws ResourceNotFoundException {
        RepairOrder repairOrder = repairOrderRepository.findById(repairOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order not found with id: " + repairOrderId));
        HttpDtoToFinishRepairOnRentService postDTOToService1 = HttpDtoToFinishRepairOnRentService.create(repairOrder.getPrice());
        return ResponseEntity.ok().body(postDTOToService1);
    }

    @PostMapping("/repairService")
    public ResponseEntity<HttpDtoToRentService> createRepairOrder(@RequestBody HttpDtoFromRentService postDTOFromService1)
            throws ResourceAccessException {
        randomPrice = (int)(Math.random() * 1000 + 1);
        randomMaster = (int) (Math.random() * 10);
        randomBrokenDetail = (int) (Math.random() * 8);
        randomValueForDateRepair = (int) (Math.random() * 20);

        //сохраняем переданный с первого сервиса заказ в БД
        RepairOrder repairOrder = new RepairOrder(new Date(postDTOFromService1.getStartDay().getTime() + ONE_DAY * randomValueForDateRepair),
                                                  listOfMasters.get(randomMaster),
                                                  listOfBrokenDetails.get(randomBrokenDetail),
                                                  postDTOFromService1.getIdExternal());
        final RepairOrder createdRepairOrder = repairOrderRepository.save(repairOrder);

        HttpDtoToRentService responseToService1 = HttpDtoToRentService.create(createdRepairOrder.getId(), createdRepairOrder.getEndRepairDay());

        //устанавливаем цену выполненого заказа через 60 секунд
        taskScheduler.schedule(() -> {
                    createdRepairOrder.setPrice(randomPrice);
                    repairOrderRepository.save(createdRepairOrder);
                },
                new Date(OffsetDateTime.now().plusSeconds(60).toInstant().toEpochMilli())
        );

        //отсылаем цену выполненого заказа первому сервису, после чего первый сервис закрывает заказ на ремонт. Отправляем через 120 секунд.
        taskScheduler.schedule(() -> {
            String plainCreds = "admin:password";
            byte[] plainCredsBytes = plainCreds.getBytes();
            byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
            String base64Creds = new String(base64CredsBytes);
            headers.add("Authorization", "Basic " + base64Creds);
            HttpDtoToFinishRepairOnRentService putDTOFromService2 = HttpDtoToFinishRepairOnRentService.create(createdRepairOrder.getPrice());
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<HttpDtoToFinishRepairOnRentService> request = new HttpEntity<>(putDTOFromService2, headers);
            ResponseEntity<HttpDtoToFinishRepairOnRentService> responseFromService1 = restTemplate
                    .exchange(urlService1 + "/" + createdRepairOrder.getRepairIdExternal() + "/finished", HttpMethod.PUT, request, HttpDtoToFinishRepairOnRentService.class);
            },
            new Date(OffsetDateTime.now().plusSeconds(120).toInstant().toEpochMilli())
        );

        return ResponseEntity.ok().body(responseToService1);
    }

}
