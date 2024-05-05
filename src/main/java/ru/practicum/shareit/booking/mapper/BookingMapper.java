package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.EntityDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
