package com.stock.stock.service;

import com.stock.stock.domain.Stock;
import com.stock.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessimisticLockStockService {

	private final StockRepository stockRepository;

	public PessimisticLockStockService(StockRepository stockRepository){
		this.stockRepository = stockRepository;
	}

	@Transactional
	public void decrease(Long id, Long q) {
		Stock stock = stockRepository.findByIdWithPessimisticLock(id);
		stock.decrease(q);
		stockRepository.save(stock);
	}
}
