package com.safetynetalerts.api.web.dto;

import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;

import java.time.LocalDate;

@Data
@JGlobalMap
public class PersonDto {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private String phone;
    private String email;
    private LocalDate birthdate;
}
