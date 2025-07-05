package ru.skypro.homework.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.json.JSONObject;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user@gmail.com")
    void setPassword() throws Exception {
        JSONObject json = new JSONObject();
        json.put("currentPassword", "oldPass123");
        json.put("newPassword", "newPass123");
        mockMvc.perform(post("/users/set_password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString())
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void getUser() throws Exception {
        mockMvc.perform(get("/users/me")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("userEmail@oiia.ru"))
                .andExpect(jsonPath("$.firstName").value("bob"))
                .andExpect(jsonPath("$.lastName").value("newby"))
                .andExpect(jsonPath("$.phone").value("+77777777777"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.image").value("image"));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void updateUser() throws Exception {
        JSONObject json = new JSONObject();
        json.put("firstName", "alexa");
        json.put("lastName", "play");
        json.put("phone", "+7 964 341-33-43");
        mockMvc.perform(patch("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("alexa"))
                .andExpect(jsonPath("$.lastName").value("play"))
                .andExpect(jsonPath("$.phone").value("+7 964 341-33-43"));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void updateUserImage() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "avatar.jpg",
                "image/jpeg",
                "test-image-content".getBytes()
        );
        mockMvc.perform(multipart("/users/me/image")
                        .file(image)
                        .with(csrf())
                        .with(request -> { request.setMethod("PATCH"); return request; }))
                .andExpect(status().isOk());
    }
}