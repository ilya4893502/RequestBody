package com.example.springapp82.util;

public class PersonNotCreatedException extends RuntimeException{

    // Переопределим конструктор, в которой мы будем добавлять сообщение об ошибке.
    public PersonNotCreatedException(String msg) {

        // Передаем это сообщение с помощью super в RuntimeException.
        super(msg);

        // Теперь у нас внутри этого исключения будет сообщение об этом исключении.
    }
}
