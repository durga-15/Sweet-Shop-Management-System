package com.incubyte.sweetshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * SWEET ENTITY - Represents a sweet/candy product in the database
 * 
 * Each sweet in the shop has the following information:
 * - id: Unique identifier (auto-generated)
 * - name: Name of the sweet (e.g., "Chocolate Cake")
 * - category: Type of sweet (e.g., "Cake", "Candy", "Pastry")
 * - price: Cost of the sweet
 * - quantity: How many items are in stock
 * 
 * This class maps directly to the 'sweets' table in the database.
 * Each instance represents one row in that table.
 */
@Entity  // This marks it as a database table
@Table(name = "sweets")  // The name of the database table
@Data  // Auto-generates getters, setters, toString
@NoArgsConstructor  // Creates empty constructor
@AllArgsConstructor  // Creates constructor with all fields
@Builder  // Allows easy creation: Sweet.builder().name("...").build()
public class Sweet {
    // Primary key - unique identifier for each sweet
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment ID
    private Long id;

    // Sweet name - must be unique and not empty
    @NotBlank(message = "Name is required")  // Cannot be blank
    @Column(nullable = false, unique = true)  // Must be unique in database
    private String name;

    // Sweet category - cannot be empty (e.g., "Cake", "Candy", "Pastry")
    @NotBlank(message = "Category is required")  // Cannot be blank
    @Column(nullable = false)  // Cannot be null in database
    private String category;

    // Sweet price - must be greater than 0
    @NotNull(message = "Price is required")  // Cannot be null
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")  // Minimum price validation
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false)
    private Integer quantity;
}

