package com.safetynetalerts.api.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class PhoneAlertDto {
    List<String> phones;
}
