package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {

  private final Map<Long, Item> map = new HashMap<>();
  private static long counter = 0;

  public Item create(Item item, User user) {
    Long id = ++counter;
    item.setId(id);
    item.setOwner(user);
    map.put(id, item);
    return item;
  }

  public Item update(Long itemId, Item item) {
    Item updatItem = map.get(itemId);
    if (item.getName() != null) {
      updatItem.setName(item.getName());
    }
    if (item.getAvailable() != null) {
      updatItem.setAvailable(item.getAvailable());
    }
    if (item.getDescription() != null) {
      updatItem.setDescription(item.getDescription());
    }
     map.put(itemId, updatItem);
    return updatItem;
  }

  public Item getById(Long itemId) {
    return map.get(itemId);
  }

  public List<Item> getAllByUser(Long userId) {
    return map.values()
            .stream()
            .filter(item -> item.getOwner().getId().equals(userId))
            .collect(Collectors.toList());
  }

  public List<Item> searchByText(String text) {
    return map.values()
            .stream()
            .filter(Item::getAvailable)
            .filter(item -> search(item, text))
            .collect(Collectors.toList());
  }

  private boolean search(Item item, String text) {
    String desc = item.getDescription().toLowerCase();
    return desc.contains(text.toLowerCase());
  }
}
