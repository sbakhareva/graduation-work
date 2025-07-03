package ru.skypro.homework.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.skypro.homework.dto.Ad;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AdsController.class)
class AdsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user@gmail.com", password = "password")
    void getAllAds() throws Exception {

        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.results[0].author").value(1))
                .andExpect(jsonPath("$.results[0].image").value("image"))
                .andExpect(jsonPath("$.results[0].pk").value(123))
                .andExpect(jsonPath("$.results[0].title").value("adEntity"));
    }

    // TODO: ложусь спасть, не получилось написать тест
    @Test
    @WithMockUser(username = "user@gmail.com")
    void addAd() throws Exception {
        JSONObject adJson = new JSONObject();
        adJson.put("price", 37);
        adJson.put("description", "ad");
        adJson.put("title", "title");

        MockMultipartFile adImage = new MockMultipartFile("adImage", "image.jpg", "image/jpeg", "some-image-content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/ads")
                        .file(adImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("ad", adJson.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void getAdInfo() {
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void deleteAd() {
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void updateAds() {
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void getAdsMe() {
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void updateImage() {
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
    void addComment() {
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void deleteComment() {
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void updateComment() {
    }
}