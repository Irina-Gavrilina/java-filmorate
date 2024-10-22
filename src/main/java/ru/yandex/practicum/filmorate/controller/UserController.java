package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
import ru.yandex.practicum.filmorate.service.UserService;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        log.info("Поступил запрос GET на получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody NewUserRequest user) {
        log.info("Получен запрос POST на создание пользователя {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public UserResponse updateUser(@RequestBody UpdateUserRequest newUser) {
        log.info("Получен запрос PUT на обновление пользователя {}", newUser);
        return userService.updateUser(newUser);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable("id") long id) {
        log.info("Получен запрос DELETE на удаление пользователя с id = {}", id);
        userService.removeUser(id);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable("id") long id) {
        log.info("Поступил запрос GET на получение данных о пользователе с id = {}", id);
        return userService.getUserById(id);
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
    public List<UserResponse> getAllFriends(@PathVariable("id") long id) {
        log.info("Поступил запрос GET на получение друзей пользователя {}", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserResponse> getCommonFriends(@PathVariable("id") long id,
                                               @PathVariable("otherId") long otherId) {
        log.info("Поступил запрос GET на получение списка общих друзей пользователей c id ={} и id ={}",
                id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}