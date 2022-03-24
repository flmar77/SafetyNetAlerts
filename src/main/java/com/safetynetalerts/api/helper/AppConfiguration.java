package com.safetynetalerts.api.helper;

import com.safetynetalerts.api.data.input.service.InputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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
        inputService.loadInMemoryDbFromInput();
        log.debug("h2 loaded");
    }

}
