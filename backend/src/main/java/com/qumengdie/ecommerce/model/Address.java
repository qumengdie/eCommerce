package com.qumengdie.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long addressId;

  @NotBlank private String street;

  @NotBlank private String buildingName;

  @NotBlank private String city;

  @NotBlank private String state;

  @NotBlank private String country;

  @NotBlank private String zipCode;

  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  public Address(
      String street,
      String buildingName,
      String city,
      String state,
      String country,
      String zipCode) {
    this.street = street;
    this.buildingName = buildingName;
    this.city = city;
    this.state = state;
    this.country = country;
    this.zipCode = zipCode;
  }
}
