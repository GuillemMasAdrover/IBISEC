package core;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import bean.Actuacio;
import bean.InformeActuacio;
import bean.Oferta;
import utils.Fitxers;
import utils.Fitxers.Fitxer;

public class OfertaCore {
	private static Oferta initOferta(Connection conn, ResultSet rs, boolean complet) throws SQLException, NamingException{
		Oferta oferta = new Oferta();
		oferta.setIdOferta(rs.getString("idoferta"));
		oferta.setIdActuacio(rs.getString("idactuacio"));		
   		oferta.setCifEmpresa(rs.getString("cifempresa"));
   		if (EmpresaCore.findEmpresa(conn, oferta.getCifEmpresa()) != null) oferta.setNomEmpresa(EmpresaCore.findEmpresa(conn, oferta.getCifEmpresa()).getName());
   		oferta.setPbase(rs.getDouble("pbase"));   		
   		oferta.setIva(rs.getDouble("iva"));
   		oferta.setPlic(rs.getDouble("plic"));
   		oferta.setTermini(rs.getString("termini"));
   		oferta.setSeleccionada(rs.getBoolean("seleccionada"));
   		oferta.setDescalificada(rs.getBoolean("descalificada")); 
   		oferta.setUsuariCreacio(UsuariCore.findUsuariByID(conn, rs.getInt("usucre")));
   		oferta.setDataCreacio(rs.getTimestamp("datacre"));
   		oferta.setComentari(rs.getString("comentari"));
   		oferta.setUsuariAprovacio(UsuariCore.findUsuariByID(conn, rs.getInt("usuaprovacio")));
   		oferta.setDataAprovacio(rs.getTimestamp("dataaprovacio"));
   		oferta.setUsuariCapValidacio(UsuariCore.findUsuariByID(conn, rs.getInt("usucapvalidacio")));
   		oferta.setDataCapValidacio(rs.getTimestamp("datacapvalidacio"));
   		oferta.setIdInforme(rs.getString("idinforme"));
   		Actuacio actuacio = ActuacioCore.findActuacio(conn, rs.getString("idactuacio"));
   		if (complet) {
   			oferta.setActuacio(actuacio);
   		}
   		oferta.setCapDobra(rs.getString("capdobres"));
   		oferta.setPresupost(getPresupost(actuacio.getIdIncidencia(), rs.getString("idactuacio"), rs.getString("idinforme"),rs.getString("cifempresa")));
   		oferta.setPersonalInscrit(getPersonalInscrit(actuacio.getIdIncidencia(), rs.getString("idactuacio"), rs.getString("idinforme"),rs.getString("cifempresa")));
   		return oferta;
	}
	
	public static String novaOferta(Connection conn, Oferta oferta, int idUsuari) throws SQLException {
		String sql = "INSERT INTO public.tbl_empresaoferta(idoferta, idactuacio, cifempresa, pbase, iva, plic, termini, seleccionada, descalificada, comentari, usucre, datacre, idinforme)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, localtimestamp, ?);";
	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		String newCode =  getNewCode(conn);
		pstm.setString(1, newCode);		
		pstm.setString(2, oferta.getIdActuacio());
		pstm.setString(3, oferta.getCifEmpresa());
		pstm.setDouble(4, oferta.getPbase());
		pstm.setDouble(5, oferta.getIva());
		pstm.setDouble(6, oferta.getPlic());
		pstm.setString(7, oferta.getTermini());
		pstm.setBoolean(8, oferta.isSeleccionada());
		pstm.setBoolean(9, oferta.isDescalificada());
		pstm.setString(10, oferta.getComentari());
		pstm.setInt(11, idUsuari);
		pstm.setString(12, oferta.getIdInforme());
		pstm.executeUpdate();
		return newCode;
	}
	
	public static Oferta findOfertaById(Connection conn, String idOferta) throws SQLException, NamingException {
		Oferta ofertaSeleccionada = new Oferta();
		String sql = "SELECT idoferta, idactuacio, cifempresa, pbase, iva, plic, termini, seleccionada, descalificada, comentari, usucre, datacre, usuaprovacio, dataaprovacio, idinforme, usucapvalidacio, datacapvalidacio, capdobres"
				+ " FROM public.tbl_empresaoferta"
				+ " WHERE idOferta = ? ;";	
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, idOferta);		
		ResultSet rs = pstm.executeQuery();	
		if (rs.next()) ofertaSeleccionada = initOferta(conn, rs, true);
		return ofertaSeleccionada;
	}
	
	public static void deleteOferta(Connection conn, String idOferta, int idUsuari) throws SQLException, NamingException {
		Oferta ofertaSeleccionada = findOfertaById(conn, idOferta);
		String sql = "DELETE FROM public.tbl_empresaoferta"
					+ " WHERE idoferta = ?";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, idOferta);
		pstm.executeUpdate();	
		if (ofertaSeleccionada.getPresupost().getRuta() != null && !ofertaSeleccionada.getPresupost().getRuta().isEmpty()) Fitxers.eliminarFitxer(conn, idUsuari, ofertaSeleccionada.getPresupost().getRuta());
	}
	
	public static void seleccionarOferta(Connection conn, String idInforme, String idOferta, String termini, String comentari) throws SQLException {
		String sql = "UPDATE public.tbl_empresaoferta"
				+ " SET termini='', seleccionada=false, comentari=''"
				+ " WHERE idinforme = ?";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, idInforme);
		pstm.executeUpdate();
		sql = "UPDATE public.tbl_empresaoferta"
					+ " SET termini=?, seleccionada=?, comentari=?"
					+ " WHERE idoferta = ?";
		pstm = conn.prepareStatement(sql);
		pstm.setString(1, termini);
		pstm.setBoolean(2, true);
		pstm.setString(3, comentari);
		pstm.setString(4, idOferta);
		pstm.executeUpdate();	
	}
	
	public static void actualitzarCapDobra(Connection conn, String idOferta, String capDobra) throws SQLException {
		String sql = "UPDATE public.tbl_empresaoferta"
				+ " SET capdobres=?"
				+ " WHERE idoferta = ?";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, capDobra);
		pstm.setString(2, idOferta);
		pstm.executeUpdate();
	}
	
	public static List<Oferta> findOfertes(Connection conn, String idInforme) throws SQLException, NamingException {
		List<Oferta> ofertes = new ArrayList<Oferta>();
		String sql = "SELECT idoferta, idactuacio, cifempresa, pbase, iva, plic, termini, seleccionada, descalificada, comentari, usucre, datacre, usuaprovacio, dataaprovacio, idinforme, usucapvalidacio, datacapvalidacio, capdobres"
					+ " FROM public.tbl_empresaoferta"
					+ " WHERE idinforme = ? "
					+ " ORDER BY plic ";
		
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, idInforme);		
		ResultSet rs = pstm.executeQuery();		
		while (rs.next()) {
			Oferta oferta = initOferta(conn, rs, false);
			ofertes.add(oferta);
		}
		return ofertes;
	}
	
	public static Oferta findOfertaSeleccionada(Connection conn, String idInforme) throws SQLException, NamingException {		
		String sql = "SELECT idoferta, idactuacio, cifempresa, pbase, iva, plic, termini, seleccionada, descalificada, comentari, usucre, datacre, usuaprovacio, dataaprovacio, idinforme, usucapvalidacio, datacapvalidacio, capdobres"
					+ " FROM public.tbl_empresaoferta"
					+ " WHERE idinforme = ? AND seleccionada = true ;";
		
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, idInforme);		
		ResultSet rs = pstm.executeQuery();		
		while (rs.next()) {
			Oferta oferta = initOferta(conn, rs, false);
			return oferta;
		}
		return null;
	}
	
	public static void validacioCapOferta(Connection conn, String idInforme, int idUsuari) throws SQLException {
		String sql = "UPDATE public.tbl_empresaoferta"
					+ " SET usucapvalidacio=?, datacapvalidacio=localtimestamp"
					+ " WHERE idinforme = ? AND seleccionada = true;";
		PreparedStatement pstm = conn.prepareStatement(sql);	
		pstm.setInt(1, idUsuari);
		pstm.setString(2, idInforme);		
		pstm.executeUpdate();
	}
	
	public static void aprovarOferta(Connection conn, String idInforme, int idUsuari) throws SQLException {
		String sql = "UPDATE public.tbl_empresaoferta"
					+ " SET usuaprovacio=?, dataaprovacio=localtimestamp"
					+ " WHERE idinforme = ? AND seleccionada = true;";
		PreparedStatement pstm = conn.prepareStatement(sql);	
		pstm.setInt(1, idUsuari);
		pstm.setString(2, idInforme);		
		pstm.executeUpdate();
	}
	
	public static String getNewCode(Connection conn) throws SQLException {
		String newCode = "1";
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String yearInString = String.valueOf(year);
		
		String sql = "SELECT idoferta, datacre"
					+ " FROM public.tbl_empresaoferta"
					+ " WHERE idoferta like '%" + yearInString +  "-OFE-%'"
					+ " ORDER BY idoferta DESC, datacre DESC LIMIT 1;";	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();
		
		String prefix = "OFE";
		if (rs.next()) { //Codis nous
			String actualCode = rs.getString("idoferta");			
			int num = Integer.valueOf(actualCode.split("-")[2]);
			String numFormatted = String.format("%04d", num + 1);
			newCode = yearInString + "-" + prefix + "-" + numFormatted;
		}
		else {
			int num = 0;		
			String numFormatted = String.format("%04d", num + 1);
			newCode = yearInString + "-" + prefix + "-" + numFormatted;
		}
		return newCode;		
	}
	
	public static Fitxer getPresupost(String idIncidencia, String idActuacio, String idInforme, String cifEmpresa) throws SQLException, NamingException {
		Fitxer oferta = new Fitxer();
		 // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		oferta = utils.Fitxers.ObtenirFitxer(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Oferta/", true);
		return oferta;
	}
	
	public static Fitxer getPersonalInscrit(String idIncidencia, String idActuacio, String idInforme, String cifEmpresa) throws SQLException, NamingException {
		Fitxer oferta = new Fitxer();
		 // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		oferta = utils.Fitxers.ObtenirFitxer(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Personal Inscrit/", true);
		return oferta;
	}
	
	public static void guardarFitxer(Connection conn, List<Fitxer> fitxers, String idIncidencia, String idActuacio, String idInforme, String cifEmpresa, String tipusFitxer, int idUsuari) throws NamingException{		
		if (!fitxers.isEmpty()) {
			String fileName = "";
			// Crear directoris si no existeixen
			Context env = (Context)new InitialContext().lookup("java:comp/env");
		    String ruta = (String)env.lookup("ruta_base");
				
			File tmpFile = new File(ruta + "/documents/"  + idIncidencia);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" +idActuacio + "/informe/" + idInforme);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}	
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" +idActuacio + "/informe/" + idInforme + "/Empreses");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}	
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" +idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}	
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" +idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/" + tipusFitxer);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}	
			fileName = ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/" + tipusFitxer + "/";
			
	        for(int i=0;i<fitxers.size();i++){
	            Fitxer fitxer = (Fitxer) fitxers.get(i);
	            if (fitxer.getFitxer().getName() != "") {
	            	File archivo_server = new File(fileName + fitxer.getFitxer().getName());
	               	try {
	               		fitxer.getFitxer().write(archivo_server);
	               		Fitxers.guardarRegistreFitxer(conn, fitxer.getFitxer().getName(), fileName  + "/" + fitxer.getFitxer().getName(), idUsuari);
	           		} catch (Exception e) {
	           			e.printStackTrace();
	           		}
	            } 
	        }
		}
	}
}
