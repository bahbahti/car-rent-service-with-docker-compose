package com.netcracker.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_day", nullable = false)
    private Date startDay;

    @Column(name = "end_day", nullable = true)
    private Date endDay;

    @Column(name = "car_id", nullable = false)
    private Integer carId;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "order_price", nullable = true)
    private Double orderPrice;

}