package dev.bank.user_service.model;


import dev.bank.user_service.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.management.relation.Role;
import java.util.UUID;

@Entity
@Table(name="users")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

}
