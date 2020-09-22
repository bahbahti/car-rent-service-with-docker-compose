package com.netcracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.sql.Date;

@Data
public class OrderDTO {

    @JsonProperty("id")
    @Min(value = 1, message = "Id can't be less than 1")
    private Integer id;

    @JsonProperty("car_id")
    @Min(value = 1, message = "Id of the car can't be less than 1")
    @NotNull(message = "Enter id of the car")
    private Integer carId;

    @JsonProperty("customer_id")
    @Min(value = 1, message = "Id of the customer can't be less than 1")
    @NotNull(message = "Enter id of the customer")
    private Integer customerId;

    @JsonProperty("start_day")
    @JsonFormat(pattern="dd.MM.yyyy")
    @NotNull(message = "Enter start startRepairDay of the order")
    private Date startDay;

    @JsonProperty("end_day")
    @JsonFormat(pattern="dd.MM.yyyy")
    private Date endDay;

    @JsonProperty("order_price")
    @Null(message = "Do not enter price of the order")
    private Double orderPrice;

}
