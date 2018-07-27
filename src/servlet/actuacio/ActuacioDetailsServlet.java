package servlet.actuacio;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Actuacio;
import bean.InformeActuacio;
import bean.Registre;
import bean.Tasca;
import bean.User;
import bean.ControlPage.SectionPage;
import bean.Factura;
import bean.Incidencia;
import core.ActuacioCore;
import core.ControlPageCore;
import core.FacturaCore;
import core.IncidenciaCore;
import core.InformeCore;
import core.RegistreCore;
import core.TascaCore;
import core.UsuariCore;
import utils.Fitxers;
import utils.MyUtils;

@WebServlet(urlPatterns = { "/actuacionsDetalls"})
public class ActuacioDetailsServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;
 
   public ActuacioDetailsServlet() {
       super();
   }
 
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
	   Connection conn = MyUtils.getStoredConnection(request);
	   User usuari = MyUtils.getLoginedUser(request.getSession());
	   if (usuari == null){
		   response.sendRedirect(request.getContextPath() + "/");
	   }else if (!UsuariCore.hasPermision(conn, usuari, SectionPage.actuacio_detalls)) {
   		response.sendRedirect(request.getContextPath() + "/");	 	
	   }else{		   
		   String referencia = request.getParameter("ref");
		   String view = request.getParameter("view");		  
		   String informeSeleccionat = request.getParameter("exp");
	       String errorString = null;
	       Actuacio actuacio = new Actuacio();	
	       Incidencia incidencia = new Incidencia();
	       List<Tasca> tasques = new ArrayList<Tasca>();
	       List<Tasca> notificacions = new ArrayList<Tasca>();
	       List<Fitxers.Fitxer> arxius = new ArrayList<Fitxers.Fitxer>();
	       List<InformeActuacio> informes = new ArrayList<InformeActuacio>();	       
	       List<Factura> factures = new ArrayList<Factura>();	      
	       List<Registre> entrades = new ArrayList<Registre>();
	       List<Registre> sortides = new ArrayList<Registre>();
	       Context env;
	       String ruta = "";
			try {
				env = (Context)new InitialContext().lookup("java:comp/env");
				ruta = (String)env.lookup("ruta_base");
			} catch (NamingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	       String rutaActuacio = ruta +  "/documents/";
	       boolean canModificarActuacio = false;
	       boolean canCreateInformePrevi = false;
	       boolean canCreateTasca = false;
	       boolean canCreateFactura = false;
	       boolean canCreateRegistre = false;
	       boolean canModificarExpedient = false;
	       boolean canModificarUrbanisme = false;
	       boolean canModificarGarantia = false;
	       boolean canModificarInstalacions = false;
	       boolean canModificarPersonal = false;
	       boolean canCreateFeina = false;
	       try {
	    	   actuacio = ActuacioCore.findActuacio(conn, referencia);
	    	   if (actuacio.getCentre() != null && actuacio.getCentre().getIdCentre() != null && actuacio.getCentre().getIdCentre().equals("9999PERSO") && !UsuariCore.hasPermision(conn, usuari, SectionPage.personal)) response.sendRedirect(request.getContextPath() + "/");
	    	   actuacio.setSeguiment(ActuacioCore.isSeguimentActuacio(conn, referencia, usuari.getIdUsuari()));
	    	   incidencia = IncidenciaCore.findIncidencia(conn, actuacio.getIdIncidencia());
	    	   actuacio.setArxiusAdjunts(Fitxers.ObtenirTotsFitxers(incidencia.getIdIncidencia()));
	    	   tasques = TascaCore.findTasquesActuacio(conn, referencia, false); 
	    	   informes = InformeCore.getInformesActuacio(conn, referencia);
	    	   factures = FacturaCore.getFacturesActuacio(conn, referencia);  
	    	   entrades = RegistreCore.searchEntradesIncidencia(conn, incidencia.getIdIncidencia());
	    	   sortides = RegistreCore.searchSortidesIncidencia(conn, incidencia.getIdIncidencia());
	    	   rutaActuacio += incidencia.getIdIncidencia() + "/Actuacio";
	    	   canModificarActuacio = UsuariCore.hasPermision(conn, usuari, SectionPage.actuacio_modificar);	
	    	   canCreateInformePrevi = UsuariCore.hasPermision(conn, usuari, SectionPage.tasques_crear) || "gerencia".equals(usuari.getDepartament());
	    	   canCreateTasca = UsuariCore.hasPermision(conn, usuari, SectionPage.tasques_crear) || "gerencia".equals(usuari.getDepartament());
	    	   canCreateFactura = UsuariCore.hasPermision(conn, usuari, SectionPage.factures_crear);
	    	   canCreateRegistre = UsuariCore.hasPermision(conn, usuari, SectionPage.registre_ent_crear);	
	    	   canModificarExpedient = UsuariCore.hasPermision(conn, usuari, SectionPage.expedient_modificar);
	    	   canCreateFeina = UsuariCore.hasPermision(conn, usuari, SectionPage.registre_ent_crear);	
	    	   canModificarUrbanisme = UsuariCore.hasPermision(conn, usuari, SectionPage.llicencia_modificar);
	    	   canModificarGarantia = UsuariCore.hasPermision(conn, usuari, SectionPage.expedient_modificar);
	    	   canModificarInstalacions = usuari.getDepartament().equals("instalacions") || usuari.getRol().contains("ADMIN");
	    	   canModificarPersonal = usuari.getRol().contains("CAP") || usuari.getRol().contains("ADMIN");
	       } catch (SQLException | NamingException e) {
	           e.printStackTrace();
	           errorString = e.getMessage();
	       }
	       // Store info in request attribute, before forward to views
	       request.setAttribute("errorString", errorString);
	       request.setAttribute("view", view);
	       request.setAttribute("informeSeleccionat", informeSeleccionat);
	       request.setAttribute("actuacio", actuacio);
	       request.setAttribute("incidencia", incidencia);
	       request.setAttribute("tasques", tasques);
	       request.setAttribute("notificacions", notificacions);
	       request.setAttribute("arxius", arxius);	   
	       request.setAttribute("informes", informes);	      
	       request.setAttribute("entrades", entrades);
	       request.setAttribute("sortides", sortides);
	       request.setAttribute("factures", factures);
	       request.setAttribute("rutaActuacio", rutaActuacio);
	       request.setAttribute("canCreateInformePrevi", canCreateInformePrevi);
	       request.setAttribute("canCreateTasca", canCreateTasca);
	       request.setAttribute("canModificarActuacio", canModificarActuacio);
	       request.setAttribute("canCreateFactura", canCreateFactura);
	       request.setAttribute("canCreateRegistre", canCreateRegistre);
	       request.setAttribute("canCreateFeina", canCreateFeina);	       
	       request.setAttribute("canModificarExpedient", canModificarExpedient);
	       request.setAttribute("canModificarUrbanisme", canModificarUrbanisme);
	       request.setAttribute("canModificarGarantia", canModificarGarantia);
	       request.setAttribute("canModificarInstalacions", canModificarInstalacions);
	       request.setAttribute("canModificarPersonal", canModificarPersonal);
	       request.setAttribute("redireccio", "actuacions");
	       request.setAttribute("menu", ControlPageCore.renderMenu(conn, usuari, "Actuacions"));
	       request.setAttribute("idUsuariLogg", usuari.getIdUsuari());
	       // Forward to /WEB-INF/views/homeView.jsp
	       // (Users can not access directly into JSP pages placed in WEB-INF)
	       RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/views/actuacio/actuacioView.jsp");
	        
	       dispatcher.forward(request, response);
	   }
   }
 
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
       doGet(request, response);
   }
 
}