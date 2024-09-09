package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }

    @Test
    public void whenUserFieldsAreOkThenDoNotThrowValidationException() {
        User user = User.builder()
                .id(1L)
                .email("user@yandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        assertDoesNotThrow(() -> userController.validate(user));
    }

    @Test
    public void whenUserEmailIsEmptyThenThrowValidationException() {
        User user = User.builder()
                .id(1L)
                .email("")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        assertThrows(ValidationException.class, () -> userController.validate(user));
    }

    @Test
    public void whenUserEmailIsBlankThenThrowValidationException() {
        User user = User.builder()
                .id(1L)
                .email(" ")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        assertThrows(ValidationException.class, () -> userController.validate(user));
    }

    @Test
    public void whenUserEmailDoesNotContainAtSymbolThenThrowValidationException() {
        User user = User.builder()
                .id(1L)
                .email("useryandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        assertThrows(ValidationException.class, () -> userController.validate(user));
    }

    @Test
    public void whenUserLoginIsEmptyThenThrowValidationException() {
        User user = User.builder()
                .id(1L)
                .email("user@yandex.ru")
                .login("")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        assertThrows(ValidationException.class, () -> userController.validate(user));
    }

    @Test
    public void whenUserLoginIsBlankThenThrowValidationException() {
        User user = User.builder()
                .id(1L)
                .email("user@yandex.ru")
                .login(" ")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        assertThrows(ValidationException.class, () -> userController.validate(user));
    }

    @Test
    public void whenUserBirthdayIsInTheFutureThenThrowValidationException() {
        User user = User.builder()
                .id(1L)
                .email("user@yandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2050, 4, 16))
                .build();
        assertThrows(ValidationException.class, () -> userController.validate(user));
    }

    @Test
    public void shouldReturnAllUsers() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userController.createUser(user1);
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(2000, 4, 16))
                .build();
        userController.createUser(user2);
        Collection<User> firstListOfUsers = List.of(user1, user2);
        List<User> secondListOfUsers = userController.getAllUsers().stream().toList();
        assertEquals(firstListOfUsers, secondListOfUsers);
    }

    @Test
    public void shouldCreateUser() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userController.createUser(user);
        assertEquals(userController.getAllUsers().size(), 1);
        assertEquals(userController.getAllUsers().stream().toList().getFirst(), user);
    }

    @Test
    public void shouldNotCreateUserWhenUserEmailIsAlreadyExists() {
        User user1 = User.builder()
                .email("user@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userController.createUser(user1);
        User user2 = User.builder()
                .email("user@yandex.ru")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(2000, 4, 16))
                .build();
        assertThrows(ValidationException.class, () -> userController.createUser(user2));
    }

    @Test
    public void shouldNotCreateUserWhenUserLoginIsAlreadyExists() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("login")
                .name("name1")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userController.createUser(user1);
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("login")
                .name("name2")
                .birthday(LocalDate.of(2000, 4, 16))
                .build();
        assertThrows(ValidationException.class, () -> userController.createUser(user2));
    }

    @Test
    public void shouldCreateUserWithLoginInsteadOfNameIfFieldNameIsEmpty() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("login")
                .name("")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userController.createUser(user);
        assertEquals(userController.getAllUsers().stream().toList().getFirst().getName(), "login");
    }

    @Test
    public void shouldCreateUserWithLoginInsteadOfNameIfFieldNameIsBlank() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("login")
                .name(" ")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userController.createUser(user);
        assertEquals(userController.getAllUsers().stream().toList().getFirst().getName(), "login");
    }

    @Test
    public void shouldCreateUserWithLoginInsteadOfNameIfFieldNameIsNull() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("login")
                .name(null)
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userController.createUser(user);
        assertEquals(userController.getAllUsers().stream().toList().getFirst().getName(), "login");
    }

    @Test
    public void shouldUpdateUser() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userController.createUser(user1);
        User user2 = User.builder()
                .id(user1.getId())
                .email("newEmail@yandex.ru")
                .login("newLogin")
                .name("newName")
                .birthday(LocalDate.of(2000, 4, 16))
                .build();
        assertEquals(userController.getAllUsers().size(), 1);
        assertEquals(userController.getAllUsers().stream().toList().getFirst(), user1);
        userController.updateUser(user2);
        assertEquals(userController.getAllUsers().size(), 1);
        assertEquals(userController.getAllUsers().stream().toList().getFirst().getEmail(), "newEmail@yandex.ru");
        assertEquals(userController.getAllUsers().stream().toList().getFirst().getLogin(), "newLogin");
        assertEquals(userController.getAllUsers().stream().toList().getFirst().getName(), "newName");
        assertEquals(userController.getAllUsers().stream().toList().getFirst().getBirthday(),
                LocalDate.of(2000, 4, 16));
    }

    @Test
    public void shouldNotUpdateUserWhenFieldIdIsEmpty() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userController.createUser(user1);
        User user2 = User.builder()
                .email("newEmail@yandex.ru")
                .login("newLogin")
                .name("newName")
                .birthday(LocalDate.of(2000, 4, 16))
                .build();
        assertThrows(ValidationException.class, () -> userController.updateUser(user2));
    }

    @Test
    public void shouldNotUpdateUserWhenThereIsNoSuchId() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userController.createUser(user1);
        User user2 = User.builder()
                .id(2L)
                .email("newEmail@yandex.ru")
                .login("newLogin")
                .name("newName")
                .birthday(LocalDate.of(2000, 4, 16))
                .build();
        assertThrows(ValidationException.class, () -> userController.updateUser(user2));
    }

    @Test
    public void shouldUpdateUserWithLoginInsteadOfNameIfFieldNameIsEmpty() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userController.createUser(user1);
        User user2 = User.builder()
                .id(user1.getId())
                .email("newEmail@yandex.ru")
                .login("newLogin")
                .name("")
                .birthday(LocalDate.of(2000, 4, 16))
                .build();
        userController.updateUser(user2);
        assertEquals(userController.getAllUsers().stream().toList().getFirst().getName(), "newLogin");
    }

    @Test
    public void shouldUpdateUserWithLoginInsteadOfNameIfFieldNameIsBlank() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userController.createUser(user1);
        User user2 = User.builder()
                .id(user1.getId())
                .email("newEmail@yandex.ru")
                .login("newLogin")
                .name(" ")
                .birthday(LocalDate.of(2000, 4, 16))
                .build();
        userController.updateUser(user2);
        assertEquals(userController.getAllUsers().stream().toList().getFirst().getName(), "newLogin");
    }

    @Test
    public void shouldUpdateUserWithLoginInsteadOfNameIfFieldNameIsNull() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userController.createUser(user1);
        User user2 = User.builder()
                .id(user1.getId())
                .email("newEmail@yandex.ru")
                .login("newLogin")
                .name(null)
                .birthday(LocalDate.of(2000, 4, 16))
                .build();
        userController.updateUser(user2);
        assertEquals(userController.getAllUsers().stream().toList().getFirst().getName(), "newLogin");
    }
}