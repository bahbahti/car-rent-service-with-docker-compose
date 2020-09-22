package com.netcracker.repository;

import com.netcracker.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Integer> {

    @Query(value = "SELECT * FROM repairs WHERE car_id =:carId", nativeQuery = true)
    List<RepairOrder> findAllRepairOrdersOfCernainCar(@Param("carId") Integer carId);

    @Query(value = "SELECT * FROM repairs WHERE customer_id =:customerId", nativeQuery = true)
    List<RepairOrder> findAllRepairOrdersOfCernainCustomer(@Param("customerId") Integer customerId);



}
