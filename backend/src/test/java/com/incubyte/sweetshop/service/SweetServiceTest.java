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


