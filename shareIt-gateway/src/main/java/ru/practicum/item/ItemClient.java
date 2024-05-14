package ru.practicum.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.Client;
import ru.practicum.item.dto.CommentRequestDto;
import ru.practicum.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends Client {

    private static final String PATH = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + PATH))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> add(long userId, ItemDto item) {
        return request(HttpMethod.POST, "", userId, null, item);
    }

    public ResponseEntity<Object> edit(long userId, Long itemId, ItemDto item) {
        return request(HttpMethod.PATCH, "/" + itemId, userId, null, item);
    }

    public ResponseEntity<Object> getById(long userId, Long itemId) {
        return request(HttpMethod.GET, "/" + itemId, userId, null, null);
    }

    public ResponseEntity<Object> getUserItems(long userId, Integer from, Integer size) {
        Map<String, Object> params = Map.of("from", from, "size", size);
        return request(HttpMethod.GET, "?from={from}&size={size}", userId, params, null);
    }


    public ResponseEntity<Object> search(String text, Integer from, Integer size) {
        Map<String, Object> params = Map.of("text", text, "from", from, "size", size);
        return request(HttpMethod.GET, "/search?text={text}&from={from}&size={size}", null, params, null);
    }

    public ResponseEntity<Object> comment(long userId, Long itemId, CommentRequestDto text) {
        return request(HttpMethod.POST, "/" + itemId + "/comment", userId, null, text);
    }
}