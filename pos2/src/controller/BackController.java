package controller;

import java.util.ArrayList;

import data.GoodsInfoBean;
import data.UserInfoBean;
import services.Access;
import services.Sales;
import services.Managements;

public class BackController {
	private Access ac;
	private Sales sales;
	private Managements ma;
	private String[] accessUserInfo;

	public BackController() {
		// Job을 처리하기 위해 Access클래스 생성 후 제어하기위한 
		// 참조변수를 스택에 생성
		ac = new Access();
		sales = new Sales();
		ma = new Managements();
	}

	public String[] logIn(String[] userInfo) {
		UserInfoBean uib = new UserInfoBean();

		// Client로 부터 전달 받은 userInfo의 값을 UserInfoBean으로 복사
		uib.setRequestValue(userInfo[0]);
		uib.setEmployeeCode(userInfo[1]);
		uib.setAccessCode(userInfo[2]);

		// Access클래스의 Job을 제어하기 위한 entrance메서드 호출
		ac.entrance(uib);

		// Access 클래스에서 저장된 데이터를 
		// 클라이언트로 보내기 위해 배열변수 저장
		// 성공했을때 accessUserInfo 전달
		if( uib.getUserName() != null) {
			accessUserInfo = new String[4];
			accessUserInfo[0] = uib.getEmployeeCode();
			accessUserInfo[1] = uib.getUserName();
			accessUserInfo[2] = (uib.isUserLevel())? "Manager":"Mate";
			accessUserInfo[3] = uib.getAccessTime();
		}else {
			accessUserInfo = null;
		}

		return accessUserInfo;
	}

	// UserRegistration
	// userValue Array --> userInfo Bean 
	// Request Mapping Value -- > A2
	public String[] userRegistration(String[] userValue) {

		UserInfoBean uib = new UserInfoBean();
		uib.setEmployeeCode(userValue[0]);
		uib.setAccessCode(userValue[1]);
		uib.setUserName(userValue[2]);
		uib.setUserPhone(userValue[3]);
		uib.setUserLevel(userValue[4].equals("Mate")? false:true);
		uib.setRequestValue("A2");

		ac.entrance(uib);

		return accessUserInfo;
	}

	public String[] accessCodeModify(String[] userValue) {
		UserInfoBean uib = new UserInfoBean();
		// Array --> Bean
		uib.setEmployeeCode(userValue[0]);
		uib.setAccessCode(userValue[1]);

		uib.setRequestValue("A3");

		ac.entrance(uib);

		return accessUserInfo;
	}

	public String[] getGoodsInfo(String goodsCode) {
		String[] goodsInfo = null;
		int serviceCode;
		GoodsInfoBean gib = new GoodsInfoBean();

		// int serviceCode = 1
		serviceCode = 1;
		// goodsCode --> GoodsInfoBean gib
		gib.setGoodsCode(goodsCode);
		sales.entrance(serviceCode, gib);

		// DAO --> Service Class --> *BackController --> FrontController
		//                 Bean  --> 유효성 체크         --> Array           
		if(gib.getGoodsName() != null) {
			goodsInfo = new String[6];
			goodsInfo[0] = gib.getGoodsCode();
			goodsInfo[1] = gib.getGoodsName();
			goodsInfo[2] = gib.getGoodsPrice() + "";
			goodsInfo[3] = gib.getGoodsQty() + "";
			goodsInfo[4] = gib.getExpireDate();
			// goodsInfo[5]은 결제시 주문코드를 넣어주기 위한 공간
		}

		return goodsInfo;
	}

	// 판매데이터 저장 시작
	public void writeSales(String[][] salesInfo) {
		ArrayList<GoodsInfoBean> salesList = 
				new ArrayList<GoodsInfoBean>();
		GoodsInfoBean gib;
		int serviceCode = 2;

		// Array --> Bean  --> ArrayList<>
		for(int i=0; i<salesInfo.length; i++) {
			gib = new GoodsInfoBean();
			gib.setSalesCode(salesInfo[i][5]);
			gib.setGoodsCode(salesInfo[i][0]);
			gib.setGoodsName(salesInfo[i][1]);
			gib.setGoodsPrice(Integer.parseInt(salesInfo[i][2]));
			gib.setGoodsQty(Integer.parseInt(salesInfo[i][3]));

			salesList.add(gib);
		}

		sales.entrance(serviceCode, salesList);
	}

	public String[][] searchOrder(String ordCode){
		String[][] result = null;  // 판매리스트
		String[] record = null;    // 상품정보
		GoodsInfoBean gib = new GoodsInfoBean(); // 주문코드 저장을 위한
		int serviceCode; // Sales Class에서 분기 기준
		ArrayList<GoodsInfoBean> salesList; // 리턴값을 받을 리스

		gib.setSalesCode(ordCode);
		serviceCode = 3;
		salesList = sales.entrance(serviceCode, gib);

		result = new String[salesList.size()][];

		for(int i=0; i<result.length; i++) {
			record = new String[5];
			record[0] = salesList.get(i).getGoodsCode();
			record[1] = salesList.get(i).getGoodsName();
			record[2] = salesList.get(i).getGoodsPrice() + "";
			record[3] = salesList.get(i).getGoodsQty() + "";
			record[4] = ordCode;

			result[i] = record;
		}

		return result;
	}

	public void goodRegistration(String[] goodsValue) {


		GoodsInfoBean gib = new GoodsInfoBean();
		int serviceCode = 1;
		gib.setGoodsCode(goodsValue[0]);
		gib.setGoodsName(goodsValue[1]);
		gib.setGoodsPrice(Integer.parseInt(goodsValue[2]));
		gib.setExpireDate(goodsValue[3]);
		gib.setGoodsQty(Integer.parseInt(goodsValue[4]));
		gib.setSafeQty(Integer.parseInt(goodsValue[5]));


		ma.entrance(serviceCode, gib);

	}

	public void goodsInfoModify(String[] goodInfo) {
		GoodsInfoBean gib = new GoodsInfoBean();
		int serviceCode = 2;

		gib.setGoodsCode(goodInfo[0]);
		gib.setGoodsQty(Integer.parseInt(goodInfo[1]));

		ma.entrance(serviceCode, gib);


	}

	// 전체반품
	public void returnGoods(String ordCode) {
		GoodsInfoBean gib = new GoodsInfoBean(); // 주문코드 저장을 위한
		int serviceCode; // Sales Class에서 분기 기준

		gib.setSalesCode(ordCode);
		serviceCode = 4;
		sales.entrance(serviceCode, gib);		
	}

	// 부분반품
	public void returnGoods(String[][] returnGoodsInfo) {
		ArrayList<GoodsInfoBean> list = new ArrayList<GoodsInfoBean>();
		GoodsInfoBean gib; // 주문코드 저장을 위한
		int serviceCode; // Sales Class에서 분기 기준

		for(int i=0; i<returnGoodsInfo.length; i++) {
			gib = new GoodsInfoBean();
			gib.setSalesCode(returnGoodsInfo[i][4]);//salescode
			gib.setGoodsCode(returnGoodsInfo[i][0]);//goodscode

			list.add(gib);
		}

		//		for(int i=0; i<list.size(); i++) {
		//			System.out.println(list.get(i).getGoodsCode());
		//		}

		serviceCode = 5;
		sales.entrance(serviceCode, list);		
	}

	public String[][] getSalesList(String days) {
		String[][] dataList = null;
		ArrayList<GoodsInfoBean> list;		
		GoodsInfoBean gib = new GoodsInfoBean();
		String[] record=null;
		int serviceCode = 2;
		
		gib.setSalesCode(days);
		
		list = ma.entrance(serviceCode, gib);
		
		dataList = new String[list.size()][];
		for(int i=0;i<dataList.length;i++) {
			record = new String[5];
			record[0]=list.get(i).getGoodsCode();
			record[1]=list.get(i).getGoodsName();
			record[2]=list.get(i).getGoodsPrice()+"";
			record[3]=list.get(i).getGoodsQty()+"";
			record[4]=list.get(i).getSalesCode();
			
			dataList[i]=record;
		}
		
		return dataList;
	}



	public String[][] getBestSalesInfo(String days) {
		String[][] dataList = null;
		String[] record = null;
		ArrayList<GoodsInfoBean> list;
		Managements mgr = new Managements();
		GoodsInfoBean gib = new GoodsInfoBean();
		int serviceCode = 3;
		
		gib.setSalesCode(days);
		list = mgr.entrance(serviceCode, gib);
		
		dataList = new String[list.size()][];
		for(int i=0; i<list.size(); i++) {
			record = new String[5];
			record[0] = list.get(i).getGoodsCode();
			record[1] = list.get(i).getGoodsName();
			record[2] = list.get(i).getGoodsPrice() + "";
			record[3] = list.get(i).getGoodsQty() + "";
			record[4] = list.get(i).getSalesCode();
			
			dataList[i] = record;
		}
		
		return dataList;
	}

}










