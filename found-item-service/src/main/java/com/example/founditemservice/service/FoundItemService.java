package com.example.founditemservice.service;

import com.example.founditemservice.dto.FoundItemDto;
import com.example.founditemservice.entity.FoundItem;
import com.example.founditemservice.exception.ResourceNotFoundException;
import com.example.founditemservice.repository.FoundItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoundItemService {

    private final FoundItemRepository foundItemRepository;

    public FoundItemService(FoundItemRepository foundItemRepository) {
        this.foundItemRepository = foundItemRepository;
    }

    public FoundItemDto addFoundItem(FoundItemDto dto, String username) {
        FoundItem item = new FoundItem();
        item.setItemName(dto.getItemName());
        item.setDescription(dto.getDescription());
        item.setCategory(dto.getCategory());
        item.setLocationFound(dto.getLocationFound());
        item.setDateFound(dto.getDateFound());
        item.setFoundBy(username);
        item.setContactInfo(dto.getContactInfo());
        item.setStatus("FOUND");
        item.setClaimed(false);
        item.setImageUrl(dto.getImageUrl());

        FoundItem saved = foundItemRepository.save(item);
        return convertToDto(saved);
    }

    public List<FoundItemDto> getAllFoundItems() {
        return foundItemRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public FoundItemDto getFoundItemById(Long id) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + id));
        return convertToDto(item);
    }

    public List<FoundItemDto> getFoundItemsByUser(String username) {
        return foundItemRepository.findByFoundBy(username).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<FoundItemDto> searchFoundItems(String keyword) {
        return foundItemRepository.searchByKeyword(keyword).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public FoundItemDto updateFoundItem(Long id, FoundItemDto dto) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + id));

        item.setItemName(dto.getItemName());
        item.setDescription(dto.getDescription());
        item.setCategory(dto.getCategory());
        item.setLocationFound(dto.getLocationFound());
        item.setDateFound(dto.getDateFound());
        item.setContactInfo(dto.getContactInfo());
        item.setImageUrl(dto.getImageUrl());

        if (dto.getStatus() != null) {
            item.setStatus(dto.getStatus());
        }

        FoundItem updated = foundItemRepository.save(item);
        return convertToDto(updated);
    }

    public FoundItemDto markAsClaimed(Long id) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + id));
        item.setClaimed(true);
        item.setStatus("CLAIMED");
        FoundItem updated = foundItemRepository.save(item);
        return convertToDto(updated);
    }

    public void deleteFoundItem(Long id) {
        if (!foundItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Found item not found with id: " + id);
        }
        foundItemRepository.deleteById(id);
    }

    private FoundItemDto convertToDto(FoundItem item) {
        FoundItemDto dto = new FoundItemDto();
        dto.setId(item.getId());
        dto.setItemName(item.getItemName());
        dto.setDescription(item.getDescription());
        dto.setCategory(item.getCategory());
        dto.setLocationFound(item.getLocationFound());
        dto.setDateFound(item.getDateFound());
        dto.setFoundBy(item.getFoundBy());
        dto.setContactInfo(item.getContactInfo());
        dto.setClaimed(item.isClaimed());
        dto.setStatus(item.getStatus());
        dto.setImageUrl(item.getImageUrl());
        return dto;
    }
}
