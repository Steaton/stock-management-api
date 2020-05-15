package com.otw.api.stock;

import com.otw.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
public class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;
    private Stock STOCK;

    @BeforeEach
    void setUp() {
        STOCK = new Stock("name", "description", LocalDateTime.now(), BigDecimal.ZERO);
    }

    @Test
    void should_create_stock(CapturedOutput output) {
        // Given
        mockStock(Optional.empty());

        // When
        stockService.createStock(STOCK);

        // Then
        assertThat(output).contains("Creating a new stock: name");
        verify(stockRepository).findByName("name");
        verify(stockRepository).save(STOCK);
    }

    @Test
    void should_not_create_duplicate_stock(CapturedOutput output) {
        // Given
        mockStock(Optional.of(STOCK));

        // When
        assertThrows(StockAlreadyExistsException.class, () -> stockService.createStock(STOCK));

        // Then
        assertThat(output).contains("Creating a new stock: name");
        assertThat(output).contains("Failed - A stock already exists with name: name");
        verify(stockRepository).findByName("name");
    }

    @Test
    void should_not_delete_non_existent_stock(CapturedOutput output) {
        // Given
        mockStock(Optional.empty());

        // When
        assertThrows(StockNotFoundException.class, () -> stockService.deleteStock("name"));

        // Then
        assertThat(output).contains("Deleting stock: name");
        assertThat(output).contains("Failed - No stock found with name: name");
        verify(stockRepository).findByName("name");
    }

    @Test
    void should_delete_stock(CapturedOutput output) {
        // Given
        mockStock(Optional.of(STOCK));

        // When
        stockService.deleteStock("name");

        // Then
        assertThat(output).contains("Deleting stock: name");
        verify(stockRepository).findByName("name");
        verify(stockRepository).deleteByName("name");
    }

    @Test
    void should_fail_to_update_price_if_stock_doesnt_exist(CapturedOutput output) {
        // Given
        mockStock(Optional.empty());

        // When
        assertThrows(StockNotFoundException.class, () -> stockService.updatePrice("name", BigDecimal.ONE));

        // Then
        assertThat(output).contains("Updating price: name");
        assertThat(output).contains("Failed - No stock found with name: name");
    }

    @Test
    void should_update_stock_price(CapturedOutput output) {
        // Given
        mockStock(Optional.of(STOCK));

        // When
        stockService.updatePrice("name", BigDecimal.ONE);

        // Then
        assertThat(output).contains("Updating price: name");
        assertEquals(BigDecimal.ONE, STOCK.getCurrentPrice());
        verify(stockRepository).findByName("name");
        verify(stockRepository).save(STOCK);
    }

    private void mockStock(Optional<Stock> stock) {
        when(stockRepository.findByName("name")).thenReturn(stock);
    }
}
