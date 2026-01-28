package com.microloan.microloan_issuance.model.response;

import jakarta.validation.constraints.Min;

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
public class DebtResponseModel {
    @Min(value = 0, message = "Не может быть отрицательной задолжности")
    private int sum;
    private String description;
}
