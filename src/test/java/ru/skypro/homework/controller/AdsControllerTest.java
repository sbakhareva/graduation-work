package ru.skypro.homework.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.impl.AdImageService;
import ru.skypro.homework.service.impl.AdsService;
import ru.skypro.homework.service.impl.CommentsService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AdsController.class)
class AdsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AdsService adsService;
    @MockitoBean
    private AdImageService adImageService;
    @MockitoBean
    private CommentsService commentsService;

    @Test
    @WithMockUser(username = "user@gmail.com")
    void getAllAds() throws Exception {
        Ad ad = new Ad(1, "image", 123, 50, "title");
        Ads ads = new Ads(1, List.of(ad));
        when(adsService.getAllAds()).thenReturn(ads);

        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(ads.getCount()))
                .andExpect(jsonPath("$.results[0].author").value(ad.getAuthor()))
                .andExpect(jsonPath("$.results[0].image").value(ad.getImage()))
                .andExpect(jsonPath("$.results[0].pk").value(ad.getPk()))
                .andExpect(jsonPath("$.results[0].title").value(ad.getTitle()));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void addAd() throws Exception {
        Ad ad = new Ad(1, "image", 123, 50, "title");

        JSONObject adJson = new JSONObject();
        adJson.put("price", 50);
        adJson.put("description", "ad");
        adJson.put("title", "title");

        MockMultipartFile adImage = new MockMultipartFile(
                "image",
                "image.jpg",
                "image/jpeg",
                "some-image-content".getBytes()
        );

        MockMultipartFile adJsonFile = new MockMultipartFile(
                "properties",
                "ad.json",
                "application/json",
                adJson.toString().getBytes(StandardCharsets.UTF_8)
        );

        when(adsService.addAd(any(CreateOrUpdateAd.class), any(MultipartFile.class), anyString())).thenReturn(ad);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/ads")
                        .file(adJsonFile)
                        .file(adImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(ad.getPrice()))
                .andExpect(jsonPath("$.title").value(ad.getTitle()));
    }


    @Test
    @WithMockUser(username = "user@gmail.com")
    void getAdInfo() throws Exception {
        ExtendedAd adInfo = new ExtendedAd(1, "firstName", "lastName", "description", "email", "image", "phone", 50, "title");

        when(adsService.getAdInfo(anyInt(), anyString())).thenReturn(adInfo);

        mockMvc.perform(get("/ads/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pk").value(adInfo.getPk()))
                .andExpect(jsonPath("$.authorFirstName").value(adInfo.getAuthorFirstName()))
                .andExpect(jsonPath("$.authorLastName").value(adInfo.getAuthorLastName()))
                .andExpect(jsonPath("$.description").value(adInfo.getDescription()))
                .andExpect(jsonPath("$.title").value(adInfo.getTitle()));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void deleteAd() throws Exception {
        mockMvc.perform(delete("/ads/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void updateAds() throws Exception {
        Ad ad = new Ad(1, "image", 123, 50, "title");

        JSONObject adJson = new JSONObject();
        adJson.put("title", "title");
        adJson.put("description", "description");
        adJson.put("price", 50);

        when(adsService.updateAd(anyInt(), any(CreateOrUpdateAd.class), anyString())).thenReturn(ad);

        mockMvc.perform(patch("/ads/1")
                        .content(adJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value(ad.getAuthor()))
                .andExpect(jsonPath("$.image").value(ad.getImage()))
                .andExpect(jsonPath("$.pk").value(ad.getPk()))
                .andExpect(jsonPath("$.price").value(ad.getPrice()))
                .andExpect(jsonPath("$.title").value(ad.getTitle()));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void getAdsMe() throws Exception {
        Ad ad = new Ad(1, "image", 123, 50, "title");
        Ads ads = new Ads(1, List.of(ad));
        when(adsService.getAdsMe(anyString())).thenReturn(ads);

        mockMvc.perform(get("/ads/me")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(ads.getCount()))
                .andExpect(jsonPath("$.results[0].author").value(ad.getAuthor()))
                .andExpect(jsonPath("$.results[0].image").value(ad.getImage()))
                .andExpect(jsonPath("$.results[0].pk").value(ad.getPk()))
                .andExpect(jsonPath("$.results[0].price").value(ad.getPrice()))
                .andExpect(jsonPath("$.results[0].title").value(ad.getTitle()));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void updateImage() throws Exception {
        String image = "this string could be an image";
        when(adsService.updateImage(anyInt(), any(MultipartFile.class), anyString())).thenReturn(image);

        MockMultipartFile adImage = new MockMultipartFile(
                "image",
                "image.jpg",
                "image/jpeg",
                "some-image-content".getBytes()
        );

        mockMvc.perform(multipart("/ads/1/image")
                        .file(adImage)
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void getComments() throws Exception {
        Comment comment = new Comment(123, "author", "image", 1587, 456, "text");
        Comments comments = new Comments(1, List.of(comment));
        when(commentsService.getComments(anyInt())).thenReturn(comments);

        mockMvc.perform(get("/ads/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.results[0].author").value(comment.getAuthor()))
                .andExpect(jsonPath("$.results[0].authorImage").value(comment.getAuthorImage()))
                .andExpect(jsonPath("$.results[0].authorFirstName").value(comment.getAuthorFirstName()))
                .andExpect(jsonPath("$.results[0].pk").value(comment.getPk()))
                .andExpect(jsonPath("$.results[0].text").value(comment.getText()));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void addComment() throws Exception {
        Comment comment = new Comment(123, "author", "image", 1587, 456, "text");
        when(commentsService.addComment(anyInt(), any(CreateOrUpdateComment.class), anyString())).thenReturn(comment);

        JSONObject commentJson = new JSONObject();
        commentJson.put("text", "text");

        mockMvc.perform(post("/ads/1/comments")
                        .content(commentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value(comment.getAuthor()))
                .andExpect(jsonPath("$.authorImage").value(comment.getAuthorImage()))
                .andExpect(jsonPath("$.authorFirstName").value(comment.getAuthorFirstName()))
                .andExpect(jsonPath("$.text").value(comment.getText()));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void deleteComment() throws Exception {
        mockMvc.perform(delete("/ads/1/comments/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void updateComment() throws Exception {
        Comment comment = new Comment(123, "author", "image", 1587, 456, "text");
        when(commentsService.updateComment(anyInt(), anyInt(), any(CreateOrUpdateComment.class), anyString())).thenReturn(comment);

        JSONObject commentJson = new JSONObject();
        commentJson.put("text", "text");

        mockMvc.perform(patch("/ads/1/comments/1")
                        .content(commentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value(comment.getAuthor()))
                .andExpect(jsonPath("$.authorImage").value(comment.getAuthorImage()))
                .andExpect(jsonPath("$.authorFirstName").value(comment.getAuthorFirstName()))
                .andExpect(jsonPath("$.text").value(comment.getText()));
    }
}