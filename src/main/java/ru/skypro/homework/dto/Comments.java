package ru.skypro.homework.dto;

import lombok.*;

import java.util.List;

/**
 * ДТО, представляющий собой список комментариев к объявлению.
 * Содержит информацию о количестве комментариев и список ДТО {@link Comment}
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comments {

    private Integer count;
    private List<Comment> results;
}
