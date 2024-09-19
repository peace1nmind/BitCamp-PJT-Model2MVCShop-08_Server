package com.model2.mvc.service.product;
// W D 

import java.util.List;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;

public interface ProductDao {
	
	// 판매하는 상품리스트 (tranCode=1)
	// Search에 orderBy, desc 있음 (정렬원하는 컬럼이 있을시 사용)
	public List<Product> selectProductList(Search search) throws Exception;
	
	// selectProductList의 totalCount
	public int selectTotalCount(Search search) throws Exception;
	
	// 상품정보 조회
	public Product selectProduct(int prodNo) throws Exception;
	
	//  가장 최근 등록 상품정보 조회
	public Product selectCurrentProduct() throws Exception;
	
	// 상품등록
	public int insertProduct(Product product) throws Exception;
	
	// 상품정보 수정
	public int updateProduct(Product product) throws Exception;
	
	// tranCode 변경
	// service에서 tranCode 변경하여 product 넣게끔
	public int updateProTranCode(Product product) throws Exception;
	
}
// class end