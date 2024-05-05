package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    List<ItemFullDto> getUserItems(Long userId, Pageable pageable);

    ItemFullDto getById(Long userId, Long id);

    List<ItemDto> searchByText(String text, Pageable pageable);

    CommentDto addCommentToItem(Long iteId, Long userId, CommentRequestDto commentDto);
}
