package com.safetynetalerts.api.data.input.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;

@Data
public class AggregatedInputModel {
    @SerializedName("persons")
    private ArrayList<PersonInputModel> personInputModels;

    @SerializedName("firestations")
    private ArrayList<FireStationInputModel> fireStationInputModels;

    @SerializedName("medicalrecords")
    private ArrayList<MedicalRecordInputModel> medicalRecordInputModels;
}
