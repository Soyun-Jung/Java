package controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class FrontController {
	private Scanner sc;

	public FrontController() {
		sc = new Scanner(System.in);
		this.controlTower(this.makeTitle());
		sc.close();
	}

	// Job Control
	private void controlTower(String title) {
		String[] accessInfo;
		String menu;
		String[] goodsInfo;
		String[][] goodsInfoList = null;

		BackController bc = new BackController();

		while(true) {
			// Request : controlTower --> Server  
			//        --> Response : controlTower
			//1. logIn  : 성공 --> 2.    실패 --> 1.
			String[] userInfo = this.logIn(title);

			if(userInfo == null) { break;}
			accessInfo = bc.logIn(userInfo);

			// 로그인 성공여부
			if(accessInfo != null) {
				while(true) {
					// 2-1 성공 selectService
					menu = this.selectService(title, accessInfo);
					if(menu.equals("1")) {
						String goodsCode;
						while(true) {
							// 1. 상품판매
							/* 같은 상품코드 입력 시 수량 증가를 위해
							 * 실행 코드의 순서를 변경
							 * 							 * 
							 * 상품 입력을 첫 번째와 첫번째 이후로 구분 하여
							 * 첫 번째 이후 부터는  결제코드(p)가 입력되기 전 까지 반복*/

							while(true) {
								goodsCode = this.sales(title, accessInfo);
								if(goodsCode.equals("0")) {break;}
								goodsInfo = bc.getGoodsInfo(goodsCode);	
								goodsInfoList = this.makeList(goodsInfo, goodsInfoList);
								if(goodsInfoList != null) {break;}
							}

							if(goodsCode.equals("0")) { break;}

							while(true) {
								goodsCode = this.sales(title, accessInfo, goodsInfoList);
								/* 첫 번째 이후 입력되는 상품코드는 기 입력된 코드 여부를
								 * 확인하여 최초 입력된 상품코드인 경우 Back-End에 
								 * 데이터 요청 */
								if(!this.isCompareGoodsCode(goodsInfoList, goodsCode) && !goodsCode.equals("p")) {
									goodsInfo = bc.getGoodsInfo(goodsCode);	
									goodsInfoList = this.makeList(goodsInfo, goodsInfoList);
								}

								// 결제 여부
								if(goodsCode.equals("p")) {	break;}
							}

							if(this.payments(goodsInfoList)) {
								// 주문코드 작성(yyyyMMddhhmmss)
								Date salesCode = new Date();
								SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
								String code = sdf.format(salesCode);

								for(int i=0; i<goodsInfoList.length; i++) {
									goodsInfoList[i][5] = code;
								}

								// 서버에 판매데이터 전송
								bc.writeSales(goodsInfoList);
								goodsInfoList = null;
								goodsCode = null;
								goodsInfo = null;
							}else {
								goodsInfoList = null;
								goodsCode = null;
								goodsInfo = null;
							}
						}

					}else if(menu.equals("2")) {
						// 2020-10-08 2. 상품반품
						String ordCode = this.minusSales(title, accessInfo, null);

						while(true) {
							if(!ordCode.equals("0")) {
								// 반품 1. 입력받은 주문코드에 해당하는 판매 리스트 요청
								if(ordCode.length() == 14) {
									goodsInfoList = bc.searchOrder(ordCode);			
									ordCode = this.minusSales(title, accessInfo, goodsInfoList);
								}else {
									// 반품 2. 전체 반품 or 부분 반품 여부 확인
									if(ordCode.equals("1")) {
										// 반품 3-1. 전체 반품 요청
										bc.returnGoods(goodsInfoList[0][4]);
										break;
									} else {
										String[][] returnGoodsInfo;
										// 반품 3-2. 부분 반품에 해당하는 상품코드 리스트 입력 받기
										int[] returnIndex =	this.selectRefund(title, accessInfo, goodsInfoList);

										/*for(int i=0; i<returnIndex.length; i++) {
											System.out.println(returnIndex[i]);
										}*/

										int count = 0;
										// returnIndex배열에 들어있는 값의 개수 파악 
										//  --> 반품정보를 저장할 배열 생성										
										for(int i=0; i<returnIndex.length; i++) {
											if(returnIndex[i] != 0) {
												count++;
											}else {break;}
										}
										returnGoodsInfo = new String[count][];

										//반품정보 저장
										for(int i=0; i<returnGoodsInfo.length; i++) {
											returnGoodsInfo[i] = goodsInfoList[returnIndex[i]-1];
										}										

										/*for(int i=0; i<returnGoodsInfo.length; i++) {
											System.out.println(returnGoodsInfo[i][0]);
										}*/

										// 반품 3-2-1. 부분 반품을 서버에 요청
										bc.returnGoods(returnGoodsInfo);
										break;
									}

								}
							}else {break;}
						}
					}else if(menu.equals("3")) {
						while(true) {
							// 3. 직원관리
							menu = this.userManagement(title, accessInfo);
							if(menu.equals("1")) {
								// 3-1. 직원등록
								accessInfo = bc.userRegistration(this.userRegistration(title, accessInfo));
							}else if(menu.equals("2")){
								// 3-2. 직원정보수정
								accessInfo = bc.accessCodeModify(this.acceessCodeMod(title, accessInfo));
							}else {	break; }
						}


					}else if(menu.equals("4")){
						while(true) {
							String days;
							String[][] dataList;
							//4. 영업관리
							menu = this.goodsManagement(title, accessInfo);

							if(menu.equals("1")) { //4-1.상품등록
								bc.goodRegistration(this.goodsRegistration(title, accessInfo));


							}else if(menu.equals("2")){ // 4-2. 상품정보수정
								
								bc.goodsInfoModify(this.goodsInfoMod(title, accessInfo));
							}
							
							if(menu.equals("3") || menu.equals("4")) {
								boolean type = menu.equals("3")? true: false;
								while(true) {

									days = this.getSalesList(type, title, accessInfo);
									if(days.equals("0")) {break;}
									
									dataList = bc.getSalesList(days);
									this.getSalesList(type, title, days, accessInfo, dataList);
								}
							}else if(menu.equals("5")) {
								/* 월간 베스트 상품 */
								while(true) {

									days = this.getSalesList(false, title, accessInfo);
									if(days.equals("0")) {break;}
									
									dataList = bc.getBestSalesInfo(days);
									this.bestSalesInfo(title, accessInfo, dataList);
								}
							}else {
								break;
							}

						}

					}else {break;}
				}
				// 로그인 정보 해제
				accessInfo = null;				
			}
		}

		this.display(title);
		this.display("_________________________________ Good Bye^^");
	}

	private void bestSalesInfo(String title, String[] accessInfo, String[][] dataList) {
		String result = null;

		this.display(title);
		
		this.display(" [ ");
		for(int i=0; i<accessInfo.length; i++) {
			this.display(accessInfo[i]);
			if(i!=accessInfo.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");

		this.display("[월간 베스트]\n\n");
		
		/*--------- End : 프로그램 타이틀 ----------*/
		
		/********** Start : 매출 조회일 ***********/
		this.display("  매출월   : " + dataList[0][4].substring(0, 6) + "\n");
		/*--------- End : 매출 조회일 ----------*/
		
		/********** Start : 매출 리스트 출력 ***********/
		this.display("---------------------------------------------------\n");
		this.display(" 순번 \t 코드 \t 상품명 \t 단가 \t 수량 \t 금액\n");
		this.display("---------------------------------------------------\n");
				
		int totalAmount = 0;
		for(int i=0; i<dataList.length; i++) {
			int amount = 0;
			this.display(" " + (i+1) + " \t");
			for(int j=0; j<dataList[i].length-1; j++) {
				this.display(dataList[i][j] + " \t");
			}
						
			amount = Integer.parseInt(dataList[i][2]) * Integer.parseInt(dataList[i][3]);
			totalAmount += amount;
			this.display(" " + amount + "\n");
		}
		
		this.display("---------------------------------------------------\n");
		this.display("-------------------------------- Total : " + totalAmount +"\n");
		/*--------- End : 매출 리스트 출력 ----------*/
		
		
		/********** Start : 사용자 데이터 ***********/
		this.display("______________________________ 확인(y) : ");
		result = this.sc.next();
		/*--------- End : 사용자 데이터 ----------*/
		
	}

	private void getSalesList(boolean type, String title, String days, String[] accessInfo, String[][] dataList) {

		//String days;
		this.display(title);

		this.display(" [ ");
		for(int i=0; i<accessInfo.length; i++) {
			this.display(accessInfo[i]);
			if(i!=accessInfo.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");
		
		this.display(type? " [일일매출]\n\n" : " [월간매출]\n\n");
		
		this.display(type? "  매출일자 : " + days + "\n" : 
			   "  매출월   : " + days + "\n");
		
		this.display("------------------------------------------------\n");
		this.display("\t 코드 \t상품명\t 단가  \t수량 \t 금액 \t 금액\n ");
		this.display("------------------------------------------------\n");

		int totalAmount = 0;
		int index = 0;
		
		for(int i=0; i<dataList.length; i++) {
			// 순번 매기기
			if(i==0) {
				index++;
				this.display(" " + index + " \t" );
			}else {
				if(dataList[i][4].equals(dataList[i-1][4])) {
					this.display("    \t");
				}else {
					index++;
					this.display(" " + index + " \t" );
				}
			}
			
			int amount = 0;
			for(int j=0; j<dataList[i].length-1; j++) {
				this.display(dataList[i][j] + " \t");
			}
						
			amount = Integer.parseInt(dataList[i][2]) * Integer.parseInt(dataList[i][3]);
			totalAmount += amount;
			this.display(" " + amount + "\n");
		}
		this.display("------------------------------------------------\n");
		this.display("-------------------------------- Total : " + totalAmount +"\n");
		/*--------- End : 매출 리스트 출력 ----------*/
		
		
		/********** Start : 사용자 데이터 ***********/
		this.display("______________________________ 확인(y) : ");
		sc.next();
		
	}

	//일별판매현황
	private String getSalesList(boolean type, String title, String[] accessInfo) {

		String days;
		this.display(title);

		this.display(" [ ");
		for(int i=0; i<accessInfo.length; i++) {
			this.display(accessInfo[i]);
			if(i!=accessInfo.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");
		this.display(type? "  매출일자 : " : "  매출월 : "  );
		days = sc.next();

		return days;
	}

	/** 2020-10-08 상품 반품
	 * @param String title			프로그램 타이틀	
	 * @param String[] accessInfo	로그인 정보
	 * @param String[][] salesList	기 판매된 상품 목록
	 * @return	String salesCode	주문코드 
	 */
	private String minusSales(String title, String[] accessInfo, String[][] salesList) {
		String salesCode = null;

		this.display(title);

		this.display(" [ ");
		for(int i=0; i<accessInfo.length; i++) {
			this.display(accessInfo[i]);
			if(i!=accessInfo.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");

		this.display(" [반품처리]\n\n");

		// 판매상품리스트 : 다차원배열 
		if(salesList != null) {
			int tot = 0;
			this.display("------------------------------------------\n");
			this.display("\t 코드 \t상품명\t 단가  \t수량 ("+salesList.length+")\n");
			this.display("------------------------------------------\n");

			for(int i=0; i<salesList.length; i++) {
				for(int j=0; j<salesList[i].length-1; j++) {
					this.display("\t"+ salesList[i][j]);
				}
				tot += (Integer.parseInt(salesList[i][2]) * Integer.parseInt(salesList[i][3]));
				this.display("\n");
			}

			this.display("------------------------------------------\n");
			this.display("                           합계 [ " + tot +" ]\n\n");

			this.display("  1. 전체반품   			2. 부분반품\n");
			this.display("  0. 이전화면");
			this.display(" _____________________________ Select : ");

			salesCode = this.sc.next();
		} else {
			// 반품할 주문 코드 입력
			this.display("  주문코드 : ");
			salesCode = this.sc.next();
		}

		return salesCode;
	}

	/** 2020-10-08 상품 반품
	 * @param String title			프로그램 타이틀	
	 * @param String[] accessInfo	로그인 정보
	 * @param String[][] salesList	기 판매된 상품 목록
	 * @return	String[] salesCodes	반품할 상품 코드리스트
	 */

	//부분반품
	private int[] selectRefund(String title, String[] accessInfo, String[][] salesList) {

		int[] returnIndex = new int[salesList.length];
		int count = 0;

		this.display(title);

		this.display(" [ ");
		for(int i=0; i<accessInfo.length; i++) {
			this.display(accessInfo[i]);
			if(i!=accessInfo.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");

		this.display(" [반품처리]\n\n");

		// 판매상품리스트 : 다차원배열 
		if(salesList != null) {
			int tot = 0;
			this.display("------------------------------------------\n");
			this.display("\t번호 \t코드 \t상품명\t 단가  \t수량 ("+salesList.length+")\n");
			this.display("------------------------------------------\n");

			for(int i=0; i<salesList.length; i++) {
				this.display("\t " + (i+1));
				for(int j=0; j<salesList[i].length-1; j++) {
					this.display("\t"+ salesList[i][j]);
				}
				this.display("\n");
			}

			this.display("------------------------------------------\n");
			while(true) {
				this.display(" _____________________________ 반품번호 : ");
				returnIndex[count] = Integer.parseInt(this.sc.next());
				this.display(" _____________________________ More(y/n)? : ");
				String more = this.sc.next();
				if(more.equals("y")) {
					count++;
				}else {
					break;
				}
			}
		} 

		return returnIndex;

		//		String refundNum = null;
		//		String[] refundGood = null;
		//
		//		this.display(title);
		//
		//		this.display(" [ ");
		//		for(int i=0; i<accessInfo.length; i++) {
		//			this.display(accessInfo[i]);
		//			if(i!=accessInfo.length-1) {this.display("   ");}
		//		}
		//		this.display(" ]\n\n");
		//
		//		this.display(" [반품처리]\n\n");
		//
		//		// 판매상품리스트 : 다차원배열 
		//		if(salesList != null) {
		//			int tot = 0;
		//			this.display("------------------------------------------\n");
		//			this.display("\t 코드 \t상품명\t 단가  \t수량 ("+salesList.length+")\n");
		//			this.display("------------------------------------------\n");
		//
		//			for(int i=0; i<salesList.length; i++) {
		//
		//				this.display(salesList[i][5]);
		//				for(int j=0; j<salesList[i].length-2; j++) {
		//					this.display("\t" + salesList[i][j]);
		//				}
		//				tot += (Integer.parseInt(salesList[i][2]) * Integer.parseInt(salesList[i][3]));
		//				this.display("\n");
		//			}
		//
		//			this.display("------------------------------------------\n");
		//
		//			this.display(" _____________________________ 반품번호 : ");
		//			refundNum = sc.next();
		//
		//			for(int i=0;i<salesList.length;i++) {
		//				if(salesList[i][5].equals((Integer.parseInt(refundNum)+1)+"")) {
		//					for(int j=0;j<salesList[i].length;j++) {
		//						refundGood[j]=salesList[i][j];
		//					}
		//
		//				}
		//			}
		//
		//		}
		//		return refundGood;

	}



	/** 상품코드 비교 & 수량증가 
	 * @param	String[][] salesInfo		이전 상품목록
	 * 			String goodsCode			현재 입력한 상품코드
	 * @return 	boolean result				상품목록이 포함되어 있는
	 * 										상품코드와 현재 입력된
	 * 										상품코드를 비교하여 
	 * 										같으면 상품목록의 수량을
	 * 										증가시키고 True를 리턴
	 * */
	private boolean isCompareGoodsCode(String[][] salesInfo, String goodsCode) {
		boolean result = false;

		for(int i=0; i<salesInfo.length; i++) {
			if(goodsCode.equals(salesInfo[i][0])) {
				salesInfo[i][3] = (Integer.parseInt(salesInfo[i][3]) + 1) + "";
				result = true;
				break;
			}
		}

		return result;
	}

	/** 유효기간 체크 
	 * @param	String expireDate
	 * @return	boolean result 		상품별 유효기간을 전달 받아
	 *  							오늘 날짜보다 크면 True를 리턴
	 * */
	private boolean isExpireDate(String expireDate) {
		boolean result = false;
		/* 유효기간 비교를 위한 금일날짜 가져오기 */
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		if(Integer.parseInt(sdf.format(today)) 
				< Integer.parseInt(expireDate)) {
			result = true;
		}
		return result;
	}

	// payment
	private boolean payments(String[][] goodsInfoList) {
		boolean isPayment;
		int tot = 0;
		int money = 0;

		for(int i=0; i<goodsInfoList.length; i++) {
			/* 유효기간이 지난 상품은 상품목록의 합계금액 계산에서 제외 */
			if(isExpireDate(goodsInfoList[i][4])){
				tot += (Integer.parseInt(goodsInfoList[i][2]) * Integer.parseInt(goodsInfoList[i][3]));
			}		
		}

		this.display(" 받은금액 : ");
		money = Integer.parseInt(this.sc.next());
		this.display(" 거스름돈 : " + (money - tot));

		this.display("\t결제하시겠습니까(y/n)? : ");
		isPayment = this.sc.next().equals("y")? true : false;

		return isPayment;
	}

	// Sales 주문시 첫 번째 입력 상품 : 상품목록 없음
	private String sales(String title, String[] accessInfo) {
		String goodsCode = null;

		this.display(title);

		this.display(" [ ");
		for(int i=0; i<accessInfo.length; i++) {
			this.display(accessInfo[i]);
			if(i!=accessInfo.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");

		this.display(" [상품판매]\n\n");
		this.display("  상품코드 : ");

		goodsCode = this.sc.next();
		return goodsCode; 
	}

	// Sales 주문시 첫 번째 이후 입력 상품 : 상품목록 있음
	private String sales(String title, String[] accessInfo, 
			String[][] goodsInfoList) {
		String goodsCode = null;

		this.display(title);

		this.display(" [ ");
		for(int i=0; i<accessInfo.length; i++) {
			this.display(accessInfo[i]);
			if(i!=accessInfo.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");

		this.display(" [상품판매]\n\n");

		// 상품리스트 : 다차원배열 
		if(goodsInfoList != null) {
			int tot = 0;
			this.display("------------------------------------------\n");
			this.display("\t 코드 \t상품명\t 단가  \t수량 ("+goodsInfoList.length+")\n");
			this.display("------------------------------------------\n");

			for(int i=0; i<goodsInfoList.length; i++) {
				for(int j=0; j<goodsInfoList[i].length-2; j++) {
					/* 유효기간이 지난 경우 미출력 */
					if(isExpireDate(goodsInfoList[i][4])){
						this.display("\t"+ goodsInfoList[i][j]);
					}
				}
				/* 유효기간이 지난 경우 합계 계산 X */
				if(isExpireDate(goodsInfoList[i][4])){
					tot += (Integer.parseInt(goodsInfoList[i][2]) * Integer.parseInt(goodsInfoList[i][3]));
					this.display("\n");
				}

			}

			this.display("------------------------------------------\n");
			this.display("                           합계 [ " + tot +" ]\n\n");

			// 진행여부
			this.display(" 진행상태[Continue/Payment] : ");
			if(this.sc.next().equals("c")) {
				this.display("  상품코드 : ");
				goodsCode = this.sc.next();
			}else {
				goodsCode = "p";
			}
		}

		return goodsCode; 
	}

	// 판매예정 상품리스트 만들기
	private String[][] makeList(String[] goodsInfo, 
			String[][] goodsInfoList){
		String[][] temp = null;

		if(this.isExpireDate(goodsInfo[4])) {
			if(goodsInfoList == null) {
				goodsInfoList = new String[1][];
			}else {
				// 기존의 데이터 옮기기
				temp = new String[goodsInfoList.length][];
				for(int i=0; i<temp.length; i++) {
					temp[i] = goodsInfoList[i]; 
				}
				goodsInfoList = null;
				//새로운 데이터 저장을 위한 Re-Allocation
				goodsInfoList = new String[temp.length+1][];
				for(int i=0; i<temp.length; i++) {
					goodsInfoList[i] = temp[i]; 
				}
			}
			goodsInfoList[goodsInfoList.length-1] = goodsInfo;
		}
		return goodsInfoList;
	}

	// LogIn Job Contol
	private String[] logIn(String title) {
		String[] userInfo = new String[3];
		userInfo[0] = "A1";

		this.display(title);
		this.display(" [ Log In ]\n\n"); 
		this.display(" [ Employee Code ] : ");
		userInfo[1] = sc.next();
		if(userInfo[1].equals("0")) {
			userInfo = null;
		}else {
			this.display(" [ Access Code ]   : ");
			userInfo[2] = sc.next();
		}

		return userInfo;
	}



	// Services Selection Job Control
	private String selectService(String title, String[] result) {
		this.display(title);

		this.display(" [ ");
		for(int i=0; i<result.length; i++) {
			this.display(result[i]);
			if(i!=result.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");

		this.display(" [서비스 선택]\n\n");
		this.display("   1. 상품판매             2. 상품반품\n");

		if(result[2].equals("Manager")) {
			this.display("   3. 직원관리             4. 영업관리\n");
		}
		this.display("   0. 이전화면\n");

		this.display(" ________________________________ Select : ");

		return sc.next();
	}

	//상품관리메인
	private String goodsManagement(String title, String[] result) {
		this.display(title);

		this.display(" [ ");
		for(int i=0; i<result.length; i++) {
			this.display(result[i]);
			if(i!=result.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");

		this.display(" [상품관리]\n\n");
		this.display("   1. 상품등록             2. 정보수정\n");
		this.display("   3. 일별현황             4. 월별현황\n");
		this.display("   5. 베스트상품          0. 이전화면\n");

		this.display(" ________________________________ Select : ");

		return sc.next();
	}

	// 상품등록
	private String[] goodsRegistration(String title, String[] result) {
		String[] goodInfo = new String[6];

		this.display(title);

		this.display(" [ ");
		for(int i=0; i<result.length; i++) {
			this.display(result[i]);
			if(i!=result.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");

		this.display(" [상품등록]\n\n");

		this.display(" [ Goods Code ] : ");
		goodInfo[0] = sc.next();
		this.display(" [ Goods Name ]   : ");
		goodInfo[1] = sc.next();
		this.display(" [ Goods Price ] : ");
		goodInfo[2] = sc.next();
		this.display(" [ Expire Date ]: ");
		goodInfo[3] = sc.next();
		this.display(" [ Goods Qty ]: ");
		goodInfo[4] = sc.next();
		this.display(" [ Safe Qty ]: ");
		goodInfo[5] = sc.next();

		return goodInfo;
	}

	// GoodsInfo Modify
	private String[] goodsInfoMod(String title, String[] accessInfo) {

		String[] goodInfo = new String[2];
		this.display(title);
		this.display(" [ ");
		for(int i=0; i<accessInfo.length; i++) {
			this.display(accessInfo[i]);
			if(i!=accessInfo.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");

		this.display(" [ GoodsInfo Modify ]\n\n"); 
		this.display(" [ Good Code ] : ");
		goodInfo[0]=sc.next();
		this.display(" [ Good  Qty ] : ");
		goodInfo[1] = sc.next();

		return goodInfo;
	}

	// 직원관리 메인
	private String userManagement(String title, String[] result) {
		this.display(title);

		this.display(" [ ");
		for(int i=0; i<result.length; i++) {
			this.display(result[i]);
			if(i!=result.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");

		this.display(" [직원관리]\n\n");
		this.display("   1. 직원등록             2. 정보수정\n");
		this.display("   0. 이전화면\n");

		this.display(" ________________________________ Select : ");

		return sc.next();
	}

	// 직원등록
	private String[] userRegistration(String title, String[] result) {
		String[] userInfo = new String[5];

		this.display(title);

		this.display(" [ ");
		for(int i=0; i<result.length; i++) {
			this.display(result[i]);
			if(i!=result.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");

		this.display(" [직원등록]\n\n");

		this.display(" [ Employee  Code ] : ");
		userInfo[0] = sc.next();
		this.display(" [ Access   Code  ]   : ");
		userInfo[1] = sc.next();
		this.display(" [ Employee Name  ] : ");
		userInfo[2] = sc.next();
		this.display(" [ Employee Phone ]: ");
		userInfo[3] = sc.next();
		this.display(" [ Employee Level ]: ");
		userInfo[4] = sc.next();

		return userInfo;
	}

	// Access Code Modify
	private String[] acceessCodeMod(String title, String[] accessInfo) {
		String[] userInfo = new String[2];

		this.display(title);
		this.display(" [ ");
		for(int i=0; i<accessInfo.length; i++) {
			this.display(accessInfo[i]);
			if(i!=accessInfo.length-1) {this.display("   ");}
		}
		this.display(" ]\n\n");

		this.display(" [ Access Code Modify ]\n\n"); 
		this.display(" [ Employee Code ] : ");
		userInfo[0] = sc.next();
		this.display(" [ Access Code ]   : ");
		userInfo[1] = sc.next();

		return userInfo;
	}

	// 타이틀 작성
	private String makeTitle() {
		StringBuffer sb = new StringBuffer();

		sb.append("\n\n\n\n");
		sb.append("***********************************************\n");
		sb.append("\n");
		sb.append("         Point Of Sales System v1.0\n");
		sb.append("\n");   
		sb.append("                      Designed by HoonZzang\n");
		sb.append("\n");
		sb.append("***********************************************\n");
		sb.append("\n");

		return sb.toString();
	}

	// 화면 출력
	private void display(String text) {
		System.out.print(text);
	}
}
