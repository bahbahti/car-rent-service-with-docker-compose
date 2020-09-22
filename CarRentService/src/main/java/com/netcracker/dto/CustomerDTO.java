package com.netcracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CustomerDTO {

    @JsonProperty("id")
    @Min(value = 1, message = "Id can't be less than 1")
    private Integer id;

    @JsonProperty("first_name")
    @NotBlank(message = "Enter first name of the customer")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Enter last name of the customer")
    private String lastName;

    @JsonProperty("area_of_living")
    @NotBlank(message = "Enter area of living of the customer")
    private String areaOfLiving;

    @JsonProperty("passport_number")
    @Min(value = 1, message = "passport number cannot be 0")
    @NotNull(message = "Enter passport number of the customer")
    @Digits(integer = 6, fraction = 0, message = "Passport should contain no more then 6 numbers")
    private Integer passportNumber;

    @JsonProperty("phone_number")
    @Min(value = 1, message = "phone number cannot be 0")
    @Digits(integer = 9, fraction = 0, message = "Number should contain no more then 10 numbers")
    private Integer phoneNumber;

    @JsonProperty("discount")
    @Max(value = 100, message = "discount cannot be more then 100 percent")
    private Integer discount;

    @JsonProperty("username")
    @NotBlank(message = "Enter username of the customer")
    private String username;

    @JsonProperty("password")
    @NotBlank(message = "Enter password of the customer")
    private String password;

    @JsonProperty("role_id")
    @NotNull(message = "Enter role id of the customer")
    private Integer roleId;

    @JsonProperty("enabled")
    private Boolean enabled;

}
