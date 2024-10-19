package ru.yandex.practicum.filmorate.dal.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dal.BaseDbStorage;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";

    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";

    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";

    private static final String FIND_BY_LOGIN_QUERY = "SELECT * FROM users WHERE login = ?";

    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";

    private static final String REMOVE_QUERY = "DELETE FROM users WHERE id = ?";

    private static final String FIND_ALL_USER_FRIENDS_QUERY =
            "SELECT id AS user_id, email, login, name, birthday " +
                    "FROM users WHERE id IN " +
                    "(SELECT invitee_id FROM friendship where inviter_id = ? AND status = 'APPROVED');";

    private static final String FIND_ALL_COMMON_USER_FRIENDS_QUERY =
            "SELECT id AS user_id, email, login, name, birthday FROM users " +
                    "WHERE id IN (" +
                    "SELECT invitee_id AS invitee " +
                    "FROM friendship " +
                    "WHERE inviter_id = ? AND status = 'APPROVED') " +
                    "AND id IN (SELECT invitee_id AS invitee " +
                    "FROM friendship " +
                    "WHERE inviter_id = ? AND status = 'APPROVED');";

    private static final String ADD_FRIEND_TO_USER_QUERY = "INSERT INTO friendship " +
            "(inviter_id, invitee_id, status) VALUES (?, ?, ?)";

    private static final String REMOVE_USERS_FRIEND_QUERY = "DELETE FROM friendship " +
            "WHERE inviter_id = ? and invitee_id = ?";

    private static final String UPDATE_FRIENDSHIP_STATUS = "ALTER TABLE friendship " +
            "SET status = ? WHERE inviter_id = ? and invitee_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> getAllUsers() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            long id = insert(
                    INSERT_QUERY,
                    user.getEmail(),
                    user.getLogin(),
                    user.getLogin(),
                    user.getBirthday());
            user.setId(id);
        } else {
            long id = insert(
                    INSERT_QUERY,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday());
            user.setId(id);
        }
        return user;
    }

   @Override
    public User updateUser(User newUser) {
       update(
               UPDATE_QUERY,
               newUser.getEmail(),
               newUser.getLogin(),
               newUser.getName(),
               newUser.getBirthday(),
               newUser.getId()
       );
       return newUser;
    }

    @Override
    public void removeUser(long userId) {
        if (!delete(REMOVE_QUERY, userId)) {
            throw new InternalServerException("Не найден пользователь для удаления");
        }
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    @Override
    public Optional<User> getUserByEmail(String userEmail) {
        return findOne(FIND_BY_EMAIL_QUERY, userEmail);
    }

    @Override
    public Optional<User> getUserByLogin(String userLogin) {
        return findOne(FIND_BY_LOGIN_QUERY, userLogin);
    }

    @Override
    public void addFriend(long inviterId, long inviteeId) {
        update(ADD_FRIEND_TO_USER_QUERY,
                inviterId,
                inviteeId,
                FriendshipStatus.APPROVED.toString());
    }

    @Override
    public void removeFriend(long inviterId, long inviteeId) {
        try {
            update(REMOVE_USERS_FRIEND_QUERY, inviterId, inviteeId);
        } catch (InternalServerException e) {
            //do nothing
        }
    }

    @Override
    public void updateFriendshipStatus(long inviterId, long inviteeId, FriendshipStatus newStatus) {
        update(
                newStatus.toString(),
                inviterId,
                inviteeId
        );
    }

    @Override
    public List<User> getAllFriends(long userId) {
        return findMany(FIND_ALL_USER_FRIENDS_QUERY, userId);
    }

    @Override
    public List<User> getCommonFriends(long inviterId, long inviteeId) {
        return findMany(FIND_ALL_COMMON_USER_FRIENDS_QUERY, inviterId, inviteeId);
    }
}