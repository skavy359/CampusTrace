package com.example.lostitemservice.service;

import com.example.lostitemservice.dto.LostItemDto;
import com.example.lostitemservice.entity.LostItem;
import com.example.lostitemservice.exception.ResourceNotFoundException;
import com.example.lostitemservice.repository.LostItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LostItemService {

    private final LostItemRepository lostItemRepository;

    public LostItemService(LostItemRepository lostItemRepository) {
        this.lostItemRepository = lostItemRepository;
    }

    public LostItemDto addLostItem(LostItemDto dto, String username) {
        LostItem item = new LostItem();
        item.setItemName(dto.getItemName());
        item.setDescription(dto.getDescription());
        item.setCategory(dto.getCategory());
        item.setLocation(dto.getLocation());
        item.setDateLost(dto.getDateLost());
        item.setReportedBy(username);
        item.setContactInfo(dto.getContactInfo());
        item.setStatus("LOST");
        item.setImageUrl(dto.getImageUrl());

        LostItem saved = lostItemRepository.save(item);
        return convertToDto(saved);
    }

    public List<LostItemDto> getAllLostItems() {
        return lostItemRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public LostItemDto getLostItemById(Long id) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lost item not found with id: " + id));
        return convertToDto(item);
    }

    public List<LostItemDto> getLostItemsByUser(String username) {
        return lostItemRepository.findByReportedBy(username).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<LostItemDto> searchLostItems(String keyword) {
        return lostItemRepository.searchByKeyword(keyword).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public LostItemDto updateLostItem(Long id, LostItemDto dto) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lost item not found with id: " + id));

        item.setItemName(dto.getItemName());
        item.setDescription(dto.getDescription());
        item.setCategory(dto.getCategory());
        item.setLocation(dto.getLocation());
        item.setDateLost(dto.getDateLost());
        item.setContactInfo(dto.getContactInfo());
        item.setImageUrl(dto.getImageUrl());

        if (dto.getStatus() != null) {
            item.setStatus(dto.getStatus());
        }

        LostItem updated = lostItemRepository.save(item);
        return convertToDto(updated);
    }

    public void deleteLostItem(Long id) {
        if (!lostItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lost item not found with id: " + id);
        }
        lostItemRepository.deleteById(id);
    }

    private LostItemDto convertToDto(LostItem item) {
        LostItemDto dto = new LostItemDto();
        dto.setId(item.getId());
        dto.setItemName(item.getItemName());
        dto.setDescription(item.getDescription());
        dto.setCategory(item.getCategory());
        dto.setLocation(item.getLocation());
        dto.setDateLost(item.getDateLost());
        dto.setReportedBy(item.getReportedBy());
        dto.setContactInfo(item.getContactInfo());
        dto.setStatus(item.getStatus());
        dto.setImageUrl(item.getImageUrl());
        return dto;
    }
}
