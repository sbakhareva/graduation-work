package ru.skypro.homework.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Ads {

    private Integer count;
    private List<Ad> results;
}
