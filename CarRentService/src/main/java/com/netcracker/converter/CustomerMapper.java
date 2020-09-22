package com.netcracker.converter;

import com.netcracker.dto.CustomerDTO;
import com.netcracker.entity.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface CustomerMapper {

    CustomerDTO toCustomerDTO (Customer customer);

    Customer toCustomer(CustomerDTO customerDTO);

    List<CustomerDTO> toCustomerDTOs(List<Customer> customers);

}
