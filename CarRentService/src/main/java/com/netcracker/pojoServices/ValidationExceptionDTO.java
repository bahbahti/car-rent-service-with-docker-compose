package com.netcracker.pojoServices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "create")
public class ValidationExceptionDTO {

    @JsonProperty("Status of operation")
    private List<String> errors;

}