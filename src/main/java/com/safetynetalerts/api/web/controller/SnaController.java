package com.safetynetalerts.api.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnaController {

    @GetMapping("/hello")
    public String hello()
    {
        return "Hello User!";
    }
}
