package com.qumengdie.ecommerce.repositories;

import com.qumengdie.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
