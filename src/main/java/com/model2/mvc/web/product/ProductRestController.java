package com.model2.mvc.web.product;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Paging;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.TranCodeMapper;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.purchase.PurchaseService;

// W 24.09.12.start 

@RestController
@RequestMapping("/json/product/*")
public class ProductRestController {

	// Field
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	int pageSize;
	
	// JSON 형식으로 보낼 Map
	private Map<String, Object> responseMap;

	// Constructor
	public ProductRestController() {
		System.out.println(":: " +  getClass().getSimpleName() + " default Constructor call\n");
	}

	// Method
	// 상품목록
	@RequestMapping("/listProduct")
	public Map listProduct(@RequestParam("menu") String menu,
						   @ModelAttribute("search") Search search,
						   @RequestParam(name = "salePage", required = false, defaultValue = "1") int salePage
						   ) throws Exception {
		
		System.out.println("/listProduct");
		
		responseMap = new HashMap<String, Object>();
		
		// 상품 목록 / 상품 관리 구분 로직
		// menu: search , manage
		responseMap.put("menu", menu);
		responseMap.put("title", (menu!=null && menu.equals("search"))? "상품 목록조회" : "상품관리 (판매전)")	;
		responseMap.put("navi", (menu!=null && menu.equals("search"))? "getProduct" : "updateProduct");
		
		
		// 검색 조건을 다루는 로직
		search.setPageSize(pageSize);
		responseMap.put("search", search);
		
		
		// 검색한 리스트값들을 다루는 로직 (list, count)
		/* 판매중인 상품들 (listProduct) */
		Map<String, Object> map = productService.getProductList(search);
		responseMap.put("map" ,map);
		
		// 페이지를 다루는 로직
		Paging paging = new Paging((int) map.get("count"), search.getCurrentPage(), pageSize, pageUnit);
		responseMap.put("paging" ,paging);
		
		
		/* 구매완료 상품들 (listSale) */
		if (menu.equals("manage")) {
			Search saleSearch = search;
			saleSearch.setCurrentPage(salePage);
			saleSearch.setPageSize(pageSize);
			
			Map<String, Object> saleMap = purchaseService.getSaleList(saleSearch);
			responseMap.put("saleMap", saleMap);
			
			Paging salePaging = new Paging((int) saleMap.get("count"), saleSearch.getCurrentPage(), pageSize, pageUnit);
			responseMap.put("salePaging", salePaging);
		}
		
		responseMap.put("tranCodeMap", TranCodeMapper.getInstance().getMap());
		
		return responseMap;
	}
	
	
	// 상품정보
	@RequestMapping("/getProduct/{prodNo}")
	public Product getProduct(@PathVariable("prodNo") String prodNo,
							 HttpServletRequest request,
							 HttpServletResponse response) 
							 throws NumberFormatException, Exception {
		
		System.out.println("/getProduct");
		
		// 상품정보를 가져오는 로직
		Product product = productService.getProduct(Integer.parseInt(prodNo));
		
		
		// 최근 본 상품 리스트 로직
		Cookie[] cookies = request.getCookies();
		Cookie history = new Cookie("history", null);
		
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				history = (cookie.getName().equals("history")) ? cookie : history;
			}
		}
		

		String historyValue = history.getValue();
		String value = "";
		
		if (historyValue == null) {
			value = prodNo;
			
		} else {
			
			if (!historyValue.contains(prodNo)) {
				value = prodNo + "&" + historyValue;
				
			} else {
				value = historyValue.replace(prodNo, "");
				
				String[] splittedValue = value.split("&");
				value = "";
				
				for (int i = 0; i < splittedValue.length; i++) {
					
					if (!(splittedValue[i]==null || splittedValue[i].equals(""))) {
						value += splittedValue[i] + ((i < splittedValue.length -1)? "&" : "");
						
					}
				}
				
				value = prodNo + "&" + value;
				
			}
	
		}
		
		history.setValue(value);
		response.addCookie(history);
		
		return product;
	}
	
	
	// 상품정보 수정
	@GetMapping("/updateProduct/{prodNo}")
	public Product updateProduct(@PathVariable("prodNo") int prodNo) 
								throws Exception {
		
		System.out.println("/updateProduct GET");
		
		return productService.getProduct(prodNo);
	}
	
	@PostMapping("/updateProduct")
	public Product updateProduct(HttpServletRequest request) 
								throws Exception {
		
		System.out.println("/updateProduct POST");
		
		int no = 1;
		
		Product product = new Product();
		
		/* 파일 업로드 변경 필요 */
		if (FileUpload.isMultipartContent(request)) {
			String uploadDir = "F:BitCamp/workspace/07.Model2MVCShop(URI,pattern)/src/main/webapp/images/uploadFiles/";
			
			DiskFileUpload fileUpload = new DiskFileUpload();
			fileUpload.setRepositoryPath(uploadDir);
			
			// 최대 업로드 사이즈 설정 (-1= 제한 없음)
			// 1024 * 1024 * 10
			fileUpload.setSizeMax(1024 * 1024 * 10);
			fileUpload.setSizeThreshold(1024 * 100);
			
			System.out.println(request.getContentLength());
			System.out.println(fileUpload.getSizeMax());
			
			if (request.getContentLength() < fileUpload.getSizeMax()) {
				
				// 문자열을 특정 구분자 기준으로 토큰(문자열 조각)으로 나누는데 사용하는 클래스(split 과 같음)
				StringTokenizer token = null;
				
				List<FileItem> fileItemList = fileUpload.parseRequest(request);
				
				// html에서 받은 값들의 개수
				int size = fileItemList.size();
				
				System.out.println(size);
				
				for (FileItem fileItem : fileItemList) {
					System.out.println(fileItem);
					// 파일 형식/파라미터 인지 확인 (파라미터면 true)
					if (fileItem.isFormField()) {	// 파라미터라면
						if (fileItem.getFieldName().equals("manuDate")) {
							
							String manuDate = "";
							
							if (fileItem.getString("euc-kr").contains("-")) {
								token = new StringTokenizer(fileItem.getString("euc-kr"), "-");
								manuDate = token.nextToken() + token.nextToken() + token.nextToken();
							} else {
								manuDate = fileItem.getString("euc-kr");
							}
							
							product.setManuDate(manuDate);
							
						} else if (fileItem.getFieldName().equals("prodName")) {
							product.setProdName(fileItem.getString("euc-kr"));
							
						} else if (fileItem.getFieldName().equals("prodDetail")) {
							product.setProdDetail(fileItem.getString("euc-kr"));
							
						} else if (fileItem.getFieldName().equals("price")) {
							product.setPrice(Integer.parseInt(fileItem.getString("euc-kr")));
							
						} else if (fileItem.getFieldName().equals("prodNo")) {
							product.setProdNo(Integer.parseInt(fileItem.getString("euc-kr")));

						}
						
						
					} else { // 파일 형식이면
						if (fileItem.getSize() > 0) { // 파일이 있으면
							int index = (fileItem.getName().contains("\\"))? 
											fileItem.getName().lastIndexOf("\\") : 
											fileItem.getName().lastIndexOf("/");
							
							String fileName = fileItem.getName().substring(index+1);
							
							product.setFileName(fileName);
							
							try {
								File uploadFile = new File(uploadDir, fileName);
								fileItem.write(uploadFile);
								
								// 파일 업로드 처리 후
								Thread.sleep(2000); // 2초 정도 대기
								
							} catch (Exception e) {
								e.printStackTrace();
								
							}
							
						} else { // 파일이 없으면
							product.setFileName("empty.GIF");
							
						}
					}
				} // for end	
				
				product = productService.updateProduct(product);
					
			} else {
				// 업로드하는 파일이 setSizeMax보다 큰 경우
				int overSize = (request.getContentLength() / 1000000);
				
				System.out.println("<script>alert('파일의 크기는 1MB까지 됩니다. 올리신 파일 용량은 " + overSize + "MB 입니다.');");
				System.out.println("history.back();</script>");
			}
			
		} else {
			System.out.println("인코딩 타입이 multipart/form-data가 아닙니다");
		}
		
		
//		product = productService.updateProduct(product);
//		responseMap.put("product", product);
		
		return product;
	}
//	
//	
//	// 상품등록
//	@GetMapping("/addProduct")
//	public String addProduct() {
//		
//		System.out.println("/product/addProduct GET");
//		
//		return "redirect:/product/addProductView.jsp";
//	}
//	
//	@PostMapping("/addProduct")
//	public String addProduct(HttpServletRequest request) 
//							 throws Exception {
//		
//		System.out.println("/product/addProduct POST");
//		
//		/* Spring mvc 파일 업로드 참고하기 */
//		/* 파일 업로드 경로 메타데이터로 변경하기 */
//		if (FileUpload.isMultipartContent(request)) {
//			String uploadDir = "F:BitCamp/workspace/07.Model2MVCShop(URI,pattern)/src/main/webapp/images/uploadFiles/";
//			
//			DiskFileUpload fileUpload = new DiskFileUpload();
//			fileUpload.setRepositoryPath(uploadDir);
//			
//			// 최대 업로드 사이즈 설정 (-1= 제한 없음)
//			// 1024 * 1024 * 10
//			fileUpload.setSizeMax(1024 * 1024 * 10);
//			fileUpload.setSizeThreshold(1024 * 100);
//			
//			System.out.println(request.getContentLength());
//			
//			if (request.getContentLength() < fileUpload.getSizeMax()) {
//				
//				Product product = new Product();
//				
//				// 문자열을 특정 구분자 기준으로 토큰(문자열 조각)으로 나누는데 사용하는 클래스(split 과 같음)
//				StringTokenizer token = null;
//				
//				List<FileItem> fileItemList = fileUpload.parseRequest(request);
//				
//				// html에서 받은 값들의 개수
//				int size = fileItemList.size();
//				
//				for (FileItem fileItem : fileItemList) {
//					
//					// 파일 형식/파라미터 인지 확인 (파라미터면 true)
//					if (fileItem.isFormField()) {	// 파라미터라면
//						if (fileItem.getFieldName().equals("manuDate")) {
//							token = new StringTokenizer(fileItem.getString("euc-kr"), "-");
//							String manuDate = token.nextToken() + token.nextToken() + token.nextToken();
//							
//							product.setManuDate(manuDate);
//							
//						} else if (fileItem.getFieldName().equals("prodName")) {
//							product.setProdName(fileItem.getString("euc-kr"));
//							
//						} else if (fileItem.getFieldName().equals("prodDetail")) {
//							product.setProdDetail(fileItem.getString("euc-kr"));
//							
//						} else if (fileItem.getFieldName().equals("price")) {
//							product.setPrice(Integer.parseInt(fileItem.getString("euc-kr")));
//							
//						}
//						
//					} else { // 파일 형식이면
//						if (fileItem.getSize() > 0) { // 파일이 있으면
//							int index = (fileItem.getName().contains("\\"))? 
//											fileItem.getName().lastIndexOf("\\") : 
//											fileItem.getName().lastIndexOf("/");
//							
//							String fileName = fileItem.getName().substring(index+1);
//							
//							product.setFileName(fileName);
//							
//							try {
//								File uploadFile = new File(uploadDir, fileName);
//								fileItem.write(uploadFile);
//								
//								// 파일 업로드 처리 후
//								Thread.sleep(2000); // 2초 정도 대기
//								
//							} catch (Exception e) {
//								e.printStackTrace();
//								
//							}
//							
//						} else { // 파일이 없으면
//							product.setFileName("empty.GIF");
//							
//						}
//					}
//				} // for end	
//
//				product = productService.addProduct(product);
//				
//				request.setAttribute("product", product); 
//					
//			} else {
//				// 업로드하는 파일이 setSizeMax보다 큰 경우
//				int overSize = (request.getContentLength() / 1000000);
//				
//				System.out.println("<script>alert('파일의 크기는 1MB까지 됩니다. 올리신 파일 용량은 " + overSize + "MB 입니다.');");
//				System.out.println("history.back();</script>");
//			}
//			
//		} else {
//			System.out.println("인코딩 타입이 multipart/form-data가 아닙니다");
//		}
//		
//		return "forward:/product/addProduct.jsp";
//		
//	}// method end

}
// class end