package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingItemRequestDto;
import ru.practicum.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;
    private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getBookings(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getUserItemBookings(
            @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") final Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") final Integer size
    ) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getBookingStatusByOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestBody @Valid BookingItemRequestDto requestDto) {
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeBooking(
            @RequestHeader(REQUEST_HEADER_USER_ID) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved
    ) {
        return bookingClient.updateBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }
}