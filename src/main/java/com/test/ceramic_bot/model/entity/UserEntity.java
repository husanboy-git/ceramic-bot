package com.test.ceramic_bot.model.entity;

import com.test.ceramic_bot.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="users", uniqueConstraints = @UniqueConstraint(columnNames = "telegramId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    private Long telegramId;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    public static UserEntity of(Long telegramId, String name, Role role) {
        return new UserEntity(telegramId, name, role);
    }
}
