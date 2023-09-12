package br.com.igormartinez.potygames.data.response;

import java.math.BigDecimal;

public record ShoppingCartItemResponseDTO (
    Long idInventoryItem,
    String name,
    String version,
    String condition,
    BigDecimal price,
    Integer quantity
) {}
