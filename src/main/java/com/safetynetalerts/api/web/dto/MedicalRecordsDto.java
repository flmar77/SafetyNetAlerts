package com.safetynetalerts.api.web.dto;

import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;

import java.util.List;

@Data
@JGlobalMap
public class MedicalRecordsDto {
    private String firstName;
    private String lastName;
    private List<String> medications;
    private List<String> allergies;
}
