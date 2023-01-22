package com.example.springapp82.controllers;

import com.example.springapp82.models.Person;
import com.example.springapp82.services.PersonService;
import com.example.springapp82.util.PersonErrorResponse;
import com.example.springapp82.util.PersonNotCreatedException;
import com.example.springapp82.util.PersonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PeopleController {

    private final PersonService personService;

    @Autowired
    public PeopleController(PersonService personService) {
        this.personService = personService;
    }


    @GetMapping()
    public List<Person> allPeople() {
        return personService.allPeople();
    }


    @GetMapping("/{id}")
    public Person person(@PathVariable("id") int id) {
        return personService.person(id);
    }


    // Создадим метод для создания человека.
    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid Person person,
                                             BindingResult bindingResult) {
        // В этом методе возвращаем специальный объект, который собой представляет HTTP-ответ. Это
        // самый простой способ. Но можно возвращать любой объект, например, Person.

        // Так как мы должны принять JSON от клиента мы используем аннотацию @RequestBody. Эта
        // аннотация помечает параметр в методе контроллера. И когда мы пришлем JSON в этот метод
        // контроллера, @RequestBody автоматически сконвертирует его в Person.

        // Аннотация @Valid будет проверять на корректность человека.


        // Если есть ошибки, то выбросим исключение.
        if (bindingResult.hasErrors()) {

            // В bindingResult лежат ошибки валидации и мы их совместим в одну строку, которую мы
            // хотим отправить обратно клиенту.
            StringBuilder errorMsg = new StringBuilder();

            // Положим все ошибки в список из FieldError.
            List<FieldError> errors = bindingResult.getFieldErrors();

            // Далее пройдемся по ошибкам и их сконкатенируем в одну большую строку, чтобы ее можно
            // было отправить клиенту.
            for (FieldError fieldError : errors) {
                errorMsg.append(fieldError.getField()).append(" - ")
                        .append(fieldError.getDefaultMessage()).append(";");
                // То есть мы для каждого поля выводим свою ошибку, отделяя эти пары ";".
            }

            // Теперь нам надо выбросить исключение и клиенту послать JSON с этой ошибкой.
            throw new PersonNotCreatedException(errorMsg.toString());
            // Теперь это исключение мы должны обработать, создадим еще один метод handleException.
        }

        // Если JSON пришел валидный, то кладем его в БД.
        personService.save(person);

        // Далее мы должны что-то вернуть клиенту. Можно было вернуть сам Person. Но вернем объект с
        // сообщением, что все прошло успешно. То есть отправляется HTTP-ответ с пустым телом и со
        // статусом 200.
        return ResponseEntity.ok(HttpStatus.OK);
        // Так мы делаем, когда не хотим создавать отдельное сообщение об успехе.
    }


    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {

        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                "Страницы с указанным id не существует",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(personErrorResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e) {

        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                // В сообщение кладем то сообщение, которое мы получили в конструкторе.
                e.getMessage(),
                System.currentTimeMillis()
        );

        // Также мы должны поставить статус BAD_REQUEST.
        return new ResponseEntity<>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
