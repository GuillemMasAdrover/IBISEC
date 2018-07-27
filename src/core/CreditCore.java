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

import bean.AssignacioCredit;
import bean.Credit;
import bean.InformeActuacio;
import bean.Partida;

public class CreditCore {
	public static void nouCredit(Connection conn, Credit credit) throws SQLException {
		String sql = "INSERT INTO public.tbl_credit(codi, presupost)"
					+ " VALUES (?,?)";
	 
		PreparedStatement pstm = conn.prepareStatement(sql);
	 
		pstm.setString(1, credit.getCodi());		
		pstm.setDouble(2, credit.getPresupost());
	 
		pstm.executeUpdate();
	}
	
	public static List<Credit> llistaCredits(Connection conn) throws SQLException {
		String sql = "SELECT codi, presupost"
					+ " FROM public.tbl_credit";
		 
		PreparedStatement pstm = conn.prepareStatement(sql);
	 
		ResultSet rs = pstm.executeQuery();
		List<Credit> list = new ArrayList<Credit>();
		while (rs.next()) {
			Credit credit = new Credit();
			credit.setCodi(rs.getString("codi"));
			credit.setPresupost(rs.getDouble("presupost"));
			list.add(credit);
		}
		return list;
	}
	
	public static Credit findCredit(Connection conn, String codi) throws SQLException {
		 
		String sql = "SELECT presupost"
					+ " FROM public.tbl_credit"
					+ " WHERE codi = ?";
	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, codi);		
		ResultSet rs = pstm.executeQuery();
	 
		if (rs.next()) {
			Credit credit = new Credit();
			credit.setCodi(codi);
			credit.setPresupost(rs.getDouble("presupost"));			
			return credit;
		}
		return null;
	}
	
	public static String getNewCode(Connection conn) throws SQLException {
		String newCode = "R.IBIS-001";
		
		String sql = "SELECT codi"
					+ " FROM public.tbl_partides"
					+ " ORDER BY codi DESC LIMIT 1;";		 
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			String actualCode = rs.getString("codi");
			int num = Integer.valueOf(actualCode.split("-")[1]);
			String formatted = String.format("%03d", num + 1);
			newCode = newCode.split("-")[0] + "-" + formatted;
		}
		return newCode;
	}
	
	public static void novaPartida(Connection conn, Partida partida, int idUsuari) throws SQLException {
		String sql = "INSERT INTO public.tbl_partides(codi, nom, import, tipus, activa, usucre, datacre)"
					+ " VALUES (?, ?, ?, ?, true,?,localtimestamp);";
	 
		PreparedStatement pstm = conn.prepareStatement(sql);
	 
		pstm.setString(1, partida.getCodi());
		pstm.setString(2, partida.getNom());
		pstm.setDouble(3, Double.valueOf(partida.getTotalPartida()));
		pstm.setString(4, partida.getTipus());
		pstm.setInt(5, idUsuari);
		pstm.executeUpdate();
	}
	
	public static void updatePartida(Connection conn, Partida partida) throws SQLException {
		String sql = "UPDATE public.tbl_partides SET nom = ?, import = ?, tipus = ?, datacre=localtimestamp"
				+ " WHERE codi = ?"; 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, partida.getNom());
		pstm.setDouble(2, Double.valueOf(partida.getTotalPartida()));
		pstm.setString(3, partida.getTipus());
		pstm.setString(4, partida.getCodi());
		pstm.executeUpdate();
	}
	

	public static void obrirPartida(Connection conn, String idPartida) throws SQLException {
		String sql = "UPDATE public.tbl_partides SET activa = true, datacre=localtimestamp"
				+ " WHERE codi = ?"; 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, idPartida);
		pstm.executeUpdate();
	}
	
	public static void tancarPartida(Connection conn, String idPartida) throws SQLException {
		String sql = "UPDATE public.tbl_partides SET activa = false, datacre=localtimestamp"
				+ " WHERE codi = ?"; 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, idPartida);
		pstm.executeUpdate();
	}
	
	public static Partida getPartida(Connection conn, String codi) throws SQLException {
		 
		String sql = "SELECT codi, nom, import, tipus, activa, usucre, datacre"
					+ " FROM public.tbl_partides"
					+ " WHERE codi = ?";
	 	
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, codi);		
		ResultSet rs = pstm.executeQuery();
		double totalPagat = 0;
		if (rs.next()) {
			Partida partida = new Partida();
			partida.setCodi(rs.getString("codi"));			
			partida.setEstat(rs.getBoolean("activa"));
			partida.setNom(rs.getString("nom"));
			partida.setTipus(rs.getString("tipus"));
			partida.setTotalPartida(rs.getDouble("import"));
			totalPagat = getTotalPagat(conn, rs.getString("codi"));
			partida.setPrevistPartida(getTotalContractat(conn, rs.getString("codi")) - totalPagat);
			partida.setReservaPartida(getTotalReservat(conn, rs.getString("codi")));
			partida.setPagatPartida(totalPagat);
			return partida;
		}
		return null;
	}
	
	public static List<Partida> getPartides(Connection conn, boolean tancades) throws SQLException {
		String sql = "SELECT *"
					+ " FROM public.tbl_partides";
		if (!tancades) sql += " WHERE activa=true";
		sql += " ORDER BY 1 desc"; 
		PreparedStatement pstm = conn.prepareStatement(sql);
	 
		ResultSet rs = pstm.executeQuery();
		List<Partida> list = new ArrayList<Partida>();
		double totalPagat = 0;
		while (rs.next()) {
			Partida partida = new Partida();
			partida.setCodi(rs.getString("codi"));			
			partida.setEstat(rs.getBoolean("activa"));
			partida.setNom(rs.getString("nom"));
			partida.setTipus(rs.getString("tipus"));
			totalPagat = getTotalPagat(conn, rs.getString("codi"));
			partida.setTotalPartida(rs.getDouble("import"));
			partida.setPrevistPartida(getTotalContractat(conn, rs.getString("codi")) - totalPagat);
			partida.setReservaPartida(getTotalReservat(conn, rs.getString("codi")));
			partida.setPagatPartida(totalPagat);
			list.add(partida);
		}
		return list;
	}
	
	public static AssignacioCredit getPartidaInforme(Connection conn, String idInforme) throws SQLException {
		AssignacioCredit assignacio = new AssignacioCredit();;
		String sql = "SELECT idassignacio, idactuacio, idinf, reserva, usureserva, datareserva, assignacio, usuassignacio, dataassignacio, comentari, usucre, datacre, idpartida, valorpa, valorpd, bei, feder"
					+ " FROM public.tbl_assignacionscredit"
					+ " WHERE idinf = ?";
		PreparedStatement pstm = conn.prepareStatement(sql);		 
		pstm.setString(1, idInforme);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			assignacio.setIdAssignacio(rs.getString("idassignacio"));
			assignacio.setIdInforme(idInforme);
			assignacio.setReserva(rs.getBoolean("reserva"));
			assignacio.setUsuReserva(UsuariCore.findUsuariByID(conn, rs.getInt("usureserva")));
			assignacio.setDatareserva(rs.getTimestamp("datareserva"));
			assignacio.setAssignacio(rs.getBoolean("assignacio"));
			assignacio.setUsuAssignacio(UsuariCore.findUsuariByID(conn, rs.getInt("usuassignacio")));
			assignacio.setDataAssignacio(rs.getTimestamp("dataassignacio"));
			assignacio.setComentari(rs.getString("comentari"));
			assignacio.setUsuCre(UsuariCore.findUsuariByID(conn, rs.getInt("usucre")));
			assignacio.setDataCre(rs.getTimestamp("datacre"));
			assignacio.setPartida(CreditCore.getPartida(conn, rs.getString("idpartida")));
			assignacio.setValorPA(rs.getDouble("valorpa"));
			assignacio.setValorPD(rs.getDouble("valorpd"));
			assignacio.setBei(rs.getBoolean("bei"));
			assignacio.setFeder(rs.getBoolean("feder"));
		}
		return assignacio;
	}
	
	public static String getCodiPartidaInforme(Connection conn, String idInforme) throws SQLException {
		String idPartida = "";
		String sql = "SELECT p.codi AS codi"
					+ " FROM public.tbl_assignacionscredit a LEFT JOIN public.tbl_partides p ON a.idpartida=p.codi"
					+ " WHERE idinf = ?";
		PreparedStatement pstm = conn.prepareStatement(sql);		 
		pstm.setString(1, idInforme);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			if (rs.getString("codi") != null) idPartida = rs.getString("codi");
		}
		return idPartida;
	}
	
	public static String getComentariPartida(Connection conn, String idInforme) throws SQLException {
		String comentari = "";
		String sql = "SELECT comentari"
					+ " FROM public.tbl_assignacionscredit"
					+ " WHERE idinf = ?";
		PreparedStatement pstm = conn.prepareStatement(sql);		 
		pstm.setString(1, idInforme);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) comentari = rs.getString("comentari");		
		return comentari;
	}
	
	public static String getPartidaActuacio(Connection conn, String idActuacio) throws SQLException {
		String idPartida = "";
		String sql = "SELECT idpartida"
					+ " FROM public.tbl_assignacionscredit"
					+ " WHERE idactuacio = ?"		
					+ " ORDER BY 1 desc LIMIT 1"; 
		PreparedStatement pstm = conn.prepareStatement(sql);		 
		pstm.setString(1, idActuacio);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			idPartida = rs.getString("idpartida");
		}
		return idPartida;
	}
	
	public static String newAssignacioCode(Connection conn) throws SQLException{
		String newCode = "1";
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String yearInString = String.valueOf(year);
		
		String sql = "SELECT idassignacio"
					+ " FROM public.tbl_assignacionscredit"
					+ " WHERE idassignacio like '%" + yearInString + "-INF%'"
					+ " ORDER BY idassignacio DESC, datacre DESC LIMIT 1;";	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();		
		String prefix = "INF";
		if (rs.next()) { //Codis nous
			String actualCode = rs.getString("idassignacio");			
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

	public static List<AssignacioCredit> findAssignacionsPartida(Connection conn, String codi, String estat) throws SQLException, NamingException {
		List<AssignacioCredit> assignacionsList = new ArrayList<AssignacioCredit>();
		String sql = "SELECT idassignacio, idactuacio, idinf, reserva, usureserva, datareserva, assignacio, usuassignacio,"
					+ " dataassignacio, comentari, usucre, datacre, idpartida, valorpa, valorpd, bei, feder"
					+ " FROM public.tbl_assignacionscredit"
					+ " WHERE idpartida=?";		 
		if (estat != null && estat.equals("reservat")) {
			sql += " AND reserva=true AND assignacio=false";
		}else if(estat != null && estat.equals("contractat")) {
			sql += " AND assignacio=true";
		}else if(estat != null && estat.equals("pagat")) {
			sql += " AND assignacio=true";
		}
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, codi);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			InformeActuacio informe = InformeCore.getInformePrevi(conn, rs.getString("idinf"), false);
			AssignacioCredit assignacio = new AssignacioCredit();
			assignacio.setIdAssignacio(rs.getString("idassignacio"));
			assignacio.setIdInforme(rs.getString("idinf"));
			assignacio.setInforme(informe);
			assignacio.setReserva(rs.getBoolean("reserva"));
			assignacio.setUsuReserva(UsuariCore.findUsuariByID(conn, rs.getInt("usureserva")));
			assignacio.setDatareserva(rs.getTimestamp("datareserva"));
			assignacio.setAssignacio(rs.getBoolean("assignacio"));
			assignacio.setUsuAssignacio(UsuariCore.findUsuariByID(conn, rs.getInt("usuassignacio")));
			assignacio.setDataAssignacio(rs.getTimestamp("dataassignacio"));
			assignacio.setComentari(rs.getString("comentari"));
			assignacio.setUsuCre(UsuariCore.findUsuariByID(conn, rs.getInt("usucre")));
			assignacio.setDataCre(rs.getTimestamp("datacre"));
			assignacio.setPartida(CreditCore.getPartida(conn, rs.getString("idpartida")));
			assignacio.setValorPA(rs.getDouble("valorpa"));
			assignacio.setValorPD(rs.getDouble("valorpd"));
			assignacio.setBei(rs.getBoolean("bei"));
			assignacio.setFeder(rs.getBoolean("feder"));
			if(estat != null && estat.equals("pagat") && informe.getTotalFacturat() > 0) {
				assignacionsList.add(assignacio);
			} else if(estat != null && estat.equals("contractat") && informe.getTotalFacturat() >= 0 && informe.getTotalFacturat() < rs.getDouble("valorpd")) {
				assignacionsList.add(assignacio);
			} else if(estat == null || (!estat.equals("contractat") && !estat.equals("pagat"))) {
				assignacionsList.add(assignacio);
			}
		}
		return assignacionsList;
	}
	
	public static List<AssignacioCredit> findAssignacions(Connection conn, String idPartida, String idCentre, Date dataIni, Date dataFi, boolean bei, boolean feder) throws SQLException, NamingException {
		List<AssignacioCredit> assignacionsList = new ArrayList<AssignacioCredit>();
		String sql = "SELECT idassignacio, idactuacio, idinf, reserva, usureserva, datareserva, assignacio, usuassignacio,"
					+ " dataassignacio, comentari, ass.usucre AS usucre, ass.datacre AS datacre, idpartida, valorpa, valorpd, bei, feder"
					+ " FROM public.tbl_assignacionscredit ass LEFT JOIN public.tbl_actuacio ac ON ass.idactuacio = ac.id";	 
		PreparedStatement pstm = null;
		boolean primeraCondicio = true;
		if (dataIni != null) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE ass.dataassignacio >= ?";
			}			
		}
		if (dataFi != null) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE ass.dataassignacio <= ?";
			} else {
				sql += " AND ass.dataassignacio <= ?";
			}			
		}
		if (!idPartida.isEmpty()) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE idpartida = ?";
			} else {
				sql += " AND idpartida = ?";
			}			
		}
		if (idCentre != null && !idCentre.isEmpty() && ! "-1".equals(idCentre)) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE idcentre = ?";
			} else {
				sql += " AND idcentre = ?";
			}			
		}
		if (bei) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE bei = " + bei;
			} else {
				sql += " AND bei = " + bei;
			}
		}
		if (feder) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE feder = " + feder;
			} else {
				sql += " AND feder = " + feder;
			}
		}
		
		pstm = conn.prepareStatement(sql);
		
		int contVars = 1;
		if (dataIni != null) {
			pstm.setDate(contVars, new java.sql.Date(dataIni.getTime()));
			contVars += 1;
		}
		if (dataFi != null) {
			pstm.setDate(contVars, new java.sql.Date(dataFi.getTime()));
			contVars += 1;
		}
		if (!idPartida.isEmpty()) {
			pstm.setString(contVars, idPartida);
			contVars += 1;
		}
		if (idCentre != null && !idCentre.isEmpty() && ! "-1".equals(idCentre)) {
			pstm.setString(contVars, idCentre);
			contVars += 1;
		}			
		ResultSet rs = pstm.executeQuery();
		AssignacioCredit assignacio = new AssignacioCredit();
		while (rs.next()) {			
			assignacio = new AssignacioCredit();
			assignacio.setIdAssignacio(rs.getString("idassignacio"));
			assignacio.setIdInforme(rs.getString("idinf"));
			assignacio.setInforme(InformeCore.getInformePrevi(conn, rs.getString("idinf"), false));
			assignacio.setReserva(rs.getBoolean("reserva"));
			assignacio.setUsuReserva(UsuariCore.findUsuariByID(conn, rs.getInt("usureserva")));
			assignacio.setDatareserva(rs.getTimestamp("datareserva"));
			assignacio.setAssignacio(rs.getBoolean("assignacio"));
			assignacio.setUsuAssignacio(UsuariCore.findUsuariByID(conn, rs.getInt("usuassignacio")));
			assignacio.setDataAssignacio(rs.getTimestamp("dataassignacio"));
			assignacio.setComentari(rs.getString("comentari"));
			assignacio.setUsuCre(UsuariCore.findUsuariByID(conn, rs.getInt("usucre")));
			assignacio.setDataCre(rs.getTimestamp("datacre"));
			assignacio.setPartida(CreditCore.getPartida(conn, rs.getString("idpartida")));
			assignacio.setValorPA(rs.getDouble("valorpa"));
			assignacio.setValorPD(rs.getDouble("valorpd"));
			assignacio.setBei(rs.getBoolean("bei"));
			assignacio.setFeder(rs.getBoolean("feder"));
			assignacionsList.add(assignacio);			
		}
		return assignacionsList;
	}
	
	public static void reservar(Connection conn, String idPartida, String idActuacio, String idInforme, double valor, String comentari, int idUsuari) throws SQLException {
		String sql = "";
		PreparedStatement pstm = null;
		if (idInforme.contains("-MOD-")) {
			sql = "INSERT INTO public.tbl_assignacionscredit(idassignacio, idactuacio, idinf, reserva, usureserva, datareserva, assignacio, comentari, usucre, datacre, idpartida, valorpa)"
					+ " VALUES (?, ?, ?, true, ?, localtimestamp, false, ?, ?, localtimestamp, ?, ?);";
		
			pstm = conn.prepareStatement(sql);
			 
			pstm.setString(1, newAssignacioCode(conn));		
			pstm.setString(2, idActuacio);
			pstm.setString(3, idInforme);
			pstm.setInt(4, idUsuari);		
			pstm.setString(5, comentari);
			pstm.setInt(6, idUsuari);
			pstm.setString(7, idPartida);
			pstm.setDouble(8, valor);
			pstm.executeUpdate();
		} else {
			AssignacioCredit partida = getPartidaInforme(conn, idInforme);
			if (partida.getIdAssignacio() == null || partida.getIdAssignacio().isEmpty()) {	
				sql = "INSERT INTO public.tbl_assignacionscredit(idassignacio, idactuacio, idinf, reserva, usureserva, datareserva, assignacio, comentari, usucre, datacre, idpartida, valorpa)"
							+ " VALUES (?, ?, ?, true, ?, localtimestamp, false, ?, ?, localtimestamp, ?, ?);";
				
				pstm = conn.prepareStatement(sql);
				 
				pstm.setString(1, newAssignacioCode(conn));		
				pstm.setString(2, idActuacio);
				pstm.setString(3, idInforme);
				pstm.setInt(4, idUsuari);		
				pstm.setString(5, comentari);
				pstm.setInt(6, idUsuari);
				pstm.setString(7, idPartida);
				pstm.setDouble(8, valor);
				pstm.executeUpdate();
			} else {	
				sql = "UPDATE public.tbl_assignacionscredit"
					+ " SET datareserva=localtimestamp, comentari=?, idpartida=?, valorpa=?"
					+ " WHERE idinf = ?;";
				pstm = conn.prepareStatement(sql);		
				pstm.setString(1, comentari);
				pstm.setString(2, idPartida);
				pstm.setDouble(3, valor);
				pstm.setString(4, idInforme);
				pstm.executeUpdate();
			}
			sql = "UPDATE public.tbl_informeactuacio"
					+ " SET datapartidarebujada = null, motiupartidarebujada = '', usupartidarebujada = null"
					+ " WHERE idinf=?";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, idInforme);	
			pstm.executeUpdate();
		}
	}
	
	public static void modificarAssignacio(Connection conn, AssignacioCredit assignacio) throws SQLException {
		String sql = "UPDATE public.tbl_assignacionscredit"
				+ " SET bei=?, feder=?"
				+ " WHERE idinf = ?;";
	
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setBoolean(1, assignacio.isBei());
		pstm.setBoolean(2, assignacio.isFeder());
		pstm.setString(3, assignacio.getIdInforme());
		pstm.executeUpdate();
	}
	
	public static void assignar(Connection conn, String idInforme, double valor) throws SQLException {
		String sql = "UPDATE public.tbl_assignacionscredit"
					+ " SET valorpd=?, assignacio=?, dataassignacio=localtimestamp"
					+ " WHERE idinf = ?;";
		
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setDouble(1, valor);
		pstm.setBoolean(2, true);
		pstm.setString(3, idInforme);
		pstm.executeUpdate();
	}
	
	public static void anularReserva(Connection conn, String idInforme) throws SQLException {
		String sql = "DELETE FROM public.tbl_assignacionscredit"
					+ " WHERE idinf = ?;";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, idInforme);
		pstm.executeUpdate();
	}
	
	public static double getTotalReservat(Connection conn, String idPartida) throws SQLException{
		double totalReservat = 0;
		String sql = "SELECT SUM(valorpa) AS total"
					+ " FROM public.tbl_assignacionscredit"
					+ " WHERE reserva=true and assignacio=false and idpartida = ?";
		PreparedStatement pstm = conn.prepareStatement(sql);		 
		pstm.setString(1, idPartida);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			totalReservat = rs.getDouble("total");
		}
		return totalReservat;
	}
	
	public static double getTotalContractat(Connection conn, String idPartida) throws SQLException{
		double totalGastat = 0;
		String sql = "SELECT SUM(valorpd) AS total"
					+ " FROM public.tbl_assignacionscredit"
					+ " WHERE assignacio=true AND idpartida = ?";
		PreparedStatement pstm = conn.prepareStatement(sql);		 
		pstm.setString(1, idPartida);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			totalGastat = rs.getDouble("total");
		}
		return totalGastat;
	}
	
	public static double getTotalPagat(Connection conn, String idPartida) throws SQLException{
		double totalGastat = 0;
		String sql = "SELECT SUM(f.import) AS total"
				  	+ " FROM public.tbl_factures f LEFT JOIN public.tbl_assignacionscredit a ON f.idinforme = a.idinf"
				  	+ " WHERE f.anulada = false AND a.idpartida = ?"
				  	+ " UNION SELECT SUM(valorpa) AS total"
				  	+ " FROM public.tbl_assignacionscredit"
				  	+ " WHERE idpartida = ? AND idinf LIKE '%-LLI-%'";
		PreparedStatement pstm = conn.prepareStatement(sql);		 
		pstm.setString(1, idPartida);
		pstm.setString(2, idPartida);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			totalGastat += rs.getDouble("total");
		}
		return totalGastat;
	}
}
