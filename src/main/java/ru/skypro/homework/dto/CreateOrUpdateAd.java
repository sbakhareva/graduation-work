package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.skypro.homework.model.AdEntity;

/**
 * ДТО для сущности {@link AdEntity} для создания новых объявлений или редактирования существующих.
 * Содержит основную информацию: заголовок, описание, цену
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CreateOrUpdateAd {

    @Size(min = 4, max = 32)
    @JsonProperty("title")
    private String title;
    @Size(min = 0, max = 10000000)
    @JsonProperty("price")
    private Integer price;
    @Size(min = 8, max = 64)
    @JsonProperty("description")
    private String description;
}
