package com.qumengdie.ecommerce.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
  @NotBlank private String username;
  @NotBlank @Email private String email;
  private Set<String> role;
  @NotBlank private String password;
}
