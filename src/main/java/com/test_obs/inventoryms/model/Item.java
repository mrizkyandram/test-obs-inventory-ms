package com.test_obs.inventoryms.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
@NoArgsConstructor // JPA requires this
@AllArgsConstructor // Optional, remove if not needed
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive number")
    private Double price;

    private String description;

    // This is a calculated field - not stored in database
    @Transient
    private Integer remainingStock;
}
