package com.netcracker.pojoServices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "create")
public class VersionDTO {

    @JsonProperty("java_version")
    private String javaVersion;

    @JsonProperty("application_version")
    private String buildVersion;

}
