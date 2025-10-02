package com.qumengdie.ecommerce.repositories;

import com.qumengdie.ecommerce.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUserName(String username);

  boolean existsByUserName(@NotBlank String username);

  boolean existsByEmail(@NotBlank String username);
}
