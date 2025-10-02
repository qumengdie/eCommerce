package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.exceptions.APIException;
import com.qumengdie.ecommerce.exceptions.ResourceNotFoundException;
import com.qumengdie.ecommerce.model.*;
import com.qumengdie.ecommerce.payload.OrderDTO;
import com.qumengdie.ecommerce.payload.OrderItemDTO;
import com.qumengdie.ecommerce.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
  @Autowired CartRepository cartRepository;

  @Autowired AddressRepository addressRepository;

  @Autowired PaymentRepository paymentRepository;

  @Autowired OrderRepository orderRepository;

  @Autowired OrderItemRepository orderItemRepository;

  @Autowired ProductRepository productRepository;

  @Autowired CartService cartService;

  @Autowired ModelMapper modelMapper;

  @Transactional
  @Override
  public OrderDTO placeOrder(
      String emailId,
      Long addressId,
      String paymentMethod,
      String pgName,
      String pgPaymentId,
      String pgStatus,
      String pgResponseMessage) {
    // get user cart
    Cart cart = cartRepository.findCartByEmail(emailId);
    if (cart == null) {
      throw new ResourceNotFoundException("Cart", "email", emailId);
    }

    Address address =
        addressRepository
            .findById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

    // create a new order with payment info
    Order order = new Order();
    order.setEmail(emailId);
    order.setOrderDate(LocalDate.now());
    order.setTotalAmount(cart.getTotalPrice());
    order.setOrderStatus("Order Accepted!");
    order.setAddress(address);

    Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
    payment.setOrder(order);
    payment = paymentRepository.save(payment);
    order.setPayment(payment);

    Order savedOrder = orderRepository.save(order);

    // get items from the cart into the order
    List<CartItem> cartItems = cart.getCartItems();
    if (cartItems.isEmpty()) {
      throw new APIException("Cart is Empty!");
    }

    List<OrderItem> orderItems = new ArrayList<>();
    for (CartItem cartItem : cartItems) {
      OrderItem orderItem = new OrderItem();
      orderItem.setProduct(cartItem.getProduct());
      orderItem.setQuantity(cartItem.getQuantity());
      orderItem.setDiscount(cartItem.getDiscount());
      orderItem.setOrderedProductPrice(cartItem.getProductPrice());
      orderItem.setOrder(savedOrder);
      orderItems.add(orderItem);
    }

    orderItems = orderItemRepository.saveAll(orderItems);

    // update product stock
    cart.getCartItems()
        .forEach(
            item -> {
              int quantity = item.getQuantity();
              Product product = item.getProduct();
              product.setQuantity(product.getQuantity() - quantity);
              productRepository.save(product);

              // clear the cart
              cartService.deleteProductFromCart(cart.getCartId(), product.getProductId());
            });

    // send back the order summary
    OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
    orderItems.forEach(
        item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

    return orderDTO;
  }
}
