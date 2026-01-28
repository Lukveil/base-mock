package com.microloan.microloan_issuance.model.request;

import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PaymentIdRequestModel {
    private String transaction_id;
    @Positive
    private float sum;
    private boolean need_processing;
}
