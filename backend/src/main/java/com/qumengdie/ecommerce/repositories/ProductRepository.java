package com.qumengdie.ecommerce.repositories;

import com.qumengdie.ecommerce.model.Category;
import com.qumengdie.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findByCategoryOrderByPriceAsc(Category category);

  List<Product> findByProductNameLikeIgnoreCase(String keyword);
}
