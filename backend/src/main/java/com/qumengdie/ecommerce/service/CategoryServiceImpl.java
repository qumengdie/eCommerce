package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.exceptions.APIException;
import com.qumengdie.ecommerce.exceptions.ResourceNotFoundException;
import com.qumengdie.ecommerce.model.Category;
import com.qumengdie.ecommerce.payload.CategoryDTO;
import com.qumengdie.ecommerce.payload.CategoryResponse;
import com.qumengdie.ecommerce.repositories.CategoryRepository;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private ModelMapper modelMapper;

  @Override
  public CategoryResponse getAllCategories(
      Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
    Sort sortByAndOrder =
        sortOrder.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
    Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

    List<Category> categories = categoryPage.getContent();
    if (categories.isEmpty()) throw new APIException("No category exists");

    List<CategoryDTO> categoryDTOS =
        categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList();

    CategoryResponse categoryResponse = new CategoryResponse();
    categoryResponse.setContent(categoryDTOS);
    categoryResponse.setPageNumber(categoryPage.getNumber());
    categoryResponse.setPageSize(categoryPage.getSize());
    categoryResponse.setTotalElements(categoryPage.getTotalElements());
    categoryResponse.setTotalPages(categoryPage.getTotalPages());
    categoryResponse.setLastPage(categoryPage.isLast());
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
