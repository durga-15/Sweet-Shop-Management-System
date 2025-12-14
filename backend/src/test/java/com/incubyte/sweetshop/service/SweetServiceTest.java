package com.incubyte.sweetshop.service;

import com.incubyte.sweetshop.dto.SweetDTO;
import com.incubyte.sweetshop.entity.Sweet;
import com.incubyte.sweetshop.repository.SweetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test suite for SweetService class.
 * Tests CRUD operations, business logic, and edge cases for sweet management functionality.
 * Uses Mockito for dependency injection and mock repository interactions.
 */
@ExtendWith(MockitoExtension.class)
class SweetServiceTest {

    @Mock
    private SweetRepository sweetRepository;

    @InjectMocks
    private SweetService sweetService;

    private SweetDTO sweetDTO;
    private Sweet sweet;

    /**
     * Setup method executed before each test.
     * Initializes test data (SweetDTO and Sweet entity) with sample values.
     * This ensures a clean state for each test case and reduces code duplication.
     */
    @BeforeEach
    void setUp() {
        sweetDTO = new SweetDTO();
        sweetDTO.setName("Gulab Jamun");
        sweetDTO.setCategory("Indian");
        sweetDTO.setPrice(new BigDecimal("50.00"));
        sweetDTO.setQuantity(100);

        sweet = Sweet.builder()
                .id(1L)
                .name("Gulab Jamun")
                .category("Indian")
                .price(new BigDecimal("50.00"))
                .quantity(100)
                .build();
    }

    /**
     * Test case: Create a new sweet successfully
     * Given: Valid SweetDTO with all required fields
     * When: createSweet() is called
     * Then: Sweet is created and returned with correct data
     * 
     * Verifies: Repository save method is called exactly once
     */
    @Test
    void testCreateSweet_Success() {
        when(sweetRepository.save(any(Sweet.class))).thenReturn(sweet);

        SweetDTO result = sweetService.createSweet(sweetDTO);

        assertNotNull(result);
        assertEquals("Gulab Jamun", result.getName());
        assertEquals(new BigDecimal("50.00"), result.getPrice());
        verify(sweetRepository).save(any(Sweet.class));
    }

    /**
     * Test case: Retrieve all sweets from the database
     * Given: Repository contains one sweet item
     * When: getAllSweets() is called
     * Then: List with one sweet is returned
     * 
     * Verifies: Correct data mapping from entity to DTO
     */
    @Test
    void testGetAllSweets() {
        when(sweetRepository.findAll()).thenReturn(Arrays.asList(sweet));

        List<SweetDTO> result = sweetService.getAllSweets();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Gulab Jamun", result.get(0).getName());
    }

     /**
     * Test case: Get a sweet by ID - happy path
     * Given: Sweet with ID 1 exists in repository
     * When: getSweetById(1L) is called
     * Then: SweetDTO is returned with correct data
     * 
     * Verifies: Correct sweet is retrieved and mapped properly
     */
    @Test
    void testGetSweetById_Success() {
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(sweet));

        SweetDTO result = sweetService.getSweetById(1L);

        assertNotNull(result);
        assertEquals("Gulab Jamun", result.getName());
    }

    /**
     * Test case: Get a sweet by non-existent ID
     * Given: Sweet with ID 1 does not exist in repository
     * When: getSweetById(1L) is called
     * Then: RuntimeException is thrown
     * 
     * Verifies: Proper error handling for missing resources
     */
    @Test
    void testGetSweetById_NotFound() {
        when(sweetRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> sweetService.getSweetById(1L));
    }

    /**
     * Test case: Update an existing sweet successfully
     * Given: Sweet with ID 1 exists and update DTO has new values
     * When: updateSweet(1L, updateDTO) is called
     * Then: Sweet is updated and saved to repository
     * 
     * Verifies: Repository save and find methods are called appropriately
     */
    @Test
    void testUpdateSweet_Success() {
        SweetDTO updateDTO = new SweetDTO();
        updateDTO.setName("Updated Gulab Jamun");
        updateDTO.setCategory("Indian");
        updateDTO.setPrice(new BigDecimal("55.00"));
        updateDTO.setQuantity(150);

        when(sweetRepository.findById(1L)).thenReturn(Optional.of(sweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(sweet);

        SweetDTO result = sweetService.updateSweet(1L, updateDTO);

        assertNotNull(result);
        verify(sweetRepository).save(any(Sweet.class));
    }

    /**
     * Test case: Delete a sweet successfully
     * Given: Sweet with ID 1 exists in repository
     * When: deleteSweet(1L) is called
     * Then: Sweet is deleted from database
     * 
     * Verifies: Repository deleteById is called exactly once
     */
    @Test
    void testDeleteSweet_Success() {
        when(sweetRepository.existsById(1L)).thenReturn(true);
        doNothing().when(sweetRepository).deleteById(1L);

        sweetService.deleteSweet(1L);

        verify(sweetRepository).deleteById(1L);
    }

    /**
     * Test case: Delete a non-existent sweet
     * Given: Sweet with ID 1 does not exist in repository
     * When: deleteSweet(1L) is called
     * Then: RuntimeException is thrown and no deletion occurs
     * 
     * Verifies: Proper validation and error handling; deleteById never called
     */
    @Test
    void testDeleteSweet_NotFound() {
        when(sweetRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> sweetService.deleteSweet(1L));
        verify(sweetRepository, never()).deleteById(anyLong());
    }

    /**
     * Test case: Search sweets with filter criteria
     * Given: Repository contains a sweet matching search criteria (name and category)
     * When: searchSweets("Gulab", "Indian", null, null) is called
     * Then: List with matching sweet is returned
     * 
     * Verifies: Correct filtering and data mapping
     */
    @Test
    void testSearchSweets() {
        when(sweetRepository.search("Gulab", "Indian", null, null))
                .thenReturn(Arrays.asList(sweet));

        List<SweetDTO> result = sweetService.searchSweets("Gulab", "Indian", null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Test case: Purchase a sweet with sufficient quantity
     * Given: Sweet with ID 1 has quantity 100 and request is for 10 units
     * When: purchaseSweet(1L, 10) is called
     * Then: Quantity is reduced by 10 (100 -> 90) and saved
     * 
     * Verifies: Correct inventory deduction and persistence
     */
    @Test
    void testPurchaseSweet_Success() {
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(sweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(sweet);

        SweetDTO result = sweetService.purchaseSweet(1L, 10);

        assertNotNull(result);
        assertEquals(90, result.getQuantity());
        verify(sweetRepository).save(any(Sweet.class));
    }

    /**
     * Test case: Purchase a sweet with insufficient quantity
     * Given: Sweet with ID 1 has quantity 100 but request is for 150 units
     * When: purchaseSweet(1L, 150) is called
     * Then: RuntimeException is thrown and no save operation occurs
     * 
     * Verifies: Inventory validation and prevention of overselling
     */
    @Test
    void testPurchaseSweet_InsufficientQuantity() {
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(sweet));

        assertThrows(RuntimeException.class, () -> sweetService.purchaseSweet(1L, 150));
        verify(sweetRepository, never()).save(any(Sweet.class));
    }

    /**
     * Test case: Restock a sweet successfully
     * Given: Sweet with ID 1 has quantity 100 and restock quantity is 50
     * When: restockSweet(1L, 50) is called
     * Then: Quantity is increased by 50 (100 -> 150) and saved
     * 
     * Verifies: Correct inventory replenishment and persistence
     */
    @Test
    void testRestockSweet_Success() {
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(sweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(sweet);

        SweetDTO result = sweetService.restockSweet(1L, 50);

        assertNotNull(result);
        assertEquals(150, result.getQuantity());
        verify(sweetRepository).save(any(Sweet.class));
    }
}

