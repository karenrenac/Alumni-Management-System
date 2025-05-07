package com.ums.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ums.model.EventRegistration;

import java.util.List;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Integer> {
    List<EventRegistration> findByAlumniId(int alumniId);
    List<EventRegistration> findByEventId(String eventId);
}
