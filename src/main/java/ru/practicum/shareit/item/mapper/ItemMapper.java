package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public class ItemMapper {

  public ItemFullDto toItemFullDTO(
          Item item,
          List<CommentDto> comments,
          Booking lastBooking,
          Booking nextBooking
  ) {
    return ItemFullDto.builder()
            .id(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.isAvailable())
            .lastBooking(lastBooking != null ? ItemBookingDto.builder()
                    .id(lastBooking.getId())
                    .bookerId(lastBooking.getBooker().getId())
                    .build() : null)
            .nextBooking(nextBooking != null ? ItemBookingDto.builder()
                    .id(nextBooking.getId())
                    .bookerId(nextBooking.getBooker().getId())
                    .build() : null)
            .comments(comments)
            .build();
  }

  public ItemDto toItemDto(Item item) {
    return ItemDto.builder()
            .id(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.isAvailable())
            .build();
  }

  public Item toItem(ItemDto dto, User owner) {
    return Item.builder()
            .id(dto.getId())
            .name(dto.getName())
            .description(dto.getDescription())
            .available(dto.getAvailable())
            .owner(owner)
            .build();
  }
}
