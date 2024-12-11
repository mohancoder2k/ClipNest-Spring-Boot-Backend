package com.mohan.ChatService.Service;

import com.mohan.ChatService.Entity.Chat;
import com.mohan.ChatService.JWT.JwtUtil;
import com.mohan.ChatService.Repository.ChatRepository;
import com.mohan.Entity.User;  // Import the User entity from UserService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    
    @Autowired
    private JwtUtil jwtUtil; // Inject JwtUtil directly


    // Method to send a new chat message
    public Chat sendMessage(User sender, User receiver, String message) {
        // Create a new Chat object with the sender, receiver, and message
        Chat chat = new Chat(sender, receiver, message);
        return chatRepository.save(chat);  // Save the chat message to the database
    }

    // Method to get all chats between two users
    public List<Chat> getChatsBetweenUsers(User sender, User receiver) {
        return chatRepository.findBySenderAndReceiver(sender, receiver);
    }

    // Method to get all chats involving a specific user (both sent and received)
    public List<Chat> getChatsForUser(User user) {
        List<Chat> sentChats = chatRepository.findBySender(user);  // Get chats sent by the user
        List<Chat> receivedChats = chatRepository.findByReceiver(user);  // Get chats received by the user
        sentChats.addAll(receivedChats);  // Combine both lists
        return sentChats;  // Return the combined list of chats
    }
}
