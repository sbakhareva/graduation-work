package ru.skypro.homework.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Ad {

    private Integer author; // id автора объявления
    private String image;
    private Integer id; // id объявления
    private Integer price;
    private String title;
}
