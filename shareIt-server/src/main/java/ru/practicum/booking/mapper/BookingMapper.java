package ru.practicum.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingRequestDto;
import ru.practicum.booking.dto.EntityDto;
import ru.practicum.booking.model.Booking;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

@Component
public class BookingMapper {

  public static Booking toBooking(BookingDto dto, User user, Item item) {
    return Booking.builder()
            .id(dto.getId())
            .start(dto.getStart())
            .end(dto.getEnd())
            .status(dto.getStatus())
            .item(item)
            .booker(user)
            .build();
  }

  public Booking toBooking(BookingRequestDto dto, User user, Item item) {
    return Booking.builder()
            .start(dto.getStart())
            .end(dto.getEnd())
            .status(dto.getStatus())
            .item(item)
            .booker(user)
            .build();
  }

  public BookingDto toBookingDto(Booking booking) {
    return BookingDto.builder()
            .id(booking.getId())
            .start(booking.getStart())
            .end(booking.getEnd())
            .status(booking.getStatus())
            .booker(EntityDto.builder()
                    .id(booking.getBooker().getId())
                    .build())
            .item(EntityDto.builder()
                    .id(booking.getItem().getId())
                    .name(booking.getItem().getName())
                    .build())
            .build();
  }
}
