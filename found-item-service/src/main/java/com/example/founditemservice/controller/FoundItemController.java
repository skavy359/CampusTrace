package com.example.founditemservice.controller;

import com.example.founditemservice.dto.FoundItemDto;
import com.example.founditemservice.service.FoundItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/found-items")
public class FoundItemController {

    private final FoundItemService foundItemService;

    public FoundItemController(FoundItemService foundItemService) {
        this.foundItemService = foundItemService;
    }

    @GetMapping
    public ResponseEntity<List<FoundItemDto>> getAllFoundItems() {
        return ResponseEntity.ok(foundItemService.getAllFoundItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoundItemDto> getFoundItemById(@PathVariable Long id) {
        return ResponseEntity.ok(foundItemService.getFoundItemById(id));
    }

    @GetMapping("/my-items")
    public ResponseEntity<List<FoundItemDto>> getMyFoundItems(Authentication authentication) {
        return ResponseEntity.ok(foundItemService.getFoundItemsByUser(authentication.getName()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoundItemDto>> searchFoundItems(@RequestParam String keyword) {
        return ResponseEntity.ok(foundItemService.searchFoundItems(keyword));
    }

    @PostMapping
    public ResponseEntity<FoundItemDto> addFoundItem(@Valid @RequestBody FoundItemDto dto,
                                                      Authentication authentication) {
        FoundItemDto created = foundItemService.addFoundItem(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoundItemDto> updateFoundItem(@PathVariable Long id,
                                                         @Valid @RequestBody FoundItemDto dto) {
        return ResponseEntity.ok(foundItemService.updateFoundItem(id, dto));
    }

    @PutMapping("/{id}/claim")
    public ResponseEntity<FoundItemDto> markAsClaimed(@PathVariable Long id) {
        return ResponseEntity.ok(foundItemService.markAsClaimed(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoundItem(@PathVariable Long id) {
        foundItemService.deleteFoundItem(id);
        return ResponseEntity.noContent().build();
    }
}
