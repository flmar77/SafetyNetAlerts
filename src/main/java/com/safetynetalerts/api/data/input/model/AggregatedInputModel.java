package com.safetynetalerts.api.data.input.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class AggregatedInputModel {
    @SerializedName("persons")
    private List<PersonInputModel> personInputModels;

    @SerializedName("firestations")
    private List<FireStationInputModel> fireStationInputModels;

    @SerializedName("medicalrecords")
    private List<MedicalRecordInputModel> medicalRecordInputModels;
}
