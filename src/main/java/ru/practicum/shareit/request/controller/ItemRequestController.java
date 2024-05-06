package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

  private final ItemRequestService itemRequestService;
  private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

  @PostMapping
  public ItemRequestDto create(
          @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
          @Valid @RequestBody final ItemRequestShortDto requestDTO
  ) {
    return itemRequestService.create(userId, requestDTO);
  }

  @GetMapping
  public List<ItemRequestDto> getSelfRequests(
          @RequestHeader(REQUEST_HEADER_USER_ID) Long userId
  ) {
    return itemRequestService.getSelf(userId);
  }

  @GetMapping("/all")
  public List<ItemRequestDto> getAll(
          @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
          @RequestParam(required = false, defaultValue = "0") final Integer from,
          @RequestParam(required = false, defaultValue = "10") final Integer size
  ) {
    return itemRequestService.getAll(
            userId,
            PageRequest.of(from, size, Sort.by("created").descending())
    );
  }

  @GetMapping("/{requestId}")
  public ItemRequestDto get(
          @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
          @PathVariable("requestId") final Long requestId
  ) {
    return itemRequestService.get(userId, requestId);
  }
}
