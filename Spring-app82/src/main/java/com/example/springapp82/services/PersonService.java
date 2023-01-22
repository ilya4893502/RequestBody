package com.example.springapp82.services;

import com.example.springapp82.models.Person;
import com.example.springapp82.repositories.PeopleRepository;
import com.example.springapp82.util.PersonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PersonService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PersonService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> allPeople() {
        return peopleRepository.findAll();
    }

    public Person person(int id) {
        return peopleRepository.findById(id).orElseThrow(PersonNotFoundException::new);
    }


    // Создадим метод, который принимает человека из контроллера и сохраняет его в БД.
    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }
}
