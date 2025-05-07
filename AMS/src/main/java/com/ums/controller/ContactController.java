package com.ums.controller;

import com.ums.model.ContactMessage;
import com.ums.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/send")
    public ContactMessage sendMessage(@RequestBody ContactMessage message) {
        return contactService.sendMessageToAdmin(message);
    }
}
