package com.otw.api.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Slf4j
public class ExchangeAlreadyExistsException extends RuntimeException {
    public ExchangeAlreadyExistsException(String message) {
        super(message);
        log.info(message);
    }
}
