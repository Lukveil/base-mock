package com.microloan.microloan_issuance.controller.async;

import com.microloan.microloan_issuance.config.AppProperties;
import com.microloan.microloan_issuance.model.request.PaymentIdRequestModel;
import com.microloan.microloan_issuance.model.response.PaymentIdResponseModel;
import com.microloan.microloan_issuance.service.PaymentIdService;
import io.micrometer.core.annotation.Timed;
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

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

@Slf4j
@RestController
@Tag(name = "TransactionClearAsyncController", description = "description")
public class TransactionClearAsyncController {
    private final AppProperties appProperties;

    private final Random random;
    private final TaskScheduler taskScheduler;

    @Value("${app.delays.transaction.min:0}")
    private int transactionClearMinDelay;

    @Value("${app.delays.transaction.max:0}")
    private int transactionClearMaxDelay;

    public TransactionClearAsyncController(AppProperties appProperties,
                                           Random random,
                                           @Qualifier("taskScheduler")
                                           TaskScheduler taskScheduler) {
        this.appProperties = appProperties;
        this.random = random;
        this.taskScheduler = taskScheduler;
    }

    private int getRandomDelay(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Удаление транзакции
     *
     * @param
     * @param
     * @return список книг
     */
    @Timed(value = "transactionClear_v2", description = "")
    @DeleteMapping(path = "/v1/transactions/clear/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<String>> transactionClear_v2(@PathVariable("id")
                                                                      String transactionId) {
        log.trace("Get transactionClear_v2");

        DeferredResult<ResponseEntity<String>> deferredResult = new DeferredResult<>(appProperties.getDelay()); // timeout

        int delay = getRandomDelay(transactionClearMinDelay, transactionClearMaxDelay);

        log.trace("Adding random delay of {} ms to transactions endpoint", delay);

        taskScheduler.schedule(() -> {
            try {

                //deferredResult.setResult(new ResponseEntity<>("deleted success", HttpStatus.valueOf(100)));

                HttpHeaders httpHeaders = new HttpHeaders();
                String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                        .format(Calendar.getInstance().getTime());
                httpHeaders.set("ProcessingTime", timeStamp);
                deferredResult.setResult(new ResponseEntity<>("deleted success", httpHeaders, HttpStatus.OK));

            } catch (Exception e) {
                log.error("Error clearing transaction {}: {}", transactionId, e.getMessage());
                deferredResult.setErrorResult(ResponseEntity
                        .internalServerError()
                        .body("delete failed"));
            }
        }, Instant.now().plusMillis(delay));

        return deferredResult;
    }
}
