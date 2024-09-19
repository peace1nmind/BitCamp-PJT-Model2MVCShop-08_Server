package com.model2.mvc.service.purchase;
// W D 

import java.util.List;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Purchase;

public interface PurchaseDao {

	// �������� ��ȸ
	public Purchase selectPurchase(int tranNo) throws Exception;
	
	// prodNo�� �������� ��ȸ
	public Purchase selectPurchaseByProd(int prodNo) throws Exception;
	
	// ����
	public int insertPurchase(Purchase purchase) throws Exception;
	
	// ���� �����̷� ��ȸ (���ſϷ�~�����)
	public List<Purchase> selectPurchaseList(Search search, String buyerId) throws Exception;
	
	// �����̷� (���ſϷ�~�����) count
	public int countPurchaseList(String buyerId) throws Exception;
	
	// ���� �����̷� ��ȸ (��ۿϷ�~)
	public List<Purchase> selectPurchaseHistoryList(Search search, String buyerId) throws Exception;
	
	// �����̷� (��ۿϷ�~) count
	public int countPurchaseHistoryList(String buyerId) throws Exception;
	
	// �ǸſϷ� ��ǰ����Ʈ ��ȸ (������)
	// RowBounds ����ϱ�
	public List<Purchase> selectSaleList(Search search) throws Exception;
	
	// �ǸſϷ� ��ǰ����Ʈ count
	public int countSaleList() throws Exception;
	
	// �������� ����
	public void updatePurchase(Purchase purchase) throws Exception;
	
	// tranCode ����
	public void updateTranStatusCode(Purchase purchase) throws Exception;
	
}
// class end