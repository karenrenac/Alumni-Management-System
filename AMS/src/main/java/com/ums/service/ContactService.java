package com.ums.service;

import java.util.List;

import com.ums.model.ContactMessage;

public interface ContactService {
    ContactMessage sendMessageToAdmin(ContactMessage message);
    List<ContactMessage> getMessagesByAlumniId(int alumniId);
}
