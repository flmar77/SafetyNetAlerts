package com.safetynetalerts.api.web.controller;

import com.safetynetalerts.api.domain.model.ChildAlertModel;
import com.safetynetalerts.api.domain.model.FireModel;
import com.safetynetalerts.api.domain.model.FireStationModel;
import com.safetynetalerts.api.domain.model.PhoneAlertModel;
import com.safetynetalerts.api.domain.service.SnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnaController {

    Logger LOGGER = LoggerFactory.getLogger(SnaController.class);

    @Autowired
    private SnaService snaService;

    @GetMapping("/firestation")
    public FireStationModel getFireStationModel(@RequestParam int stationNumber) {
        LOGGER.info("request to get FireStationModel of station : " + stationNumber);
        return snaService.getFireStationModel(stationNumber);
        //TODO : add wrong cases and log ?
    }

    @GetMapping("/childAlert")
    public ChildAlertModel getChildAlertModel(@RequestParam String address) {
        LOGGER.info("request to get ChildAlertModel of Address : " + address);
        return snaService.getChildAlertModel(address);
        //TODO : add wrong cases and log ?
    }

    @GetMapping("/phoneAlert")
    public PhoneAlertModel getPhoneAlertModel(@RequestParam int firestation_number) {
        LOGGER.info("request to get PhoneAlertModel of firestation : " + firestation_number);
        return snaService.getPhoneAlertModel(firestation_number);
        //TODO : add wrong cases and log ?
    }

    @GetMapping("/fire")
    public FireModel getFireModel(@RequestParam String address) {
        LOGGER.info("request to get FireModel of address : " + address);
        return snaService.getFireModel(address);
        //TODO : add wrong cases and log ?
    }

}
