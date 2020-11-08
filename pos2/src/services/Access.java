package services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import data.DataAccessObject;
import data.UserInfoBean;

public class Access {
	private DataAccessObject dao;
	
	public Access() {
		dao = new DataAccessObject();
	}

	public void entrance(UserInfoBean uib) {
		// uib.getRequestValue()에 저장되어 있는 
		// Request Value에 따라 logIn, userReg, userMod 메서드로 분기 
		switch(uib.getRequestValue()) {
		case "A1":
			this.logIn(uib);
			break;
		case "A2":
			this.userReg(uib);
			break;
		case "A3":
			this.userMod(uib);
			break;		
		}
	}

	private void logIn(UserInfoBean uib) {

		// 아이디 조회 요청
		if(dao.isEmployee(0, uib)) {
			// 패스워드 일치여부 요청
			if(dao.isAccess(0, uib)){
				// 현재 시스템 날짜시간 가져오기
				Date now = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
				uib.setAccessTime(sdf.format(now));
				
				// history.txt에 기록하기
				dao.setLogInInfo(1, uib);
				// 로그인 사용자 데이터 가져오기
				// history 데이터 가져오기
				dao.getUserInfo(0, uib);				
				
				//필요없는데이터 삭제
				uib.setAccessCode(null);
				uib.setRequestValue(null);
			}
		}
		
	}

	private void userReg(UserInfoBean uib) {
		// 사용자 등록
		dao.userReg(0, uib);
	}

	private void userMod(UserInfoBean uib) {
		ArrayList<UserInfoBean> empList;
		empList = dao.getEmployeesInfo(0);
		/* 	0 : uib : 7777,1234,훈짱,010-5680-8050,M
			1 : uib : 1111,1234,훈,010-5680-8050,A
			2 : uib : 2222,1234,짱,010-5680-8050,A
			3 : uib : 3333,1234,알바,010-5680-8050,A
			4 : uib : 9999,1234,강동훈,010-2190-4128,M
		 */
		
		// 클라이언트로부터 전달받은 EmployeeCode값과 같은 
		// 빈을 리스트안에서 찾아 새로운 AccessCode값 저장
		for(int i=0; i<empList.size(); i++) {
			if(uib.getEmployeeCode().equals(
				empList.get(i).getEmployeeCode())) {
				empList.get(i).setAccessCode(uib.getAccessCode());
				break;
			}
		}
		
		// empList에 저장된 Bean값들을 saler.txt파일에 Overwrite
		dao.setEmployeesInfoModify(0, empList);
	}

}
