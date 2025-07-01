package ru.skypro.homework.dto;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Ads {

    private Integer count;
    private List<Ad> results;

    // Метод для преобразования из сущности в DTO
    public static Ads fromEntity(List<ru.skypro.homework.model.Ad> adEntities) {
        if (adEntities == null) {
            return null;
        }
        Ads ads = new Ads();
        ads.setCount(adEntities.size());
        ads.setResults(adEntities.stream()
                .map(Ad::fromEntity) // Используем метод fromEntity из класса Ad
                .collect(Collectors.toList()));
        return ads;
    }

    // Метод для преобразования из DTO в сущность
    public static List<ru.skypro.homework.model.Ad> toEntity(Ads adsDto) {
        if (adsDto == null) {
            return null;
        }
        return adsDto.getResults().stream()
                .map(Ad::toEntity) // Используем метод toEntity из класса Ad
                .collect(Collectors.toList());
    }
}

