package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void successCreate() {
        User user = createUser();
        User saved = userRepository.save(user);

        boolean actualExists = userRepository.existsByEmailIgnoreCase(user.getEmail());
        Optional<User> actualUser = userRepository.findById(saved.getId());

        Assertions.assertTrue(actualExists);
        Assertions.assertTrue(actualUser.isPresent());
        Assertions.assertEquals(actualUser.get().getEmail(), user.getEmail());
        Assertions.assertEquals(actualUser.get().getName(), user.getName());
    }

    @Test
    void successGet() {
        Item item = createItem();
        User user = createUser();
        User saved = userRepository.save(user);
        item.setOwner(saved);
        itemRepository.save(item);

        List<Item> items = itemRepository.getItemsByOwnerId(saved.getId(), null);
        Item resultItem = items.get(0);

        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item, resultItem);
    }

    private Item createItem() {
        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        return item;
    }

    private User createUser() {
        User user = new User();
        user.setName("user_name1");
        user.setEmail("user_email1");
        return user;
    }
}
