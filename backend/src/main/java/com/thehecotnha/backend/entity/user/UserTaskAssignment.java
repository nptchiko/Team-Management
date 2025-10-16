package com.thehecotnha.backend.entity.user;

import com.thehecotnha.backend.entity.Task;
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
@Table(name = "user_task_assign")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserTaskAssignment {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    Task task;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @CreationTimestamp
    @Column(name = "assigned_date")
    LocalDateTime assignDate;

}
