package com.example.multitenant.repo;

import com.example.multitenant.model.AdminUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface AdminRepository extends MongoRepository<AdminUser,String> {
    Optional<AdminUser> findByEmail(String email);
}
