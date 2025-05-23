package com.test_obs.inventoryms.controller;

import com.test_obs.inventoryms.dto.InventoryDTO;
import com.test_obs.inventoryms.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private InventoryDTO inventoryDTO;

    @BeforeEach
    void setUp() {
        inventoryDTO = new InventoryDTO();
        inventoryDTO.setId(1L);
        inventoryDTO.setItemId(1L);
        inventoryDTO.setItemName("Test Item");
        inventoryDTO.setQuantity(10);
        inventoryDTO.setType("IN");
    }

    @Test
    void getInventoryById_ShouldReturnInventory() {
        when(inventoryService.getInventoryById(anyLong())).thenReturn(inventoryDTO);

        ResponseEntity<InventoryDTO> response = inventoryController.getInventoryById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inventoryDTO, response.getBody());
        verify(inventoryService, times(1)).getInventoryById(1L);
    }

    @Test
    void getAllInventory_ShouldReturnPageOfInventory() {
        Page<InventoryDTO> page = new PageImpl<>(Collections.singletonList(inventoryDTO));
        when(inventoryService.getAllInventory(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<InventoryDTO>> response = inventoryController.getAllInventory(Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(inventoryDTO, response.getBody().getContent().get(0));
        verify(inventoryService, times(1)).getAllInventory(any(Pageable.class));
    }

    @Test
    void createInventory_ShouldReturnCreatedInventory() {
        when(inventoryService.saveInventory(any(InventoryDTO.class))).thenReturn(inventoryDTO);

        ResponseEntity<InventoryDTO> response = inventoryController.createInventory(inventoryDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(inventoryDTO, response.getBody());
        verify(inventoryService, times(1)).saveInventory(any(InventoryDTO.class));
    }

    @Test
    void updateInventory_ShouldReturnUpdatedInventory() {
        when(inventoryService.updateInventory(anyLong(), any(InventoryDTO.class))).thenReturn(inventoryDTO);

        ResponseEntity<InventoryDTO> response = inventoryController.updateInventory(1L, inventoryDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inventoryDTO, response.getBody());
        verify(inventoryService, times(1)).updateInventory(eq(1L), any(InventoryDTO.class));
    }

    @Test
    void deleteInventory_ShouldReturnNoContent() {
        doNothing().when(inventoryService).deleteInventory(anyLong());

        ResponseEntity<Void> response = inventoryController.deleteInventory(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(inventoryService, times(1)).deleteInventory(1L);
    }
}