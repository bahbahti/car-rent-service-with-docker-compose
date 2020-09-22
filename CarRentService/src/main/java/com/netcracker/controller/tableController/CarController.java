package com.netcracker.controller.tableController;

import com.netcracker.converter.CarMapper;
import com.netcracker.converter.CustomerMapper;
import com.netcracker.converter.RepairOrderMapper;
import com.netcracker.dto.CarDTO;
import com.netcracker.dto.CustomerDTO;
import com.netcracker.dto.RepairOrderDTO;
import com.netcracker.entity.Car;
import com.netcracker.entity.Customer;
import com.netcracker.entity.RepairOrder;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.repository.CarRepository;
import com.netcracker.repository.CustomerRepository;
import com.netcracker.repository.RepairOrderRepository;
import com.netcracker.repository.filtering.CarRepositoryForFilterQuery;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
public class CarController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RepairOrderRepository repairOrderRepository;

    @Autowired
    private CarRepositoryForFilterQuery carRepositoryForFilterQuery;

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private RepairOrderMapper repairOrderMapper;

    @GetMapping("/cars")
    @ResponseBody
    public ResponseEntity<List<CarDTO>> getAllCars(@RequestParam(name = "id", required = false) List<Integer> id, @RequestParam(name = "name", required = false, defaultValue = "") List<String> name,
                                                   @RequestParam(name = "cost", required = false) List<Integer> cost, @RequestParam(name = "isAvailable", required = false) Boolean isAvailable,
                                                   @RequestParam(name = "color", required = false, defaultValue = "") List<String> color, @RequestParam(name = "storage", required = false, defaultValue = "") List<String> storage,
                                                   @RequestParam(name = "registrationNumber", required = false, defaultValue = "") List<String> registrationNumber) {
        List<Car> cars = carRepositoryForFilterQuery.queryFunction(id, name, cost, isAvailable, color, storage, registrationNumber);
        List<CarDTO> carsDTO = carMapper.toCarDTOs(cars);
        return ResponseEntity.ok().body(carsDTO);
    }

    @GetMapping("/cars/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable(value = "id") Integer carId)
            throws ResourceNotFoundException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car was not found with id: " + carId));
        CarDTO carDTO = carMapper.toCarDTO(car);
        return ResponseEntity.ok().body(carDTO);
    }

    @GetMapping("/cars/{id}/customers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomersOfCernainCar(@PathVariable(value = "id") Integer carId)
            throws ResourceNotFoundException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car was not found with id: " + carId));
        List<Customer> customers = customerRepository.findAllCustomersOfCernainCar(carId);
        List<CustomerDTO> customersDTO = customerMapper.toCustomerDTOs(customers);
        return ResponseEntity.ok().body(customersDTO);
    }

    @GetMapping("/cars/{id}/repairOrders")
    public ResponseEntity<List<RepairOrderDTO>> getAllRepairOrdersOfCernainCar(@PathVariable(value = "id") Integer carId)
            throws ResourceNotFoundException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car was not found with id: " + carId));
        List<RepairOrder> repairOrders = repairOrderRepository.findAllRepairOrdersOfCernainCar(carId);
        List<RepairOrderDTO> repairOrdersDTO = repairOrderMapper.toRepairOrderDTOs(repairOrders);
        return ResponseEntity.ok().body(repairOrdersDTO);
    }

    @PostMapping("/cars")
    public ResponseEntity<CarDTO> createCar(@Valid @RequestBody CarDTO carDTOToSave)
            throws MethodArgumentNotValidException, HttpMessageNotReadableException,
            DataIntegrityViolationException, HibernateException {
        Car carToSave = carMapper.toCar(carDTOToSave);
        final Car createdCar = carRepository.save(carToSave);
        final CarDTO createdCarDTO = carMapper.toCarDTO(createdCar);
        return ResponseEntity.ok().body(createdCarDTO);
    }

    @DeleteMapping("/cars/{id}")
    public ResponseEntity<CarDTO> deleteCar(@PathVariable(value = "id") Integer carId)
            throws ResourceNotFoundException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car was not found with id: " + carId));
        carRepository.delete(car);
        return new ResponseEntity<CarDTO>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/cars/{id}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable(value = "id") Integer carId, @Valid @RequestBody CarDTO newCarDTO)
            throws ResourceNotFoundException, MethodArgumentNotValidException, HttpMessageNotReadableException,
            HibernateException, DataIntegrityViolationException{
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car was not found with id: " + carId));
        Car newCar = carMapper.toCar(newCarDTO);
        car.setCost(newCar.getCost());
        car.setName(newCar.getName());
        car.setStorage(newCar.getStorage());
        car.setRegistrarionNumber(newCar.getRegistrarionNumber());
        car.setColor(newCar.getColor());
        final Car updatedCar = carRepository.save(car);
        final CarDTO updaredCarDTO = carMapper.toCarDTO(updatedCar);
        return ResponseEntity.ok().body(updaredCarDTO);
    }

}
