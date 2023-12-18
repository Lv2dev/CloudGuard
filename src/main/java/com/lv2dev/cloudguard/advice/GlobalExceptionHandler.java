package com.lv2dev.cloudguard.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ControllerAdvice;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex) {
        // 예외에 따른 상태 코드와 메시지를 반환
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }

    // 필요에 따라 다른 예외 처리 메소드를 추가할 수 있습니다.
}
