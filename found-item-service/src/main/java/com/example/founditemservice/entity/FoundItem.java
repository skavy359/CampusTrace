package com.example.founditemservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "found_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    @Column(length = 500)
    private String description;

    private String category;

    private String locationFound;

    private LocalDate dateFound;

    @Column(nullable = false)
    private String foundBy;

    private String contactInfo;

    @Column(nullable = false)
    private boolean claimed;

    @Column(nullable = false)
    private String status; 

    private String imageUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "FOUND";
        }
        this.claimed = false;
    }
}
