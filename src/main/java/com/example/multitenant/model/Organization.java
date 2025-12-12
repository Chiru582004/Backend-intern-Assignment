package com.example.multitenant.model;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Getter
@Setter
@Document(collection = "organizations")
public class Organization {
    @Id private String id;
    private String organizationName;     // unique
    private String collectionName;       // org_<organizationName>
    private String adminId;              // reference to AdminUser.id
    private Instant createdAt = Instant.now();
    // getters/setters (omitted for brevity) or use Lombok
}