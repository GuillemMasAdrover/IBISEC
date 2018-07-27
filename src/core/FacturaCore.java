package core;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import bean.Actuacio;
import bean.Factura;
import bean.InformeActuacio;
import bean.User;
import utils.Fitxers;
import utils.Fitxers.Fitxer;

public class FacturaCore {
	static final String SQL_CAMPS = "idfactura, idinforme, idactuacio, nifproveidor, datafactura, concepte, import, tipusfactura,"
									+ " nombrefactura, dataentrada, usuconformador, dataconformador, notes, usucre, datacre,"
									+ " dataenviatconformador, dataenviatcomptabilitat, datadescarregadafactura, anulada, motiuanulada";
	
	static final String SQL_CAMPS_CERT = "idcertificacio, idinforme, idactuacio, nifproveidor, datacertificacio, concepte, import,"
									+ " nombrecertificacio, dataentrada, usuconformador, dataconformador, notes, usucre, datacre,"
									+ " dataenviatconformador, dataenviatcomptabilitat, anulada, motiuanulada";
	
	private static Factura initFactura(Connection conn, ResultSet rs, boolean ambDocuments) throws SQLException, NamingException{	
		Factura factura = new Factura();
		factura.setIdFactura(rs.getString("idfactura"));
		factura.setIdInforme(rs.getString("idinforme"));	
		factura.setIdActuacio(rs.getString("idactuacio"));		
		factura.setIdProveidor(rs.getString("nifproveidor"));
		if (EmpresaCore.findEmpresa(conn, rs.getString("nifproveidor")) != null ) factura.setNomProveidor(EmpresaCore.findEmpresa(conn, rs.getString("nifproveidor")).getName());
		factura.setDataFactura(rs.getTimestamp("datafactura"));
		factura.setConcepte(rs.getString("concepte"));
		factura.setValor(rs.getDouble("import"));
		factura.setTipusFactura(rs.getString("tipusfactura"));
		factura.setNombreFactura(rs.getString("nombrefactura"));
		factura.setDataEntrada(rs.getTimestamp("dataentrada"));
		factura.setUsuariConformador(UsuariCore.findUsuariByID(conn, rs.getInt("usuconformador")));
		factura.setDataConformacio(rs.getTimestamp("dataconformador"));
		factura.setNotes(rs.getString("notes"));
		factura.setDataEnviatConformador(rs.getTimestamp("dataenviatconformador"));
		factura.setDataEnviatComptabilitat(rs.getTimestamp("dataenviatcomptabilitat"));
		factura.setUsuariCreador(UsuariCore.findUsuariByID(conn, rs.getInt("usucre")));
		factura.setFactura(getArxiu(ActuacioCore.findActuacio(conn, rs.getString("idactuacio")).getIdIncidencia(), rs.getString("idactuacio"), rs.getString("idinforme"), rs.getString("nifproveidor"), rs.getString("idfactura"), false));
		factura.setAltres(getArxiusAltres(ActuacioCore.findActuacio(conn, rs.getString("idactuacio")).getIdIncidencia(), rs.getString("idactuacio"), rs.getString("idinforme"), rs.getString("nifproveidor"), rs.getString("idfactura")));
		factura.setDataDescarregadaConformada(rs.getTimestamp("datadescarregadafactura"));
		factura.setAnulada(rs.getBoolean("anulada"));
		factura.setMotiuAnulada(rs.getString("motiuanulada"));
		return factura;
	}	
	
	private static Factura initCertificacio(Connection conn, ResultSet rs, boolean readFirma) throws SQLException, NamingException{	
		Factura factura = new Factura();
		factura.setIdFactura(rs.getString("idcertificacio"));
		factura.setIdInforme(rs.getString("idinforme"));	
		factura.setIdActuacio(rs.getString("idactuacio"));		
		factura.setIdProveidor(rs.getString("nifproveidor"));
		if (EmpresaCore.findEmpresa(conn, rs.getString("nifproveidor")) != null ) factura.setNomProveidor(EmpresaCore.findEmpresa(conn, rs.getString("nifproveidor")).getName());
		factura.setDataFactura(rs.getTimestamp("datacertificacio"));
		factura.setConcepte(rs.getString("concepte"));
		factura.setValor(rs.getDouble("import"));
		factura.setNombreFactura(rs.getString("nombrecertificacio"));
		factura.setDataEntrada(rs.getTimestamp("dataentrada"));
		factura.setUsuariConformador(UsuariCore.findUsuariByID(conn, rs.getInt("usuconformador")));
		factura.setDataConformacio(rs.getTimestamp("dataconformador"));
		factura.setNotes(rs.getString("notes"));
		factura.setDataEnviatConformador(rs.getTimestamp("dataenviatconformador"));
		factura.setDataEnviatComptabilitat(rs.getTimestamp("dataenviatcomptabilitat"));
		factura.setUsuariCreador(UsuariCore.findUsuariByID(conn, rs.getInt("usucre")));
		factura.setCertificacions(getArxiuCertificacio(ActuacioCore.findActuacio(conn, rs.getString("idactuacio")).getIdIncidencia(), rs.getString("idactuacio"), rs.getString("idinforme"), rs.getString("nifproveidor"), rs.getString("idcertificacio"), readFirma));
		factura.setAltres(getArxiusAltres(ActuacioCore.findActuacio(conn, rs.getString("idactuacio")).getIdIncidencia(), rs.getString("idactuacio"), rs.getString("idinforme"), rs.getString("nifproveidor"), rs.getString("idcertificacio")));
		factura.setAnulada(rs.getBoolean("anulada"));
		factura.setMotiuAnulada(rs.getString("motiuanulada"));
		return factura;
	}	
	
	public static void saveArxiuCertificacio(String idIncidencia, String idActuacio, String idInforme, String cifEmpresa, String idCertificacio, List<Fitxer> fitxers, Connection conn, int idUsuari) throws NamingException, SQLException {
		if (!fitxers.isEmpty()) {
			Factura certificacio = FacturaCore.getCertificacio(conn, idCertificacio);
			String fileName = "";
			String nomProveidor = "";
			// Crear directoris si no existeixen
			 // Get the base naming context
		    Context env = (Context)new InitialContext().lookup("java:comp/env");
		    // Get a single value
			String ruta =  (String)env.lookup("ruta_base");
			File tmpFile =  new File(ruta + "/documents/" + idIncidencia);
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
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Certificacions");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Certificacions/" + idCertificacio);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			fileName = ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Certificacions/" + idCertificacio + "/";
	        String noms = "";
	        int num = 1;
			for(int i=0;i<fitxers.size();i++){		        	
	            Fitxer fitxer = (Fitxer) fitxers.get(i);	            
	            if (fitxer.getFitxer().getName() != "") {	
	            	if (certificacio.getNomProveidor() != null) {
	            		nomProveidor = certificacio.getNomProveidor().replace("/"," ");
	            	} else {
	            		nomProveidor = certificacio.getIdProveidor();
	            	}
	            	File archivo_server = new File(fileName  + "/" + nomProveidor + "-" + certificacio.getNombreFactura().replace("/"," ") + "-" + certificacio.getDataEntradaString().replace("/","") + "_" + num + ".pdf");
	               	noms += fitxer.getFitxer().getName() + "<br>";
	               	num++;
	            	try {
	               		fitxer.getFitxer().write(archivo_server);	               		
	           		} catch (Exception e) {
	           			e.printStackTrace();
	           		}
	            } 
	        }
	        Fitxers.guardarRegistreFitxer(conn, noms, fileName  + "/" + nomProveidor + "-" + certificacio.getNombreFactura().replace("/"," ") + "-" + certificacio.getDataEntradaString().replace("/","") + ".pdf", idUsuari);
		}
	}
	
	public static void saveArxiu(String idIncidencia, String idActuacio, String idInforme, String cifEmpresa, String idFactura, List<Fitxer> fitxers, Connection conn, int idUsuari) throws NamingException, SQLException {
		if (fitxers != null && fitxers.size() > 0) {
			Factura factura = FacturaCore.getFactura(conn, idFactura);
			String fileName = "";
			String nomProveidor = "";
			// Crear directoris si no existeixen
			 // Get the base naming context
		    Context env = (Context)new InitialContext().lookup("java:comp/env");
		    // Get a single value
			String ruta =  (String)env.lookup("ruta_base");
			File tmpFile =  new File(ruta + "/documents/" + idIncidencia);
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
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Factures");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Factures/" + idFactura);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			fileName = ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Factures/" + idFactura + "/";
	        for(int i=0;i<fitxers.size();i++){		        	
	            Fitxer fitxer = (Fitxer) fitxers.get(i);	            
	            if (fitxer.getFitxer() != null && fitxer.getFitxer().getName() != "") {	
	            	if (factura.getNomProveidor() != null) {
	            		nomProveidor = factura.getNomProveidor().replace("/"," ");
	            	} else {
	            		nomProveidor = factura.getIdProveidor();
	            	}
	            	File archivo_server = new File(fileName  + "/" + nomProveidor + "-" + factura.getNombreFactura().replace("/"," ") + "-" + factura.getDataEntradaString().replace("/","") + ".pdf");
	               	try {
	               		fitxer.getFitxer().write(archivo_server);
	               		Fitxers.guardarRegistreFitxer(conn, fitxer.getFitxer().getName(), fileName  + "/" + nomProveidor + "-" + factura.getNombreFactura().replace("/"," ") + "-" + factura.getDataEntradaString().replace("/","") + ".pdf", idUsuari);
	           		} catch (Exception e) {
	           			e.printStackTrace();
	           		}
	            } 
	        }
		}
	}
	
	public static void saveArxiuAltres(String idIncidencia, String idActuacio, String idInforme, String cifEmpresa, String idFactura, List<Fitxer> fitxers, Connection conn, int idUsuari) throws NamingException, SQLException {
		if (fitxers != null && fitxers.size() > 0) {
			String fileName = "";
			// Crear directoris si no existeixen
			 // Get the base naming context
		    Context env = (Context)new InitialContext().lookup("java:comp/env");
		    // Get a single value
			String ruta =  (String)env.lookup("ruta_base");
			File tmpFile =  new File(ruta + "/documents/" + idIncidencia);
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
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Factures");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Factures/" + idFactura);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Factures/" + idFactura + "/Altres");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			fileName = ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Factures/" + idFactura + "/Altres/";
	        for(int i=0;i<fitxers.size();i++){		        	
	            Fitxer fitxer = (Fitxer) fitxers.get(i);	            
	            if (fitxer.getFitxer().getName() != "") {		            	
	            	File archivo_server = new File(fileName  + "/" + fitxer.getFitxer().getName());
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
	
	public static List<Fitxer> getArxiuCertificacio(String idIncidencia, String idActuacio, String idInforme, String cifEmpresa, String idCertificacio, boolean readFirma) throws NamingException {
		List<Fitxer> factura = new ArrayList<Fitxer>();
		// Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		if (idIncidencia == null) idIncidencia = "-1";
		factura =  utils.Fitxers.ObtenirTotsFitxers(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Certificacions/" + idCertificacio + "/", "certificació");
		return factura;
	}
	
	public static Fitxer getArxiu(String idIncidencia, String idActuacio, String idInforme, String cifEmpresa, String idFactura, boolean readFirma) throws NamingException {
		Fitxer factura = new Fitxer();
		// Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		if (idIncidencia == null) idIncidencia = "-1";
		factura =  utils.Fitxers.ObtenirFitxer(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Factures/" + idFactura + "/", true);
		return factura;
	}
	
	public static List<Fitxer> getArxiusAltres(String idIncidencia, String idActuacio, String idInforme, String cifEmpresa, String idFactura) throws NamingException {
		List<Fitxer> altres = new ArrayList<Fitxer>();
		// Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		if (idIncidencia == null) idIncidencia = "-1";
		altres =  utils.Fitxers.ObtenirTotsFitxers(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio + "/informe/" + idInforme + "/Empreses/" + cifEmpresa + "/Factures/" + idFactura + "/Altres/", "altres");
		return altres;
	}
	
	public static void newFactura(Connection conn, Factura factura, int idUsuari) throws SQLException{
		String sql = "INSERT INTO public.tbl_factures(" + SQL_CAMPS + ")"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,localtimestamp, null, ?, ?, false, null)";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, factura.getIdFactura());
		pstm.setString(2, factura.getIdInforme());
		pstm.setString(3, factura.getIdActuacio());
		pstm.setString(4, factura.getIdProveidor());
		if (factura.getDataFactura() != null) {
			pstm.setDate(5, new java.sql.Date(factura.getDataFactura().getTime()));
		} else {
			 pstm.setDate(5, null);
		}
		pstm.setString(6, factura.getConcepte());
		pstm.setDouble(7, factura.getValor());
		pstm.setString(8, factura.getTipusFactura());
		pstm.setString(9, factura.getNombreFactura());
		if (factura.getDataEntrada() != null) {
			pstm.setDate(10, new java.sql.Date(factura.getDataEntrada().getTime()));
		} else {
			 pstm.setDate(10, null);
		}
		if (factura.getUsuariConformador() != null) {
			pstm.setInt(11, factura.getUsuariConformador().getIdUsuari());
		} else {
			 pstm.setInt(11, 0);
		}
		pstm.setDate(12, null);				
		pstm.setString(13, factura.getNotes());
		pstm.setInt(14, idUsuari);
		pstm.setDate(15, null);
		pstm.setDate(16, null);
		pstm.executeUpdate();
	}
	
	public static void newCertificacio(Connection conn, Factura certificacio, int idUsuari) throws SQLException{
		String sql = "INSERT INTO public.tbl_certificacions(" + SQL_CAMPS_CERT + ")"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,localtimestamp, localtimestamp, ?, false, null)";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, certificacio.getIdFactura());
		pstm.setString(2, certificacio.getIdInforme());
		pstm.setString(3, certificacio.getIdActuacio());
		pstm.setString(4, certificacio.getIdProveidor());
		if (certificacio.getDataFactura() != null) {
			pstm.setDate(5, new java.sql.Date(certificacio.getDataFactura().getTime()));
		} else {
			 pstm.setDate(5, null);
		}
		pstm.setString(6, certificacio.getConcepte());
		pstm.setDouble(7, certificacio.getValor());
		pstm.setString(8, certificacio.getNombreFactura());
		if (certificacio.getDataEntrada() != null) {
			pstm.setDate(9, new java.sql.Date(certificacio.getDataEntrada().getTime()));
		} else {
			 pstm.setDate(9, null);
		}
		pstm.setInt(10, certificacio.getUsuariConformador().getIdUsuari());
		pstm.setDate(11, null);				
		pstm.setString(12, certificacio.getNotes());
		pstm.setInt(13, idUsuari);
		pstm.setDate(14, null);
		pstm.executeUpdate();
	}
	
	public static void modificarFactura(Connection conn, Factura factura, int idUsuari) throws SQLException{
		String sql = "UPDATE public.tbl_factures"
					+ " SET idactuacio = ?, idinforme = ?, nifproveidor=?, datafactura=?,"
					+ " concepte=?, import=?, tipusfactura=?, nombrefactura=?, dataentrada=?,"
					+ " usuconformador=?, notes=?, dataconformador=?, dataenviatconformador=?, dataenviatcomptabilitat=?, datadescarregadafactura=?"
					+ " WHERE idfactura=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, factura.getIdActuacio());
		pstm.setString(2, factura.getIdInforme());
		pstm.setString(3, factura.getIdProveidor());
		if (factura.getDataFactura() != null) {
			pstm.setDate(4, new java.sql.Date(factura.getDataFactura().getTime()));
		} else {
			 pstm.setDate(4, null);
		}
		pstm.setString(5, factura.getConcepte());
		pstm.setDouble(6, factura.getValor());
		pstm.setString(7, factura.getTipusFactura());
		pstm.setString(8, factura.getNombreFactura());
		if (factura.getDataEntrada() != null) {
			pstm.setDate(9, new java.sql.Date(factura.getDataEntrada().getTime()));
		} else {
			 pstm.setDate(9, null);
		}
		if (factura.getUsuariConformador() != null){
			pstm.setInt(10, factura.getUsuariConformador().getIdUsuari());
		} else {
			pstm.setObject(10, null);
		}		
		pstm.setString(11, factura.getNotes());
		if (factura.getDataConformacio() != null) {
			pstm.setDate(12, new java.sql.Date(factura.getDataConformacio().getTime()));
		} else {
			 pstm.setDate(12, null);
		}
		if (factura.getDataEnviatConformador() != null) {
			pstm.setDate(13, new java.sql.Date(factura.getDataEnviatConformador().getTime()));
		} else {
			 pstm.setDate(13, null);
		}
		if (factura.getDataEnviatComptabilitat() != null) {
			pstm.setDate(14, new java.sql.Date(factura.getDataEnviatComptabilitat().getTime()));
		} else {
			 pstm.setDate(14, null);
		}
		if (factura.getDataDescarregadaConformada() != null) {
			pstm.setDate(15, new java.sql.Date(factura.getDataDescarregadaConformada().getTime()));
		} else {
			 pstm.setDate(15, null);
		}
		pstm.setString(16, factura.getIdFactura());
		pstm.executeUpdate();
	}
	
	public static void modificarCertificacio(Connection conn, Factura certificacio, int idUsuari) throws SQLException{
		String sql = "UPDATE public.tbl_certificacions"
					+ " SET nifproveidor=?, datacertificacio=?,"
					+ " concepte=?, import=?, nombrecertificacio=?, dataentrada=?,"
					+ " usuconformador=?, notes=?, dataconformador=?, dataenviatconformador=?, dataenviatcomptabilitat=?"
					+ " WHERE idcertificacio=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, certificacio.getIdProveidor());
		if (certificacio.getDataFactura() != null) {
			pstm.setDate(2, new java.sql.Date(certificacio.getDataFactura().getTime()));
		} else {
			 pstm.setDate(2, null);
		}
		pstm.setString(3, certificacio.getConcepte());
		pstm.setDouble(4, certificacio.getValor());
		pstm.setString(5, certificacio.getNombreFactura());
		if (certificacio.getDataEntrada() != null) {
			pstm.setDate(6, new java.sql.Date(certificacio.getDataEntrada().getTime()));
		} else {
			 pstm.setDate(6, null);
		}
		pstm.setInt(7, certificacio.getUsuariConformador().getIdUsuari());
		pstm.setString(8, certificacio.getNotes());
		if (certificacio.getDataConformacio() != null) {
			pstm.setDate(9, new java.sql.Date(certificacio.getDataConformacio().getTime()));
		} else {
			 pstm.setDate(9, null);
		}
		if (certificacio.getDataEnviatConformador() != null) {
			pstm.setDate(10, new java.sql.Date(certificacio.getDataEnviatConformador().getTime()));
		} else {
			 pstm.setDate(10, null);
		}
		if (certificacio.getDataEnviatComptabilitat() != null) {
			pstm.setDate(11, new java.sql.Date(certificacio.getDataEnviatComptabilitat().getTime()));
		} else {
			 pstm.setDate(11, null);
		}
		pstm.setString(12, certificacio.getIdFactura());
		pstm.executeUpdate();
	}
	
	public static Factura getFactura(Connection conn, String idFactura) throws SQLException, NamingException{
		Factura factura = new Factura();
		String sql = "SELECT " + SQL_CAMPS
			 		+ " FROM public.tbl_factures"
			 		+ " WHERE idfactura = ?";	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, idFactura);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			factura = initFactura(conn, rs, true);
		}
		return factura;
	}
	
	public static Factura getCertificacio(Connection conn, String idCertificacio) throws SQLException, NamingException{
		Factura factura = new Factura();
		String sql = "SELECT " + SQL_CAMPS_CERT
			 		+ " FROM public.tbl_certificacions"
			 		+ " WHERE idcertificacio = ?";	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, idCertificacio);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			factura = initCertificacio(conn, rs, true);
		}
		return factura;
	}
	
	public static List<Factura> getFacturesNoAssociades(Connection conn) throws SQLException, NamingException{
		List<Factura> factures = new ArrayList<Factura>();
		String CAMPS_FACTURA = "f.idfactura AS idfactura, f.idinforme AS idinforme, f.idactuacio AS idactuacio, f.nifproveidor AS nifproveidor, f.datafactura AS datafactura, f.concepte AS concepte, f.import AS import, f.tipusfactura AS tipusfactura, f.nombrefactura AS nombrefactura, f.dataentrada AS dataentrada, f.usuconformador AS usuconformador,  f.dataconformador AS dataconformador, f.notes AS notes, f.usucre AS usucre, f.datacre AS datacre, f.dataenviatconformador AS dataenviatconformador, f.dataenviatcomptabilitat AS dataenviatcomptabilitat, f.datadescarregadafactura AS datadescarregadafactura, f.anulada AS anulada, f.motiuanulada AS motiuanulada";
		String sql = "SELECT " + CAMPS_FACTURA
		 		+ " FROM public.tbl_factures f LEFT JOIN public.tbl_informeactuacio i ON f.idinforme = i.idinf"
		 		+ " WHERE f.anulada = false AND f.dataenviatcomptabilitat IS NULL AND i.idinf IS NULL";
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			factures.add(initFactura(conn, rs, false));
		}
		return factures;
	}
	
	public static List<Factura> advancedSearchFactures(Connection conn, Date dataInici, Date dataFi, String estatFactura) throws SQLException, NamingException{
		List<Factura> factures = new ArrayList<Factura>();
		String CAMPS_FACTURA = "f.idfactura AS idfactura, f.idinforme AS idinforme, f.idactuacio AS idactuacio, f.nifproveidor AS proveidorfactura, f.datafactura AS datafactura, f.concepte AS concepte, f.import AS import, f.nombrefactura AS nombrefactura, f.dataentrada AS dataentrada, f.usuconformador AS usuconformador,  f.dataconformador AS dataconformador, f.dataenviatconformador AS dataenviatconformador, f.dataenviatcomptabilitat AS dataenviatcomptabilitat, f.datadescarregadafactura AS datadescarregadafactura, f.notes AS notesfactura, f.anulada AS anulada, f.motiuanulada AS motiuanulada";
		String CAMPS_INFORME = "i.usucre AS usuariinforme, i.datacre AS datacreacioinforme, i.usuaprovacio AS usuaprovacioinforme, i.dataaprovacio AS dataaprovacioinforme, i.notes AS notesinforme, i.tipo AS tipocontracte, i.expcontratacio AS expcontratacio, i.datapd AS datapd, i.tipopd AS tipopd";
		String CAMPS_ACTUACIO = "a.descripcio AS descactuacio, a.usucre AS usucreactuacio, a.datacre AS datacreactuacio, a.dataaprovacio AS dataaprovacioactuacio, a.idcentre AS idcentre, a.notes AS notesactuacio";
		String sql = "SELECT " + CAMPS_FACTURA + ", " + CAMPS_INFORME + ", " + CAMPS_ACTUACIO 
		 		+ " FROM public.tbl_factures f LEFT JOIN public.tbl_informeactuacio i ON f.idinforme = i.idinf"
		 		+ " LEFT JOIN public.tbl_actuacio a ON i.idactuacio = a.id";
		
		boolean primeraCondicio = true;
		if (dataInici != null) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE datafactura >= ?";
			}			
		}
		if (dataFi != null) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE datafactura <= ?";
			} else {
				sql += " and datafactura <= ?";
			}			
		}		
		if (!estatFactura.equals("-1")) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE";
				switch (estatFactura) {
					case "pendConf":
						sql += " anulada = FALSE AND dataconformador IS NULL AND dataenviatcomptabilitat IS NULL";
						break;
					case "confor":
						sql += " anulada = FALSE AND dataconformador IS NOT NULL AND datadescarregadafactura IS NULL";
						break;
					case "noDesc":
						sql += " anulada = FALSE AND datadescarregadafactura IS NULL";
						break;
				}
			} else {
				sql += " AND";
				switch (estatFactura) {
					case "pendConf":
						sql += " anulada = FALSE AND dataconformador IS NULL AND dataenviatcomptabilitat IS NULL";
						break;
					case "confor":
						sql += " anulada = FALSE AND dataconformador IS NOT NULL AND datadescarregadafactura IS NULL";
						break;
					case "noDesc":
						sql += " anulada = FALSE AND datadescarregadafactura IS NULL";
						break;					
				}
			}			
		}
		PreparedStatement pstm = conn.prepareStatement(sql);	
		int contVars = 1;
		if (dataInici != null) {
			pstm.setDate(contVars, new java.sql.Date(dataInici.getTime()));
			contVars += 1;
		}
		if (dataFi != null) {
			pstm.setDate(contVars, new java.sql.Date(dataFi.getTime()));
			contVars += 1;
		}		
		ResultSet rs = pstm.executeQuery();	
		Factura factura = new Factura();
		InformeActuacio informe = new InformeActuacio();
		Actuacio actuacio = new Actuacio();
		while (rs.next()) {
			factura = new Factura();
			factura.setIdFactura(rs.getString("idfactura"));
			factura.setIdInforme(rs.getString("idinforme"));	
			factura.setIdActuacio(rs.getString("idactuacio"));
			factura.setIdProveidor(rs.getString("proveidorfactura"));
			factura.setDataFactura(rs.getTimestamp("datafactura"));
			factura.setConcepte(rs.getString("concepte"));
			factura.setValor(rs.getDouble("import"));			
			factura.setNombreFactura(rs.getString("nombrefactura"));
			factura.setDataEntrada(rs.getTimestamp("dataentrada"));
			factura.setUsuariConformador(UsuariCore.findUsuariByID(conn, rs.getInt("usuconformador")));
			factura.setDataConformacio(rs.getTimestamp("dataconformador"));
			factura.setDataDescarregadaConformada(rs.getTimestamp("datadescarregadafactura"));
			factura.setNotes(rs.getString("notesfactura"));
			factura.setAnulada(rs.getBoolean("anulada"));
			factura.setMotiuAnulada(rs.getString("motiuanulada"));
			factura.setFactura(FacturaCore.getArxiu(ActuacioCore.findActuacio(conn, rs.getString("idactuacio")).getIdIncidencia(), rs.getString("idactuacio"), rs.getString("idinforme"), rs.getString("proveidorfactura"), rs.getString("idfactura"), false));
			informe = new InformeActuacio();
			informe.setIdInf(rs.getString("idinforme"));	
			User usuari = UsuariCore.findUsuariByID(conn, rs.getInt("usuariinforme"));
			informe.setUsuari(usuari);
			informe.setDataCreacio(rs.getTimestamp("datacreacioinforme"));
			informe.setPropostaInformeSeleccionada(InformeCore.getPropostaSeleccionada(conn, rs.getString("idinforme")));
			informe.setAssignacioCredit(CreditCore.getPartidaInforme(conn, rs.getString("idinforme")));	
			informe.setUsuariAprovacio(UsuariCore.findUsuariByID(conn, rs.getInt("usuaprovacioinforme")));
			informe.setDataAprovacio(rs.getTimestamp("dataaprovacioinforme"));
			informe.setNotes(rs.getString("notesinforme"));			
			informe.setOfertaSeleccionada(OfertaCore.findOfertaSeleccionada(conn, rs.getString("idinforme")));			
			informe.setTipo(rs.getString("tipocontracte"));
			informe.setExpcontratacio(ExpedientCore.findExpedient(conn, rs.getString("expcontratacio")));
			informe.setDataPD(rs.getTimestamp("datapd"));
			informe.setTipoPD(rs.getString("tipopd"));
			factura.setInforme(informe);
			
			actuacio = new Actuacio();			
			actuacio.setReferencia(rs.getString("idactuacio"));
			actuacio.setDescripcio(rs.getString("descactuacio"));		
			actuacio.setDataCreacio(rs.getTimestamp("datacreactuacio"));
			actuacio.setIdUsuariCreacio(rs.getInt("usucreactuacio"));		
			actuacio.setCentre(CentreCore.findCentre(conn, rs.getString("idcentre"), false));			
			actuacio.setDataAprovacio(rs.getTimestamp("dataaprovacioactuacio"));		
			actuacio.setNotes(rs.getString("notesactuacio"));
			factura.setActuacio(actuacio);
			
			factures.add(factura);
		}
		return factures;
	}
	
	public static List<Factura> advancedSearchCertificacions(Connection conn, Date dataInici, Date dataFi, String idCentre) throws SQLException, NamingException{
		List<Factura> factures = new ArrayList<Factura>();
		String CAMPS_FACTURA = "c.idcertificacio AS idcertificacio, c.idinforme AS idinforme, c.idactuacio AS idactuacio, c.nifproveidor AS proveidorcertificacio, c.datacertificacio AS datacertificacio, c.concepte AS concepte, c.import AS import, c.nombrecertificacio AS nombrecertificacio, c.dataentrada AS dataentrada, c.usuconformador AS usuconformador,  c.dataconformador AS dataconformador, c.notes AS notescertificacio, c.anulada AS anulada, c.motiuanulada AS motiuanulada";
		String CAMPS_INFORME = "i.usucre AS usuariinforme, i.datacre AS datacreacioinforme, i.usuaprovacio AS usuaprovacioinforme, i.dataaprovacio AS dataaprovacioinforme, i.notes AS notesinforme, i.tipo AS tipocontracte, i.expcontratacio AS expcontratacio, i.datapd AS datapd, i.tipopd AS tipopd";
		String CAMPS_ACTUACIO = "a.descripcio AS descactuacio, a.usucre AS usucreactuacio, a.datacre AS datacreactuacio, a.dataaprovacio AS dataaprovacioactuacio, a.idcentre AS idcentre, a.notes AS notesactuacio";
		String sql = "SELECT " + CAMPS_FACTURA + ", " + CAMPS_INFORME + ", " + CAMPS_ACTUACIO 
		 		+ " FROM public.tbl_certificacions c LEFT JOIN public.tbl_informeactuacio i ON c.idinforme = i.idinf"
		 		+ " LEFT JOIN public.tbl_actuacio a ON i.idactuacio = a.id";	
		boolean primeraCondicio = true;
		if (dataInici != null) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE datacertificacio >= ?";
			}			
		}
		if (dataFi != null) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE datacertificacio <= ?";
			} else {
				sql += " AND datacertificacio <= ?";
			}			
		}	
		if (idCentre != null && !idCentre.equals("-1")) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE idcentre = ?";
			} else {
				sql += " AND idcentre = ?";
			}			
		}	
		
		PreparedStatement pstm = conn.prepareStatement(sql);	
		int contVars = 1;
		if (dataInici != null) {
			pstm.setDate(contVars, new java.sql.Date(dataInici.getTime()));
			contVars += 1;
		}
		if (dataFi != null) {
			pstm.setDate(contVars, new java.sql.Date(dataFi.getTime()));
			contVars += 1;
		}
		if (idCentre != null && !idCentre.equals("-1")) {
			pstm.setString(contVars,idCentre);
			contVars += 1;
		}
		ResultSet rs = pstm.executeQuery();	
		Factura factura = new Factura();
		InformeActuacio informe = new InformeActuacio();
		Actuacio actuacio = new Actuacio();
		while (rs.next()) {
			factura = new Factura();
			factura.setIdFactura(rs.getString("idcertificacio"));
			factura.setIdInforme(rs.getString("idinforme"));	
			factura.setIdActuacio(rs.getString("idactuacio"));
			factura.setIdProveidor(rs.getString("proveidorcertificacio"));
			factura.setDataFactura(rs.getTimestamp("datacertificacio"));
			factura.setConcepte(rs.getString("concepte"));
			factura.setValor(rs.getDouble("import"));			
			factura.setNombreFactura(rs.getString("nombrecertificacio"));
			factura.setDataEntrada(rs.getTimestamp("dataentrada"));
			factura.setUsuariConformador(UsuariCore.findUsuariByID(conn, rs.getInt("usuconformador")));
			factura.setDataConformacio(rs.getTimestamp("dataconformador"));
			factura.setNotes(rs.getString("notescertificacio"));
			factura.setAnulada(rs.getBoolean("anulada"));
			factura.setMotiuAnulada(rs.getString("motiuanulada"));
			informe = new InformeActuacio();
			informe.setIdInf(rs.getString("idinforme"));	
			User usuari = UsuariCore.findUsuariByID(conn, rs.getInt("usuariinforme"));
			informe.setUsuari(usuari);
			informe.setDataCreacio(rs.getTimestamp("datacreacioinforme"));
			informe.setPropostaInformeSeleccionada(InformeCore.getPropostaSeleccionada(conn, rs.getString("idinforme")));
			informe.setAssignacioCredit(CreditCore.getPartidaInforme(conn, rs.getString("idinforme")));	
			informe.setUsuariAprovacio(UsuariCore.findUsuariByID(conn, rs.getInt("usuaprovacioinforme")));
			informe.setDataAprovacio(rs.getTimestamp("dataaprovacioinforme"));
			informe.setNotes(rs.getString("notesinforme"));			
			informe.setOfertaSeleccionada(OfertaCore.findOfertaSeleccionada(conn, rs.getString("idinforme")));			
			informe.setTipo(rs.getString("tipocontracte"));
			informe.setExpcontratacio(ExpedientCore.findExpedient(conn, rs.getString("expcontratacio")));
			informe.setDataPD(rs.getTimestamp("datapd"));
			informe.setTipoPD(rs.getString("tipopd"));
			factura.setInforme(informe);
			
			actuacio = new Actuacio();			
			actuacio.setReferencia(rs.getString("idactuacio"));
			actuacio.setDescripcio(rs.getString("descactuacio"));		
			actuacio.setDataCreacio(rs.getTimestamp("datacreactuacio"));
			actuacio.setIdUsuariCreacio(rs.getInt("usucreactuacio"));		
			actuacio.setCentre(CentreCore.findCentre(conn, rs.getString("idcentre"), false));			
			actuacio.setDataAprovacio(rs.getTimestamp("dataaprovacioactuacio"));		
			actuacio.setNotes(rs.getString("notesactuacio"));
			factura.setActuacio(actuacio);
			
			factures.add(factura);
		}
		return factures;
	}
	
	public static List<Factura> searchFactures(Connection conn, Date dataInici, Date dataFi) throws SQLException, NamingException{
		List<Factura> factures = new ArrayList<Factura>();
		String sql = "SELECT " + SQL_CAMPS
			 		+ " FROM public.tbl_factures"
			 		+ " WHERE datafactura >= ? and datafactura <= ?";	 
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setDate(1, new java.sql.Date(dataInici.getTime()));
		pstm.setDate(2, new java.sql.Date(dataFi.getTime()));
		ResultSet rs = pstm.executeQuery();		
		while (rs.next()) {
			factures.add(initFactura(conn, rs, false));
		}
		return factures;
	}

	public static List<Factura> getFacturesActuacio(Connection conn, String idActuacio) throws SQLException, NamingException {
		List<Factura> factures = new ArrayList<Factura>();
		String sql = "SELECT " + SQL_CAMPS
			 		+ " FROM public.tbl_factures"
			 		+ " WHERE idactuacio = ?";	 
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, idActuacio);
		ResultSet rs = pstm.executeQuery();		
		while (rs.next()) {
			factures.add(initFactura(conn, rs, false));
		}
		return factures;
	}
	
	public static List<Factura> getFacturesInforme(Connection conn, String idInforme) throws SQLException, NamingException {
		List<Factura> factures = new ArrayList<Factura>();
		String sql = "SELECT " + SQL_CAMPS
			 		+ " FROM public.tbl_factures"
			 		+ " WHERE idinforme = ?";	 
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, idInforme);
		ResultSet rs = pstm.executeQuery();		
		while (rs.next()) {
			factures.add(initFactura(conn, rs, false));
		}
		return factures;
	}
	
	public static List<Factura> getCertificacionsInforme(Connection conn, String idInforme) throws SQLException, NamingException {
		List<Factura> factures = new ArrayList<Factura>();
		String sql = "SELECT " + SQL_CAMPS_CERT
			 		+ " FROM public.tbl_certificacions"
			 		+ " WHERE idinforme = ?";	 
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, idInforme);
		ResultSet rs = pstm.executeQuery();		
		while (rs.next()) {
			factures.add(initCertificacio(conn, rs, false));
		}
		return factures;
	}	
	
	public static String getNewCode(Connection conn) throws SQLException {
		String code = "1";
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String yearInString = String.valueOf(year);
		String sql = "SELECT idfactura, datafactura"
					+ " FROM public.tbl_factures"
					+ " WHERE idfactura like '%" + yearInString + "-FA%'"
					+ " ORDER BY idfactura DESC, datacre DESC LIMIT 1;";	
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();
		
		String prefix = "FA";
		if (rs.next()) { //Codis nous
			String actualCode = rs.getString("idfactura");			
			int num = Integer.valueOf(actualCode.split("-")[2]);
			String numFormatted = String.format("%04d", num + 1);
			code = yearInString + "-" + prefix + "-" + numFormatted;
		} else { //Codis antics
			int num = 0;		
			String numFormatted = String.format("%04d", num + 1);
			code = yearInString + "-" + prefix + "-" + numFormatted;
		}
		return code;
	}
	
	public static String getNewCodeCert(Connection conn) throws SQLException {
		String code = "1";
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String yearInString = String.valueOf(year);
		String sql = "SELECT idcertificacio, datacertificacio"
					+ " FROM public.tbl_certificacions"
					+ " WHERE idcertificacio like '%" + yearInString + "-FA%'"
					+ " ORDER BY idcertificacio DESC, datacre DESC LIMIT 1;";	
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();
		
		String prefix = "FA";
		if (rs.next()) { //Codis nous
			String actualCode = rs.getString("idcertificacio");			
			int num = Integer.valueOf(actualCode.split("-")[2]);
			String numFormatted = String.format("%04d", num + 1);
			code = yearInString + "-" + prefix + "-" + numFormatted;
		} else { //Codis antics
			int num = 0;		
			String numFormatted = String.format("%04d", num + 1);
			code = yearInString + "-" + prefix + "-" + numFormatted;
		}
		return code;
	}
	
	public static void descarregada(Connection conn, String idFactura) throws SQLException {
		String sql = "UPDATE  public.tbl_factures"
				+ " SET datadescarregadafactura = localtimestamp"
		 		+ " WHERE idfactura = ?";	 
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, idFactura);
		pstm.executeUpdate();
	}
	
	public static void anular(Connection conn, String idFactura, String motiu) throws SQLException, NamingException {
		String sql = "UPDATE  public.tbl_factures"
				+ " SET anulada = true, motiuanulada = ?"
		 		+ " WHERE idfactura = ?";	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, motiu);
		pstm.setString(2, idFactura);
		pstm.executeUpdate();
	}
	
	public static void anularCertificacio(Connection conn, String idFactura, String motiu) throws SQLException, NamingException {
		String sql = "UPDATE  public.tbl_certificacions"
				+ " SET anulada = true, motiuanulada = ?"
		 		+ " WHERE idcertificacio = ?";	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, motiu);
		pstm.setString(2, idFactura);
		pstm.executeUpdate();
	}
	
	public static List<Factura> getFacturesConformadesPend(Connection conn) throws SQLException, NamingException {
		List<Factura> facturesList = new ArrayList<Factura>();
		String sql = "SELECT " + SQL_CAMPS
				+ " FROM public.tbl_factures"
				+ " WHERE anulada = false AND dataenviatcomptabilitat IS NOT NULL AND datadescarregadafactura IS NULL";	
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {			
			facturesList.add(initFactura(conn, rs, false));
		}
		return facturesList;
	}
	
	public static List<Factura> getFacturesPerInforme(Connection conn, String nif, Date dataIni, Date dataFi) throws SQLException, NamingException {
		List<Factura> facturesList = new ArrayList<Factura>();
		String sql = "SELECT idinforme, SUM(import) AS total"
				+ " FROM public.tbl_factures"
				+ " WHERE anulada = false AND nifproveidor = ? AND datafactura BETWEEN ? AND ?"
				+ " GROUP BY idinforme;";	
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, nif);
		pstm.setDate(2, new java.sql.Date(dataIni.getTime()));
		pstm.setDate(3, new java.sql.Date(dataFi.getTime()));
		ResultSet rs = pstm.executeQuery();
		Factura factura = null;
		while (rs.next()) {		
			factura = new Factura();
			factura.setInforme(InformeCore.getInformePrevi(conn, rs.getString("idinforme"), false));
			factura.setValor(rs.getDouble("total"));
			facturesList.add(factura);
		}
		return facturesList;
	}
}
