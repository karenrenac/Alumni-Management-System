package com.ums.service;

import com.ums.model.ActivityLog;
import com.ums.model.Admin;
import com.ums.model.ContactMessage;
import com.ums.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactMessageRepository messageRepo;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private HttpSession session; // To fetch logged-in admin

    @Override
    public ContactMessage sendMessageToAdmin(ContactMessage message) {
        ContactMessage saved = messageRepo.save(message);

        // Logging logic
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin != null) {
            ActivityLog log = new ActivityLog();
            log.setAdminId(admin.getAdminId());
            log.setAction("Sent message to Alumni");
            log.setEntityType("ContactMessage");
            log.setEntityId(saved.getMessageId());
            log.setTimestamp(new Timestamp(System.currentTimeMillis()));
            activityLogService.insertLog(log);
        }

        return saved;
    }

    @Override
    public List<ContactMessage> getMessagesByAlumniId(int alumniId) {
        return messageRepo.findByAlumniId(alumniId);
    }
}
