package servlet.credit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Partida;
import core.CreditCore;
import utils.MyUtils;

/**
 * Servlet implementation class doCreatePartidaServlet
 */
@WebServlet("/DoCreatePartida")
public class DoCreatePartidaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DoCreatePartidaServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = MyUtils.getStoredConnection(request);
		 
		String codi = request.getParameter("codi");	
		String nom = request.getParameter("nom");
		double totalPartida =  Double.parseDouble(request.getParameter("import").replace(",", "."));	
		String tipus = request.getParameter("idTipus");		
		Partida partida = new Partida(codi, nom, totalPartida, tipus, true); 
		String errorString = null;	
	       if (errorString == null) {
	           try {
	        	   int idUsuari = MyUtils.getLoginedUser(request.getSession()).getIdUsuari();
	               CreditCore.novaPartida(conn, partida, idUsuari);
	           } catch (SQLException e) {
	               e.printStackTrace();
	               errorString = e.getMessage();
	           }
	       }
	        
	       // Store infomation to request attribute, before forward to views.
	       request.setAttribute("errorString", errorString);
	       request.setAttribute("partida", partida);	      
	       // If error, forward to Edit page.
	       if (errorString != null) {
	           RequestDispatcher dispatcher = request.getServletContext()
	                   .getRequestDispatcher("/WEB-INF/views/credit/createPartidaView.jsp");
	           dispatcher.forward(request, response);
	       }
	 
	       // If everything nice.
	       // Redirect to the product listing page.            
	       else {
	           response.sendRedirect(request.getContextPath() + "/credit");
	       }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
