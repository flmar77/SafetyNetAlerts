package com.safetynetalerts.api.data.input.model;

import lombok.Data;

@Data
public class PersonInputModel {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private String phone;
    private String email;
}