package core;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import bean.Empresa;
import bean.Empresa.Administrador;
import utils.Fitxers;
import utils.Fitxers.Fitxer;

public class EmpresaCore {
	
	static final String SQL_CAMPS = "cif, nom, direccio, cp, ciutat, provincia, telefon, fax, email, usumod, datamod,"
								+ " dataexpacreditacio1, dataexpacreditacio2, dataexpacreditacio3,"
								+ " classificacio, dataconstitucio, informacioadicional, exercicieconomic, dataregistremercantil, ratioap,"
								+ " datavigenciaclassificaciorolece, datavigenciaclassificaciojccaib, datavigenciaclassificaciojca, pime, activa, cifsuccesora, motiuextincio, prohibiciocontractar";
	
	
	private static Empresa initEmpresa(Connection conn, ResultSet rs) throws SQLException, NamingException{		
		Empresa empresa = new Empresa();
		empresa.setCif(rs.getString("cif"));
		empresa.setName(rs.getString("nom"));
		empresa.setDireccio(rs.getString("direccio"));
		empresa.setCP(rs.getString("cp"));
		empresa.setCiutat(rs.getString("ciutat"));
		empresa.setProvincia(rs.getString("provincia"));
		empresa.setTelefon(rs.getString("telefon"));
		empresa.setFax(rs.getString("fax"));
		empresa.setEmail(rs.getString("email"));
		empresa.setDataConstitucio(rs.getTimestamp("dataconstitucio"));	
		empresa.setDocumentsEscrituraList(getEscritures(empresa.getCif()));
		empresa.setDocumentREA(getDocumentREA(empresa.getCif()));
		empresa.setDateExpAcreditacio1(rs.getTimestamp("dataexpacreditacio1"));
		empresa.setDateExpAcreditacio2(rs.getTimestamp("dataexpacreditacio2"));
		empresa.setDateExpAcreditacio3(rs.getTimestamp("dataexpacreditacio3"));	
		empresa.setClassificacioString(rs.getString("classificacio"));
		empresa.setAdministradorsString(getAdministradorsString(conn, empresa.getCif()));
		List<Empresa.Administrador> administradorsList = getAdministradors(conn, empresa.getCif());				
		empresa.setAdministradors(administradorsList);	
		empresa.setSolEconomica(getSolEconomica(empresa.getCif()));
		empresa.setSolTecnica(getSolTecnica(empresa.getCif()));
		empresa.setInformacioAdicional(rs.getString("informacioadicional"));
		empresa.setExerciciEconomic(rs.getTimestamp("exercicieconomic"));
		empresa.setRegistreMercantilData(rs.getTimestamp("dataregistremercantil"));
		empresa.setRatioAP(rs.getDouble("ratioap"));
		empresa.setClassificacioFileROLECE(getClassificacioROLECE(empresa.getCif()));
		empresa.setClassificacioFileJCCaib(getClassificacioJCCaib(empresa.getCif()));
		empresa.setClassificacioFileJCA(getClassificacioJCA(empresa.getCif()));		
		empresa.setDataVigenciaClassificacioROLECE(rs.getTimestamp("datavigenciaclassificaciorolece"));
		empresa.setDataVigenciaClassificacioJCCaib(rs.getTimestamp("datavigenciaclassificaciojccaib"));
		empresa.setDataVigenciaClassificacioJCA(rs.getTimestamp("datavigenciaclassificaciojca"));
		empresa.setPime(rs.getBoolean("pime"));
		empresa.setActiva(rs.getBoolean("activa"));
		empresa.setProhibicioContractar(rs.getBoolean("prohibiciocontractar"));
		if (rs.getBoolean("prohibiciocontractar")) {
			empresa.setDocumentsProhibicioContractarList(getDocumentsProhibicioContractar(rs.getString("cif")));
		}
		if (!rs.getBoolean("activa")) {
			empresa.setSuccesora(findSuccesora(conn, rs.getString("cifsuccesora")));
			empresa.setMotiuExtincio(rs.getString("motiuExtincio"));
			empresa.setExtincioFile(getExtincioFile(empresa.getCif()));
			empresa.setSuccesoraFile(getSuccesioFile(empresa.getCif()));
		}
		return empresa;
	}
	
	private static Empresa initUTE(Connection conn, ResultSet rs) throws SQLException, NamingException{
		Empresa empresa = new Empresa();
		 empresa.setCif(rs.getString("cif"));
		 empresa.setName(rs.getString("nom"));
		 empresa.setInformacioAdicional(rs.getString("informacioadicional"));
		 empresa.setActiva(rs.getBoolean("activa"));
		 Empresa.UTE ute = empresa.new UTE();
		 String[] empresesString = rs.getString("empreses").split("#");
		 List<Empresa> empreses = new ArrayList<Empresa>();			
		 for(int i=0; i<empresesString.length; i++) {
			 if (! empresesString[i].isEmpty()) {
				 try {
					 empreses.add(EmpresaCore.findEmpresa(conn, empresesString[i]));
				 } catch (SQLException e) {
					 e.printStackTrace();
				 }	    	
			 }
		 }				 
		 ute.setEmpreses(empreses);
		 List<Empresa.Administrador> administradorsList = getAdministradors(conn, empresa.getCif());				
		 empresa.setAdministradors(administradorsList);	
		 empresa.setUte(ute);			 
		 return empresa;
	}
	
	public static void deleteEmpresa(Connection conn, String codi) throws SQLException {
		String sql = "DELETE FROM public.tbl_empreses"
					+ " WHERE cif= ?";		 
		PreparedStatement pstm = conn.prepareStatement(sql); 
		pstm.setString(1, codi);
		pstm.executeUpdate();
	}
	
	public static void extincioEmpresa(Connection conn, String codi, String motiuExtincio) throws SQLException {
		String sql = "UPDATE public.tbl_empreses"
					+ " SET activa = false, motiuextincio = ?"
					+ " WHERE cif= ?";		 
		PreparedStatement pstm = conn.prepareStatement(sql); 
		pstm.setString(1, motiuExtincio);
		pstm.setString(2, codi);
		pstm.executeUpdate();
	}
	
	public static void addSuccesora(Connection conn, String codi, String codiSuccesora) throws SQLException {
		String sql = "UPDATE public.tbl_empreses"
					+ " SET activa = false, cifsuccesora = ?"
					+ " WHERE cif= ?";		 
		PreparedStatement pstm = conn.prepareStatement(sql); 
		pstm.setString(1, codiSuccesora);
		pstm.setString(2, codi);
		pstm.executeUpdate();
	}
	
	public static void insertEmpresa(Connection conn, Empresa empresa, List<Empresa.Administrador> administradorsList, int idUsuari) throws SQLException {
		String sql = "INSERT INTO public.tbl_empreses(cif, nom, direccio, cp, ciutat, provincia, telefon, fax, email, usumod, datamod, activa)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, localtimestamp, true)";		 
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, empresa.getCif());
		pstm.setString(2, empresa.getName());
		pstm.setString(3, empresa.getDireccio());
		pstm.setString(4, empresa.getCP());
		pstm.setString(5, empresa.getCiutat());
		pstm.setString(6, empresa.getProvincia());
		pstm.setString(7, empresa.getTelefon());
		pstm.setString(8, empresa.getFax());
		pstm.setString(9, empresa.getEmail());
		pstm.setInt(10, idUsuari);		
		pstm.executeUpdate();		
	}
	
	public static void updateEmpresa(Connection conn, Empresa empresa, String cifActual, List<Empresa.Administrador> administradorsList, int idUsuari) throws SQLException {
		 String sql = "UPDATE public.tbl_empreses"
				 	+ " SET cif=?, nom=?, direccio=?, cp=?, ciutat=?, provincia=?, telefon=?, fax=?, email=?, usumod=?, datamod=localtimestamp," 
				 		+ " dataexpacreditacio1=?, dataexpacreditacio2=?, dataexpacreditacio3=?," 
				 		+ " classificacio=?, dataconstitucio=?, informacioadicional=?, exercicieconomic=?,  dataregistremercantil=?,  ratioap=?,"
				 		+ " datavigenciaclassificaciorolece=?, datavigenciaclassificaciojccaib=?, datavigenciaclassificaciojca=?, pime=?, prohibiciocontractar=?"
				 	+ " WHERE cif = ?";	 
		 PreparedStatement pstm = conn.prepareStatement(sql);	
		 pstm.setString(1, empresa.getCif());
		 pstm.setString(2, empresa.getName());
		 pstm.setString(3, empresa.getDireccio());
		 pstm.setString(4, empresa.getCP());
		 pstm.setString(5, empresa.getCiutat());
		 pstm.setString(6, empresa.getProvincia());
		 pstm.setString(7, empresa.getTelefon());
		 pstm.setString(8, empresa.getFax());
		 pstm.setString(9, empresa.getEmail());
		 pstm.setInt(10, idUsuari);
			
		 if (empresa.getDateExpAcreditacio1() != null) {
			 pstm.setDate(11, new java.sql.Date(empresa.getDateExpAcreditacio1().getTime()));
		 } else {
			 pstm.setDate(11, null);
		 }		 
		
		 if (empresa.getDateExpAcreditacio2() != null) {
			 pstm.setDate(12, new java.sql.Date(empresa.getDateExpAcreditacio2().getTime()));
		 } else {
			 pstm.setDate(12, null);
		 }				
		 if (empresa.getDateExpAcreditacio3() != null) {
			 pstm.setDate(13, new java.sql.Date(empresa.getDateExpAcreditacio3().getTime()));
		 } else {
			 pstm.setDate(13, null);
		 }	
		 pstm.setString(14, empresa.getClassificacioString());		
		 if (empresa.getDataConstitucio() != null) {
			 pstm.setDate(15, new java.sql.Date(empresa.getDataConstitucio().getTime()));
		 } else {
			 pstm.setDate(15, null);
		 }	
		 pstm.setString(16, empresa.getInformacioAdicional());		 
		 if (empresa.getExerciciEconomic() != null) {
			 pstm.setDate(17, new java.sql.Date(empresa.getExerciciEconomic().getTime()));
		 } else {
			 pstm.setDate(17, null);
		 }	
		 if (empresa.getRegistreMercantilData() != null) {
			 pstm.setDate(18, new java.sql.Date(empresa.getRegistreMercantilData().getTime()));
		 } else {
			 pstm.setDate(18, null);
		 }	
		 pstm.setDouble(19, empresa.getRatioAP());
		 if (empresa.getDataVigenciaClassificacioROLECE() != null) {
			 pstm.setDate(20, new java.sql.Date(empresa.getDataVigenciaClassificacioROLECE().getTime()));
		 } else {
			 pstm.setDate(20, null);
		 }	
		 if (empresa.getDataVigenciaClassificacioJCCaib() != null) {
			 pstm.setDate(21, new java.sql.Date(empresa.getDataVigenciaClassificacioJCCaib().getTime()));
		 } else {
			 pstm.setDate(21, null);
		 }	
		 if (empresa.getDataVigenciaClassificacioJCA() != null) {
			 pstm.setDate(22, new java.sql.Date(empresa.getDataVigenciaClassificacioJCA().getTime()));
		 } else {
			 pstm.setDate(22, null);
		 }	
		 pstm.setBoolean(23, empresa.isPime());
		 pstm.setBoolean(24, empresa.isProhibicioContractar());
		 pstm.setString(25, cifActual);
		 pstm.executeUpdate();		
	 }
	 
	 public static Empresa findEmpresa(Connection conn, String cif) throws SQLException, NamingException {
		 String sql = "SELECT " + SQL_CAMPS 
				 	+ " FROM public.tbl_empreses"
				 	+ " WHERE cif=?";
	 
		 PreparedStatement pstm = conn.prepareStatement(sql);
	     pstm.setString(1, cif);	 
	     ResultSet rs = pstm.executeQuery();	 
	     if (rs.next()) {
	    	 return initEmpresa(conn, rs);
	     } else { //És una UTE
	    	 sql = "SELECT cif, nom, empreses, informacioadicional, activa"
					 	+ " FROM public.tbl_ute"
					 	+ " WHERE cif=?";
		 
	    	 
			 pstm = conn.prepareStatement(sql);
		     pstm.setString(1, cif);	 
		     rs = pstm.executeQuery();	 
		     if (rs.next()) {
		    	 return initUTE(conn, rs);
		     }
	     }
	     return null;
	 }
	 public static List<Empresa> getEmpreses(Connection conn) throws SQLException {
		 String sql = "SELECT " + SQL_CAMPS
				 	+ " FROM public.tbl_empreses";	 
		 PreparedStatement pstm = conn.prepareStatement(sql);	 
		 ResultSet rs = pstm.executeQuery();
		 List<Empresa> list = new ArrayList<Empresa>();
		 while (rs.next()) {
			 Empresa empresa = new Empresa();
			 empresa.setCif(rs.getString("cif"));
			 empresa.setName(rs.getString("nom"));
			 empresa.setDireccio(rs.getString("direccio"));
			 empresa.setCP(rs.getString("cp"));
			 empresa.setCiutat(rs.getString("ciutat"));
			 empresa.setProvincia(rs.getString("provincia"));
			 empresa.setTelefon(rs.getString("telefon"));
			 empresa.setFax(rs.getString("fax"));
			 empresa.setEmail(rs.getString("email"));
			 empresa.setPime(rs.getBoolean("pime"));
			 empresa.setActiva(rs.getBoolean("activa"));
			 list.add(empresa);
		 }
	     return list;
     }	
	 
	 public static List<Empresa> getDespesaEmpreses(Connection conn, Date dataIni, Date dataFi) throws SQLException {
		 String sql = "SELECT e.cif AS cif, e.nom AS nom, e.direccio AS direccio, e.ciutat AS ciutat, e.provincia AS provincia, e.email AS email, e.activa AS activa, SUM(o.pbase) AS pbase, SUM(o.plic) AS plic"
				 	+ " FROM public.tbl_empresaoferta o"
				 	+ " 	LEFT JOIN public.tbl_empreses e ON o.cifempresa = e.cif"
				 	+ " 	LEFT JOIN public.tbl_informeactuacio i ON o.idinforme = i.idinf"
				 	+ "		LEFT JOIN public.tbl_propostesinforme p ON p.idinf = i.idinf"
				 	+ " 	LEFT JOIN public.tbl_expedient x ON x.expcontratacio = i.expcontratacio"
				 	+ " WHERE x.anulat = false AND o.seleccionada = true AND p.seleccionada = true AND i.dataaprovacio >= '09/03/2018' AND o.datacre >= ? AND o.datacre <= ? AND ((o.pbase < 15000 AND p.tipusobra != 'obr') OR (o.pbase < 40000 AND p.tipusobra = 'obr'))"  
					+ " GROUP BY e.cif"
				 	+ " ORDER BY e.cif";
		 PreparedStatement pstm = conn.prepareStatement(sql);	 
		 pstm.setDate(1, new java.sql.Date(dataIni.getTime()));
		 pstm.setDate(2, new java.sql.Date(dataFi.getTime()));
		 ResultSet rs = pstm.executeQuery();
		 List<Empresa> list = new ArrayList<Empresa>();
		 System.out.println(pstm.toString());
		 while (rs.next()) {
			 Empresa empresa = new Empresa();
			 empresa.setCif(rs.getString("cif"));
			 empresa.setName(rs.getString("nom"));
			 empresa.setDireccio(rs.getString("direccio"));
			 empresa.setCiutat(rs.getString("ciutat"));
			 empresa.setProvincia(rs.getString("provincia"));
			 empresa.setEmail(rs.getString("email"));
			 empresa.setActiva(rs.getBoolean("activa"));
			 empresa.setTotalPbasePeriode(rs.getDouble("pbase"));
			 empresa.setTotalPLicPeriode(rs.getDouble("plic"));
			 empresa.setTotalPbasePeriodeAdjudicat(getTotalBaseAdjudicat(conn, rs.getString("cif"), dataIni, dataFi));
			 list.add(empresa);
		 }
		 sql = "SELECT e.cif AS cif, SUM(m.pbase) AS pbase, e.nom AS nom, e.direccio AS direccio, e.ciutat AS ciutat, e.provincia AS provincia, e.email AS email, e.activa AS activa, SUM(m.plic) AS plic"
				 + " FROM public.tbl_modificacioinforme m"
				 + " 	LEFT JOIN public.tbl_empreses e ON m.cifempresa = e.cif"
				 + " 	LEFT JOIN public.tbl_informeactuacio i ON m.idinforme = i.idinf"
				 + " 	LEFT JOIN public.tbl_empresaoferta o ON i.idinf = o.idinforme" 
				 + " 	LEFT JOIN public.tbl_propostesinforme p ON p.idinf = i.idinf"
				 + " 	LEFT JOIN public.tbl_expedient x ON x.expcontratacio = i.expcontratacio"
				 + " WHERE x.anulat = false AND o.seleccionada = true AND p.seleccionada = true AND i.dataaprovacio >= '09/03/2018' AND o.datacre >= ? AND o.datacre <= ? AND ((o.pbase < 15000 AND p.tipusobra != 'obr') OR (o.pbase < 40000 AND p.tipusobra = 'obr'))"
				 + " GROUP BY e.cif"
				 + " ORDER BY e.cif";		 		
		 pstm = conn.prepareStatement(sql);	 
		 pstm.setDate(1, new java.sql.Date(dataIni.getTime()));
		 pstm.setDate(2, new java.sql.Date(dataFi.getTime()));
		 rs = pstm.executeQuery();
		 System.out.println(pstm.toString());
		 while (rs.next()) {
			 Empresa empresa = new Empresa();
			 empresa.setCif(rs.getString("cif"));
			 empresa.setName(rs.getString("nom"));
			 empresa.setDireccio(rs.getString("direccio"));
			 empresa.setCiutat(rs.getString("ciutat"));
			 empresa.setProvincia(rs.getString("provincia"));
			 empresa.setEmail(rs.getString("email"));
			 empresa.setActiva(rs.getBoolean("activa"));
			 empresa.setTotalPbasePeriode(rs.getDouble("pbase"));
			 empresa.setTotalPLicPeriode(rs.getDouble("plic"));
			 empresa.setTotalPbasePeriodeAdjudicat(getTotalBaseAdjudicat(conn, rs.getString("cif"), dataIni, dataFi));
			 list.add(empresa);
		 }
	     return list;
  }	
	 
	 public static double getDespesaEmpresa(Connection conn, String cif, Date dataIni, Date dataFi) throws SQLException {
		 String sql = "SELECT SUM(o.pbase) AS pbase, SUM(o.plic) AS plic"
				 	+ " FROM public.tbl_empresaoferta o"
				 	+ " 	LEFT JOIN public.tbl_empreses e ON o.cifempresa = e.cif"
				 	+ " 	LEFT JOIN public.tbl_informeactuacio i ON o.idinforme = i.idinf"
				 	+ "		LEFT JOIN public.tbl_propostesinforme p ON p.idinf = i.idinf"
				 	+ " 	LEFT JOIN public.tbl_expedient x ON x.expcontratacio = i.expcontratacio"
				 	+ " WHERE e.cif = ? AND x.anulat = false AND o.seleccionada = true AND p.seleccionada = true AND i.dataaprovacio >= '09/03/2018' AND o.datacre >= ? AND o.datacre <= ? AND ((o.pbase < 15000 AND p.tipusobra != 'obr') OR (o.pbase < 40000 AND p.tipusobra = 'obr'))"  
					+ " GROUP BY e.cif"
				 	+ " ORDER BY e.cif";
		 PreparedStatement pstm = conn.prepareStatement(sql);	
		 pstm.setString(1, cif);
		 pstm.setDate(2, new java.sql.Date(dataIni.getTime()));
		 pstm.setDate(3, new java.sql.Date(dataFi.getTime()));
		 ResultSet rs = pstm.executeQuery();
		 double totalAdjudicat = 0;
		 if (rs.next()) totalAdjudicat = rs.getDouble("pbase");
		 sql = "SELECT e.cif AS cif, SUM(m.pbase) AS pbase, e.nom AS nom, e.direccio AS direccio, e.ciutat AS ciutat, e.provincia AS provincia, e.email AS email, e.activa AS activa, SUM(m.plic) AS plic"
				 + " FROM public.tbl_modificacioinforme m"
				 + " 	LEFT JOIN public.tbl_empreses e ON m.cifempresa = e.cif"
				 + " 	LEFT JOIN public.tbl_informeactuacio i ON m.idinforme = i.idinf"
				 + " 	LEFT JOIN public.tbl_empresaoferta o ON i.idinf = o.idinforme" 
				 + " 	LEFT JOIN public.tbl_propostesinforme p ON p.idinf = i.idinf"
				 + " 	LEFT JOIN public.tbl_expedient x ON x.expcontratacio = i.expcontratacio"
				 + " WHERE  e.cif = ? AND x.anulat = false AND o.seleccionada = true AND p.seleccionada = true AND i.dataaprovacio >= '09/03/2018' AND o.datacre >= ? AND o.datacre <= ? AND ((o.pbase < 15000 AND p.tipusobra != 'obr') OR (o.pbase < 40000 AND p.tipusobra = 'obr'))"
				 + " GROUP BY e.cif"
				 + " ORDER BY e.cif";		 		
		 pstm = conn.prepareStatement(sql);	 
		 pstm.setString(1, cif);
		 pstm.setDate(2, new java.sql.Date(dataIni.getTime()));
		 pstm.setDate(3, new java.sql.Date(dataFi.getTime()));
		 rs = pstm.executeQuery();
		 if (rs.next()) totalAdjudicat += rs.getDouble("pbase");
	     return totalAdjudicat;
  }	
	 
	 private static Empresa findSuccesora(Connection conn, String cif) throws SQLException, NamingException {
		 Empresa succesora = new Empresa();
		 String sql = "SELECT " + SQL_CAMPS
				 	+ " FROM public.tbl_empreses"
				 	+ " WHERE cif = ?";
		 PreparedStatement pstm = conn.prepareStatement(sql);
		 pstm.setString(1, cif);
		 ResultSet rs = pstm.executeQuery();
		 if (rs.next()) {
			 succesora = initEmpresa(conn, rs);
		 }
		 return succesora;
	 }
	 
	 public static void insertUTE(Connection conn, Empresa empresa, int idUsuari) throws SQLException {
		String sql = "INSERT INTO public.tbl_ute(cif, nom, empreses, datacre, usucre, activa)"
					+ " VALUES (?, ?, ?, localtimestamp, ?, true)";		 
		PreparedStatement pstm = conn.prepareStatement(sql);	 
		pstm.setString(1, empresa.getCif());
		pstm.setString(2, empresa.getName());
		pstm.setString(3, empresa.getUte().getEmpresesString());		
		pstm.setInt(4, idUsuari);		
		pstm.executeUpdate();
	 }
		
	 public static List<Empresa> getEmpresesUTE(Connection conn) throws SQLException {
		 String sql = "SELECT cif, nom, empreses, activa"
				 	+ " FROM public.tbl_ute";	 
		 PreparedStatement pstm = conn.prepareStatement(sql);	 
		 ResultSet rs = pstm.executeQuery();
		 List<Empresa> list = new ArrayList<Empresa>();
		 while (rs.next()) {
			 Empresa empresa = new Empresa();
			 empresa.setCif(rs.getString("cif"));
			 empresa.setName(rs.getString("nom"));
			 empresa.setActiva(rs.getBoolean("activa"));
			 Empresa.UTE ute = empresa.new UTE();		
			 empresa.setUte(ute);			 
			 list.add(empresa);
		 }
	     return list;
     }
	 
	 public static void updateEmpresaUTE(Connection conn, Empresa empresa, List<Empresa.Administrador> administradorsList, int idUsuari) throws SQLException {
		 String sql = "UPDATE public.tbl_ute"
				 	+ " SET nom=?, informacioadicional=?"
				 	+ " WHERE cif = ?";
		 PreparedStatement pstm = conn.prepareStatement(sql);	 		 
		 pstm.setString(1, empresa.getName());
		 pstm.setString(2, empresa.getInformacioAdicional());
		 pstm.setString(3, empresa.getCif());
		 pstm.executeUpdate();
	 }
	 
	 public static void addAdministrador(Connection conn, String cif, Empresa.Administrador administrador, int idUsuari) throws SQLException {
		 String sqlInsert = "INSERT INTO public.tbl_administradorsempresa(nifempresa, nom, dni, validfins, usucre, datacre, protocolmod, notarimod, datamod, tipus, datavalidacio, organvalidacio)"
			 			+ " VALUES (?, ?, ?, ?, ?, localtimestamp,?,?,?,?,?,?);";	 
		 PreparedStatement pstmInsert = null; 			 
		 pstmInsert = conn.prepareStatement(sqlInsert);	
		 pstmInsert.setString(1, cif);
		 pstmInsert.setString(2, administrador.getNom());
		 pstmInsert.setString(3, administrador.getDni());
		 if (administrador.getDataValidesaFins() != null) {
			 pstmInsert.setDate(4, new java.sql.Date(administrador.getDataValidesaFins().getTime()));
		 } else {
			 pstmInsert.setDate(4, null);
		 }				
		 pstmInsert.setInt(5, idUsuari);
		 pstmInsert.setString(6, administrador.getProtocolModificacio());
		 pstmInsert.setString(7, administrador.getNotariModificacio());
		 if (administrador.getDataModificacio() != null) {
			 pstmInsert.setDate(8, new java.sql.Date(administrador.getDataModificacio().getTime()));
		 } else {
			 pstmInsert.setDate(8, null);
		 }
		 pstmInsert.setString(9, administrador.getTipus());
		 if (administrador.getDataValidacio() != null) {
			 pstmInsert.setDate(10, new java.sql.Date(administrador.getDataValidacio().getTime()));
		 } else {
			 pstmInsert.setDate(10, null);
		 }				
		 pstmInsert.setString(11, administrador.getEntitatValidacio());
		 pstmInsert.executeUpdate();		
	 }
	 
	 public static void deleteAdministrador(Connection conn, String cif, String dniAdministrador, int idUsuari) throws SQLException, NamingException {
		 String sql = "DELETE FROM public.tbl_administradorsempresa"
				 	+ " WHERE nifempresa = ? AND dni = ?";
		 PreparedStatement pstm = conn.prepareStatement(sql); 
		 pstm.setString(1, cif);
		 pstm.setString(2, dniAdministrador);
		 pstm.executeUpdate();
		 Fitxer documentAdministrador = getDocumentAdministrador(conn, cif, dniAdministrador);
		 if (documentAdministrador.getRuta() != null && !documentAdministrador.getRuta().isEmpty())  Fitxers.eliminarFitxer(conn, idUsuari, documentAdministrador.getRuta());
	 }
	 
	 public static void updateAdministrador(Connection conn, String cif, String dniAdministrador, Administrador administrador) throws SQLException {
		 String sql = "UPDATE public.tbl_administradorsempresa"
				 	+ " SET nifempresa=?, nom=?, dni=?, validfins=?, protocolmod=?, notarimod=?, datamod=?, tipus=?, datavalidacio=?, organvalidacio=?"
				 	+ " WHERE dni = ? AND nifempresa = ?";
		 PreparedStatement pstm = conn.prepareStatement(sql); 
		 pstm.setString(1, cif);
		 pstm.setString(2, administrador.getNom());
		 pstm.setString(3, administrador.getDni());
		 if (administrador.getDataValidesaFins() != null) {
			 pstm.setDate(4, new java.sql.Date(administrador.getDataValidesaFins().getTime()));
		 } else {
			 pstm.setDate(4, null);
		 }	
		 pstm.setString(5, administrador.getProtocolModificacio());
		 pstm.setString(6, administrador.getNotariModificacio());
		 if (administrador.getDataModificacio() != null) {
			 pstm.setDate(7, new java.sql.Date(administrador.getDataModificacio().getTime()));
		 } else {
			 pstm.setDate(7, null);
		 }
		 pstm.setString(8, administrador.getTipus());
		 if (administrador.getDataValidacio() != null) {
			 pstm.setDate(9, new java.sql.Date(administrador.getDataValidacio().getTime()));
		 } else {
			 pstm.setDate(9, null);
		 }				
		 pstm.setString(10, administrador.getEntitatValidacio());
		 pstm.setString(11, dniAdministrador);
		 pstm.setString(12, cif);
		 pstm.executeUpdate();
	 }
	 
	 public static Administrador findAdministrador(Connection conn, String cif, String dniAdministrador) throws SQLException, NamingException {
		 Administrador administrador = new Empresa().new Administrador();
		 String sql = "SELECT nom, dni, validfins, protocolmod, notarimod, datamod, tipus, datavalidacio, organvalidacio"
				 	+ " FROM public.tbl_administradorsempresa"
				 	+ " WHERE nifempresa = ? AND dni = ?";
		 PreparedStatement pstm = conn.prepareStatement(sql); 
		 pstm.setString(1, cif);
		 pstm.setString(2, dniAdministrador);
		 ResultSet rs = pstm.executeQuery();
		 if (rs.next()) {
			 administrador = new Empresa().new Administrador();
			 administrador.setNom(rs.getString("nom"));
			 administrador.setDni(rs.getString("dni"));
			 administrador.setDataValidesaFins(rs.getTimestamp("validfins"));
			 administrador.setProtocolModificacio(rs.getString("protocolmod"));
			 administrador.setNotariModificacio(rs.getString("notarimod"));
			 administrador.setDataModificacio(rs.getTimestamp("datamod"));
			 administrador.setTipus(rs.getString("tipus"));
			 administrador.setDataValidacio(rs.getTimestamp("datavalidacio"));
			 administrador.setEntitatValidacio(rs.getString("organvalidacio"));
			 administrador.setDocumentAdministrador(getDocumentAdministrador(conn, cif, administrador.getDni()));
		 }
		 return administrador;
	 }
	 
	 private static List<Empresa.Administrador> getAdministradors(Connection conn, String cif) throws SQLException, NamingException {
		 List<Empresa.Administrador> administradorsList = new ArrayList<Empresa.Administrador>();
		 String sql = "SELECT nom, dni, validfins, protocolmod, notarimod, datamod, tipus, datavalidacio, organvalidacio"
				 	+ " FROM public.tbl_administradorsempresa"
				 	+ " WHERE nifempresa = ?"; 
		 PreparedStatement pstm = conn.prepareStatement(sql); 
		 pstm.setString(1, cif);
		 ResultSet rs = pstm.executeQuery();
		 Empresa.Administrador administrador = new Empresa().new Administrador();
		 while (rs.next()) {
			 administrador = new Empresa().new Administrador();
			 administrador.setNom(rs.getString("nom"));
			 administrador.setDni(rs.getString("dni"));
			 administrador.setDataValidesaFins(rs.getTimestamp("validfins"));
			 administrador.setProtocolModificacio(rs.getString("protocolmod"));
			 administrador.setNotariModificacio(rs.getString("notarimod"));
			 administrador.setDataModificacio(rs.getTimestamp("datamod"));
			 administrador.setTipus(rs.getString("tipus"));
			 administrador.setDataValidacio(rs.getTimestamp("datavalidacio"));
			 administrador.setEntitatValidacio(rs.getString("organvalidacio"));
			 administrador.setDocumentAdministrador(getDocumentAdministrador(conn, cif, administrador.getDni()));
			 administradorsList.add(administrador);
		 }
		 sql = "SELECT b.ref AS ref, b.databastanteo AS databastanteo, b.empresa AS empresa, b.personafacultada AS personafacultada, b.carrec AS carrec, b.anybastanteo AS anybastanteo, e.refescritura AS refescritura, e.escritura AS escritura, e.dataescritura AS dataescritura, e.nprotocol AS nprotocol, e.notari AS notari"
					+ " FROM public.tbl_bastanteos b LEFT JOIN public.tbl_escritura e ON b.ref = e.refbastanteo"
					+ " WHERE b.empresa = ?";
	 
		 pstm = conn.prepareStatement(sql);
		 pstm.setString(1, cif);		
		 rs = pstm.executeQuery();
		 while (rs.next()) {
			 administrador = new Empresa().new Administrador();
			 administrador.setNom(rs.getString("personafacultada"));
			 administrador.setDni(rs.getString("ref"));
			 //administrador.setDataValidesaFins(rs.getTimestamp("validfins"));
			 administrador.setProtocolModificacio(rs.getString("nprotocol"));
			 administrador.setNotariModificacio(rs.getString("notari"));
			 administrador.setDataModificacio(rs.getTimestamp("dataescritura"));
			 administrador.setTipus(rs.getString("escritura"));
			 administrador.setDataValidacio(rs.getTimestamp("databastanteo"));
			 administrador.setEntitatValidacio("ibisec");
			 //administrador.setDocumentAdministrador(getDocumentAdministrador(conn, cif, administrador.getDni()));
			 administradorsList.add(administrador);
		 }
		 return administradorsList;
	 }
	 
	 private static Fitxer getDocumentAdministrador(Connection conn, String cif, String cifAdministrador) throws NamingException{
		 Fitxer fitxer = new Fitxer();
		 // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		 File dir = new File(ruta + "/documents/Empreses/" + cif + "/Administrador/" + cifAdministrador);
		 File[] fichers = dir.listFiles();
		 if (fichers == null) {
			
		 } else { 			 
			 for (int x=0;x<fichers.length;x++) {
				 fitxer = new Fitxer();
				 fitxer.setNom(fichers[x].getName());
				 fitxer.setRuta(ruta + "/documents/Empreses/" + cif + "/Administrador/" + cifAdministrador +"/" + fichers[x].getName());
			 }
		 }
		 
		 return fitxer;
	 }
	 
	 private static String getAdministradorsString(Connection conn, String cif) throws SQLException {
		String administradorsList = "";
		 String sql = "SELECT nom, dni, validfins, protocolmod, notarimod, datamod, tipus, datavalidacio, organvalidacio"
				 	+ " FROM public.tbl_administradorsempresa"
				 	+ " WHERE nifempresa = ?"; 
		 PreparedStatement pstm = conn.prepareStatement(sql); 
		 pstm.setString(1, cif);
		 ResultSet rs = pstm.executeQuery();
		 DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		 while (rs.next()) {	
			 String validfins = "";
			 String datamod = "";
			 String datavalidacio = "";
			 if (rs.getDate("validfins") != null) validfins = df.format(rs.getDate("validfins"));
			 if (rs.getDate("datamod") != null) datamod = df.format(rs.getDate("datamod"));
			 if (rs.getDate("datavalidacio") != null) datavalidacio = df.format(rs.getDate("datavalidacio"));
			 administradorsList += rs.getString("nom") + "#" + rs.getString("dni") + "#" + rs.getString("tipus") + "#" + validfins
			 						+ "#" + rs.getString("notarimod") + "#" + rs.getString("protocolmod") + "#" + datamod + "#" + datavalidacio + "#" + rs.getString("organvalidacio") + ";";
		 }
		 return administradorsList;
	 }
	
	 public static List<Fitxer> getEscritures(String cif) throws NamingException {
		 List<Fitxer> fitxersList = new ArrayList<Fitxer>();
		 // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		 File dir = new File(ruta + "/documents/Empreses/" + cif + "/Escritura");
		 File[] fichers = dir.listFiles();
		 Fitxer fitxer = new Fitxer();
		 if (fichers == null) {
			
		 } else { 
			 for (int x=0;x<fichers.length;x++) {
				 fitxer = new Fitxer();
				 fitxer.setNom(fichers[x].getName());
				 fitxer.setRuta(ruta + "/documents/Empreses/" + cif + "/Escritura/" + fichers[x].getName());
				 fitxersList.add(fitxer);
			 }
		 }
		 return fitxersList;
	 }
	 
	 public static List<Fitxer> getDocumentsProhibicioContractar(String cif) throws NamingException {
		 List<Fitxer> fitxersList = new ArrayList<Fitxer>();
		 // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		 File dir = new File(ruta + "/documents/Empreses/" + cif + "/Prohibicio");
		 File[] fichers = dir.listFiles();
		 Fitxer fitxer = new Fitxer();
		 if (fichers == null) {
			
		 } else { 
			 for (int x=0;x<fichers.length;x++) {
				 fitxer = new Fitxer();
				 fitxer.setNom(fichers[x].getName());
				 fitxer.setRuta(ruta + "/documents/Empreses/" + cif + "/Prohibicio/" + fichers[x].getName());
				 fitxersList.add(fitxer);
			 }
		 }
		 return fitxersList;
	 }
	 
	 public static Fitxer getDocumentREA(String cif) throws NamingException {
		 Fitxer fitxer = new Fitxer();
		 // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		 File dir = new File(ruta + "/documents/Empreses/" + cif + "/REA");
		 File[] fichers = dir.listFiles();
		 if (fichers == null) {
			
		 } else { 
			 for (int x=0;x<fichers.length;x++) {
				 fitxer = new Fitxer();
				 fitxer.setNom(fichers[x].getName());
				 fitxer.setRuta(ruta + "/documents/Empreses/" + cif + "/REA/" + fichers[x].getName());
			 }
		 }
		 return fitxer;
	 }
	 
	 public static Fitxer getClassificacioROLECE(String cif) throws NamingException {
		 Fitxer fitxer = new Fitxer();
		 // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		 File dir = new File(ruta + "/documents/Empreses/" + cif + "/Classificacio/ROLECE");
		 File[] fichers = dir.listFiles();
		 if (fichers == null) {
			
		 } else { 
			 for (int x=0;x<fichers.length;x++) {
				 fitxer = new Fitxer();
				 fitxer.setNom(fichers[x].getName());
				 fitxer.setRuta(ruta + "/documents/Empreses/" + cif + "/Classificacio/ROLECE/" + fichers[x].getName());
			 }
		 }
		 return fitxer;
	 } 
	 
	 public static Fitxer getClassificacioJCCaib(String cif) throws NamingException {
		 Fitxer fitxer = new Fitxer();
		 // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		 File dir = new File(ruta + "/documents/Empreses/" + cif + "/Classificacio/JCCaib");
		 File[] fichers = dir.listFiles();
		 if (fichers == null) {
			
		 } else { 
			 for (int x=0;x<fichers.length;x++) {
				 fitxer = new Fitxer();
				 fitxer.setNom(fichers[x].getName());
				 fitxer.setRuta(ruta + "/documents/Empreses/" + cif + "/Classificacio/JCCaib/" + fichers[x].getName());
			 }
		 }
		 return fitxer;
	 } 
	 
	 public static Fitxer getClassificacioJCA(String cif) throws NamingException {
		 Fitxer fitxer = new Fitxer();
		 // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		 File dir = new File(ruta + "/documents/Empreses/" + cif + "/Classificacio/JCA");
		 File[] fichers = dir.listFiles();
		 if (fichers == null) {
			
		 } else { 
			 for (int x=0;x<fichers.length;x++) {
				 fitxer = new Fitxer();
				 fitxer.setNom(fichers[x].getName());
				 fitxer.setRuta(ruta + "/documents/Empreses/" + cif + "/Classificacio/JCA/" + fichers[x].getName());
			 }
		 }
		 return fitxer;
	 } 
	 
	 public static Fitxer getSolEconomica(String cif) throws NamingException {
		 Fitxer fitxer = new Fitxer();
		 // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		 File dir = new File(ruta + "/documents/Empreses/" + cif + "/Sol Economica");
		 File[] fichers = dir.listFiles();
		 if (fichers == null) {
			
		 } else { 
			 for (int x=0;x<fichers.length;x++) {
				 fitxer = new Fitxer();
				 fitxer.setNom(fichers[x].getName());
				 fitxer.setRuta(ruta + "/documents/Empreses/" + cif + "/Sol Economica/" + fichers[x].getName());
			 }
		 }
		 return fitxer;
	 }
	 
	 public static Fitxer getSolTecnica(String cif) throws NamingException {
		 Fitxer fitxer = new Fitxer();
		 // Get the base naming context
		    Context env = (Context)new InitialContext().lookup("java:comp/env");
		    // Get a single value
			String ruta =  (String)env.lookup("ruta_base");
		 File dir = new File(ruta + "/documents/Empreses/" + cif + "/Sol Tecnica");
		 File[] fichers = dir.listFiles();
		 if (fichers == null) {
			
		 } else { 
			 for (int x=0;x<fichers.length;x++) {
				 fitxer = new Fitxer();
				 fitxer.setNom(fichers[x].getName());
				 fitxer.setRuta(ruta + "/documents/Empreses/" + cif + "/Sol Tecnica/" + fichers[x].getName());
			 }
		 }
		 return fitxer;
	 }
	 
	 public static Fitxer getExtincioFile(String cif) throws NamingException {
		 Fitxer fitxer = new Fitxer();
		 // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		 File dir = new File(ruta + "/documents/Empreses/" + cif + "/Extincio");
		 File[] fichers = dir.listFiles();
		 if (fichers == null) {
			
		 } else { 
			 for (int x=0;x<fichers.length;x++) {
				 fitxer = new Fitxer();
				 fitxer.setNom(fichers[x].getName());
				 fitxer.setRuta(ruta + "/documents/Empreses/" + cif + "/Extincio/" + fichers[x].getName());
			 }
		 }
		 return fitxer;
	 } 
	 
	 public static Fitxer getSuccesioFile(String cif) throws NamingException {
		 Fitxer fitxer = new Fitxer();
		 // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
		String ruta =  (String)env.lookup("ruta_base");
		 File dir = new File(ruta + "/documents/Empreses/" + cif + "/Succesio");
		 File[] fichers = dir.listFiles();
		 if (fichers == null) {
			
		 } else { 
			 for (int x=0;x<fichers.length;x++) {
				 fitxer = new Fitxer();
				 fitxer.setNom(fichers[x].getName());
				 fitxer.setRuta(ruta + "/documents/Empreses/" + cif + "/Succesio/" + fichers[x].getName());
			 }
		 }
		 return fitxer;
	 } 
	 
	 public static void guardarFitxer(Connection conn, int idUsuari, List<Fitxer> fitxers, String cif, String cifAdministrador) throws NamingException{		
			if (!fitxers.isEmpty()) {
				String fileName = "";
				// Crear directoris si no existeixen
				 // Get the base naming context
			    Context env = (Context)new InitialContext().lookup("java:comp/env");
			    // Get a single value
				String ruta =  (String)env.lookup("ruta_base");
				File tmpFile = new File(ruta + "/documents/Empreses");
				if (!tmpFile.exists()) {
					tmpFile.mkdir();
				}
				tmpFile = new File(ruta + "/documents/Empreses/" + cif);
				if (!tmpFile.exists()) {
					tmpFile.mkdir();
				}
		        for(int i=0;i<fitxers.size();i++){		        	
		            Fitxer fitxer = (Fitxer) fitxers.get(i);
		            if ("classificacioROLECE".equals(fitxer.getNomCamp())) {
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/Classificacio");
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/Classificacio/ROLECE");
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						fileName = ruta + "/documents/Empreses/" + cif + "/Classificacio/ROLECE/";
					}
		            if ("classificacioJCCaib".equals(fitxer.getNomCamp())) {
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/Classificacio");
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/Classificacio/JCCaib");
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						fileName = ruta + "/documents/Empreses/" + cif + "/Classificacio/JCCaib/";
					}
		            if ("classificacioJCA".equals(fitxer.getNomCamp())) {
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/Classificacio");
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/Classificacio/JCA");
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						fileName = ruta + "/documents/Empreses/" + cif + "/Classificacio/JCA/";
					}
		            if ("fileEconomica".equals(fitxer.getNomCamp())) {
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/Sol Economica");
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						fileName = ruta + "/documents/Empreses/" + cif + "/Sol Economica/";
					}
					if ("FileTecnica".equals(fitxer.getNomCamp())) {
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/Sol Tecnica");
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						fileName = ruta + "/documents/Empreses/" + cif + "/Sol Tecnica/";
					}
					if (("fileEscritura").equals(fitxer.getNomCamp())) {
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/Escritura");
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						fileName = ruta + "/documents/Empreses/" + cif + "/Escritura/";
					}
					if (("fileREA").equals(fitxer.getNomCamp())) {
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/REA");
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						fileName = ruta + "/documents/Empreses/" + cif + "/REA/";
					}
					if (("fileAdministrador").equals(fitxer.getNomCamp())) {
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/Administrador");
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/Administrador/" + cifAdministrador);
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						fileName = ruta + "/documents/Empreses/" + cif + "/Administrador/" + cifAdministrador + "/";
					}
					if (("documentextincio").equals(fitxer.getNomCamp())) {
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/Extincio");
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						fileName = ruta + "/documents/Empreses/" + cif + "/Extincio/";
					}
					if (("documentsuccessio").equals(fitxer.getNomCamp())) {
						tmpFile = new File(ruta + "/documents/Empreses/" + cif + "/Successio");
						if (!tmpFile.exists()) {
							tmpFile.mkdir();
						}
						fileName = ruta + "/documents/Empreses/" + cif + "/Successio/";
					}
		            if (fitxer.getFitxer().getName() != "") {	
		            	File archivo_server = new File(fileName + fitxer.getFitxer().getName());
		               	try {
		               		fitxer.getFitxer().write(archivo_server);
		               		Fitxers.guardarRegistreFitxer(conn, fitxer.getFitxer().getName(), fileName + fitxer.getFitxer().getName(), idUsuari);
		           		} catch (Exception e) {
		           			e.printStackTrace();
		           		}
		            } 
		        }
			}
		}
	 
	 private static double getTotalBaseAdjudicat(Connection conn, String cif, Date dataIni, Date dataFi) throws SQLException {
		 double total = 0;
		 String sql = "SELECT SUM(o.pbase) AS total"
				 	+ " FROM public.tbl_empresaoferta o"
				 	+ " 	LEFT JOIN public.tbl_empreses e ON o.cifempresa = e.cif"
				 	+ " 	LEFT JOIN public.tbl_informeactuacio i ON o.idinforme = i.idinf"
				 	+ "		LEFT JOIN public.tbl_propostesinforme p ON p.idinf = i.idinf"
				 	+ " 	LEFT JOIN public.tbl_expedient x ON x.expcontratacio = i.expcontratacio"
				 	+ " WHERE x.anulat = false  AND o.seleccionada = true AND p.seleccionada = true AND (x.dataadjudicacio IS NOT null OR x.dataformalitzaciocontracte IS NOT null) AND i.dataaprovacio >= '09/03/2018' AND o.datacre >= ? AND o.datacre <= ? AND ((o.pbase < 15000 AND p.tipusobra != 'obr') OR (o.pbase < 40000 AND p.tipusobra = 'obr')) AND cif = ?";
		 PreparedStatement pstm = conn.prepareStatement(sql);	
		 pstm.setDate(1, new java.sql.Date(dataIni.getTime()));
		 pstm.setDate(2, new java.sql.Date(dataFi.getTime()));
		 pstm.setString(3, cif);
		 ResultSet rs = pstm.executeQuery();
		 System.out.println(pstm.toString());
		 if (rs.next()) {
			 total = rs.getDouble("total");
		 }
		 sql = "SELECT SUM(m.pbase) AS total"
				 + " FROM public.tbl_modificacioinforme m"
				 + "    LEFT JOIN public.tbl_informeactuacio i ON m.idinforme = i.idinf"	
				 + "	LEFT JOIN public.tbl_propostesinforme p ON p.idinf = i.idinf"
				 + " 	LEFT JOIN public.tbl_empresaoferta o ON  i.idinf = o.idinforme"
				 + " WHERE i.dataaprovacio >= '09/03/2018' AND o.seleccionada = true AND p.seleccionada = true AND m.datacre >= ? AND m.datacre <= ? AND ((o.pbase < 15000 AND p.tipusobra != 'obr') OR (o.pbase < 40000 AND p.tipusobra = 'obr')) AND m.cifempresa = ?"; 
		 pstm = conn.prepareStatement(sql);	
		 pstm.setDate(1, new java.sql.Date(dataIni.getTime()));
		 pstm.setDate(2, new java.sql.Date(dataFi.getTime()));
		 pstm.setString(3, cif);
		 rs = pstm.executeQuery();
		 System.out.println(pstm.toString());
		 if (rs.next()) {
			 total += rs.getDouble("total");
		 }		
		 return total;
	 }
	 
}
