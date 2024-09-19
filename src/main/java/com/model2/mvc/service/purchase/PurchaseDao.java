package com.model2.mvc.service.purchase;
// W D 

import java.util.List;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Purchase;

public interface PurchaseDao {

	// 구매정보 조회
	public Purchase selectPurchase(int tranNo) throws Exception;
	
	// prodNo로 구매정보 조회
	public Purchase selectPurchaseByProd(int prodNo) throws Exception;
	
	// 구매
	public int insertPurchase(Purchase purchase) throws Exception;
	
	// 유저 구매이력 조회 (구매완료~배송중)
	public List<Purchase> selectPurchaseList(Search search, String buyerId) throws Exception;
	
	// 구매이력 (구매완료~배송중) count
	public int countPurchaseList(String buyerId) throws Exception;
	
	// 유저 구매이력 조회 (배송완료~)
	public List<Purchase> selectPurchaseHistoryList(Search search, String buyerId) throws Exception;
	
	// 구매이력 (배송완료~) count
	public int countPurchaseHistoryList(String buyerId) throws Exception;
	
	// 판매완료 상품리스트 조회 (관리자)
	// RowBounds 사용하기
	public List<Purchase> selectSaleList(Search search) throws Exception;
	
	// 판매완료 상품리스트 count
	public int countSaleList() throws Exception;
	
	// 구매정보 수정
	public void updatePurchase(Purchase purchase) throws Exception;
	
	// tranCode 수정
	public void updateTranStatusCode(Purchase purchase) throws Exception;
	
}
// class end