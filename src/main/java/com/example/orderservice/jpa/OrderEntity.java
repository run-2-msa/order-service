package com.example.orderservice.jpa;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String productId;

    @Column(nullable = false)
    private Integer qty;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false, length = 120)
    private String userId;

    @Column(nullable = false, length = 120, unique = true)
    private String orderId;

    // @Column(nullable = false, updatable = false, insertable = false)
    // @ColumnDefault(value = "CURRENT_TIMESTAMP") // h2db, MySql v5.6.5 >=
    private LocalDateTime createdAt;
}
