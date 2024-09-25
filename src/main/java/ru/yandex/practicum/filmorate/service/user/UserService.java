package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }

    public Optional<User> findUserById(long userId) {
        return userStorage.findUserById(userId);
    }

    public List<User> getAllFriends(long userId) {
        Optional<User> optUser = userStorage.findUserById(userId);
        if (optUser.isPresent()) {
            User user = optUser.get();
            Set<Long> userFriends = user.getFriends();
            if (user.getFriends() == null) {
                return new ArrayList<>();
            } else {
                return user.getFriends()
                        .stream()
                        .map(userStorage::getUserById)
                        .toList();
            }
        } else {
            log.error("Пользователь с id={} не найден", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
    }

    public void addFriend(long userId, long friendId) {
        User user;
        User friend;
        Optional<User> optUser = userStorage.findUserById(userId);
        if (optUser.isPresent()) {
            user = optUser.get();
        } else {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
        Optional<User> optFriend = userStorage.findUserById(friendId);
        if (optFriend.isPresent()) {
            friend = optFriend.get();
        } else {
            log.error("Пользователь с id = {} не найден", friendId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", friendId));
        }
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void removeFriend(long userId, long friendId) {
        User user;
        User friend;
        Optional<User> optUser = userStorage.findUserById(userId);
        if (optUser.isPresent()) {
            user = optUser.get();
        } else {
            log.error("Пользователь с id = {} не был найден", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
        Optional<User> optFriend = userStorage.findUserById(friendId);
        if (optFriend.isPresent()) {
            friend = optFriend.get();
        } else {
            log.error("Пользователь с id = {} не был найден", friendId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", friendId));
        }
        if (user.getFriends() != null && friend.getFriends() != null) {
            if (user.getFriends().contains(friendId)) {
                user.removeFriend(friendId);
                friend.removeFriend(userId);
            } else {
                log.error("Пользователь с id = {} в друзьях у пользователя с id = {} не найден",
                        friendId, userId);
                throw new NotFoundException(String.format("Пользователь с id = %d в друзьях у пользователя с id = %d не " +
                        "найден", friendId, userId));
            }
        }
    }

    public List<User> getCommonFriends(Long userId, long friendId) {
        User user;
        User friend;
        Optional<User> optUser = userStorage.findUserById(userId);
        if (optUser.isPresent()) {
            user = optUser.get();
        } else {
            log.error("Пользователя с id = {} нет в базе", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
        Optional<User> optFriend = userStorage.findUserById(friendId);
        if (optFriend.isPresent()) {
            friend = optFriend.get();
        } else {
            log.error("Пользователя с id = {} нет в базе", friendId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", friendId));
        }
        Set<Long> userFriends = user.getFriends();
        Set<Long> friendFriends = friend.getFriends();
        if (userFriends != null && friendFriends != null) {
            boolean hasIntersection = userFriends.stream().anyMatch(friendFriends::contains);
            if (hasIntersection) {
                Set<Long> commonFriends = new HashSet<>(userFriends);
                commonFriends.retainAll(friendFriends);
                return commonFriends.stream()
                        .map(userStorage::getUserById)
                        .toList();
            } else {
                log.error("У пользователей с id = {} и id = {} нет общих друзей", userId, friendId);
                throw new NotFoundException(String.format("У пользователей с id = %d и id = %d нет общих друзей",
                        userId, friendId));
            }
        } else {
            log.error("У пользователей с id = {} и id = {} нет общих друзей", userId, friendId);
            throw new NotFoundException(String.format("У пользователей с id = %d и id = %d нет общих друзей",
                    userId, friendId));
        }
    }
}