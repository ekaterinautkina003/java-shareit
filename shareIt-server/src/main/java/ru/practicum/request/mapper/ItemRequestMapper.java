package ru.practicum.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestShortDto;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

  private final ItemMapper itemMapper;

  public ItemRequestDto toItemRequestDto(ItemRequest entity) {
    return ItemRequestDto.builder()
            .id(entity.getId())
            .description(entity.getDescription())
            .created(entity.getCreated())
            .items(toList(entity))
            .build();
  }

  public ItemRequest toItemRequest(
          User user,
          ItemRequestShortDto requestDTO
  ) {
    return ItemRequest.builder()
            .description(requestDTO.getDescription())
            .requestor(user)
            .build();
  }

  private List<ItemDto> toList(ItemRequest entity) {
    if (entity.getItems() == null) {
      return Collections.emptyList();
    }
    if (entity.getItems().isEmpty()) {
      return Collections.emptyList();
    }
    return entity.getItems().stream()
            .map(itemMapper::toItemDto)
            .collect(Collectors.toList());
  }
}