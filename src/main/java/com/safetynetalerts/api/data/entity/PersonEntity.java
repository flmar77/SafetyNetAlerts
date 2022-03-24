package com.safetynetalerts.api.data.entity;

import com.googlecode.jmapper.annotations.JMap;
import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class PersonEntity {

    @Id
    @GeneratedValue
    private Long id;

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

    private LocalDate birthdate;

    @ElementCollection
    private List<String> medications;

    @ElementCollection
    private List<String> allergies;

    private Long fireStation;
}
