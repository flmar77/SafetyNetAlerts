package com.safetynetalerts.api.domain.model;

import com.googlecode.jmapper.annotations.JMap;
import lombok.Data;

import java.util.List;

@Data
public class FirePersonModel {
    @JMap
    private String lastName;
    @JMap
    private String phone;
    private int age;
    @JMap
    private List<String> medications;
    @JMap
    private List<String> allergies;
}
