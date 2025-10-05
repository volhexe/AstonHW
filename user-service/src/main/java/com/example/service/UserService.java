package com.example.service;

import com.example.dto.UserCreateRequest;
import com.example.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto create(UserCreateRequest request);
    Optional<UserDto> getById(Long id);
    List<UserDto> getAll();
    UserDto update(Long id, UserCreateRequest request);
    boolean delete(Long id);
}
