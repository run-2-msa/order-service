package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.dto.kafkadto.Field;
import com.example.orderservice.dto.kafkadto.KafkaOrderDto;
import com.example.orderservice.dto.kafkadto.Payload;
import com.example.orderservice.dto.kafkadto.Schema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    List<Field> fields = Arrays.asList(
            new Field("string", true, "order_id"),
            new Field("string", true, "user_id"), // Long - int64
            new Field("string", true, "product_id"), // Long - int64
            new Field("int32", true, "qty"),
            new Field("int64", true, "unit_price"),
            new Field("int64", true, "total_price")
    );

    Schema schema = Schema.builder()
                        .type("struct")
                        .fields(fields)
                        .optional(false)
                        .name("orders")
                        .build();

    public OrderDto send(String topic, OrderDto orderDto){
        var payload = Payload.builder()
                        .orderId(orderDto.getOrderId())
                        .userId(orderDto.getUserId())
                        .productId(orderDto.getProductId())
                        .qty(orderDto.getQty())
                        .unitPrice(orderDto.getUnitPrice())
                        .totalPrice(orderDto.getTotalPrice())
                        .build();

        var kafkaOrderDto = KafkaOrderDto.builder()
                            .schema(schema)
                            .payload(payload)
                            .build();

        String jsonInString = "";
        try {
            jsonInString = objectMapper.writeValueAsString(kafkaOrderDto);
        } catch (JsonProcessingException e) {
            e.getStackTrace();
        }
        kafkaTemplate.send(topic,jsonInString);
        log.info("Kafka Producer sent data from the Order microservice: "+ jsonInString);

        return orderDto;
    }
}
