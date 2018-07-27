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

import javax.naming.NamingException;

import bean.Actuacio;
import bean.InformeActuacio;
import bean.Actuacio.Feina;
import bean.Resultat;
import bean.Resultat.Estadistiques;
public class ActuacioCore {
	
	static final String SQL_CAMPS = "id, idincidencia, descripcio, idcentre, usucre, datacre, dataaprovacio, usuaprovacio, datatancament, usutancament, datamodificacio, darreramodificacio, usuaprovarpa, dataaprovarpa, notes, motiutancament";
	
	private static Actuacio initActuacio(Connection conn, ResultSet rs, Estadistiques estad) throws SQLException{
		Actuacio actuacio = new Actuacio();
		actuacio.setReferencia(rs.getString("id"));
		actuacio.setDescripcio(rs.getString("descripcio"));		
		actuacio.setDataCreacio(rs.getTimestamp("datacre"));
		actuacio.setIdUsuariCreacio(rs.getInt("usucre"));		
		actuacio.setCentre(CentreCore.findCentre(conn, rs.getString("idcentre"), false));	
		actuacio.setDataTancament(rs.getTimestamp("datatancament"));	
		actuacio.setMotiuTancament(rs.getString("motiutancament"));
		actuacio.setDataAprovacio(rs.getTimestamp("dataaprovacio"));		
		actuacio.setIdIncidencia(rs.getString("idincidencia"));		
		actuacio.setDarreraModificacio(rs.getTimestamp("datamodificacio"));
		actuacio.setModificacio(rs.getString("darreramodificacio"));
		actuacio.setDataAprovarPa(rs.getTimestamp("dataaprovarpa"));
		actuacio.setNotes(rs.getString("notes"));
		actuacio.setRefExt(searchRefExterna(conn, rs.getString("id")));
		if (estad != null) {
			if (actuacio.getDataTancament() == null) {
				if (actuacio.getDataAprovacio() != null) {
					estad.setAprovadesPT(estad.getAprovadesPT() + 1);
				} else if (actuacio.getDataAprovarPa() != null) {
					estad.setAprovadesPA(estad.getAprovadesPA() + 1);
				} else {
					estad.setPendents(estad.getPendents() + 1);
				}			
			} else {
				estad.setTancades(estad.getTancades() + 1);
			}
		}
		actuacio.setFeines(getFeinesActuacio(conn, rs.getString("id")));
		return actuacio;
	}
	
	public static void novaActuacio(Connection conn, Actuacio actuacio) throws SQLException {
		String sql = "INSERT INTO public.tbl_actuacio(" + SQL_CAMPS + ")"
					+ "VALUES (?,?,?,?,?,localtimestamp,null,null,null,null,localtimestamp,?,null,null,'','')";
	 
		PreparedStatement pstm = conn.prepareStatement(sql);
	 
		pstm.setString(1, actuacio.getReferencia());
		pstm.setString(2, actuacio.getIdIncidencia());
		pstm.setString(3, actuacio.getDescripcio());
		pstm.setString(4, actuacio.getCentre().getIdCentre());
		pstm.setInt(5, actuacio.getIdUsuariCreacio());
		pstm.setString(6, "Creació");		
		
		pstm.executeUpdate();
	}
	
	public static Actuacio findActuacio(Connection conn, String referencia) throws SQLException {
		 
		String sql = "SELECT " + SQL_CAMPS
					+ " FROM public.tbl_actuacio"
					+ " WHERE id = ?";	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, referencia);		
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			Actuacio actuacio = initActuacio(conn, rs, null); 
			return actuacio;
		}
		return new Actuacio();
	}
	
	public static Resultat topAcuacions(Connection conn) throws SQLException {
		String sql = "SELECT " + SQL_CAMPS
					+ " FROM public.tbl_actuacio"
					+ " ORDER BY datacre DESC, id DESC"
					+ " LIMIT 500";
	 
		PreparedStatement pstm = conn.prepareStatement(sql);
	 
		ResultSet rs = pstm.executeQuery();
		Resultat result = new Resultat();
		List<Actuacio> list = new ArrayList<Actuacio>();
		Estadistiques estad = result.new Estadistiques();
		while (rs.next()) {
			Actuacio actuacio = initActuacio(conn, rs, estad);
			list.add(actuacio);
		}
		result.setEstad(estad);
		result.setLlistaActuacions(list);
		return result;
	}
	
	public static Resultat searchActuacions(Connection conn, String idCentre, String estat, Date dataIni, Date dataFi) throws SQLException {
		String sql = "SELECT " + SQL_CAMPS
					+ " FROM public.tbl_actuacio";					
		PreparedStatement pstm;		
		boolean primeraCondicio = true;
		if (dataIni != null) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE datacre >= ?";
			}			
		}
		if (dataFi != null) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE datacre <= ?";
			} else {
				sql += " and datacre <= ?";
			}			
		}
		if (idCentre != null && !idCentre.isEmpty() && ! "-1".equals(idCentre)) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE idcentre = ?";
			} else {
				sql += " and idcentre = ?";
			}			
		}
		if (estat != null && ! estat.equals("-1")) {
			if (primeraCondicio) {
				primeraCondicio = false;
				if ("AprovadaPT".equals(estat)) sql+= " WHERE datatancament IS null AND dataaprovacio IS NOT null"; 
				if ("AprovadaPA".equals(estat)) sql+= " WHERE datatancament IS null AND dataaprovarpa IS NOT null"; 
				if ("Pendent".equals(estat)) sql+= " WHERE datatancament IS null AND dataaprovacio IS null AND dataaprovarpa IS null"; 
				if ("Tancada".equals(estat)) sql+= " WHERE datatancament IS NOT null"; 		
			} else {
				if ("AprovadaPT".equals(estat)) sql+= " AND datatancament IS null AND dataaprovacio IS NOT null"; 
				if ("AprovadaPA".equals(estat)) sql+= " AND datatancament IS null AND dataaprovarpa IS NOT null"; 
				if ("Pendent".equals(estat)) sql+= " AND datatancament IS null AND dataaprovacio IS null AND dataaprovarpa IS null"; 
				if ("Tancada".equals(estat)) sql+= " AND datatancament IS NOT null"; 	
			}			
		}
		sql += " ORDER BY datacre DESC, id DESC";
		
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
		if (idCentre != null && !idCentre.isEmpty() && ! "-1".equals(idCentre)) {
			pstm.setString(contVars, idCentre);
			contVars += 1;
		}				
		ResultSet rs = pstm.executeQuery();
		Resultat result = new Resultat();
		List<Actuacio> list = new ArrayList<Actuacio>();
		Estadistiques estad = result.new Estadistiques();
		while (rs.next()) {			
			Actuacio actuacio = initActuacio(conn, rs, estad);	
			list.add(actuacio);
		}
		result.setEstad(estad);
		result.setLlistaActuacions(list);
		return result;
	}
	
	public static List<Actuacio> searchActuacionsList(Connection conn, String idCentre, String estat, Date dataPeticioIni, Date dataPeticioFi, String tipus, Date dataExecucioIni, Date dataExecucioFi) throws SQLException, NamingException {
		String sql = "SELECT DISTINCT a.id AS idactuacio, a.descripcio AS descripcio, i.expcontratacio AS expcontratacio"
				+ 	" FROM public.tbl_actuacio a LEFT JOIN public.tbl_informeactuacio i ON a.id = i.idactuacio"
				+ 	"							 LEFT JOIN public.tbl_expedient e ON e.expcontratacio = i.expcontratacio"
				+	" WHERE a.idcentre = ?";
				
		PreparedStatement pstm;		
		if (dataPeticioIni != null) {
			sql += " AND a.datacre >= ?";	
		}
		if (dataPeticioFi != null) {
			sql += " AND a.datacre <= ?";					
		}
		if (dataExecucioIni != null && dataExecucioFi != null) {
			sql += " AND e.datainiciexecucio <= ? AND e.datarecepcio IS NULL";					
		} else {
			if (dataExecucioIni != null) {
				sql += " AND e.datainiciexecucio <= ? AND e.datarecepcio IS NULL";	
			}
			if (dataExecucioFi != null) {
				sql += " AND e.datainiciexecucio <= ? AND e.datarecepcio IS NULL";					
			}
		}	
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance(); 
		Date dataFi = cal.getTime();
		String dataFiString = df.format(dataFi);	
		if (estat != null && ! estat.equals("-1")) {	
			if ("redaccio".equals(estat)) sql+= " AND a.datatancament IS NULL AND e.dataliquidacio IS NULL AND i.expcontratacio = ''"; 
			if ("iniciExpedient".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND i.expcontratacio != '' AND i.databoib IS NULL AND e.dataadjudicacio IS NULL"; 
			if ("publicat".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND i.databoib IS NOT NULL AND e.datalimitprsentacio >= '" + dataFiString + "' AND e.dataadjudicacio IS NULL"; 
			if ("licitacio".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND i.databoib IS NOT NULL AND e.datalimitprsentacio < '" + dataFiString + "' AND e.dataadjudicacio IS NULL";
			if ("adjudicacio".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND e.dataadjudicacio IS NOT NULL AND e.dataformalitzaciocontracte IS NULL"; 	
			if ("firmat".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND i.databoib > '01/01/2015' AND (e.datainiciexecucio IS NULL OR e.datainiciexecucio > localtimestamp) AND e.dataformalitzaciocontracte IS NOT NULL"; 	
			if ("execucio".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND i.databoib > '01/01/2015' AND e.datainiciexecucio <= localtimestamp AND e.datarecepcio IS NULL"; 	
			if ("garantia".equals(estat)) sql+= " AND e.dataliquidacio IS NULL AND i.databoib > '01/01/2015' AND e.datarecepcio IS NOT NULL AND e.dataretorngarantia < localtimestamp"; 	
			if ("acabat".equals(estat)) sql+= " AND e.dataliquidacio IS NOT NULL"; 	
		}
		if (tipus != null &&  !tipus.isEmpty() && ! tipus.equals("-1")) {
			sql+= " AND i.tipo = ?"; 		
		} else {
			sql+= " AND i.tipo NOT LIKE '%lloguer%' AND i.tipo NOT LIKE '%interns%'";
		}
		
		pstm = conn.prepareStatement(sql);
		pstm.setString(1, idCentre);
		int contVars = 2;
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
		}		
		ResultSet rs = pstm.executeQuery();		
		List<Actuacio> list = new ArrayList<Actuacio>();
		while (rs.next()) {			
			Actuacio actuacio = new Actuacio();
			actuacio.setReferencia(rs.getString("idactuacio"));
			actuacio.setDescripcio(rs.getString("descripcio"));
			InformeActuacio informe = new InformeActuacio();
			informe.setExpcontratacio(ExpedientCore.findExpedient(conn, rs.getString("expcontratacio")));
			actuacio.setInformePrevi(informe);
			list.add(actuacio);
		}
		return list;
	}
	
	public static Resultat searchActuacionsInciencia(Connection conn, String idIncidencia) throws SQLException {
		List<Actuacio> list = new ArrayList<Actuacio>();
		String sql = "SELECT " + SQL_CAMPS
				 	+ " FROM public.tbl_actuacio"
				 	+ " WHERE idincidencia = ?";
				
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setString(1, idIncidencia);	
		ResultSet rs = pstm.executeQuery();
		Resultat result = new Resultat();
		Estadistiques estad = result.new Estadistiques();
		while (rs.next()) {
			Actuacio actuacio = initActuacio(conn, rs, estad);			
			list.add(actuacio);
		}
		result.setEstad(estad);
		result.setLlistaActuacions(list);
		return result;
	}
	
	public static String searchRefExterna(Connection conn, String idActuacio) throws SQLException {
		String refExt = "";
		String sql = "SELECT expcontratacio"
			 	+ " FROM public.tbl_informeactuacio"
			 	+ " WHERE idactuacio = ?";			
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setString(1, idActuacio);	
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			refExt += rs.getString("expcontratacio") + " // ";
		}
		return refExt;
	}
	
	public static String getNewCode(Connection conn) throws SQLException {		
		String newCode = "1";
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String yearInString = String.valueOf(year);
		String sql = "SELECT id, datacre"
					+ " FROM public.tbl_actuacio"
					+ " WHERE id like '%" + yearInString + "-ACT%'"
					+ " ORDER BY datacre DESC, id DESC LIMIT 1;";	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();		
		String prefix = "ACT";
		if (rs.next()) { //Codis nous
			String actualCode = rs.getString("id");			
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

	public static void actualitzarActuacio(Connection conn, String idActuacio, String modificacio) throws SQLException{
		String sql = "UPDATE public.tbl_actuacio"
				+ " SET darreramodificacio=?, datamodificacio=localtimestamp"
				+ " WHERE id=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm = conn.prepareStatement(sql);
		pstm.setString(1, modificacio);
		pstm.setString(2, idActuacio);
		pstm.executeUpdate();
	}
	
	public static void modificarActuacio(Connection conn, String idActuacio, String descripcio, String notes) throws SQLException {
		String sql = "UPDATE public.tbl_actuacio"
				+ " SET descripcio=?, notes=?"
				+ " WHERE id=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm = conn.prepareStatement(sql);
		pstm.setString(1, descripcio);
		pstm.setString(2, notes);
		pstm.setString(3, idActuacio);
		pstm.executeUpdate();
	}
	
	public static void aprovarPA(Connection conn, String referencia, int idUsuari) throws SQLException {
		String sql = "UPDATE public.tbl_actuacio"
					+ " SET dataaprovarpa=localtimestamp, usuaprovarpa=?, darreramodificacio='aprovar proposta', datamodificacio=localtimestamp"
					+ " WHERE id=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, idUsuari);
		pstm.setString(2, referencia);
		pstm.executeUpdate();
	}
	
	public static void eliminarAprovacioPA(Connection conn, String referencia) throws SQLException {
		String sql = "UPDATE public.tbl_actuacio"
				+ " SET dataaprovarpa=null, usuaprovarpa=null, darreramodificacio='', datamodificacio=localtimestamp"
				+ " WHERE id=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm = conn.prepareStatement(sql);
		pstm.setString(1, referencia);
		pstm.executeUpdate();
	}
	
	public static void aprovar(Connection conn, String referencia, int idUsuari) throws SQLException {
		String sql = "UPDATE public.tbl_actuacio"
					+ " SET dataaprovacio=localtimestamp, usuaprovacio=?, darreramodificacio='aprovar actuació', datamodificacio=localtimestamp"
					+ " WHERE id=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, idUsuari);
		pstm.setString(2, referencia);
		pstm.executeUpdate();
	}
	
	public static void tancar(Connection conn, String referencia, String motiu,  int idUsuari) throws SQLException {
		String sql = "UPDATE public.tbl_actuacio"
					+ " SET datatancament=localtimestamp, usutancament=?, darreramodificacio='tancar actuació', datamodificacio=localtimestamp, motiutancament=?"
					+ " WHERE id=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setInt(1, idUsuari);
		pstm.setString(2, motiu);
		pstm.setString(3, referencia);
		pstm.executeUpdate();
	}
	
	public static void obrir(Connection conn, String referencia) throws SQLException {
		String sql = "UPDATE public.tbl_actuacio"
					+ " SET datatancament=null, usutancament=null, darreramodificacio='reobrir actuació', datamodificacio=localtimestamp, motiutancament=null"
					+ " WHERE id=?;";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, referencia);
		pstm.executeUpdate();
	}
	
	public static void seguirActuacio(Connection conn, String idActuacio, int idUsuari) throws SQLException {
		String sql = "INSERT INTO public.tbl_seguiments(idusuari, idactuacio)"
				+ " VALUES (?, ?);";		 
		PreparedStatement pstm = null; 
		pstm = conn.prepareStatement(sql);	
		pstm.setInt(1, idUsuari);
		pstm.setString(2, idActuacio);
		pstm.executeUpdate();
	}
	
	public static void desSeguirActuacio(Connection conn, String idActuacio, int idUsuari) throws SQLException {
		String sql = "DELETE FROM public.tbl_seguiments"
					+ " WHERE idactuacio = ? AND idusuari = ?";		 
		PreparedStatement pstm = null; 
		pstm = conn.prepareStatement(sql);	
		pstm.setString(1, idActuacio);
		pstm.setInt(2, idUsuari);
		pstm.executeUpdate();
	}
	
	public static List<Actuacio> llistaActuacionsSeguiment(Connection conn, int idUsuari) throws SQLException {
		String sql = "SELECT *"
					+ " FROM public.tbl_seguiments s LEFT JOIN public.tbl_actuacio a ON s.idactuacio = a.id"
					+ " WHERE idactuacio IS NOT NULL AND s.idusuari = ?";
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setInt(1, idUsuari);		
		ResultSet rs = pstm.executeQuery();
		List<Actuacio> list = new ArrayList<Actuacio>();
		while (rs.next()) {
			Actuacio actuacio = initActuacio(conn, rs, null);
			list.add(actuacio);
		}
	    return list;
	}
	
	public static boolean isSeguimentActuacio(Connection conn, String idActuacio, int idUsuari) throws SQLException{
		boolean seguint = false;
		String sql = "SELECT idusuari, idactuacio"
					+ " FROM public.tbl_seguiments"
					+ " WHERE idactuacio = ? AND idusuari = ?";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, idActuacio);
		pstm.setInt(2, idUsuari);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) seguint = true;
		return seguint;
	}
	
	private static List<Feina> getFeinesActuacio(Connection conn, String idActuacio) throws SQLException {
		List<Feina> feinesList = new ArrayList<Feina>();
		String sql = "SELECT idfeina, nomremitent, nomdestinatari, contingut, data, notes"
					+ " FROM public.tbl_feines"
					+ " WHERE idactuacio = ?"
					+ " ORDER BY idfeina";
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setString(1, idActuacio);	
		ResultSet rs = pstm.executeQuery();
		Feina feina = null;
		while (rs.next()) {
			feina = new Actuacio().new Feina();
			feina.setIdFeina(rs.getString("idfeina"));
			feina.setNomRemitent(rs.getString("nomremitent"));
			feina.setNomDestinatari(rs.getString("nomdestinatari"));
			feina.setContingut(rs.getString("contingut"));
			feina.setData(rs.getTimestamp("data"));
			feina.setNotes(rs.getString("notes"));		
			feinesList.add(feina);
		}
		return feinesList;
	}
	
	public static void afegirFeina(Connection conn, String idActuacio, Feina feina) throws SQLException {
		String sql = "INSERT INTO public.tbl_feines(idfeina, idactuacio, nomremitent, nomdestinatari, contingut, data, notes)"
					+ " VALUES (?, ?, ?, ?, ?, localtimestamp, ?);";		 
		PreparedStatement pstm = conn.prepareStatement(sql);	
		pstm.setString(1, getCodiNovaFeina(conn));
		pstm.setString(2, idActuacio);
		pstm.setString(3, feina.getNomRemitent());
		pstm.setString(4, feina.getNomDestinatari());
		pstm.setString(5, feina.getContingut());
		pstm.setString(6, feina.getNotes());
		pstm.executeUpdate();
	}
	
	public static void eliminarFeina(Connection conn, String idFeina) throws SQLException {
		String sql = "DELETE FROM public.tbl_feines"
				+ " WHERE idfeina = ?";		 
		PreparedStatement pstm = conn.prepareStatement(sql);	
		pstm.setString(1, idFeina);
		pstm.executeUpdate();
	}
	
	public static void modificarFeina(Connection conn, Feina feina) throws SQLException {
		String sql = "UPDATE public.tbl_feines"
					+ " SET nomremitent = ?, nomdestinatari = ?, contingut = ?, notes = ?"
					+ " WHERE idfeina = ?";		 
		PreparedStatement pstm = conn.prepareStatement(sql);	
		pstm.setString(1, feina.getNomRemitent());
		pstm.setString(2, feina.getNomDestinatari());
		pstm.setString(3, feina.getContingut());
		pstm.setString(4, feina.getNotes());
		pstm.setString(5, feina.getIdFeina());
		pstm.executeUpdate();
	}
	
	public static Feina findFeina(Connection conn, String idFeina) throws SQLException {
		Feina feina = new Actuacio().new Feina();
		String sql = "SELECT idfeina, idactuacio, nomremitent, nomdestinatari, contingut, data, notes"
				+ " FROM public.tbl_feines"
				+ " WHERE idfeina = ?";		 
		PreparedStatement pstm = conn.prepareStatement(sql);	
		pstm.setString(1, idFeina);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) { //Codis nous
			feina.setIdFeina(rs.getString("idfeina"));
			feina.setNomRemitent(rs.getString("nomremitent"));
			feina.setNomDestinatari(rs.getString("nomdestinatari"));
			feina.setContingut(rs.getString("contingut"));
			feina.setNotes(rs.getString("notes"));
		}
		return feina;
	}
	
	public static String getCodiNovaFeina(Connection conn) throws SQLException {
		String codi = "1";
		String sql = "SELECT idfeina"
				+ " FROM public.tbl_feines"
				+ " ORDER BY idfeina::INT DESC LIMIT 1;";	 
		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) { //Codis nous
			String actualCode = rs.getString("idfeina");			
			int num = Integer.valueOf(actualCode) + 1;	
			codi = String.valueOf(num);
		}
		return codi;
	}
}
