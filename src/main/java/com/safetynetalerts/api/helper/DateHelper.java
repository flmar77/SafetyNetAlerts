package com.safetynetalerts.api.helper;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DateHelper {

    public LocalDate now() {
        return LocalDate.now();
    }
}
