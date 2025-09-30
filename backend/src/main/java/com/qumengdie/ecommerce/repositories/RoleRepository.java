package com.qumengdie.ecommerce.repositories;

import com.qumengdie.ecommerce.model.AppRole;
import com.qumengdie.ecommerce.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByRoleName(AppRole appRole);
}
