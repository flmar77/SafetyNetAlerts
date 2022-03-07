package com.safetynetalerts.api.data.inputmodel;

import lombok.Data;

import java.util.List;

@Data
public class MedicalRecordInputModel {
    private String firstName;
    private String lastName;
    private String birthdate;
    private List<String> medications;
    private List<String> allergies;
}
