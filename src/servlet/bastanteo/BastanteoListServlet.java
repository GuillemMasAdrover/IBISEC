package servlet.bastanteo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.User;
import bean.Bastanteo;
import bean.ControlPage.SectionPage;
import core.BastanteosCore;
import core.ControlPageCore;
import core.ExpedientCore;
import core.UsuariCore;
import utils.MyUtils;

/**
 * Servlet implementation class BastanteoListServlet
 */
@WebServlet("/bastanteos")
public class BastanteoListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BastanteoListServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = MyUtils.getStoredConnection(request);
		User usuari = MyUtils.getLoginedUser(request.getSession());
		if (usuari == null) {
			response.sendRedirect(request.getContextPath() + "/preLogin");
		}else if (!UsuariCore.hasPermision(conn, usuari, SectionPage.bastanteos_list)) {
    		response.sendRedirect(request.getContextPath() + "/");	
		} else {
			String errorString = "";
			List<Bastanteo> bastanteosList = new ArrayList<Bastanteo>();
			List<String> anysList = new ArrayList<String>();
			String filtrar = request.getParameter("filtrar");			
			Calendar now = Calendar.getInstance();
			String yearInString = request.getParameter("any");
			int year = now.get(Calendar.YEAR);
			try {
				if (filtrar != null) {	
					
				} else {
					 yearInString = String.valueOf(year);
				}
				anysList = ExpedientCore.getAnysExpedients(conn);
				bastanteosList = BastanteosCore.getBastanteos(conn, yearInString);								
			} catch (SQLException | NamingException e) {
				e.printStackTrace();
				errorString = e.getMessage();
			}

			// Store info in request attribute, before forward to views
			request.setAttribute("anyFilter", yearInString);
			request.setAttribute("errorString", errorString);
			request.setAttribute("anysList", anysList);
			request.setAttribute("bastanteosList", bastanteosList);
			request.setAttribute("menu", ControlPageCore.renderMenu(conn, usuari,"bastanteos"));
			// Forward to /WEB-INF/views/homeView.jsp
			// (Users can not access directly into JSP pages placed in WEB-INF)
			RequestDispatcher dispatcher = this.getServletContext()
					.getRequestDispatcher("/WEB-INF/views/bastanteos/bastanteoListView.jsp");

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
