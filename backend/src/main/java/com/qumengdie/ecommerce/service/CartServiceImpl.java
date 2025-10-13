package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.exceptions.APIException;
import com.qumengdie.ecommerce.exceptions.ResourceNotFoundException;
import com.qumengdie.ecommerce.model.Cart;
import com.qumengdie.ecommerce.model.CartItem;
import com.qumengdie.ecommerce.model.Product;
import com.qumengdie.ecommerce.payload.CartDTO;
import com.qumengdie.ecommerce.payload.CartItemDTO;
import com.qumengdie.ecommerce.payload.ProductDTO;
import com.qumengdie.ecommerce.repositories.CartItemRepository;
import com.qumengdie.ecommerce.repositories.CartRepository;
import com.qumengdie.ecommerce.repositories.ProductRepository;
import com.qumengdie.ecommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {
  @Autowired CartRepository cartRepository;

  @Autowired ProductRepository productRepository;

  @Autowired CartItemRepository cartItemRepository;

  @Autowired AuthUtil authUtil;

  @Autowired ModelMapper modelMapper;

  @Override
  public CartDTO addProductToCart(Long productId, Integer quantity) {
    Cart cart = getOrCreateCart();

    // get product by productId
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

    // check if this product exist in this cart
    CartItem cartItem =
        cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
    if (cartItem != null) {
      throw new APIException("Product" + product.getProductName() + "already exists in the cart");
    }

    // check if enough product in the storage
    if (product.getQuantity() == 0) {
      throw new APIException(product.getProductName() + "is not Available");
    }
    if (product.getQuantity() < quantity) {
      throw new APIException(
          "Please, make an order of the "
              + product.getProductName()
              + "less than or equal to the quantity "
              + quantity);
    }

    // save the new cart item with the product and quantity
    CartItem newCartItem = new CartItem();
    newCartItem.setProduct(product);
    newCartItem.setCart(cart);
    newCartItem.setQuantity(quantity);
    newCartItem.setDiscount(product.getDiscount());
    newCartItem.setProductPrice(product.getSpecialPrice());
    cartItemRepository.save(newCartItem);

    // update the total price for the cart after adding the products
    cart.setTotalPrice(cart.getTotalPrice() + product.getSpecialPrice() * quantity);

    cart.getCartItems().add(newCartItem);
    cartRepository.save(cart);

    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

    // set the quantity for productDTO of the products in the cart
    List<CartItem> cartItems = cart.getCartItems();
    Stream<ProductDTO> productDTOStream =
        cartItems.stream()
            .map(
                item -> {
                  ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                  productDTO.setQuantity(item.getQuantity());
                  return productDTO;
                });

    cartDTO.setProducts(productDTOStream.toList());

    return cartDTO;
  }

  @Override
  public List<CartDTO> getAllCarts() {
    List<Cart> carts = cartRepository.findAll();
    if (carts.isEmpty()) {
      throw new APIException("No cart exists");
    }

    return carts.stream()
        .map(
            cart -> {
              CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
              List<ProductDTO> productDTOS =
                  cart.getCartItems().stream()
                      .map(c -> modelMapper.map(c.getProduct(), ProductDTO.class))
                      .toList();
              cartDTO.setProducts(productDTOS);
              return cartDTO;
            })
        .toList();
  }

  @Override
  public CartDTO getCart(String emailId, Long cartId) {
    Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
    if (cart == null) {
      throw new ResourceNotFoundException("Cart", "cartId", cartId);
    }

    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
    cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
    List<ProductDTO> productDTOS =
        cart.getCartItems().stream()
            .map(c -> modelMapper.map(c.getProduct(), ProductDTO.class))
            .toList();
    cartDTO.setProducts(productDTOS);

    return cartDTO;
  }

  @Override
  @Transactional
  public CartDTO updateProductQuantityInCart(Long productId, int quantity) {
    // get the cart
    String emailId = authUtil.loggedInEmail();
    Cart userCart = cartRepository.findCartByEmail(emailId);
    Long cartId = userCart.getCartId();
    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

    // get the product
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

    // check product quantity
    if (product.getQuantity() == 0) {
      throw new APIException(product.getProductName() + "is not Available");
    }
    if (product.getQuantity() < quantity) {
      throw new APIException(
          "Please, make an order of the "
              + product.getProductName()
              + "less than or equal to the quantity "
              + quantity);
    }

    // get the cart item
    CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
    if (cartItem == null) {
      throw new APIException("Product" + product.getProductName() + "not in the cart!");
    }

    // calculate new quantity
    int newQuantity = cartItem.getQuantity() + quantity;

    // validate new quantity
    if (newQuantity < 0) {
      throw new APIException("The resulting quantity cannot be negative.");
    }

    // delete if quantity is 0
    if (newQuantity == 0) {
      deleteProductFromCart(cartId, productId);
    } else {
      // set the fields for cart item and save
      cartItem.setProductPrice(product.getSpecialPrice());
      cartItem.setQuantity(cartItem.getQuantity() + quantity);
      cartItem.setDiscount(product.getDiscount());
      cart.setTotalPrice(cart.getTotalPrice() + cartItem.getProductPrice() * quantity);
      cartRepository.save(cart);
    }

    // get the saved updated item
    CartItem updatedItem = cartItemRepository.save(cartItem);
    // if quantity is 0, delete this cart item
    if (updatedItem.getQuantity() == 0) {
      cartItemRepository.deleteById(updatedItem.getCartItemId());
    }

    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

    // set the quantity for products in cart items
    List<ProductDTO> productDTOS =
        cart.getCartItems().stream()
            .map(
                item -> {
                  ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                  productDTO.setQuantity(item.getQuantity());
                  return productDTO;
                })
            .toList();
    cartDTO.setProducts(productDTOS);

    return cartDTO;
  }

  @Transactional
  @Override
  public String deleteProductFromCart(Long cartId, Long productId) {
    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

    CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

    if (cartItem == null) {
      throw new ResourceNotFoundException("Product", "productId", productId);
    }

    cart.setTotalPrice(
        cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

    cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

    return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
  }

  @Override
  public void updateProductInCarts(Long cartId, Long productId) {
    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

    CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
    if (cartItem == null) {
      throw new APIException("Product" + product.getProductName() + "not in the cart!");
    }

    // reduce the old price
    double cartPrice =
        cart.getTotalPrice() - cartItem.getProductPrice() * cartItem.getProductPrice();

    cartItem.setProductPrice(product.getSpecialPrice());

    // add the new price
    cart.setTotalPrice(cartPrice + cartItem.getProductPrice() * cartItem.getProductPrice());

    cartItem = cartItemRepository.save(cartItem);
  }

  private Cart getOrCreateCart() {
    Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
    if (userCart != null) {
      return userCart;
    }

    Cart cart = new Cart();
    cart.setTotalPrice(0.0);
    cart.setUser(authUtil.loggedInUser());
    return cartRepository.save(cart);
  }

  @Transactional
  @Override
  public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
    // Get user's email
    String emailId = authUtil.loggedInEmail();

    // Check if an existing cart is available or create a new one
    Cart existingCart = cartRepository.findCartByEmail(emailId);
    if (existingCart == null) {
      existingCart = new Cart();
      existingCart.setTotalPrice(0.00);
      existingCart.setUser(authUtil.loggedInUser());
      existingCart = cartRepository.save(existingCart);
    } else {
      // Clear all current items in the existing cart
      cartItemRepository.deleteAllByCartId(existingCart.getCartId());
    }

    double totalPrice = 0.00;

    // Process each item in the request to add to the cart
    for (CartItemDTO cartItemDTO : cartItems) {
      Long productId = cartItemDTO.getProductId();
      Integer quantity = cartItemDTO.getQuantity();

      // Find the product by ID
      Product product =
          productRepository
              .findById(productId)
              .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

      // Directly update product stock and total price
      totalPrice += product.getSpecialPrice() * quantity;

      // Create and save cart item
      CartItem cartItem = new CartItem();
      cartItem.setProduct(product);
      cartItem.setCart(existingCart);
      cartItem.setQuantity(quantity);
      cartItem.setProductPrice(product.getSpecialPrice());
      cartItem.setDiscount(product.getDiscount());
      cartItemRepository.save(cartItem);
    }

    // Update the cart's total price and save
    existingCart.setTotalPrice(totalPrice);
    cartRepository.save(existingCart);
    return "Cart created/updated with the new items successfully";
  }
}
