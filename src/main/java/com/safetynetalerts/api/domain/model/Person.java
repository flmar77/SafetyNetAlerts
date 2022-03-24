package com.safetynetalerts.api.domain.model;

import com.googlecode.jmapper.annotations.JMap;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Person {

    @JMap
    private String firstName;
    @JMap
    private String lastName;
    @JMap
    private String address;
    @JMap
    private String city;
    @JMap
    private String zip;
    @JMap
    private String phone;
    @JMap
    private String email;
    @JMap
    private LocalDate birthdate;
    @JMap
    private List<String> medications;
    @JMap
    private List<String> allergies;
    @JMap
    private int fireStation;

    private int age;

}
