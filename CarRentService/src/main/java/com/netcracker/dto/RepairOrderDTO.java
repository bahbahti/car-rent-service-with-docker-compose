package com.netcracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.netcracker.entity.enums.RepairStatus;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.sql.Date;

@Data
public class RepairOrderDTO {

    @JsonProperty("id")
    @Min(value = 1, message = "Id can't be less than 1")
    private Integer id;

    @JsonProperty("car_id")
    @Min(value = 1, message = "Id of the car can't be less than 1")
    @NotNull(message = "Enter id of the car")
    private Integer carId;

    @JsonProperty("customer_id")
    @Null(message = "You cannot enter customer id")
    private Integer customerId;

    @JsonProperty("start_day")
    @JsonFormat(pattern="dd.MM.yyyy")
    @NotNull(message = "Enter start startDay of the repair")
    private Date startRepairDay;

    @JsonProperty("end_day")
    @JsonFormat(pattern="dd.MM.yyyy")
    @Null(message = "You cannot enter end startDay of repair")
    private Date endRepairDay;

    @JsonProperty("repair_id_external")
    @Null(message = "You cannot enter id of external service")
    private Integer repairIdExternal;

    @JsonProperty("status_of_repair")
    private RepairStatus repairStatus = RepairStatus.PENDING;

    @JsonProperty("price")
    @Null(message = "You cannot enter price of repair. It will be counted in time")
    private Integer price;

}
