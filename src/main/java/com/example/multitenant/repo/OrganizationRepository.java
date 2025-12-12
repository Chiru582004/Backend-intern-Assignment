package com.example.multitenant.repo;

import com.example.multitenant.model.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface OrganizationRepository extends MongoRepository<Organization,String> {
    Optional<Organization> findByOrganizationName(String organizationName);
    boolean existsByOrganizationName(String organizationName);
}
