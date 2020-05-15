package com.otw.technical;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<String> reponseMyException(Exception e) {
//        log.error(e.getMessage());
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
//    }
}
