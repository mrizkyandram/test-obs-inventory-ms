package com.test_obs.inventoryms.service;

import com.test_obs.inventoryms.dto.ItemDTO;
import com.test_obs.inventoryms.exception.ResourceNotFoundException;
import com.test_obs.inventoryms.model.Item;
import com.test_obs.inventoryms.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item item;
    private ItemDTO itemDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(10.0);
        item.setRemainingStock(0); // Initialize with 0 stock

        itemDTO = new ItemDTO();
        itemDTO.setId(1L);
        itemDTO.setName("Test Item");
        itemDTO.setPrice(10.0);
        itemDTO.setRemainingStock(0); // Initialize with 0 stock
    }

    @Test
    void getItemById_Found() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.findRemainingStockByItemId(1L)).thenReturn(5);

        ItemDTO result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals(10.0, result.getPrice());
        assertEquals(5, result.getRemainingStock()); // Verify updated stock
    }

    @Test
    void getItemById_NotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.getItemById(99L);
        });

        verify(itemRepository, never()).findRemainingStockByItemId(any());
    }

    @Test
    void getAllItems() {
        List<Item> items = Arrays.asList(item);
        Page<Item> page = new PageImpl<>(items);
        Pageable pageable = PageRequest.of(0, 10);

        when(itemRepository.findAll(pageable)).thenReturn(page);
        when(itemRepository.findRemainingStockByItemId(1L)).thenReturn(5);

        Page<ItemDTO> result = itemService.getAllItems(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        ItemDTO firstItem = result.getContent().get(0);
        assertEquals("Test Item", firstItem.getName());
        assertEquals(5, firstItem.getRemainingStock());
    }

    @Test
    void saveItem() {
        itemDTO.setRemainingStock(null); // Stock should not be set when creating
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDTO result = itemService.saveItem(itemDTO);

        assertNotNull(result);
        assertEquals("Test Item", result.getName());
        assertEquals(10.0, result.getPrice());
        assertEquals(0, result.getRemainingStock()); // Verify default stock
        verify(itemRepository, never()).findRemainingStockByItemId(any());
    }

    @Test
    void updateItem_Found() {
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemRepository.findRemainingStockByItemId(1L)).thenReturn(5);

        ItemDTO result = itemService.updateItem(1L, itemDTO);

        assertNotNull(result);
        assertEquals("Test Item", result.getName());
        assertEquals(10.0, result.getPrice());
        assertEquals(5, result.getRemainingStock());
    }

    @Test
    void updateItem_NotFound() {
        when(itemRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.updateItem(99L, itemDTO);
        });

        verify(itemRepository, never()).save(any());
        verify(itemRepository, never()).findRemainingStockByItemId(any());
    }

    @Test
    void deleteItem_Found() {
        when(itemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(itemRepository).deleteById(1L);

        assertDoesNotThrow(() -> {
            itemService.deleteItem(1L);
        });

        verify(itemRepository, times(1)).deleteById(1L);
        verify(itemRepository, never()).findRemainingStockByItemId(any());
    }

    @Test
    void deleteItem_NotFound() {
        when(itemRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.deleteItem(99L);
        });

        verify(itemRepository, never()).deleteById(any());
        verify(itemRepository, never()).findRemainingStockByItemId(any());
    }
}