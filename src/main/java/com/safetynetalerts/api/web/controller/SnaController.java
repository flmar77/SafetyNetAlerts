package com.safetynetalerts.api.web.controller;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.domain.service.SnaService;
import com.safetynetalerts.api.web.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class SnaController {

    @Autowired
    private SnaService snaService;

    @GetMapping("/firestation")
    public FireStationDto getFireStationDto(@RequestParam int stationNumber) {
        log.info("request to get FireStationDto of station : {}", stationNumber);

        FireStationDto firestationDto = new FireStationDto();

        List<Person> personList = snaService.getPersonsByStation(stationNumber);

        JMapper<FireStationPersonDto, Person> personMapper = new JMapper<>(FireStationPersonDto.class, Person.class);
        List<FireStationPersonDto> fireStationPersons = personList.stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());
        firestationDto.setFireStationPersons(fireStationPersons);

        firestationDto.setChildCounter(snaService.getChildCounter(personList));
        firestationDto.setAdultCounter(snaService.getAdultCounter(personList));

        return firestationDto;
    }

    @GetMapping("/childAlert")
    public ChildAlertDto getChildAlertDto(@RequestParam String address) {
        log.info("request to get ChildAlertDto of address : {}", address);

        ChildAlertDto childAlertDto = new ChildAlertDto();

        List<Person> personList = snaService.getPersonsByAddress(address);

        JMapper<ChildAlertPersonDto, Person> personMapper = new JMapper<>(ChildAlertPersonDto.class, Person.class);
        List<ChildAlertPersonDto> ChildList = snaService.getChildren(personList).stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());
        List<ChildAlertPersonDto> AdultList = snaService.getAdults(personList).stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());

        childAlertDto.setAlertedChildren(ChildList);
        childAlertDto.setAlertedAdults(AdultList);

        return childAlertDto;
    }

    @GetMapping("/phoneAlert")
    public PhoneAlertDto getPhoneAlertDto(@RequestParam int firestation_number) {
        log.info("request to get PhoneAlertDto of station : {}", firestation_number);

        PhoneAlertDto phoneAlertDto = new PhoneAlertDto();

        List<Person> personList = snaService.getPersonsByStation(firestation_number);

        List<String> phones = personList.stream()
                .map(Person::getPhone)
                .distinct()
                .collect(Collectors.toList());

        phoneAlertDto.setPhones(phones);

        return phoneAlertDto;
    }

    @GetMapping("/fire")
    public FireDto getFireDto(@RequestParam String address) {
        log.info("request to get FireDto of address : {}", address);

        FireDto fireDto = new FireDto();

        List<Person> personList = snaService.getPersonsByAddress(address);

        JMapper<FirePersonDto, Person> personMapper = new JMapper<>(FirePersonDto.class, Person.class);
        List<FirePersonDto> firePersons = personList.stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());
        fireDto.setFirePersons(firePersons);

        fireDto.setFireStation(snaService.getFireStation(personList));

        return fireDto;
    }

}
