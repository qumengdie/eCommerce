package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.exceptions.APIException;
import com.qumengdie.ecommerce.exceptions.ResourceNotFoundException;
import com.qumengdie.ecommerce.model.Category;
import com.qumengdie.ecommerce.model.Product;
import com.qumengdie.ecommerce.payload.ProductDTO;
import com.qumengdie.ecommerce.payload.ProductResponse;
import com.qumengdie.ecommerce.repositories.CategoryRepository;
import com.qumengdie.ecommerce.repositories.ProductRepository;
import java.io.IOException;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
  public ProductResponse getAllProducts(
      Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
    Sort sortByAndOrder =
        sortOrder.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
    Page<Product> productPage = productRepository.findAll(pageDetails);

    List<Product> products = productPage.getContent();

    if (products.isEmpty()) {
      throw new APIException("No Product exists");
    }

    List<ProductDTO> productDTOS =
        products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

    ProductResponse productResponse = new ProductResponse();
    productResponse.setContent(productDTOS);
    productResponse.setPageNumber(productPage.getNumber());
    productResponse.setPageSize(productPage.getSize());
    productResponse.setTotalElements(productPage.getTotalElements());
    productResponse.setTotalPages(productPage.getTotalPages());
    productResponse.setLastPage(productPage.isLast());
    return productResponse;
  }

  @Override
  public ProductResponse searchByCategory(
      Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

    Sort sortByAndOrder =
        sortOrder.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
    Page<Product> productPage =
        productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);

    List<Product> products = productPage.getContent();

    if (products.isEmpty()) {
      throw new APIException(category.getCategoryName() + "category does not have any product");
    }

    List<ProductDTO> productDTOS =
        products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

    ProductResponse productResponse = new ProductResponse();
    productResponse.setContent(productDTOS);
    productResponse.setPageNumber(productPage.getNumber());
    productResponse.setPageSize(productPage.getSize());
    productResponse.setTotalElements(productPage.getTotalElements());
    productResponse.setTotalPages(productPage.getTotalPages());
    productResponse.setLastPage(productPage.isLast());
    return productResponse;
  }

  @Override
  public ProductResponse searchByKeyword(
      String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
    Sort sortByAndOrder =
        sortOrder.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
    Page<Product> productPage =
        productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);

    List<Product> products = productPage.getContent();

    if (products.isEmpty()) {
      throw new APIException("No Product exists with keyword " + keyword);
    }

    List<ProductDTO> productDTOS =
        products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

    ProductResponse productResponse = new ProductResponse();
    productResponse.setContent(productDTOS);
    productResponse.setPageNumber(productPage.getNumber());
    productResponse.setPageSize(productPage.getSize());
    productResponse.setTotalElements(productPage.getTotalElements());
    productResponse.setTotalPages(productPage.getTotalPages());
    productResponse.setLastPage(productPage.isLast());
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
