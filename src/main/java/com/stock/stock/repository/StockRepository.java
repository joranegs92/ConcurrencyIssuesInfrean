package com.stock.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stock.stock.domain.Stock;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;

public interface StockRepository extends JpaRepository<Stock, Long> {

	/*Pessimistic rock*/
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select s from Stock s where s.id = :id")
	Stock findByIdWithPessimisticLock(Long id);

	/*
	* 장점: 충돌이 많은 경우에는 성능이 좋다 . 데이터정합성이 보장된다
	* 단점: 성능감소가 있을가능성이 높다
	* */


	@Lock(value = LockModeType.OPTIMISTIC)
	@Query("select s from Stock s where s.id = :id")
	Stock findByIdWithOptimisticLock(Long id);

}
