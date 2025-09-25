package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.exceptions.APIException;
import com.qumengdie.ecommerce.exceptions.ResourceNotFoundException;
import com.qumengdie.ecommerce.model.Category;
import com.qumengdie.ecommerce.payload.CategoryDTO;
import com.qumengdie.ecommerce.payload.CategoryResponse;
import com.qumengdie.ecommerce.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private ModelMapper modelMapper;

  @Override
  public CategoryResponse getAllCategories() {
    List<Category> categories = categoryRepository.findAll();
    if (categories.isEmpty()) throw new APIException("No category exists");

    List<CategoryDTO> categoryDTOS =
        categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList();

    CategoryResponse categoryResponse = new CategoryResponse();
    categoryResponse.setContent(categoryDTOS);

    return categoryResponse;
  }

  @Override
  public CategoryDTO createCategory(CategoryDTO categoryDTO) {
    Category category = modelMapper.map(categoryDTO, Category.class);

    Category categoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName());
    if (categoryFromDb != null)
      throw new APIException("Category with name " + category.getCategoryName() + "already exists");

    Category savedCategory = categoryRepository.save(category);
    return modelMapper.map(savedCategory, CategoryDTO.class);
  }

  @Override
  public CategoryDTO deleteCategory(Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

    categoryRepository.delete(category);
    return modelMapper.map(category, CategoryDTO.class);
  }

  @Override
  public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
    // check if exists first
    categoryRepository
        .findById(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

    Category category = modelMapper.map(categoryDTO, Category.class);

    category.setCategoryId(categoryId);
    Category savedCategory = categoryRepository.save(category);
    return modelMapper.map(savedCategory, CategoryDTO.class);
  }
}
