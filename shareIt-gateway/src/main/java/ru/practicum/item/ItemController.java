package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.CommentRequestDto;
import ru.practicum.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;
    private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> add(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @Valid @RequestBody ItemDto item
    ) {
        return itemClient.add(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> edit(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto item
    ) {
        return itemClient.edit(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @PathVariable Long itemId
    ) {
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestParam(required = false, defaultValue = "0") final Integer from,
            @RequestParam(required = false, defaultValue = "10") final Integer size
    ) {
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam String text,
            @RequestParam(required = false, defaultValue = "0") final Integer from,
            @RequestParam(required = false, defaultValue = "10") final Integer size
    ) {
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> comment(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentRequestDto text
    ) {
        return itemClient.comment(userId, itemId, text);
    }
}
