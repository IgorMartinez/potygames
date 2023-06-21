package br.com.igormartinez.potygames.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.igormartinez.potygames.data.dto.v1.UserAddressDTO;
import br.com.igormartinez.potygames.services.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("api/v1/user/{user-id}/address")
public class UserAddressController {

    @Autowired
    UserAddressService service;

    @Operation(
        summary = "Find all address of a given user",
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @GetMapping
    public List<UserAddressDTO> findAllAddressesByIdUser(@PathVariable(value = "user-id") Long idUser){
        return service.findAllByIdUser(idUser);
    }

    @Operation(
        summary = "Find a address of a user by id",
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @GetMapping("/{address-id}")
    public UserAddressDTO findAddressById(
        @PathVariable(value = "user-id") Long idUser,  
        @PathVariable(value = "address-id") Long idAddress){
        return service.findById(idUser, idAddress);
    }

    @Operation(
        summary = "Creates a new address to a user",
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @PostMapping
    public UserAddressDTO createAddress(
        @PathVariable(value = "user-id") Long idUser,
        @RequestBody UserAddressDTO userAddressDTO) {
        return service.create(idUser, userAddressDTO);
    }

    @Operation(
        summary = "Updates a address of a user",
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @PutMapping("/{address-id}")
    public UserAddressDTO updateAddress(
        @PathVariable(value = "user-id") Long idUser,
        @PathVariable(value = "address-id") Long idAddress,
        @RequestBody UserAddressDTO userAddressDTO) {
        return service.update(idUser, idAddress, userAddressDTO);
    }

    @Operation(
        summary = "Deletes a address of a user",
        responses = {
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @DeleteMapping("/{address-id}")
    public ResponseEntity<?> deleteAddress(
        @PathVariable(value = "user-id") Long idUser,  
        @PathVariable(value = "address-id") Long idAddress) {
        service.delete(idUser, idAddress);
        return ResponseEntity.noContent().build();
    }
}
