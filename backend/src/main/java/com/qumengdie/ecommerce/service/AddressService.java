package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.model.User;
import com.qumengdie.ecommerce.payload.AddressDTO;

import java.util.List;

public interface AddressService {
  AddressDTO createAddress(AddressDTO addressDTO, User user);

  List<AddressDTO> getAddresses();

  AddressDTO getAddressById(Long addressId);

  List<AddressDTO> getUserAddresses(User user);

  AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO);

  String deleteAddressById(Long addressId);
}
