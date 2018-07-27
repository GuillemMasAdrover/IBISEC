package servlet.bastanteo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;

import bean.Bastanteo;
import bean.Bastanteo.Escritura;
import core.BastanteosCore;
import core.EmpresaCore;
import utils.Fitxers;
import utils.MyUtils;

/**
 * Servlet implementation class DoEditEscrituraServlet
 */
@WebServlet("/doEditEscritura")
public class DoEditEscrituraServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DoEditEscrituraServlet() {
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
       	
       	String ref = multipartParams.getParametres().get("ref");
       	String refBastanteo = multipartParams.getParametres().get("refBastanteo");
       	Escritura escritura = new Bastanteo().new Escritura();
       	String errorString = null;
       	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
       	try {
       		escritura.setEscritura(multipartParams.getParametres().get("escritura"));
			if (multipartParams.getParametres().get("dataEscritura") != null && ! multipartParams.getParametres().get("dataEscritura").isEmpty()) {
				escritura.setDataEscritura(formatter.parse(multipartParams.getParametres().get("dataEscritura")));
			}
			escritura.setNumProtocol(multipartParams.getParametres().get("nProtocol"));	
			escritura.setNotari(multipartParams.getParametres().get("notari"));
			BastanteosCore.modificarEscritura(conn, escritura, ref);
		} catch (ParseException | SQLException | NamingException e) {
			errorString = e.toString();
		}
		// Store infomation to request attribute, before forward to views.
		request.setAttribute("errorString", errorString);
		request.setAttribute("escritura", escritura);
 
 
		// If error, forward to Edit page.
		if (errorString != null) {
           RequestDispatcher dispatcher = request.getServletContext()
                   .getRequestDispatcher("/WEB-INF/views/bastanteos/editEscrituraView.jsp");
           dispatcher.forward(request, response);
		}
        
		// If everything nice.
		// Redirect to the product listing page.            
		else {
           response.sendRedirect(request.getContextPath() + "/bastanteo?ref=" + refBastanteo);
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
