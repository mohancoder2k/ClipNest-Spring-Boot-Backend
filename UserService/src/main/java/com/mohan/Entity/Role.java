package com.mohan.Entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "roles") // MongoDB annotation for collections
public class Role {
    @Id
    private String id; // MongoDB ID (String by default for ObjectId)

    @Indexed(unique = true) // Ensures 'name' is unique in MongoDB
    private String name; // Role name

    // Constructor for creating new Role instances
    public Role(String name) {
        this.name = name;
    }

    // Default constructor required by MongoDB
    public Role() {
    }
}
