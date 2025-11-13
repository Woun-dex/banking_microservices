package dev.bank.user_service.dto;

import dev.bank.user_service.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private UUID id ;
    private String username;
    private String email;
    private UserRole role;
}
