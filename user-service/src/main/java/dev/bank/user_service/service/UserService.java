package dev.bank.user_service.service;


import dev.bank.user_service.JwtService;
import dev.bank.user_service.dto.CreateUserDto;
import dev.bank.user_service.dto.LoginResponse;
import dev.bank.user_service.dto.UserResponseDto;
import dev.bank.user_service.enums.UserRole;
import dev.bank.user_service.model.User;
import dev.bank.user_service.repository.UserRepository;
import jdk.jfr.Enabled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final JwtService jwtService;

    Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();


    public UserResponseDto createUser(CreateUserDto dto) {

        if (repo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String hashedPassword = encoder.encode(dto.getPassword());

        User user = new User();

        user.setUsername(dto.getUsername());
        user.setPassword(hashedPassword);
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole() != null ? dto.getRole() : UserRole.CUSTOMER);

        User saved = repo.save(user);

        return new UserResponseDto(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRole()
        );

    }

    public UserResponseDto getUserById(UUID id) {
        log.info("Getting user by ID: {}", id);
        User user = repo.findById(id)
            .orElseThrow(() -> {
                log.error("User not found with ID: {}", id);
                return new RuntimeException("User not found with ID: " + id);
            });

        log.info("Found user: {} ({})", user.getUsername(), user.getId());
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    public Map<String,String> loginRequest(String username, String password) {
        User user = repo.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }
        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        String token = jwtService.generateToken(username , String.valueOf(user.getRole()));
        return Map.of("userId", user.getId().toString(), "token", token);
    }
}
