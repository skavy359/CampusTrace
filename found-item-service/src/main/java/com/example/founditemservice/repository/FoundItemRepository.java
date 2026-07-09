package com.example.founditemservice.repository;

import com.example.founditemservice.entity.FoundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Long> {

    List<FoundItem> findByFoundBy(String foundBy);

    List<FoundItem> findByStatus(String status);

    List<FoundItem> findByClaimed(boolean claimed);

    @Query("SELECT f FROM FoundItem f WHERE LOWER(f.itemName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(f.category) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(f.locationFound) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<FoundItem> searchByKeyword(@Param("keyword") String keyword);
}
