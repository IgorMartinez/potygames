package br.com.igormartinez.potygames.data.response;

public record ProductDTO(
    Long id,
    Long idProductType,
    String name,
    String description
) {}
