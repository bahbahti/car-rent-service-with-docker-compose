package com.netcracker.repository;

import com.netcracker.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query(value = "SELECT DISTINCT customers.* FROM customers inner join orders ON customers.id = orders.customer_id" +
                   " WHERE orders.car_id =:carId", nativeQuery = true)
    List<Customer> findAllCustomersOfCernainCar(@Param("carId") Integer carId);

    Optional<Customer> findCustomerByUsername (@Param("username") String username);

}
