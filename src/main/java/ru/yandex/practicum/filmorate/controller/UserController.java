package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Поступил запрос GET на получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        log.info("Получен запрос POST на создание пользователя {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        log.info("Получен запрос PUT на обновление пользователя {}", newUser);
        return userService.updateUser(newUser);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable("userId") long userId) {
        log.info("Поступил запрос GET на получение данных о пользователе с id = {}", userId);
        Optional<User> optUser = userService.findUserById(userId);
        if (optUser.isPresent()) {
            return optUser.get();
        }
        throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") long id,
                          @PathVariable("friendId") long friendId) {
        log.info("Получен запрос PUT на добавление пользователя c id = {} в друзья к пользователю с id = {}",
                id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") long id,
                             @PathVariable("friendId") long friendId) {
        log.info("Получен запрос DELETE на удаление пользователя c id = {} из друзей пользователя с id = {}",
                friendId, id);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable("id") long id) {
        log.info("Поступил запрос GET на получение друзей пользователя {}", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") long id,
                                       @PathVariable("otherId") long otherId) {
        log.info("Поступил запрос GET на получение списка общих друзей пользователей c id ={} и id ={}",
                id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}