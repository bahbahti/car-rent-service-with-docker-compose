package com.netcracker.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "external_repairs")
@Data
@NoArgsConstructor
public class RepairOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "end_day", nullable = false)
    private Date endRepairDay;

    @Column(name = "price", nullable = true)
    private Integer price;

    @Column(name = "master_name", nullable = false)
    private String masterName;

    @Column(name = "broken_detail", nullable = false)
    private String brokenDetail;

    @Column(name = "repair_id_external", nullable = false)
    private Integer repairIdExternal;

    public RepairOrder(Date endRepairDay, String masterName, String brokenDetail, Integer repairIdExternal) {
        this.endRepairDay = endRepairDay;
        this.masterName = masterName;
        this.brokenDetail = brokenDetail;
        this.repairIdExternal = repairIdExternal;
    }

}
