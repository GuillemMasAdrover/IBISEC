package servlet.actuacio;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.ControlPage.SectionPage;
import bean.Incidencia;
import bean.User;
import core.ActuacioCore;
import core.ControlPageCore;
import core.IncidenciaCore;
import core.UsuariCore;
import utils.MyUtils;

/**
 * Servlet implementation class CreateActuacioServlet
 */
@WebServlet(urlPatterns = { "/createActuacio" })
public class CreateActuacioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateActuacioServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {  
    	Connection conn = MyUtils.getStoredConnection(request);
    	User usuari = MyUtils.getLoginedUser(request.getSession());
        if (usuari == null){
 		   response.sendRedirect(request.getContextPath() + "/");
        }else if (!UsuariCore.hasPermision(conn, usuari, SectionPage.actuacio_modificar)) {
    		response.sendRedirect(request.getContextPath() + "/");	 	
 	   	}else{ 	   		
	        Incidencia incidencia = new Incidencia();
	        try {
				request.setAttribute("nouCodi", ActuacioCore.getNewCode(conn));
				incidencia = IncidenciaCore.findIncidencia(conn, request.getParameter("idIncidencia"));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        request.setAttribute("incidencia", incidencia);
	        request.setAttribute("menu", ControlPageCore.renderMenu(conn, usuari,"Actuacions"));
	        RequestDispatcher dispatcher = request.getServletContext()
	                .getRequestDispatcher("/WEB-INF/views/actuacio/createActuacioView.jsp");
	        dispatcher.forward(request, response);
 	   }
    }
   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
