package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.RequestBookingStatus;

import java.util.List;

public interface BookingService {

  BookingDto create(Long userId, BookingRequestDto bookingDto);

  BookingDto update(Long userId, Long bookingId, boolean approved);

  BookingDto getById(Long userId, Long bookingId);

  List<BookingDto> getBookingsByUser(Long userId, String state);

  List<BookingDto> getBookingStatusByOwner(Long userId, String state);
}
