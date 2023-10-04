package com.stock.stock.service;

import com.stock.stock.repository.StockRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.stock.stock.domain.Stock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class StockServiceTest {
	@Autowired
	private PessimisticLockStockService stockService;

	@Autowired
	private StockRepository stockRepository;

	//100개 저장함
	@BeforeEach
	public void insert() {
		Stock stock = new Stock(1L, 100L);

		stockRepository.saveAndFlush(stock);
	}
	@AfterEach
	public void after() {
		stockRepository.deleteAll();
	}
	// 1개 빼기 남은 재고수량은 99개여야한다,
	@Test
	public void 재고감소(){
		stockService.decrease(1L, 1L);

		Stock stock = stockRepository.findById(1L).orElseThrow();

		assertEquals(99, stock.getQuantity());
	}
	@Test
	public void 동시에100개요청() throws InterruptedException {
		int threadCount = 100;
		/*
		* 비동기로 실행하는 작업을 단순화해서 사용할수 있게 도와주는 자바의 api이다.
		* 최대 32개의 쓰레드로 고정된 쓰레드풀 생성
		* 처음에 32개의 작업이 즉시 쓰레드풀의 32개 쓰레드에 의해 동시에 실행
		* */
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount); //다른쓰레드의 작업이 종료될때까지 대기할수있도록 도와주는 클래스
		for(int i =0; i<threadCount; i++){ //100개의 쓰레드를 돌린다.
			executorService.submit(()->{ //32개의 쓰레드만 동작하고 나머지 68개의 작업은 작업큐에 저장
				try{
					stockService.decrease(1L, 1L);
				}
				finally {
					latch.countDown(); // 동시에 작업하던 32개의 쓰레드가 작업을 완료하면 카운트 감소  , 100개의 쓰레드 작업모두 종료되면 latch의 카운트 내부는 0이 된다
				}

			});
		}
		latch.await();
		Stock stock = stockRepository.findById(1L).orElseThrow();
		assertEquals(0, stock.getQuantity()); // 예상되는 동작은 100개가 다 줄어드는것 실제로는 예상과는 다르게 동작함

		/*
		* 첫번째 케이스 : stockservice @Transactional+ decrease에 syncronized 추가 안한 상태 결과: 실패
		* 이유: race Condition : 두개이상의 프로세스가 공동자원을 병행적으로 읽거나 쓰는동작을 할때 경쟁상황이 펼쳐저 문제가 되는 상황
		* 때문
		* thread1 -> thread1재고감소 -> thread1재고업데이트 -> thread1 종료 -> thread2 시작을 예상했으나,
		* 실제로 발생하는 것은
		* thrad1 -> thread1재고감소 -> thread2 발생 -> thread2 재고감소-> thread1업데이트 -> thread2  ... 으로 발생
		*
		* */

		/*
		* 두번째 케이스 : stockservice @Transactional + decrease에 syncronized 추가
		* 결과: 실패
		* 이유
		* */

		/*
		* 세번째 케이스:  stockservice @Transactional 주석처리 + decrease에 syncronized 추가
		*
		* 결과 :성공
		* 트랜젝션의 동작방식:
		* 1. stockService를 가지는 클래스를 새로 만든다
		* 2. 메소드호출
		* 3. 트랜젝션 시작
		* 4. 메소드 진행
		* 5. 트랜젝션 종료와 동시에 db업데이트
		*
		* 트렌젝션이 종료되면 동시에 트랜젝션이 시작하기 때문에 업데이트보다 트렌젝션 시작이 더 빨라서 다른쓰레드에서 업데이트된 디비를 사용하는게 아니라 기존의 디비를 사용하게된다.
		*
		* */
	}
}
