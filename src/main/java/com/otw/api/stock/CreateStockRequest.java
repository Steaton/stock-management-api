package com.otw.api.stock;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
@Getter
public class CreateStockRequest {
    @NonNull
    private String name;

    @NonNull
    private String description;
}
