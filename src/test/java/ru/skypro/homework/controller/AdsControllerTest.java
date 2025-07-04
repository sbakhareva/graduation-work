package ru.skypro.homework.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AdsController.class)
class AdsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user@gmail.com")
    void getAllAds() throws Exception {

        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.results[0].author").value(1))
                .andExpect(jsonPath("$.results[0].image").value("image"))
                .andExpect(jsonPath("$.results[0].pk").value(123))
                .andExpect(jsonPath("$.results[0].title").value("adEntity"));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void addAd() throws Exception {
        JSONObject adJson = new JSONObject();
        adJson.put("price", 37);
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

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/ads")
                        .file(adJsonFile)
                        .file(adImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(37))
                .andExpect(jsonPath("$.title").value("title"));
    }


    @Test
    @WithMockUser(username = "user@gmail.com")
    void getAdInfo() throws Exception {
        mockMvc.perform(get("/ads/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pk").value(1234))
                .andExpect(jsonPath("$.firstName").value("bob"))
                .andExpect(jsonPath("$.lastName").value("newby"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.title").value("adEntity"));
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
        JSONObject adJson = new JSONObject();
        adJson.put("title", "title");
        adJson.put("description", "description");
        adJson.put("price", 50);

        mockMvc.perform(patch("/ads/1")
                        .content(adJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value(1))
                .andExpect(jsonPath("$.image").value("image"))
                .andExpect(jsonPath("$.pk").value(123))
                .andExpect(jsonPath("$.price").value(50))
                .andExpect(jsonPath("$.title").value("ad"));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void getAdsMe() throws Exception {
        mockMvc.perform(get("/ads/me")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.results[0].author").value(1))
                .andExpect(jsonPath("$.results[0].image").value("image"))
                .andExpect(jsonPath("$.results[0].pk").value(123))
                .andExpect(jsonPath("$.results[0].price").value(50))
                .andExpect(jsonPath("$.results[0].title").value("ad"));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void updateImage() throws Exception {
        MockMultipartFile adImage = new MockMultipartFile(
                "image",
                "image.jpg",
                "image/jpeg",
                "some-image-content".getBytes()
        );

        mockMvc.perform(multipart("/ads/1/image")
                        .file(adImage)
                        .with(csrf())
                        .with(request -> { request.setMethod("PATCH"); return request; }))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void getComments() throws Exception {
        mockMvc.perform(get("/ads/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.results[0].author").value(1))
                .andExpect(jsonPath("$.results[0].authorImage").value("image"))
                .andExpect(jsonPath("$.results[0].authorName").value("author"))
                .andExpect(jsonPath("$.results[0].pk").value(1276))
                .andExpect(jsonPath("$.results[0].text").value("text"));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void addComment() throws Exception {
        JSONObject commentJson = new JSONObject();
        commentJson.put("text", "text");

        mockMvc.perform(post("/ads/1/comments")
                        .content(commentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value(1))
                .andExpect(jsonPath("$.authorImage").value("image"))
                .andExpect(jsonPath("$.authorName").value("author"))
                .andExpect(jsonPath("$.text").value("text"));
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
        JSONObject commentJson = new JSONObject();
        commentJson.put("text", "text");

        mockMvc.perform(patch("/ads/1/comments/1")
                        .content(commentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value(1))
                .andExpect(jsonPath("$.authorImage").value("image"))
                .andExpect(jsonPath("$.authorName").value("author"))
                .andExpect(jsonPath("$.text").value("text"));
    }
}