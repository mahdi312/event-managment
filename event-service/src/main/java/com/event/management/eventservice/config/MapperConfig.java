package com.event.management.eventservice.config;

import com.event.management.eventservice.mapper.EventMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public EventMapper eventMapper() {
        return Mappers.getMapper(EventMapper.class);
    }
}


