package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.naming.NamingException;

import bean.Bastanteo;
import bean.Bastanteo.Escritura;
import bean.Empresa;

public class BastanteosCore {
	static final String SQL_CAMPS = "ref, databastanteo, empresa, personafacultada, carrec, anybastanteo";
	static final String SQL_CAMPS_ESCRITURA = "refescritura, refbastanteo, escritura, dataescritura, nprotocol, notari";
	
	private static Bastanteo initBastanteo(Connection conn, ResultSet rs) throws SQLException, NamingException{
		Bastanteo bastanteo = new Bastanteo();
		bastanteo.setRef(rs.getString("ref"));
		bastanteo.setDatabastanteo(rs.getTimestamp("databastanteo"));
		Empresa empresa = EmpresaCore.findEmpresa(conn, rs.getString("empresa"));
		if (empresa == null) {
			empresa = new Empresa();
			empresa.setName(rs.getString("empresa"));;
		}
		bastanteo.setEmpresa(empresa);
		bastanteo.setEscrituresList(findEscritures(conn, rs.getString("ref")));
		bastanteo.setPersonaFacultada(rs.getString("personafacultada"));
		bastanteo.setCarrec(rs.getString("carrec"));
		bastanteo.setAnyBastanteo(rs.getString("anybastanteo"));
		
		return bastanteo;
	}
	
	private static Escritura initEscritura(Connection conn, ResultSet rs) throws SQLException, NamingException{
		Escritura escritura = new Bastanteo().new Escritura();
		escritura.setEscritura(rs.getString("escritura"));
		escritura.setDataEscritura(rs.getTimestamp("dataescritura"));
		escritura.setNumProtocol(rs.getString("nprotocol"));
		escritura.setNotari(rs.getString("notari"));		
		escritura.setCodi(rs.getInt("refescritura"));
		return escritura;
	}
	
	public static String nouBastanteo(Connection conn, Bastanteo bastanteo) throws SQLException {
		String newCode = getNewCode(conn);
		String sql = "INSERT INTO public.tbl_bastanteos(" + SQL_CAMPS + ")"
					+ "VALUES (?, ?, ?, ?, ?, ?)";
	 
		PreparedStatement pstm = conn.prepareStatement(sql);
	 
		pstm.setString(1, newCode);
		if (bastanteo.getDatabastanteo() != null) {
			pstm.setDate(2, new java.sql.Date(bastanteo.getDatabastanteo().getTime()));
		} else {
			pstm.setDate(2, null);
		}
		pstm.setString(3, bastanteo.getEmpresa().getCif());
		pstm.setString(4, bastanteo.getPersonaFacultada());
		pstm.setString(5, bastanteo.getCarrec());	
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String yearInString = String.valueOf(year);
		pstm.setString(6, yearInString);	
		pstm.executeUpdate();
		return newCode;
	}
	
	public static void novaEscritura(Connection conn, Escritura escritura, String refBastanteo) throws SQLException {		
		String sql = "INSERT INTO public.tbl_escritura(" + SQL_CAMPS_ESCRITURA + ")"
					+ "VALUES (?, ?, ?, ?, ?, ?)";

		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setInt(1, getNewCodeEscritura(conn));
		pstm.setString(2, refBastanteo);
		pstm.setString(3, escritura.getEscritura());
		if (escritura.getDataEscritura() != null) {
			pstm.setDate(4, new java.sql.Date(escritura.getDataEscritura().getTime()));
		} else {
			pstm.setDate(4, null);
		}
		pstm.setString(5, escritura.getNumProtocol());	
		pstm.setString(6, escritura.getNotari());	
		
		pstm.executeUpdate();
	}
	
	public static void eliminarEscritura(Connection conn, String referencia) throws SQLException {
		String sql = "DELETE FROM public.tbl_escritura"
					+ " WHERE refescritura = ?;";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setInt(1, Integer.valueOf(referencia));
		pstm.executeUpdate();
	}
	
	public static Bastanteo findBastanteo(Connection conn, String referencia) throws SQLException, NamingException {		 
		String sql = "SELECT " + SQL_CAMPS
					+ " FROM public.tbl_bastanteos"
					+ " WHERE ref = ?";
	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, referencia);		
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			Bastanteo bastanteo = initBastanteo(conn, rs); 
			return bastanteo;
		}
		return new Bastanteo();
	}
	
	public static List<Bastanteo> findBastanteosEmpresa(Connection conn, String cif) throws SQLException, NamingException {
		List<Bastanteo> bastanteosList = new ArrayList<Bastanteo>();
		String sql = "SELECT " + SQL_CAMPS
				+ " FROM public.tbl_bastanteos"
				+ " WHERE empresa = ?";
 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, cif);		
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			bastanteosList.add(initBastanteo(conn, rs)); 			
		}
		return bastanteosList;
	}

	public static Escritura findEscritura(Connection conn, String refEscritura) throws SQLException, NamingException {		 
		String sql = "SELECT " + SQL_CAMPS_ESCRITURA
					+ " FROM public.tbl_escritura"
					+ " WHERE refescritura = ?";
	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setInt(1, Integer.valueOf(refEscritura));		
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			return initEscritura(conn, rs); 
		}
		return new Bastanteo().new Escritura();
	}
	
	public static List<Escritura> findEscritures(Connection conn, String refBastanteo) throws SQLException, NamingException {		 
		List<Escritura> escrituresList = new ArrayList<Escritura>();
		String sql = "SELECT " + SQL_CAMPS_ESCRITURA
					+ " FROM public.tbl_escritura"
					+ " WHERE refbastanteo = ?";
	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, refBastanteo);		
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			escrituresList.add(initEscritura(conn, rs)); 
		}
		return escrituresList;
	}
	
	public static List<Bastanteo> getBastanteos(Connection conn, String year) throws SQLException, NamingException {
		List<Bastanteo> bastanteosList = new ArrayList<Bastanteo>();
		String sql = "SELECT " + SQL_CAMPS
				+ " FROM public.tbl_bastanteos";					
		PreparedStatement pstm;		
		boolean primeraCondicio = true;	
		if (year != null && !year.isEmpty() && ! year.equals("-1")) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE anybastanteo::INT = " + year;
							
			}else{
					sql += " AND anybastanteo::INT = " + year;
							
			}			
		}
		sql += " ORDER BY databastanteo DESC, ref DESC";
		pstm = conn.prepareStatement(sql);	
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {		
			bastanteosList.add(initBastanteo(conn, rs));
		}
		return bastanteosList;
	}
	
	public static void modificarBastanteo(Connection conn, Bastanteo bastanteo) throws SQLException, NamingException {
		String sql = "UPDATE public.tbl_bastanteos"
					+ " SET databastanteo=?, empresa=?, personafacultada=?, carrec=?"
					+ " WHERE ref=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);
		if (bastanteo.getDatabastanteo() != null) {
			pstm.setDate(1, new java.sql.Date(bastanteo.getDatabastanteo().getTime()));
		} else {
			pstm.setDate(1, null);
		}
		pstm.setString(2, bastanteo.getEmpresa().getCif());
		pstm.setString(3, bastanteo.getPersonaFacultada());
		pstm.setString(4, bastanteo.getCarrec());
		pstm.setString(5, bastanteo.getRef());	
		pstm.executeUpdate();
	}
	
	public static void modificarEscritura(Connection conn, Escritura escritura, String ref) throws SQLException, NamingException {
		String sql = "UPDATE public.tbl_escritura"
				+ " SET escritura=?, dataescritura=?, nprotocol=?, notari=?"
				+ " WHERE refescritura=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, escritura.getEscritura());
		if (escritura.getDataEscritura() != null) {
			pstm.setDate(2, new java.sql.Date(escritura.getDataEscritura().getTime()));
		} else {
			pstm.setDate(2, null);
		}		
		pstm.setString(3, escritura.getNumProtocol());
		pstm.setString(4, escritura.getNotari());
		pstm.setInt(5, Integer.valueOf(ref));	
		pstm.executeUpdate();
	}
	
	public static String getNewCode(Connection conn) throws SQLException {
		String newCode = "";
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String yearInString = String.valueOf(year);
		String sql = "SELECT ref"
					+ " FROM public.tbl_bastanteos"
					+ " WHERE ref like '%/" + yearInString + "%'"
					+ " ORDER BY ref DESC;";	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();		
		if (rs.next()) { //Codis nous
			String actualCode = rs.getString("ref");			
			int num = Integer.valueOf(actualCode.split("/")[0]);
			String numFormatted = String.format("%03d", num + 1);
			newCode = numFormatted + "/" + yearInString;
		} else {
			newCode = "001/" + yearInString;
		}
		return newCode;
	}
	
	public static int getNewCodeEscritura(Connection conn) throws SQLException {
		int newCode = -1;
		String sql = "SELECT refescritura"
					+ " FROM public.tbl_escritura"
					+ " ORDER BY refescritura DESC;";	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();		
		if (rs.next()) { //Codis nous
			int actualCode = rs.getInt("refescritura");				
			newCode = actualCode + 1;
		}
		return newCode;
	}
}
