package com.netcracker.pojoServices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "create")
public class StatusDTO {

    @JsonProperty("status_of_connection")
    private String connection;

}