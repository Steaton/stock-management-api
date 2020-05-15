package com.otw.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Stock {
    @Id
    @GeneratedValue
    private int id;
    @Column(unique = true)
    private String name;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastUpdated;
    private BigDecimal currentPrice;
    @JsonIgnore
    @ManyToMany(mappedBy = "stocks")
    private List<Exchange> exchanges;

    public Stock(String name, String description, LocalDateTime lastUpdated, BigDecimal currentPrice) {
        this.name = name;
        this.description = description;
        this.lastUpdated = lastUpdated;
        this.currentPrice = currentPrice;
        this.exchanges = new ArrayList<>();
    }

    public void addExchange(Exchange exchange) {
        this.exchanges.add(exchange);
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
}
