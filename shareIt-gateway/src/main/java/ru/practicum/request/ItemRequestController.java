package ru.practicum.request;

import lombok.AllArgsConstructor;
import org.apache.http.protocol.RequestDate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestShortDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
            @Valid @RequestBody final ItemRequestShortDto requestDto
    ) {
        return itemRequestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getSelfRequests(
            @RequestHeader(REQUEST_HEADER_USER_ID) Long userId
    ) {
        return itemRequestClient.getSelfRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = "0") final Integer from,
            @RequestParam(required = false, defaultValue = "10") final Integer size
    ) {
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(
            @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
            @PathVariable("requestId") final Long requestId
    ) {
        return itemRequestClient.get(userId, requestId);
    }
}
