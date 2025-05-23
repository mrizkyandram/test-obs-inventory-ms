package com.test_obs.inventoryms.controller;

import com.test_obs.inventoryms.dto.ItemDTO;
import com.test_obs.inventoryms.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;


    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        ItemDTO item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<Page<ItemDTO>> getAllItems(Pageable pageable) {
        Page<ItemDTO> items = itemService.getAllItems(pageable);
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody ItemDTO itemDTO) {
        ItemDTO createdItem = itemService.saveItem(itemDTO);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @Valid @RequestBody ItemDTO itemDTO) {
        ItemDTO updatedItem = itemService.updateItem(id, itemDTO);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}