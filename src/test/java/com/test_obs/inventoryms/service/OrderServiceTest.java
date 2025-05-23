package com.test_obs.inventoryms.service;

import com.test_obs.inventoryms.dto.OrderDTO;
import com.test_obs.inventoryms.exception.InsufficientStockException;
import com.test_obs.inventoryms.exception.ResourceNotFoundException;
import com.test_obs.inventoryms.model.Inventory;
import com.test_obs.inventoryms.model.Item;
import com.test_obs.inventoryms.model.Order;
import com.test_obs.inventoryms.repository.InventoryRepository;
import com.test_obs.inventoryms.repository.ItemRepository;
import com.test_obs.inventoryms.repository.OrderRepository;
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
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderDTO orderDTO;
    private Item item;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        order = new Order();
        order.setId(1L);
        order.setOrderNo("O1");
        order.setItem(item);
        order.setQuantity(5);
        order.setPrice(100.0);

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setOrderNo("O1");
        orderDTO.setItemId(1L);
        orderDTO.setItemName("Test Item");
        orderDTO.setQuantity(5);
        orderDTO.setPrice(100.0);

        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setItem(item);
        inventory.setQuantity(5);
        inventory.setType("W");
    }

    @Test
    void getOrderById_ShouldReturnOrderDTO() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(orderDTO.getId(), result.getId());
        assertEquals(orderDTO.getOrderNo(), result.getOrderNo());
        assertEquals(orderDTO.getItemId(), result.getItemId());
        assertEquals(orderDTO.getQuantity(), result.getQuantity());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_ShouldThrowResourceNotFoundException() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getAllOrders_ShouldReturnPageOfOrderDTO() {
        Page<Order> page = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<OrderDTO> result = orderService.getAllOrders(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(orderDTO.getId(), result.getContent().get(0).getId());
        verify(orderRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void saveOrder_ShouldReturnSavedOrderDTO() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.findRemainingStockByItemId(anyLong())).thenReturn(10);
        when(orderRepository.countOrders()).thenReturn(0L);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        OrderDTO result = orderService.saveOrder(orderDTO);

        assertNotNull(result);
        assertEquals(orderDTO.getId(), result.getId());
        assertEquals("O1", result.getOrderNo()); // Verify order number generation
        verify(itemRepository, times(2)).findById(1L); // Changed from times(1) to times(2)
        verify(itemRepository, times(1)).findRemainingStockByItemId(1L);
        verify(orderRepository, times(1)).countOrders();
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void saveOrder_ShouldThrowResourceNotFoundExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.saveOrder(orderDTO));
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, never()).findRemainingStockByItemId(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void saveOrder_ShouldThrowInsufficientStockException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.findRemainingStockByItemId(anyLong())).thenReturn(2); // Less than requested quantity

        assertThrows(InsufficientStockException.class, () -> orderService.saveOrder(orderDTO));
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findRemainingStockByItemId(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrderDTO_WhenNoItemOrQuantityChange() {
        when(orderRepository.existsById(anyLong())).thenReturn(true);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item)); // Add this mock
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO result = orderService.updateOrder(1L, orderDTO);

        assertNotNull(result);
        assertEquals(orderDTO.getId(), result.getId());
        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findById(1L); // Add this verification
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrderDTO_WhenItemOrQuantityChanged() {
        OrderDTO updatedOrderDTO = new OrderDTO();
        updatedOrderDTO.setId(1L);
        updatedOrderDTO.setItemId(2L); // Different item
        updatedOrderDTO.setQuantity(3); // Different quantity
        updatedOrderDTO.setPrice(150.0);

        Item newItem = new Item();
        newItem.setId(2L);
        newItem.setName("New Item");

        when(orderRepository.existsById(anyLong())).thenReturn(true);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        // Allow multiple calls to findById(2L)
        when(itemRepository.findById(2L)).thenReturn(Optional.of(newItem));
        when(itemRepository.findRemainingStockByItemId(anyLong())).thenReturn(10);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        OrderDTO result = orderService.updateOrder(1L, updatedOrderDTO);

        assertNotNull(result);
        assertEquals(orderDTO.getId(), result.getId());
        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).findById(1L);
        // Verify findById(2L) was called twice
        verify(itemRepository, times(2)).findById(2L);
        verify(itemRepository, times(1)).findRemainingStockByItemId(2L);
        verify(inventoryRepository, times(2)).save(any(Inventory.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrder_ShouldThrowResourceNotFoundExceptionWhenOrderNotFound() {
        when(orderRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(1L, orderDTO));
        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateOrder_ShouldThrowInsufficientStockExceptionWhenNotEnoughStock() {
        OrderDTO updatedOrderDTO = new OrderDTO();
        updatedOrderDTO.setId(1L);
        updatedOrderDTO.setItemId(2L);
        updatedOrderDTO.setQuantity(15); // More than available stock
        updatedOrderDTO.setPrice(150.0);

        Item newItem = new Item();
        newItem.setId(2L);
        newItem.setName("New Item");

        when(orderRepository.existsById(anyLong())).thenReturn(true);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(newItem));
        when(itemRepository.findRemainingStockByItemId(anyLong())).thenReturn(10); // Less than requested quantity

        assertThrows(InsufficientStockException.class, () -> orderService.updateOrder(1L, updatedOrderDTO));
        verify(inventoryRepository, times(1)).save(any(Inventory.class)); // Only the top-up should be called
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void deleteOrder_ShouldDeleteOrderAndCreateTopUpInventory() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        doNothing().when(orderRepository).deleteById(anyLong());

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).findById(1L);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteOrder_ShouldThrowResourceNotFoundExceptionWhenOrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder(1L));
        verify(orderRepository, times(1)).findById(1L);
        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(orderRepository, never()).deleteById(anyLong());
    }
}