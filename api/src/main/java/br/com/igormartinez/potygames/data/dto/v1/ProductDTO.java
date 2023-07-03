package br.com.igormartinez.potygames.data.dto.v1;

public record ProductDTO(
    Long id,
    Long idProductType,
    String name,
    String description
) {}
