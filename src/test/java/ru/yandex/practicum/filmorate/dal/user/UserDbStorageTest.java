package ru.yandex.practicum.filmorate.dal.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan("ru.yandex.practicum.filmorate.*")
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @AfterEach
    public void afterEach() {
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    public void createUser() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userDbStorage.createUser(user);
        final Long userId = user.getId();
        Optional<User> userFromDbOpt = userDbStorage.getUserById(userId);
        assertThat(userFromDbOpt)
                .isPresent()
                .hasValueSatisfying(userFromDb ->
                        assertThat(userFromDb)
                                .hasFieldOrPropertyWithValue("id", userId));
    }

    @Test
    public void findAll() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userDbStorage.createUser(user);
        user.setEmail("newUser@yandex.ru");
        user.setLogin("newLogin");
        userDbStorage.createUser(user);
        List<User> users = userDbStorage.getAllUsers();
        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    public void getUserById() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        userDbStorage.createUser(user);
        final Long userId = user.getId();
        Optional<User> userFromBaseOptional = userDbStorage.getUserById(userId);
        assertThat(userFromBaseOptional)
                .isPresent()
                .hasValueSatisfying(userFromBase ->
                        assertThat(userFromBase)
                                .hasFieldOrPropertyWithValue("id", userId));
    }

    @Test
    public void updateUser() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        user = userDbStorage.createUser(user);
        user.setEmail("user2@yandex.ru");
        user.setLogin("login2");
        user.setName("name2");
        userDbStorage.updateUser(user);
        final Long userId = user.getId();
        Optional<User> userFromBaseOptional = userDbStorage.getUserById(userId);
        assertThat(userFromBaseOptional)
                .isPresent()
                .hasValueSatisfying(userFromBase ->
                        assertThat(userFromBase)
                                .hasFieldOrPropertyWithValue("email", "user2@yandex.ru")
                                .hasFieldOrPropertyWithValue("login", "login2")
                                .hasFieldOrPropertyWithValue("name", "name2"));
    }

    @Test
    public void addFriendToUser() {
        User user = User.builder()
                .email("test01@mail.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2005, 5, 1))
                .build();
        user = userDbStorage.createUser(user);
        final long firstUserId = user.getId();
        user.setEmail("test02@mail.ru");
        user.setLogin("test2");
        user.setName("test2");
        user = userDbStorage.createUser(user);
        final long secondUserId = user.getId();
        userDbStorage.addFriend(firstUserId, secondUserId);
        List<User> userFriends = userDbStorage.getAllFriends(firstUserId);
        assertThat(userFriends.size()).isEqualTo(1);
        assertThat(userFriends.getFirst()).hasFieldOrPropertyWithValue("id", secondUserId);
    }

    @Test
    public void removeFriend() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        user = userDbStorage.createUser(user);
        final long firstUserId = user.getId();
        user.setEmail("test02@mail.ru");
        user.setLogin("test2");
        user.setName("test2");
        userDbStorage.createUser(user);
        final long secondUserId = user.getId();
        userDbStorage.addFriend(firstUserId, secondUserId);
        List<User> userFriends = userDbStorage.getAllFriends(firstUserId);
        assertThat(userFriends.size()).isEqualTo(1);
        userDbStorage.removeFriend(firstUserId, secondUserId);
        userFriends = userDbStorage.getAllFriends(firstUserId);
        assertThat(userFriends.size()).isEqualTo(0);
    }

    @Test
    public void getAllUsersFriends() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        user = userDbStorage.createUser(user);
        final long firstUserId = user.getId();
        user.setEmail("user2@yandex.ru");
        user.setLogin("login2");
        user.setName("name2");
        userDbStorage.createUser(user);
        final long secondUserId = user.getId();
        userDbStorage.addFriend(firstUserId, secondUserId);
        List<User> userFriends = userDbStorage.getAllFriends(firstUserId);
        assertThat(userFriends.size()).isEqualTo(1);
    }

    @Test
    public void getCommonFriends() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1992, 4, 16))
                .build();
        user = userDbStorage.createUser(user);
        final long firstUserId = user.getId();
        user.setEmail("user2@yandex.ru");
        user.setLogin("login2");
        user.setName("name2");
        userDbStorage.createUser(user);
        final long secondUserId = user.getId();
        user.setEmail("user3@yandex.ru");
        user.setLogin("login3");
        user.setName("name3");
        userDbStorage.createUser(user);
        final long thirdUserId = user.getId();
        userDbStorage.addFriend(firstUserId, secondUserId);
        userDbStorage.addFriend(thirdUserId, secondUserId);
        List<User> userFriends = userDbStorage.getCommonFriends(firstUserId, thirdUserId);
        assertThat(userFriends.size()).isEqualTo(1);
        assertThat(userFriends.getFirst().getId()).isEqualTo(secondUserId);
    }
}