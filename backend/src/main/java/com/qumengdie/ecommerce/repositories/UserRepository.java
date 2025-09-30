package com.qumengdie.ecommerce.repositories;

import com.qumengdie.ecommerce.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUserName(String username);

  boolean existsByUserName(@NotBlank String username);

  boolean existsByEmail(@NotBlank String username);
}
