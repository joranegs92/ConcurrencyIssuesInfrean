package com.stock.stock.facade;

import com.stock.stock.service.OptimisticLockService;
import org.springframework.stereotype.Component;

@Component
public class OptimisticLockStockFacade {

	private final OptimisticLockService optimisticLockService;

	public OptimisticLockStockFacade(OptimisticLockService optimisticLockService){
		this.optimisticLockService = optimisticLockService;
	}

	public void decrease(Long id, Long quantity) throws InterruptedException {
		while (true){ //
			try{
				optimisticLockService.decrease(id, quantity); // 무한루프 얘가 성공할때까지

				break;
			}catch (Exception e){
				Thread.sleep(50); //충돌이 발생하면 짧은시간동안 기다린 후에 재시도하도록 한다.
			}
		}

	}
}
