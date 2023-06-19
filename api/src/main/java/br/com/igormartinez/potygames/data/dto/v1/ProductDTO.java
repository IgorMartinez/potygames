package br.com.igormartinez.potygames.data.dto.v1;

import java.math.BigDecimal;

public record ProductDTO(
    Long id,
    Long idProductType,
    String name,
    String altName,
    BigDecimal price,
    Integer quantity
) {}
