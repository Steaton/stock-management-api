package com.otw.api.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Slf4j
public class ExchangeAlreadyContainsStockException extends RuntimeException {
    public ExchangeAlreadyContainsStockException(String message) {
        super(message);
        log.info(message);
    }
}
