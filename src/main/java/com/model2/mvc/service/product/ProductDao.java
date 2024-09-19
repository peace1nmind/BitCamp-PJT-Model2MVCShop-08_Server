package com.model2.mvc.service.product;
// W D 

import java.util.List;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;

public interface ProductDao {
	
	// �Ǹ��ϴ� ��ǰ����Ʈ (tranCode=1)
	// Search�� orderBy, desc ���� (���Ŀ��ϴ� �÷��� ������ ���)
	public List<Product> selectProductList(Search search) throws Exception;
	
	// selectProductList�� totalCount
	public int selectTotalCount(Search search) throws Exception;
	
	// ��ǰ���� ��ȸ
	public Product selectProduct(int prodNo) throws Exception;
	
	//  ���� �ֱ� ��� ��ǰ���� ��ȸ
	public Product selectCurrentProduct() throws Exception;
	
	// ��ǰ���
	public int insertProduct(Product product) throws Exception;
	
	// ��ǰ���� ����
	public int updateProduct(Product product) throws Exception;
	
	// tranCode ����
	// service���� tranCode �����Ͽ� product �ְԲ�
	public int updateProTranCode(Product product) throws Exception;
	
}
// class end