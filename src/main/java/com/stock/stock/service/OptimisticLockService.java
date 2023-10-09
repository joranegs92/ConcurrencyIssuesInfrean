package com.stock.stock.service;

import com.stock.stock.domain.Stock;
import com.stock.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptimisticLockService {

	private final StockRepository stockRepository;

	public OptimisticLockService(StockRepository stockRepository){
		this.stockRepository = stockRepository;
	}

	@Transactional
	public void decrease(Long id, Long quantity){
		Stock stock = stockRepository.findByIdWithOptimisticLock(id);

		stock.decrease(quantity);
		stockRepository.save(stock);
	}
}
