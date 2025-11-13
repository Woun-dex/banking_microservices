package dev.bank.user_service.controller;


import dev.bank.user_service.dto.CreateUserDto;
import dev.bank.user_service.dto.UserResponseDto;
import dev.bank.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService service;
    
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid CreateUserDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createUser(dto));
    }

    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable UUID id){
        return service.getUserById(id);
    }
    
    
}
