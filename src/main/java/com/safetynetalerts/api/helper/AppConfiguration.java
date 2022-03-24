package com.safetynetalerts.api.helper;

import com.safetynetalerts.api.input.service.InputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Slf4j
@Configuration
public class AppConfiguration implements CommandLineRunner {

    @Autowired
    private InputService inputService;

    @Bean
    public HttpTraceRepository htttpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }

    @Override
    public void run(String... args) {
        try {
            inputService.loadInMemoryDbFromInput();
            log.debug("h2 loaded");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
