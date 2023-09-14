package br.com.igormartinez.potygames.data.response;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
    Long id,
    String name,
    String version,
    String condition,
    BigDecimal unitPrice,
    Integer quantity
) {}
