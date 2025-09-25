package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.exceptions.APIException;
import com.qumengdie.ecommerce.exceptions.ResourceNotFoundException;
import com.qumengdie.ecommerce.model.Category;
import com.qumengdie.ecommerce.repositories.CategoryRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired private CategoryRepository categoryRepository;

  @Override
  public List<Category> getAllCategories() {
    List<Category> categories = categoryRepository.findAll();
    if (categories.isEmpty()) throw new APIException("No category exists");
    return categoryRepository.findAll();
  }

  @Override
  public void createCategory(Category category) {
    Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
    if (savedCategory != null)
      throw new APIException("Category with name " + category.getCategoryName() + "already exists");
    categoryRepository.save(category);
  }

  @Override
  public String deleteCategory(Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

    categoryRepository.delete(category);
    return "Category with Id " + categoryId + " deleted";
  }

  @Override
  public Category updateCategory(Category category, Long categoryId) {
    categoryRepository
        .findById(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

    category.setCategoryId(categoryId);
    return categoryRepository.save(category);
  }
}
