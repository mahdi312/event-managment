package com.event.management.ticketingservice.service;

import com.event.management.ticketingservice.dto.PaymentRequest;
import com.event.management.ticketingservice.dto.PaymentResponse;
import com.event.management.ticketingservice.dto.PaymentStatus;
import com.event.management.ticketingservice.exception.PaymentFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PaymentService {

    private final Random random = new Random();

    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for amount: {} with transaction ref: {}",
                request.getAmount(),
                request.getTransactionRef()
        );

        try {
            TimeUnit.SECONDS.sleep(1 + random.nextInt(2));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment processing interrupted", e);
        }

        if ("1234123412341234".equals(request.getCardNumber())) {
            log.warn("Payment failed for transaction ref: {}", request.getTransactionRef());
            throw new PaymentFailedException("Payment failed because of invalid card number.");
        }

        if (random.nextBoolean()) {
            log.info("Payment successful for transaction ref: {}", request.getTransactionRef());
            return PaymentResponse.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .message("Payment processed successfully.")
                    .build();
        } else {
            log.warn("Payment failed for transaction ref: {}", request.getTransactionRef());
            throw new PaymentFailedException("Payment gateway declined the transaction.");
        }
    }
}