package model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
public class User {

    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    @Email
    private String email;
    @Getter
    @Setter
    private String login;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private LocalDate birthday;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}

/*
Свойства model.User:
целочисленный идентификатор — id;
электронная почта — email;
логин пользователя — login;
имя для отображения — name;
дата рождения — birthday.
 */