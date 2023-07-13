package br.com.igormartinez.potygames.data.dto.v1;

import java.math.BigDecimal;

public record InventoryItemDTO (
    Long id,
    Long product,
    Long yugiohCard,
    String version,
    String condition,
    BigDecimal price,
    Integer quantity
) {}
