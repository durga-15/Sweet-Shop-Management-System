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

    