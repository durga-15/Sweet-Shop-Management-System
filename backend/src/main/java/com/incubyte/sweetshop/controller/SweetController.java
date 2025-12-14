package com.incubyte.sweetshop.controller;

import com.incubyte.sweetshop.dto.SweetDTO;
import com.incubyte.sweetshop.service.SweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * SWEET CONTROLLER - Manages all sweet/product API endpoints
 * 
 * This controller handles HTTP requests related to sweets:
 * - Creating new sweets (admin only)
 * - Getting all sweets or specific sweets
 * - Searching sweets by name, category, or price
 * - Updating sweet information (admin only)
 * - Deleting sweets (admin only)
 * - Purchasing sweets (customers)
 * - Restocking sweets (admin only)
 * 
 * All endpoints start with: http://localhost:8081/api/sweets
 * Example: http://localhost:8081/api/sweets/search
 */
@RestController  // This tells Spring this class handles web requests
@RequestMapping("/api/sweets")  // All sweet endpoints start with /api/sweets
@RequiredArgsConstructor  // Automatically creates constructor for dependencies
@CrossOrigin(origins = "*")  // Allows frontend to access these endpoints
public class SweetController {
    // Service that handles the actual business logic for sweets
    private final SweetService sweetService;

    /**
     * CREATE SWEET - Add a new sweet to the shop
     * Endpoint: POST /api/sweets
     * Permission: ADMIN ONLY
     * Receives: sweet details (name, category, price, quantity)
     * Returns: the created sweet with its ID
     */
    @PostMapping  // Responds to POST requests
    @PreAuthorize("hasRole('ADMIN')")  // Only admins can create sweets
    public ResponseEntity<SweetDTO> createSweet(@Valid @RequestBody SweetDTO sweetDTO) {
        SweetDTO created = sweetService.createSweet(sweetDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * GET ALL SWEETS - Retrieve all sweets available in the shop
     * Endpoint: GET /api/sweets
     * Permission: Anyone can view
     * Returns: list of all sweets
     */
    @GetMapping  // Responds to GET requests at /api/sweets
    public ResponseEntity<List<SweetDTO>> getAllSweets() {
        List<SweetDTO> sweets = sweetService.getAllSweets();
        return ResponseEntity.ok(sweets);
    }

    /**
     * SEARCH SWEETS - Find sweets by name, category, or price range
     * Endpoint: GET /api/sweets/search
     * Parameters: name (optional), category (optional), minPrice (optional), maxPrice (optional)
     * Returns: list of sweets matching the search criteria
     */
    @GetMapping("/search")  // Responds to GET requests at /api/sweets/search
    public ResponseEntity<List<SweetDTO>> searchSweets(
            @RequestParam(required = false) String name,      // Optional: filter by sweet name
            @RequestParam(required = false) String category,  // Optional: filter by category
            @RequestParam(required = false) BigDecimal minPrice,  // Optional: minimum price
            @RequestParam(required = false) BigDecimal maxPrice   // Optional: maximum price
    ) {
        List<SweetDTO> sweets = sweetService.searchSweets(name, category, minPrice, maxPrice);
        return ResponseEntity.ok(sweets);
    }

    /**
     * GET SWEET BY ID - Retrieve details of a specific sweet
     * Endpoint: GET /api/sweets/{id}
     * Parameter: id - the ID of the sweet to retrieve
     * Returns: the sweet details or error if not found
     */
    @GetMapping("/{id}")  // Responds to GET requests at /api/sweets/{id}
    public ResponseEntity<SweetDTO> getSweetById(@PathVariable Long id) {  // {id} is the sweet's ID
        SweetDTO sweet = sweetService.getSweetById(id);
        return ResponseEntity.ok(sweet);
    }

    /**
     * UPDATE SWEET - Modify sweet information
     * Endpoint: PUT /api/sweets/{id}
     * Permission: ADMIN ONLY
     * Parameter: id - the ID of the sweet to update
     * Receives: updated sweet details
     * Returns: the updated sweet
     */
    @PutMapping("/{id}")  // Responds to PUT requests at /api/sweets/{id}
    @PreAuthorize("hasRole('ADMIN')")  // Only admins can update sweets
    public ResponseEntity<SweetDTO> updateSweet(@PathVariable Long id, @Valid @RequestBody SweetDTO sweetDTO) {
        SweetDTO updated = sweetService.updateSweet(id, sweetDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE SWEET - Remove a sweet from the shop
     * Endpoint: DELETE /api/sweets/{id}
     * Permission: ADMIN ONLY
     * Parameter: id - the ID of the sweet to delete
     */
    @DeleteMapping("/{id}")  // Responds to DELETE requests at /api/sweets/{id}
    @PreAuthorize("hasRole('ADMIN')")  // Only admins can delete sweets
    public ResponseEntity<Void> deleteSweet(@PathVariable Long id) {
        sweetService.deleteSweet(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PURCHASE SWEET - Buy a sweet (reduces stock quantity)
     * Endpoint: POST /api/sweets/{id}/purchase
     * Parameter: id - the ID of the sweet to purchase
     * Receives: quantity - how many to purchase
     * Returns: the updated sweet with new stock level
     */
    @PostMapping("/{id}/purchase")  // Responds to POST requests at /api/sweets/{id}/purchase
    public ResponseEntity<SweetDTO> purchaseSweet(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");  // Get quantity from request
        if (quantity == null || quantity <= 0) {
            return ResponseEntity.badRequest().build();
        }
        SweetDTO updated = sweetService.purchaseSweet(id, quantity);
        return ResponseEntity.ok(updated);
    }

    /**
     * RESTOCK SWEET - Add more inventory (increases stock quantity)
     * Endpoint: POST /api/sweets/{id}/restock
     * Permission: ADMIN ONLY
     * Parameter: id - the ID of the sweet to restock
     * Receives: quantity - how many items to add
     * Returns: the updated sweet with new stock level
     */
    @PostMapping("/{id}/restock")  // Responds to POST requests at /api/sweets/{id}/restock
    @PreAuthorize("hasRole('ADMIN')")  // Only admins can restock
    public ResponseEntity<SweetDTO> restockSweet(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");  // Get quantity from request
        if (quantity == null || quantity <= 0) {
            return ResponseEntity.badRequest().build();
        }
        SweetDTO updated = sweetService.restockSweet(id, quantity);
        return ResponseEntity.ok(updated);
    }
}

