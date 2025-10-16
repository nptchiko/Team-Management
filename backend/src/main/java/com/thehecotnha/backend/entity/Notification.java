package com.thehecotnha.backend.entity;

import com.thehecotnha.backend.entity.user.User;
import com.thehecotnha.backend.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer notification_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "task_id")
    Task task;

    @Column(nullable = false)
    String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    String content;

    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime created_date;

    @Column(nullable = false)
    Boolean is_read;

    @Enumerated(EnumType.STRING)
    NotificationType type;
}