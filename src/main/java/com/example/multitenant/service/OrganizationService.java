package com.example.multitenant.service;

import com.example.multitenant.model.*;
import com.example.multitenant.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;

@Service
public class OrganizationService {
    private final OrganizationRepository orgRepo;
    private final AdminRepository adminRepo;
    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder encoder;

    public OrganizationService(OrganizationRepository orgRepo, AdminRepository adminRepo,
                               MongoTemplate mongoTemplate, BCryptPasswordEncoder encoder) {
        this.orgRepo = orgRepo; this.adminRepo = adminRepo; this.mongoTemplate = mongoTemplate; this.encoder = encoder;
    }

    public Organization createOrganization(String orgName, String adminEmail, String adminPassword) {
        if (orgRepo.existsByOrganizationName(orgName)) throw new IllegalArgumentException("Org exists");
        String collectionName = "org_" + orgName.toLowerCase().replaceAll("[^a-z0-9_]", "_");
        // create collection if not exists
        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
        }
        // create admin
        AdminUser admin = new AdminUser();
        admin.setEmail(adminEmail);
        admin.setPasswordHash(encoder.encode(adminPassword));
        AdminUser savedAdmin = adminRepo.save(admin);

        Organization org = new Organization();
        org.setOrganizationName(orgName);
        org.setCollectionName(collectionName);
        org.setAdminId(savedAdmin.getId());
        Organization savedOrg = orgRepo.save(org);

        // link admin to org
        savedAdmin.setOrganizationId(savedOrg.getId());
        adminRepo.save(savedAdmin);

        return savedOrg;
    }

    public Optional<Organization> getByName(String name) { return orgRepo.findByOrganizationName(name); }

    public void deleteOrganization(String orgName, String requestingAdminId) {
        Organization org = orgRepo.findByOrganizationName(orgName).orElseThrow();
        // check ownership
        if (!org.getAdminId().equals(requestingAdminId)) throw new SecurityException("Not owner");
        // drop collection
        if (mongoTemplate.collectionExists(org.getCollectionName())) {
            mongoTemplate.dropCollection(org.getCollectionName());
        }
        // delete admin
        adminRepo.deleteById(org.getAdminId());
        // delete org metadata
        orgRepo.deleteById(org.getId());
    }

    // Update: create new collection (if orgName changed), sync data naive approach
    public Organization updateOrganization(String currentName, String newName, String newEmail, String newPassword) {
        Organization org = orgRepo.findByOrganizationName(currentName).orElseThrow();
        if (!currentName.equals(newName) && orgRepo.existsByOrganizationName(newName)) {
            throw new IllegalArgumentException("new org name exists");
        }
        String oldCollection = org.getCollectionName();
        String newCollection = "org_" + newName.toLowerCase().replaceAll("[^a-z0-9_]", "_");

        // create new collection
        if (!mongoTemplate.collectionExists(newCollection)) {
            mongoTemplate.createCollection(newCollection);
            // naive sync: copy all docs
            var docs = mongoTemplate.findAll(org.getClass(), oldCollection); // better to use Document and raw copy
            if (!docs.isEmpty()) {
                mongoTemplate.insert(docs, newCollection);
            }
            // optionally drop old collection or keep as backup
        }

        org.setOrganizationName(newName);
        org.setCollectionName(newCollection);
        orgRepo.save(org);

        // update admin if provided
        if (newEmail != null || newPassword != null) {
            AdminUser admin = adminRepo.findById(org.getAdminId()).orElseThrow();
            if (newEmail != null) admin.setEmail(newEmail);
            if (newPassword != null) admin.setPasswordHash(encoder.encode(newPassword));
            adminRepo.save(admin);
        }
        return org;
    }
}
