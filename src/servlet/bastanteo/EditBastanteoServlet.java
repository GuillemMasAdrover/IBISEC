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

import bean.Expedient;
import bean.InformeActuacio;
import bean.User;
import bean.Bastanteo;
import bean.ControlPage.SectionPage;
import bean.Empresa;
import core.BastanteosCore;
import core.ControlPageCore;
import core.EmpresaCore;
import core.ExpedientCore;
import core.InformeCore;
import core.UsuariCore;
import utils.MyUtils;

/**
 * Servlet implementation class EditBastanteoServlet
 */
@WebServlet("/bastanteo")
public class EditBastanteoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditBastanteoServlet() {
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
	        Bastanteo bastanteo = new Bastanteo();
	        List<Empresa> empresesList = new ArrayList<Empresa>();
	        String errorString = null;	      
	        try {
	        	bastanteo = BastanteosCore.findBastanteo(conn, ref);
	        	empresesList = EmpresaCore.getEmpreses(conn); 	
	        	empresesList.addAll(EmpresaCore.getEmpresesUTE(conn)); 	
	        } catch (SQLException | NamingException e) {
	            e.printStackTrace();
	            errorString = e.getMessage();
	        }	
	        // If no error.
	        // The product does not exist to edit.
	        // Redirect to productList page.
	        if (errorString != null || bastanteo.getRef() == null) {
	            response.sendRedirect(request.getServletPath() + "/bastanteos");
	            return;
	        }
	 
	        // Store errorString in request attribute, before forward to views.
	        request.setAttribute("empresesList", empresesList);
	        request.setAttribute("errorString", errorString);
	        request.setAttribute("bastanteo", bastanteo);
	        request.setAttribute("menu", ControlPageCore.renderMenu(conn, usuari,"bastanteos"));
	        
	        RequestDispatcher dispatcher = request.getServletContext()
	                .getRequestDispatcher("/WEB-INF/views/bastanteos/editBastanteoView.jsp");
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
