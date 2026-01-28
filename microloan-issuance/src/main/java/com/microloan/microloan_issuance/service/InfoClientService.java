package com.microloan.microloan_issuance.service;

import com.microloan.microloan_issuance.model.response.DebtResponseModel;
import com.microloan.microloan_issuance.model.response.InfoClientResponseModel;
import io.micrometer.core.annotation.Timed;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.microloan.microloan_issuance.utils.DescriptionGen.generateRandomDescription;

@Service
public class InfoClientService {

    @Timed(value = "getClientInfoDebt_InfoClientService", description = "")
    public InfoClientResponseModel getClientInfoDebt_v2(String accountId, int count) {

        return InfoClientResponseModel.builder()
                .vip_client(isEvenAccount(accountId)) // Определяем VIP-статус на основе четности accountId
                .blocked(false)
                .acc_id(accountId)
                .inn(accountId + "111")
                .debt(IntStream.range(0, count)
                        .mapToObj(i -> DebtResponseModel.builder()
                                .sum(Math.abs(new Random().nextInt()))
                                .description(generateRandomDescription(128))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Проверяет, является ли номер счета четным
     * @param accountId номер счета (предполагается, что содержит число)
     * @return true если четный, false если нечетный
     */
    private boolean isEvenAccount(String accountId) {
        try {
            // Извлекаем числовую часть из accountId
            // Вариант 1: если accountId целиком число
            long accountNumber = Long.parseLong(accountId);
            return accountNumber % 2 == 0;

            // Вариант 2: если accountId содержит число в конце строки
            // String numericPart = accountId.replaceAll("\\D+", "");
            // if (!numericPart.isEmpty()) {
            //     long accountNumber = Long.parseLong(numericPart);
            //     return accountNumber % 2 == 0;
            // }
            // return false;

        } catch (NumberFormatException e) {
            // Если accountId не содержит число, используем хэш для детерминированного результата
            return Math.abs(accountId.hashCode()) % 2 == 0;
        }
    }

}
