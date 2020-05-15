package com.otw.api.exchange;

import com.otw.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Integer> {
    Optional<Exchange> findByName(String exchangeName);
}
