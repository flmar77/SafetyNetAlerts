package com.safetynetalerts.api.web.dto;

import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JGlobalMap
public class MedicalRecordsDto {
    private String firstName;
    private String lastName;
    private List<String> medications;
    private List<String> allergies;
}
