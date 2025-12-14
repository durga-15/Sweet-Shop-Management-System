package com.incubyte.sweetshop.repository;

import com.incubyte.sweetshop.entity.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SweetRepository extends JpaRepository<Sweet, Long> {
    List<Sweet> findByNameContainingIgnoreCase(String name);
    List<Sweet> findByCategoryIgnoreCase(String category);
    
    @Query(value = "SELECT s.* FROM sweets s WHERE " +
           "(CAST(:name AS TEXT) IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:name AS TEXT), '%'))) AND " +
           "(CAST(:category AS TEXT) IS NULL OR LOWER(s.category) = LOWER(CAST(:category AS TEXT))) AND " +
           "(CAST(:minPrice AS NUMERIC) IS NULL OR s.price >= CAST(:minPrice AS NUMERIC)) AND " +
           "(CAST(:maxPrice AS NUMERIC) IS NULL OR s.price <= CAST(:maxPrice AS NUMERIC))",
           nativeQuery = true)
    List<Sweet> search(@Param("name") String name,
                       @Param("category") String category,
                       @Param("minPrice") BigDecimal minPrice,
                       @Param("maxPrice") BigDecimal maxPrice);
}

