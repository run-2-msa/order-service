package com.example.orderservice;

import com.example.orderservice.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@SpringBootTest
class OrderServiceApplicationTests {

	@Test
	void contextLoads() {
		var orderDto = new OrderDto();
		orderDto.setProductId("CATALOG-001");
		orderDto.setQty(2);
		orderDto.setUnitPrice(new BigDecimal("2000"));
		orderDto.setTotalPrice(new BigDecimal("4000"));
		orderDto.setCreatedAt(LocalDateTime.now());
		orderDto.setOrderId(UUID.randomUUID().toString());
		orderDto.setUserId("d0047986-0292-41d3-894b-19702b39de39");
		var objectMapper = new ObjectMapper();

		objectMapper.registerModule(new Jdk8Module()); // 8버전 이후에 나온 클래스들을 처리 해주기 위해서 (Optional같은)
		objectMapper.registerModule(new JavaTimeModule()); // local date같은 애들도 추가....
		// 날짜 관련 직렬화 disable
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		var aa = "";
		try {
			aa = objectMapper.writeValueAsString(orderDto);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		log.info(aa);
	}

}
