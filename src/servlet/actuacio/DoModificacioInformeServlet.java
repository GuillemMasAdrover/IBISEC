package servlet.actuacio;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;

import bean.InformeActuacio;
import bean.InformeActuacio.PropostaInforme;
import bean.Oferta;
import bean.User;
import core.ActuacioCore;
import core.InformeCore;
import core.OfertaCore;
import core.TascaCore;
import core.UsuariCore;
import utils.Fitxers;
import utils.MyUtils;

/**
 * Servlet implementation class DoModificacioInformeServlet
 */
@WebServlet("/DoModificacioInforme")
public class DoModificacioInformeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DoModificacioInformeServlet() {
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
	    String idInforme = multipartParams.getParametres().get("idInforme");
	    String objecte = multipartParams.getParametres().get("objecteModificacio");
	    String tipusIncidencia = multipartParams.getParametres().get("tipusIncidencia");
	    User Usuari = MyUtils.getLoginedUser(request.getSession());	   
	    boolean reqLlicencia = false;
	    if (multipartParams.getParametres().get("reqLlicencia") != null) reqLlicencia = multipartParams.getParametres().get("reqLlicencia").equals("si");	   
	    String tipusLlicencia = multipartParams.getParametres().get("tipusLlicencia");	   	   
	    double pbase = 0;
	    double iva = 0;
	    double plic = 0;
	    
	    if (tipusIncidencia.equals("penalitzacio")) {	
	    	plic = Double.parseDouble(multipartParams.getParametres().get("plicPenalitzacio").replace(',','.'));
	    	if (plic > 0) plic = -1*plic;
	    } else {
	    	pbase = Double.parseDouble(multipartParams.getParametres().get("pbase").replace(',','.'));
	    	iva = Double.parseDouble(multipartParams.getParametres().get("iva"));
	    	plic = Double.parseDouble(multipartParams.getParametres().get("plic").replace(',','.'));	
	    }
	    
	    String termini = multipartParams.getParametres().get("termini");
	    
	    String empresaCif = multipartParams.getParametres().get("llistaEmpreses");	  
	    String propostaTecnica = multipartParams.getParametres().get("propostaTecnica");
	    
	    String errorString = null;	    
	    InformeActuacio informe = new InformeActuacio();
	  	try {	 
	  		informe = InformeCore.getInformePrevi(conn, idInforme, false);
	  		PropostaInforme proposta = informe.new PropostaInforme();
	  		proposta.setTipusObra(informe.getPropostaInformeSeleccionada().getTipusObra());
	  		proposta.setLlicencia(reqLlicencia);
	  		if (reqLlicencia) proposta.setTipusLlicencia(tipusLlicencia);
	  		proposta.setObjecte(objecte);
	  		proposta.setPbase(pbase);
	  		proposta.setIva(iva);
	  		proposta.setPlic(plic);
	  		proposta.setTermini(termini);
	  		
	  		Oferta ofertaProposta = new Oferta();
	  		if (!tipusIncidencia.equals("penalitzacio")) {	 
		  		ofertaProposta.setIdActuacio(informe.getActuacio().getReferencia());
		  		ofertaProposta.setCifEmpresa(empresaCif);
		  		ofertaProposta.setPlic(plic);
		   		ofertaProposta.setPbase(pbase);
		   		ofertaProposta.setIva(iva);			
		  		ofertaProposta.setComentari(propostaTecnica);
	  		}
	  		
	  		//Cream modificació	  		
	  		String idModificacio = InformeCore.afegirModificacioInforme(conn, idInforme, proposta, ofertaProposta, Usuari, tipusIncidencia);	
	  		if (!tipusIncidencia.equals("penalitzacio")) {	 
		  		ofertaProposta.setIdInforme(idModificacio);
		  		ofertaProposta.setSeleccionada(true);
		  		OfertaCore.novaOferta(conn, ofertaProposta, Usuari.getIdUsuari());
	  		}
	  		InformeCore.saveInformeModificacio(conn, informe.getIdIncidencia(), informe.getActuacio().getReferencia(), idInforme, idModificacio, multipartParams.getFitxers(), Usuari.getIdUsuari());
	  		
	  		if (Usuari.getRol().contains("CAP")) {
	  			ActuacioCore.actualitzarActuacio(conn, informe.getActuacio().getReferencia(), "Proposta modificació realitzada");
				int idUsuari = UsuariCore.findUsuarisByRol(conn, "CAP,CONTA").get(0).getIdUsuari();
				if (pbase > 0) {
					TascaCore.novaTasca(conn, "resPartidaModificacio", idUsuari, Usuari.getIdUsuari(), informe.getActuacio().getReferencia(), informe.getActuacio().getIdIncidencia(), "Modificació expedient " + informe.getExpcontratacio().getExpContratacio(), "Modificació expedient " + informe.getExpcontratacio().getExpContratacio(), idModificacio, null, request.getRemoteAddr(), "automatic");
				} else {
					String comentari = "S'ha realitzat la proposta de modificació" + idInforme ;
					TascaCore.novaTasca(conn, "autoritzacioModificacio", idUsuari, Usuari.getIdUsuari(), informe.getActuacio().getReferencia(), informe.getActuacio().getIdIncidencia(), comentari, "Autorització modificació actuació", idInforme, null, request.getRemoteAddr(), "automatic");
				}
				
	  		} else {
	  			//Cream tasca a Cap
	  			if (tipusIncidencia.equals("penalitzacio")) {
	  				TascaCore.novaTasca(conn, "penalitzacio", UsuariCore.finCap(conn, Usuari.getDepartament()).getIdUsuari(), Usuari.getIdUsuari(), informe.getActuacio().getReferencia(), informe.getIdIncidencia(), "Penalització", "Penalització", idModificacio, null, request.getRemoteAddr(), "automatic");
	  			} else {
	  				TascaCore.novaTasca(conn, "modificacio", UsuariCore.finCap(conn, Usuari.getDepartament()).getIdUsuari(), Usuari.getIdUsuari(), informe.getActuacio().getReferencia(), informe.getIdIncidencia(), "Sol·licitud modificació", "Modificació", idModificacio, null, request.getRemoteAddr(), "automatic");
	  			}
	  		}
	   		/*;*/
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorString = e.getMessage();
		} 
	  	
	   	// Store infomation to request attribute, before forward to views.
	   	request.setAttribute("errorString", errorString);
	  	// If error, forward to Edit page.
	   	if (errorString != null) {
	   		RequestDispatcher dispatcher = request.getServletContext()
	   				.getRequestDispatcher("/WEB-INF/views/actuacio/CreateModificacioInformeView.jsp");
	   		dispatcher.forward(request, response);
	   	}// If everything nice. Redirect to the product listing page.            
	   	else {
	   		response.sendRedirect(request.getContextPath() + "/actuacionsDetalls?ref=" + informe.getActuacio().getReferencia());
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
