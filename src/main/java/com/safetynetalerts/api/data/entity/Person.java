package com.safetynetalerts.api.data.entity;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;
    private String address;
    private int fireStation;
    private String city;
    private String zip;
    private String phone;
    private String email;
    private LocalDate birthdate;

    @ElementCollection
    private List<String> medications;

    @ElementCollection
    private List<String> allergies;
}
