package com.safetynetalerts.api.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class FireStation {
    private Long station;
    private List<String> addresses;
}
