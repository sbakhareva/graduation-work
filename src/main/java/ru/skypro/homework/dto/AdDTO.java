package ru.skypro.homework.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdDTO {

    private Integer author; // id автора объявления
    private String image;
    private Integer pk; // id объявления
    @Size(min = 0, max = 10000000)
    private Integer price;
    @Size(min = 4, max = 32)
    private String title;
}


