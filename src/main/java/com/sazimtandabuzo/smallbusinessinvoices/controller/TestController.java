package com.sazimtandabuzo.smallbusinessinvoices.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Test endpoints for JWT authentication")
public class TestController {
    
    @GetMapping("/all")
    @Operation(summary = "Public access endpoint")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @Operation(
        summary = "User access endpoint",
        description = "This endpoint requires user authentication",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @Operation(
        summary = "Moderator access endpoint",
        description = "This endpoint requires moderator or admin role",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public String moderatorAccess() {
        return "Moderator Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin access endpoint",
        description = "This endpoint requires admin role",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public String adminAccess() {
        return "Admin Board.";
    }
}
