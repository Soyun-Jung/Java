package controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.inputService;

/**
 * Servlet implementation class inputservlet
 */
@WebServlet("/inputDB")
public class inputController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public inputController() {
        super();
        
    }

	protected void doProccess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		String input1 = request.getParameter("data1");
		String input2 = request.getParameter("data2");
		System.out.println(input1);
		System.out.println(input2);
		
		//service ��Ű���� inputService�� import
		inputService inputsvc = new inputService();
		
		//input ����� ��� ���� booleanŸ���� inputResult ����
		boolean svcResult = inputsvc.inputDB(input1, input2);
		
		if(svcResult) {
			//������������ �̵�
			RequestDispatcher dispatcher = request.getRequestDispatcher("inputSuccess.jsp");
			dispatcher.forward(request, response);
			
		} else {
			//������������ �̵�
			RequestDispatcher dispatcher = request.getRequestDispatcher("inputFail.jsp");
			dispatcher.forward(request, response);
			
		}
		
		
		
		
	}

	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doProccess(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doProccess(request, response);
	}

}
