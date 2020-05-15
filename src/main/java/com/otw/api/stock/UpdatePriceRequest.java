package com.otw.api.stock;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class UpdatePriceRequest {
    @NonNull
    private String stockName;

    @NonNull
    private BigDecimal price;
}
