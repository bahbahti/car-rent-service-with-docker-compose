package com.netcracker.controller.infoController;

import com.netcracker.pojoServices.VersionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("version")
public class VersionController {

    @Value("${build.version}")
    private String buildVersion;
    String javaVersion = System.getProperty("java.version");


    @GetMapping
    public ResponseEntity<VersionDTO> getInfo(){
        VersionDTO pojo = VersionDTO.create(javaVersion, buildVersion);
        return ResponseEntity.ok().body(pojo);
    }
}
