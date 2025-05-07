package com.ums.model;

import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
public class ContactMessage {
    @Id
    private String messageId; // VARCHAR based

    private int alumniId;
    private String subject;

    @Lob
    private String message;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.sql.Timestamp sentAt;
    

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = new Timestamp(System.currentTimeMillis());
        }
    }
    
    // Constructors
    public ContactMessage() {}

    public ContactMessage(String messageId, int alumniId, String subject, String message) {
        this.messageId = messageId;
        this.alumniId = alumniId;
        this.subject = subject;
        this.message = message;
    }

    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }
  
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getAlumniId() {
        return alumniId;
    }
 
    public void setAlumniId(int alumniId) {
        this.alumniId = alumniId;
    }

    public String getSubject() {
        return subject;
    }
 
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }
 
    public void setMessage(String message) {
        this.message = message;
    }

    public java.sql.Timestamp getSentAt() {
        return sentAt;
    }
  
    public void setSentAt(java.sql.Timestamp sentAt) {
        this.sentAt = sentAt;
    }
}
