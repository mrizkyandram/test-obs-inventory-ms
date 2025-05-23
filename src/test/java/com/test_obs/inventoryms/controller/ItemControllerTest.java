package com.test_obs.inventoryms.controller;

import com.test_obs.inventoryms.dto.ItemDTO;
import com.test_obs.inventoryms.service.ItemService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private ItemDTO itemDTO;

    @BeforeEach
    void setUp() {
        itemDTO = new ItemDTO();
        itemDTO.setId(1L);
        itemDTO.setName("Test Item");
        itemDTO.setDescription("Test Description");
        itemDTO.setPrice(100.0);
    }

    @Test
    void getItemById_ShouldReturnItem() {
        when(itemService.getItemById(anyLong())).thenReturn(itemDTO);

        ResponseEntity<ItemDTO> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemDTO, response.getBody());
        verify(itemService, times(1)).getItemById(1L);
    }

    @Test
    void getAllItems_ShouldReturnPageOfItems() {
        Page<ItemDTO> page = new PageImpl<>(Collections.singletonList(itemDTO));
        when(itemService.getAllItems(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ItemDTO>> response = itemController.getAllItems(Pageable.unpaged());

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(itemDTO, response.getBody().getContent().get(0));
        verify(itemService, times(1)).getAllItems(any(Pageable.class));
    }

    @Test
    void createItem_ShouldReturnCreatedItem() {
        when(itemService.saveItem(any(ItemDTO.class))).thenReturn(itemDTO);

        ResponseEntity<ItemDTO> response = itemController.createItem(itemDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(itemDTO, response.getBody());
        verify(itemService, times(1)).saveItem(any(ItemDTO.class));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() {
        when(itemService.updateItem(anyLong(), any(ItemDTO.class))).thenReturn(itemDTO);

        ResponseEntity<ItemDTO> response = itemController.updateItem(1L, itemDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemDTO, response.getBody());
        verify(itemService, times(1)).updateItem(1L, itemDTO);
    }

    @Test
    void deleteItem_ShouldReturnNoContent() {
        doNothing().when(itemService).deleteItem(anyLong());

        ResponseEntity<Void> response = itemController.deleteItem(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(itemService, times(1)).deleteItem(1L);
    }
}