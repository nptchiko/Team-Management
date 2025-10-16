package com.thehecotnha.backend.entity.user;


import com.thehecotnha.backend.enums.Role;
import com.thehecotnha.backend.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer user_id;

    String username;

    String email;

    String password;

    String avatar_link;

    @Enumerated(EnumType.STRING)
    Role role;

    String phone;

    @Enumerated(EnumType.STRING)
    UserStatus status;

    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime created_date;

    @UpdateTimestamp
    LocalDateTime updated_date;

}
