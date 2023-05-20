package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.ArrayList;

@Data
public class User {

    private ArrayList<Integer> friends = new ArrayList<>();

    private int id;
    @Email
    private String email;

    private String login;

    private String name;

    private LocalDate birthday;




}