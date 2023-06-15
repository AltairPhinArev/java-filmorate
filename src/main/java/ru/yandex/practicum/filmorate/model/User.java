package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Data
public class User {

    private Set<Long> friends = new TreeSet<>();

    private Long id;

    @Email
    private String email;

    private String login;

    private String name;

    private LocalDate birthday;

    public User(Long id,String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}