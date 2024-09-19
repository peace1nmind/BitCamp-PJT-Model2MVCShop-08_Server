package com.model2.mvc.service.product;
// W D 

import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;

public interface ProductService {
	
	// ��ǰ���
	public Product addProduct(Product product) throws Exception;
	
	// ��ǰ�� ��ȸ
	public Product getProduct(int prodNo) throws Exception;
	
	// �Ǹ� ��ǰ ����Ʈ
	public Map<String, Object> getProductList(Search search) throws Exception;
	
	// ��ǰ���� ����
	public Product updateProduct(Product product) throws Exception;
	
	// TranCode ���� (1: �Ǹ���, 2: ���ſϷ�, 3: �����, 4: ��ۿϷ�)
	public void updateTranCode(int prodNo, String proTranCode) throws Exception;
	
}
// class end