package com.otw.api.stock;

import com.otw.model.Stock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public void createStock(Stock stock) {
        log.info("Creating a new stock: {}", stock.getName());
        Optional<Stock> stockOptional = stockRepository.findByName(stock.getName());
        validateStockDoesNotAlreadyExist(stockOptional);
        stockRepository.save(stock);
    }

    public void deleteStock(String stockName) {
        log.info("Deleting stock: {}", stockName);
        Optional<Stock> stockOptional = stockRepository.findByName(stockName);
        validateExists(stockOptional, stockName);
        stockRepository.deleteByName(stockName);
    }

    public void updatePrice(String stockName, BigDecimal price) {
        log.info("Updating price: {}", stockName);
        Optional<Stock> stockOptional = stockRepository.findByName(stockName);
        validateExists(stockOptional, stockName);
        stockOptional.get().setCurrentPrice(price);
        stockRepository.save(stockOptional.get());
    }

    private void validateStockDoesNotAlreadyExist(Optional<Stock> stockOptional) {
        if (stockOptional.isPresent()) {
            throw new StockAlreadyExistsException("Failed - A stock already exists with name: " + stockOptional.get().getName());
        }
    }

    private void validateExists(Optional<Stock> stockOptional, String stockName) {
        if (!stockOptional.isPresent()) {
            throw new StockNotFoundException("Failed - No stock found with name: " + stockName);
        }
    }
}
