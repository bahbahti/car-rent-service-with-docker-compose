package com.netcracker.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.netcracker.entity.enums.RepairStatus;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Table(name = "cars")
@Data
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrarionNumber;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "storage", nullable = false)
    private String storage;

    @OneToMany(mappedBy = "carId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, targetEntity = Order.class)
    private List<Order> orders;

    @OneToMany(mappedBy = "carId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, targetEntity = RepairOrder.class)
    private List<RepairOrder> repairOrders;

    @Transient
    private Boolean isAvailable = true;

    //для возможности изменения (UPDATE) заказа с параметром isAnavailable = false
    @Transient
    private Integer orderIdThatIsNotEnded;

    //для сравнения старта нового заказа и окончания последнего заказа
    @Transient
    private Date lastOrderDay;

    @JsonGetter(value = "isAvailable")
    public Boolean getIsAvailable() {
        return isAvailable;
    }

    @JsonSetter(value = "isAvailable")
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @PostLoad
    private void postLoad() {

        //на данную машину заказов нет
        if (orders.isEmpty()) {
            isAvailable = true;
        }

        //на данную машину заказ только один
        else if (orders.size() == 1) {
            if (orders.iterator().next().getEndDay() == null) {
                orderIdThatIsNotEnded = orders.iterator().next().getId();
                isAvailable = false;
            } else {
                isAvailable = true;
                lastOrderDay = orders.iterator().next().getEndDay();
            }
        }

        //на данную машину заказов много
        else {
            for (int i = 0; i < orders.size() - 1; i++) {
                for (int k = i + 1; k < orders.size(); k++) {
                    if (orders.get(i).getEndDay() == null || orders.get(k).getEndDay() == null) {
                        if (orders.get(i).getEndDay() == null) {
                            orderIdThatIsNotEnded = orders.get(i).getId();
                        } else {
                            orderIdThatIsNotEnded = orders.get(k).getId();
                        }
                        lastOrderDay = null;
                        break;
                    } else if (orders.get(i).getEndDay().after(orders.get(k).getEndDay())) {
                        lastOrderDay = orders.get(i).getEndDay();
                    } else if (orders.get(i).getEndDay().equals(orders.get(k).getEndDay())) {
                        lastOrderDay = orders.get(i).getEndDay();
                    } else {
                        lastOrderDay = orders.get(k).getEndDay();
                    }
                }
            }

            if (lastOrderDay == null) {
                isAvailable = false;
            } else {
                isAvailable = true;
            }

        }

        //проверка на поломки
        for (RepairOrder repairOrder : repairOrders) {
            if (!repairOrder.getRepairStatus().equals(RepairStatus.FINISHED)) {
                isAvailable = false;
                break;
            }
        }
    }

}
