package com.example.service;

import com.example.dto.UserCreateRequest;
import com.example.dto.UserDto;
import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private static final String TOPIC = "user-events";
    public UserServiceImpl(UserRepository repository, KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    private UserDto toDto(User u) {
        return new UserDto(u.getId(), u.getName(), u.getEmail(), u.getAge(), u.getCreatedAt());
    }

    @Override
    @Transactional
    public UserDto create(UserCreateRequest request) {
        log.info("Creating user email={} name={}", request.email(), request.name());
        User user = new User(request.name(), request.email(), request.age());

        try {
            User saved = repository.save(user);
            log.info("User created id={} email={}", saved.getId(), saved.getEmail());

            // Отправляем событие
            kafkaTemplate.send("user-events", new UserEvent("CREATE", saved.getEmail()));

            return toDto(saved);
        } catch (DataIntegrityViolationException dive) {
            log.warn("Constraint violation creating user email={}", request.email(), dive);
            throw new RuntimeException("Constraint violation: " + dive.getMessage(), dive);
        }
    }

    @Override
    public Optional<UserDto> getById(Long id) {
        log.debug("Fetching user id={}", id);
        return repository.findById(id).map(u -> {
            log.debug("Found user id={} email={}", u.getId(), u.getEmail());
            return toDto(u);
        });
    }

    @Override
    public List<UserDto> getAll() {
        log.debug("Listing all users");
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserCreateRequest request) {
        log.info("Updating user id={}", id);
        User user = new User();
        user.setId(id);
        if (request.name() != null && !request.name().isBlank()) user.setName(request.name().trim());
        if (request.email() != null && !request.email().isBlank()) user.setEmail(request.email().trim());
        if (request.age() != null) user.setAge(request.age());
        User saved = repository.save(user);
        log.info("User updated id={}", saved.getId());
        return toDto(saved);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        log.info("Deleting user id={}", id);
        Optional<User> userOpt = repository.findById(id);

        if (userOpt.isEmpty()) {
            log.warn("User to delete not found id={}", id);
            return false;
        }

        repository.deleteById(id);
        log.info("User deleted id={}", id);

        // Отправляем событие удаления
        kafkaTemplate.send("user-events", new UserEvent("DELETE", userOpt.get().getEmail()));

        return true;
    }
}
