package ru.practicum.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.Client;
import ru.practicum.user.dto.UserDto;

@Component
public class UserClient extends Client {

    private static final String PATH = "/users";

    public UserClient(
            @Value("${shareit-server.url}") String serverUrl,
            RestTemplateBuilder builder
    ) {
        super(builder.uriTemplateHandler(
                new DefaultUriBuilderFactory(serverUrl + PATH))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> save(UserDto user) {
        return request(HttpMethod.POST, "", null, null, user);
    }

    public ResponseEntity<Object> getById(Long id) {
        return request(HttpMethod.GET, "/" + id, null, null, null);
    }

    public ResponseEntity<Object> getAll() {
        return request(HttpMethod.GET, "/", null, null, null);
    }

    public ResponseEntity<Object> update(Long id, UserDto user) {
        return request(HttpMethod.PATCH, "/" + id, null, null, user);
    }

    public ResponseEntity<Object> deleteById(Long id) {
        return request(HttpMethod.DELETE, "/" + id, null, null, null);
    }
}