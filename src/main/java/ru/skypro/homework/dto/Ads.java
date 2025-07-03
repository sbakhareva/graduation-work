package ru.skypro.homework.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ads {

    private Integer count;
    private List<Ad> results;
}

