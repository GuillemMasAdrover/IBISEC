package servlet.actuacio;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;

import bean.Expedient;
import bean.InformeActuacio;
import bean.Oferta;
import bean.User;
import core.ActuacioCore;
import core.CreditCore;
import core.ExpedientCore;
import core.InformeCore;
import core.LlicenciaCore;
import core.OfertaCore;
import core.TascaCore;
import core.UsuariCore;
import utils.Fitxers;
import utils.MyUtils;
import utils.Fitxers.Fitxer;

/**
 * Servlet implementation class DoCanvisActuacio
 */
@WebServlet("/DoCanvisActuacio")
public class DoCanvisActuacioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DoCanvisActuacioServlet() {
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
		String ipRemota = request.getRemoteAddr();
		//Registrar actuació
	    String idActuacio = multipartParams.getParametres().get("idActuacio");
	    String idIncidencia = multipartParams.getParametres().get("idIncidencia");
	    String idInforme = multipartParams.getParametres().get("idInforme");
	    String aprovarPA = multipartParams.getParametres().get("aprovarPA");
	    String aprovarPD = multipartParams.getParametres().get("aprovarPD");
	    int idTasca = -1;
	    if (multipartParams.getParametres().get("idTasca") != null) idTasca = Integer.parseInt(multipartParams.getParametres().get("idTasca"));
	    String obrir = multipartParams.getParametres().get("obrir");
	    String tancar = multipartParams.getParametres().get("tancar");	   
	    String motiu = multipartParams.getParametres().get("motiu");
	    User Usuari = MyUtils.getLoginedUser(request.getSession());	   
	    List<Fitxer> fitxers = multipartParams.getFitxers(); 	   		
	    if (aprovarPA != null) { 
	    	
	    }else if (aprovarPD != null) {
	    	try {
	    		InformeActuacio informe = new InformeActuacio();
				ActuacioCore.aprovar(conn, idActuacio, Usuari.getIdUsuari());
				ActuacioCore.actualitzarActuacio(conn, idActuacio, "Autorització generada");
				OfertaCore.aprovarOferta(conn, idInforme, Usuari.getIdUsuari());
				if (OfertaCore.findOfertaSeleccionada(conn, idInforme) != null) {
					CreditCore.assignar(conn, idInforme, OfertaCore.findOfertaSeleccionada(conn, idInforme).getPlic());
				}	   			
	   			TascaCore.nouHistoric(conn, String.valueOf(idTasca), "Autorització generada", Usuari.getIdUsuari(), ipRemota, "automatic");
	   			TascaCore.tancar(conn, idTasca);	   			
	   			if (idInforme.contains("-MOD-")) {
	   				informe = InformeCore.getMoficacioInforme(conn, idInforme);
	   				//Notificam la modificació al cap
	   				Fitxers.guardarFitxer(conn, fitxers, informe.getActuacio().getIdIncidencia(), idActuacio, "", "", informe.getIdInfOriginal(), idInforme, "Autorització Despesa modificació", Usuari.getIdUsuari());
		    		TascaCore.nouHistoric(conn, String.valueOf(idTasca), "Autorització Proposta modificació", Usuari.getIdUsuari(), ipRemota, "automatic");			   		
			   		TascaCore.tancar(conn, idTasca);
			   		CreditCore.assignar(conn, idInforme, informe.getOfertaSeleccionada().getPlic());
			   		if (informe.getUsuari() != null) TascaCore.novaTasca(conn, "generic", informe.getUsuari().getIdUsuari(), Usuari.getIdUsuari(), idActuacio, idIncidencia, "S'ha aprovat la despesa de modificació per a l'informe: " + informe.getIdInfOriginal(), "Aprovació proposta despesa modificació",informe.getIdInf(),null, ipRemota, "automatic");
	   			} else {
	   				Fitxers.guardarFitxer(conn, fitxers, idIncidencia, idActuacio, "", "", "", idInforme, "Autorització  Proposta despesa", Usuari.getIdUsuari());
	   				if (OfertaCore.findOfertaSeleccionada(conn, idInforme) != null) {
	   					informe = InformeCore.getInformePrevi(conn, idInforme, false);
		   				//Crear tasca redacció contracte
		   				InformeCore.modificarEstat(conn, idInforme, "execucio");
		   				int usuariTasca = Integer.parseInt(getServletContext().getInitParameter("idUsuariRedaccioContracte"));   	
		   				if (informe.getExpcontratacio().getContracte().equals("major")) {
		   					usuariTasca = UsuariCore.findUsuarisByRol(conn, "CAP,JUR").get(0).getIdUsuari();
		   				} 
		   				Calendar cal = Calendar.getInstance(); 
		   				Expedient expedient = informe.getExpcontratacio();
		   				expedient.setDataAdjudicacio(cal.getTime());  
			    		ExpedientCore.updateExpedient(conn, expedient);
						TascaCore.novaTasca(conn, "contracte", usuariTasca, Usuari.getIdUsuari(), idActuacio, idIncidencia, "Redactar contracte per expedient " + informe.getExpcontratacio().getExpContratacio(), "Redacció contracte",informe.getIdInf(),null, ipRemota, "automatic");
			   			
			   			//Nova tasca llicència
		   				if (informe.getPropostaInformeSeleccionada().isLlicencia() && informe.getPropostaInformeSeleccionada().getTipusLlicencia().equals("comun")) {
							usuariTasca = Integer.parseInt(getServletContext().getInitParameter("idUsuariLlicencies"));   		
							TascaCore.novaTasca(conn, "generic", usuariTasca, Usuari.getIdUsuari(), idActuacio, idIncidencia, "Sol·licitar comunicació prèvia obra", "Sol·licitud comunicació prèvia",informe.getIdInf(),null, ipRemota, "automatic");
							LlicenciaCore.novaLlicencia(conn, informe.getExpcontratacio().getExpContratacio(), informe.getPropostaInformeSeleccionada().getTipusLlicencia());
		   				}
	   				}	   				
	   			}	   			
			} catch (SQLException | NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   		}else if (tancar != null) { // tancam actuació
   			try {
   				ActuacioCore.actualitzarActuacio(conn, idActuacio, "Tancar actuació");
   				ActuacioCore.tancar(conn, idActuacio, motiu, Usuari.getIdUsuari());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   			
   		}else if (obrir != null) { // obrim actuació
   			try {
				ActuacioCore.obrir(conn, idActuacio);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   		} 
	    if (aprovarPA != null) { 
	    	response.sendRedirect(request.getContextPath() + "/CrearDocument?tipus=AutoritzacioPAObres&idIncidencia=" + idIncidencia + "&idActuacio=" + idActuacio + "&idInforme=" + idInforme); 
	    }else{
	    	response.sendRedirect(request.getContextPath() + "/actuacionsDetalls?ref=" + idActuacio);
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
