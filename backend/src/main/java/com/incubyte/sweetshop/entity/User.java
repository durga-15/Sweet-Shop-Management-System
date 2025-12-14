package com.incubyte.sweetshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * USER ENTITY - Represents a user/customer account in the database
 * 
 * Each user has:
 * - id: Unique identifier (auto-generated)
 * - username: Unique login name (e.g., "john_doe")
 * - email: User's email address
 * - password: Encrypted password (never stored in plain text)
 * - role: Either 'USER' (customer) or 'ADMIN' (shop manager)
 * 
 * This class maps to the 'users' table in the database.
 * When a user registers or admin is created, a new row is added here.
 */
@Entity  // This marks it as a database table
@Table(name = "users")  // The name of the database table
@Data  // Auto-generates getters, setters, toString
@NoArgsConstructor  // Creates empty constructor
@AllArgsConstructor  // Creates constructor with all fields
@Builder  // Allows easy creation: User.builder().username("...").build()
public class User {
    // Primary key - unique identifier for each user
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment ID
    private Long id;

    // Username - unique login name (e.g., "john_doe")
    @Column(unique = true, nullable = false)  // Must be unique, cannot be null
    private String username;

    // Password - encrypted (hashed) for security, never stored in plain text
    @Column(nullable = false)  // Cannot be null
    private String password;

    // Email address - unique for each user
    @Column(unique = true, nullable = false)  // Must be unique, cannot be null
    private String email;

    // Role - determines what permissions this user has
    @Enumerated(EnumType.STRING)  // Store as text: "USER" or "ADMIN"
    @Column(nullable = false)  // Cannot be null
    @Builder.Default
    private Role role = Role.USER;  // Default: new users are regular customers

    /**
     * ROLE ENUM - Defines the two types of users in the system
     * 
     * USER: Regular customer
     *   - Can view sweets
     *   - Can purchase sweets
     *   - Cannot add or modify sweets
     * 
     * ADMIN: Shop manager
     *   - Can do everything a USER can
     *   - Can add new sweets
     *   - Can update sweet information
     *   - Can delete sweets
     *   - Can restock inventory
     */
    public enum Role {
        USER,   // Regular customer
        ADMIN   // Shop manager/administrator
    }
}

