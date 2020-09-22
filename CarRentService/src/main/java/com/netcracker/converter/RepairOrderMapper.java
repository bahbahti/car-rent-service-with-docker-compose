package com.netcracker.converter;

import com.netcracker.dto.RepairOrderDTO;
import com.netcracker.entity.RepairOrder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface RepairOrderMapper {

    RepairOrderDTO toRepairOrderDTO (RepairOrder repairOrder);

    RepairOrder toRepairOrder(RepairOrderDTO repairOrderDTO);

    List<RepairOrderDTO> toRepairOrderDTOs(List<RepairOrder> repairOrders);

}
