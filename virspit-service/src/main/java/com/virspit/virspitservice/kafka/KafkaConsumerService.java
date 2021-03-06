package com.virspit.virspitservice.kafka;

import com.virspit.virspitservice.domain.product.dto.Event;
import com.virspit.virspitservice.domain.product.dto.ProductDto;
import com.virspit.virspitservice.domain.product.dto.ProductKafkaDto;
import com.virspit.virspitservice.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ProductService productService;

    @KafkaListener(topics = "${kafka-topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "productContainer")
    public void listenProductGroup(ProductKafkaDto productKafkaDto) throws IOException {
        log.info("Consumer Message is : {}", productKafkaDto);
        if(Event.DELETE.equals(productKafkaDto.getEvent())) {
            productService.deleteProduct(productKafkaDto.getId());
            return;
        }
        productService.insert(productKafkaDto);
    }

}
