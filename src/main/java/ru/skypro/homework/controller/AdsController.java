package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import ru.skypro.homework.dto.*;

import org.springframework.http.MediaType;
import ru.skypro.homework.exception.*;
import ru.skypro.homework.service.impl.AdImageService;
import ru.skypro.homework.service.impl.AdsService;
import ru.skypro.homework.service.impl.CommentsService;

import java.io.IOException;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/ads")
public class AdsController {

    private static final Logger logger = LoggerFactory.getLogger(AdsController.class);

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
    @Operation(summary = "Получение всех объявлений", tags = {"Объявления"}, responses = {
            @ApiResponse(responseCode = "200", description = "Успешно получен список объявлений"),
            @ApiResponse(responseCode = "404", description = "Объявления не найдены"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    public ResponseEntity<Ads> getAllAds() {
        try {
            Ads ads = adsService.getAllAds();
            return ResponseEntity.ok(ads);
        } catch (NoAdsFoundException e) {
            return ResponseEntity.notFound().build();
        }

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
    @Operation(summary = "Добавление объявления", tags = {"Объявления"}, responses = {
            @ApiResponse(responseCode = "200", description = "Обновление добавлено"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Ошибка в данных запроса")
    })
    public ResponseEntity<Ad> addAd(@RequestPart("properties") CreateOrUpdateAd createOrUpdateAd,
                                    @RequestPart(value = "image") MultipartFile adImage,
                                    Authentication authentication) {
        if (authentication == null) {
            logger.warn("Пользователь не авторизован.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String email = authentication.getName();
            Ad ad = adsService.addAd(createOrUpdateAd, adImage, email);
            return ResponseEntity.ok(ad);
        } catch (NoUsersFoundByEmailException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получение информации об объявлении
     *
     * @param id             идентификатор объявления
     * @return объект {@link ExtendedAd}, содержащий подробную информацию об объявлении и создавшем его пользователе
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получение информации об объявлении", tags = {"Объявления"}, responses = {
            @ApiResponse(responseCode = "200", description = "Получена информация об объявлении"),
            @ApiResponse(responseCode = "404", description = "Объявлений не найдено"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    public ResponseEntity<ExtendedAd> getAdInfo(@PathVariable("id") Integer id) {
        try {
            ExtendedAd ad = adsService.getAdInfo(id);
            logger.info("Успешно получена информация об объявлении.");
            return ResponseEntity.ok(ad);
        } catch (NoAdsFoundException e) {
            return ResponseEntity.notFound().build();
        }

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
    @Operation(summary = "Удаление объявления", tags = {"Объявления"}, responses = {
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован или нет прав на операцию"),
            @ApiResponse(responseCode = "204", description = "Объявление успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    })
    public ResponseEntity<Void> deleteAd(@PathVariable("id") Integer id,
                                         Authentication authentication) {

        if (authentication == null) {
            logger.warn("Пользователь не авторизован.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String email = authentication.getName();
            adsService.deleteAd(id, email);
            return ResponseEntity.noContent().build();
        } catch (NoneOfYourBusinessException e) {
            logger.warn("У пользователя {} нет прав на выполнение этой операции", authentication.getName());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoAdsFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
    @Operation(summary = "Обновление информации об объявлении", tags = {"Объявления"}, responses = {
            @ApiResponse(responseCode = "200", description = "Информация успешно обновлена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован или нет прав на операцию"),
            @ApiResponse(responseCode = "404", description = "Объявление или пользователь не найдены")
    })
    public ResponseEntity<Ad> updateAds(@PathVariable(value = "id", required = true) Integer id,
                                        @RequestBody CreateOrUpdateAd ad,
                                        Authentication authentication) {
        if (authentication == null) {
            logger.warn("Пользователь не авторизован.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String email = authentication.getName();
            Ad updatedAd = adsService.updateAd(id, ad, email);
            logger.info("Информация объявления успешно обновлена");
            return ResponseEntity.ok(updatedAd);
        } catch (NoUsersFoundByEmailException | NoAdsFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoneOfYourBusinessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }

    /**
     * Получение всех объявлений авторизованного пользователя
     *
     * @param authentication информация о пользователе из хидеров
     * @return объект {@link Ads}, содержащий общее количество и список кратких ДТО объявлений {@link Ad}
     */
    @GetMapping("/me")
    @Operation(summary = "Получение объявлений авторизованного пользователя", tags = {"Объявления"}, responses = {
            @ApiResponse(responseCode = "200", description = "Успешно получен список объявлений"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Не найден пользователь или объявления")
    })
    public ResponseEntity<Ads> getAdsMe(Authentication authentication) {
        if (authentication == null) {
            logger.warn("Пользователь не авторизован.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String email = authentication.getName();
            Ads ads = adsService.getAdsMe(email);
            return ResponseEntity.ok(ads);
        } catch (NoUsersFoundByEmailException | NoAdsFoundException e) {
            return ResponseEntity.notFound().build();
        }

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
    @Operation(summary = "Обновление картинки объявления", tags = {"Объявления"}, responses = {
            @ApiResponse(responseCode = "200", description = "Изображение успешно обновлено"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован или не имеет прав на операцию"),
            @ApiResponse(responseCode = "404", description = "Не найдено изображение или объявление")
    })
    public ResponseEntity<String> updateImage(@PathVariable("id") Integer id,
                                              @RequestParam MultipartFile image,
                                              Authentication authentication) {
        if (authentication == null) {
            logger.warn("Пользователь не авторизован.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String email = authentication.getName();
            String path = adsService.updateImage(id, image, email);
            logger.info("Изображение успешно обновлено");
            return ResponseEntity.ok(path);
        } catch (NoAdsFoundException | NoImagesFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoneOfYourBusinessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.debug("Ошибка при обработке изображения");
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получение комментариев объявления
     *
     * @param id идентификатор объявления
     * @return объект {@link Comments}, содержащий количество комментариев к объявлению
     * и информацию о комментариях в формате ДТО {@link Comment}
     */
    @GetMapping("/{id}/comments")
    @Operation(summary = "Получение комментариев объявления", tags = {"Комментарии"}, responses = {
            @ApiResponse(responseCode = "200", description = "Успешно получен список комментариев"),
            @ApiResponse(responseCode = "404", description = "Не найдено объявление или у него нет комментариев")
    })
    public ResponseEntity<Comments> getComments(@PathVariable(value = "id", required = true) Integer id) {
        try {
            Comments comments = commentsService.getComments(id);
            return ResponseEntity.ok(comments);
        } catch (NoAdsFoundException | NoCommentsException e) {
            return ResponseEntity.notFound().build();
        }
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
    @Operation(summary = "Добавление комментария к объявлению", tags = {"Комментарии"}, responses = {
            @ApiResponse(responseCode = "200", description = "Успешно создан комментарий"),
            @ApiResponse(responseCode = "404", description = "Не найдено объявление или пользователь"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    public ResponseEntity<Comment> addComment(@PathVariable("id") Integer id,
                                              @RequestBody CreateOrUpdateComment comment,
                                              Authentication authentication) {
        if (authentication == null) {
            logger.warn("Пользователь не авторизован.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String email = authentication.getName();
            Comment newComment = commentsService.addComment(id, comment, email);
            logger.info("Комментарий успешно создан");
            return ResponseEntity.ok(newComment);
        } catch (NoUsersFoundByEmailException | NoAdsFoundException e) {
            return ResponseEntity.notFound().build();
        }

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
    @Operation(summary = "Удаление комментария", tags = {"Комментарии"}, responses = {
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован или не имеет прав на операцию"),
            @ApiResponse(responseCode = "204", description = "Комментарий успешно удален"),
            @ApiResponse(responseCode = "404", description = "Комментарии не найдены")
    })
    public ResponseEntity<Void> deleteComment(@PathVariable(value = "adId", required = true) Integer adId,
                                              @PathVariable(value = "commentId", required = true) Integer commentId,
                                              Authentication authentication) {
        if (authentication == null) {
            logger.warn("Пользователь не авторизован.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String email = authentication.getName();
            commentsService.deleteComment(adId, commentId, email);
            logger.info("Комментарий успешно удален");
            return ResponseEntity.noContent().build();
        } catch (NoneOfYourBusinessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoCommentsException e) {
            return ResponseEntity.notFound().build();
        }
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
    @Operation(summary = "Обновление комментария", tags = {"Комментарии"}, responses = {
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован или не имеет прав на операцию"),
            @ApiResponse(responseCode = "404", description = "У объявления пока нет комментариев"),
            @ApiResponse(responseCode = "200", description = "Комментарий успешно обновлен")
    })
    public ResponseEntity<Comment> updateComment(@PathVariable(value = "adId") Integer adId,
                                                 @PathVariable(value = "commentId") Integer commentId,
                                                 @RequestBody CreateOrUpdateComment comment,
                                                 Authentication authentication) {
        if (authentication == null) {
            logger.warn("Пользователь не авторизован.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String email = authentication.getName();
            Comment updatedComment = commentsService.updateComment(adId, commentId, comment, email);
            logger.info("Комментарий успешно обновлен");
            return ResponseEntity.ok(updatedComment);
        } catch (NoneOfYourBusinessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoCommentsException e) {
            return ResponseEntity.notFound().build();
        }
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
