package br.com.igormartinez.potygames.data.request;

import jakarta.validation.constraints.NotBlank;

public record AccountCredentials (
    @NotBlank(message = "The username must be not blank.")
    String username,

    @NotBlank(message = "The password must be not blank.")
    String password
) {}
