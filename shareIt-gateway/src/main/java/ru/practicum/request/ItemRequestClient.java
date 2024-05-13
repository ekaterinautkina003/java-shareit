package ru.practicum.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.Client;
import ru.practicum.request.dto.ItemRequestShortDto;

import java.util.Map;

@Service
public class ItemRequestClient extends Client {

    private static final String PATH = "/requests";

    @Autowired
    public ItemRequestClient(
            @Value("${shareit-server.url}")
            String serverUrl,
            RestTemplateBuilder builder
    ) {
        super(builder.uriTemplateHandler(
                new DefaultUriBuilderFactory(serverUrl + PATH))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, ItemRequestShortDto requestDTO) {
        return request(HttpMethod.POST, "", userId, null, requestDTO);
    }

    public ResponseEntity<Object> getSelfRequests(Long userId) {
        return request(HttpMethod.GET, "", userId, null, null);
    }

    public ResponseEntity<Object> getAll(Long userId, Integer from, Integer size) {
        Map<String, Object> param = Map.of("from", from, "size", size);
        return request(HttpMethod.GET, "/all?from={from}&size={size}", userId, param, null);
    }

    public ResponseEntity<Object> get(Long userId, Long requestId) {
        return request(HttpMethod.GET, "/" + requestId, userId, null, null);
    }
}
