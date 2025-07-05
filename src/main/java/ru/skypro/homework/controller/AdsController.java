package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.mappers.CreateOrUpdateAdDTOMapper;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.MediaType;
import ru.skypro.homework.service.AdsService;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/ads")
public class AdsController {

    private final CreateOrUpdateAdDTOMapper mapper = new CreateOrUpdateAdDTOMapper();
    private final AdsService adsService;

    public AdsController(AdsService adsService) {
        this.adsService = adsService;
    }

    @GetMapping
    @Operation(summary = "Получение всех объявлений", tags = {"Объявления"})
    public Ads getAllAds() {
        return adsService.getAllAds();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Добавление объявления", tags = {"Объявления"})
    public Ad addAd(@RequestPart("properties") CreateOrUpdateAd createOrUpdateAd,
                    @RequestPart("image") MultipartFile adImage,
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
    public ResponseEntity<?> deleteAd(@PathVariable("id") Integer id) {
        adsService.deleteAd(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновление информации об объявлении", tags = {"Объявления"})
    public Ad updateAds(@PathVariable(value = "id", required = true) Integer id,
                        @RequestBody CreateOrUpdateAd ad) {
        return adsService.updateAd(id, ad);
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
                              @RequestParam MultipartFile image) {
        return adsService.updateImage(id, image);
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Получение комментариев объявления", tags = {"Комментарии"})
    public Comments getComments(@PathVariable(value = "id", required = true) Integer id) {
        return adsService.getComments(id);
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Добавление комментария к объявлению", tags = {"Комментарии"})
    public Comment addComment(@PathVariable("id") Integer id,
                              @RequestBody CreateOrUpdateComment comment,
                              Authentication authentication) {
        String email = authentication.getName();
        return adsService.addComment(id, comment, email);
    }

    @DeleteMapping("/{adId}/comments/{commentId}")
    @Operation(summary = "Удаление комментария", tags = {"Комментарии"})
    public ResponseEntity<?> deleteComment(@PathVariable(value = "adId", required = true) Integer adId,
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
