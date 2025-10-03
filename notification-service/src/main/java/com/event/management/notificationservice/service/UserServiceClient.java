package com.event.management.notificationservice.service;

import com.event.management.notificationservice.dto.UserDetailsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", url = "${user-service.url:http://user-service:8081}")
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{id}")
    UserDetailsDto getUserById(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("id") Long id);
}