package com.event.management.ticketingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private Double amount;
    private String currency;
    private String transactionRef;
}