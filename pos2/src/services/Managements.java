package services;

import data.GoodsInfoBean;

import java.util.ArrayList;
import java.util.Comparator;

import java.util.ArrayList;

import data.DataAccessObject;

public class Managements {

	private DataAccessObject dao;

	public Managements() {
		dao = new DataAccessObject();
	}


	public ArrayList<GoodsInfoBean> entrance(int serviceCode, GoodsInfoBean gib) {

		ArrayList<GoodsInfoBean> list = null;

		switch(serviceCode) {
		case 1:
			this.goodRegistration(gib);
			break;
		case 2:
			list = this.getSalesList(gib);
			break;
		case 3:
			list = this.getBestSalesInfo(gib);
			break;
		}

		return list;
	}

	private ArrayList<GoodsInfoBean> getBestSalesInfo(GoodsInfoBean gib){
		ArrayList<GoodsInfoBean> list = null;
		ArrayList<GoodsInfoBean> summaryList = null;
		DataAccessObject dao = new DataAccessObject();

		list = dao.getSalesList(2, gib);
		summaryList = new ArrayList<GoodsInfoBean>();

		// 데이터 통합하기 (수량 부분합)
		for(GoodsInfoBean temp : list) {
			if(summaryList.size() == 0) {
				summaryList.add(temp);
			}else {
				boolean check = true; 
				for(int i=0; i<summaryList.size(); i++) {
					if(summaryList.get(i).getGoodsCode().equals(temp.getGoodsCode())) {
						summaryList.get(i).setGoodsQty(summaryList.get(i).getGoodsQty() 
								+ temp.getGoodsQty());
						check = false;
						break;
					}
				}

				if(check) {	summaryList.add(temp); }
			}
		}

		// 리스트 정렬(수량 기준 내림차순)
		summaryList.sort(new Comparator<GoodsInfoBean>() {
			public int compare(GoodsInfoBean gib1, GoodsInfoBean gib2) {
				if (gib1.getGoodsQty() == gib2.getGoodsQty()) {	return 0;}
				else if (gib1.getGoodsQty() > gib2.getGoodsQty()) {	return -1;}
				else {return 1;}
			}
		});

		return summaryList;
	}

	private ArrayList<GoodsInfoBean> getSalesList(GoodsInfoBean gib) {

		ArrayList<GoodsInfoBean> list;
		int fileIndex=2;

		list=dao.getSalesList(fileIndex,gib);


		return list;
	}


	private void goodsModify(GoodsInfoBean gib) {



	}

	private void goodRegistration(GoodsInfoBean gib) {

		dao.setgoodRegistration(3,gib);

	}

}
