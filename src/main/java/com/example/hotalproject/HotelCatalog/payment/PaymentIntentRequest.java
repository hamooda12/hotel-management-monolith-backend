package com.example.hotalproject.HotelCatalog.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;
}