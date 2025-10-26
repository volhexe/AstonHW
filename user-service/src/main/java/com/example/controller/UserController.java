package com.example.controller;

import com.example.dto.UserCreateRequest;
import com.example.dto.UserDto;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
@Tag(name = "Users", description = "API for managing users: creation, reading, updating, and deletion.")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Get list of all users",
            description = "Returns the full list of users in UserDto format."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User list successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<UserDto> list() {
        log.info("GET /api/users list");
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Returns the user by the specified ID. If the user is not found, returns 404."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserDto> get(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        log.info("GET /api/users/{}", id);
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user based on the data from the request. Returns the created user with ID and createdAt."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data (e.g., duplicate email)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserDto> create(@RequestBody UserCreateRequest req) {
        log.info("POST /api/users - {}", req);
        UserDto created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update user by ID",
            description = "Updates an existing user. Only provided fields (name, email, age) are updated. If the user is not found, returns 404."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserDto> update(
            @Parameter(description = "ID of the user to update", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody UserCreateRequest req) {
        log.info("PUT /api/users/{}", id);
        UserDto updated = service.update(id, req);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            log.warn("User not found for update id={}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user by ID",
            description = "Deletes the user by the specified ID. Returns 204 on success, even if the user is not found (no content)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the user to delete", required = true, example = "1")
            @PathVariable("id") Long id) {
        log.info("DELETE /api/users/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}