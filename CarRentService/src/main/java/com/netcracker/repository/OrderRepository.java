package com.netcracker.repository;

import com.netcracker.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query(value = "SELECT * FROM orders WHERE customer_id =:customerId AND end_day IS NOT NULL", nativeQuery = true)
    List<Order> findPaidOrders(@Param("customerId") Integer customerId);

    @Query(value = "SELECT * FROM orders WHERE customer_id =:customerId AND end_day IS NULL", nativeQuery = true)
    List<Order> findCurrentOrders(@Param("customerId") Integer customerId);

}
