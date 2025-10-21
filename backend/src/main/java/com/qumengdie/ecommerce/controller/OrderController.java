package com.qumengdie.ecommerce.controller;

import com.qumengdie.ecommerce.config.AppConstants;
import com.qumengdie.ecommerce.payload.OrderDTO;
import com.qumengdie.ecommerce.payload.OrderRequestDTO;
import com.qumengdie.ecommerce.payload.OrderResponse;
import com.qumengdie.ecommerce.payload.OrderStatusUpdateDTO;
import com.qumengdie.ecommerce.payload.StripePaymentDTO;
import com.qumengdie.ecommerce.service.OrderService;
import com.qumengdie.ecommerce.service.StripeService;
import com.qumengdie.ecommerce.util.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

  @GetMapping("/admin/orders")
  public ResponseEntity<OrderResponse> getAllOrders(
      @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER)
          Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
      @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY) String sortBy,
      @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
    OrderResponse response = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping("/admin/orders/{orderId}/status")
  public ResponseEntity<OrderDTO> updateOrderStatus(
      @PathVariable Long orderId, @RequestBody OrderStatusUpdateDTO orderStatusUpdateDTO) {
    OrderDTO orderDTO = orderService.updateOrder(orderId, orderStatusUpdateDTO.getStatus());
    return new ResponseEntity<>(orderDTO, HttpStatus.OK);
  }
}
