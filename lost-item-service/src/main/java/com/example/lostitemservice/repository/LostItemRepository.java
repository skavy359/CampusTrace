package com.example.lostitemservice.repository;

import com.example.lostitemservice.entity.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long> {

    List<LostItem> findByReportedBy(String reportedBy);

    List<LostItem> findByStatus(String status);

    List<LostItem> findByCategory(String category);

    @Query("SELECT l FROM LostItem l WHERE LOWER(l.itemName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(l.category) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(l.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<LostItem> searchByKeyword(@Param("keyword") String keyword);
}
