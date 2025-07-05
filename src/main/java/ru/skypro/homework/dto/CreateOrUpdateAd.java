package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
