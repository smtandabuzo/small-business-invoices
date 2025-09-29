package com.sazimtandabuzo.smallbusinessinvoices.security;

import com.sazimtandabuzo.smallbusinessinvoices.security.user.ERole;
import com.sazimtandabuzo.smallbusinessinvoices.security.user.Role;
import com.sazimtandabuzo.smallbusinessinvoices.security.user.RoleRepository;
import com.sazimtandabuzo.smallbusinessinvoices.security.user.User;
import com.sazimtandabuzo.smallbusinessinvoices.security.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment env;

    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        if (roleRepository.count() == 0) {
            Role adminRole = new Role(ERole.ROLE_ADMIN);
            Role modRole = new Role(ERole.ROLE_MODERATOR);
            Role userRole = new Role(ERole.ROLE_USER);
            
            roleRepository.save(adminRole);
            roleRepository.save(modRole);
            roleRepository.save(userRole);
            
            System.out.println("Default roles created successfully!");
        }

        // Create default admin user if it doesn't exist
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            
            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            
            admin.setRoles(roles);
            userRepository.save(admin);
            
            System.out.println("Default admin user created successfully!");
            System.out.println("Username: admin");
            System.out.println("Password: admin123");
            System.out.println("PLEASE CHANGE THIS PASSWORD AFTER FIRST LOGIN!");
        }
    }
}
