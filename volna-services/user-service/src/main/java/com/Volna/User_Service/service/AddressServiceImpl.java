package com.Volna.User_Service.service;
import com.Volna.User_Service.dto.AddressRequestDto;
import com.Volna.User_Service.dto.AddressResponseDto;
import com.Volna.User_Service.entity.Address;
import com.Volna.User_Service.entity.UserProfile;
import com.Volna.User_Service.exception.ResourceNotFoundException;
import com.Volna.User_Service.repository.AddressRepository;
import com.Volna.User_Service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public AddressResponseDto addAddress(
            UUID authUserId,
            AddressRequestDto request) {

        UserProfile userProfile = getUserProfile(authUserId);

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.resetDefaultAddresses(userProfile.getId());
        }

        Address address = Address.builder()
                .userProfile(userProfile)
                .addressType(request.getAddressType())
                .houseNumber(request.getHouseNumber())
                .streetAddress(request.getStreetAddress())
                .landmark(request.getLandmark())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isDefault(request.getIsDefault())
                .build();

        Address savedAddress = addressRepository.save(address);

        return mapToResponseDto(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDto> getUserAddresses(
            UUID authUserId) {

        UserProfile userProfile = getUserProfile(authUserId);

        return addressRepository
                .findByUserProfileId(userProfile.getId())
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponseDto getAddressById(
            UUID authUserId,
            UUID addressId) {

        UserProfile userProfile = getUserProfile(authUserId);

        Address address = addressRepository
                .findByIdAndUserProfileId(
                        addressId,
                        userProfile.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with ID: " + addressId
                        )
                );

        return mapToResponseDto(address);
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(
            UUID authUserId,
            UUID addressId,
            AddressRequestDto request) {

        UserProfile userProfile = getUserProfile(authUserId);

        Address address = addressRepository
                .findByIdAndUserProfileId(
                        addressId,
                        userProfile.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with ID: " + addressId
                        )
                );

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.resetDefaultAddresses(
                    userProfile.getId()
            );
        }

        address.setAddressType(request.getAddressType());
        address.setHouseNumber(request.getHouseNumber());
        address.setStreetAddress(request.getStreetAddress());
        address.setLandmark(request.getLandmark());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setIsDefault(request.getIsDefault());

        Address updatedAddress = addressRepository.save(address);

        return mapToResponseDto(updatedAddress);
    }

    @Override
    @Transactional
    public AddressResponseDto makeDefault(
            UUID authUserId,
            UUID addressId) {

        UserProfile userProfile = getUserProfile(authUserId);

        Address address = addressRepository
                .findByIdAndUserProfileId(
                        addressId,
                        userProfile.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with ID: " + addressId
                        )
                );

        addressRepository.resetDefaultAddresses(
                userProfile.getId()
        );

        address.setIsDefault(true);

        Address savedAddress = addressRepository.save(address);

        return mapToResponseDto(savedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(
            UUID authUserId,
            UUID addressId) {

        UserProfile userProfile = getUserProfile(authUserId);

        Address address = addressRepository
                .findByIdAndUserProfileId(
                        addressId,
                        userProfile.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with ID: " + addressId
                        )
                );

        addressRepository.delete(address);
    }

    private UserProfile getUserProfile(UUID authUserId) {

        return userProfileRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User profile not found for ID: "
                                        + authUserId
                        )
                );
    }

    private AddressResponseDto mapToResponseDto(
            Address address) {

        return AddressResponseDto.builder()
                .id(address.getId())
                .addressType(address.getAddressType())
                .houseNumber(address.getHouseNumber())
                .streetAddress(address.getStreetAddress())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .isDefault(address.getIsDefault())
                .build();
    }
}