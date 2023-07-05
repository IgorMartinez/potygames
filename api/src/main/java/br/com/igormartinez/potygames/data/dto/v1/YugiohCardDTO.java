package br.com.igormartinez.potygames.data.dto.v1;

import java.util.List;

public record YugiohCardDTO(
    Long id,
    String name,
    Long category,
    Long type,
    String attribute,
    Integer levelRankLink,
    String effectLoreText,
    Integer pendulumScale,
    List<String> linkArrows,
    Integer atk,
    Integer def
) {}
