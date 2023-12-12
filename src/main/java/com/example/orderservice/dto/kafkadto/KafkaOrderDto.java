package com.example.orderservice.dto.kafkadto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class KafkaOrderDto {
    private Schema schema;
    private Payload payload;
}
