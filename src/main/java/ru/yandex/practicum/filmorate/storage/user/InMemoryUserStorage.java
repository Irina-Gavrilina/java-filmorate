package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        validate(user);
        if (isEmailAlreadyExists(user.getEmail())) {
            log.error("Имейл '{}' уже используется", user.getEmail());
            throw new ValidationException("Этот имейл уже используется");
        }
        if (isLoginAlreadyExists(user.getLogin())) {
            log.error("Логин '{}' уже используется", user.getLogin());
            throw new ValidationException("Этот логин уже используется");
        }
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.error("Не указан id пользователя {}", newUser);
            throw new ValidationException("Id пользователя должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            validate(newUser);
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }
            oldUser.setBirthday(newUser.getBirthday());
            return oldUser;
        }
        log.error("Пользователь с id = {} не найден", newUser.getId());
        throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", newUser.getId()));
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return users.values()
                .stream()
                .filter(user -> user.getId() == userId)
                .findFirst();
    }

    @Override
    public User getUserById(long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        }
        log.error("Пользователь с id = {} не был найден", userId);
        throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean isEmailAlreadyExists(String currentEmail) {
        return users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(currentEmail));
    }

    private boolean isLoginAlreadyExists(String currentLogin) {
        return users.values()
                .stream()
                .map(User::getLogin)
                .anyMatch(login -> login.equals(currentLogin));
    }

    private void validate(User user) {
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