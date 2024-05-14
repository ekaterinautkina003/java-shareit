package ru.practicum.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingRequestDto;
import ru.practicum.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

  private final BookingService bookingService;

  @PostMapping
  public BookingDto create(
          @RequestHeader("X-Sharer-User-Id") Long userId,
          @RequestBody BookingRequestDto bookingRequestDTO
  ) {
    return bookingService.create(userId, bookingRequestDTO);
  }

  @PatchMapping("/{bookingId}")
  public BookingDto update(
          @RequestHeader("X-Sharer-User-Id") Long userId,
          @PathVariable Long bookingId,
          @RequestParam(name = "approved") boolean approved
  ) {
    return bookingService.update(userId, bookingId, approved);
  }

  @GetMapping("/{bookingId}")
  public BookingDto getById(
          @RequestHeader("X-Sharer-User-Id") Long userId,
          @PathVariable Long bookingId
  ) {
    return bookingService.getById(userId, bookingId);
  }

  @GetMapping
  public List<BookingDto> getAll(
          @RequestHeader("X-Sharer-User-Id") Long userId,
          @RequestParam(name = "state", defaultValue = "ALL") String state,
          @RequestParam(required = false, defaultValue = "0") final Integer from,
          @RequestParam(required = false, defaultValue = "10") final Integer size
  ) {
    int page = from > 0 ? from / size : from;
    return bookingService.getBookingsByUser(userId, state, PageRequest.of(page, size));
  }

  @GetMapping("/owner")
  public List<BookingDto> getAllByUser(
          @RequestHeader("X-Sharer-User-Id") Long userId,
          @RequestParam(name = "state", defaultValue = "ALL") String state,
          @RequestParam(required = false, defaultValue = "0") final Integer from,
          @RequestParam(required = false, defaultValue = "10") final Integer size
  ) {
    return bookingService.getBookingStatusByOwner(userId, state, PageRequest.of(from, size));
  }
}
