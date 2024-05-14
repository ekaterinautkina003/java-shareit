package ru.practicum.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.Client;
import ru.practicum.booking.dto.BookingItemRequestDto;
import ru.practicum.booking.dto.BookingState;

import java.util.Map;

@Component
public class BookingClient extends Client {

    private static final String PATH = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + PATH))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("state", state.name(), "from", from, "size", size);
        return request(HttpMethod.GET, "?state={state}&from={from}&size={size}", userId, parameters, null);
    }

    public ResponseEntity<Object> getBookingStatusByOwner(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("state", state.name(), "from", from, "size", size);
        return request(HttpMethod.GET, "/owner?state={state}&from={from}&size={size}", userId, parameters, null);
    }

    public ResponseEntity<Object> bookItem(long userId, BookingItemRequestDto requestDto) {
        return request(HttpMethod.POST, "", userId, null, requestDto);
    }

    public ResponseEntity<Object> updateBooking(long ownerId, long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return request(HttpMethod.PATCH, "/" + bookingId + "?approved={approved}", ownerId, parameters, null);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return request(HttpMethod.GET, "/" + bookingId, userId, null, null);
    }
}
