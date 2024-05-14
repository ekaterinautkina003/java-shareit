package ru.practicum.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

  BookingDto create(Long userId, BookingRequestDto bookingDto);

  BookingDto update(Long userId, Long bookingId, boolean approved);

  BookingDto getById(Long userId, Long bookingId);

  List<BookingDto> getBookingsByUser(Long userId, String state, Pageable pageable);

  List<BookingDto> getBookingStatusByOwner(Long userId, String state, Pageable pageable);
}
