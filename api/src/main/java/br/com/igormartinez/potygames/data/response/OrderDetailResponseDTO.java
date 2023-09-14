package br.com.igormartinez.potygames.data.response;

import java.math.BigDecimal;
import java.util.List;

public record OrderDetailResponseDTO(
    Long id,
    String status,
    BigDecimal totalPrice,
    List<OrderItemResponseDTO> items,
    OrderAddressResponseDTO billingAddress,
    OrderAddressResponseDTO deliveryAddress
) {}
