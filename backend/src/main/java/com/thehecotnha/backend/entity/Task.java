package com.thehecotnha.backend.entity;

import com.thehecotnha.backend.enums.Priority;
import com.thehecotnha.backend.enums.TaskStatus;
import com.thehecotnha.backend.enums.WorkType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer task_id;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    Sprint sprint;

    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    Task parent_task;

    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "work_type")
    WorkType workType ;

    @Column(name = "due_date")
    LocalDate dueDate;

    @Column(columnDefinition = "TEXT", name = "attachment_link")
    String attachmentLink;

    @Column(updatable = false, name = "created_date")
    @CreationTimestamp
    LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    LocalDateTime updatedDate;
}