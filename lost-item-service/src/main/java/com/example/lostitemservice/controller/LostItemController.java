package com.example.lostitemservice.controller;

import com.example.lostitemservice.dto.LostItemDto;
import com.example.lostitemservice.service.LostItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lost-items")
public class LostItemController {

    private final LostItemService lostItemService;

    public LostItemController(LostItemService lostItemService) {
        this.lostItemService = lostItemService;
    }

    @GetMapping
    public ResponseEntity<List<LostItemDto>> getAllLostItems() {
        return ResponseEntity.ok(lostItemService.getAllLostItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LostItemDto> getLostItemById(@PathVariable Long id) {
        return ResponseEntity.ok(lostItemService.getLostItemById(id));
    }

    @GetMapping("/my-items")
    public ResponseEntity<List<LostItemDto>> getMyLostItems(Authentication authentication) {
        return ResponseEntity.ok(lostItemService.getLostItemsByUser(authentication.getName()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<LostItemDto>> searchLostItems(@RequestParam String keyword) {
        return ResponseEntity.ok(lostItemService.searchLostItems(keyword));
    }

    @PostMapping
    public ResponseEntity<LostItemDto> addLostItem(@Valid @RequestBody LostItemDto dto,
                                                    Authentication authentication) {
        LostItemDto created = lostItemService.addLostItem(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LostItemDto> updateLostItem(@PathVariable Long id,
                                                       @Valid @RequestBody LostItemDto dto) {
        return ResponseEntity.ok(lostItemService.updateLostItem(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLostItem(@PathVariable Long id) {
        lostItemService.deleteLostItem(id);
        return ResponseEntity.noContent().build();
    }
}
