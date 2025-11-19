package dev.bank.user_service.controller;


import dev.bank.user_service.JwtService;
import dev.bank.user_service.dto.CreateUserDto;
import dev.bank.user_service.dto.UserResponseDto;
import dev.bank.user_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService service;

    private final JwtService jwtService;
    
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid CreateUserDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createUser(dto));
    }

    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable UUID id){
        return service.getUserById(id);
    }

    @GetMapping("/me")
    public Map<String , String> getProfile(HttpServletRequest req) {
        String userId = (String) req.getAttribute("userId");
        String role = (String) req.getAttribute("role");
        return Map.of("userId", userId, "role", role);
    }
      
    
    
}
