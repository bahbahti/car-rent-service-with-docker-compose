package com.netcracker.pojoServices;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netcracker.dto.OrderDTO;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "create")
public class CurrentOrdersDTO {

    @JsonProperty("debt_price")
    private Double debtPrice;

    @JsonProperty("current_orders")
    private List<OrderDTO> orders;
}
