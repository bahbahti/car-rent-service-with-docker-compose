package com.netcracker.pojoServices.pojoForRepairService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@Data
public class HttpDtoToFinishFromRepairService {

    @JsonProperty("price")
    private Integer price;

}
