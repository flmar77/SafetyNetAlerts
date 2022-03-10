package com.safetynetalerts.api.domain.model;

import com.googlecode.jmapper.annotations.JMap;
import lombok.Data;

@Data
public class ChildAlertPersonModel {
    @JMap
    private String firstName;
    @JMap
    private String lastName;
    private int age;
}
