package com.qumengdie.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long paymentId;

  @OneToOne(
      mappedBy = "payment",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private Order order;

  @NotBlank private String paymentMethod;

  private String pgPaymentId;
  private String pgStatus;
  private String pgResponseMessage;
  private String pgName;

  public Payment(
      String paymentMethod,
      String pgPaymentId,
      String pgStatus,
      String pgResponseMessage,
      String pgName) {
    this.paymentMethod = paymentMethod;
    this.pgPaymentId = pgPaymentId;
    this.pgStatus = pgStatus;
    this.pgResponseMessage = pgResponseMessage;
    this.pgName = pgName;
  }
}
