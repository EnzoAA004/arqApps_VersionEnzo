package com.uade.pedido.service;

import com.uade.pedido.config.KafkaConfig;
import com.uade.pedido.dto.PedidoRequest;
import com.uade.pedido.entity.Pedido;
import com.uade.pedido.event.PedidoCreatedEvent;
import com.uade.pedido.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final KafkaTemplate<String, PedidoCreatedEvent> kafkaTemplate;

    public PedidoService(PedidoRepository pedidoRepository,
                         KafkaTemplate<String, PedidoCreatedEvent> kafkaTemplate) {
        this.pedidoRepository = pedidoRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Pedido create(PedidoRequest request) {
        Pedido pedido = new Pedido();
        pedido.setDescripcion(request.descripcion());
        pedido.setCantidad(request.cantidad());
        pedido.setEstado("PENDIENTE");
        Pedido saved = pedidoRepository.save(pedido);
        kafkaTemplate.send(KafkaConfig.TOPIC, String.valueOf(saved.getId()), new PedidoCreatedEvent(saved.getId()));
        return saved;
    }

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    @KafkaListener(topics = KafkaConfig.TOPIC, groupId = "pedido-service-group")
    @Transactional
    public void confirm(PedidoCreatedEvent event) throws InterruptedException {
        Thread.sleep(3500);
        pedidoRepository.findById(event.getPedidoId()).ifPresent(pedido -> {
            pedido.setEstado("CONFIRMADO");
            pedidoRepository.save(pedido);
            log.info("Pedido confirmado por Kafka: id={}", pedido.getId());
        });
    }
}
