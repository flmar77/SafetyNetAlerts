package com.safetynetalerts.api.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FireStation {
    private Long station;
    private List<String> addresses;
}
