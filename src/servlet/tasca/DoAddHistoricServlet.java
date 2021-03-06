package servlet.tasca;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.math.NumberUtils;

import bean.User;
import core.TascaCore;
import core.UsuariCore;
import utils.Fitxers;
import utils.MyUtils;

/**
 * Servlet implementation class DoAddHistoric
 */
@WebServlet("/DoAddHistoric")
public class DoAddHistoricServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DoAddHistoricServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		Connection conn = MyUtils.getStoredConnection(request);
		
		Fitxers.formParameters multipartParams = new Fitxers.formParameters();
		try {
			multipartParams = Fitxers.getParamsFromMultipartForm(request);
		} catch (FileUploadException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	    int idTasca = Integer.parseInt(multipartParams.getParametres().get("idTasca"));
	    String idIncidencia = multipartParams.getParametres().get("idIncidencia");
	    String idActuacio = multipartParams.getParametres().get("idActuacio");
	    String reasignar = multipartParams.getParametres().get("reasignar");
	    String tancar = multipartParams.getParametres().get("tancar");
	    User Usuari = MyUtils.getLoginedUser(request.getSession());	   
	    String comentari = multipartParams.getParametres().get("comentari");
	    String idComentari = "0000";
	    String errorString = null;
	   	//Registrar comentari;	   
	   	try {
			idComentari = TascaCore.nouHistoric(conn, idTasca, comentari, Usuari.getIdUsuari());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorString = e.getMessage();
		}
	   	//Guardar adjunts
	   	Fitxers.guardarFitxer(multipartParams.getFitxers(), idIncidencia, idActuacio, "Tasca", String.valueOf(idTasca), idComentari);
	   	
	   	// Store infomation to request attribute, before forward to views.
	   	request.setAttribute("errorString", errorString);
	  	// If error, forward to Edit page.
	   	if (errorString != null) {
	   		RequestDispatcher dispatcher = request.getServletContext()
	   				.getRequestDispatcher("/WEB-INF/views/actuacio/tascaView.jsp");
	   		dispatcher.forward(request, response);
	   	}// If everything nice. Redirect to the product listing page.            
	   	else {
	   		if (reasignar != null) { //reassignem la incid�ncia
	   			String idUsuariNou = multipartParams.getParametres().get("idUsuari");
	   			try {
	   				if (NumberUtils.isNumber(idUsuariNou)) {
	   					TascaCore.reasignar(conn, Integer.parseInt(idUsuariNou), idTasca);
	   				} else {
	   					TascaCore.reasignar(conn, UsuariCore.finCap(conn, idUsuariNou).getIdUsuari(), idTasca);
	   				}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	   			response.sendRedirect(request.getContextPath() + "/tascaList");
	   		}else if (tancar != null) { // tancam incid�ncia
	   			try {
					TascaCore.tancar(conn, idTasca);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	   			response.sendRedirect(request.getContextPath() + "/tascaList");
	   		}else {
	   			response.sendRedirect(request.getContextPath() + "/tasca?id=" + idTasca);
	   		}	   		
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
