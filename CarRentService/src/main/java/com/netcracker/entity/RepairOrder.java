package com.netcracker.entity;

import com.netcracker.entity.enums.RepairStatus;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "repairs")
@Data
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "car_id", nullable = false)
    private Integer carId;

    @Column(name = "customer_id", nullable = true)
    private Integer customerId;

    @Column(name = "start_day", nullable = false)
    private Date startRepairDay;

    @Column(name = "end_day", nullable = true)
    private Date endRepairDay;

    @Column(name = "repair_id_external", nullable = true)
    private Integer repairIdExternal;

    @Column(name = "price", nullable = true)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_of_repair", nullable = false)
    private RepairStatus repairStatus;

}
