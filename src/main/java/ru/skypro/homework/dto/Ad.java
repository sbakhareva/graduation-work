package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.skypro.homework.model.AdEntity;

/**
 * ДТО для сущности {@link AdEntity} для передачи данных между сервером и пользователем.
 * Содержит краткую информацию об объявлении:
 * <ul>
 *     <li>{@link #author} — идентификатор автора объявления</li>
 *     <li>{@link #image} — путь к эндпоинту для получения байтов фото из файловой системы</li>
 *    <li>{@link #pk} — идентификатор объявления</li>
 *    <li>{@link #price} — цена</li>
 *    <li>{@link #title} — заголовок</li>
 *  </ul>
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ad {

    @JsonProperty("author")
    private Integer author;
    @JsonProperty("image")
    private String image;
    @JsonProperty("pk")
    private Integer pk; // id объявления
    @Size(min = 0, max = 10000000)
    @JsonProperty("price")
    private Integer price;
    @Size(min = 4, max = 32)
    @JsonProperty("title")
    private String title;
}


