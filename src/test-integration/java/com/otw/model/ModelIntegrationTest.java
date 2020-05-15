package com.otw.model;

import com.otw.api.exchange.ExchangeRepository;
import com.otw.api.stock.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ModelIntegrationTest {

    private Stock STOCK1;
    private Stock STOCK2;
    private Exchange EXCHANGE1;
    private Exchange EXCHANGE2;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ExchangeRepository exchangeRepository;

    @BeforeEach
    void setUp() {
        // Create Constant Test data
        STOCK1 = new Stock("stockName", "description", LocalDateTime.now(), BigDecimal.ZERO);
        STOCK2 = new Stock("stock2Name", "description2", LocalDateTime.now(), BigDecimal.ZERO);
        EXCHANGE1 = new Exchange("exchangeName", "description");
        EXCHANGE2 = new Exchange("exchange2Name", "description2");
    }

    @Test
    void should_create_stock_on_multiple_exchanges() {
        // Given
        addStockToExchange(STOCK1, EXCHANGE1);
        addStockToExchange(STOCK1, EXCHANGE2);

        // When
        stockRepository.save(STOCK1);
        exchangeRepository.save(EXCHANGE1);
        exchangeRepository.save(EXCHANGE2);

        Stock stock = stockRepository.findByName("stockName").get();
        Exchange exchange = exchangeRepository.findByName("exchangeName").get();
        Exchange exchange2 = exchangeRepository.findByName("exchange2Name").get();

        // Then
        assertEquals(1, exchange.getStocks().size());
        assertTrue(exchange.getStocks().contains(stock));
        assertNotNull(stock.getId());
        assertEquals("stockName", stock.getName());
        assertEquals("description", stock.getDescription());
        assertThat(stock.getLastUpdated()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(stock.getLastUpdated()).isAfter(LocalDateTime.now().minus(Duration.ofSeconds(20)));
        assertEquals(BigDecimal.ZERO, stock.getCurrentPrice());

        assertEquals(2, stock.getExchanges().size());
        assertTrue(stock.getExchanges().contains(exchange));
        assertNotNull(exchange.getId());
        assertEquals("exchangeName", exchange.getName());
        assertEquals("description", exchange.getDescription());

        assertTrue(stock.getExchanges().contains(exchange2));
        assertNotNull(exchange.getId());
        assertEquals("exchange2Name", exchange2.getName());
        assertEquals("description2", exchange2.getDescription());
    }

    @Test
    void should_create_multiple_stocks_on_single_exchange() {
        // Given
        addStockToExchange(STOCK1, EXCHANGE1);
        addStockToExchange(STOCK2, EXCHANGE1);

        // When
        stockRepository.save(STOCK1);
        stockRepository.save(STOCK2);
        exchangeRepository.save(EXCHANGE1);

        Stock stock = stockRepository.findByName("stockName").get();
        Stock stock2 = stockRepository.findByName("stock2Name").get();
        Exchange exchange = exchangeRepository.findByName("exchangeName").get();

        // Then
        assertEquals(2, exchange.getStocks().size());
        assertTrue(exchange.getStocks().contains(stock));
        assertNotNull(stock.getId());
        assertEquals("stockName", stock.getName());
        assertEquals("description", stock.getDescription());
        assertThat(stock.getLastUpdated()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(stock.getLastUpdated()).isAfter(LocalDateTime.now().minus(Duration.ofSeconds(20)));
        assertEquals(BigDecimal.ZERO, stock.getCurrentPrice());

        assertTrue(exchange.getStocks().contains(stock2));
        assertNotNull(stock2.getId());
        assertEquals("stock2Name", stock2.getName());
        assertEquals("description2", stock2.getDescription());
        assertThat(stock2.getLastUpdated()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(stock2.getLastUpdated()).isAfter(LocalDateTime.now().minus(Duration.ofSeconds(20)));
        assertEquals(BigDecimal.ZERO, stock2.getCurrentPrice());

        assertEquals(1, stock.getExchanges().size());
        assertTrue(stock.getExchanges().contains(exchange));
        assertNotNull(exchange.getId());
        assertEquals("exchangeName", exchange.getName());
        assertEquals("description", exchange.getDescription());
    }

    private void addStockToExchange(Stock stock, Exchange exchange) {
        stock.addExchange(exchange);
        exchange.addStock(stock);
    }
}
