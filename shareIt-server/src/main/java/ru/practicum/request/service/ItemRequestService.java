package ru.practicum.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestShortDto;

import java.util.List;

public interface ItemRequestService {

  List<ItemRequestDto> getAll(Long userId, Pageable pageable);

  ItemRequestDto get(Long userId, Long requestId);

  List<ItemRequestDto> getSelf(Long userId);

  ItemRequestDto create(Long usedId, ItemRequestShortDto itemRequestShortDto);
}
