package com.example.multitenant.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Getter
@Setter
@Document(collection = "admins")
public class AdminUser {
    @Id private String id;
    private String email;
    private String passwordHash;
    private String organizationId; // ref to Organization id
    private Instant createdAt = Instant.now();
    // getters/setters
}
