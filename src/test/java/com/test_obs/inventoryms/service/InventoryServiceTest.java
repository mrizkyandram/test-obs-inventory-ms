package com.test_obs.inventoryms.service;

import com.test_obs.inventoryms.dto.InventoryDTO;
import com.test_obs.inventoryms.exception.ResourceNotFoundException;
import com.test_obs.inventoryms.model.Inventory;
import com.test_obs.inventoryms.model.Item;
import com.test_obs.inventoryms.repository.InventoryRepository;
import com.test_obs.inventoryms.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;
    private InventoryDTO inventoryDTO;
    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setItem(item);
        inventory.setQuantity(10);
        inventory.setType("IN");

        inventoryDTO = new InventoryDTO();
        inventoryDTO.setId(1L);
        inventoryDTO.setItemId(1L);
        inventoryDTO.setItemName("Test Item");
        inventoryDTO.setQuantity(10);
        inventoryDTO.setType("IN");
    }

    @Test
    void getInventoryById_ShouldReturnInventoryDTO() {
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.of(inventory));

        InventoryDTO result = inventoryService.getInventoryById(1L);

        assertNotNull(result);
        assertEquals(inventoryDTO.getId(), result.getId());
        assertEquals(inventoryDTO.getItemId(), result.getItemId());
        assertEquals(inventoryDTO.getItemName(), result.getItemName());
        assertEquals(inventoryDTO.getQuantity(), result.getQuantity());
        assertEquals(inventoryDTO.getType(), result.getType());
        verify(inventoryRepository, times(1)).findById(1L);
    }

    @Test
    void getInventoryById_ShouldThrowResourceNotFoundException() {
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getInventoryById(1L));
        verify(inventoryRepository, times(1)).findById(1L);
    }

    @Test
    void getAllInventory_ShouldReturnPageOfInventoryDTO() {
        Page<Inventory> page = new PageImpl<>(Collections.singletonList(inventory));
        when(inventoryRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<InventoryDTO> result = inventoryService.getAllInventory(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(inventoryDTO.getId(), result.getContent().get(0).getId());
        verify(inventoryRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void saveInventory_ShouldReturnSavedInventoryDTO() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        InventoryDTO result = inventoryService.saveInventory(inventoryDTO);

        assertNotNull(result);
        assertEquals(inventoryDTO.getId(), result.getId());
        assertEquals(inventoryDTO.getItemId(), result.getItemId());
        assertEquals(inventoryDTO.getItemName(), result.getItemName());
        verify(itemRepository, times(1)).findById(1L);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void saveInventory_ShouldThrowResourceNotFoundExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.saveInventory(inventoryDTO));
        verify(itemRepository, times(1)).findById(1L);
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void updateInventory_ShouldReturnUpdatedInventoryDTO() {
        when(inventoryRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        InventoryDTO result = inventoryService.updateInventory(1L, inventoryDTO);

        assertNotNull(result);
        assertEquals(inventoryDTO.getId(), result.getId());
        assertEquals(inventoryDTO.getItemId(), result.getItemId());
        assertEquals(inventoryDTO.getItemName(), result.getItemName());
        verify(inventoryRepository, times(1)).existsById(1L);
        verify(itemRepository, times(1)).findById(1L);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void updateInventory_ShouldThrowResourceNotFoundExceptionWhenInventoryNotFound() {
        when(inventoryRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.updateInventory(1L, inventoryDTO));
        verify(inventoryRepository, times(1)).existsById(1L);
        verify(itemRepository, never()).findById(anyLong());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void deleteInventory_ShouldDeleteInventory() {
        when(inventoryRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(inventoryRepository).deleteById(anyLong());

        inventoryService.deleteInventory(1L);

        verify(inventoryRepository, times(1)).existsById(1L);
        verify(inventoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteInventory_ShouldThrowResourceNotFoundExceptionWhenInventoryNotFound() {
        when(inventoryRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.deleteInventory(1L));
        verify(inventoryRepository, times(1)).existsById(1L);
        verify(inventoryRepository, never()).deleteById(anyLong());
    }
}
