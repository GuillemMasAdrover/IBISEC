package servlet.licitacio;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Resultat;
import core.ActuacioCore;
import utils.MyUtils;
 
@WebServlet(urlPatterns = { "/licitacions"})
public class LicitacioListServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;
 
   public LicitacioListServlet() {
       super();
   }
 
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
	   if (MyUtils.getLoginedUser(request.getSession()) == null){
		   response.sendRedirect(request.getContextPath() + "/");
	   }else{
		   Connection conn = MyUtils.getStoredConnection(request);		   
	       String errorString = null;
	       Resultat result = new Resultat();	      
	       try {
	           result = ActuacioCore.topAcuacions(conn);	           
	       } catch (SQLException e) {
	           e.printStackTrace();
	           errorString = e.getMessage();
	       }
	       
	       // Store info in request attribute, before forward to views
	       request.setAttribute("errorString", errorString);
	       request.setAttribute("actuacionsList", result.getLlistaActuacions());
	       // Forward to /WEB-INF/views/homeView.jsp
	       // (Users can not access directly into JSP pages placed in WEB-INF)
	       RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/views/licitacio/licitacioListView.jsp");
	        
	       dispatcher.forward(request, response);
	   }
   }
 
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
       doGet(request, response);
   }
 
}