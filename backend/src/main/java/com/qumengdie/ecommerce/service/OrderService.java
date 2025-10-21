package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.payload.OrderDTO;
import com.qumengdie.ecommerce.payload.OrderResponse;
import jakarta.transaction.Transactional;

public interface OrderService {
  @Transactional
  OrderDTO placeOrder(
      String emailId,
      Long addressId,
      String paymentMethod,
      String pgName,
      String pgPaymentId,
      String pgStatus,
      String pgResponseMessage);

  OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

  OrderDTO updateOrder(Long orderId, String status);
}
