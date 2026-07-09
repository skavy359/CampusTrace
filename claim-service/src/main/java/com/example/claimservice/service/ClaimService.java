package com.example.claimservice.service;

import com.example.claimservice.dto.ClaimDto;
import com.example.claimservice.entity.Claim;
import com.example.claimservice.exception.ResourceNotFoundException;
import com.example.claimservice.repository.ClaimRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClaimService {

    private static final Logger logger = LoggerFactory.getLogger(ClaimService.class);

    private final ClaimRepository claimRepository;
    private final RestTemplate restTemplate;

    public ClaimService(ClaimRepository claimRepository, RestTemplate restTemplate) {
        this.claimRepository = claimRepository;
        this.restTemplate = restTemplate;
    }

    public ClaimDto submitClaim(ClaimDto dto, String username) {
        Claim claim = new Claim();
        claim.setItemId(dto.getItemId());
        claim.setItemType(dto.getItemType());
        claim.setClaimedBy(username);
        claim.setDescription(dto.getDescription());
        claim.setProofOfOwnership(dto.getProofOfOwnership());
        claim.setStatus("PENDING");

        Claim saved = claimRepository.save(claim);

        sendNotification(username, "Your claim for item #" + dto.getItemId() +
                " (" + dto.getItemType() + ") has been submitted and is pending review.", "CLAIM_SUBMITTED");

        return convertToDto(saved);
    }

    public List<ClaimDto> getAllClaims() {
        return claimRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ClaimDto getClaimById(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
        return convertToDto(claim);
    }

    public List<ClaimDto> getClaimsByUser(String username) {
        return claimRepository.findByClaimedBy(username).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ClaimDto> getClaimsByStatus(String status) {
        return claimRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ClaimDto approveClaim(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));

        claim.setStatus("APPROVED");
        Claim updated = claimRepository.save(claim);

        sendNotification(claim.getClaimedBy(),
                "Your claim for item #" + claim.getItemId() + " has been APPROVED! " +
                        "Please contact the administration to collect your item.",
                "CLAIM_APPROVED");

        return convertToDto(updated);
    }

    public ClaimDto rejectClaim(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));

        claim.setStatus("REJECTED");
        Claim updated = claimRepository.save(claim);

        sendNotification(claim.getClaimedBy(),
                "Your claim for item #" + claim.getItemId() + " has been REJECTED. " +
                        "If you believe this is an error, please contact the administration.",
                "CLAIM_REJECTED");

        return convertToDto(updated);
    }

    public ClaimDto updateClaimStatus(Long id, String status) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));

        claim.setStatus(status);
        Claim updated = claimRepository.save(claim);
        return convertToDto(updated);
    }

    public void deleteClaim(Long id) {
        if (!claimRepository.existsById(id)) {
            throw new ResourceNotFoundException("Claim not found with id: " + id);
        }
        claimRepository.deleteById(id);
    }

    private void sendNotification(String username, String message, String type) {
        try {
            Map<String, String> notificationRequest = new HashMap<>();
            notificationRequest.put("userId", username);
            notificationRequest.put("message", message);
            notificationRequest.put("type", type);

            restTemplate.postForObject(
                    "http://notification-service/api/notifications",
                    notificationRequest,
                    Object.class
            );
            logger.info("Notification sent to user: {}", username);
        } catch (Exception e) {
            
            logger.error("Failed to send notification to user {}: {}", username, e.getMessage());
        }
    }

    private ClaimDto convertToDto(Claim claim) {
        ClaimDto dto = new ClaimDto();
        dto.setId(claim.getId());
        dto.setItemId(claim.getItemId());
        dto.setItemType(claim.getItemType());
        dto.setClaimedBy(claim.getClaimedBy());
        dto.setDescription(claim.getDescription());
        dto.setProofOfOwnership(claim.getProofOfOwnership());
        dto.setStatus(claim.getStatus());
        dto.setCreatedAt(claim.getCreatedAt());
        dto.setUpdatedAt(claim.getUpdatedAt());
        return dto;
    }
}
