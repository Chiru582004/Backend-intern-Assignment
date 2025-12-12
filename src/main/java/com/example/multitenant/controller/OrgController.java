package com.example.multitenant.controller;


import com.example.multitenant.model.Organization;
import com.example.multitenant.service.OrganizationService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.Map;

@RestController
@RequestMapping("/org")
public class OrgController {
    private final OrganizationService orgService;
    public OrgController(OrganizationService orgService){ this.orgService = orgService; }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Map<String,String> body){
        String name = body.get("organization_name"), email = body.get("email"), pwd = body.get("password");
        if (name==null||email==null||pwd==null) return ResponseEntity.badRequest().body("missing");
        Organization org = orgService.createOrganization(name,email,pwd);
        return ResponseEntity.ok(Map.of("id",org.getId(),"name",org.getOrganizationName(),"collection",org.getCollectionName()));
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam String organization_name) {
        return orgService.getByName(organization_name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(null));
    }


    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Map<String,String> body){
        String current = body.get("organization_name");
        String newName = body.getOrDefault("new_organization_name", current);
        String newEmail = body.get("email");
        String newPwd = body.get("password");
        var updated = orgService.updateOrganization(current, newName, newEmail, newPwd);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String,String> body, Authentication auth){
        String orgName = body.get("organization_name");
        if (auth==null) return ResponseEntity.status(401).build();
        String adminId = (String) auth.getPrincipal();
        try {
            orgService.deleteOrganization(orgName, adminId);
            return ResponseEntity.ok(Map.of("deleted",orgName));
        } catch (SecurityException se) { return ResponseEntity.status(403).body("forbidden"); }
        catch (Exception ex) { return ResponseEntity.status(500).body("error"); }
    }
}
