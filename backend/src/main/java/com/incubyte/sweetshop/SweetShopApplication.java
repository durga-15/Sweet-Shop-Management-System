package com.incubyte.sweetshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SWEET SHOP APPLICATION - MAIN ENTRY POINT
 * 
 * This is the starting point of the Sweet Shop backend application.
 * It initializes and runs the entire server that handles:
 * - User registration and login (Authentication)
 * - Managing sweet products (adding, updating, deleting)
 * - Searching and buying sweets
 * - Admin functions for managing inventory
 * 
 * Think of this as the "power button" for the entire backend system.
 */
@SpringBootApplication
public class SweetShopApplication {
    // Main method - this is called when you start the application
    public static void main(String[] args) {
        // Start the Spring Boot application server
        SpringApplication.run(SweetShopApplication.class, args);
    }
}

