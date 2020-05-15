package com.otw.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Exchange {

    @Id
    @GeneratedValue
    private int id;
    @Column(unique = true)
    private String name;
    private String description;
    private boolean liveInMarket = false;
    @ManyToMany
    @JoinTable(
            name = "stocks",
            joinColumns = @JoinColumn(name = "stock_id"),
            inverseJoinColumns = @JoinColumn(name = "exchange_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"stock_id", "exchange_id"}))
    private List<Stock> stocks;

    public Exchange(String name, String description) {
        this.name = name;
        this.description = description;
        this.liveInMarket = false;
        this.stocks = new ArrayList<>();
    }

    public boolean containsStock(String stockName) {
        for (Stock stock : stocks) {
            if (stock.getName().equals(stockName)) {
                return true;
            }
        }
        return false;
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
        updateliveInMarket();
    }

    public void removeStock(String stockName) {
        doRemoveStock(stockName);
        updateliveInMarket();
    }

    private void doRemoveStock(String stockName) {
        for (Stock stock : stocks) {
            if (stockName.equals(stock.getName())) {
                stocks.remove(stock);
                break;
            }
        }
    }

    private void updateliveInMarket() {
        if (stocks.size() < 5) {
            liveInMarket = false;
        } else {
            liveInMarket = true;
        }
    }
}
