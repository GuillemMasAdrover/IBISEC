package servlet.actuacio;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Empresa;
import bean.Incidencia;
import bean.Partida;
import bean.User;
import bean.ControlPage.SectionPage;
import core.ActuacioCore;
import core.ControlPageCore;
import core.CreditCore;
import core.EmpresaCore;
import core.IncidenciaCore;
import core.UsuariCore;
import utils.MyUtils;

/**
 * Servlet implementation class CreateCompletActuacioServlet
 */
@WebServlet("/createCompletActuacio")
public class CreateCompletActuacioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateCompletActuacioServlet() {
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
        }else if (!UsuariCore.hasPermision(conn, usuari, SectionPage.actuacio_modificar)) {
    		response.sendRedirect(request.getContextPath() + "/");	 	
 	   	}else{ 	   		
	        List<Partida> partidesList = new ArrayList<Partida>();
	        List<Empresa> empresesList = new ArrayList<Empresa>();
	        List<User> usuarisList = new ArrayList<User>();
	        try {
				request.setAttribute("nouCodi", ActuacioCore.getNewCode(conn));
				partidesList = CreditCore.getPartides(conn, false);
				empresesList = EmpresaCore.getEmpreses(conn);
				usuarisList = UsuariCore.llistaUsuaris(conn, true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        request.setAttribute("partidesList", partidesList);
	 	    request.setAttribute("empresesList", empresesList);
	 	    request.setAttribute("llistaUsuaris", usuarisList);
	        request.setAttribute("menu", ControlPageCore.renderMenu(conn, usuari,"ActuacionsManuals"));
	        RequestDispatcher dispatcher = request.getServletContext()
	                .getRequestDispatcher("/WEB-INF/views/actuacio/createCompletActuacioView.jsp");
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
