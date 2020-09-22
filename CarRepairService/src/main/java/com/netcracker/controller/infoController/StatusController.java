package com.netcracker.controller.infoController;

import com.netcracker.pojoServices.StatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("status")
public class StatusController {

    @Autowired
    public DataSource dataSource;

    @GetMapping
    public ResponseEntity<StatusDTO> getInfo() throws SQLException {
        Connection connection = dataSource.getConnection();
        StatusDTO statusDTO = StatusDTO.create("OK");
        return ResponseEntity.ok().body(statusDTO);
    }
}