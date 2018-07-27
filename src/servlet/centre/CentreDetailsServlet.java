package servlet.centre;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Centre;
import bean.User;
import bean.ControlPage.SectionPage;
import core.CentreCore;
import core.ControlPageCore;
import core.UsuariCore;
import utils.MyUtils;

/**
 * Servlet implementation class CentreDetailsServlet
 */
@WebServlet("/centreDetalls")
public class CentreDetailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CentreDetailsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = MyUtils.getStoredConnection(request);
		   User usuari = MyUtils.getLoginedUser(request.getSession());
		   if (usuari == null){
			   response.sendRedirect(request.getContextPath() + "/");
		   }else if (!UsuariCore.hasPermision(conn, usuari, SectionPage.centres_detalls)) {
	   		response.sendRedirect(request.getContextPath() + "/");	 	
		   }else{				   
			   String codi = request.getParameter("codi");
		       String errorString = null;
		       boolean canViewIncidencies = false;
		       boolean canCreateTasca = true;
		       Centre centre = new Centre();			      
		       try {
		    	  centre = CentreCore.findCentre(conn, codi, true);
		    	  canViewIncidencies = UsuariCore.hasPermision(conn, usuari, SectionPage.incidencia_list);		    	  
		       } catch (SQLException e) {
		           e.printStackTrace();
		           errorString = e.getMessage();
		       }
		  
		       // Store info in request attribute, before forward to views
		       request.setAttribute("errorString", errorString);
		       request.setAttribute("canViewIncidencies", canViewIncidencies);
		       request.setAttribute("canCreateTasca", canCreateTasca);
		       request.setAttribute("centre", centre);
		       request.setAttribute("menu", ControlPageCore.renderMenu(conn, usuari, "centres"));
		       // Forward to /WEB-INF/views/homeView.jsp
		       // (Users can not access directly into JSP pages placed in WEB-INF)
		       RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/views/centre/centreDetailView.jsp");
		        
		       dispatcher.forward(request, response);
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
