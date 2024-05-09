package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void checkQuery() {
        User user = createUser();
        User savedUser = userRepository.save(user);
        Item item = createItem();
        item.setOwner(user);
        Item savedItem = itemRepository.save(item);
        Comment comment = createComment();
        comment.setUser(savedUser);
        comment.setItem(savedItem);

        var result = commentRepository.findAllByItemId(commentRepository.save(comment).getId());

        Assertions.assertNotNull(result);
    }

    private Item createItem() {
        Item item = new Item();
        item.setName("name_item");
        item.setDescription("desc");
        item.setAvailable(true);
        return item;
    }

    private User createUser() {
        User user = new User();
        user.setName("user_name2");
        user.setEmail("user_email2");
        return user;
    }

    private Comment createComment() {
       Comment comment = new Comment();
       comment.setText("test");
       return comment;
    }
}