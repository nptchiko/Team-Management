package com.thehecotnha.backend.entity.user;


import com.thehecotnha.backend.entity.Team;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.dialect.InnoDBStorageEngine;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_team_belong")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserTeamBelong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team team;

    @CreationTimestamp
    @JoinColumn(name = "joined_date")
    LocalDateTime joinedDate;

}
