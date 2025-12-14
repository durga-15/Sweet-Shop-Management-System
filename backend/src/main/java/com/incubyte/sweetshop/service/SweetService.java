package com.incubyte.sweetshop.service;

import com.incubyte.sweetshop.dto.SweetDTO;
import com.incubyte.sweetshop.entity.Sweet;
import com.incubyte.sweetshop.repository.SweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SWEET SERVICE - Manages all sweet/product operations
 * 
 * This service handles:
 * - Creating new sweets (admin only)
 * - Retrieving sweets (list all or search)
 * - Updating sweet information
 * - Deleting sweets
 * - Searching sweets by name, category, or price
 * - Purchasing sweets (reduce quantity)
 * - Restocking sweets (increase quantity)
 * 
 * It converts between database entities and data transfer objects (DTOs)
 * which are sent to the frontend.
 */
@Service  // This tells Spring this class contains business logic
@RequiredArgsConstructor  // Automatically injects dependencies
public class SweetService {
    // Database access for sweet records
    private final SweetRepository sweetRepository;

    /**
     * CREATE SWEET - Add a new sweet to the database
     * 
     * Receives a SweetDTO and saves it to the database
     * Returns the created sweet with its auto-generated ID
     */
    public SweetDTO createSweet(SweetDTO sweetDTO) {
        // Convert DTO (data from frontend) to database entity
        Sweet sweet = convertToEntity(sweetDTO);
        // Save to database
        sweet = sweetRepository.save(sweet);
        // Convert back to DTO and return
        return convertToDTO(sweet);
    }

    /**
     * GET ALL SWEETS - Retrieve every sweet in the database
     * 
     * Returns a list of all available sweets
     */
    public List<SweetDTO> getAllSweets() {
        // Get all sweets from database
        return sweetRepository.findAll().stream()
                // Convert each database entity to DTO for frontend
                .map(this::convertToDTO)
                // Collect results into a list
                .collect(Collectors.toList());
    }

    /**
     * GET SWEET BY ID - Find a specific sweet by its ID
     * 
     * Receives: sweet ID
     * Returns: the specific sweet details
     * Throws: error if sweet not found
     */
    public SweetDTO getSweetById(Long id) {
        // Find sweet in database, throw error if not found
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sweet not found with id: " + id));
        // Convert to DTO and return
        return convertToDTO(sweet);
    }

    /**
     * UPDATE SWEET - Modify a sweet's information
     * 
     * Receives: sweet ID and new sweet details
     * Updates: name, category, price, quantity
     * Returns: the updated sweet
     */
    public SweetDTO updateSweet(Long id, SweetDTO sweetDTO) {
        // Find the sweet in database, throw error if not found
        Sweet existingSweet = sweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sweet not found with id: " + id));

        // Update all fields with new values
        existingSweet.setName(sweetDTO.getName());
        existingSweet.setCategory(sweetDTO.getCategory());
        existingSweet.setPrice(sweetDTO.getPrice());
        existingSweet.setQuantity(sweetDTO.getQuantity());

        // Save updated sweet to database
        existingSweet = sweetRepository.save(existingSweet);
        // Convert to DTO and return
        return convertToDTO(existingSweet);
    }

    /**
     * DELETE SWEET - Remove a sweet from the database
     * 
     * Receives: sweet ID
     * Throws: error if sweet not found
     */
    public void deleteSweet(Long id) {
        // Check if sweet exists in database
        if (!sweetRepository.existsById(id)) {
            throw new RuntimeException("Sweet not found with id: " + id);
        }
        // Delete the sweet from database
        sweetRepository.deleteById(id);
    }

    /**
     * SEARCH SWEETS - Find sweets matching certain criteria
     * 
     * Receives: optional filters (name, category, minPrice, maxPrice)
     * Returns: list of sweets matching the search criteria
     */
    public List<SweetDTO> searchSweets(String name, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        // Query database with search filters
        List<Sweet> sweets = sweetRepository.search(name, category, minPrice, maxPrice);
        // Convert each result to DTO
        return sweets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * PURCHASE SWEET - Reduce stock when a customer buys a sweet
     * 
     * Receives: sweet ID and quantity to purchase
     * Updates: reduces the quantity in stock
     * Throws: error if not enough stock
     * 
     * @Transactional ensures the operation completes fully or not at all
     */
    @Transactional  // Ensures this operation is all-or-nothing (atomic)
    public SweetDTO purchaseSweet(Long id, Integer quantity) {
        // Find sweet in database
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sweet not found with id: " + id));

        // Check if there's enough stock available
        if (sweet.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient quantity available. Available: " + sweet.getQuantity());
        }

        // Reduce quantity by the amount purchased
        sweet.setQuantity(sweet.getQuantity() - quantity);
        // Save updated stock level to database
        sweet = sweetRepository.save(sweet);
        // Return updated sweet
        return convertToDTO(sweet);
    }

    /**
     * RESTOCK SWEET - Increase stock when admin restocks items
     * 
     * Receives: sweet ID and quantity to add
     * Updates: increases the quantity in stock
     * 
     * @Transactional ensures the operation completes fully or not at all
     */
    @Transactional  // Ensures this operation is all-or-nothing (atomic)
    public SweetDTO restockSweet(Long id, Integer quantity) {
        // Find sweet in database
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sweet not found with id: " + id));

        // Add the new quantity to existing stock
        sweet.setQuantity(sweet.getQuantity() + quantity);
        // Save updated stock level to database
        sweet = sweetRepository.save(sweet);
        // Return updated sweet
        return convertToDTO(sweet);
    }

    /**
     * CONVERT TO DTO - Transforms database entity to frontend data format
     * 
     * DTO = Data Transfer Object (simplified format for sending to frontend)
     * Helps separate what we store in database from what we send to users
     */
    private SweetDTO convertToDTO(Sweet sweet) {
        SweetDTO dto = new SweetDTO();
        dto.setId(sweet.getId());
        dto.setName(sweet.getName());
        dto.setCategory(sweet.getCategory());
        dto.setPrice(sweet.getPrice());
        dto.setQuantity(sweet.getQuantity());
        return dto;
    }

    /**
     * CONVERT TO ENTITY - Transforms frontend data to database entity format
     * 
     * Entity = Database record format (how data is stored in the database)
     * Helps separate what we receive from frontend from how we store it
     */
    private Sweet convertToEntity(SweetDTO dto) {
        return Sweet.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .build();
    }
}
    