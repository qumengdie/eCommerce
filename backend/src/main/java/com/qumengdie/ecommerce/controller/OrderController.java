package com.qumengdie.ecommerce.controller;

import com.qumengdie.ecommerce.payload.OrderDTO;
import com.qumengdie.ecommerce.payload.OrderRequestDTO;
import com.qumengdie.ecommerce.payload.StripePaymentDTO;
import com.qumengdie.ecommerce.service.OrderService;
import com.qumengdie.ecommerce.service.StripeService;
import com.qumengdie.ecommerce.util.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
  @Autowired OrderService orderService;

  @Autowired AuthUtil authUtil;

  @Autowired private StripeService stripeService;

  @PostMapping("/order/users/payments/{paymentMethod}")
  public ResponseEntity<OrderDTO> orderProducts(
      @PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO) {
    String emailId = authUtil.loggedInEmail();
    OrderDTO orderDTO =
        orderService.placeOrder(
            emailId,
            orderRequestDTO.getAddressId(),
            paymentMethod,
            orderRequestDTO.getPgName(),
            orderRequestDTO.getPgPaymentId(),
            orderRequestDTO.getPgStatus(),
            orderRequestDTO.getPgResponseMessage());
    return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
  }

  @PostMapping("/order/stripe-client-secret")
  public ResponseEntity<String> createStripeClientSecret(
      @RequestBody StripePaymentDTO stripePaymentDTO) throws StripeException {
    PaymentIntent paymentIntent = stripeService.paymentIntent(stripePaymentDTO);
    return new ResponseEntity<>(paymentIntent.getClientSecret(), HttpStatus.CREATED);
  }
}
