package com.qumengdie.ecommerce.controller;

import com.qumengdie.ecommerce.model.User;
import com.qumengdie.ecommerce.payload.AddressDTO;
import com.qumengdie.ecommerce.service.AddressService;
import com.qumengdie.ecommerce.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {
  @Autowired AuthUtil authUtil;

  @Autowired AddressService addressService;

  @PostMapping("/addresses")
  public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
    User user = authUtil.loggedInUser();
    AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);
    return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
  }

  @GetMapping("/addresses")
  public ResponseEntity<List<AddressDTO>> getAddresses() {
    List<AddressDTO> addressDTOList = addressService.getAddresses();
    return new ResponseEntity<>(addressDTOList, HttpStatus.OK);
  }

  @GetMapping("/addresses/{addressId}")
  public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
    AddressDTO addressDTO = addressService.getAddressById(addressId);
    return new ResponseEntity<>(addressDTO, HttpStatus.OK);
  }

  @GetMapping("/users/addresses")
  public ResponseEntity<List<AddressDTO>> getUserAddresses() {
    User user = authUtil.loggedInUser();
    List<AddressDTO> addressDTOList = addressService.getUserAddresses(user);
    return new ResponseEntity<>(addressDTOList, HttpStatus.OK);
  }

  @PutMapping("/addresses/{addressId}")
  public ResponseEntity<AddressDTO> updateAddressById(
      @PathVariable Long addressId, @RequestBody AddressDTO addressDTO) {
    AddressDTO updatedAdressDTO = addressService.updateAddressById(addressId, addressDTO);
    return new ResponseEntity<>(updatedAdressDTO, HttpStatus.OK);
  }

  @DeleteMapping("/addresses/{addressId}")
  public ResponseEntity<String> deleteAddressById(@PathVariable Long addressId) {
    String status = addressService.deleteAddressById(addressId);
    return new ResponseEntity<>(status, HttpStatus.OK);
  }
}
