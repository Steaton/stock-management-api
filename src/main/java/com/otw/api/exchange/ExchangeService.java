package com.otw.api.exchange;

import com.otw.api.stock.StockNotFoundException;
import com.otw.api.stock.StockRepository;
import com.otw.model.Stock;
import com.otw.model.Exchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ExchangeService {

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Autowired
    private StockRepository stockRepository;

    public void createExchange(Exchange exchange) {
        log.info("Creating exchange: {}", exchange.getName());
        Optional<Exchange> exchangeOptional = exchangeRepository.findByName(exchange.getName());
        validateExchangeDoesNotAlreadyExist(exchangeOptional);
        exchangeRepository.save(exchange);
    }

    public void addStockToExchange(String stockName, String exchangeName) {
        log.info("Adding stock: {} to exchange: {}", stockName, exchangeName);
        Optional<Stock> stock = stockRepository.findByName(stockName);
        Optional<Exchange> exchange = exchangeRepository.findByName(exchangeName);
        validateStockAndExchangeExist(stock, exchange, stockName, exchangeName);
        validateExchangeDoesNotContainStock(stockName, exchange.get());
        doAddStockToExchange(stock.get(), exchange.get());
    }

    private void doAddStockToExchange(Stock stock, Exchange exchange) {
        stock.addExchange(exchange);
        exchange.addStock(stock);
        exchangeRepository.save(exchange);
    }

    public void removeStockFromExchange(String stockName, String exchangeName) {
        log.info("Removing stock: {} from exchange: {}", stockName, exchangeName);
        Optional<Stock> stock = stockRepository.findByName(stockName);
        Optional<Exchange> exchange = exchangeRepository.findByName(exchangeName);
        validateStockAndExchangeExist(stock, exchange, stockName, exchangeName);
        validateExchangeContainsStock(stockName, exchange.get());
        doRemoveStockFromExchange(stockName, exchange.get());
    }

    private void doRemoveStockFromExchange(String stockName, Exchange exchange) {
        exchange.removeStock(stockName);
        exchangeRepository.save(exchange);
    }

    public List<Stock> listStocks(String exchangeName) {
        log.info("Listing stocks on exchange: {}", exchangeName);
        Optional<Exchange> exchange = exchangeRepository.findByName(exchangeName);
        validateExchangeExists(exchange, exchangeName);
        return exchange.get().getStocks();
    }

    private void validateStockAndExchangeExist(Optional<Stock> stock, Optional<Exchange> exchange, String stockName, String exchangeName) {
        validateStockExists(stock, stockName);
        validateExchangeExists(exchange, exchangeName);
    }

    private void validateExchangeDoesNotAlreadyExist(Optional<Exchange> exchange) {
        if (exchange.isPresent()) {
            throw new ExchangeAlreadyExistsException("Failed - The exchange already exists: " + exchange.get().getName());
        }
    }

    private void validateExchangeContainsStock(String stockName, Exchange exchange) {
        if (!exchange.containsStock(stockName)) {
            throw new ExchangeAlreadyContainsStockException("Failed - The exchange: " + exchange.getName() + " does not contain this stock: " + stockName);
        }
    }

    private void validateExchangeDoesNotContainStock(String stockName, Exchange exchange) {
        if (exchange.containsStock(stockName)) {
            throw new ExchangeAlreadyContainsStockException("Failed - The exchange: " + exchange.getName() + " already contains this stock: " + stockName);
        }
    }

    private void validateStockExists(Optional<Stock> stock, String stockName) {
        if (!stock.isPresent()) {
            throw new StockNotFoundException("Failed - The stock does not exist: " + stockName);
        }
    }

    private void validateExchangeExists(Optional<Exchange> exchange, String exchangeName) {
        if (!exchange.isPresent()) {
            throw new ExchangeNotFoundException("Failed - The exchange does not exist: " + exchangeName);
        }
    }
}
