package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ad {

    @JsonProperty("author")
    private Integer author; // id автора объявления
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


