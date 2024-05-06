package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

  private final UserRepository userRepository;
  private final ItemRequestMapper itemRequestMapper;
  private final ItemRequestRepository itemRequestRepository;

  @Override
  public List<ItemRequestDto> getAll(Long userId, Pageable pageable) {
    return itemRequestRepository.findAllWithoutUserId(userId, pageable)
            .stream()
            .map(itemRequestMapper::toItemRequestDto)
            .collect(Collectors.toList());
  }

  @Override
  public ItemRequestDto get(Long userId, Long requestId) {
    userRepository.findById(userId)
            .orElseThrow(NotFoundException::new);
    return itemRequestRepository.findById(requestId)
            .map(itemRequestMapper::toItemRequestDto)
            .orElseThrow(NotFoundException::new);
  }

  @Override
  public List<ItemRequestDto> getSelf(Long userId) {
    userRepository.findById(userId)
            .orElseThrow(NotFoundException::new);
    return itemRequestRepository.findAllByRequestorId(userId)
            .stream()
            .map(itemRequestMapper::toItemRequestDto)
            .collect(Collectors.toList());
  }

  @Override
  public ItemRequestDto create(Long userId, ItemRequestShortDto itemRequestShortDto) {
    return userRepository.findById(userId)
            .map(user -> itemRequestMapper.toItemRequest(user, itemRequestShortDto))
            .map(itemRequestRepository::save)
            .map(itemRequestMapper::toItemRequestDto)
            .orElseThrow(NotFoundException::new);
  }
}
