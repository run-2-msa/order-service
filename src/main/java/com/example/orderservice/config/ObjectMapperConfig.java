package com.example.orderservice.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper(){

        var objectMapper = new ObjectMapper();

        objectMapper.registerModule(new Jdk8Module()); // 8버전 이후에 나온 클래스들을 처리 해주기 위해서 (Optional같은)
        objectMapper.registerModule(new JavaTimeModule()); // local date같은 애들 모듈 추가
        // 날짜 관련 직렬화 disable
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }
}