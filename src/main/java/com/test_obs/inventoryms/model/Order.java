package com.test_obs.inventoryms.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
@NoArgsConstructor // JPA requires this
@AllArgsConstructor // Optional, remove if not needed
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true)
    private String orderNo; // O1, O2, etc.

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @NotNull(message = "Item is required")
    private Item item;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive number")
    private Double price;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}