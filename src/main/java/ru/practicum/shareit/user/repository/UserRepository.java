package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {

    private static long counter = 0;
    private final Map<Long, User> map = new HashMap<>();

    public User getById(Long userId) {
        return map.get(userId);
    }

    public User create(User user) {
        if (isExistsByEmail(user.getEmail())) {
            throw new AlreadyExistsException();
        }
        Long id = ++counter;
        user.setId(id);
        map.put(id, user);
        return user;
    }

    private boolean isExistsByEmail(String email) {
        return map.values()
                .stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    public User update(Long userId, User user) {
        User userById = map.get(userId);
        if (user.getEmail() != null) {
            if (isExistsByEmail(user.getEmail()) && !user.getEmail().equals(userById.getEmail())) {
                throw new AlreadyExistsException();
            }
            userById.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userById.setName(user.getName());
        }
        map.put(userId, userById);
        return userById;
    }

    public void delete(Long userId) {
        map.remove(userId);
    }

    public List<User> getAll() {
        return List.copyOf(map.values());
    }
}
