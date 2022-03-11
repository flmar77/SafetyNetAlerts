package com.safetynetalerts.api.web.controller;

import com.safetynetalerts.api.domain.model.ChildAlertModel;
import com.safetynetalerts.api.domain.model.FireModel;
import com.safetynetalerts.api.domain.model.FireStationModel;
import com.safetynetalerts.api.domain.model.PhoneAlertModel;
import com.safetynetalerts.api.domain.service.SnaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SnaController {

    @Autowired
    private SnaService snaService;

    @GetMapping("/firestation")
    public FireStationModel getFireStationModel(@RequestParam int stationNumber) {
        log.info("request to get FireStationModel of station : {}", stationNumber);
        return snaService.getFireStationModel(stationNumber);
    }

    @GetMapping("/childAlert")
    public ChildAlertModel getChildAlertModel(@RequestParam String address) {
        log.info("request to get ChildAlertModel of Address : " + address);
        return snaService.getChildAlertModel(address);
    }

    @GetMapping("/phoneAlert")
    public PhoneAlertModel getPhoneAlertModel(@RequestParam int firestation_number) {
        log.info("request to get PhoneAlertModel of firestation : " + firestation_number);
        return snaService.getPhoneAlertModel(firestation_number);
    }

    @GetMapping("/fire")
    public FireModel getFireModel(@RequestParam String address) {
        log.info("request to get FireModel of address : " + address);
        return snaService.getFireModel(address);
    }

}
