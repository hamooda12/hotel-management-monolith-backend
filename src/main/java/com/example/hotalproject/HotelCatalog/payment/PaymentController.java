package com.example.hotalproject.HotelCatalog.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management (mock)")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/intent")
    @Operation(summary = "Create a payment intent for a booking")
    public ResponseEntity<PaymentResponse> createPaymentIntent(
            @Valid @RequestBody PaymentIntentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPaymentIntent(request));
    }

    @PostMapping("/{paymentId}/simulate")
    @Operation(summary = "Simulate payment outcome (SUCCESS or FAILED)")
    public ResponseEntity<PaymentResponse> simulatePayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody PaymentSimulateRequest request) {
        return ResponseEntity.ok(paymentService.simulatePayment(paymentId, request));
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund a successful payment (only for cancelled bookings)")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId));
    }
}