package com.thehecotnha.backend.entity.user;

import com.thehecotnha.backend.entity.Project;
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
@Table(name = "user_project_lead")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProjectLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "project_id")
    Project project;

    @CreationTimestamp
    @Column(name = "assigned_date")
    LocalDateTime assignedDate;
}
