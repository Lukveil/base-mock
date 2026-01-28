package com.microloan.microloan_issuance.controller.async;

import com.microloan.microloan_issuance.config.AppProperties;
import com.microloan.microloan_issuance.model.request.PaymentIdRequestModel;
import com.microloan.microloan_issuance.model.response.PaymentIdResponseModel;
import com.microloan.microloan_issuance.service.InfoClientService;
import com.microloan.microloan_issuance.service.PaymentIdService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.internal.TimedScheduledExecutorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Slf4j
@RestController
@Tag(name = "PaymentIdAsyncController", description = "description")
public class PaymentIdAsyncController {
    private final AppProperties appProperties;

    private final PaymentIdService paymentIdService;
    private final Random random;
    private final TaskScheduler taskScheduler;


    @Value("${app.delays.payment.min:0}")
    private int paymentMinDelay;

    @Value("${app.delays.payment.max:0}")
    private int paymentMaxDelay;

    public PaymentIdAsyncController(AppProperties appProperties, PaymentIdService paymentIdService,
                                    Random random,
                                    @Qualifier("taskScheduler")
                                    TaskScheduler taskScheduler) {
        this.appProperties = appProperties;
        this.paymentIdService = paymentIdService;
        this.random = random;
        this.taskScheduler = taskScheduler;
    }

    private int getRandomDelay(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Возвращает информацию о книгах
     *
     * @param
     * @param
     * @return список книг
     */
    @Timed(value = "getPaymentIdAsync_v2", description = "")
    @PostMapping(path = "/v2/payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<PaymentIdResponseModel>> getPaymentIdAsync_v2(@RequestBody
                                                                                       PaymentIdRequestModel paymentIdRequestModel,
                                                                                       @RequestHeader
                                                                                       HttpHeaders headers) {
        log.trace("Get getPaymentIdAsync_v2");
        DeferredResult<ResponseEntity<PaymentIdResponseModel>> deferredResult = new DeferredResult<>(appProperties.getDelay()); // timeout

        int delay = getRandomDelay(paymentMinDelay, paymentMaxDelay);

        log.trace("Adding random delay of {} ms to payment endpoint", delay);

        List<String> myCountValues = headers.get("BankCode");

        // Проверяем, что заголовок существует
        if (myCountValues == null || myCountValues.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Missing required header: MyCount"
            );
        }

        // Берем единственное значение
        String countHeaders = myCountValues.getFirst();

        taskScheduler.schedule(() -> {
            try {

                PaymentIdResponseModel paymentIdResponseModel = paymentIdService.getAceptedTransaction(paymentIdRequestModel.getTransaction_id(),
                                                                                                        countHeaders.chars().filter(Character::isDigit)
                                                                                                                .map(Character::getNumericValue)
                                                                                                                .sum());

                deferredResult.setResult(new ResponseEntity<>(paymentIdResponseModel, HttpStatus.OK));

            } catch (Exception e) {
                deferredResult.setErrorResult(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            }
        }, Instant.now().plusMillis(delay));

        return deferredResult;
    }
}
