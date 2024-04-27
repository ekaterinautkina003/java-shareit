package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

  ItemDto create(Long userId, ItemDto itemDto);

  ItemDto update(Long userId, Long itemId, ItemDto itemDto);

  List<ItemFullDto> getUserItems(Long userId);

  ItemFullDto getById(Long userId, Long id);

  List<ItemDto> searchByText(String text);

  CommentDto addCommentToItem(Long iteId, Long userId, CommentRequestDto commentDto);
}
