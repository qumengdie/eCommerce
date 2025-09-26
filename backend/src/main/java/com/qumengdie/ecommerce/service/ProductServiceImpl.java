package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.exceptions.APIException;
import com.qumengdie.ecommerce.exceptions.ResourceNotFoundException;
import com.qumengdie.ecommerce.model.Category;
import com.qumengdie.ecommerce.model.Product;
import com.qumengdie.ecommerce.payload.ProductDTO;
import com.qumengdie.ecommerce.payload.ProductResponse;
import com.qumengdie.ecommerce.repositories.CategoryRepository;
import com.qumengdie.ecommerce.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

  @Autowired private ProductRepository productRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private ModelMapper modelMapper;

  @Autowired private FileService fileService;

  @Value("${project.image}")
  private String path;

  @Override
  public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

    boolean isProductNotPresent = true;

    List<Product> products = category.getProducts();
    for (Product value : products) {
      if (value.getProductName().equals(productDTO.getProductName())) {
        isProductNotPresent = false;
        break;
      }
    }

    if (isProductNotPresent) {
      Product product = modelMapper.map(productDTO, Product.class);
      product.setImage("default.png");
      product.setCategory(category);
      double specialPrice =
          product.getPrice() - (product.getDiscount() * 0.01) * product.getPrice();
      product.setSpecialPrice(specialPrice);
      Product savedProduct = productRepository.save(product);
      return modelMapper.map(savedProduct, ProductDTO.class);
    } else {
      throw new APIException("Product already exists");
    }
  }

  @Override
  public ProductResponse getAllProducts() {
    List<Product> products = productRepository.findAll();
    List<ProductDTO> productDTOS =
        products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

    if (products.isEmpty()) {
      throw new APIException("No Product exists");
    }

    ProductResponse productResponse = new ProductResponse();
    productResponse.setContent(productDTOS);
    return productResponse;
  }

  @Override
  public ProductResponse searchByCategory(Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

    List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);
    List<ProductDTO> productDTOS =
        products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

    ProductResponse productResponse = new ProductResponse();
    productResponse.setContent(productDTOS);
    return productResponse;
  }

  @Override
  public ProductResponse searchByKeyword(String keyword) {
    List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%');
    List<ProductDTO> productDTOS =
        products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

    ProductResponse productResponse = new ProductResponse();
    productResponse.setContent(productDTOS);
    return productResponse;
  }

  @Override
  public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
    Product productFromDb =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

    Product product = modelMapper.map(productDTO, Product.class);
    productFromDb.setProductName(product.getProductName());
    productFromDb.setDescription(product.getDescription());
    productFromDb.setQuantity(product.getQuantity());
    productFromDb.setPrice(product.getPrice());
    productFromDb.setDiscount(product.getDiscount());
    productFromDb.setSpecialPrice(product.getSpecialPrice());

    Product savedProduct = productRepository.save(productFromDb);
    return modelMapper.map(savedProduct, ProductDTO.class);
  }

  @Override
  public ProductDTO deleteProduct(Long productId) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

    productRepository.delete(product);
    return modelMapper.map(product, ProductDTO.class);
  }

  @Override
  public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
    Product productFromDb =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

    String fileName = fileService.uploadImage(path, image);

    productFromDb.setImage(fileName);

    Product savedProduct = productRepository.save(productFromDb);
    return modelMapper.map(savedProduct, ProductDTO.class);
  }
}
