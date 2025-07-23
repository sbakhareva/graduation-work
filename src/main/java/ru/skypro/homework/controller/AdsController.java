package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import org.springframework.http.MediaType;
import ru.skypro.homework.service.impl.AdImageService;
import ru.skypro.homework.service.impl.AdsService;
import ru.skypro.homework.service.impl.CommentsService;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/ads")
public class AdsController {

    private final AdsService adsService;
    private final CommentsService commentsService;
    private final AdImageService adImageService;

    public AdsController(AdsService adsService,
                         CommentsService commentsService,
                         AdImageService adImageService) {
        this.adsService = adsService;
        this.commentsService = commentsService;
        this.adImageService = adImageService;
    }

    /**
     * Получение всех существующих в базе данных объявлений
     *
     * @return объект {@link Ads}, содержащий общее количество и список кратких ДТО объявлений {@link Ad}
     */
    @GetMapping
    @Operation(summary = "Получение всех объявлений", tags = {"Объявления"})
    public Ads getAllAds() {
        return adsService.getAllAds();
    }

    /**
     * Добавление нового объявления
     *
     * @param createOrUpdateAd объект {@link CreateOrUpdateAd}, содержащий основную информацию об объявлении
     * @param adImage          картинка в формате MultipartFile
     * @param authentication   информация о пользователе из хидеров
     * @return объект {@link Ad}, содержащий информацию об объявлении и id создавшего его пользователя
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Добавление объявления", tags = {"Объявления"})
    public Ad addAd(@RequestPart("properties") CreateOrUpdateAd createOrUpdateAd,
                    @RequestPart(value = "image") MultipartFile adImage,
                    Authentication authentication) {
        String email = authentication.getName();
        return adsService.addAd(createOrUpdateAd, adImage, email);
    }

    /**
     * Получение информации об объявлении
     *
     * @param id             идентификатор объявления
     * @param authentication информация о пользователе из хидеров
     * @return объект {@link ExtendedAd}, содержащий подробную информацию об объявлении и создавшем его пользователе
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получение информации об объявлении", tags = {"Объявления"})
    public ExtendedAd getAdInfo(@PathVariable("id") Integer id,
                                Authentication authentication) {
        String email = authentication.getName();
        return adsService.getAdInfo(id, email);
    }

    /**
     * Удаление объявления
     *
     * @param id             идентификатор объявления
     * @param authentication информация о пользователе из хидеров
     * @return {@link ResponseEntity}:
     * <ul>
     *   <li><b>204 No Content</b> – если объявление успешно удалено</li>
     * </ul>
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление объявления", tags = {"Объявления"})
    public ResponseEntity<?> deleteAd(@PathVariable("id") Integer id,
                                      Authentication authentication) {
        String email = authentication.getName();
        adsService.deleteAd(id, email);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновление информации об объявлении
     *
     * @param id             идентификатор объявления
     * @param ad             объект {@link CreateOrUpdateAd}, содержащий данные для обновления информации объявления
     * @param authentication информация о пользователе из хидеров
     * @return объект {@link Ad}, содержащий информацию об объявлении и id создавшего его пользователя
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Обновление информации об объявлении", tags = {"Объявления"})
    public Ad updateAds(@PathVariable(value = "id", required = true) Integer id,
                        @RequestBody CreateOrUpdateAd ad,
                        Authentication authentication) {
        String email = authentication.getName();
        return adsService.updateAd(id, ad, email);
    }

    /**
     * Получение всех объявлений авторизованного пользователя
     *
     * @param authentication информация о пользователе из хидеров
     * @return объект {@link Ads}, содержащий общее количество и список кратких ДТО объявлений {@link Ad}
     */
    @GetMapping("/me")
    @Operation(summary = "Получение объявлений авторизованного пользователя", tags = {"Объявления"})
    public Ads getAdsMe(Authentication authentication) {
        String email = authentication.getName();
        return adsService.getAdsMe(email);
    }

    /**
     * Обновление картинки объявления
     *
     * @param id             идентификатор объявления
     * @param image          новая картинка
     * @param authentication информация о пользователе из хидеров
     * @return ссылка на картинку в файловой системе
     */
    @PatchMapping("/{id}/image")
    @Operation(summary = "Обновление картинки объявления", tags = {"Объявления"})
    public String updateImage(@PathVariable("id") Integer id,
                              @RequestParam MultipartFile image,
                              Authentication authentication) {
        String email = authentication.getName();
        return adsService.updateImage(id, image, email);
    }

    /**
     * Получение комментариев объявления
     *
     * @param id идентификатор объявления
     * @return объект {@link Comments}, содержащий количество комментариев к объявлению
     * и информацию о комментариях в формате ДТО {@link Comment}
     */
    @GetMapping("/{id}/comments")
    @Operation(summary = "Получение комментариев объявления", tags = {"Комментарии"})
    public Comments getComments(@PathVariable(value = "id", required = true) Integer id) {
        return commentsService.getComments(id);
    }

    /**
     * Добавление комментария к объявлению
     *
     * @param id             идентификатор объявления
     * @param comment        объект {@link CreateOrUpdateComment}, содержащий текст комментария
     * @param authentication информация о пользователе из хидеров
     * @return объект {@link Comment}, содержащий подробную информацию о комментарии и оставившем его пользователе
     */
    @PostMapping("/{id}/comments")
    @Operation(summary = "Добавление комментария к объявлению", tags = {"Комментарии"})
    public Comment addComment(@PathVariable("id") Integer id,
                              @RequestBody CreateOrUpdateComment comment,
                              Authentication authentication) {
        String email = authentication.getName();
        return commentsService.addComment(id, comment, email);
    }

    /**
     * Удаление комментария
     *
     * @param adId           идентификатор объявления
     * @param commentId      идентификатор комментария
     * @param authentication информация о пользователе из хидеров
     * @return {@link ResponseEntity}:
     * <ul>
     *   <li><b>204 No Content</b> – если комментарий успешно удален</li>
     * </ul>
     */
    @DeleteMapping("/{adId}/comments/{commentId}")
    @Operation(summary = "Удаление комментария", tags = {"Комментарии"})
    public ResponseEntity<?> deleteComment(@PathVariable(value = "adId", required = true) Integer adId,
                                           @PathVariable(value = "commentId", required = true) Integer commentId,
                                           Authentication authentication) {
        String email = authentication.getName();
        commentsService.deleteComment(adId, commentId, email);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновление комментария
     *
     * @param adId           идентификатор объявления
     * @param commentId      идентификатор комментария
     * @param comment        объект {@link CreateOrUpdateComment}, содержащий обновленный текст комментария
     * @param authentication информация о пользователе из хидеров
     * @return объект {@link Comment}, содержащий подробную информацию о комментарии и оставившем его пользователе
     */
    @PatchMapping("/{adId}/comments/{commentId}")
    @Operation(summary = "Обновление комментария", tags = {"Комментарии"})
    public Comment updateComment(@PathVariable(value = "adId") Integer adId,
                                 @PathVariable(value = "commentId") Integer commentId,
                                 @RequestBody CreateOrUpdateComment comment,
                                 Authentication authentication) {
        String email = authentication.getName();
        return commentsService.updateComment(adId, commentId, comment, email);
    }

    /**
     * Отображение картинок всех объявлений на главной странице и всех объявлений пользователя
     *
     * @param imageId идентификатор картинки в базе данных
     * @return картинка в виде массива байт
     */
    @Operation(summary = "Получение картинок объявлений", tags = {"Объявления"})
    @GetMapping(value = "/ads-images/{id}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/*"})
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Integer imageId) {
        return adImageService.getImage(imageId);
    }

    /**
     * Отображение картинки текущего открытого объявления
     *
     * @param adId    идентификатор объявления
     * @param imageId идентификатор картинки
     * @return картинка в виде массива байт
     */
    @Operation(summary = "Получение картинки текущего объявления", tags = {"Объявления"})
    @GetMapping(value = "/{adId}/ads-images/{imageId}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/*"})
    public ResponseEntity<byte[]> getThisImage(@PathVariable("adId") Integer adId,
                                               @PathVariable("imageId") Integer imageId) {
        return adImageService.getThisAdImage(adId, imageId);
    }
}
