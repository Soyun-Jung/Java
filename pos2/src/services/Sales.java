package services;

import java.util.ArrayList;

import data.DataAccessObject;
import data.GoodsInfoBean;

public class Sales {

	public Sales() {}

	public ArrayList<GoodsInfoBean> entrance(int serviceCode, GoodsInfoBean gib) {
		ArrayList<GoodsInfoBean> salesList = null;
		// 상품검색 : 1    판매리스트 : 3
		switch(serviceCode) {
		case 1:
			this.search(gib);
			break;
		case 3:
			salesList = getSalesList(gib);
			break;

		case 4:
			this.returnGoods(gib);
			break;
		}

		return salesList;
	}

	public void entrance(int serviceCode, 
			ArrayList<GoodsInfoBean> salesList) {
		// 판매정보기록 : 2
		switch(serviceCode) {
		case 2:
			this.writeSalesInfo(salesList);
			break;
		case 5:
			this.returnGoods(salesList);
			break;
		}
	}

	private void search(GoodsInfoBean gib) {
		DataAccessObject dao;
		dao = new DataAccessObject();

		dao.getGoodsInfo(3, gib);
	}

	// 판매정보기록
	private void writeSalesInfo(ArrayList<GoodsInfoBean> salesList) {
		DataAccessObject dao = new DataAccessObject();
		boolean result;
		result = dao.writeSalesInfo(2, salesList);
	} 

	private ArrayList<GoodsInfoBean> getSalesList(GoodsInfoBean gib){
		ArrayList<GoodsInfoBean> salesInfo = null; // 판매리스트
		DataAccessObject dao = new DataAccessObject(); // 데이터베이스 제어

		salesInfo = dao.getSalesList(2, gib);

		return salesInfo;
	}

	// 전체반품
		private void returnGoods(GoodsInfoBean gib) {
			ArrayList<GoodsInfoBean> salesList = null; // 판매리스트
			DataAccessObject dao = new DataAccessObject(); // 데이터베이스 제어
			
			// 전체 판매리스트
			salesList = dao.getTotalSalesList(2);
			
			for(int index = (salesList.size()-1); index>=0; index--) {
				if(salesList.get(index).getSalesCode().equals(gib.getSalesCode())) {
					salesList.remove(index);
				}
			}
			
			dao.writeSalesInfo(2, salesList, false);
		}
		
		// 부분반품
			private void returnGoods(ArrayList<GoodsInfoBean> list) {
				ArrayList<GoodsInfoBean> salesList = null; // 판매리스트
				DataAccessObject dao = new DataAccessObject(); // 데이터베이스 제어
				
				// 전체 판매리스트
				salesList = dao.getTotalSalesList(2);
				
				for(int index = (salesList.size()-1); index>=0; index--) {
					for(int j=0; j<list.size(); j++) {
						if(salesList.get(index).getSalesCode().equals(list.get(j).getSalesCode()) && salesList.get(index).getGoodsCode().equals(list.get(j).getGoodsCode())) {
						   
							salesList.remove(index);
						}
					}
				}
				
				dao.writeSalesInfo(2, salesList, false);
			}
}







