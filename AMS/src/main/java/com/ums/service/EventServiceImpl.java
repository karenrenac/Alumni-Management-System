package com.ums.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ums.model.AlumniEvent;
import com.ums.repository.AlumniEventRepository;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private AlumniEventRepository eventRepo;

    @Override
    public AlumniEvent insertEvent(AlumniEvent event) {
        return eventRepo.save(event);
    }

    @Override
    public AlumniEvent updateEvent(AlumniEvent event, String eventId) {
        Optional<AlumniEvent> optionalEvent = eventRepo.findById(eventId);
        if (optionalEvent.isPresent()) {
            AlumniEvent existingEvent = optionalEvent.get();
            existingEvent.setTitle(event.getTitle());
            existingEvent.setDescription(event.getDescription());
            existingEvent.setVenue(event.getVenue());
            existingEvent.setEventDate(event.getEventDate());
            existingEvent.setEventTime(event.getEventTime());
            return eventRepo.save(existingEvent);
        } else {
            throw new RuntimeException("Event not found with ID: " + eventId);
        }
    }

    @Override
    public void deleteEvent(String eventId) {
        eventRepo.deleteById(eventId);
    }

    @Override
    public List<AlumniEvent> fetchAllEvents() {
        return eventRepo.findAll();
    }

    @Override
    public AlumniEvent fetchEventById(String eventId) {
        return eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
    }
}
