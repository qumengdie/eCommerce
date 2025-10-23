package com.qumengdie.ecommerce.controller;

import com.qumengdie.ecommerce.config.AppConstants;
import com.qumengdie.ecommerce.payload.ProductDTO;
import com.qumengdie.ecommerce.payload.ProductResponse;
import com.qumengdie.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

  @Autowired ProductService productService;

  @Autowired ModelMapper modelMapper;

  @PostMapping("/admin/categories/{categoryId}/product")
  public ResponseEntity<ProductDTO> addProduct(
      @Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {
    ProductDTO savedProductDTO = productService.addProduct(categoryId, productDTO);
    return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
  }

  @GetMapping("/public/products")
  public ResponseEntity<ProductResponse> getAllProducts(
      @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER)
          Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
      @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
      @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder,
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "category", required = false) String category) {
    ProductResponse productResponse =
        productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder, keyword, category);
    return new ResponseEntity<>(productResponse, HttpStatus.OK);
  }

  @GetMapping("/public/categories/{categoryId}/products")
  public ResponseEntity<ProductResponse> getProductsByCategory(
      @PathVariable Long categoryId,
      @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER)
          Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
      @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
      @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
    ProductResponse productResponse =
        productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder);
    return new ResponseEntity<>(productResponse, HttpStatus.OK);
  }

  @GetMapping("/public/products/keyword/{keyword}")
  public ResponseEntity<ProductResponse> getProductsByKeyword(
      @PathVariable String keyword,
      @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER)
          Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
      @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
      @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
    ProductResponse productResponse =
        productService.searchByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
    return new ResponseEntity<>(productResponse, HttpStatus.OK);
  }

  @PutMapping("/admin/products/{productId}")
  public ResponseEntity<ProductDTO> updateProduct(
      @Valid @RequestBody ProductDTO productDTO, @PathVariable Long productId) {
    ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);
    return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
  }

  @DeleteMapping("/admin/products/{productId}")
  public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
    ProductDTO updatedProductDTO = productService.deleteProduct(productId);
    return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
  }

  @PutMapping("/products/{productId}/image")
  public ResponseEntity<ProductDTO> updateProductImage(
      @PathVariable Long productId, @RequestParam("image") MultipartFile image) throws IOException {
    ProductDTO updatedProductDTO = productService.updateProductImage(productId, image);
    return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
  }

  @GetMapping("/admin/products")
  public ResponseEntity<ProductResponse> getAllProductsForAdmin(
      @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER)
          Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
      @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
      @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
    ProductResponse productResponse =
        productService.getAllProductsForAdmin(pageNumber, pageSize, sortBy, sortOrder);
    return new ResponseEntity<>(productResponse, HttpStatus.OK);
  }
}
