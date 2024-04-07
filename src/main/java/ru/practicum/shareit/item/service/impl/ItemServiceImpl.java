package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto create(Long userId, ItemCreateDto itemDto) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new NotFoundException();
        }
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemRepository.create(item, user));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemUpdateDto itemDto) {
        ItemDto item = getById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new IllegalCallerException();
        }
        return itemMapper.toItemDto(itemRepository.update(itemId, itemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto getById(Long itemId) {
        return itemMapper.toItemDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ItemDto> getAllByUser(Long userId) {
        return itemRepository.getAllByUser(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.searchByText(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
