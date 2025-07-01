package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @GetMapping
    @Operation(summary = "Получение всех объявлений", tags = {"Объявления"})
    public Ads getAllAds() {
        return new Ads(1, List.of(new Ad(1, "image", 123, 50, "ad")));
    }

    @PostMapping
    @Operation(summary = "Добавление объявления", tags = {"Объявления"})
    public Ad addAd(@RequestBody Ad ad,
                    @RequestParam MultipartFile adImage) {
        return new Ad(1, "image", 123, 50, "ad");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение информации об объявлении", tags = {"Объявления"})
    public ExtendedAd getAdInfo(@PathVariable("id") Integer id) {
        return new ExtendedAd(1234, "bob", "newby", "description", "user@gmail.com", "image", "+79643413343", 50, "ad");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление объявления", tags = {"Объявления"})
    public ResponseEntity<Void> deleteAd(@PathVariable("id") Integer id) {
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновление информации об объявлении", tags = {"Объявления"})
    public Ad updateAds(@PathVariable(value = "id", required = true) Integer id,
                       @RequestBody CreateOrUpdateAd ad) {
        return new Ad(1, "image", 123, 50, "ad");
    }

    @GetMapping("/me")
    @Operation(summary = "Получение объявлений авторизованного пользователя", tags = {"Объявления"})
    public Ads getAdsMe() {
        return new Ads(1, List.of(new Ad(1, "image", 123, 50, "ad")));
    }

    @PatchMapping("/{id}/image")
    @Operation(summary = "Обновление картинки объявления", tags = {"Объявления"})
    public String updateImage(@PathVariable("id") Integer id,
                              @RequestParam MultipartFile image) {
        return "image";
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Получение комментариев объявления", tags = {"Комментарии"})
    public Comments getComments(@PathVariable(value = "id", required = true) Integer id) {
        return new Comments(1, List.of(new Comment(1, "image", "author", LocalDateTime.now().getMinute(), 1276, "text")));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Добавление комментария к объявлению", tags = {"Комментарии"})
    public Comment addComment(@PathVariable("id") Integer id,
                              @RequestBody CreateOrUpdateComment comment) {
        return new Comment(1, "image", "author", LocalDateTime.now().getMinute(), 1276, "text");
    }

    @DeleteMapping("/{adId}/comments/{commentId}")
    @Operation(summary = "Удаление комментария", tags = {"Комментарии"})
    public ResponseEntity<Void> deleteComment(@PathVariable(value = "adId", required = true) Integer adId,
                              @PathVariable(value = "commentId", required = true) Integer commentId) {
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{adId}/comments/{commentId}")
    @Operation(summary = "Обновление комментария", tags = {"Комментарии"})
    public Comment updateComment(@PathVariable(value = "adId", required = true) Integer adId,
                                 @PathVariable(value = "commentId", required = true) Integer commentId,
                                 @RequestBody CreateOrUpdateComment comment) {
        return new Comment(1, "image", "author", LocalDateTime.now().getMinute(), 1276, "text");
    }
}
