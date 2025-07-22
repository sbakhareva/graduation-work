package ru.skypro.homework.dto;

import lombok.*;

import java.util.List;

/**
 * ДТО, представляющий собой список объявлений для отображения на главной странице или в профиле пользователя.
 * Содержит информацию о количестве объявлений и список ДТО {@link Ad}
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ads {

    private Integer count;
    private List<Ad> results;
}

