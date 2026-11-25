package org.modelmapper;


import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

@Test
public class ConditionMapTest extends AbstractTest {
    public static class Person {
        String firstName;
        String lastName;
        String employer;

        String getEmployer() {
            return employer;
        }

        String getLastName() {
            return lastName;
        }
    }

    public static class PersonDTO {
        String firstName;
        String surName;
        String employerName;

        String getEmployerName() {
            return employerName;
        }

        void setEmployerName(String employerName) {
            this.employerName = employerName;
        }

        void setSurName(String surName) {
            this.surName = surName;
        }
    }

    public void ConditionTest(){
        TypeMap<Person, PersonDTO> map0 = modelMapper.createTypeMap(Person.class, PersonDTO.class, "map0").addMappings(mapper -> {
            mapper.map(Person::getLastName, PersonDTO::setSurName);
        });
        TypeMap<Person, PersonDTO> map1 = modelMapper.createTypeMap(Person.class, PersonDTO.class, "map1").addMappings(mapper -> {
            mapper.map(Person::getEmployer, PersonDTO::setEmployerName);
        });
        TypeMap<Person, PersonDTO> map2 = modelMapper.createTypeMap(Person.class, PersonDTO.class, "map2").addMappings(mapper -> {
            mapper.map(Person::getLastName, PersonDTO::setSurName);
            mapper.map(Person::getEmployer, PersonDTO::setEmployerName);
        });
        HashMap<String, Boolean> s0 = new HashMap<>();
        s0.put("lastname",true);
        HashMap<String, Boolean> s1 = new HashMap<>();
        s1.put("employer", true);
        HashMap<String, Boolean> s2 = new HashMap<>();
        s2.put("lastname",true);
        s2.put("employer", true);

        ArrayList<Situation> Slist = new ArrayList<>();
        Situation<Person, PersonDTO> situation1 = new Situation<>(s0, map0);
        Situation<Person, PersonDTO> situation2 = new Situation<>(s1, map1);
        Situation<Person, PersonDTO> situation3 = new Situation<>(s2, map2);
        Slist.add(situation1);
        Slist.add(situation2);
        Slist.add(situation3);

        Person person = new Person();
        person.employer = "Donald Mike";
        person.firstName = "Mike";
        person.lastName = "Donald";

        PersonDTO m0 = new PersonDTO();
        modelMapper.map(person,m0,s0,Slist);
        assertEquals(m0.surName,"Donald");
        assertEquals(m0.employerName, null);
        assertEquals(m0.firstName, "Mike");

        PersonDTO m1 = new PersonDTO();
        modelMapper.map(person,m1,s1,Slist);
        assertEquals(m1.surName,null);
        assertEquals(m1.employerName, "Donald Mike");
        assertEquals(m1.firstName, "Mike");

        PersonDTO m2 = new PersonDTO();
        modelMapper.map(person,m2,s2,Slist);
        assertEquals(m2.surName,"Donald");
        assertEquals(m2.employerName, "Donald Mike");
        assertEquals(m2.firstName, "Mike");
    }
}
