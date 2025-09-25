package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.payload.CategoryDTO;
import com.qumengdie.ecommerce.payload.CategoryResponse;

public interface CategoryService {
  CategoryResponse getAllCategories(
      Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

  CategoryDTO createCategory(CategoryDTO categoryDTO);

  CategoryDTO deleteCategory(Long categoryId);

  CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
