package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import bean.Actuacio;
import bean.Projecte;
import bean.User;

public class ProjecteCore {
	static final String SQL_CAMPS = "idprojecte, idactuacio, usuaris, datacreacio, nomprojecte";
	
	private static Projecte initProjecte(Connection conn, ResultSet rs) throws SQLException, NamingException{
		Projecte projecte = new Projecte();
		projecte.setIdProjecte(rs.getString("idprojecte"));
		projecte.setActuacio(ActuacioCore.findActuacio(conn, rs.getString("idactuacio")));
		projecte.setUsuarisList(getUsuaris(conn, rs.getString("usuaris")));
		projecte.setDataCreacio(rs.getTimestamp("datacreacio"));
		projecte.setNomProjecte(rs.getString("nomprojecte"));
		return projecte;
	}
	
	public static String nouProjecte(Connection conn, Projecte projecte) throws SQLException {
		String newCode = newCodeProjecte(conn);
		String sql = "INSERT INTO public.tbl_projecte(" + SQL_CAMPS + ")"
				+ "VALUES (?,?,?,localtimestamp,?)";
 
		PreparedStatement pstm = conn.prepareStatement(sql);
	 
		pstm.setString(1, newCode);
		pstm.setString(2, projecte.getActuacio().getReferencia());
		pstm.setString(3, projecte.getUsuarisListString());		
		pstm.setString(4, projecte.getNomProjecte());
		pstm.executeUpdate();
		return newCode;
	}
	
	public static Projecte findProjecte(Connection conn, String idProjecte) throws SQLException, NamingException {
		Projecte projecte = new Projecte();
		String sql = "SELECT " + SQL_CAMPS
				+ " FROM public.tbl_projecte"
				+ " WHERE idprojecte = ?";	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, idProjecte);		
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			projecte = initProjecte(conn, rs);
		}
		return projecte;
	}
	
	public static List<Projecte> getProjectes(Connection conn) throws SQLException, NamingException {
		List<Projecte> projectesList = new ArrayList<Projecte>();
		String sql = "SELECT " + SQL_CAMPS
				+ " FROM public.tbl_projecte"
				+ " ORDER BY datacreacio DESC, idprojecte DESC";
 
		PreparedStatement pstm = conn.prepareStatement(sql); 
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			Projecte projecte = initProjecte(conn, rs);
			projectesList.add(projecte);
		}
		return projectesList;
	}
	
	private static List<User> getUsuaris(Connection conn, String usuaris) throws SQLException {
		List<User> usuarisList = new ArrayList<User>();
		for (String idUsuari: usuaris.split("#")) {
			usuarisList.add(UsuariCore.findUsuari(conn, idUsuari));
		}
		return usuarisList;
	}
	
	private static String newCodeProjecte(Connection conn) throws SQLException {
		String codi = "1";
		String sql = "SELECT idprojecte"
				+ " FROM public.tbl_projecte"
				+ " ORDER BY idprojecte::INT DESC LIMIT 1;";	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) { //Codis nous
			String actualCode = rs.getString("idprojecte");			
			int num = Integer.valueOf(actualCode) + 1;	
			codi = String.valueOf(num);
		}
		return codi;
	}
}
