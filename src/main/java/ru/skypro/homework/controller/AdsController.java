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
import ru.skypro.homework.service.AdImageService;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.CommentsService;

import java.io.IOException;

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
    public ResponseEntity<Ad> addAd(@RequestPart("properties") CreateOrUpdateAd createOrUpdateAd,
                    @RequestPart(value = "image", required = false) MultipartFile adImage,
                    Authentication authentication) {
        String email = authentication.getName();
        try {
            Ad ad = adsService.addAd(createOrUpdateAd, adImage, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(ad);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение информации об объявлении", tags = {"Объявления"})
    public ResponseEntity<ExtendedAd> getAdInfo(@PathVariable("id") Integer id,
                                Authentication authentication) {
        String email = authentication.getName();
        try {
            ExtendedAd ad = adsService.getAdInfo(id, email);
            return ResponseEntity.ok(ad);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление объявления", tags = {"Объявления"})
    public ResponseEntity<?> deleteAd(@PathVariable("id") Integer id,
                                      Authentication authentication) {
        String email = authentication.getName();
        try {
            adsService.deleteAd(id, email);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновление информации об объявлении", tags = {"Объявления"})
    public ResponseEntity<Ad> updateAds(@PathVariable(value = "id", required = true) Integer id,
                        @RequestBody CreateOrUpdateAd ad,
                        Authentication authentication) {
        String email = authentication.getName();
        try {
            Ad updatedAd = adsService.updateAd(id, ad, email);
            return ResponseEntity.ok(updatedAd);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Получение объявлений авторизованного пользователя", tags = {"Объявления"})
    public ResponseEntity<Ads> getAdsMe(Authentication authentication) {
        String email = authentication.getName();
        try {
            Ads ads = adsService.getAdsMe(email);
            return ResponseEntity.ok(ads);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{id}/image")
    @Operation(summary = "Обновление картинки объявления", tags = {"Объявления"})
    public ResponseEntity<String> updateImage(@PathVariable("id") Integer id,
                              @RequestParam MultipartFile image,
                              Authentication authentication) {
        String email = authentication.getName();
        try {
            String imagePath = adsService.updateImage(id, image, email);
            return ResponseEntity.ok(imagePath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Получение комментариев объявления", tags = {"Комментарии"})
    public ResponseEntity<Comments> getComments(@PathVariable(value = "id", required = true) Integer id) {
        try {
            Comments comments = commentsService.getComments(id);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Добавление комментария к объявлению", tags = {"Комментарии"})
    public ResponseEntity<Comment> addComment(@PathVariable("id") Integer id,
                              @RequestBody CreateOrUpdateComment comment,
                              Authentication authentication) {
        String email = authentication.getName();
        try {
            Comment newComment = commentsService.addComment(id, comment, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{adId}/comments/{commentId}")
    @Operation(summary = "Удаление комментария", tags = {"Комментарии"})
    public ResponseEntity<?> deleteComment(@PathVariable(value = "adId", required = true) Integer adId,
                                           @PathVariable(value = "commentId", required = true) Integer commentId,
                                           Authentication authentication) {
        String email = authentication.getName();
        try {
            commentsService.deleteComment(adId, commentId, email);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PatchMapping("/{adId}/comments/{commentId}")
    @Operation(summary = "Обновление комментария", tags = {"Комментарии"})
    public ResponseEntity<Comment> updateComment(@PathVariable(value = "adId") Integer adId,
                                 @PathVariable(value = "commentId") Integer commentId,
                                 @RequestBody CreateOrUpdateComment comment,
                                 Authentication authentication) {
        String email = authentication.getName();
        try {
            Comment updatedComment = commentsService.updateComment(adId, commentId, comment, email);
            return ResponseEntity.ok(updatedComment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping(value = "/images/{id}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/*"})
    @Operation(summary = "Получение изображения объявления", tags = {"Изображения"})
    public ResponseEntity<byte[]> getAdImage(@PathVariable("id") Integer id) {
        try {
            AdImage image = adImageService.getImage(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(image.getMediaType()));
            headers.setContentLength(image.getFileSize());
            headers.setCacheControl("max-age=31536000"); // Кэширование на 1 год

            // Пытаемся получить полное изображение с диска
            byte[] imageBytes = adImageService.getImageBytes(id);
            
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
