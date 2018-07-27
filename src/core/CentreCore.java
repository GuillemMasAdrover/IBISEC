package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bean.Centre;

public class CentreCore {
	
	private static Centre initCentre(Connection conn, ResultSet rs, Boolean complet) throws SQLException {
		Centre centre = new Centre();
		centre.setIdCentre(rs.getString("codi"));
		centre.setNom(rs.getString("nom"));
		centre.setTipo(rs.getString("tipo"));
		centre.setIlla(rs.getString("illa"));
		centre.setMunicipi(rs.getString("municipi"));
		centre.setLocalitat(rs.getString("localitat"));
		centre.setAdreca(rs.getString("adreca"));
		centre.setCp(rs.getString("cp"));
		centre.setLat(rs.getDouble("lat"));
		centre.setLng(rs.getDouble("lng"));
		if (complet) {
			centre.setActuacions(ActuacioCore.searchActuacions(conn, rs.getString("codi"), null,null,null));
			centre.setIncidencies(IncidenciaCore.searchIncidencies(conn, rs.getString("codi"), false, false, null, null));
		}
		return centre;
	}
	
	public static void nouCentre(Connection conn, Centre centre) throws SQLException {
		String sql = "INSERT INTO public.tbl_centres(codi, tipo, nom, illa, municipi, localitat, adreca, cp)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, getNewCode(conn));
		pstm.setString(2, centre.getTipo());
		pstm.setString(3, centre.getNom());
		pstm.setString(4, centre.getIlla());
		pstm.setString(5, centre.getMunicipi());
		pstm.setString(6, centre.getLocalitat());
		pstm.setString(7, centre.getAdreca());
		pstm.setString(8, centre.getCp());
		pstm.executeUpdate();
	}
	
	public static String nomCentre(Connection conn, String idCentre) throws SQLException {		 
		String sql = "SELECT nom from public.tbl_centres"
					+ " WHERE codi = ?";
		String nomCentre = "";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, idCentre);		
		ResultSet rs = pstm.executeQuery();	 
		if (rs.next()) {
			nomCentre = rs.getString("nom");
		}
		return nomCentre;
	}
	
	public static String nomCentreComplet(Connection conn, String idCentre) throws SQLException {		 
		String sql = "SELECT nom, localitat from public.tbl_centres"
					+ " WHERE codi = ?";
		String nomCentre = "";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, idCentre);		
		ResultSet rs = pstm.executeQuery();	 
		if (rs.next()) {
			nomCentre = rs.getString("nom") + " (" + rs.getString("localitat") + ")";
		}
		return nomCentre;
	}
	
	public static List<Centre> findCentres(Connection conn, boolean ambIncidencies) throws SQLException {
		List<Centre> centres = new ArrayList<Centre>();
		String sql = "SELECT codi, tipo, nom, illa, municipi, localitat, adreca, cp, lat, lng"
					+ " FROM public.tbl_centres;";
		PreparedStatement pstm = conn.prepareStatement(sql);				
		ResultSet rs = pstm.executeQuery();	 
		while (rs.next()) {
			Centre centre = initCentre(conn, rs, ambIncidencies);
			centres.add(centre);
		}
		return centres;
	}
	
	public static List<Centre> findCentresWithIncidencies(Connection conn, String tipus, Date dataPeticioIni, Date dataPeticioFi, String estat, Date dataExecucioIni, Date dataExecucioFi) throws SQLException {
		List<Centre> centres = new ArrayList<Centre>();
		String sql = "SELECT codi, tipo, nom, illa, municipi, localitat, adreca, cp, lat, lng"
					+ " FROM public.tbl_centres c" 
					+ " WHERE EXISTS("
					+ " SELECT DISTINCT i.idinf AS idinforme"
					+ " FROM public.tbl_informeactuacio i LEFT JOIN public.tbl_propostesinforme p ON i.idinf = p.idinf" 
					+ " LEFT JOIN public.tbl_expedient exp ON exp.expcontratacio = i.expcontratacio"
					+ " LEFT JOIN public.tbl_empresaoferta e ON e.idinforme = i.idinf"
					+ " LEFT JOIN public.tbl_empreses em ON e.cifempresa = em.cif"
					+ " LEFT JOIN public.tbl_usuaris u ON i.usucre = u.idusuari"
					+ " LEFT JOIN public.tbl_actuacio a ON i.idactuacio = a.id"
					+ " WHERE (p.seleccionada = true OR p.seleccionada IS NULL) AND (e.seleccionada = true OR e.seleccionada IS NULL) AND NOT(i.datatancament IS NOT NULL OR (exp.expcontratacio IS NOT NULL AND exp.datarecepcio IS NOT NULL)) AND i.estat != 'acabat' AND i.estat != 'garantia' AND u.departament IN ('obres','instalacions')"
					+	" AND a.idcentre = c.codi)";
					
		PreparedStatement pstm;		
		if (dataPeticioIni != null) {
			//sql += " AND a.datacre >= ?";	
		}
		if (dataPeticioFi != null) {
			//sql += " AND a.datacre <= ?";					
		}
		if (dataExecucioIni != null && dataExecucioFi != null) {
			//sql += " AND e.datainiciexecucio <= ? AND e.datarecepcio IS NULL";					
		} else {
			if (dataExecucioIni != null) {
				//sql += " AND e.datainiciexecucio <= ? AND e.datarecepcio IS NULL";	
			}
			if (dataExecucioFi != null) {
				//sql += " AND e.datainiciexecucio <= ? AND e.datarecepcio IS NULL";					
			}
		}			
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance(); 
		Date dataFi = cal.getTime();
		String dataFiString = df.format(dataFi);	
		if (estat != null && ! estat.equals("-1")) {	
			//if ("redaccio".equals(estat)) sql+= " AND a.datatancament IS NULL AND e.dataliquidacio IS NULL AND i.expcontratacio = ''"; 
			//if ("iniciExpedient".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND i.expcontratacio != '' AND i.databoib IS NULL AND e.dataadjudicacio IS NULL"; 
			//if ("publicat".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND i.databoib IS NOT NULL AND e.datalimitprsentacio >= '" + dataFiString + "' AND e.dataadjudicacio IS NULL"; 
			//if ("licitacio".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND i.databoib IS NOT NULL AND e.datalimitprsentacio < '" + dataFiString + "' AND e.dataadjudicacio IS NULL"; 	
			//if ("adjudicacio".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND e.dataadjudicacio IS NOT NULL AND e.dataformalitzaciocontracte IS NULL"; 	
			//if ("firmat".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND e.datarecepcio IS NULL AND i.databoib > '01/01/2016' AND (e.datainiciexecucio IS NULL OR e.datainiciexecucio > localtimestamp) AND e.dataformalitzaciocontracte IS NOT NULL"; 	
			//if ("execucio".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND i.databoib > '01/01/2016' AND e.datainiciexecucio <= localtimestamp AND e.datarecepcio IS NULL"; 	
			//if ("garantia".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND i.databoib > '01/01/2016' AND e.datarecepcio IS NOT NULL AND e.dataretorngarantia < localtimestamp"; 	
			//if ("acabat".equals(estat)) sql+= " AND e.dataliquidacio IS NOT NULL"; 	
		}
		if (tipus != null &&  !tipus.isEmpty() && ! tipus.equals("-1")) {
			//sql+= " AND i.tipo = ?"; 		
		} else {
			//sql+= " AND i.tipo NOT LIKE '%lloguer%' AND i.tipo NOT LIKE '%interns%'";
		}
		//sql += " )";
		
		pstm = conn.prepareStatement(sql);
		/*int contVars = 1;
		if (dataPeticioIni != null) {
			pstm.setDate(contVars, new java.sql.Date(dataPeticioIni.getTime()));
			contVars += 1;
		}
		if (dataPeticioFi != null) {
			pstm.setDate(contVars, new java.sql.Date(dataPeticioFi.getTime()));
			contVars += 1;
		}
		if (dataExecucioIni != null && dataExecucioFi != null) {
			pstm.setDate(contVars, new java.sql.Date(dataExecucioFi.getTime()));
			contVars += 1;
		} else {
			if (dataExecucioIni != null) {
				pstm.setDate(contVars, new java.sql.Date(dataExecucioIni.getTime()));
				contVars += 1;
			}
			if (dataExecucioFi != null) {
				pstm.setDate(contVars, new java.sql.Date(dataExecucioFi.getTime()));
				contVars += 1;
			}
		}
		
		if (tipus != null && !tipus.isEmpty() && ! tipus.equals("-1")) {
			pstm.setString(contVars, tipus);
			contVars += 1;
		}		*/
		ResultSet rs = pstm.executeQuery();	 
		while (rs.next()) {
			Centre centre = initCentre(conn, rs, false);
			centres.add(centre);
		}
		return centres;
	}
	
	
	public static Centre findCentre(Connection conn, String codi, boolean complet) throws SQLException {		
		String sql = "SELECT codi, tipo, nom, illa, municipi, localitat, adreca, cp, lat, lng"
					+ " FROM public.tbl_centres"
					+ " WHERE codi = ?;";
		PreparedStatement pstm = conn.prepareStatement(sql);	
		pstm.setString(1, codi);	
		ResultSet rs = pstm.executeQuery();	 
		Centre centre = new Centre();
		if (rs.next()) {
			centre = initCentre(conn, rs, complet);			
		}
		return centre;
	}
	
	public static String getNewCode(Connection conn) throws SQLException {	
		String newCode = "";
		String sql = "SELECT codi FROM public.tbl_centres WHERE codi NOT IN ('9999PERSO','99999999') AND codi LIKE '99%' ORDER BY 1 DESC LIMIT 1";
		PreparedStatement pstm = conn.prepareStatement(sql);	
		ResultSet rs = pstm.executeQuery();	 
		if (rs.next()) {
			newCode = String.valueOf(Integer.valueOf(rs.getString("codi")) + 1);
		}
		return newCode;
	}
}
