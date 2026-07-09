package com.example.claimservice.repository;

import com.example.claimservice.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByClaimedBy(String claimedBy);

    List<Claim> findByStatus(String status);

    List<Claim> findByItemIdAndItemType(Long itemId, String itemType);
}
