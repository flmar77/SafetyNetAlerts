package com.safetynetalerts.api.input.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

@Getter
public class AggregatedInputModel {
    @SerializedName("persons")
    private List<PersonInputModel> personInputModels;

    @SerializedName("firestations")
    private List<FireStationInputModel> fireStationInputModels;

    @SerializedName("medicalrecords")
    private List<MedicalRecordInputModel> medicalRecordInputModels;
}
