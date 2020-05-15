package com.otw.api.stock;

import com.otw.model.Stock;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Api(value="Stock Management API", consumes = "application/json")
@RestController
@RequestMapping(path = "/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created stock"),
            @ApiResponse(code = 400, message = "Request is not valid"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "User does not have correct role"),
            @ApiResponse(code = 409, message = "Stock already exists"),
            @ApiResponse(code = 500, message = "Something technical went wrong")
    })
    @PostMapping(path = "/create")
    public ResponseEntity createStock(@NonNull @RequestBody CreateStockRequest createStockRequest) {
        stockService.createStock(toEntity(createStockRequest));
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted stock"),
            @ApiResponse(code = 400, message = "Request is not valid"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "User does not have correct role"),
            @ApiResponse(code = 409, message = "Stock does not exist"),
            @ApiResponse(code = 500, message = "Something technical went wrong")
    })
    @DeleteMapping(path = "/delete/{stockName}")
    @Transactional
    public ResponseEntity deleteStock(@NonNull @PathVariable String stockName) {
        stockService.deleteStock(stockName);
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated price"),
            @ApiResponse(code = 400, message = "Request is not valid"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "User does not have correct role"),
            @ApiResponse(code = 404, message = "Stock does not exist"),
            @ApiResponse(code = 500, message = "Something technical went wrong")
    })
    @PostMapping(path = "/updatePrice")
    public ResponseEntity updatePrice(@NonNull @RequestBody UpdatePriceRequest updatePriceRequest) {
        stockService.updatePrice(updatePriceRequest.getStockName(), updatePriceRequest.getPrice());
        return ResponseEntity.ok().build();
    }

    private Stock toEntity(CreateStockRequest createStockRequest) {
        return new Stock(
                createStockRequest.getName(),
                createStockRequest.getDescription(),
                LocalDateTime.now(),
                BigDecimal.ZERO);
    }
}

