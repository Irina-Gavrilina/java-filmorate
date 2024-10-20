package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<UserResponse> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::mapToUserResponse)
                .toList();
    }

    public UserResponse createUser(NewUserRequest request) {
        validateUser(UserMapper.mapToUser(request));
        Optional<User> alreadyExistsUserEmail = userStorage.getUserByEmail(request.getEmail());
        if (alreadyExistsUserEmail.isPresent()) {
            log.error("Имейл '{}' уже используется", request.getEmail());
            throw new ValidationException("Этот имейл уже используется");
        }
        Optional<User> alreadyExistsUserLogin = userStorage.getUserByLogin(request.getLogin());
        if (alreadyExistsUserLogin.isPresent()) {
            log.error("Логин '{}' уже используется", request.getLogin());
            throw new ValidationException("Этот логин уже используется");
        }
        return UserMapper.mapToUserResponse(userStorage.createUser(UserMapper.mapToUser(request)));
    }

    public UserResponse updateUser(UpdateUserRequest request) {
        User updatedUser = userStorage.getUserById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден",
                        request.getId())));
        validateUser(UserMapper.mapToUser(request));
        userStorage.updateUser(updatedUser);
        return UserMapper.mapToUserResponse(updatedUser);
    }

    public void removeUser(long userId) {
        userStorage.removeUser(userId);
    }

    public UserResponse getUserById(long userId) {
        return userStorage.getUserById(userId)
                .map(UserMapper::mapToUserResponse)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
    }

    public List<UserResponse> getAllFriends(long userId) {
        Optional<User> user = userStorage.getUserById(userId);
        if (user.isEmpty()) {
            log.error("Пользователь с id={} не найден", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
        return userStorage.getAllFriends(userId).stream()
                .map(UserMapper::mapToUserResponse)
                .toList();
    }

    public void addFriend(long inviterId, long inviteeId) {
        Optional<User> inviter = userStorage.getUserById(inviterId);
        if (inviter.isEmpty()) {
            log.error("Пользователь с id = {} не найден", inviterId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", inviterId));
        }
        Optional<User> invitee = userStorage.getUserById(inviteeId);
        if (invitee.isEmpty()) {
            log.error("Пользователь с id = {} не найден", inviteeId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", inviteeId));
        }
        userStorage.addFriend(inviterId, inviteeId);
    }

    public void removeFriend(long userId, long friendId) {
        Optional<User> user = userStorage.getUserById(userId);
        if (user.isEmpty()) {
            log.error("Пользователь с id = {} не был найден", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
        Optional<User> friend = userStorage.getUserById(friendId);
        if (friend.isEmpty()) {
            log.error("Пользователь с id = {} не был найден", friendId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", friendId));
        }
        userStorage.removeFriend(userId, friendId);
    }

    public List<UserResponse> getCommonFriends(Long firstUserId, long secondUserId) {
        Optional<User> user = userStorage.getUserById(firstUserId);
        if (user.isEmpty()) {
            log.error("Пользователь с id = {} не был найден", firstUserId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", firstUserId));
        }
        Optional<User> friend = userStorage.getUserById(secondUserId);
        if (friend.isEmpty()) {
            log.error("Пользователь с id = {} не был найден", secondUserId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", secondUserId));
        }
        return userStorage.getCommonFriends(firstUserId, secondUserId).stream()
                .map(UserMapper::mapToUserResponse)
                .toList();
    }

    private void validateUser(User user) {
        log.info("Запущен процесс валидации для пользователя {}", user);
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Отсутствует электронная почта или символ '@' {}", user);
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'");
        }
        if (user.getLogin().isBlank()) {
            log.error("Логин отсутствует или содержит пробелы {}", user);
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения указана в будущем {}", user);
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}