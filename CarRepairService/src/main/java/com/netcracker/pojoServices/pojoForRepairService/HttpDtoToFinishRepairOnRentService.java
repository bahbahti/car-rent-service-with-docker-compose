package com.netcracker.pojoServices.pojoForRepairService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@Data
public class HttpDtoToFinishRepairOnRentService {

    @JsonProperty("price")
    private Integer price;

}
