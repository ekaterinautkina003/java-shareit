package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

  @Autowired
  private JacksonTester<ItemDto> json;

  @Test
  void toJson() throws Exception {
    var item = ItemDto.builder()
            .id(1L)
            .name("test")
            .description("test")
            .available(Boolean.TRUE)
            .build();

    JsonContent<ItemDto> result = json.write(item);
    assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(item.getId().intValue());
    assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
    assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(item.getDescription());
    assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(item.getAvailable());
    assertThat(result).extractingJsonPathValue("$.requestId").isNull();
  }
}