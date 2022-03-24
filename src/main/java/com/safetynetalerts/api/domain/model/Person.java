package com.safetynetalerts.api.domain.model;

import com.googlecode.jmapper.annotations.JMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

//TODO : @EqualsAndHashCode génère du code non couvert... mais sans lui, pas de comparaison possible de person :(

@Getter
@Setter
@EqualsAndHashCode
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
