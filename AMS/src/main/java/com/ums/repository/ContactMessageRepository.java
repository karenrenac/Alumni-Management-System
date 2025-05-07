package com.ums.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ums.model.ContactMessage;

import java.util.List;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, String> {
    List<ContactMessage> findByAlumniId(int alumniId);
}
