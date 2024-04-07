package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setDescription(item.getDescription());
        itemDto.setItemRequest(item.getItemRequest());
        itemDto.setName(item.getName());
        itemDto.setOwner(item.getOwner());
        return itemDto;
    }

    public Item toItem(ItemCreateDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setAvailable(itemDto.getAvailable());
        item.setDescription(itemDto.getDescription());
        item.setItemRequest(itemDto.getItemRequest());
        item.setName(itemDto.getName());
        item.setOwner(itemDto.getOwner());
        return item;
    }

    public Item toItem(ItemUpdateDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setAvailable(itemDto.getAvailable());
        item.setDescription(itemDto.getDescription());
        item.setItemRequest(itemDto.getItemRequest());
        item.setName(itemDto.getName());
        item.setOwner(itemDto.getOwner());
        return item;
    }

    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setAvailable(itemDto.getAvailable());
        item.setDescription(itemDto.getDescription());
        item.setItemRequest(itemDto.getItemRequest());
        item.setName(itemDto.getName());
        item.setOwner(itemDto.getOwner());
        return item;
    }
}
