package com.mohan.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@Document(collection = "videos") // Specifies this is a MongoDB collection
public class Video {
    @Id
    private String id; // MongoDB uses a String ID, typically in ObjectId format

    private String title;

    private byte[] videoData; // Binary data type for storing video

    @DBRef
    private User user; // Reference to a User document in MongoDB

    private LocalDateTime createdAt = LocalDateTime.now();
}
