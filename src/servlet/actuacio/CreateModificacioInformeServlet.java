package servlet.actuacio;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Empresa;
import bean.InformeActuacio;
import bean.User;
import bean.ControlPage.SectionPage;
import core.ControlPageCore;
import core.EmpresaCore;
import core.InformeCore;
import core.UsuariCore;
import utils.MyUtils;

/**
 * Servlet implementation class CreateModificacioInformeServlet
 */
@WebServlet("/modificarInforme")
public class CreateModificacioInformeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateModificacioInformeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User usuari = MyUtils.getLoginedUser(request.getSession());
    	Connection conn = MyUtils.getStoredConnection(request);
 	   if (MyUtils.getLoginedUser(request.getSession()) == null){
 		   response.sendRedirect(request.getContextPath() + "/preLogin");
 	   }else if (!UsuariCore.hasPermision(conn, usuari, SectionPage.tasques_detalls)) {
  		response.sendRedirect(request.getContextPath() + "/");	
 	   }else{ 		   
 	       String errorString = null;
 	       String idInforme = request.getParameter("idInforme");  
 	       InformeActuacio informePrevi = new InformeActuacio();
 	       List<Empresa> empresesList = new ArrayList<Empresa>();      
 	      
 	       try {
 	    	   
 	    	   informePrevi = InformeCore.getInformePrevi(conn, idInforme, false);
 	    	   empresesList = EmpresaCore.getEmpreses(conn);
 	    	   empresesList.addAll(EmpresaCore.getEmpresesUTE(conn));
 	       } catch (SQLException | NamingException e) {
 	           e.printStackTrace();
 	           errorString = e.getMessage();
 	       }
 	       // Store info in request attribute, before forward to views
 	       request.setAttribute("errorString", errorString);
 	       request.setAttribute("informePrevi", informePrevi);
 	       request.setAttribute("empresesList", empresesList);
 	       request.setAttribute("menu", ControlPageCore.renderMenu(conn, usuari,"Actuacio"));
 	       request.setAttribute("idUsuariLogg", usuari.getIdUsuari());

 	       RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/views/actuacio/createModificacioInformeView.jsp");
 	        
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
