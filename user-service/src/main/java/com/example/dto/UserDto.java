package com.example.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt
) {}
