package com.microloan.microloan_issuance.controller.async;

import com.microloan.microloan_issuance.config.AppProperties;
import com.microloan.microloan_issuance.model.response.InfoClientResponseModel;
import com.microloan.microloan_issuance.service.InfoClientService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;


import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Random;

@Slf4j
@RestController
@Tag(name = "ClientInfoDebtAsyncController", description = "description")
public class ClientInfoDebtAsyncController {
    private final AppProperties appProperties;

    private final InfoClientService service;
    private final Random random;

    private final TaskScheduler taskScheduler;

    @Value("${app.delays.info.min:0}")
    private int InfoClientMinDelay;

    @Value("${app.delays.info.max:0}")
    private int InfoClientMaxDelay;

    public ClientInfoDebtAsyncController(AppProperties appProperties, InfoClientService service,
                                         Random random,
                                         @Qualifier("taskScheduler")
                                         TaskScheduler taskScheduler) {
        this.appProperties = appProperties;
        this.service = service;
        this.random = random;
        this.taskScheduler = taskScheduler;
    }

    private int getRandomDelay(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    @Timed(value = "getClientInfoDebtAsync", description = "")
    @GetMapping(path = "/v2/checkAccount",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<InfoClientResponseModel>> getClientInfoDebtAsync(@RequestParam("acc")
                                                                                          String accountId,
                                                                                          @RequestParam("days")
                                                                                          int days) {

        log.debug("Post getClientInfoDebtAsync");
        log.trace("🟢 [START] Запрос получен, поток: {}", Thread.currentThread().getName());

        DeferredResult<ResponseEntity<InfoClientResponseModel>> deferredResult = new DeferredResult<>(appProperties.getDelay()); // timeout

        int delay = getRandomDelay(InfoClientMinDelay, InfoClientMaxDelay);


        log.trace("Adding random delay of {} ms to checkAccount endpoint", delay);
        // Регистрируем задачу на выполнение через delay миллисекунд
        // Поток НЕ блокируется, он сразу освобождается
        taskScheduler.schedule(() -> {
            log.trace("🔄 [SCHEDULED] Обработка начата в потоке: {}",
                    Thread.currentThread().getName());

            try {
                // Выполняем реальную работу без sleep
                InfoClientResponseModel infoClientResponseModel = service.getClientInfoDebt_v2(accountId, days);

                ResponseEntity<InfoClientResponseModel> response = ResponseEntity.status(HttpStatus.OK)
                        .body(infoClientResponseModel);

                deferredResult.setResult(response);

            } catch (Exception e) {
                log.error("Error processing token request", e);
                deferredResult.setErrorResult(
                        ResponseEntity.accepted().build());
            }

        }, Instant.now().plusMillis(delay));

        return deferredResult;
    }


//    /**
//     * Возвращает информацию о книгах
//     *
//     * @param idBook     id книги.
//     * @param chooseType контекст.
//     * @return список книг
//     */
//    @Timed(value = "getBookInfoAsync", description = "")
//    @GetMapping(path = "/api/v2/bookInfo/{isbn}",
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public DeferredResult<ResponseEntity<BookInfoResponseModel>> getBookInfoAsync_v2(@PathVariable("isbn")
//                                                                                     String idBook,
//                                                                                     @RequestParam("scope")
//                                                                                     String chooseType) {
//        log.trace("Get getBookInfoAsync_v2");
//        DeferredResult<ResponseEntity<BookInfoResponseModel>> deferredResult = new DeferredResult<>(30000L); // 30 сек timeout
//
//        int delay = getRandomDelay(bookInfoMinDelay, bookInfoMaxDelay);
//        log.trace("Adding random delay of {} ms to bookInfo endpoint", delay);
//
//        taskScheduler.schedule(() -> {
//            try {
//
//                BookInfoResponseModel bookInfoResponseModel = service.getBookInfo(idBook, chooseType);
//
//                ResponseEntity<BookInfoResponseModel> response = new ResponseEntity<>(bookInfoResponseModel, HttpStatus.FOUND);
//
//                deferredResult.setResult(response);
//
//            } catch (Exception e) {
//                deferredResult.setErrorResult(
//                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
//            }
//        }, Instant.now().plusMillis(delay));
//
//        return deferredResult;
//    }
//
//
//    @PostMapping(path = "/api/v2/getBookByLessons",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed(value = "getInfoBookByLessonsAsync", description = "")
//    @Operation(summary = "summary")
//    @Parameter(name = "MyCount", description = "count get books", required = true, in = ParameterIn.HEADER)
//    public DeferredResult<ResponseEntity<InfoByLessonsResponseModel>> getInfoBookByLessonsAsync_v2(@RequestBody
//                                                                                                   InfoByLessonsRequestModel requestModel,
//                                                                                                   @RequestHeader
//                                                                                                   HttpHeaders headers) {
//        log.trace("POST getInfoBookByLessonsAsync_v2");
//        DeferredResult<ResponseEntity<InfoByLessonsResponseModel>> deferredResult = new DeferredResult<>(30000L); // 30 сек timeout
//
//        int delay = getRandomDelay(bookByLessonsMinDelay, bookByLessonsMaxDelay);
//        log.trace("Adding random delay of {} ms to getInfoBookByLessonsAsync endpoint", delay);
//
//        List<String> lessons = requestModel.getLessons();
//        //int countHeaders = Integer.parseInt(headers.get("MyCount").getFirst().toString());
//        // Получаем ВСЕ значения заголовка MyCount
//        List<String> myCountValues = headers.get("MyCount");
//
//        // Проверяем, что заголовок существует
//        if (myCountValues == null || myCountValues.isEmpty()) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Missing required header: MyCount"
//            );
//        }
//
//        // Берем единственное значение
//        int countHeaders = Integer.parseInt(myCountValues.getFirst());
//
//        // Проверяем, что значение ровно одно (не список через запятую)
//        if (myCountValues.size() > 1 || countHeaders < 1) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "MyCount header should have exactly one value. Got: " + myCountValues
//            );
//        }
//
//        taskScheduler.schedule(() -> {
//            try {
//
//                InfoByLessonsResponseModel infoByLessonsResponseModel = service.getInfoByLessons(lessons, countHeaders);
//
//                ResponseEntity<InfoByLessonsResponseModel> response = new ResponseEntity<>(infoByLessonsResponseModel, HttpStatus.OK); //HttpStatus.FOUND
//
//                deferredResult.setResult(response);
//
//            } catch (Exception e) {
//                deferredResult.setErrorResult(
//                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
//            }
//        }, Instant.now().plusMillis(delay));
//
//        return deferredResult;
//    }

}
