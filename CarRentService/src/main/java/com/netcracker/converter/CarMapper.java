package com.netcracker.converter;

import com.netcracker.dto.CarDTO;
import com.netcracker.entity.Car;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface CarMapper {

    CarDTO toCarDTO (Car car);

    Car toCar(CarDTO carDTO);

    List<CarDTO> toCarDTOs(List<Car> cars);

}
