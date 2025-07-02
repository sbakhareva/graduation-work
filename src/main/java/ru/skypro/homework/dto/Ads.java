package ru.skypro.homework.dto;

import lombok.*;
import ru.skypro.homework.model.Ad;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ads {

    private Integer count;
    private List<AdDTO> results;
}

