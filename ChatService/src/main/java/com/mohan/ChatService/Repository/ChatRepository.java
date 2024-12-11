package com.mohan.ChatService.Repository;



import com.mohan.ChatService.Entity.Chat;
import com.mohan.Entity.User;  // Import the User entity from UserService
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String> {
    
    // Retrieve all chats between a sender and a receiver
    List<Chat> findBySenderAndReceiver(User sender, User receiver);
    
    // Retrieve all chats sent by a specific user
    List<Chat> findBySender(User sender);
    
    // Retrieve all chats received by a specific user
    List<Chat> findByReceiver(User receiver);

    // Optionally, you can add other methods, like retrieving chats by timestamp or message content.
}
