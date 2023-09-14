package br.com.igormartinez.potygames.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.igormartinez.potygames.data.request.OrderRequestDTO;
import br.com.igormartinez.potygames.data.response.OrderDetailResponseDTO;
import br.com.igormartinez.potygames.data.response.OrderResponseDTO;
import br.com.igormartinez.potygames.data.validation.annotations.PositiveNotNull;
import br.com.igormartinez.potygames.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/order")
public class OrderController {

    @Autowired
    OrderService service;

    @GetMapping
    public List<OrderDetailResponseDTO> findAll() {
        return service.findAllByUser();
    }

    @GetMapping("/{order-id}")
    public OrderDetailResponseDTO findById(
            @PathVariable(name = "order-id") @PositiveNotNull(message = "The order-id must be a positive number.") Long orderId) {
        return service.findById(orderId);
    }

    @Operation(
        summary = "Creates a new order", 
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Conflict", responseCode = "409", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
    @PostMapping
    public OrderResponseDTO createOrder(@RequestBody @Valid OrderRequestDTO request) {
        return service.createOrder(request);
    }

    @Operation(
        summary = "Cancel a order", 
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
    @PutMapping("/{order-id}/cancel")
    public OrderResponseDTO cancelOrder(
            @PathVariable(name = "order-id") @PositiveNotNull(message = "The order-id must be a positive number.") Long orderId) {
        return service.cancelOrder(orderId);
    }
}
