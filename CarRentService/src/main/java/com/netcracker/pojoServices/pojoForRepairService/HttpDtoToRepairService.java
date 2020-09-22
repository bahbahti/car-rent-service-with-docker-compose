package com.netcracker.pojoServices.pojoForRepairService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@Data
public class HttpDtoToRepairService {

    @JsonProperty("rent_service_id")
    private Integer idInternal;

    @JsonProperty("start_day")
    private Date startDay;

}
