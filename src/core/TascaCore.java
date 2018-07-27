package core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.math.NumberUtils;

import bean.Historic;
import bean.InformeActuacio;
import bean.Tasca;
import utils.Fitxers;
import utils.Fitxers.Fitxer;

public class TascaCore {
	
	private static Tasca initTasca(Connection conn, ResultSet rs, boolean complet) throws SQLException, NamingException {
		Tasca tasca = new Tasca();
		tasca.setIdTasca(rs.getInt("idtasca"));		
		tasca.setUsuari(UsuariCore.findUsuariByID(conn,rs.getInt("idusuari")));
		tasca.setDepartament(rs.getString("departament"));
		if (!rs.getString("idactuacio").equals("-1"))  {
			tasca.setActuacio(ActuacioCore.findActuacio(conn,  rs.getString("idactuacio")));
		}
		if (!rs.getString("idincidencia").equals("-1"))  {
			tasca.setIncidencia(IncidenciaCore.findIncidencia(conn, rs.getString("idincidencia")));
		}
		tasca.setIdActuacio(rs.getString("idactuacio"));
		tasca.setActiva(rs.getBoolean("activa"));
		tasca.setDescripcio(rs.getString("descripcio"));
		tasca.setTipus(rs.getString("tipus"));
		tasca.setDataCreacio(TascaCore.findInici(conn, rs.getInt("idtasca")));
		tasca.setIdinforme(rs.getString("idinforme"));
		tasca.setIdinformeOriginal(rs.getString("idinforme"));
		tasca.setPrioritat(rs.getInt("prioritat"));		
		if (rs.getString("idinforme") != null) {
			if (NumberUtils.isNumber(rs.getString("idinforme")) || rs.getString("idinforme").contains("-INF-")) {
				tasca.setInforme(InformeCore.getInformePreviInfo(conn, rs.getString("idinforme")));
			} else if (rs.getString("idinforme").contains("-MOD-")) {
				InformeActuacio informe = InformeCore.getMoficacioInforme(conn, rs.getString("idinforme"));
				tasca.setInforme(informe);
				tasca.setIdinformeOriginal(informe.getIdInfOriginal());
			}
		}		
		tasca.setLlegida(rs.getBoolean("llegida"));		
		tasca.setPrimerComentari(findHistorial(conn, rs.getInt("idtasca"), rs.getString("idincidencia"), rs.getString("idactuacio")).get(0).getComentari());
		tasca.setDarreraModificacio(findDarreraModificacioHistorial(conn, rs.getInt("idtasca"), rs.getString("idincidencia"), rs.getString("idactuacio")));
		if (complet) tasca.setDocuments(getDocuments(rs.getString("idtasca"), rs.getString("idincidencia"), rs.getString("idactuacio")));
		return tasca;		
	}
	
	public static Tasca findTascaId(Connection conn, int idTasca, int idUsuari) throws SQLException, NamingException {
		String sql = "SELECT idtasca, idusuari, idactuacio, descripcio, tipus, activa, idincidencia, idinforme, llegida, departament, prioritat"
					+ " FROM public.tbl_tasques"
					+ " WHERE idtasca = ?";	 
		 PreparedStatement pstm = conn.prepareStatement(sql);	 
		 pstm.setInt(1, idTasca);		
		 ResultSet rs = pstm.executeQuery();
		 Tasca tasca = new Tasca();
		 if (rs.next()) {
			 tasca = initTasca(conn, rs, true);
			 tasca.setSeguiment(isSeguiment(conn,tasca.getIdTasca(), idUsuari));
			 if (tasca.getActuacio() != null) tasca.setSeguimentActuacio(ActuacioCore.isSeguimentActuacio(conn,tasca.getActuacio().getReferencia(), idUsuari));
		 }		
	     return tasca;
	}
	
	public static void assignarPrioritat(Connection conn, int idTasca, int prioritat) throws SQLException {
		String sql = "UPDATE public.tbl_tasques"
				+ " SET prioritat=?"
				+ " WHERE idtasca=?";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setInt(1, prioritat);	
		pstm.setInt(2, idTasca);
		pstm.executeUpdate();
	}
	
	public static List<List<Tasca>> llistaTasquesUsuari(Connection conn, int idUsuari, String area, boolean withTancades) throws SQLException, NamingException {
		String sql = "SELECT idtasca, idusuari, idactuacio, descripcio, tipus, activa, idincidencia, idinforme, llegida, departament, prioritat"
				 	+ " FROM public.tbl_tasques";
		if (idUsuari > -1) {
			sql  += " WHERE idusuari = ?";
			if (! withTancades) sql += " AND activa = true";
		} else if (area != null) {
			sql  += " WHERE departament = ?";
			if (! withTancades) sql += " AND activa = true";
		} else if (! withTancades) sql += " WHERE activa = true";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		if (idUsuari > -1) {
			pstm.setInt(1, idUsuari);
		} else if (area != null) {
			pstm.setString(1, area);
		}			
		ResultSet rs = pstm.executeQuery();
		List<List<Tasca>> list = new ArrayList<List<Tasca>>();
		List<Tasca> infPrevList = new ArrayList<Tasca>();
		List<Tasca> solInfPrevList = new ArrayList<Tasca>();
		List<Tasca> vistInfPrevList = new ArrayList<Tasca>();
        List<Tasca> redacDocTecnicaList = new ArrayList<Tasca>();
        List<Tasca> vistDocTecnicaList = new ArrayList<Tasca>();        
        List<Tasca> sigDocExpList = new ArrayList<Tasca>();
        List<Tasca> propAdjList = new ArrayList<Tasca>();
        List<Tasca> resAdjList = new ArrayList<Tasca>();
        List<Tasca> redContracteList = new ArrayList<Tasca>();
        List<Tasca> resCreditList = new ArrayList<Tasca>();
        List<Tasca> altresList = new ArrayList<Tasca>();  
        List<Tasca> docPreLicitacioList = new ArrayList<Tasca>();
        List<Tasca> judicialList = new ArrayList<Tasca>();
        List<Tasca> conformarFacturaList = new ArrayList<Tasca>();    
        List<Tasca> revisarCertificacioList = new ArrayList<Tasca>();
        List<Tasca> contractesList = new ArrayList<Tasca>();
        List<Tasca> contractesFirmaList = new ArrayList<Tasca>();
        List<Tasca> notificacionsList = new ArrayList<Tasca>();
        System.out.println("    1.1: " + new Date());
		while (rs.next()) {			
			if (rs.getString("tipus").equals("infPrev")) { // 0
				infPrevList.add(initTasca(conn, rs, false));
			} else if (rs.getString("tipus").equals("solInfPrev")) { // 1
				solInfPrevList.add(initTasca(conn, rs, false));
			} else if (rs.getString("tipus").equals("vistInfPrev")) { // 2
				vistInfPrevList.add(initTasca(conn, rs, false));
			} else if (rs.getString("tipus").equals("doctecnica")) { // 3
				redacDocTecnicaList.add(initTasca(conn, rs, false));
			} else if (rs.getString("tipus").equals("vistDocTecnica")) { // 4
				vistDocTecnicaList.add(initTasca(conn, rs, false));
			} else if (rs.getString("tipus").equals("autoritzacioActuacio")) { // 5
				sigDocExpList.add(initTasca(conn, rs, false));
			} else if (rs.getString("tipus").equals("liciMenor")) { // 6
				propAdjList.add(initTasca(conn, rs, false));
			} else if (rs.getString("tipus").equals("autoritzacioDespesa") || rs.getString("tipus").equals("autoritzacioModificacio")) { // 7
				resAdjList.add(initTasca(conn, rs, false));
			} else if (rs.getString("tipus").equals("doctecnica")) { // 8
				redContracteList.add(initTasca(conn, rs, false));
			} else if (rs.getString("tipus").equals("resPartida") || rs.getString("tipus").equals("resPartidaModificacio")) { // 9
				resCreditList.add(initTasca(conn, rs, false));
			} else if (rs.getString("tipus").equals("docprelicitacio") || rs.getString("tipus").equals("certificatCreditGerencia") || rs.getString("tipus").equals("preLicitacio")) { // 10
				docPreLicitacioList.add(initTasca(conn, rs, false));			
			} else if (rs.getString("tipus").equals("judicial") || rs.getString("tipus").equals("pagamentJudicial")) { // 11
				judicialList.add(initTasca(conn, rs, false));
			} else if (rs.getString("tipus").equals("conformarFactura")) { // 12
				conformarFacturaList.add(initTasca(conn, rs, false));
			} else if (rs.getString("tipus").equals("revisarCertificacio") || rs.getString("tipus").equals("firmaCertificacio") || rs.getString("tipus").equals("certificacioFirmada")) { // 13
				revisarCertificacioList.add(initTasca(conn, rs, false));
			} else if (rs.getString("tipus").equals("contracte")) {
				contractesList.add(initTasca(conn, rs, false)); // 14
			} else if (rs.getString("tipus").equals("firmaContracte")) {
				contractesFirmaList.add(initTasca(conn, rs, false)); // 15
			} else if (rs.getString("tipus").equals("notificacio")) {
				notificacionsList.add(initTasca(conn, rs, false)); // 17
			}else {
				altresList.add(initTasca(conn, rs, false)); // 16
			}
		}		
		list.add(infPrevList); // 0
        list.add(solInfPrevList); // 1
        list.add(vistInfPrevList); // 2
        list.add(redacDocTecnicaList); // 3
        list.add(vistDocTecnicaList);  // 4
        list.add(sigDocExpList); // 5
        list.add(propAdjList); // 6
        list.add(resAdjList);  // 7
        list.add(redContracteList);  // 8
        list.add(resCreditList); // 9
        list.add(docPreLicitacioList);  // 10
        list.add(judicialList);  // 11
        list.add(conformarFacturaList); // 12
        list.add(revisarCertificacioList); // 13
        list.add(contractesList);  // 14
        list.add(contractesFirmaList);  // 15 
        list.add(altresList);  // 16 
        list.add(notificacionsList);  // 17 
	  	return list;
    }
	
	public static List<Tasca> llistaTasquesSeguiment(Connection conn, int idUsuari) throws SQLException, NamingException {
		String sql = "SELECT t.idtasca AS idtasca, t.idusuari AS idusuari, t.idactuacio AS idactuacio, t.idincidencia AS idincidencia, t.descripcio AS descripcio, t.tipus AS tipus, t.activa AS activa, t.idinforme AS idinforme, t.llegida AS llegida, t.departament AS departament, t.prioritat AS prioritat"
					+ " FROM public.tbl_seguiments s LEFT JOIN public.tbl_tasques t ON s.idtasca = t.idtasca"
					+ " WHERE s.idusuari = ? AND tipus NOT LIKE '%notificacio%'";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setInt(1, idUsuari);		
		ResultSet rs = pstm.executeQuery();
		List<Tasca> list = new ArrayList<Tasca>();
		while (rs.next()) {
			Tasca tasca = initTasca(conn, rs, false);
			list.add(tasca);
		}
	    return list;
	}
	
	public static List<Tasca> llistaNotificacionsUsuari(Connection conn, int idUsuari) throws SQLException, NamingException {
		 String sql = "SELECT idtasca, idusuari, idactuacio, descripcio, tipus, activa, idincidencia, idinforme, llegida, departament, prioritat"
				 	+ " FROM public.tbl_tasques"
				 	+ " WHERE idusuari = ? AND activa = true AND tipus LIKE '%notificacio%'";	 
		 PreparedStatement pstm = conn.prepareStatement(sql);	 
		 pstm.setInt(1, idUsuari);		
		 ResultSet rs = pstm.executeQuery();
		 List<Tasca> list = new ArrayList<Tasca>();
		 while (rs.next()) {
			 Tasca tasca = initTasca(conn, rs, false);
			 list.add(tasca);
		 }
	     return list;
   }
	
	public static void llegirNotificacio(Connection conn, int idNotificacio) throws SQLException {
		String sql = "UPDATE public.tbl_tasques"
				+ " SET llegida=true"
				+ " WHERE idtasca=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm = conn.prepareStatement(sql);	 
		pstm.setInt(1, idNotificacio);
		pstm.executeUpdate();
	}
	
	public static int numNotificacions(Connection conn, int idUsuari) throws SQLException {
		int notificacions = 0;
		String sql = "SELECT COUNT(*) AS notificacions"
			 	+ " FROM public.tbl_tasques"
			 	+ " WHERE idusuari = ? AND activa = true AND tipus LIKE '%notificacio%' AND llegida = false";	 
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setInt(1, idUsuari);		
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			notificacions = rs.getInt("notificacions");
		}
		return notificacions;
	}
	
	public static List<Tasca> findTasquesActuacio(Connection conn, String referencia, boolean withCancelades) throws SQLException, NamingException {
		 String sql = "SELECT idtasca, idusuari, idactuacio, descripcio, tipus, activa, idincidencia, idinforme, llegida, departament, prioritat"
				 	+ " FROM public.tbl_tasques"
				 	+ " WHERE idactuacio = ? AND tipus NOT LIKE '%notificacio%'";	 
		 if (!withCancelades) sql += " AND activa = true";
		 PreparedStatement pstm = conn.prepareStatement(sql);	 
		 pstm.setString(1, referencia);		
		 ResultSet rs = pstm.executeQuery();
		 List<Tasca> list = new ArrayList<Tasca>();
		 while (rs.next()) {
			 Tasca tasca = initTasca(conn, rs, false);
			 list.add(tasca);
		 }
	     return list;
	}
	
	public static List<Tasca> findNotificacionsActuacio(Connection conn, String referencia) throws SQLException, NamingException {
		 String sql = "SELECT idtasca, idusuari, idactuacio, descripcio, tipus, activa, idincidencia, idinforme, llegida, departament, prioritat"
				 	+ " FROM public.tbl_tasques"
				 	+ " WHERE idactuacio = ? AND tipus LIKE '%notificacio%'";	 
		 PreparedStatement pstm = conn.prepareStatement(sql);	 
		 pstm.setString(1, referencia);		
		 ResultSet rs = pstm.executeQuery();
		 List<Tasca> list = new ArrayList<Tasca>();
		 while (rs.next()) {
			 Tasca tasca = initTasca(conn, rs, false);
			 list.add(tasca);
		 }
	     return list;
	}
	
	public static List<Tasca> findTasquesIncidencia(Connection conn, String referencia) throws SQLException, NamingException {
		 String sql = "SELECT idtasca, idusuari, idactuacio, descripcio, tipus, activa, idincidencia, idinforme, llegida, departament, prioritat"
				 	+ " FROM public.tbl_tasques"
				 	+ " WHERE idincidencia = ? AND tipus NOT LIKE '%notificacio%'";	 
		 PreparedStatement pstm = conn.prepareStatement(sql);	 
		 pstm.setString(1, referencia);		
		 ResultSet rs = pstm.executeQuery();
		 List<Tasca> list = new ArrayList<Tasca>();
		 while (rs.next()) {
			 Tasca tasca = initTasca(conn, rs, false);
			 list.add(tasca);
		 }
	     return list;
	}
	
	public static List<Tasca> findNotificacionsIncidencia(Connection conn, String referencia) throws SQLException, NamingException {
		 String sql = "SELECT idtasca, idusuari, idactuacio, descripcio, tipus, activa, idincidencia, idinforme, llegida, departament, prioritat"
				 	+ " FROM public.tbl_tasques"
				 	+ " WHERE idincidencia = ? AND tipus LIKE '%notificacio%'";	 
		 PreparedStatement pstm = conn.prepareStatement(sql);	 
		 pstm.setString(1, referencia);		
		 ResultSet rs = pstm.executeQuery();
		 List<Tasca> list = new ArrayList<Tasca>();
		 while (rs.next()) {
			 Tasca tasca = initTasca(conn, rs, false);
			 list.add(tasca);
		 }
	     return list;
	}
	
	public static List<Tasca> findTasquesJudicial(Connection conn, String referencia) throws SQLException, NamingException {
		 String sql = "SELECT idtasca, idusuari, idactuacio, descripcio, tipus, activa, idincidencia, idinforme, llegida, departament, prioritat"
				 	+ " FROM public.tbl_tasques"
				 	+ " WHERE idinforme = ?";	 
		 PreparedStatement pstm = conn.prepareStatement(sql);	 
		 pstm.setString(1, referencia);		
		 ResultSet rs = pstm.executeQuery();
		 List<Tasca> list = new ArrayList<Tasca>();
		 while (rs.next()) {
			 Tasca tasca = initTasca(conn, rs, false);
			 list.add(tasca);
		 }
	     return list;
	}
	
	public static Tasca findTascaVacances(Connection conn, int idSolicitud) throws SQLException, NamingException {
		 String sql = "SELECT idtasca, idusuari, idactuacio, descripcio, tipus, activa, idincidencia, idinforme, llegida, departament, prioritat"
				 	+ " FROM public.tbl_tasques"
				 	+ " WHERE tipus = 'vacances' AND activa = true AND idinforme = ?" ;	 
		 PreparedStatement pstm = conn.prepareStatement(sql);	 
		 pstm.setString(1, Integer.toString(idSolicitud));		
		 ResultSet rs = pstm.executeQuery();
		 Tasca tasca = new Tasca();
		 if (rs.next()) tasca = initTasca(conn, rs, false);
	     return tasca;
	}
	
	public static List<Tasca> findTasquesInforme(Connection conn, String idInforme, boolean withCancelades) throws SQLException, NamingException {
		String sql = "SELECT idtasca, idusuari, idactuacio, descripcio, tipus, activa, idincidencia, idinforme, llegida, departament, prioritat"
			 	+ " FROM public.tbl_tasques"
			 	+ " WHERE idinforme = ?";	
		 if (!withCancelades) {
			 sql += " AND activa = true";
		 }
		 PreparedStatement pstm = conn.prepareStatement(sql);	 
		 pstm.setString(1, idInforme);		
		 ResultSet rs = pstm.executeQuery();
		 List<Tasca> list = new ArrayList<Tasca>();
		 while (rs.next()) {
			 Tasca tasca = initTasca(conn, rs, false);
			 list.add(tasca);
		 }
	     return list;
	}
	
	public static Date findInici(Connection conn, int idTasca) throws SQLException{
		Date dataInici = new Date();
		String sql = "SELECT data"
					+ " FROM public.tbl_historial"
					+ " WHERE idtasca = ?"
					+ " ORDER BY data"
					+ " LIMIT 1;";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, String.valueOf(idTasca));		
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			dataInici = rs.getTimestamp("data");
		}
		return dataInici;
	}
	
	public static List<Historic> findHistorial(Connection conn, int idTasca, String idIncidencia, String idActuacio) throws SQLException, NamingException {
		String sql = "SELECT idhistoric, comentari, idusuari, data, ipremota, tipus"
					+ " FROM public.tbl_historial"
					+ " WHERE idtasca = ?"
					+ " ORDER BY idhistoric ASC";	 
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, String.valueOf(idTasca));		
		ResultSet rs = pstm.executeQuery();
		List<Historic> list = new ArrayList<Historic>();
		while (rs.next()) {
			Historic historic = new Historic();
			historic.setComentari(rs.getString("comentari"));
			historic.setData(rs.getTimestamp("data"));
			historic.setIdHistoric(rs.getInt("idhistoric"));
			historic.setIdTasca(idTasca);
			historic.setUsuari(UsuariCore.findUsuariByID(conn, rs.getInt("idusuari")));
			historic.setIpRemota(rs.getString("ipremota"));
			historic.setTipus(rs.getString("tipus"));
			if (!idIncidencia.equals("-1")) historic.setAdjunts(utils.Fitxers.ObtenirFitxers(idIncidencia, idActuacio, "Tasca", String.valueOf(idTasca), rs.getString("idhistoric")));
			list.add(historic);
		}
	    return list;
	}
	
	public static Date findDarreraModificacioHistorial(Connection conn, int idTasca, String idIncidencia, String idActuacio) throws SQLException {
		String sql = "SELECT data"
					+ " FROM public.tbl_historial"
					+ " WHERE idtasca = ?"
					+ " ORDER BY idhistoric DESC LIMIT 1";	 
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, String.valueOf(idTasca));		
		ResultSet rs = pstm.executeQuery();
		Date data = new Date();
		if (rs.next())  data = rs.getTimestamp("data");			
	    return data;
	}
	
	public static int novaTasca(Connection conn, String tipus, int idUsuari, int idUsuariComentari, String idActuacio, String idIncidencia, String comentari, String assumpte, String idInformePrevi, List<Fitxer> adjunts, String ipRemota, String tipusHistorial) throws SQLException, NamingException {
		String sql = "INSERT INTO public.tbl_tasques(idtasca, idusuari, idactuacio, descripcio, tipus, activa, idincidencia, idinforme, llegida, departament)"
					+" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";		 
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		int idNovaTasca = idNovaTasca(conn);
		
		if ("solInfPrev".equals(tipus)) {
			assumpte = "Sol·licitud informe previ";
		} else if("resPartida".equals(tipus)) {	
			assumpte = "Sol·licitud reserva partida";
			comentari = "Sol·licitud reserva partida";
			tipusHistorial = "automatic";
		} else if("liciMenor".equals(tipus)) {
			assumpte = "Proposta d'adjudicació";
			comentari ="1. Recerca de pressupostos";
			tipusHistorial = "automatic";
		} else if("liciMajor".equals(tipus)) {
			assumpte = "Licitació contracte major";
			comentari = "Iniciar procés licitació contracte major";		
			tipusHistorial = "automatic";
		}
		pstm.setInt(1, idNovaTasca);
		pstm.setInt(2, idUsuari);
		pstm.setString(3, idActuacio);
		pstm.setString(4, assumpte);
		pstm.setString(5, tipus);
		pstm.setBoolean(6, true);
		pstm.setString(7, idIncidencia);
		pstm.setString(8, idInformePrevi);
		pstm.setBoolean(9, false);
		pstm.setString(10, UsuariCore.findUsuariByID(conn, idUsuari).getDepartament());			
		pstm.executeUpdate();		
		//Registrar comentari 1
		nouHistoric(conn, Integer.toString(idNovaTasca), comentari, idUsuariComentari, ipRemota, tipusHistorial);
		try {
			Fitxers.guardarFitxer(conn, adjunts, idIncidencia, idActuacio, "Tasca", Integer.toString(idNovaTasca), "", "", "", idUsuariComentari);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return idNovaTasca;
	}
	
	public static void novaTasca(Connection conn, String tipus, String idUsuaris, int idUsuariComentari, String idActuacio, String idIncidencia, String comentari, String assumpte, String idInformePrevi, String ipRemota, String tipusHistorial) throws SQLException, NamingException {
		String sql = "INSERT INTO public.tbl_tasques(idtasca, idusuari, idactuacio, descripcio, tipus, activa, idincidencia, idinforme, llegida, departament)"
					+" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";		 
		PreparedStatement pstm = null;
		String usuarisList[] = idUsuaris.split("#");
		String idNovesTasques = "";
		for(int i=1; i<usuarisList.length; i++) { 
			pstm = conn.prepareStatement(sql);	 
			int idNovaTasca = idNovaTasca(conn);
			
			if ("solInfPrev".equals(tipus)) {
				assumpte = "Sol·licitud informe previ";
			} else if("resPartida".equals(tipus)) {	
				assumpte = "Sol·licitud reserva partida";
				comentari = "Sol·licitud reserva partida";
				tipusHistorial = "automatic";
			} else if("liciMenor".equals(tipus)) {
				InformeActuacio informePrevi = InformeCore.getInformesActuacio(conn, idActuacio).get(0);
				assumpte = "Proposta d'adjudicació";
				comentari ="1. Recerca de pressupostos";
				tipusHistorial = "automatic";
				if (informePrevi.getPropostaInformeSeleccionada().isLlicencia()) comentari += "<br> 2. Sol·licitud de llicència o autorització urbanística corresponent";
			} else if("liciMajor".equals(tipus)) {
				assumpte = "Licitació contracte major";
				comentari = "Iniciar procés licitació contracte major";		
				tipusHistorial = "automatic";
			}
			pstm.setInt(1, idNovaTasca);
			pstm.setInt(2, Integer.parseInt(usuarisList[i]));
			pstm.setString(3, idActuacio);
			pstm.setString(4, assumpte);
			pstm.setString(5, tipus);
			pstm.setBoolean(6, true);
			pstm.setString(7, idIncidencia);
			pstm.setString(8, idInformePrevi);
			pstm.setBoolean(9, false);
			pstm.setString(10, UsuariCore.findUsuariByID(conn, Integer.parseInt(usuarisList[i])).getDepartament());			
			pstm.executeUpdate();
			idNovesTasques += idNovaTasca + ",";
		}
		
		
		//Registrar comentari 1
		nouHistoric(conn, idNovesTasques, comentari, idUsuariComentari, ipRemota, tipusHistorial);
	}
	
	public static void actualitzarInforme(Connection conn, int idTasca, String idInforme) throws SQLException {
		String sql = "UPDATE public.tbl_tasques"
				+ " SET idinforme = ?"
				+ " WHERE idtasca=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, idInforme);
		pstm.setInt(2, idTasca);
		pstm.executeUpdate();
	}
	
	public static String nouHistoric(Connection conn, String idTasca, String comentari, int idUsuari, String ipRemota, String tipus) throws SQLException {
		String sql = "INSERT INTO public.tbl_historial(idhistoric, idtasca, comentari, idusuari, data, ipremota, tipus)"
					+ " VALUES (?, ?, ?, ?,localtimestamp, ?, ?);";		 
		PreparedStatement pstm = null; 
		pstm = conn.prepareStatement(sql);	
		int idNouHistoric = idNouHistoric(conn);
		pstm.setInt(1, idNouHistoric);
		pstm.setString(2, idTasca);
		pstm.setString(3, comentari);
		pstm.setInt(4, idUsuari);
		pstm.setString(5, ipRemota);
		pstm.setString(6, tipus);
		pstm.executeUpdate();
		return String.valueOf(idNouHistoric);
	}
	
	public static void reasignar(Connection conn, int idUsuari, int idTasca, String tipus, String assumpte) throws SQLException{
		String sql = "UPDATE public.tbl_tasques"
					+ " SET idusuari=?, departament =?, tipus = ?, descripcio = ?, llegida = false"
					+ " WHERE idtasca=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm = conn.prepareStatement(sql);	 
		pstm.setInt(1, idUsuari);
		pstm.setString(2, UsuariCore.findUsuariByID(conn, idUsuari).getDepartament());
		pstm.setString(3, tipus);
		pstm.setString(4, assumpte);
		pstm.setInt(5, idTasca);
		pstm.executeUpdate();
	}
	
	public static void modificarTipus(Connection conn, int idTasca, String tipus) throws SQLException{
		String sql = "UPDATE public.tbl_tasques"
					+ " SET tipus = ?"
					+ " WHERE idtasca=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm = conn.prepareStatement(sql);	
		pstm.setString(1, tipus);
		pstm.setInt(2, idTasca);
		pstm.executeUpdate();
	}
	
	public static void tancar(Connection conn, int idTasca) throws SQLException {
		String sql = "UPDATE public.tbl_tasques"
					+ " SET activa=false"
					+ " WHERE idtasca=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm = conn.prepareStatement(sql);	 
		pstm.setInt(1, idTasca);
		pstm.executeUpdate();
	}
	
	public static void eliminarTasca(Connection conn, String idTasca) throws SQLException {
		String sql = "DELETE FROM public.tbl_tasques"
				+ " WHERE idtasca=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm = conn.prepareStatement(sql);	 
		pstm.setInt(1, Integer.parseInt(idTasca));
		pstm.executeUpdate();
		
		sql = "DELETE FROM public.tbl_historial"
				+ " WHERE idtasca=?;";
		pstm = conn.prepareStatement(sql);	 
		pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, idTasca);
		pstm.executeUpdate();	
	}
	
	public static List<Fitxer> getDocuments(String idTasca, String idIncidencia, String idActuacio) throws NamingException {		
		List<Fitxer> adjunts = new ArrayList<Fitxer>();
		adjunts = utils.Fitxers.ObtenirFitxers(idIncidencia, idActuacio, "Tasca", idTasca, "");
		return adjunts;
	}
	
	public static int idNovaTasca(Connection conn) throws SQLException{
		int id = 1;
		String sql = "SELECT idtasca"
					+ " FROM public.tbl_tasques"
					+ " ORDER BY idtasca"
					+ " DESC LIMIT 1;";		 
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			String actualCode = rs.getString("idtasca");
			int num = Integer.valueOf(actualCode);
			id = num + 1;
		}
		return id;
	}
	public static int idNouHistoric(Connection conn) throws SQLException{
		int id = 1;
		String sql = "SELECT idhistoric"
					+ " FROM public.tbl_historial"
					+ " ORDER BY idhistoric"
					+ " DESC LIMIT 1;";		 
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			String actualCode = rs.getString("idhistoric");
			int num = Integer.valueOf(actualCode);
			id = num + 1;
		}
		return id;
	}
	
	public static void seguirTasca(Connection conn, int idTasca, int idUsuari) throws SQLException {
		String sql = "INSERT INTO public.tbl_seguiments(idusuari, idtasca)"
				+ " VALUES (?, ?);";		 
		PreparedStatement pstm = null; 
		pstm = conn.prepareStatement(sql);	
		pstm.setInt(1, idUsuari);
		pstm.setInt(2, idTasca);
		pstm.executeUpdate();
	}
	
	public static void desSeguirTasca(Connection conn, int idTasca, int idUsuari) throws SQLException {
		String sql = "DELETE FROM public.tbl_seguiments"
					+ " WHERE idtasca = ? AND idusuari = ?";		 
		PreparedStatement pstm = null; 
		pstm = conn.prepareStatement(sql);	
		pstm.setInt(1, idTasca);
		pstm.setInt(2, idUsuari);
		pstm.executeUpdate();
	}
	
	private static boolean isSeguiment(Connection conn, int idTasca, int idUsuari) throws SQLException{
		boolean seguint = false;
		String sql = "SELECT idusuari, idtasca"
					+ " FROM public.tbl_seguiments"
					+ " WHERE idtasca = ? AND idusuari = ?";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setInt(1, idTasca);
		pstm.setInt(2, idUsuari);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) seguint = true;
		return seguint;
	}
	
	public static boolean haveNewTasca(Connection conn, int idUsuari) throws SQLException{
		boolean newTasca = false;
		String sql = "SELECT idtasca"
					+ " FROM public.tbl_tasques" 
					+ " WHERE idusuari = ? AND activa=true AND llegida = false AND tipus != 'notificacio';";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setInt(1, idUsuari);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			//newTasca = true;		
		}
		return newTasca;
	}
	
	public static List<Fitxer> getTascaDocuments(int idTasca) throws NamingException {
		List<Fitxer> adjunts = new ArrayList<Fitxer>();
		adjunts = utils.Fitxers.ObtenirFitxersTasca(String.valueOf(idTasca));
		return adjunts;
	}
}
