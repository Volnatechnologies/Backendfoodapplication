package com.Volna.User_Service.service;

import com.Volna.User_Service.dto.AddressRequestDto;
import com.Volna.User_Service.dto.AddressResponseDto;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    AddressResponseDto addAddress(
            UUID authUserId,
            AddressRequestDto request
    );

    List<AddressResponseDto> getUserAddresses(
            UUID authUserId
    );

    AddressResponseDto getAddressById(
            UUID authUserId,
            UUID addressId
    );

    AddressResponseDto updateAddress(
            UUID authUserId,
            UUID addressId,
            AddressRequestDto request
    );

    AddressResponseDto makeDefault(
            UUID authUserId,
            UUID addressId
    );

    void deleteAddress(
            UUID authUserId,
            UUID addressId
    );
}