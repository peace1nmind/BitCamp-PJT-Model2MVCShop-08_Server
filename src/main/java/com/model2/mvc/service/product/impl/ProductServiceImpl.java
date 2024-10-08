package com.model2.mvc.service.product.impl;
// W D 

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductDao;
import com.model2.mvc.service.product.ProductService;

@Service("productServiceImpl")
public class ProductServiceImpl implements ProductService {

	// Field
	@Autowired
	@Qualifier("productDaoImpl")
	private ProductDao productDao;

	// Constructor
	public ProductServiceImpl() {
	}

	@Override
	public Product addProduct(Product product) throws Exception {
		
		productDao.insertProduct(product);
		
		return productDao.selectCurrentProduct();
	}

	@Override
	public Product getProduct(int prodNo) throws Exception {
		
		return productDao.selectProduct(prodNo);
	}

	@Override
	public Map<String, Object> getProductList(Search search) throws Exception {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", productDao.selectProductList(search));
		map.put("count", productDao.selectTotalCount(search));
		
		return map;
	}

	@Override
	public Product updateProduct(Product product) throws Exception {
		
		productDao.updateProduct(product);
		
		return productDao.selectProduct(product.getProdNo());
	}

	@Override
	public void updateTranCode(int prodNo, String proTranCode) throws Exception {
		
		Product product = productDao.selectProduct(prodNo);
		product.setProTranCode(proTranCode);
		
		productDao.updateProTranCode(product);
	}

}
// class end