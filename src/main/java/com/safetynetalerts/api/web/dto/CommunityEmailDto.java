package com.safetynetalerts.api.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class CommunityEmailDto {
    private List<String> emails;
}
