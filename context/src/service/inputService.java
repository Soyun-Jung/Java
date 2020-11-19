package service;

import java.sql.Connection;

import dao.inputDAO;
import static dao.inputDAO.*;
import static db.JdbcUtil.*;

public class inputService {

	public boolean inputDB(String input1, String input2) {
		//dao패키지의 inputDAO를 import
		inputDAO dao = inputDAO.getInstance();		
		Connection con = db.JdbcUtil.getConnection();
		dao.setConnection(con);
		
		boolean svcResult = false;
		
		int daoResult = dao.inputDB(input1, input2);
		
		if(daoResult>0) {
			commit(con);
			svcResult=true;
			close(con);
		} else {
			close(con);
			//svcResult=false;
		}
		
		return svcResult;
	}



}
