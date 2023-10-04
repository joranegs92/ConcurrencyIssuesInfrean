package com.stock.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stock.stock.domain.Stock;
public interface StockRepository extends JpaRepository<Stock, Long> {
}
