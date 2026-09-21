package com.Volna.User_Service.controller;
import com.Volna.User_Service.dto.AddressRequestDto;
import com.Volna.User_Service.dto.AddressResponseDto;
import com.Volna.User_Service.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponseDto> addAddress(
            @AuthenticationPrincipal String authUserId,
            @Valid @RequestBody AddressRequestDto request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.addAddress(
                        UUID.fromString(authUserId),
                        request
                ));
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAddresses(
            @AuthenticationPrincipal String authUserId) {

        return ResponseEntity.ok(
                addressService.getUserAddresses(
                        UUID.fromString(authUserId)
                )
        );
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddress(
            @AuthenticationPrincipal String authUserId,
            @PathVariable UUID addressId) {

        return ResponseEntity.ok(
                addressService.getAddressById(
                        UUID.fromString(authUserId),
                        addressId
                )
        );
    }

   @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @AuthenticationPrincipal String authUserId,
            @PathVariable UUID addressId,
            @Valid @RequestBody AddressRequestDto request) {

        return ResponseEntity.ok(
                addressService.updateAddress(
                        UUID.fromString(authUserId),
                        addressId,
                        request
                )
        );
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressResponseDto> makeDefault(
            @AuthenticationPrincipal String authUserId,
            @PathVariable UUID addressId) {

        return ResponseEntity.ok(
                addressService.makeDefault(
                        UUID.fromString(authUserId),
                        addressId
                )
        );
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal String authUserId,
            @PathVariable UUID addressId) {

        addressService.deleteAddress(
                UUID.fromString(authUserId),
                addressId
        );

        return ResponseEntity.noContent().build();
    }
}
