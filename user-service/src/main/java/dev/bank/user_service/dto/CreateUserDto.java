package dev.bank.user_service.dto;


import dev.bank.user_service.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {
    private String username;
    private String email;
    private String password;
    private UserRole role;
}
