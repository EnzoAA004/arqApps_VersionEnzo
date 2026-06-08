package com.uade.pedido.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PedidoRequest(
        @NotBlank
        @Size(max = 255)
        String descripcion,

        @Min(1)
        @Max(1000)
        int cantidad
) {
}
