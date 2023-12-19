package org.giftorg.backed.exception;

import lombok.extern.slf4j.Slf4j;
import org.giftorg.backed.entity.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 拦截所有异常信息
     */
    @ExceptionHandler
    public Response doException(Exception e) {
        log.error("server failed: {}", e.getMessage(), e);
        return new Response(1, "server failed");
    }
}
