package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import org.springframework.http.MediaType;
import ru.skypro.homework.model.AdImage;
import ru.skypro.homework.model.UserImage;
import ru.skypro.homework.service.AdImageService;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.CommentsService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@AllArgsConstructor
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/ads")
public class AdsController {

    private final AdsService adsService;
    private final CommentsService commentsService;
    private final AdImageService adImageService;

    @GetMapping
    @Operation(summary = "Получение всех объявлений", tags = {"Объявления"})
    public Ads getAllAds() {
        return adsService.getAllAds();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Добавление объявления", tags = {"Объявления"})
    public Ad addAd(@RequestPart("properties") CreateOrUpdateAd createOrUpdateAd,
                    @RequestPart(value = "image") MultipartFile adImage,
                    Authentication authentication) {
        String email = authentication.getName();
        return adsService.addAd(createOrUpdateAd, adImage, email);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Получение информации об объявлении", tags = {"Объявления"})
    public ExtendedAd getAdInfo(@PathVariable("id") Integer id,
                                Authentication authentication) {
        String email = authentication.getName();
        return adsService.getAdInfo(id, email);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление объявления", tags = {"Объявления"})
    public ResponseEntity<?> deleteAd(@PathVariable("id") Integer id,
                                      Authentication authentication) {
        String email = authentication.getName();
        adsService.deleteAd(id, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновление информации об объявлении", tags = {"Объявления"})
    public Ad updateAds(@PathVariable(value = "id", required = true) Integer id,
                        @RequestBody CreateOrUpdateAd ad,
                        Authentication authentication) {
        String email = authentication.getName();
        return adsService.updateAd(id, ad, email);
    }

    @GetMapping("/me")
    @Operation(summary = "Получение объявлений авторизованного пользователя", tags = {"Объявления"})
    public Ads getAdsMe(Authentication authentication) {
        String email = authentication.getName();
        return adsService.getAdsMe(email);
    }

    @PatchMapping("/{id}/image")
    @Operation(summary = "Обновление картинки объявления", tags = {"Объявления"})
    public String updateImage(@PathVariable("id") Integer id,
                              @RequestParam MultipartFile image,
                              Authentication authentication) {
        String email = authentication.getName();
        return adsService.updateImage(id, image, email);
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Получение комментариев объявления", tags = {"Комментарии"})
    public Comments getComments(@PathVariable(value = "id", required = true) Integer id) {
        return commentsService.getComments(id);
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Добавление комментария к объявлению", tags = {"Комментарии"})
    public Comment addComment(@PathVariable("id") Integer id,
                              @RequestBody CreateOrUpdateComment comment,
                              Authentication authentication) {
        String email = authentication.getName();
        return commentsService.addComment(id, comment, email);
    }

    @DeleteMapping("/{adId}/comments/{commentId}")
    @Operation(summary = "Удаление комментария", tags = {"Комментарии"})
    public ResponseEntity<?> deleteComment(@PathVariable(value = "adId", required = true) Integer adId,
                                           @PathVariable(value = "commentId", required = true) Integer commentId,
                                           Authentication authentication) {
        String email = authentication.getName();
        commentsService.deleteComment(adId, commentId, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{adId}/comments/{commentId}")
    @Operation(summary = "Обновление комментария", tags = {"Комментарии"})
    public Comment updateComment(@PathVariable(value = "adId") Integer adId,
                                 @PathVariable(value = "commentId") Integer commentId,
                                 @RequestBody CreateOrUpdateComment comment,
                                 Authentication authentication) {
        String email = authentication.getName();
        return commentsService.updateComment(adId, commentId, comment, email);
    }

    @GetMapping(value = "/ads-images/by-ad-id/{id}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/*"})
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Integer adId) {
        AdImage image = adImageService.getImage(adId);
        byte[] imageBytes;
        try {
            imageBytes = Files.readAllBytes(Path.of(image.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image.getMediaType()));
        headers.setContentLength(imageBytes.length);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
