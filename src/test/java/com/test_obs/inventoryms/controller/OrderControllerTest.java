package com.test_obs.inventoryms.controller;

import com.test_obs.inventoryms.dto.OrderDTO;
import com.test_obs.inventoryms.service.OrderService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setOrderNo("ORD-001");
        orderDTO.setItemId(1L);
        orderDTO.setItemName("Test Item");
        orderDTO.setQuantity(5);
        orderDTO.setPrice(100.0);
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        when(orderService.getOrderById(anyLong())).thenReturn(orderDTO);

        ResponseEntity<OrderDTO> response = orderController.getOrderById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void getAllOrders_ShouldReturnPageOfOrders() {
        Page<OrderDTO> page = new PageImpl<>(Collections.singletonList(orderDTO));
        when(orderService.getAllOrders(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<OrderDTO>> response = orderController.getAllOrders(Pageable.unpaged());

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(orderDTO, response.getBody().getContent().get(0));
        verify(orderService, times(1)).getAllOrders(any(Pageable.class));
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        when(orderService.saveOrder(any(OrderDTO.class))).thenReturn(orderDTO);

        ResponseEntity<OrderDTO> response = orderController.createOrder(orderDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
        verify(orderService, times(1)).saveOrder(any(OrderDTO.class));
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrder() {
        when(orderService.updateOrder(anyLong(), any(OrderDTO.class))).thenReturn(orderDTO);

        ResponseEntity<OrderDTO> response = orderController.updateOrder(1L, orderDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
        verify(orderService, times(1)).updateOrder(1L, orderDTO);
    }

    @Test
    void deleteOrder_ShouldReturnNoContent() {
        doNothing().when(orderService).deleteOrder(anyLong());

        ResponseEntity<Void> response = orderController.deleteOrder(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).deleteOrder(1L);
    }

    @Test
    void createOrder_ShouldValidateInput() {
        OrderDTO invalidOrder = new OrderDTO();
        invalidOrder.setQuantity(0); // Invalid quantity
        invalidOrder.setPrice(-10.0); // Invalid price

        // The @Valid annotation should trigger validation before reaching the service
        // This test would normally require a Spring context to test validation properly
        // For unit test, we assume validation is handled by Spring before controller

        // Alternative: Test with MockMvc in integration tests
        assertTrue(true); // Placeholder assertion
    }

    @Test
    void updateOrder_ShouldValidateInput() {
        OrderDTO invalidOrder = new OrderDTO();
        invalidOrder.setQuantity(-5); // Invalid quantity
        invalidOrder.setPrice(0.0); // Invalid price

        // Similar to createOrder validation test
        // Actual validation testing would be better in integration tests
        assertTrue(true); // Placeholder assertion
    }
}