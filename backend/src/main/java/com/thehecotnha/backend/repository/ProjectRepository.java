package com.thehecotnha.backend.repository;

import com.thehecotnha.backend.entity.Project;
import com.thehecotnha.backend.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository <Project, Integer> {
}
