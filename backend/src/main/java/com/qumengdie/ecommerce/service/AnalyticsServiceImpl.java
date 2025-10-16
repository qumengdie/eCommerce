package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.payload.AnalyticsResponse;
import com.qumengdie.ecommerce.repositories.OrderRepository;
import com.qumengdie.ecommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {
  @Autowired private ProductRepository productRepository;

  @Autowired private OrderRepository orderRepository;

  @Override
  public AnalyticsResponse getAnalyticsData() {
    AnalyticsResponse response = new AnalyticsResponse();

    long productCount = productRepository.count();
    long totalOrders = orderRepository.count();
    Double totalRevenue = orderRepository.getTotalRevenue();

    response.setProductCount(String.valueOf(productCount));
    response.setTotalOrders(String.valueOf(totalOrders));
    response.setTotalRevenue(String.valueOf(totalRevenue != null ? totalRevenue : 0));

    return response;
  }
}
