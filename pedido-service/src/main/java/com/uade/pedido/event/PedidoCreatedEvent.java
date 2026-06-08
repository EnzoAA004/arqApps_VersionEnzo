package com.uade.pedido.event;

public class PedidoCreatedEvent {

    private Long pedidoId;

    public PedidoCreatedEvent() {
    }

    public PedidoCreatedEvent(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }
}
