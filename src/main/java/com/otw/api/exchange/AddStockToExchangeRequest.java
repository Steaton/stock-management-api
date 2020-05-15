package com.otw.api.exchange;

import lombok.Data;
import lombok.NonNull;

@Data
public class AddStockToExchangeRequest {
    @NonNull
    private String stockName;
    @NonNull
    private String exchangeName;
}
