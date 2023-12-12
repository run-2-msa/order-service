package com.example.orderservice.dto.kafkadto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Payload {
    private String orderId;
    private String userId;
    private String productId;
    private int qty;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    // private LocalDateTime createdAt; Timestamp관련 -> CURRENT_TIMESTAMP
}
