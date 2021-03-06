package com.virspit.virspitproduct.domain.product.kafka;

import com.virspit.virspitproduct.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderConsumer {

    private final ProductService productService;

    @KafkaListener(topics = "${spring.kafka.topic.order}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "orderFactory")
    public void consumeOrder(OrderDto orderDto) {
        log.info("Ordered product id={}", orderDto);
        productService.decreaseRemainedCount(orderDto.getProduct().getId());
    }
}
