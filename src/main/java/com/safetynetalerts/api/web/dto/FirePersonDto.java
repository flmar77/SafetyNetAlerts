package com.safetynetalerts.api.web.dto;

import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;

import java.util.List;

@Data
@JGlobalMap
public class FirePersonDto {
    private String lastName;
    private String phone;
    private int age;
    private List<String> medications;
    private List<String> allergies;
}
