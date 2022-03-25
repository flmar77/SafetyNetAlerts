package com.safetynetalerts.api.domain.model;

import com.googlecode.jmapper.annotations.JMap;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Person {

    @JMap
    private String firstName;
    @JMap
    private String lastName;
    @JMap
    private String address;
    @JMap
    private String city;
    @JMap
    private String zip;
    @JMap
    private String phone;
    @JMap
    private String email;
    @JMap
    private LocalDate birthdate;
    @JMap
    private List<String> medications;
    @JMap
    private List<String> allergies;
    @JMap
    private int fireStation;

    private int age;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final Person comparedPerson = (Person) obj;
        return this.getFirstName().equals(comparedPerson.getFirstName())
                || this.getLastName().equals(comparedPerson.getLastName());
    }

}
