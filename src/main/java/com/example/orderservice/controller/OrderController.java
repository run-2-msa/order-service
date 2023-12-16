package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.OrderRequest;
import com.example.orderservice.vo.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final Environment env;
    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;
    private final OrderProducer orderProducer;

    @GetMapping("/health-check")
    public String status(){
        return String.format("It's Working in Order Service on PORT %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable String userId,
                                                     @RequestBody @Valid OrderRequest orderRequest){

        log.info("Before call createOrder order data");
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        var orderDto = mapper.map(orderRequest, OrderDto.class);
        orderDto.setUserId(userId);

        /*var newOrderDto = orderService.createOrder(orderDto);
        var response =  mapper.map(newOrderDto, OrderResponse.class);*/

        // kafka /
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(new BigDecimal(orderDto.getQty()).multiply(orderDto.getUnitPrice()));
        // orderDto.setCreatedAt(LocalDateTime.now()); CURRENT_TIMESTAMP

        //orderProducer.send("orders", orderDto); // sink db
        // send this order to kafka
        kafkaProducer.send("example-catalog-topic", orderDto); // producer
        var response =  mapper.map(orderDto, OrderResponse.class);

        log.info("After call createOrder order data");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping("/users/{userId}") // 음..
    public ResponseEntity<List<OrderResponse>> getOrderList(@PathVariable String userId){
        log.info("Before call getOrder order data");
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
       /* try{
            Thread.sleep(1000);
            throw new RuntimeException("장애 발생!!");
        } catch (InterruptedException e) {
            log.warn(e.getMessage());
        }*/
        log.info("After call getOrder order data");
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
