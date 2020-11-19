package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class inputDAO {
	
	private static inputDAO dao;
	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	

	public static inputDAO getInstance() {
		
		if(dao==null) {
			dao=new inputDAO();
		}
		
		return dao;
	}
	
	//setConnection ¸Å¼­µå
	public void setConnection(Connection con) {
		this.con = con;
	}

	public int inputDB(String input1, String input2) {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO \"input\"(input1, input2) " + 
				"VALUES(?, ?)";
		
		int daoResult = 0;
		
		try {
			pstmt = con.prepareStatement(sql);
			
			pstmt.setNString(1, input1);
			pstmt.setNString(2, input2);
			System.out.println(input1);
			System.out.println(input2);
			
			daoResult=pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return daoResult;
	}

}
