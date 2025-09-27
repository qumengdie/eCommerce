package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.payload.ProductDTO;
import com.qumengdie.ecommerce.payload.ProductResponse;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
  ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

  ProductResponse getAllProducts(
      Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

  ProductResponse searchByCategory(
      Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

  ProductResponse searchByKeyword(
      String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

  ProductDTO updateProduct(Long productId, ProductDTO productDTO);

  ProductDTO deleteProduct(Long productId);

  ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
