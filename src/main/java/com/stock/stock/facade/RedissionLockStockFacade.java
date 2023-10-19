package com.stock.stock.facade;

import com.stock.stock.service.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissionLockStockFacade {

	private RedissonClient redissonClient;

	private StockService stockService;

	public RedissionLockStockFacade( RedissonClient redissonClient,StockService stockService){
		this.redissonClient = redissonClient;
		this.stockService = stockService;
	}
	public void decrease(Long id, Long quantity) {
		RLock lock = redissonClient.getLock(id.toString());

		try{
			boolean avaliable = lock.tryLock(15, 1, TimeUnit.SECONDS);

			if(!avaliable){
				System.out.println("획득실패");
			}
			stockService.decrease(id, quantity);
		}catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
