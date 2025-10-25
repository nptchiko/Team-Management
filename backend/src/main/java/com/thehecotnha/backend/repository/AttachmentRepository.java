package com.thehecotnha.backend.repository;

import com.thehecotnha.backend.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<AttachmentRepository, Integer> {
=======
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {

}
