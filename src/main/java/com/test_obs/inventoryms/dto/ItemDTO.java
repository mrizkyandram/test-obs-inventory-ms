package com.test_obs.inventoryms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private Long id;

    @NotBlank(message = "Item name is required")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive number")
    private Double price;

    private Integer remainingStock;

    private String description;
}