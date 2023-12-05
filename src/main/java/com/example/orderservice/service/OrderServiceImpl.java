package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.jpa.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(new BigDecimal(orderDto.getQty()).multiply(orderDto.getUnitPrice()));


        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        var orderEntity = mapper.map(orderDto, OrderEntity.class);
        orderEntity.setCreatedAt(LocalDateTime.now());

        var newOrderEntity = orderRepository.save(orderEntity);

        return mapper.map(newOrderEntity, OrderDto.class);
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        var orderEntity = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("주문 내역이 없습니다."));

        return new ModelMapper().map(orderEntity, OrderDto.class);
    }

    @Override
    public List<OrderEntity> getAllOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
