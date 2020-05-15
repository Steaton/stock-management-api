package com.otw.api.stock;

import com.otw.model.Stock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class CreateStockControllerTest {

    @Mock
    private StockService stockService;

    @InjectMocks
    private StockController stockController;

    @Test
    void should_not_allow_null_request() {
        // When
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> stockController.createStock(null));

        // Then
        assertEquals("createStockRequest is marked non-null but is null", exception.getMessage());
    }

    @Test
    void should_not_allow_null_name() {
        // When
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new CreateStockRequest(null, "description"));

        // Then
        assertEquals("name is marked non-null but is null", exception.getMessage());
    }

    @Test
    void should_create_stock() {
        // When
        stockController.createStock(new CreateStockRequest("name", "description"));

        // Then
        verifyStockServiceCalled();
    }

    private void verifyStockServiceCalled() {
        ArgumentCaptor<Stock> argumentCaptor = ArgumentCaptor.forClass(Stock.class);
        verify(stockService).createStock(argumentCaptor.capture());
        Stock stock = argumentCaptor.getValue();
        assertEquals("name", stock.getName());
        assertEquals("description", stock.getDescription());
        assertEquals(BigDecimal.ZERO, stock.getCurrentPrice());
        assertThat(stock.getLastUpdated()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(stock.getLastUpdated()).isAfter(LocalDateTime.now().minus(Duration.ofSeconds(20)));
    }
}