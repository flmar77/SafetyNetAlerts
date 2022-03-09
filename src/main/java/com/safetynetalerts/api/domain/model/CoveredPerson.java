package com.safetynetalerts.api.domain.model;

import lombok.Data;

@Data
public class CoveredPerson {
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
}
