package com.safetynetalerts.api.domain.model;

import lombok.Data;

@Data
public class AlertedPerson {
    private String firstName;
    private String lastName;
    private int age;
}
