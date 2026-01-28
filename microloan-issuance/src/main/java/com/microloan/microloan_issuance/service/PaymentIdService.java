package com.microloan.microloan_issuance.service;

import com.microloan.microloan_issuance.model.response.*;
import io.micrometer.core.annotation.Timed;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentIdService {
    // ThreadLocal для StringBuilders (если генерируем много строк)
    private static final ThreadLocal<StringBuilder> WORD_BUILDER =
            ThreadLocal.withInitial(() -> new StringBuilder(8)); // max word length

    private static final ThreadLocal<StringBuilder> BIK_BUILDER =
            ThreadLocal.withInitial(() -> new StringBuilder(138)); // max BIK length

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Timed(value = "getAceptedTransaction_PaymentIdService", description = "")
    public PaymentIdResponseModel getAceptedTransaction(String transactionId, int sum_bankBik) {
        return PaymentIdResponseModel.builder()
                .transaction_id(transactionId)
                .bank_bik(generateBankBikOptimized())
                .status("ACCEPTED")
                .contactResponseModelList(List.of(
                        ContactResponseModel.builder()
                                .name("HL pay company")
                                .telecom(generateTelecomListOptimized(sum_bankBik))
                                .build()
                ))
                .build();
    }

    private List<String> generateTelecomListOptimized(int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }

        // Более быстрая генерация через pre-allocated array
        String[] array = new String[count];
        for (int i = 0; i < count; i++) {
            array[i] = generateWordOptimized();
        }
        return Arrays.asList(array);
    }

    private String generateWordOptimized() {
        int length = 4 + RANDOM.nextInt(5); // 4-8 символов

        // ThreadLocal StringBuilder (избегаем создания новых)
        StringBuilder sb = WORD_BUILDER.get();
        sb.setLength(0);

        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + RANDOM.nextInt(26)));
        }
        return sb.toString();
    }

    private String generateBankBikOptimized() {
        int length = 10 + RANDOM.nextInt(128);

        //ThreadLocal StringBuilder
        StringBuilder sb = BIK_BUILDER.get();
        sb.setLength(0);

        for (int i = 0; i < length; i++) {
            sb.append((char) ('0' + RANDOM.nextInt(10)));
        }
        return sb.toString();
    }
}
