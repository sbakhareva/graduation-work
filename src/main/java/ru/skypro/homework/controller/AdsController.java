package ru.skypro.homework.controller;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import java.util.List;

@RestController
@RequestMapping("/ads")
public class AdsController {

    @Description("Получение всех объявлений")
    @GetMapping
    public Ads getAllAds() {
        return null;
    }

    @Description("Добавление объявления")
    @PostMapping
    public Ad addAd(@RequestBody Ad ad,
                    @RequestParam MultipartFile adImage) {
        return null;
    }

    @Description("Получение информации об объявлении")
    @GetMapping("/{id}")
    public ExtendedAd getAdInfo(@PathVariable("id") Integer id) {
        return null;
    }

    @Description("Удаление объявления")
    @DeleteMapping("/{id}")
    public void deleteAd(@PathVariable("id") Integer id) {
    }

    @Description("Обновление информации об объявлении")
    @PatchMapping("/{id}")
    public Ad updateAds(@PathVariable(value = "id", required = true) Integer id,
                       @RequestBody CreateOrUpdateAd ad) {
        return null;
    }

    @Description("Получение объявлений авторизованного пользователя")
    @GetMapping("/me")
    public Ads getAdsMe() {
        return null;
    }

    @Description("Обновление картинки объявления")
    @PatchMapping("/{id}/image")
    public String updateImage(@PathVariable("id") Integer id,
                              @RequestParam MultipartFile image) {
        return "image";
    }

    @Description("Получение комментариев объявления")
    @GetMapping("/{id}/comments")
    public Comments getComments(@PathVariable(value = "id", required = true) Integer id) {
        return null;
    }

    @Description("Добавление комментария к объявлению")
    @PostMapping("/{id}/comments")
    public Comment addComment(@PathVariable("id") Integer id,
                              @RequestBody CreateOrUpdateComment comment) {
        return null;
    }

    @Description("Удаление комментария")
    @DeleteMapping("/{adId}/comments/{commentId}")
    public void deleteComment(@PathVariable(value = "adId", required = true) Integer adId,
                              @PathVariable(value = "commentId", required = true) Integer commentId) {
    }

    @Description("Обновление комментария")
    @PatchMapping("/{adId}/comments/{commentId}")
    public Comment updateComment(@PathVariable(value = "adId", required = true) Integer adId,
                                 @PathVariable(value = "commentId", required = true) Integer commentId,
                                 @RequestBody CreateOrUpdateComment comment) {
        return null;
    }
}
