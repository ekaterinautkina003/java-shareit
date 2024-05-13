package ru.practicum.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.booking.model.Booking;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemBookingDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemFullDto;
import ru.practicum.item.model.Item;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.user.model.User;

import java.util.List;

@Component
public class ItemMapper {

  public ItemFullDto toItemFullDto(
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
            .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
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

  public Item toItemWithRequest(ItemDto dto, User owner, ItemRequest request) {
    return Item.builder()
            .id(dto.getId())
            .name(dto.getName())
            .description(dto.getDescription())
            .available(dto.getAvailable())
            .owner(owner)
            .request(request)
            .build();
  }
}
