package ru.yandex.practicum.filmorate.dal.user;

import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User newUser);

    void removeUser(long userId);

    Optional<User> getUserById(long userId);

    Optional<User> getUserByEmail(String userEmail);

    Optional<User> getUserByLogin(String userLogin);

    void addFriend(long inviterId, long inviteeId);

    void removeFriend(long userId, long friendId);

    void updateFriendshipStatus(long userId, long friendId, FriendshipStatus newStatus);

    List<User> getAllFriends(long userId);

    List<User> getCommonFriends(long inviterId, long inviteeId);
}