package com.mohan.ChatService.Entity;

 // Import the User entity from the UserService
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mohan.Entity.User;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@Document(collection = "chats")
public class Chat {
    @Id
    private String id;
    
    private User sender;  // Reference to the sender User
    private User receiver;  // Reference to the receiver User
    
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();

    // Constructors, getters, setters, etc.

    public Chat() {}

    public Chat(User sender, User receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    // Getters and setters for sender, receiver, message, and timestamp
}