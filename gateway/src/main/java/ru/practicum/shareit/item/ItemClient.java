package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemDtoRequest itemDtoRequest, Long userId) {
        checkNameItem(itemDtoRequest);
        checkDescriptionItem(itemDtoRequest);
        checkAvailableItem(itemDtoRequest);
        return post("", userId, itemDtoRequest);
    }

    public ResponseEntity<Object> getAllItems(int from, int size, Long userId) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> updateItem(ItemDtoRequest itemDtoRequest, Long itemId, Long userId) {
        return patch("/" + itemId, userId, itemDtoRequest);
    }

    public ResponseEntity<Object> searchItem(Long userId, int from, int size, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(CommentDto commentDto, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

    private void checkNameItem(ItemDtoRequest item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Не указано имя предмета");
        }
    }

    private void checkDescriptionItem(ItemDtoRequest item) {
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Не указано описание предмета");
        }
    }

    private void checkAvailableItem(ItemDtoRequest item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("Не указана доступность товара");
        }
    }
}
