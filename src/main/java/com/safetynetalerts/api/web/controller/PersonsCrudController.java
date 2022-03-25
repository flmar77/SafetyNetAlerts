package com.safetynetalerts.api.web.controller;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.domain.service.SnaService;
import com.safetynetalerts.api.web.dto.PersonDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class PersonsCrudController {

    @Autowired
    private SnaService snaService;

    @GetMapping("/persons")
    public List<PersonDto> getAllPersonsDto() {
        log.info("request to get AllPersonsDto");

        List<Person> personList = snaService.getAllPersons();

        return mapPersonsToPersonsDto(personList);
    }

    @GetMapping("/persons/{firstName}&{lastName}")
    public List<PersonDto> getPersonsDto(@PathVariable String firstName, @PathVariable String lastName) {
        log.info("request to get PersonsDto of firstName={} & lastName={}", firstName, lastName);

        List<Person> personList = snaService.getPersonsByFirstNameAndLastName(firstName, lastName);

        return mapPersonsToPersonsDto(personList);
    }

    @PostMapping("/persons")
    public ResponseEntity<?> createPersonDto(@RequestBody PersonDto personDto) {
        log.info("request to post PersonsDto : {}", personDto);

        if (wrongPersonDtoInput(personDto)) {
            String errorMessage = "error while posting PersonDto because of wrong input : " + personDto;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            Person person = snaService.createPerson(personDto);
            JMapper<PersonDto, Person> personToPersonDtoMapper = new JMapper<>(PersonDto.class, Person.class);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(personToPersonDtoMapper.getDestination(person));
        } catch (EntityExistsException e) {
            String errorMessage = "error while posting PersonDto because of existing person with firstName=" + personDto.getFirstName() + " & lastName=" + personDto.getLastName();
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

    }

    @PutMapping("/persons/{firstName}&{lastName}")
    public ResponseEntity<?> updatePersonDto(@PathVariable String firstName, @PathVariable String lastName, @RequestBody PersonDto personDto) {
        log.info("request to put PersonsDto : {}", personDto);

        if (wrongNamesDtoInput(firstName, lastName)) {
            String errorMessage = "error while putting PersonDto because of wrong firstName=" + firstName + " and/or lastName=" + lastName;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            Person person = snaService.updatePersonWithoutMedicalRecords(firstName, lastName, personDto);
            JMapper<PersonDto, Person> personToPersonDtoMapper = new JMapper<>(PersonDto.class, Person.class);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(personToPersonDtoMapper.getDestination(person));
        } catch (NoSuchElementException e) {
            String errorMessage = "error while putting PersonDto because of non existing person with firstName=" + firstName + " & lastName=" + lastName;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorMessage);
        }
    }

    @DeleteMapping("/persons/{firstName}&{lastName}")
    public ResponseEntity<String> deletePersonDto(@PathVariable String firstName, @PathVariable String lastName) {
        log.info("request to delete PersonsDto");

        if (wrongNamesDtoInput(firstName, lastName)) {
            String errorMessage = "error while deleting PersonDto because of wrong firstName=" + firstName + " and/or lastName=" + lastName;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            snaService.deletePerson(firstName, lastName);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("successfully delete");
        } catch (NoSuchElementException e) {
            String errorMessage = "error while deleting PersonDto because of non existing person with firstName=" + firstName + " & lastName=" + lastName;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorMessage);
        }
    }

    private List<PersonDto> mapPersonsToPersonsDto(List<Person> personList) {
        JMapper<PersonDto, Person> personToPersonDtoMapper = new JMapper<>(PersonDto.class, Person.class);
        return personList.stream()
                .map(personToPersonDtoMapper::getDestination)
                .collect(Collectors.toList());
    }

    private boolean wrongPersonDtoInput(PersonDto personDto) {
        return wrongNamesDtoInput(personDto.getFirstName(), personDto.getLastName());
    }

    private boolean wrongNamesDtoInput(String firstName, String lastName) {
        return firstName == null || firstName.equals("")
                || lastName == null || lastName.equals("");
    }
}
