package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import bean.Registre;
import utils.Fitxers;
import utils.Fitxers.Fitxer;

public class RegistreCore {
	
	private static Registre initRegistre(Connection conn, ResultSet rs, String tipus) throws SQLException {
		Registre registre = new Registre();
		registre.setId(rs.getString("id"));
		registre.setData(rs.getDate("data"));
		if ("S".equals(tipus)) {
			registre.setRemDes(rs.getString("destinatari"));
		}else{
			registre.setRemDes(rs.getString("remitent"));
		}		
		registre.setTipus(rs.getString("tipus"));
		registre.setContingut(rs.getString("contingut"));
		registre.setIdIncidencies(rs.getString("idincidencia"));
		registre.setIdActuacions(findActuacions(conn, rs.getString("idincidencia")));
		registre.setIdCentres(rs.getString("idcentre"));
		String centresList = rs.getString("idcentre");
		if (centresList != null && !centresList.isEmpty()) {
			String[] idCentresList = centresList.split("#");
			String nomCentres = "";
			for(int i=0; i<idCentresList.length; i++) { 	
				nomCentres += CentreCore.nomCentre(conn, idCentresList[i]) + "#";
			}		
			registre.setNomCentres(nomCentres);	
		}
		registre.setActiu(rs.getBoolean("actiu"));
		return registre;
	}
	
	public static void nouRegistre(Connection conn, String tipus, Registre registre) throws SQLException {
		String sql = "INSERT INTO public.tbl_regentrada (id, data, tipus, remitent, contingut, idcentre, idincidencia, usucre, datacre, actiu, idinforme)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, localtimestamp, true, ?);";
		if (tipus == "S") {
			sql = "INSERT INTO public.tbl_regsortida (id, data, tipus, destinatari, contingut, idcentre, idincidencia, usucre, datacre, actiu, idinforme)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, localtimestamp, true, ?);";
		}	
		
		PreparedStatement pstm = conn.prepareStatement(sql);
	 
		pstm.setString(1, getNewCode(conn, tipus));		
		pstm.setDate(2, new java.sql.Date(registre.getData().getTime()));
		pstm.setString(3, registre.getTipus());
		pstm.setString(4, registre.getRemDes());
		pstm.setString(5, registre.getContingut());
		pstm.setString(6,  registre.getIdCentres());
		pstm.setString(7, registre.getIdIncidencies());
		pstm.setInt(8, registre.getIdUsuari());			
		pstm.setString(9, registre.getIdInforme());		
		pstm.executeUpdate();
	}
	
	public static void modificarRegistre(Connection conn, Registre registre, String tipus) throws SQLException {
		String sql = "UPDATE public.tbl_regentrada"
				+ " SET  data = ?, tipus = ?, remitent = ?, contingut = ?, idcentre = ?, idincidencia = ?"
				+ " WHERE id=?";
		if ("S".equals(tipus)) {
			sql = "UPDATE public.tbl_regsortida"
					+ " SET data = ?, tipus = ?, destinatari = ?, contingut = ?, idcentre = ?, idincidencia = ?"
					+ " WHERE id=?";
		}		
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setDate(1, new java.sql.Date(registre.getData().getTime()));
		pstm.setString(2, registre.getTipus());
		pstm.setString(3, registre.getRemDes());
		pstm.setString(4, registre.getContingut());
		pstm.setString(5,  registre.getIdCentres());
		pstm.setString(6, registre.getIdIncidencies());
		pstm.setString(7, registre.getId());				
		pstm.executeUpdate();
	}
	
	public static void anularRegistre(Connection conn, String idRegistre, String tipus) throws SQLException {
		String sql = "UPDATE public.tbl_regentrada"
					+ " SET actiu = false"
					+ " WHERE id=?";
		if ("S".equals(tipus)) {
			sql = "UPDATE public.tbl_regsortida"
					+ " SET actiu = false"
					+ " WHERE id=?";
		}		
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, idRegistre);
		pstm.executeUpdate();
	}
	
	public static Registre findRegistre(Connection conn, String tipus, String idRegistre) throws SQLException {
		Registre registre = new Registre();
		String sql = "SELECT id, data, tipus, remitent, contingut, idcentre, idincidencia, usucre, datacre, actiu"
					+ " FROM public.tbl_regentrada"
					+ " WHERE id=?";
		if ("S".equals(tipus)) {
			sql = "SELECT id, data, tipus, destinatari, contingut, idcentre, idincidencia, usucre, datacre, actiu"
					+ " FROM public.tbl_regsortida"
					+ " WHERE id=?";
		}
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, idRegistre);	
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {		
			registre = initRegistre(conn, rs, tipus);			
		}
		return registre;
	}
	
	public static List<Registre> entrades(Connection conn) throws SQLException {
		String sql = "SELECT id, data, tipus, remitent, contingut, idcentre, idincidencia, actiu"
					+ " FROM public.tbl_regentrada"
					+ " WHERE actiu = true"
					+ " ORDER BY data DESC, id DESC LIMIT 500";
	 
		PreparedStatement pstm = conn.prepareStatement(sql);
	 
		ResultSet rs = pstm.executeQuery();
		List<Registre> list = new ArrayList<Registre>();
		System.out.println(pstm.toString());
		while (rs.next()) {			
			list.add(initRegistre(conn, rs, "E"));
		}
		return list;
	}
	
	public static List<Registre> searchEntrades(Connection conn, String idCentre, Date dataIni, Date dataFi) throws SQLException {
		String sql = "SELECT id, data, tipus, remitent, contingut, idcentre, idincidencia, actiu"
					+ " FROM public.tbl_regentrada";
		ResultSet rs = null;
		if (dataIni != null && dataFi != null) {
			sql += " WHERE data >= ? and data <= ?";
			if (idCentre != "") {
				sql += "and idcentre like ?";
			}
			sql += " ORDER BY data DESC, id DESC";
			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.setDate(1, new java.sql.Date(dataIni.getTime()));
			pstm.setDate(2, new java.sql.Date(dataFi.getTime()));
			if (idCentre != "") pstm.setString(3, "%" + idCentre + "%");
			rs = pstm.executeQuery();
			System.out.println(pstm.toString());
		} else {
			if (idCentre != "") {
				sql += " WHERE idcentre like ?";
			}
			sql += " ORDER BY data DESC, id DESC";
			PreparedStatement pstm = conn.prepareStatement(sql);
			if (idCentre != "") pstm.setString(1, "%" + idCentre + "%");
			rs = pstm.executeQuery();
			System.out.println(pstm.toString());
		}
		List<Registre> list = new ArrayList<Registre>();
		
		while (rs.next()) {
			list.add(initRegistre(conn, rs, "E"));
		}
		return list;
	}
	
	public static List<Registre> searchEntradesIncidencia(Connection conn, String referencia) throws SQLException {
		String sql = "SELECT id, data, tipus, remitent, contingut, idcentre, idincidencia, actiu"
					+ " FROM public.tbl_regentrada"
					+ " WHERE idincidencia like ?"		
					+ " ORDER BY data DESC, id DESC";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, "%" + referencia + "%");
		ResultSet rs = pstm.executeQuery();
		List<Registre> list = new ArrayList<Registre>();
		while (rs.next()) {			
			list.add(initRegistre(conn, rs, "E"));
		}
		return list;
	}
	
	public static void actualitzarIdIncidencia(Connection conn, String tipus, String idRegistre, String idActuacio) throws SQLException{
		String sql = "UPDATE public.tbl_regentrada"
					+ " SET idincidencia=?"
					+ " WHERE id=?";
		if (tipus == "S") {
			sql = "UPDATE public.tbl_regsortida"
					+ " SET idincidencia=?"
					+ " WHERE id=?";
		}		
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, idActuacio);
		pstm.setString(2, idRegistre);				
		pstm.executeUpdate();
	}
	
	public static List<Registre> sortides(Connection conn) throws SQLException {
		String sql = "SELECT id, data, tipus, destinatari, contingut, idincidencia, idcentre, actiu"
					+ " FROM public.tbl_regsortida"
					+ " WHERE actiu = true"
					+ " ORDER BY data DESC, id DESC LIMIT 500";
	 
		PreparedStatement pstm = conn.prepareStatement(sql);
	 
		ResultSet rs = pstm.executeQuery();
		List<Registre> list = new ArrayList<Registre>();
		while (rs.next()) {			
			list.add(initRegistre(conn, rs, "S"));
		}
		return list;
	}
	
	public static List<Registre> searchSortides(Connection conn, String idCentre, Date dataIni, Date dataFi) throws SQLException {
		String sql = "SELECT id, data, tipus, destinatari, contingut, idcentre, idincidencia, actiu"
					+ " FROM public.tbl_regsortida";
					ResultSet rs = null;
		if (dataIni != null && dataFi != null) {
			sql += " WHERE data >= ? and data <= ?";
			if (idCentre != "") {
				sql += "and idcentre like ?";
			}
			sql += " ORDER BY data DESC, id DESC";
			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.setDate(1, new java.sql.Date(dataIni.getTime()));
			pstm.setDate(2, new java.sql.Date(dataFi.getTime()));
			if (idCentre != "") pstm.setString(3, "%" + idCentre + "%");
			rs = pstm.executeQuery();
		} else {
			if (idCentre != "") {
				sql += " WHERE idcentre like ?";
			}
			sql += " ORDER BY data DESC, id DESC";
			PreparedStatement pstm = conn.prepareStatement(sql);
			if (idCentre != "") pstm.setString(1, "%" + idCentre + "%");
			rs = pstm.executeQuery();
		}
		List<Registre> list = new ArrayList<Registre>();
		while (rs.next()) {			
			list.add(initRegistre(conn, rs, "S"));
		}
		return list;
	}
	
	public static List<Registre> searchSortidesIncidencia(Connection conn, String referencia) throws SQLException {
		String sql = "SELECT id, data, tipus, destinatari, contingut, idcentre, idincidencia, actiu"
					+ " FROM public.tbl_regsortida"
					+ " WHERE idincidencia like ? "		
					+ " ORDER BY data DESC, id DESC";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, "%" + referencia + "%");
		ResultSet rs = pstm.executeQuery();
		List<Registre> list = new ArrayList<Registre>();
		while (rs.next()) {			
			list.add(initRegistre(conn, rs, "S"));
		}
		return list;
	}
	
	public static String getNewCode(Connection conn, String tipus) throws SQLException {
		String code = "1";
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String yearInString = String.valueOf(year);
		
		String sql = "SELECT id"
					+ " FROM public.tbl_regentrada"
					+ " WHERE id like '%" + yearInString + "-RE%'"
					+ " ORDER BY id DESC LIMIT 1;";		
		if ("S".equals(tipus)) {
			sql = "SELECT id"
					+ " FROM public.tbl_regsortida"
					+ " WHERE id like '%" + yearInString + "-RS%'"
					+ " ORDER BY id DESC LIMIT 1;";	
		}
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();		
		String prefix = "RE";
		if ("S".equals(tipus)) prefix = "RS";
		if (rs.next()) { //Codis nous
			String actualCode = rs.getString("id");			 
			int num = Integer.valueOf(actualCode.split("-")[2]);
			String numFormatted = String.format("%04d", num + 1);
			code = yearInString + "-" + prefix + "-" + numFormatted;			
		} else {//Codis antics
			int num = 0;		
			String numFormatted = String.format("%04d", num + 1);
			code = yearInString + "-" + prefix + "-" + numFormatted;
		}
		return code;
	}
	private static String findActuacions(Connection conn, String idIncidencies) throws SQLException {
		String idActuacions = "";
		if (idIncidencies != null && !idIncidencies.isEmpty()) {
			for (String incidencia: idIncidencies.split("#")) {
				String sql = "SELECT id"
						+ " FROM public.tbl_actuacio"
						+ " WHERE idincidencia = ?";	
				PreparedStatement pstm = conn.prepareStatement(sql);
				pstm.setString(1, incidencia);
				ResultSet rs = pstm.executeQuery();
				while (rs.next()) {
					idActuacions += rs.getString("id") + "#";
				}
			}
		}
		return idActuacions;
	}
	
	public static List<Fitxer> getArxiusAdjunts(Connection conn, String idincidencia, String idInforme, String tipus) throws SQLException, NamingException {
		List<Fitxer> documentsAdjunts = new ArrayList<Fitxer>();
		String sql = "SELECT id"
				+ " FROM public.tbl_regentrada"
				+ " WHERE tipus = ? AND idinforme LIKE '%" + idInforme + "%'";	
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, tipus);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			documentsAdjunts.addAll(Fitxers.ObtenirFitxers(idincidencia, "", "RegistreE", rs.getString("id"), ""));
		}
		return documentsAdjunts;
	}
	
	public static List<Fitxer> getArxiusAdjuntsPerTipus(Connection conn, String idincidencia, String idInforme, String tipus) throws SQLException, NamingException {
		List<Fitxer> documentsAdjunts = new ArrayList<Fitxer>();
		String sql = "SELECT id"
				+ " FROM public.tbl_regentrada"
				+ " WHERE tipus = ? AND idinforme LIKE '%" + idInforme + "%'";	
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, tipus);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			documentsAdjunts.addAll(Fitxers.ObtenirFitxers(idincidencia, "", "RegistreE", rs.getString("id"), ""));
		}
		return documentsAdjunts;
	}
	
	public static List<Registre> getEntrades(Connection conn, String idInforme) throws SQLException {
		List<Registre> registresList = new ArrayList<Registre>();
		String sql = "SELECT id, data, tipus, remitent, contingut, idcentre, idincidencia, usucre, datacre, actiu"
				+ " FROM public.tbl_regentrada"
				+ " WHERE idinforme LIKE '%" + idInforme + "%'";	
		PreparedStatement pstm = conn.prepareStatement(sql);		
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			registresList.add(initRegistre(conn, rs, "E"));
		}
		return registresList;
	}
	
	public static List<Registre> getSortides(Connection conn, String idInforme) throws SQLException {
		List<Registre> registresList = new ArrayList<Registre>();
		String sql = "SELECT id, data, tipus, destinatari, contingut, idcentre, idincidencia, usucre, datacre, actiu"
				+ " FROM public.tbl_regsortida"
				+ " WHERE idinforme LIKE '%" + idInforme + "%'";	
		PreparedStatement pstm = conn.prepareStatement(sql);		
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			registresList.add(initRegistre(conn, rs, "S"));
		}
		return registresList;
	}
}
