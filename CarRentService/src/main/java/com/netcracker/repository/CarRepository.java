package com.netcracker.repository;

import com.netcracker.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer>{

    @Query(value = "SELECT DISTINCT cars.* FROM cars inner join orders ON cars.id = orders.car_id" +
            " WHERE orders.customer_id =:customerId", nativeQuery = true)
    List<Car> findAllCarsOfCernainCustomer(@Param("customerId") Integer customerId);

}
