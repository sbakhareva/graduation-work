package ru.skypro.homework.controller;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/ads")
public class AdsController {

    @Description("Получение всех объявлений")
    @GetMapping
    public Ads getAllAds() {
        return new Ads(1, List.of(new Ad(1, "image", 123, 50, "ad")));
    }

    @Description("Добавление объявления")
    @PostMapping
    public Ad addAd(@RequestBody Ad ad,
                    @RequestParam MultipartFile adImage) {
        return new Ad(1, "image", 123, 50, "ad");
    }

    @Description("Получение информации об объявлении")
    @GetMapping("/{id}")
    public ExtendedAd getAdInfo(@PathVariable("id") Integer id) {
        return new ExtendedAd(1234, "bob", "newby", "description", "user@gmail.com", "image", "+79643413343", 50, "ad");
    }

    @Description("Удаление объявления")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable("id") Integer id) {
        return ResponseEntity.noContent().build();
    }

    @Description("Обновление информации об объявлении")
    @PatchMapping("/{id}")
    public Ad updateAds(@PathVariable(value = "id", required = true) Integer id,
                       @RequestBody CreateOrUpdateAd ad) {
        return new Ad(1, "image", 123, 50, "ad");
    }

    @Description("Получение объявлений авторизованного пользователя")
    @GetMapping("/me")
    public Ads getAdsMe() {
        return new Ads(1, List.of(new Ad(1, "image", 123, 50, "ad")));
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
        return new Comments(1, List.of(new Comment(1, "image", "author", LocalDateTime.now().getMinute(), 1276, "text")));
    }

    @Description("Добавление комментария к объявлению")
    @PostMapping("/{id}/comments")
    public Comment addComment(@PathVariable("id") Integer id,
                              @RequestBody CreateOrUpdateComment comment) {
        return new Comment(1, "image", "author", LocalDateTime.now().getMinute(), 1276, "text");
    }

    @Description("Удаление комментария")
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable(value = "adId", required = true) Integer adId,
                              @PathVariable(value = "commentId", required = true) Integer commentId) {
        return ResponseEntity.noContent().build();
    }

    @Description("Обновление комментария")
    @PatchMapping("/{adId}/comments/{commentId}")
    public Comment updateComment(@PathVariable(value = "adId", required = true) Integer adId,
                                 @PathVariable(value = "commentId", required = true) Integer commentId,
                                 @RequestBody CreateOrUpdateComment comment) {
        return new Comment(1, "image", "author", LocalDateTime.now().getMinute(), 1276, "text");
    }
}
