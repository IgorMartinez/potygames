package br.com.igormartinez.potygames.data.request;

import br.com.igormartinez.potygames.data.validation.annotations.PositiveNotNull;

public record ShoppingCartItemRequestDTO (
    @PositiveNotNull(message = "The id of inventory item must be a positive number.")
    Long idInventoryItem,

    @PositiveNotNull(message = "The quantity must be positive number.")
    Integer quantity
) {}
