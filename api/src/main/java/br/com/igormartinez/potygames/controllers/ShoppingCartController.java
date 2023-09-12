package br.com.igormartinez.potygames.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.igormartinez.potygames.data.request.ShoppingCartItemRequestDTO;
import br.com.igormartinez.potygames.data.response.ShoppingCartItemResponseDTO;
import br.com.igormartinez.potygames.data.validation.annotations.PositiveNotNull;
import br.com.igormartinez.potygames.services.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user/{user-id}/shopping-cart")
@Validated
public class ShoppingCartController {

    @Autowired
    ShoppingCartService service;

    @Operation(
        summary = "Get all items in a user's shopping cart.",
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @GetMapping
    public List<ShoppingCartItemResponseDTO> findAllByUser(
        @PathVariable(name = "user-id") @PositiveNotNull(message = "The user-id must be a positive number.") Long idUser) {
        return service.findAllByUser(idUser);
    }

    @Operation(
        summary = "Add a item in user's shopping cart.",
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Conflict", responseCode = "409", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @PostMapping
    public ShoppingCartItemResponseDTO addItemToCart(
        @PathVariable(name = "user-id") @PositiveNotNull(message = "The user-id must be a positive number.") Long idUser, 
        @RequestBody @Valid ShoppingCartItemRequestDTO itemDTO) {
        return service.addItemToCart(idUser, itemDTO);
    }

    @Operation(
        summary = "Update a item in user's shopping cart.",
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @PutMapping("/{inventory-item-id}")
    public ShoppingCartItemResponseDTO updateItemInCart(
        @PathVariable(name = "user-id") @PositiveNotNull(message = "The user-id must be a positive number.") Long idUser, 
        @PathVariable(name = "inventory-item-id") @PositiveNotNull(message = "The inventory-item-id must be a positive number.") Long idInventoryItem,
        @RequestBody @Valid ShoppingCartItemRequestDTO item) {
        return service.updateItemInCart(idUser, idInventoryItem, item);
    }

    @Operation(
        summary = "Remove a item from user's shopping cart.",
        responses = {
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @DeleteMapping("/{inventory-item-id}")
    public ResponseEntity<?> removeItemFromCart(
        @PathVariable(name = "user-id") @PositiveNotNull(message = "The user-id must be a positive number.") Long idUser,
        @PathVariable(name = "inventory-item-id") @PositiveNotNull(message = "The inventory-item-id must be a positive number.") Long idInventoryItem) {
        
        service.removeItemFromCart(idUser, idInventoryItem);
        return ResponseEntity.noContent().build();
    }
}
