package com.otw.api.exchange;

import lombok.Data;
import lombok.NonNull;

@Data
public class CreateExchangeRequest {
    @NonNull
    private String name;
    @NonNull
    private String description;
}
