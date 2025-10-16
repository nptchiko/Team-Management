package com.thehecotnha.backend.entity;


import com.thehecotnha.backend.enums.ProjectStatus;
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
@Table(name = "project")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Project {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer project_id;

    @Column(nullable = false, length = 50, name = "sprint_key")
    String sprintKey;

    @Column(nullable = false)
    String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ProjectStatus status;

    @Column(updatable = false, name = "created_date")
    @CreationTimestamp
    LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    LocalDateTime updatedDate;

}
