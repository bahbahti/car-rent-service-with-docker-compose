package com.netcracker.pojoServices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "create")
public class BadRequestDTO {

    @JsonProperty("status_of_operation")
    private String operation;

}
