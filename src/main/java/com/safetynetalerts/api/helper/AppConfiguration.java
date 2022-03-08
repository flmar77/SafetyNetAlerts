package com.safetynetalerts.api.helper;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public HttpTraceRepository htttpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }

}
