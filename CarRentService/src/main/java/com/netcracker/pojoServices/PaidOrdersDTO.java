package com.netcracker.pojoServices;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netcracker.dto.OrderDTO;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "create")
public class PaidOrdersDTO {

    @JsonProperty("total_price")
    private Double totalPrice;

    @JsonProperty("paid_orders")
    private List<OrderDTO> orders;

}
