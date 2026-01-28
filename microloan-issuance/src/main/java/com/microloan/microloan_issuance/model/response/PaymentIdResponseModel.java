package com.microloan.microloan_issuance.model.response;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PaymentIdResponseModel {
    private String transaction_id;
    @Size(min = 10, message = "Имя должно содержать минимум 10 символа")
    private String bank_bik;
    private String status;
    List<ContactResponseModel> contactResponseModelList;
    
}

