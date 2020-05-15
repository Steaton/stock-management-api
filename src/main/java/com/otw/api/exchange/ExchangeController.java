package com.otw.api.exchange;

import com.otw.model.Exchange;
import com.otw.model.Stock;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@Controller
@RequestMapping("/exchange")
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created exchange"),
            @ApiResponse(code = 400, message = "Request is not valid"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "User does not have correct role"),
            @ApiResponse(code = 409, message = "Exchange already exists"),
            @ApiResponse(code = 500, message = "Something technical went wrong")
    })
    @PostMapping(path = "/create")
    public ResponseEntity createExchange(@NonNull @RequestBody CreateExchangeRequest createExchangeRequest) {
        exchangeService.createExchange(new Exchange(createExchangeRequest.getName(), createExchangeRequest.getDescription()));
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added stock to exchange"),
            @ApiResponse(code = 400, message = "Request is not valid"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "User does not have correct role"),
            @ApiResponse(code = 409, message = "Stock already exists in exchange"),
            @ApiResponse(code = 500, message = "Something technical went wrong")
    })
    @PostMapping("/addStock")
    private ResponseEntity addStockToExchange(@NonNull @RequestBody AddStockToExchangeRequest request) {
        exchangeService.addStockToExchange(request.getStockName(), request.getExchangeName());
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully removed stock from exchange"),
            @ApiResponse(code = 400, message = "Request is not valid"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "User does not have correct role"),
            @ApiResponse(code = 404, message = "Stock not found in exchange"),
            @ApiResponse(code = 500, message = "Something technical went wrong")
    })
    @DeleteMapping("/removeStock/{stockName}/{exchangeName}")
    @Transactional
    private ResponseEntity removeStockFromExchange(@NonNull @PathVariable String stockName,
                                                   @NonNull @PathVariable String exchangeName) {
        exchangeService.removeStockFromExchange(stockName, exchangeName);
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved stocks for exchange"),
            @ApiResponse(code = 400, message = "Request is not valid"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "User does not have correct role"),
            @ApiResponse(code = 404, message = "Exchange not found"),
            @ApiResponse(code = 500, message = "Something technical went wrong")
    })
    @GetMapping("/list/{exchangeName}")
    private @ResponseBody List<Stock> listExchange(@NonNull @PathVariable String exchangeName) {
        return exchangeService.listStocks(exchangeName);
    }
}
