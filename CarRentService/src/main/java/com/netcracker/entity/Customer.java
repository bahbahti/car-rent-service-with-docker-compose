package com.netcracker.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "customers",
uniqueConstraints = {@UniqueConstraint(columnNames = {"first_name", "last_name"})}
)
@Data
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "area_of_living", nullable = false)
    private String areaOfLiving;

    @Column(name = "discount", nullable = true)
    private Integer discount;

    @Column(name = "passport_number", nullable = false, unique = true)
    private Integer passportNumber;

    @Column(name = "phone_number", nullable = true)
    private Integer phoneNumber;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer roleId;

    @Column(nullable = false)
    private Boolean enabled = true;

    @OneToMany(mappedBy = "customerId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, targetEntity = Order.class)
    private List<Order> orders;

    @OneToMany(mappedBy = "customerId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, targetEntity = RepairOrder.class)
    private List<RepairOrder> repairOrders;



}
