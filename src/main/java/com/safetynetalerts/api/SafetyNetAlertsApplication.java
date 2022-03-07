package com.safetynetalerts.api;

import com.safetynetalerts.api.data.inputservice.InputService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SafetyNetAlertsApplication implements CommandLineRunner {

    Logger LOGGER = LoggerFactory.getLogger(SafetyNetAlertsApplication.class);

    @Autowired
    private InputService inputService;

    public static void main(String[] args) {
        SpringApplication.run(SafetyNetAlertsApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            inputService.loadInMemoryDbFromInput();
            LOGGER.debug("h2 loaded");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
