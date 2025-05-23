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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return convertToDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(this::convertToDTO);
    }

    @Transactional
    public OrderDTO saveOrder(OrderDTO orderDTO) {
        // Get the item and check stock
        Item item = itemRepository.findById(orderDTO.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + orderDTO.getItemId()));

        Integer remainingStock = itemRepository.findRemainingStockByItemId(item.getId());

        if (remainingStock < orderDTO.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for item: " + item.getName() +
                    " (Available: " + remainingStock + ", Requested: " + orderDTO.getQuantity() + ")");
        }

        // Create the order
        Order order = convertToEntity(orderDTO);

        // Generate order number (O + sequential number)
        long orderCount = orderRepository.countOrders() + 1;
        order.setOrderNo("O" + orderCount);

        Order savedOrder = orderRepository.save(order);

        // Create withdrawal inventory record
        Inventory withdrawal = new Inventory();
        withdrawal.setItem(item);
        withdrawal.setQuantity(orderDTO.getQuantity());
        withdrawal.setType("W"); // Withdrawal for order
        inventoryRepository.save(withdrawal);

        return convertToDTO(savedOrder);
    }

    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }

        // Get the original order
        Order originalOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));


        // If the item or quantity changed, we need to check stock and adjust inventory
        if (!originalOrder.getItem().getId().equals(orderDTO.getItemId()) ||
                !originalOrder.getQuantity().equals(orderDTO.getQuantity())) {

            // Restore the original inventory by creating a top-up entry
            Inventory topUp = new Inventory();
            topUp.setItem(originalOrder.getItem());
            topUp.setQuantity(originalOrder.getQuantity());
            topUp.setType("T"); // Top up for the returned items
            inventoryRepository.save(topUp);

            // Check stock for the new order
            Item newItem = itemRepository.findById(orderDTO.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + orderDTO.getItemId()));

            Integer remainingStock = itemRepository.findRemainingStockByItemId(newItem.getId());

            if (remainingStock < orderDTO.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for item: " + newItem.getName() +
                        " (Available: " + remainingStock + ", Requested: " + orderDTO.getQuantity() + ")");
            }

            // Create withdrawal for the new order
            Inventory withdrawal = new Inventory();
            withdrawal.setItem(newItem);
            withdrawal.setQuantity(orderDTO.getQuantity());
            withdrawal.setType("W"); // Withdrawal for order
            inventoryRepository.save(withdrawal);
        }

        // Update the order
        Order order = convertToEntity(orderDTO);
        order.setId(id);
        order.setOrderNo(originalOrder.getOrderNo()); // Preserve the order number

        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Restore the inventory by creating a top-up entry
        Inventory topUp = new Inventory();
        topUp.setItem(order.getItem());
        topUp.setQuantity(order.getQuantity());
        topUp.setType("T"); // Top up for the returned items
        inventoryRepository.save(topUp);

        // Delete the order
        orderRepository.deleteById(id);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setItemId(order.getItem().getId());
        dto.setItemName(order.getItem().getName());
        dto.setQuantity(order.getQuantity());
        dto.setPrice(order.getPrice());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

    private Order convertToEntity(OrderDTO dto) {
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + dto.getItemId()));

        Order order = new Order();
        order.setId(dto.getId());
        // Don't set orderNo here - it's generated in saveOrder
        order.setItem(item);
        order.setQuantity(dto.getQuantity());
        order.setPrice(dto.getPrice());
        return order;
    }
}