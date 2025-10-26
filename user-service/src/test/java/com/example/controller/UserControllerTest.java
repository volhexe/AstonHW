package com.example.controller;

import com.example.dto.UserCreateRequest;
import com.example.dto.UserDto;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() throws Exception {
        UserCreateRequest req = new UserCreateRequest("Test", "test@example.com", 25);
        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();
        UserDto created = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertEquals("Test", created.name());
        assertEquals("test@example.com", created.email());
        assertEquals(25, created.age());
    }

    @Test
    void testGetUserById() throws Exception {
        User saved = userRepository.save(new User("Bob", "bob@example.com", 30));
        MvcResult result = mockMvc.perform(get("/api/users/" + saved.getId()))
                .andExpect(status().isOk())
                .andReturn();
        UserDto fetched = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertEquals(saved.getId(), fetched.id());
        assertEquals("Bob", fetched.name());
        assertEquals("bob@example.com", fetched.email());
        assertEquals(30, fetched.age());
    }

    @Test
    void testListUsers() throws Exception {
        userRepository.save(new User("Charlie", "charlie@example.com", 40));
        userRepository.save(new User("Dave", "dave@example.com", 28));
        MvcResult result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn();
        List<UserDto> users = objectMapper.readValue(result.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, UserDto.class));
        assertEquals(2, users.size());
        assertEquals("charlie@example.com", users.get(0).email());
        assertEquals("dave@example.com", users.get(1).email());
    }

    @Test
    void testUpdateUser() throws Exception {
        User saved = userRepository.save(new User("Eve", "eve@example.com", 22));
        UserCreateRequest updateReq = new UserCreateRequest("Eve Updated", "eve.updated@example.com", 23);
        MvcResult result = mockMvc.perform(put("/api/users/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andReturn();
        UserDto updated = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertEquals("Eve Updated", updated.name());
        assertEquals("eve.updated@example.com", updated.email());
        assertEquals(23, updated.age());
    }

    @Test
    void testDeleteUser() throws Exception {
        User saved = userRepository.save(new User("Delete", "deleteuser@example.com", 35));
        mockMvc.perform(delete("/api/users/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetUserNotFound() throws Exception {
        mockMvc.perform(get("/api/users/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        UserCreateRequest req = new UserCreateRequest("Ghost", "ghost@example.com", 100);
        mockMvc.perform(put("/api/users/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        mockMvc.perform(delete("/api/users/9999"))
                .andExpect(status().isNoContent());
    }
}