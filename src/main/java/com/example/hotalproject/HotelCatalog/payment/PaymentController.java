package com.example.hotalproject.HotelCatalog.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.hotalproject.security.Role;
import com.example.hotalproject.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management (mock)")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/intent")
    @Operation(summary = "Create a payment intent for a booking")
    public ResponseEntity<PaymentResponse> createPaymentIntent(
            @Valid @RequestBody PaymentIntentRequest request) {
        boolean privileged = SecurityUtils.hasRole(Role.ADMIN) || SecurityUtils.hasRole(Role.MANAGER);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPaymentIntent(request, SecurityUtils.currentUserEmail(), privileged));
    }

    @PostMapping("/{paymentId}/simulate")
    @Operation(summary = "Simulate payment outcome (SUCCESS or FAILED)")
    public ResponseEntity<PaymentResponse> simulatePayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody PaymentSimulateRequest request) {
        boolean privileged = SecurityUtils.hasRole(Role.ADMIN) || SecurityUtils.hasRole(Role.MANAGER);
        return ResponseEntity.ok(paymentService.simulatePayment(paymentId, request, SecurityUtils.currentUserEmail(), privileged));
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund a successful payment (only for cancelled bookings)")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long paymentId) {
        boolean privileged = SecurityUtils.hasRole(Role.ADMIN) || SecurityUtils.hasRole(Role.MANAGER);
        return ResponseEntity.ok(paymentService.refundPayment(paymentId, SecurityUtils.currentUserEmail(), privileged));
    }
    @GetMapping("/{paymentId}")
    @Operation(summary = "Get Payment with Id")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long paymentId) {
        boolean privileged = SecurityUtils.hasRole(Role.ADMIN) || SecurityUtils.hasRole(Role.MANAGER);
        return ResponseEntity.ok(paymentService.getPayment(paymentId, SecurityUtils.currentUserEmail(), privileged));
    }
    @GetMapping("")
    @Operation(summary = "Get Payment with Id")
    public ResponseEntity<List<PaymentResponse>> getPayment() {
        boolean privileged = SecurityUtils.hasRole(Role.ADMIN) || SecurityUtils.hasRole(Role.MANAGER);
        return ResponseEntity.ok(paymentService.getPayments(SecurityUtils.currentUserEmail(), privileged));
    }
}