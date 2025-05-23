package com.test_obs.inventoryms.service;

import com.test_obs.inventoryms.dto.InventoryDTO;
import com.test_obs.inventoryms.exception.ResourceNotFoundException;
import com.test_obs.inventoryms.model.Inventory;
import com.test_obs.inventoryms.model.Item;
import com.test_obs.inventoryms.repository.InventoryRepository;
import com.test_obs.inventoryms.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public InventoryDTO getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found with id: " + id));
        return convertToDTO(inventory);
    }

    @Transactional(readOnly = true)
    public Page<InventoryDTO> getAllInventory(Pageable pageable) {
        Page<Inventory> inventories = inventoryRepository.findAll(pageable);
        return inventories.map(this::convertToDTO);
    }

    @Transactional
    public InventoryDTO saveInventory(InventoryDTO inventoryDTO) {
        Inventory inventory = convertToEntity(inventoryDTO);
        Inventory savedInventory = inventoryRepository.save(inventory);
        return convertToDTO(savedInventory);
    }

    @Transactional
    public InventoryDTO updateInventory(Long id, InventoryDTO inventoryDTO) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory record not found with id: " + id);
        }

        Inventory inventory = convertToEntity(inventoryDTO);
        inventory.setId(id); // ✅ Now works because of @Setter

        Inventory updatedInventory = inventoryRepository.save(inventory);
        return convertToDTO(updatedInventory);
    }

    @Transactional
    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory record not found with id: " + id);
        }
        inventoryRepository.deleteById(id);
    }

    private InventoryDTO convertToDTO(Inventory inventory) {
        InventoryDTO dto = new InventoryDTO();
        dto.setId(inventory.getId());
        dto.setItemId(inventory.getItem().getId());
        dto.setItemName(inventory.getItem().getName());
        dto.setQuantity(inventory.getQuantity());
        dto.setType(inventory.getType());
        dto.setCreatedAt(inventory.getCreatedAt());
        return dto;
    }

    private Inventory convertToEntity(InventoryDTO dto) {
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + dto.getItemId()));

        Inventory inventory = new Inventory();
        inventory.setId(dto.getId());
        inventory.setItem(item);
        inventory.setQuantity(dto.getQuantity());
        inventory.setType(dto.getType());
        return inventory;
    }
}
