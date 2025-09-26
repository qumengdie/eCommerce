package com.qumengdie.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotBlank private String productName;
  private String image;
  private String description;
  private Integer quantity;
  private double price;
  private double discount;
  private double specialPrice;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;
}
