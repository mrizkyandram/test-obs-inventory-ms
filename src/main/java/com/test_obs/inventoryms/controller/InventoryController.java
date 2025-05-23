package com.test_obs.inventoryms.controller;

import com.test_obs.inventoryms.dto.InventoryDTO;
import com.test_obs.inventoryms.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getInventoryById(@PathVariable Long id) {
        InventoryDTO inventory = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping
    public ResponseEntity<Page<InventoryDTO>> getAllInventory(Pageable pageable) {
        Page<InventoryDTO> inventory = inventoryService.getAllInventory(pageable);
        return ResponseEntity.ok(inventory);
    }

    @PostMapping
    public ResponseEntity<InventoryDTO> createInventory(@Valid @RequestBody InventoryDTO inventoryDTO) {
        InventoryDTO createdInventory = inventoryService.saveInventory(inventoryDTO);
        return new ResponseEntity<>(createdInventory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryDTO> updateInventory(@PathVariable Long id, @Valid @RequestBody InventoryDTO inventoryDTO) {
        InventoryDTO updatedInventory = inventoryService.updateInventory(id, inventoryDTO);
        return ResponseEntity.ok(updatedInventory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}