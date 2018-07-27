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
import bean.Incidencia;
import bean.InformeActuacio;
import bean.Oferta;
import bean.InformeActuacio.PropostaInforme;
import core.ActuacioCore;
import core.CentreCore;
import core.CreditCore;
import core.ExpedientCore;
import core.IncidenciaCore;
import core.InformeCore;
import core.LlicenciaCore;
import core.OfertaCore;
import core.UsuariCore;
import utils.MyUtils;

/**
 * Servlet implementation class DoCreateCompletActuacioServlet
 */
@WebServlet("/DoCreateCompletActuacio")
public class DoCreateCompletActuacioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DoCreateCompletActuacioServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Connection conn = MyUtils.getStoredConnection(request);
		
	    String idActuacio = request.getParameter("referencia");
	    String idCentre = request.getParameter("idCentre");	   
	    String descripcio = request.getParameter("descripcio");	    
	    int idUsuari = MyUtils.getLoginedUser(request.getSession()).getIdUsuari();
	    int idTecnic = Integer.parseInt(request.getParameter("llistaUsuaris"));	
	    String tipusObra = request.getParameter("tipusContracte");
	    boolean llicencia = false;
	    String reqLlicencia = request.getParameter("reqLlicencia");
	    String tipusLlicencia = "";
	    boolean contracte = false;
	    String errorString = null;	 
	    
	    double pbase = 0;
		double iva = 0;
		double plic = 0;
		String termini = "";
		String comentari = "";
	   	if (errorString == null) {
	   		try {
	   			//Crear incidència
	   			Incidencia incidencia = new Incidencia();
	   		    incidencia.setIdIncidencia(IncidenciaCore.getNewCode(conn));
	   		    incidencia.setIdCentre(idCentre);
	   		    incidencia.setUsuCre(UsuariCore.findUsuariByID(conn, idUsuari));
	   		    incidencia.setDescripcio(descripcio);
	   		    IncidenciaCore.novaIncidencia(conn, incidencia);
	   		    
	   		    //Crear actuació
	   		    Actuacio actuacio = new Actuacio();
		 	    actuacio.setReferencia(idActuacio);
		 	    actuacio.setDescripcio(descripcio);
		 	    actuacio.setCentre(CentreCore.findCentre(conn, idCentre, false));
		 	    actuacio.setIdIncidencia(incidencia.getIdIncidencia());
		 	    actuacio.setIdUsuariCreacio(idUsuari);
		 	    ActuacioCore.novaActuacio(conn, actuacio);
		 	    
		 	    //Crear informe
		 	    InformeActuacio informe = new InformeActuacio();
			    PropostaInforme proposta = informe.new PropostaInforme();
			    List<PropostaInforme> llistaPropostes = new ArrayList<PropostaInforme>();
			    informe.setIdIncidencia(incidencia.getIdIncidencia());
			    informe.setActuacio(actuacio);
			    proposta = informe.new PropostaInforme();
			    proposta.setObjecte(descripcio);
			    proposta.setTipusObra(tipusObra);
			    if (new String("obr").equals(tipusObra)) {
			    	llicencia = new String("si").equals(reqLlicencia);	 	   
					if (llicencia) tipusLlicencia = request.getParameter("tipusLlicencia") ;		
					contracte = true;
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
			   
			    //Reservam crèdit
		   		if (request.getParameter("llistaPartides") != "-1") CreditCore.reservar(conn, request.getParameter("llistaPartides"), idActuacio, idinforme, plic, comentari, idUsuari);
		   		
		   		//Cream expedient   				
   				double importObraMajor = Double.parseDouble(getServletContext().getInitParameter("importObraMajor"));
   				informe = InformeCore.getInformePrevi(conn, idinforme, false); //recuperam informe amb tota la informació actualitzada
   				String nouCodi = ExpedientCore.crearExpedient(conn, informe, importObraMajor, false, "");	   				
   				if (llicencia) LlicenciaCore.novaLlicencia(conn, nouCodi, tipusLlicencia);
		   		
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
