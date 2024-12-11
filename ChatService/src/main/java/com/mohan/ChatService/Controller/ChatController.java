package com.mohan.ChatService.Controller;

import com.mohan.ChatService.Entity.Chat;
import com.mohan.ChatService.Service.ChatService;
import com.mohan.Entity.User;  // Import the User entity
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Endpoint to send a message from one user to another
    @PostMapping("/send")
    public Chat sendMessage(@RequestBody ChatRequest chatRequest) {
        User sender = chatRequest.getSender();  // Get the sender user
        User receiver = chatRequest.getReceiver();  // Get the receiver user
        String message = chatRequest.getMessage();  // Get the chat message
        return chatService.sendMessage(sender, receiver, message);  // Call the service to send the message
    }

    // Endpoint to get all chats between two users
    @GetMapping("/between")
    public List<Chat> getChatsBetweenUsers(@RequestParam("senderId") String senderId, @RequestParam("receiverId") String receiverId) {
        User sender = new User();  // Create a sender User object (could be fetched from DB)
        sender.setId(senderId);
        
        User receiver = new User();  // Create a receiver User object (could be fetched from DB)
        receiver.setId(receiverId);
        
        return chatService.getChatsBetweenUsers(sender, receiver);  // Get chats between the two users
    }

    // Endpoint to get all chats for a specific user (both sent and received)
    @GetMapping("/user")
    public List<Chat> getChatsForUser(@RequestParam("userId") String userId) {
        User user = new User();  // Create a user object (could be fetched from DB)
        user.setId(userId);
        return chatService.getChatsForUser(user);  // Get all chats for the user
    }
}

// Request DTO to capture the data sent by the client for sending a message
class ChatRequest {
    private User sender;
    private User receiver;
    private String message;

    // Getters and setters
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
