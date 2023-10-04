package com.stock.stock.service;

import com.stock.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import com.stock.stock.domain.Stock;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

	private final StockRepository stockRepository;

	public StockService(StockRepository stockRepository){
		this.stockRepository = stockRepository;
	}

	/*
	* synchronized 한개의 쓰레드만 접근이 가능하게 할 수 있다
	* */
/*	@Transactional*/
	public synchronized void decrease(Long id, Long quantity){
		//Stock조회하고 재고를 감소시킨뒤 갱신된값을 저장한다
		Stock stock = stockRepository.findById(id).orElseThrow();
		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}
}
