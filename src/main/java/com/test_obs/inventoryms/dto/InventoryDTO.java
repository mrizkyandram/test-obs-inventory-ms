package com.test_obs.inventoryms.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {
    private Long id;

    @NotNull(message = "Item ID is required")
    private Long itemId;

    private String itemName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    @NotNull(message = "Type is required")
    @Pattern(regexp = "^[TW]$", message = "Type must be either T (Top Up) or W (Withdrawal)")
    private String type;

    private LocalDateTime createdAt;
}