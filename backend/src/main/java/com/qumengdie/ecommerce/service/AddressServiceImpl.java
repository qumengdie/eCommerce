package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.exceptions.ResourceNotFoundException;
import com.qumengdie.ecommerce.model.Address;
import com.qumengdie.ecommerce.model.User;
import com.qumengdie.ecommerce.payload.AddressDTO;
import com.qumengdie.ecommerce.repositories.AddressRepository;
import com.qumengdie.ecommerce.repositories.UserRepository;
import com.qumengdie.ecommerce.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

  @Autowired AuthUtil authUtil;

  @Autowired ModelMapper modelMapper;

  @Autowired AddressRepository addressRepository;

  @Autowired UserRepository userRepository;

  @Override
  public AddressDTO createAddress(AddressDTO addressDTO, User user) {
    Address address = modelMapper.map(addressDTO, Address.class);

    List<Address> addresses = user.getAddresses();
    addresses.add(address);
    user.setAddresses(addresses);

    address.setUser(user);
    Address savedAddress = addressRepository.save(address);

    return modelMapper.map(address, AddressDTO.class);
  }

  @Override
  public List<AddressDTO> getAddresses() {
    List<Address> addresses = addressRepository.findAll();
    return addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
  }

  @Override
  public AddressDTO getAddressById(Long addressId) {
    Address address =
        addressRepository
            .findById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Address", "adressId", addressId));

    return modelMapper.map(address, AddressDTO.class);
  }

  @Override
  public List<AddressDTO> getUserAddresses(User user) {
    List<Address> addresses = user.getAddresses();
    return addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
  }

  @Override
  public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {
    Address addressFromDb =
        addressRepository
            .findById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Address", "adressId", addressId));

    addressFromDb.setStreet(addressDTO.getStreet());
    addressFromDb.setBuildingName(addressDTO.getBuildingName());
    addressFromDb.setCity(addressDTO.getCity());
    addressFromDb.setState(addressDTO.getState());
    addressFromDb.setCountry(addressDTO.getCountry());
    addressFromDb.setZipCode(addressDTO.getZipCode());

    Address updatedAddress = addressRepository.save(addressFromDb);

    User user = addressFromDb.getUser();
    user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
    user.getAddresses().add(updatedAddress);
    userRepository.save(user);

    return modelMapper.map(updatedAddress, AddressDTO.class);
  }

  @Override
  public String deleteAddressById(Long addressId) {
    Address addressFromDb =
        addressRepository
            .findById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

    User user = addressFromDb.getUser();
    user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
    userRepository.save(user);

    addressRepository.delete(addressFromDb);

    return "Address with address Id " + addressId + " deleted!";
  }
}
