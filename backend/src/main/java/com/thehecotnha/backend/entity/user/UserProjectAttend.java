package com.thehecotnha.backend.entity.user;


import com.thehecotnha.backend.entity.Project;
import com.thehecotnha.backend.entity.Team;
import jakarta.annotation.Nullable;
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
@Table(name = "user_project_participate")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProjectAttend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    Project project;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @Nullable
    @JoinColumn(name = "team_id")
    Team team;

    @CreationTimestamp
    @Column(name = "joined_date")
    LocalDateTime joinedDate;
}
