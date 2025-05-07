package com.ums.service;

import java.util.List;
import com.ums.model.AlumniEvent;

public interface EventService {
    AlumniEvent insertEvent(AlumniEvent event);
    AlumniEvent updateEvent(AlumniEvent event, String eventId);
    void deleteEvent(String eventId);
    List<AlumniEvent> fetchAllEvents();
    AlumniEvent fetchEventById(String eventId);
}
