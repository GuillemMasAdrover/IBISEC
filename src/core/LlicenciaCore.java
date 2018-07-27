package core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

import bean.InformeActuacio;
import bean.Llicencia;
import utils.Fitxers;
import utils.Fitxers.Fitxer;

public class LlicenciaCore {
	static final String SQL_CAMPS = "codi, expcontratacio, tipus, taxa, observacio, datapagadataxa, datapagadaico, importatib, ico, datasolicitud, dataconcesio";
	
	private static Llicencia initLlicencia(Connection conn, ResultSet rs, String idIncidencia, String idInforme) throws SQLException, NamingException{
		Llicencia llicencia = new Llicencia();
		llicencia.setCodi(rs.getString("codi"));
		llicencia.setCodiExpedient(rs.getString("expcontratacio"));
		llicencia.setTipus(rs.getString("tipus"));
		llicencia.setTaxa(rs.getDouble("taxa"));
		llicencia.setIco(rs.getDouble("ico"));		
		llicencia.setObservacio(rs.getString("observacio"));
		llicencia.setPeticio(rs.getTimestamp("datasolicitud"));
		llicencia.setConcesio(rs.getTimestamp("dataconcesio"));
		llicencia.setPagamentTaxa(rs.getTimestamp("datapagadataxa"));
		llicencia.setPagamentICO(rs.getTimestamp("datapagadaico"));
		llicencia.setArxius(getArxius(rs.getString("codi"), conn, idIncidencia, idInforme));
		llicencia.setDocumentSolLlicencia(getDocumentSolLlicenciaString(rs.getString("codi")));
		llicencia.setDocumentConcessioLlicencia(getDocumentConcessioLlicencia(rs.getString("codi")));
		llicencia.setDocumentPagamentLlicencia(getDocumentPagamentLlicencia(rs.getString("codi")));
		llicencia.setDocumentTitolHabilitant(getDocumentTitolHabilitant(rs.getString("codi")));
		llicencia.setIdPartida(getPartidaLlicencia(conn, rs.getString("codi")));
		return llicencia;
	}
	
	public static List<Llicencia> getLlicencies(Connection conn, String estat, String tipus) throws SQLException, NamingException {
		List<Llicencia> llicencies = new ArrayList<Llicencia>();
		String sql = "SELECT " + SQL_CAMPS
				+ " FROM public.tbl_llicencia";		
		boolean primeraCondicio = true;
		if (!tipus.isEmpty()) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE tipus = ?";
			}			
		}
		if (!estat.isEmpty()) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE";
			} else {
				sql += " AND";
			}	
			if ("pendent".equals(estat)) {
				sql += " datasolicitud IS NULL";
			}
			if ("solicitad".equals(estat)) {
				sql += " datasolicitud IS NOT NULL AND dataconcesio IS NULL";
			}
			if ("concedida".equals(estat)) {
				sql += " dataconcesio IS NOT NULL AND datapagada IS NULL";
			}
			if ("pagada".equals(estat)) {
				sql += " datapagada IS NOT NULL";
			}
		}
		
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);	
		
		int contVars = 1;
		if (!tipus.isEmpty()) {
			pstm.setString(contVars, tipus);
			contVars += 1;
		}		
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {		
			llicencies.add(initLlicencia(conn, rs, "", ""));
		}
		return llicencies;
	}
	
	public static Llicencia findLlicencia(Connection conn, String codi, String idIncidencia, String idInforme) throws SQLException, NamingException {
		Llicencia llicencia = new Llicencia();
		String sql = "SELECT " + SQL_CAMPS
				+ " FROM public.tbl_llicencia"
				+ " WHERE codi = ?";					
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);	
		pstm.setString(1, codi);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) llicencia = initLlicencia(conn, rs, idIncidencia, idInforme);
		return llicencia;
	}
	
	public static Llicencia findLlicenciaExpedient(Connection conn, String idInforme, String idIncidencia) throws SQLException, NamingException {
		Llicencia llicencia = new Llicencia();
		String sql = "SELECT " + SQL_CAMPS
				+ " FROM public.tbl_llicencia"
				+ " WHERE expcontratacio = ?";					
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);	
		pstm.setString(1, idInforme);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) llicencia = initLlicencia(conn, rs, idIncidencia, idInforme);
		return llicencia;
	}
	
	public static void updateLlicencia(Connection conn, Llicencia llicencia) throws SQLException {
		String sql = "UPDATE public.tbl_llicencia"
					+ " SET taxa=?, observacio=?, datapagadataxa=?, datapagadaico=?, ico=?, datasolicitud=?, dataconcesio=?"
					+ " WHERE codi = ?";	
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);	
		pstm.setDouble(1, llicencia.getTaxa());
		pstm.setString(2, llicencia.getObservacio());
		if (llicencia.getPagamentTaxa() != null) {
			pstm.setDate(3, new java.sql.Date(llicencia.getPagamentTaxa().getTime()));
		} else {
			pstm.setDate(3, null);
		}
		if (llicencia.getPagamentICO() != null) {
			pstm.setDate(4, new java.sql.Date(llicencia.getPagamentICO().getTime()));
		} else {
			pstm.setDate(4, null);
		}
		pstm.setDouble(5, llicencia.getIco());
		if (llicencia.getPeticio() != null) {
			pstm.setDate(6, new java.sql.Date(llicencia.getPeticio().getTime()));
		} else {
			pstm.setDate(6, null);
		}
		if (llicencia.getConcesio() != null) {
			pstm.setDate(7, new java.sql.Date(llicencia.getConcesio().getTime()));
		} else {
			pstm.setDate(7, null);
		}
		pstm.setString(8, llicencia.getCodi());
		pstm.executeUpdate();
	}
	
	public static String novaLlicencia(Connection conn, String expContractacio, String tipus) throws SQLException {
		String sql = "INSERT INTO public.tbl_llicencia(codi, expcontratacio, tipus, datacre)"
					+ " VALUES (?, ?, ?, localtimestamp);";
		PreparedStatement pstm;
		String codi = getNouCodi(conn);
		pstm = conn.prepareStatement(sql);	
		pstm.setString(1, codi);
		pstm.setString(2, expContractacio);
		pstm.setString(3, tipus);
		pstm.executeUpdate();
		return codi;
	}
	
	public static void novaLlicencia(Connection conn, String expContractacio, Llicencia llicencia) throws SQLException {
		String sql = "INSERT INTO public.tbl_llicencia(codi, expcontratacio, tipus, taxa, observacio, datapagadataxa, ico, datasolicitud, dataconcesio, datacre, datapagadaico)"
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, localtimestamp, ?);";	
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);	
		pstm.setString(1, getNouCodi(conn));
		pstm.setString(2, llicencia.getCodiExpedient());
		pstm.setString(3, llicencia.getTipus());
		pstm.setDouble(4, llicencia.getTaxa());
		pstm.setString(5, llicencia.getObservacio());
		if (llicencia.getPagamentTaxa() != null) {
			pstm.setDate(6, new java.sql.Date(llicencia.getPagamentTaxa().getTime()));
		} else {
			pstm.setDate(6, null);
		}
		pstm.setDouble(7, llicencia.getIco());
		if (llicencia.getPeticio() != null) {
			pstm.setDate(8, new java.sql.Date(llicencia.getPeticio().getTime()));
		} else {
			pstm.setDate(8, null);
		}
		if (llicencia.getConcesio() != null) {
			pstm.setDate(9, new java.sql.Date(llicencia.getConcesio().getTime()));
		} else {
			pstm.setDate(9, null);
		}
		if (llicencia.getPagamentICO() != null) {
			pstm.setDate(10, new java.sql.Date(llicencia.getPagamentICO().getTime()));
		} else {
			pstm.setDate(10, null);
		}
		pstm.executeUpdate();
	}
	
	private static String getNouCodi(Connection conn) throws SQLException {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String yearInString = String.valueOf(year);
		String code = "1";
		String sql = "SELECT codi, datacre"
					+ " FROM public.tbl_llicencia"
					+ " WHERE codi like '%" + yearInString + "-LLI%'"
					+ " ORDER BY codi DESC, datacre DESC LIMIT 1;";	
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();		
		String prefix = "LLI";
		if (rs.next()) { //Codis nous
			String actualCode = rs.getString("codi");			
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
	
	public static void guardarArxiu(Connection conn, List<Fitxer> fitxers, String codi, int idUsuari, String seccio) throws NamingException {
		if (fitxers != null) {
			String fileName = "";
			// Crear directoris si no existeixen
			 // Get the base naming context
		    Context env = (Context)new InitialContext().lookup("java:comp/env");
		    // Get a single value
			String ruta =  (String)env.lookup("ruta_base");
			File tmpFile = new File(ruta + "/documents/Llicencies");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/Llicencies/" + codi);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			fileName = ruta + "/documents/Llicencies/" + codi + "/";
			if (!seccio.isEmpty()) {
				tmpFile = new File(ruta + "/documents/Llicencies/" + codi + "/" + seccio);
				if (!tmpFile.exists()) {
					tmpFile.mkdir();
				}
				fileName = ruta + "/documents/Llicencies/" + codi + "/" + seccio + "/";
			}
			
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
	
	private static List<Fitxers.Fitxer> getArxius(String codi, Connection conn, String idIncidencia, String idInforme) throws NamingException, SQLException {
		List<Fitxer> fitxersList = new ArrayList<Fitxer>();
		 // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		 File dir = new File(ruta + "/documents/Llicencies/" + codi);
		 File[] fichers = dir.listFiles();
		 Fitxer fitxer = new Fitxer();
		 if (fichers == null) {
			
		 } else { 
			 for (int x=0;x<fichers.length;x++) {
				 if (!fichers[x].isDirectory()) {
					 fitxer = new Fitxer();
					 fitxer.setNom(fichers[x].getName());
					 fitxer.setRuta(ruta + "/documents/Llicencies/" + codi + "/" + fichers[x].getName());
					 try {
						fitxer.setData(Files.getLastModifiedTime(fichers[x].toPath()));
					 } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					 }
					 fitxersList.add(fitxer);
				 }
			 }
		 }
		 fitxersList.addAll(RegistreCore.getArxiusAdjuntsPerTipus(conn, idIncidencia, idInforme, "Autorització urbanística"));
		 return fitxersList;
	}
	
	private static List<Fitxer> getDocumentSolLlicenciaString(String codi) throws NamingException {
		List<Fitxer> fitxersList = new ArrayList<Fitxer>();
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
		String ruta =  (String)env.lookup("ruta_base");
		File dir = new File(ruta + "/documents/Llicencies/" + codi + "/Solicitud");
		File[] fichers = dir.listFiles();
		Fitxer fitxer = new Fitxer();
		if (fichers == null) {
			
		} else { 
			for (int x=0;x<fichers.length;x++) {
				fitxer = new Fitxer();
				fitxer.setNom(fichers[x].getName());
				fitxer.setRuta(ruta + "/documents/Llicencies/" + codi + "/Solicitud/" + fichers[x].getName());
				try {
					fitxer.setData(Files.getLastModifiedTime(fichers[x].toPath()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fitxersList.add(fitxer);
			}
		}
		return fitxersList;
	}
	private static List<Fitxer> getDocumentConcessioLlicencia(String codi) throws NamingException {
		List<Fitxer> fitxersList = new ArrayList<Fitxer>();
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
		String ruta =  (String)env.lookup("ruta_base");
		File dir = new File(ruta + "/documents/Llicencies/" + codi + "/Concessio");
		File[] fichers = dir.listFiles();
		Fitxer fitxer = new Fitxer();
		if (fichers == null) {
			
		} else { 
			for (int x=0;x<fichers.length;x++) {
				fitxer = new Fitxer();
				fitxer.setNom(fichers[x].getName());
				fitxer.setRuta(ruta + "/documents/Llicencies/" + codi + "/Concessio/" + fichers[x].getName());
				try {
					fitxer.setData(Files.getLastModifiedTime(fichers[x].toPath()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fitxersList.add(fitxer);
			}
		}
		return fitxersList;
	}
	private static List<Fitxer> getDocumentPagamentLlicencia(String codi) throws NamingException{
		List<Fitxer> fitxersList = new ArrayList<Fitxer>();
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
		String ruta =  (String)env.lookup("ruta_base");
		File dir = new File(ruta + "/documents/Llicencies/" + codi + "/Pagament");
		File[] fichers = dir.listFiles();
		Fitxer fitxer = new Fitxer();
		if (fichers == null) {
			
		} else { 
			for (int x=0;x<fichers.length;x++) {
				fitxer = new Fitxer();
				fitxer.setNom(fichers[x].getName());
				fitxer.setRuta(ruta + "/documents/Llicencies/" + codi + "/Pagament/" + fichers[x].getName());
				try {
					fitxer.setData(Files.getLastModifiedTime(fichers[x].toPath()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fitxersList.add(fitxer);
			}
		}
		return fitxersList;
	}
	private static List<Fitxer> getDocumentTitolHabilitant(String codi) throws NamingException {
		List<Fitxer> fitxersList = new ArrayList<Fitxer>();
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
		String ruta =  (String)env.lookup("ruta_base");
		File dir = new File(ruta + "/documents/Llicencies/" + codi + "/Habilitant");
		File[] fichers = dir.listFiles();
		Fitxer fitxer = new Fitxer();
		if (fichers == null) {
			
		} else { 
			for (int x=0;x<fichers.length;x++) {
				fitxer = new Fitxer();
				fitxer.setNom(fichers[x].getName());
				fitxer.setRuta(ruta + "/documents/Llicencies/" + codi + "/Habilitant/" + fichers[x].getName());
				try {
					fitxer.setData(Files.getLastModifiedTime(fichers[x].toPath()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fitxersList.add(fitxer);
			}
		}
		return fitxersList;
	}
	
	public static String getPartidaLlicencia(Connection conn, String codi) throws SQLException {
		String idPartida = "";
		String sql = "SELECT idpartida"
				+ " FROM public.tbl_assignacionscredit"
				+ " WHERE idinf = ?";					
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);	
		pstm.setString(1, codi);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) idPartida = rs.getString("idpartida");
		return idPartida;
	}
	
	public static void actualitzarPartida(Connection conn, Llicencia llicencia, double valor, InformeActuacio informe) throws SQLException {
		System.out.println(llicencia.getIdPartida());
		if (informe.getAssignacioCredit() != null && informe.getAssignacioCredit().getPartida() != null) {
			if (llicencia.getIdPartida() == null || llicencia.getIdPartida().isEmpty()) {
				String sql = "INSERT INTO public.tbl_assignacionscredit(idassignacio, idactuacio, idinf, reserva, assignacio, idpartida, valorpa, valorpd)"
							+ " VALUES (?, ?, ?, true, true, ?, ?, ?);";
				PreparedStatement pstm;
				pstm = conn.prepareStatement(sql);	
				pstm.setString(1, llicencia.getCodi());
				pstm.setString(2, informe.getActuacio().getReferencia());
				pstm.setString(3, llicencia.getCodi());
				if (informe.getAssignacioCredit() != null && informe.getAssignacioCredit().getPartida() != null) {
					pstm.setString(4, informe.getAssignacioCredit().getPartida().getCodi());
				} else {
					pstm.setString(4, "");
				}			
				pstm.setDouble(5, valor);
				pstm.setDouble(6, valor);
				pstm.executeUpdate();
			} else {
				String sql = "UPDATE public.tbl_assignacionscredit"
							+ " SET valorpa=?, valorpd=?"
							+ " WHERE idassignacio = ?;";
				PreparedStatement pstm;
				pstm = conn.prepareStatement(sql);	
				pstm.setDouble(1, valor);
				pstm.setDouble(2, valor);
				pstm.setString(3, llicencia.getCodi());
				pstm.executeUpdate();
			}
		}		
	}
}
