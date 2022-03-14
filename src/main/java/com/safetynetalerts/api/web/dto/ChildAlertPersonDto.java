package com.safetynetalerts.api.web.dto;

import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;

@Data
@JGlobalMap
public class ChildAlertPersonDto {
    private String firstName;
    private String lastName;
    private int age;
}
