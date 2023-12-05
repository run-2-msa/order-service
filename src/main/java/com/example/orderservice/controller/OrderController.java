package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.OrderRequest;
import com.example.orderservice.vo.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final Environment env;
    private final OrderService orderService;

    @GetMapping("/health-check")
    public String status(){
        return String.format("It's Working in Order Service on PORT %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable String userId,
                                                     @RequestBody @Valid OrderRequest orderRequest){

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        var orderDto = mapper.map(orderRequest, OrderDto.class);
        orderDto.setUserId(userId);
        var newOrderDto = orderService.createOrder(orderDto);

        var response =  mapper.map(newOrderDto, OrderResponse.class);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping("/users/{userId}") // 음..
    public ResponseEntity<List<OrderResponse>> getOrderList(@PathVariable String userId){

        List<OrderEntity> orderList = orderService.getAllOrdersByUserId(userId);

        var response = orderList.stream().map(it->{
            return OrderResponse.builder()
                    .orderId(it.getOrderId())
                    .productId(it.getProductId())
                    .qty(it.getQty())
                    .unitPrice(it.getUnitPrice())
                    .totalPrice(it.getTotalPrice())
                    .createdAt(it.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId){

        OrderDto orderDto = orderService.getOrderByOrderId(orderId);

        var response =  OrderResponse.builder()
                .orderId(orderDto.getOrderId())
                .productId(orderDto.getProductId())
                .qty(orderDto.getQty())
                .unitPrice(orderDto.getUnitPrice())
                .totalPrice(orderDto.getTotalPrice())
                .createdAt(orderDto.getCreatedAt())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
