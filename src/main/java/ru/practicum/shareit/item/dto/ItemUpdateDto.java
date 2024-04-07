package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Setter
@Getter
public class ItemUpdateDto {
  private Long id;
  private String name;
  private String description;
  private Boolean available;
  private User owner;
  private ItemRequest itemRequest;
}
