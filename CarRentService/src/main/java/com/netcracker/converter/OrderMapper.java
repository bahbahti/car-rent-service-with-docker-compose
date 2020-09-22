package com.netcracker.converter;

import com.netcracker.dto.OrderDTO;
import com.netcracker.entity.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {

    OrderDTO toOrderDTO (Order order);

    Order toOrder(OrderDTO oderDTO);

    List<OrderDTO> toOrderDTOs(List<Order> orders);

}
