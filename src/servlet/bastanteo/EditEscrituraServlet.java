package servlet.bastanteo;

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

import bean.Bastanteo;
import bean.Bastanteo.Escritura;
import bean.Empresa;
import bean.User;
import bean.ControlPage.SectionPage;
import core.BastanteosCore;
import core.ControlPageCore;
import core.EmpresaCore;
import core.UsuariCore;
import utils.MyUtils;

/**
 * Servlet implementation class EditEscrituraServlet
 */
@WebServlet("/escritura")
public class EditEscrituraServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditEscrituraServlet() {
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
 		   response.sendRedirect(request.getContextPath() + "/");
    	}else if (!UsuariCore.hasPermision(conn, usuari, SectionPage.bastanteos_modificar)) {
    		response.sendRedirect(request.getContextPath() + "/");	 
 	   	}else{
			String ref = request.getParameter("ref");
			String refBastanteo = request.getParameter("refBastanteo");
	        Escritura escritura = new Bastanteo().new Escritura();
	        String errorString = null;	      
	        try {
	        	escritura = BastanteosCore.findEscritura(conn, ref);	        	
	        } catch (SQLException | NamingException e) {
	            e.printStackTrace();
	            errorString = e.getMessage();
	        }	
	        // If no error.
	        // The product does not exist to edit.
	        // Redirect to productList page.
	        if (errorString != null || escritura.getEscritura() == null) {
	            response.sendRedirect(request.getServletPath() + "/bastanteo?ref=" + refBastanteo);
	            return;
	        }
	 
	        // Store errorString in request attribute, before forward to views.
	        request.setAttribute("errorString", errorString);
	        request.setAttribute("escritura", escritura);
	        request.setAttribute("ref", ref);
	        request.setAttribute("refBastanteo", refBastanteo);	        
	        request.setAttribute("menu", ControlPageCore.renderMenu(conn, usuari,"Empreses"));
	        
	        RequestDispatcher dispatcher = request.getServletContext()
	                .getRequestDispatcher("/WEB-INF/views/bastanteos/editEscrituraView.jsp");
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
