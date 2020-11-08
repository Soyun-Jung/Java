package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class DataAccessObject {
	private String[] filePath = { 
			"D:\\pos\\pos2\\src\\data\\saler.txt",
			"D:\\pos\\pos2\\src\\data\\history.txt" , 
			"D:\\pos\\pos2\\src\\data\\salesInfo.txt", 
			"D:\\pos\\pos2\\src\\data\\goods.txt"};
	private File file;
	private FileReader fr;
	private BufferedReader br;
	private FileWriter fw;
	private BufferedWriter bw;

	public DataAccessObject(){}


	/*
	while(true) {
		record = br.readLine();
		if(record == null) {
			break;
		}
		// 문자 분리 작업
		// 아이디 비교작업
		// 리턴값 저장
	}
	 */

	// 직원코드 여부 확인
	public boolean isEmployee(int fileIndex, UserInfoBean uib){
		boolean result = false;
		file = new File(filePath[fileIndex]);

		try {
			// br = new BufferedReader(new FileReader(file));
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String record = null;

			while((record = br.readLine()) != null) {
				// 문자 분리 작업
				// 아이디 비교작업
				if(uib.getEmployeeCode()
						.equals(record.substring(0, 4))) {
					//리턴값 저장
					result = true;
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	// 패스워드 여부 확인
	public boolean isAccess(int fileIndex, UserInfoBean uib) {
		boolean result = false;
		file = new File(filePath[fileIndex]);
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String record = null;

			while((record = br.readLine()) != null) {
				// 직원코드 추출 // 입력값과 비교
				if(uib.getEmployeeCode()
						.equals(record.substring(0,4))) {
					// 비밀번호 추출
					String temp = record.substring(5);
					if(uib.getAccessCode()
							.equals(temp.substring(0, 
									temp.indexOf(",")))) {
						result = true;
						break;
					}
					// 입력값과 비교
				}
			}
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 특정 직원정보 가져오기
	public void getUserInfo(int fileIndex, UserInfoBean uib) {
		file = new File(filePath[fileIndex]);
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String record = null;

			while((record = br.readLine()) != null) {
				// 직원코드 추출  7777,1234,훈짱,010-5680-8050,M 
				// 입력값과 비교
				if(uib.getEmployeeCode()
						.equals(record.substring(0,4))) {
					// 비밀번호 추출
					String temp = record.substring(5);
					if(uib.getAccessCode()
							.equals(temp.substring(0, 
									temp.indexOf(",")))) {
						// 클라이언트에 보낼 데이터 저장
						// 1. record = 7777,1234,훈짱,010-5680-8050,M
						// 2. temp = 1234,훈짱,010-5680-8050,M
						temp = temp.substring(temp.indexOf(",")+1);
						// 3. temp = 훈짱,010-5680-8050,M
						uib.setUserName(
								temp.substring(0, temp.indexOf(",")));
						temp = temp.substring(temp.indexOf(",")+1);
						// 4. temp = 010-5680-8050,M
						//uib.setAccessTime(
						//temp.substring(0, temp.indexOf(",")));
						temp = temp.substring(temp.indexOf(",")+1);
						// 5. temp = M
						uib.setUserLevel((temp.equals("M"))? true: false);

						break;
					}

				}
			}
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 로그인기록 남기기
	public void setLogInInfo(int fileIndex, UserInfoBean uib) {
		file = new File(filePath[fileIndex]);
		String info = uib.getEmployeeCode() + "," + 
				uib.getAccessTime() + "," + "1" + "\n";

		try {
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);


			bw.write(info);
			bw.flush();
			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 사용자 등록
	public boolean userReg(int fileIndex, UserInfoBean uib) {
		boolean result = false;
		file = new File(filePath[fileIndex]);
		// 7777,1234,훈짱,010-5680-8050,M
		String userInfo = uib.getEmployeeCode() + "," + 
				uib.getAccessCode() + "," +
				uib.getUserName() + "," +
				uib.getUserPhone() + "," +
				(uib.isUserLevel()?"M":"A");

		try {
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);

			bw.write(userInfo);
			bw.flush();
			bw.close();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}		

		return result;
	}

	/**
	 * ArrayList<generic> 
	 *  - .add(object) : 개체 추가
	 *  - .get(index) : index위치의 object 가져오기
	 *  - .set(index, object) : index위치의 Object 수정
	 *  - .clear() : arraylist에 포함된 모든 Objects 삭제
	 *  - .contains()   .indexOf()   .isEmpty()
	 */

	public ArrayList<UserInfoBean> getEmployeesInfo(int fileIndex) {
		UserInfoBean uib;
		String record;
		String[] temp;
		ArrayList<UserInfoBean> employees = new ArrayList<UserInfoBean>();

		file = new File(filePath[fileIndex]);
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			while((record = br.readLine()) != null) {
				// 7777,1234,훈짱,010-5680-8050,M
				uib = new UserInfoBean();
				temp = record.split(",");
				uib.setEmployeeCode(temp[0]);
				uib.setAccessCode(temp[1]);
				uib.setUserName(temp[2]);
				uib.setUserPhone(temp[3]);
				uib.setUserLevel(temp[4].equals("M")? true: false);

				// ArrayList에 데이터가 저장된 빈 담기
				employees.add(uib);
			}
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return employees;
	}

	public boolean setEmployeesInfoModify(int fileIndex, ArrayList<UserInfoBean> list) {
		boolean result = false;
		String userInfo;

		file = new File(filePath[fileIndex]);	

		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			for(UserInfoBean uib : list) {
				userInfo = uib.getEmployeeCode() + "," + 
						uib.getAccessCode() + "," +
						uib.getUserName() + "," +
						uib.getUserPhone() + "," +
						(uib.isUserLevel()?"M":"A") + "\n";
				bw.write(userInfo);
				bw.flush();
			}

			bw.close();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}		

		return result;
	}

	public void getGoodsInfo(int fileIndex, GoodsInfoBean gib) {
		file = new File(filePath[fileIndex]);
		String[] record;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			while((record = (br.readLine()).split(",")) != null) {
				if(gib.getGoodsCode().equals(record[0])) {
					gib.setGoodsName(record[1]);
					gib.setGoodsPrice(Integer.parseInt(record[2]));
					gib.setGoodsQty(1);
					gib.setExpireDate(record[3]);
					break;
				}
			}

			br.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public boolean writeSalesInfo(int fileIndex, ArrayList<GoodsInfoBean> salesList) {
		boolean result = false;
		file = new File(filePath[fileIndex]);
		String record;
		try {
			bw = new BufferedWriter(new FileWriter(file, true));
			//20201006100000,1001,새우깡,1500,1
			for(GoodsInfoBean gib : salesList) {
				record = gib.getSalesCode() + "," +
						gib.getGoodsCode() + "," + 
						gib.getGoodsName() + "," +
						gib.getGoodsPrice() + "," +
						gib.getGoodsQty() + "\n";
				bw.write(record);
				bw.flush();
			}
			bw.close();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<GoodsInfoBean> getSalesList(int fileIndex, GoodsInfoBean gib){
		ArrayList<GoodsInfoBean> salesList = null; // 판매리스트
		file = new File(filePath[fileIndex]);
		GoodsInfoBean goodsInfo;
		String record; // 데이터베이스의 한 줄을 저장
		String[] goodsInfoStr; // 레코드를 분리하여 저장

		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			salesList = new ArrayList<GoodsInfoBean>();
			while((record = br.readLine()) != null) {
				goodsInfoStr = record.split(",");
				if(goodsInfoStr[0].substring(0, gib.getSalesCode().length()).equals(gib.getSalesCode())) {
					goodsInfo = new GoodsInfoBean();
					goodsInfo.setSalesCode(goodsInfoStr[0]);
					goodsInfo.setGoodsCode(goodsInfoStr[1]);
					goodsInfo.setGoodsName(goodsInfoStr[2]);
					goodsInfo.setGoodsPrice(Integer.parseInt(goodsInfoStr[3]));
					goodsInfo.setGoodsQty(Integer.parseInt(goodsInfoStr[4]));

					salesList.add(goodsInfo);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return salesList;
	}


	public void setgoodRegistration(int fileIndex, GoodsInfoBean gib) {
		
		file = new File(filePath[fileIndex]);
		// 5002,대파(단),4500,20201006,100,10
		String goodInfo =  "\n"+ gib.getGoodsCode()+ "," + gib.getGoodsName()+ "," + gib.getGoodsPrice()+ "," + gib.getExpireDate()+ "," + gib.getGoodsQty()+ "," + gib.getSafeQty();


		try {
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);

			bw.write(goodInfo);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean writeSalesInfo(int fileIndex, ArrayList<GoodsInfoBean> salesList, boolean isOverWrite) {
		boolean result = false;
		file = new File(filePath[fileIndex]);
		String record;
		try {
			if(isOverWrite) {
				bw = new BufferedWriter(new FileWriter(file, true));
			}else {
				bw = new BufferedWriter(new FileWriter(file));
			}
			//20201006100000,1001,새우깡,1500,1
			for(GoodsInfoBean gib : salesList) {
				record = gib.getSalesCode() + "," +
						gib.getGoodsCode() + "," + 
						gib.getGoodsName() + "," +
						gib.getGoodsPrice() + "," +
						gib.getGoodsQty() + "\n";
				bw.write(record);
				bw.flush();
			}
			bw.close();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	
	public ArrayList<GoodsInfoBean> getTotalSalesList(int fileIndex){
		ArrayList<GoodsInfoBean> salesList = null; // 판매리스트
		file = new File(filePath[fileIndex]);
		GoodsInfoBean goodsInfo;
		String record; // 데이터베이스의 한 줄을 저장
		String[] goodsInfoStr; // 레코드를 분리하여 저장

		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			salesList = new ArrayList<GoodsInfoBean>();
			while((record = br.readLine()) != null) {
				goodsInfoStr = record.split(",");
					goodsInfo = new GoodsInfoBean();
					goodsInfo.setSalesCode(goodsInfoStr[0]);
					goodsInfo.setGoodsCode(goodsInfoStr[1]);
					goodsInfo.setGoodsName(goodsInfoStr[2]);
					goodsInfo.setGoodsPrice(Integer.parseInt(goodsInfoStr[3]));
					goodsInfo.setGoodsQty(Integer.parseInt(goodsInfoStr[4]));

					salesList.add(goodsInfo);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return salesList;
	}


//	public ArrayList<GoodsInfoBean> getSalesList(int fileIndex, GoodsInfoBean gib) { 
//		ArrayList<GoodsInfoBean> list = null;
//		String record;
//		GoodsInfoBean dataBean;
//		String[] data;
//		
//		file = new File(filePath[fileIndex]);
//		
//		try {
//			fr=new FileReader(file);
//			br=new BufferedReader(fr);
//			
//			list = new ArrayList<GoodsInfoBean>();
//			while((record=br.readLine())!=null) {
//				data=record.split(",");
//				if(data[0].substring(0,8).equals(gib.getSalesCode())) {
//					dataBean = new GoodsInfoBean();
//					dataBean.setGoodsCode(data[1]);
//					dataBean.setGoodsName(data[2]);
//					dataBean.setGoodsPrice(Integer.parseInt(data[3]));
//					dataBean.setGoodsQty(Integer.parseInt(data[4]));
//					
//					list.add(dataBean);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return list;
//	}
}




