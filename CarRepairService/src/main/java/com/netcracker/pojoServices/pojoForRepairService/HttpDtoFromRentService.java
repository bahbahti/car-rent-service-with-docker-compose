package com.netcracker.pojoServices.pojoForRepairService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@Data
public class HttpDtoFromRentService {

    @JsonProperty("rent_service_id")
    private Integer idExternal;

    @JsonProperty("start_day")
    private Date startDay;

}