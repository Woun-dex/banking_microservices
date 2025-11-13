package dev.bank.user_service.service;


import dev.bank.user_service.dto.CreateUserDto;
import dev.bank.user_service.dto.UserResponseDto;
import dev.bank.user_service.model.User;
import dev.bank.user_service.repository.UserRepository;
import jdk.jfr.Enabled;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

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
        user.setRole(dto.getRole());

        User saved = repo.save(user);

        return new UserResponseDto(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRole()
        );

    }

    public UserResponseDto getUserById(UUID id) {
        User user = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}
