package com.example.founditemservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoundItemDto {

    private Long id;

    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name must not exceed 100 characters")
    private String itemName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String category;

    private String locationFound;

    private LocalDate dateFound;

    private String foundBy;

    private String contactInfo;

    private Boolean claimed;

    private String status;

    private String imageUrl;
}
