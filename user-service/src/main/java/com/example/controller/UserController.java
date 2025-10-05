package com.example.controller;

import com.example.dto.UserCreateRequest;
import com.example.dto.UserDto;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDto> list() {
        log.info("GET /api/users list");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable("id") Long id) {
        log.info("GET /api/users/{}", id);
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserCreateRequest req) {
        log.info("POST /api/users - {}", req);
        UserDto created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable("id") Long id, @RequestBody UserCreateRequest req) {
        log.info("PUT /api/users/{}", id);
        try {
            return ResponseEntity.ok(service.update(id, req));
        } catch (RuntimeException e) {
            log.warn("Update failed for id={}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.info("DELETE /api/users/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}