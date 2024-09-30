package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User newUser);

    void removeUser(long userId);

    User getUserById(long userId);

    Optional<User> findUserById(long userId);
}