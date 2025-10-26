package com.example;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NotificationServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NotificationServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void testSendEmailApi() throws Exception {
        String json = "{\"email\":\"test@example.com\", \"operation\":\"CREATE\"}";

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());


        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(mailSender, Mockito.times(1)).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        assertEquals("test@example.com", sent.getTo()[0]);
        assertEquals("Добро пожаловать!", sent.getSubject());
        assertTrue(sent.getText().contains("успешно создан"));
    }

    @Test
    void testSendEmailApiOnDelete() throws Exception {
        String json = "{\"email\":\"deleteuser@example.com\", \"operation\":\"DELETE\"}";

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());


        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(mailSender, Mockito.times(1)).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        assertEquals("deleteuser@example.com", sent.getTo()[0]);
        assertEquals("Аккаунт удалён", sent.getSubject());
        assertTrue(sent.getText().contains("был удалён"));
    }
}