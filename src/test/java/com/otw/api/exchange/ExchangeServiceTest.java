package com.otw.api.exchange;

import com.otw.api.stock.StockNotFoundException;
import com.otw.api.stock.StockRepository;
import com.otw.model.Exchange;
import com.otw.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
public class ExchangeServiceTest {

    private Stock STOCK;
    private Exchange EXCHANGE;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ExchangeRepository exchangeRepository;

    @InjectMocks
    private ExchangeService exchangeService;

    @BeforeEach
    void setUp() {
        STOCK = new Stock("stock", "description", LocalDateTime.now(), BigDecimal.ZERO);
        EXCHANGE = new Exchange("exchange", "description");
    }

    @Test
    void should_fail_to_create_exchange_if_echange_already_exists(CapturedOutput output) {
        // Given
        when(exchangeRepository.findByName("name")).thenReturn(Optional.of(EXCHANGE));

        // When
        assertThrows(ExchangeAlreadyExistsException.class,
                () -> exchangeService.createExchange(new Exchange("name", "description")));

        // Then
        assertThat(output).contains("Creating exchange: name");
        assertThat(output).contains("Failed - The exchange already exists: exchange");
    }

    @Test
    void should_create_exchange_successfully(CapturedOutput output) {
        // Given
        when(exchangeRepository.findByName("exchange")).thenReturn(Optional.empty());

        // When
        exchangeService.createExchange(new Exchange("exchange", "description"));

        // Then
        assertThat(output).contains("Creating exchange: exchange");
        assertExchangeCreated();
    }

    @Test
    void should_fail_to_add_stock_if_stock_does_not_exist(CapturedOutput output) {
        // Given
        mockStock(Optional.empty());

        // When
        assertThrows(StockNotFoundException.class,
                () -> exchangeService.addStockToExchange("stock", "exchange"));

        // Then
        assertThat(output).contains("Adding stock: stock to exchange: exchange");
        assertThat(output).contains("Failed - The stock does not exist: stock");
    }

    @Test
    void should_fail_to_add_stock_if_exchange_does_not_exist(CapturedOutput output) {
        // Given
        mockStock(Optional.of(STOCK));
        mockExchange(Optional.empty());

        // When
        assertThrows(ExchangeNotFoundException.class,
                () -> exchangeService.addStockToExchange("stock", "exchange"));

        // Then
        assertThat(output).contains("Adding stock: stock to exchange: exchange");
        assertThat(output).contains("Failed - The exchange does not exist: exchange");
    }

    @Test
    void should_fail_if_exchange_already_contains_stock(CapturedOutput output) {
        // Given
        Stock stock = mockStock(Optional.of(STOCK));
        Exchange exchange = mockExchange(Optional.of(EXCHANGE));
        exchange.addStock(stock);

        // When
        assertThrows(ExchangeAlreadyContainsStockException.class,
                () -> exchangeService.addStockToExchange("stock", "exchange"));

        // Then
        assertThat(output).contains("Adding stock: stock to exchange: exchange");
        assertThat(output).contains("Failed - The exchange: exchange already contains this stock: stock");
    }

    @Test
    void should_add_stock_to_exchange_successfully(CapturedOutput output) {
        // Given
        Stock stock = mockStock(Optional.of(STOCK));
        Exchange exchange = mockExchange(Optional.of(EXCHANGE));

        // When
        exchangeService.addStockToExchange("stock", "exchange");

        // Then
        assertThat(output).contains("Adding stock: stock to exchange: exchange");
        assertTrue(stock.getExchanges().contains(exchange));
        assertTrue(exchange.getStocks().contains(stock));
        verify(exchangeRepository).save(exchange);
    }

    @Test
    void should_fail_if_exchange_does_not_contain_stock(CapturedOutput output) {
        // Given
        Stock stock = mockStock(Optional.of(STOCK));
        Exchange exchange = mockExchange(Optional.of(EXCHANGE));

        // When
        assertThrows(ExchangeAlreadyContainsStockException.class,
                () -> exchangeService.removeStockFromExchange("stock", "exchange"));

        // Then
        assertThat(output).contains("Removing stock: stock from exchange: exchange");
        assertThat(output).contains("Failed - The exchange: exchange does not contain this stock: stock");
    }

    @Test
    void should_remove_stock_from_exchange_successfully(CapturedOutput output) {
        // Given
        Stock stock = mockStock(Optional.of(STOCK));
        Exchange exchange = mockExchange(Optional.of(EXCHANGE));
        exchange.addStock(stock);

        // When
        exchangeService.removeStockFromExchange("stock", "exchange");

        // Then
        assertThat(output).contains("Removing stock: stock from exchange: exchange");
        assertTrue(!stock.getExchanges().contains(exchange));
        assertTrue(!exchange.getStocks().contains(stock));
        verify(exchangeRepository).save(exchange);
    }

    @Test
    void should_liveInMarket_once_exchange_contains_five_stocks() {
        // Given
        mockExchange(Optional.of(EXCHANGE));

        // When
        addFiveStocksToExchange();

        // Then
        assertTrue(EXCHANGE.isLiveInMarket());
        verify(exchangeRepository, times(5)).save(EXCHANGE);
    }

    @Test
    void should_notLiveInMarket_once_exchange_fall_below_five_stocks() {
        // Given
        mockExchange(Optional.of(EXCHANGE));
        addFiveStocksToExchange();

        // When
        exchangeService.removeStockFromExchange("stock1", "exchange");

        // Then
        assertFalse(EXCHANGE.isLiveInMarket());
        verify(exchangeRepository, times(6)).save(EXCHANGE);
    }

    @Test
    void should_fail_to_list_non_existent_exchange(CapturedOutput output) {
        // Given
        mockExchange(Optional.empty());

        // When
        assertThrows(ExchangeNotFoundException.class,
                () -> exchangeService.listStocks("exchange"));

        // Then
        assertThat(output).contains("Listing stocks on exchange: exchange");
        assertThat(output).contains("Failed - The exchange does not exist: exchange");
    }

    @Test
    void should_list_stock_on_exchange_successfully() {
        // Given
        mockExchange(Optional.of(EXCHANGE));
        addFiveStocksToExchange();

        // When
        List<Stock> stocks = exchangeService.listStocks("exchange");

        // Then
        assertEquals(5, stocks.size());
        assertStocksCorrect(stocks);
    }

    private void assertStocksCorrect(List<Stock> stocks) {
        for (int i = 0; i < 5; i++) {
            Stock stock = stocks.get(i);
            assertEquals("stock" + i, stock.getName());
            assertEquals("description" + i, stock.getDescription());
            assertEquals(BigDecimal.ZERO, stock.getCurrentPrice());
            assertThat(stock.getLastUpdated()).isBeforeOrEqualTo(LocalDateTime.now());
            assertThat(stock.getLastUpdated()).isAfter(LocalDateTime.now().minus(Duration.ofSeconds(20)));
        }
    }

    private void assertExchangeCreated() {
        ArgumentCaptor<Exchange> argumentCaptor = ArgumentCaptor.forClass(Exchange.class);
        verify(exchangeRepository).save(argumentCaptor.capture());
        assertEquals(0, argumentCaptor.getValue().getId());
        assertEquals("exchange", argumentCaptor.getValue().getName());
        assertEquals("description", argumentCaptor.getValue().getDescription());
        assertFalse(argumentCaptor.getValue().isLiveInMarket());
    }

    private void addFiveStocksToExchange() {
        for (int i = 0; i < 5; i++) {
            Stock stock = new Stock("stock" + i, "description" + i, LocalDateTime.now(), BigDecimal.ZERO);
            when(stockRepository.findByName(eq("stock" + i))).thenReturn(Optional.of(stock));
            exchangeService.addStockToExchange("stock" + i, "exchange");
        }
    }

    private Stock mockStock(Optional<Stock> stock) {
        when(stockRepository.findByName("stock")).thenReturn(stock);
        return STOCK;
    }

    private Exchange mockExchange(Optional<Exchange> exchange) {
        when(exchangeRepository.findByName("exchange")).thenReturn(exchange);
        return EXCHANGE;
    }
}
