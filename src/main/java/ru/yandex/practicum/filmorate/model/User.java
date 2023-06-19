package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.jshell.Snippet;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private Long id;

    @Email
    private String email;

    private String login;

    private String name;

    private LocalDate birthday;

    private Set<User> friends = new HashSet<>();

    @Builder
    public User(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}