package br.com.igormartinez.potygames.data.response;

import java.math.BigDecimal;

public record InventoryItemDTO (
    Long id,
    Long product,
    String version,
    String condition,
    BigDecimal price,
    Integer quantity
) {}
