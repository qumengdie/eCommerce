package com.qumengdie.ecommerce.controller;

import com.qumengdie.ecommerce.payload.CategoryDTO;
import com.qumengdie.ecommerce.payload.CategoryResponse;
import com.qumengdie.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {

  @Autowired private CategoryService categoryService;

  @GetMapping("/public/categories")
  public ResponseEntity<CategoryResponse> getAllCategories() {
    CategoryResponse categories = categoryService.getAllCategories();
    return new ResponseEntity<>(categories, HttpStatus.OK);
  }

  @PostMapping("/public/categories")
  public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
    CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
    return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
  }

  @DeleteMapping("/admin/categories/{categoryId}")
  public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
    CategoryDTO deleteCategoryDTO = categoryService.deleteCategory(categoryId);
    return ResponseEntity.ok(deleteCategoryDTO);
  }

  @PutMapping("/public/categories/{categoryId}")
  public ResponseEntity<CategoryDTO> updateCategory(
      @Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId) {
    CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
    return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);
  }
}
