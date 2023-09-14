package br.com.igormartinez.potygames.data.request;

import java.util.List;

import br.com.igormartinez.potygames.data.validation.annotations.NotDuplicated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDTO (

    @NotEmpty(message = "The items of order must be provided.")
    @NotDuplicated(message = "The list of items cannot have duplicated elements.")
    List<@Valid OrderItemResquestDTO> items,

    @NotNull(message = "The billing address of order must be provided.")
    @Valid
    OrderAddressRequestDTO billingAddress,
    
    @NotNull(message = "The delivery address of order must be provided.")
    @Valid
    OrderAddressRequestDTO deliveryAddress
) {}
