package servlet.actuacio;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Actuacio;
import bean.Expedient;
import bean.Incidencia;
import bean.InformeActuacio;
import bean.Oferta;
import bean.User;
import bean.InformeActuacio.PropostaInforme;
import core.ActuacioCore;
import core.CreditCore;
import core.ExpedientCore;
import core.IncidenciaCore;
import core.InformeCore;
import core.LlicenciaCore;
import core.OfertaCore;
import core.UsuariCore;
import utils.MyUtils;

/**
 * Servlet implementation class DoCreateInformeManualServlet
 */
@WebServlet("/DoCreateInformeManual")
public class DoCreateInformeManualServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DoCreateInformeManualServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
			Connection conn = MyUtils.getStoredConnection(request);
			
		    String idActuacio = request.getParameter("idActuacio");
		    int idTecnic =  Integer.parseInt(request.getParameter("idTecnic"));
		    String descripcio = request.getParameter("descripcio");
		    String comentariCap = request.getParameter("comentariCap");
		    String tipusObra = request.getParameter("tipusContracte");
		    boolean llicencia = false;
		    String reqLlicencia = request.getParameter("reqLlicencia");
		    String tipusLlicencia = "";
		    boolean contracte = true;
		    String errorString = null;	 
		    boolean newExpedient = "on".equals(request.getParameter("newExpedient"));
		    double pbase = 0;
			double iva = 0;
			double plic = 0;
			String termini = "";
			String comentari = "";
		   	if (errorString == null) {
		   		try {	
		   			User tecnic = UsuariCore.findUsuariByID(conn, idTecnic);
		   			Actuacio actuacio = ActuacioCore.findActuacio(conn, idActuacio);
			 	    //Crear informe
			 	    InformeActuacio informe = new InformeActuacio();
				    PropostaInforme proposta = informe.new PropostaInforme();
				    List<PropostaInforme> llistaPropostes = new ArrayList<PropostaInforme>();
				    informe.setIdIncidencia(actuacio.getIdIncidencia());
				    informe.setActuacio(actuacio);
				    informe.setUsuari(tecnic);
				    proposta = informe.new PropostaInforme();
				    proposta.setObjecte(descripcio);
				    proposta.setTipusObra(tipusObra);
				    if (new String("obr").equals(tipusObra)) {
				    	llicencia = new String("si").equals(reqLlicencia);	 	   
						if (llicencia) tipusLlicencia = request.getParameter("tipusLlicencia") ;
				    }	
				    proposta.setLlicencia(llicencia);
				    proposta.setTipusLlicencia(tipusLlicencia);
				    proposta.setContracte(contracte);
				    pbase = Double.parseDouble(request.getParameter("pbase").replace(',','.'));
				    iva = Double.parseDouble(request.getParameter("iva"));
				    plic = Double.parseDouble(request.getParameter("plic").replace(',','.'));
				    proposta.setPbase(pbase);
				    proposta.setIva(iva);
				    proposta.setPlic(plic);
				    termini = request.getParameter("termini" );
				    comentari = request.getParameter("comentariTecnic");			   
				    proposta.setTermini(termini);
				    proposta.setComentari(comentari);
				    proposta.setSeleccionada(true);			    
				    llistaPropostes.add(proposta);
				    informe.setLlistaPropostes(llistaPropostes);
				    informe.setPropostaInformeSeleccionada(proposta);			    
				    String idinforme =  InformeCore.nouInforme(conn, informe, idTecnic);
				    informe = InformeCore.getInformePrevi(conn, idinforme, false);
				    InformeCore.seleccionarProposta(conn, informe.getLlistaPropostes().get(0).getIdProposta(), idinforme);
				     
				    if(newExpedient || tipusObra.equals("conveni")) {
					    //Cream expedient   				
		   				double importMajor = Double.parseDouble(getServletContext().getInitParameter("importObraMajor"));
		   				informe = InformeCore.getInformePrevi(conn, idinforme, false); //recuperam informe amb tota la informació actualitzada
		   				if (informe.getPropostaInformeSeleccionada().getTipusObra().equals("srv")) {
		   					importMajor = Double.parseDouble(getServletContext().getInitParameter("importServeiMajor"));
		   				}else if(informe.getPropostaInformeSeleccionada().getTipusObra().equals("submi")) {
		   					importMajor = Double.parseDouble(getServletContext().getInitParameter("importSubministramentMajor"));
		   				}
		   				String nouCodi = ExpedientCore.crearExpedient(conn, informe, importMajor, false, "");
		   				if (llicencia) LlicenciaCore.novaLlicencia(conn, nouCodi, tipusLlicencia);
				    }
		   		} catch (SQLException | NamingException e) {
		  			e.printStackTrace();
		  			errorString = e.getMessage();
		   		}
		   	}
		   	
		   	// Store infomation to request attribute, before forward to views.
		   	request.setAttribute("errorString", errorString);
		  	// If error, forward to Edit page.
		   	if (errorString != null) {
		   		RequestDispatcher dispatcher = request.getServletContext()
		   				.getRequestDispatcher("/WEB-INF/views/incidencia/createIncidenciaView.jsp");
		   		dispatcher.forward(request, response);
		   	}// If everything nice. Redirect to the product listing page.            
		   	else {
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
