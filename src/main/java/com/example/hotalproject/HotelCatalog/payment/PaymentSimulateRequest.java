package com.example.hotalproject.HotelCatalog.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSimulateRequest {

    @NotNull(message = "Outcome is required")
    @Pattern(regexp = "SUCCESS|FAILED", message = "Outcome must be SUCCESS or FAILED")
    private String outcome;
}