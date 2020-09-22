package com.netcracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CarDTO {

    @JsonProperty("id")
    @Min(value = 1, message = "Id can't be less than 1")
    private Integer id;

    @JsonProperty("name")
    @NotBlank(message = "Enter name of the car")
    private String name;

    @JsonProperty("cost")
    @Min(value = 1, message = "Cost can't be less than 1")
    @NotNull(message = "Enter cost of the car")
    private Integer cost;

    @JsonProperty("registrarion_number")
    @NotBlank(message = "Enter registration number of the car")
    private String registrarionNumber;

    @JsonProperty("color")
    @NotBlank(message = "Enter color of the car")
    private String color;

    @JsonProperty("storage")
    @NotBlank(message = "Enter storage of the car")
    private String storage;

    @JsonProperty("is_available")
    private Boolean isAvailable;

}
