package Controllers;

import model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



@RestController
public class UserController {

    private int UserId = 1;
    private static final Logger log = LogManager.getLogger(User.class);
    private final List<User> users = new ArrayList<>();

    @GetMapping("/users")
    public List<User> findAll() {
        log.debug("Текущее количество юзеров {}", users.size());
        return users;
    }

    @PostMapping(value = "/post/user")
    public void createUser(@RequestBody User user) {
        if (user.getEmail() != null && user.getBirthday().isBefore(LocalDate.now()) && user.getLogin() != null &&
            !user.getLogin().contains(" ") && user.getEmail().contains("@")) {

            user.setId(UserId++);
            users.add(user);
        }
    }

    @PostMapping(value = "/post/updateUser")
    public void updateUser(@RequestBody @NotNull User user) {
        User existingUser = users.get(user.getId());
        if (existingUser != null) {
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setBirthday(user.getBirthday());

            users.set(user.getId(), existingUser);
        }
    }
}

