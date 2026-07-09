package com.example.claimservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDto {

    private Long id;

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotBlank(message = "Item type is required (LOST or FOUND)")
    private String itemType;

    private String claimedBy;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 500, message = "Proof of ownership must not exceed 500 characters")
    private String proofOfOwnership;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
