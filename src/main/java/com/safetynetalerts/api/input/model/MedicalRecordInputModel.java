package com.safetynetalerts.api.input.model;

import lombok.Getter;

import java.util.List;

@Getter
public class MedicalRecordInputModel {
    private String firstName;
    private String lastName;
    private String birthdate;
    private List<String> medications;
    private List<String> allergies;
}
