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

import bean.ReservaVehicle;
import bean.ReservaVehicle.Reserva;
import bean.User;
import bean.Vacances;
import bean.Vacances.Reserves;

public class CalendarCore {
	public static ReservaVehicle getSetmana(Connection conn, Calendar cal, String vehicle) {
		ReservaVehicle setmana = new ReservaVehicle();		     
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        setmana.setSetmana(week);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);       
        setmana.setDilluns(cal.get(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.DATE, 1);
        setmana.setDimarts(cal.get(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.DATE, 1);
        setmana.setDimecres(cal.get(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.DATE, 1);
        setmana.setDijous(cal.get(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.DATE, 1);
        setmana.setDivendres(cal.get(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.DATE, 1);
        setmana.setDissabte(cal.get(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.DATE, 1);
        setmana.setDiumenge(cal.get(Calendar.DAY_OF_MONTH));
        
        setmana.setReservesDilluns(getReserves(conn, week, Calendar.MONDAY, vehicle));
        setmana.setReservesDimarts(getReserves(conn, week, Calendar.TUESDAY, vehicle));
        setmana.setReservesDimecres(getReserves(conn, week, Calendar.WEDNESDAY, vehicle));
        setmana.setReservesDijous(getReserves(conn, week, Calendar.THURSDAY, vehicle));
        setmana.setReservesDivendres(getReserves(conn, week, Calendar.FRIDAY, vehicle));        
      
		return setmana;
	}
	
	public static String getDiesPossibles() {
		String dies = "";
		Calendar cal = Calendar.getInstance();	 
		for (int i = 0; i <= 5; i++) {		
			System.out.println("ENTRA: " + i);
			if (cal.get(Calendar.DAY_OF_WEEK) == 7) {
				cal.add(Calendar.DATE, 2);
			}
			dies += "<option value='" + cal.get(Calendar.DAY_OF_WEEK) + "#" + cal.get(Calendar.WEEK_OF_YEAR) + "#" + cal.get(Calendar.YEAR) + "'>" + getDiaSetmana(cal.get(Calendar.DAY_OF_WEEK)) + " " + cal.get(Calendar.DAY_OF_MONTH) + "</option>";
			cal.add(Calendar.DATE, 1);
		}
		return dies;
	}
	
	public static boolean potReservar(Connection conn, int idUsuari, String vehicle, int setmana, int dia, int year, int horaIni, int horaFi) throws SQLException {
		boolean potReservar = true;
		String sql = "SELECT setmana"
				+ " FROM public.tbl_vehicles"
				+ " WHERE setmana = ? AND dia = ? AND hora >= ? AND hora <= ? AND year = ? AND vehicle = ? LIMIT 1";
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, setmana);	
		pstm.setInt(2, dia);
		pstm.setInt(3, horaIni);
		if (horaFi < horaIni) horaFi = horaIni;
		pstm.setInt(4, horaFi);
		pstm.setInt(5, year);
		pstm.setString(6, vehicle);
		ResultSet rs = pstm.executeQuery();
		System.out.println(pstm.toString());
		if (rs.next()) potReservar = false;
		sql = "SELECT setmana"
				+ " FROM public.tbl_vehicles"
				+ " WHERE setmana = ? AND year = ? AND idusuari = ? LIMIT 1";
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, setmana);	
		pstm.setInt(2, year);
		pstm.setInt(3, idUsuari);
		rs = pstm.executeQuery();
		if (rs.next()) potReservar = false;
		return potReservar;
	}
	
	public static void reservar(Connection conn, int idUsuari, String vehicle, int setmana, int dia, int any, int horaIni, int horaFi, String motiu) throws SQLException {
		String sql = "INSERT INTO public.tbl_vehicles(setmana, dia, hora, idusuari, motiu, vehicle, year)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstm;
		int cont = horaIni + 1;
		while (cont < horaFi) {
			sql += ",(?, ?, ?, ?, ?, ?, ?)";
			cont += 1;
		}		
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, setmana);
		pstm.setInt(2, dia);
		pstm.setInt(3, horaIni);
		pstm.setInt(4, idUsuari);
		pstm.setString(5, motiu);
		pstm.setString(6, vehicle);
		pstm.setInt(7, any);
		cont = horaIni + 1;
		int numVar = 8;
		while (cont < horaFi) {
			pstm.setInt(numVar, setmana);
			pstm.setInt(numVar + 1, dia);
			pstm.setInt(numVar + 2, cont);
			pstm.setInt(numVar + 3, idUsuari);
			pstm.setString(numVar + 4, motiu);
			pstm.setString(numVar + 5, vehicle);
			pstm.setInt(numVar + 6, any);
			cont += 1;
			numVar += 7;
		}		
		pstm.executeUpdate();
	}
	
	public static void eliminarReserva(Connection conn, int any, int setmana, int dia, String vehicle, int idusuari) throws SQLException {
		String sql = "DELETE FROM public.tbl_vehicles"
				+ " WHERE setmana = ? AND year = ? AND dia = ? AND vehicle = ? AND idusuari = ?";
 
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, setmana);
		pstm.setInt(2, any);
		pstm.setInt(3, dia);
		pstm.setString(4, vehicle);
		pstm.setInt(5, idusuari);
		pstm.executeUpdate();
	}
	
	public static List<Reserva> getReservesUsuari(Connection conn, int setmana, int idUsuari) throws SQLException {
		List<Reserva> reservesList = new ArrayList<Reserva>();
		String sql = "SELECT setmana, dia, hora, idusuari, motiu, vehicle, year"
				+ " FROM public.tbl_vehicles"
				+ " WHERE (setmana = ? OR setmana = ?)  AND idusuari = ?";
 
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, setmana);	
		pstm.setInt(2, setmana + 1);	
		pstm.setInt(3, idUsuari);
		ResultSet rs = pstm.executeQuery();
		Reserva reserva = new ReservaVehicle().new Reserva();
		while (rs.next()) {			
			reserva = new ReservaVehicle().new Reserva();
			reserva.setMotiu(rs.getString("motiu"));
			reserva.setUsuari(UsuariCore.findUsuariByID(conn, rs.getInt("idusuari")));
			reserva.setSetmana(rs.getInt("setmana"));
			reserva.setDia(rs.getInt("dia"));
			reserva.setHora(rs.getInt("hora"));
			reserva.setVehicle(rs.getString("vehicle"));
			reserva.setYear(rs.getInt("year"));
			reservesList.add(reserva);
		}
		return reservesList;
	}
	
	public static String pintarReservesUsuari(List<Reserva> reserves) {
		String reservesHTML = "";
		String reservaActual = "";
		int diaAnterior = -1;
		String horaFinal = "";
		Calendar date = Calendar.getInstance();
		String horesFormat = "";
		String vehicle = "";
		Reserva reservaAnt = null;
		for (Reserva reserva: reserves) {			
			if (diaAnterior != reserva.getDia()) { // Nova reserva
				if (!reservaActual.isEmpty()) { // acabam la reserva anterior
					reservesHTML += "<span>" + reservaActual + horaFinal + "</span>";
					reservesHTML += "<span data-any=" + reserva.getYear() + " data-setmana=" + reserva.getSetmana() + " data-dia=" + reserva.getDia() + " data-vehicle=" + reserva.getVehicle() + " data-idusuari=" + reserva.getUsuari().getIdUsuari() + " class='glyphicon glyphicon-remove deleteReservaVehicle'></span></br>"; 
				}
				date = Calendar.getInstance();
				date.set(Calendar.YEAR, reserva.getYear());
				date.set(Calendar.WEEK_OF_YEAR, reserva.getSetmana());
				date.set(Calendar.DAY_OF_WEEK, reserva.getDia());
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");					
				if (reserva.getVehicle().equals("cotxe")) {
					horesFormat = ReservaVehicle.horesCotxe[reserva.getHora()-1];
					vehicle = " del cotxe";
				} else if (reserva.getVehicle().equals("cotxeElectric")) {
					horesFormat = ReservaVehicle.horesCotxeElectric[reserva.getHora()-1];
					vehicle = " del cotxe elèctric";
				} else {
					horesFormat = ReservaVehicle.horesFurgoneta[reserva.getHora()-1];
					vehicle = " de la furgoneta";
				}
				reservaActual = "Reserva dia " + df.format(date.getTime()) + vehicle + " de " + horesFormat + " a ";				
				diaAnterior = reserva.getDia();
			}
			if (reserva.getVehicle().equals("cotxe")) {
				horaFinal = ReservaVehicle.horesCotxe[reserva.getHora()];
			} else if (reserva.getVehicle().equals("cotxeElectric")) {
				horaFinal = ReservaVehicle.horesCotxeElectric[reserva.getHora()];
			} else{
				horaFinal = ReservaVehicle.horesFurgoneta[reserva.getHora()];
			}
			reservaAnt = reserva;
		}	
		if (!reservaActual.isEmpty()) { // acabam la reserva anterior
			reservesHTML += "<span>" + reservaActual + horaFinal + "  </span>";
			reservesHTML += "<span data-any=" + reservaAnt.getYear() + " data-setmana=" + reservaAnt.getSetmana() + " data-dia=" + reservaAnt.getDia() + " data-vehicle=" + reservaAnt.getVehicle() + " data-idusuari=" + reservaAnt.getUsuari().getIdUsuari() + " class='glyphicon glyphicon-remove deleteReservaVehicle'></span></br>"; 
		}
		return reservesHTML;
	}
	
	private static Reserva[] getReserves(Connection conn, int setmana, int dia, String vehicle) {
		Reserva[] reserves = new Reserva[19];
		String sql = "SELECT setmana, dia, hora, idusuari, motiu, vehicle"
				+ " FROM public.tbl_vehicles"
				+ " WHERE setmana = ? AND dia = ? AND vehicle = ?";
 
		PreparedStatement pstm;
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, setmana);	
			pstm.setInt(2, dia);	
			pstm.setString(3, vehicle);
			ResultSet rs = pstm.executeQuery();
			int hora = 1;
			Reserva reserva = new ReservaVehicle().new Reserva();
			while (rs.next()) {
				hora = rs.getInt("hora");
				reserva = new ReservaVehicle().new Reserva();
				reserva.setMotiu(rs.getString("motiu"));
				reserva.setUsuari(UsuariCore.findUsuariByID(conn, rs.getInt("idusuari")));
				reserves[hora -1] = reserva;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return reserves;
	}
	
	private static String getDiaSetmana(int diaSetmana) {
		String dia = "";
		switch (diaSetmana) {
			case 2:
				dia = "dilluns";
				break;
			case 3:
				dia = "dimarts";
				break;
			case 4:
				dia = "dimecres";
				break;
			case 5:
				dia = "dijous";
				break;
			case 6:
				dia = "divendres";
				break;
		}
		return dia;
	}
	
	public static Integer[] getDiesMes(Calendar cal) {
		Integer[] diesMes = new Integer[37];
		int primerDia = cal.get(Calendar.DAY_OF_WEEK);
		int diaFinalMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		switch (primerDia) {
			case 1: 
				primerDia = 6;
				break;
			case 2: 
				primerDia = 0;
				break;
			case 3: 
				primerDia = 1;
				break;
			case 4: 
				primerDia = 2;
				break;
			case 5: 
				primerDia = 3;
				break;
			case 6: 
				primerDia = 4;
				break;
			case 7: 
				primerDia = 5;
				break;
		}
		diesMes[primerDia] = 1;
		int posicioDiaActual = primerDia + 1;
		int diaActual = 2;
		while (posicioDiaActual < 37 && diaActual <= diaFinalMes) { 
			diesMes[posicioDiaActual] = diaActual;
			diaActual++;
			posicioDiaActual++;    
	    }     
		return diesMes;
	}
	
	public static List<Reserves> getVacances(Connection conn, String departament) throws SQLException {
		List<Reserves> reservesList = new ArrayList<Reserves>();
		String sql = "SELECT idusuari, tipus, inici, fi, motiu, vistiplau, autoritzacio, rebutjarautoritzacio, rebutjarvistiplau"
				+ " FROM public.tbl_vacances"
				+ " WHERE departament = ?";
 
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setString(1, departament);
		ResultSet rs = pstm.executeQuery();
		Reserves reserva = new Vacances().new Reserves();
		while (rs.next()) {
			reserva = new Vacances().new Reserves();
			reserva.setIdUsuari(rs.getInt("idusuari"));
			reserva.setTipus(rs.getString("tipus"));
			reserva.setDataInici(rs.getTimestamp("inici"));
			reserva.setDataFi(rs.getTimestamp("fi"));
			reserva.setMotiu(rs.getString("motiu"));
			reserva.setVistiplau(rs.getTimestamp("vistiplau"));
			reserva.setAutoritzacio(rs.getTimestamp("autoritzacio"));
			reserva.setRebutjarAutoritzacio(rs.getTimestamp("rebutjarautoritzacio"));
			reserva.setRebutjarVistiplau(rs.getTimestamp("rebutjarvistiplau"));
			reservesList.add(reserva);
		}
		return reservesList;
	}
	
	public static List<Reserves> vacancesList(Connection conn, int idUsuari, String estat, Date dataInici,  Date dataFi) throws SQLException {
		List<Reserves> reservesList = new ArrayList<Reserves>();
		String sql = "SELECT v.idusuari AS usuariid, u.nom AS nom, u.cognoms AS cognoms, tipus, inici, fi, motiu, vistiplau, autoritzacio, rebutjarautoritzacio, rebutjarvistiplau"
					+ " FROM public.tbl_vacances v LEFT JOIN public.tbl_usuaris u ON v.idusuari = u.idusuari";
		boolean primeraCondicio = true;
		if (idUsuari > -1) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE v.idusuari = ?";
			}			
		}
		if (estat != null && ! estat.equals("-1")) {
			if (primeraCondicio) {
				primeraCondicio = false;
				if ("Aprovades".equals(estat)) sql+= " WHERE autoritzacio IS NOT NULL"; 
				if ("Vistiplau".equals(estat)) sql+= " WHERE vistiplau IS NOT NULL"; 				
			} else {
				if ("Aprovades".equals(estat)) sql+= " AND autoritzacio IS NOT NULL"; 
				if ("Vistiplau".equals(estat)) sql+= " AND vistiplau IS NOT NULL"; 				
			}			
		}			
		if (dataInici != null) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE inici >= ?";
			} else {
				sql += " and inici >= ?";
			}			
		}
		if (dataInici != null) {
			if (primeraCondicio) {
				primeraCondicio = false;
				sql += " WHERE fi <= ?";
			} else {
				sql += " and fi <= ?";
			}			
		}
		sql += " ORDER BY inici DESC, fi DESC";
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		
		int contVars = 1;
		if (idUsuari > -1) {
			pstm.setInt(contVars, idUsuari);
			contVars += 1;
		}	
		if (dataInici != null) {
			pstm.setDate(contVars, new java.sql.Date(dataInici.getTime()));
			contVars += 1;
		}
		if (dataFi != null) {
			pstm.setDate(contVars, new java.sql.Date(dataFi.getTime()));
			contVars += 1;
		}
			
		ResultSet rs = pstm.executeQuery();
		Reserves reserva = new Vacances().new Reserves();
		while (rs.next()) {
			reserva = new Vacances().new Reserves();
			reserva.setIdUsuari(rs.getInt("usuariid"));
			reserva.setNomUsuari(rs.getString("nom") + " " + rs.getString("cognoms"));
			reserva.setTipus(rs.getString("tipus"));
			reserva.setDataInici(rs.getTimestamp("inici"));
			reserva.setDataFi(rs.getTimestamp("fi"));
			reserva.setMotiu(rs.getString("motiu"));
			reserva.setVistiplau(rs.getTimestamp("vistiplau"));
			reserva.setAutoritzacio(rs.getTimestamp("autoritzacio"));
			reserva.setRebutjarAutoritzacio(rs.getTimestamp("rebutjarautoritzacio"));
			reserva.setRebutjarVistiplau(rs.getTimestamp("rebutjarvistiplau"));
			reservesList.add(reserva);
		}
		return reservesList;
	}
	
	public static boolean quedenDies(Connection conn, int idUsuari, Vacances vacances, String tipus, Date dataInici, Date dataFi) throws SQLException {
		boolean quedenDies = false;
		int totalDies = getDiesHabils(dataInici, dataFi, vacances, idUsuari);
		if (getDiesDisponibles(conn, idUsuari, vacances, tipus) >= totalDies) quedenDies = true;
		return quedenDies;
	}
	
	public static int reservarVacances(Connection conn, int idUsuari, String tipus, Date dataInici, Date dataFi, String motiu, String departament) throws SQLException {
		String sql = "INSERT INTO public.tbl_vacances(idsolicitud, idusuari, tipus, inici, fi, motiu, departament)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement pstm;		
		pstm = conn.prepareStatement(sql);
		int codiSolicitud = getNewReservaCode(conn);
		pstm.setInt(1, codiSolicitud);
		pstm.setInt(2, idUsuari);
		pstm.setString(3, tipus);
		pstm.setDate(4, new java.sql.Date(dataInici.getTime()));
		pstm.setDate(5, new java.sql.Date(dataFi.getTime()));
		pstm.setString(6, motiu);
		pstm.setString(7,  departament);
		pstm.executeUpdate();
		return codiSolicitud;
	}
	
	public static Reserves getSolicitudVacances(Connection conn, int idSolicitud) throws SQLException {
		Reserves reserva = new Vacances().new Reserves();
		String sql = "SELECT idsolicitud, idusuari, tipus, inici, fi, motiu, departament, vistiplau, autoritzacio"
					+ " FROM public.tbl_vacances"
					+ " WHERE idsolicitud = ?";
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, idSolicitud);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			reserva.setIdUsuari(rs.getInt("idUsuari"));
			reserva.setTipus(rs.getString("tipus"));
			reserva.setDataInici(rs.getTimestamp("inici"));
			reserva.setDataFi(rs.getTimestamp("fi"));
			reserva.setMotiu(rs.getString("motiu"));
			reserva.setDepartament(rs.getString("departament"));
			reserva.setVistiplau(rs.getTimestamp("vistiplau"));
			reserva.setAutoritzacio(rs.getTimestamp("autoritzacio"));
		}
		return reserva;
	}
	
	public static void aprovarAutoritzacioVacances(Connection conn, int idSolicitud) throws SQLException {
		String sql = "UPDATE public.tbl_vacances"
					+ " SET autoritzacio = localtimestamp"
					+ " WHERE idsolicitud = ?";
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, idSolicitud);
		pstm.executeUpdate();
	}
	
	public static void aprovarVistiplauVacances(Connection conn, int idSolicitud) throws SQLException {
		String sql = "UPDATE public.tbl_vacances"
					+ " SET vistiplau = localtimestamp"
					+ " WHERE idsolicitud = ?";
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, idSolicitud);
		pstm.executeUpdate();
	}
	
	public static void rebutjarAutoritzacioVacances(Connection conn, int idSolicitud) throws SQLException {
		String sql = "UPDATE public.tbl_vacances"
					+ " SET rebutjarautoritzacio = localtimestamp"
					+ " WHERE idsolicitud = ?";
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, idSolicitud);
		pstm.executeUpdate();
	}
	
	public static void rebutjarVistiplauVacances(Connection conn, int idSolicitud) throws SQLException {
		String sql = "UPDATE public.tbl_vacances"
					+ " SET rebutjarvistiplau = localtimestamp"
					+ " WHERE idsolicitud = ?";
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, idSolicitud);
		pstm.executeUpdate();
	}
	
	public static void eliminarVacances(Connection conn, int idSolicitud) throws SQLException {
		String sql = "DELETE FROM public.tbl_vacances"
					+ " WHERE idsolicitud = ?";
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, idSolicitud);
		pstm.executeUpdate();
	}
	
	public static String getProperesVacancesUsuari(Connection conn, int idUsuari) throws SQLException {
		String htmlReserves = "";
		String sql = "SELECT idsolicitud, idusuari, tipus, inici, fi, motiu, departament, vistiplau, autoritzacio, rebutjarvistiplau, rebutjarautoritzacio"
					+ " FROM public.tbl_vacances"
					+ " WHERE idusuari = ? AND inici >= localtimestamp;";
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, idUsuari);
		ResultSet rs = pstm.executeQuery();
		Reserves reserves = null;
		
		while (rs.next()) {
			reserves = new Vacances().new Reserves();
			reserves.setDataInici(rs.getDate("inici"));
			reserves.setDataFi(rs.getDate("fi"));
			reserves.setVistiplau(rs.getDate("vistiplau"));
			reserves.setAutoritzacio(rs.getDate("autoritzacio"));
			reserves.setRebutjarAutoritzacio(rs.getDate("rebutjarautoritzacio"));
			reserves.setRebutjarVistiplau(rs.getDate("rebutjarvistiplau"));
			htmlReserves += "<div class='row'>";
			htmlReserves += "<div class='col-md-5'><span>De " + reserves.getDataIniciString() + " a " + reserves.getDataFiString() + " motiu: " + rs.getString("motiu") + "</span></div>";
			htmlReserves += "<div class='col-md-1'><span data-idsolicitud=" + rs.getInt("idsolicitud") + " class='glyphicon glyphicon-remove deleteReservaVacances'></span></div>"; 
			htmlReserves += "<div class='col-md-3'>";
			if (!reserves.getAutoritzacioString().isEmpty())  {
				htmlReserves += "<span>    Autoritzat dia: " + reserves.getAutoritzacioString() + "</span>"; 
			} else if(!reserves.getRebutjarAutoritzacioString().isEmpty()) {
				htmlReserves += "<span>    Autorització rebutjada dia: " + reserves.getRebutjarAutoritzacioString() + "</span>"; 
			}
			htmlReserves += "</div><div class='col-md-3'>";
			if (!reserves.getVistiplauString().isEmpty())  {
				htmlReserves += "<span>    Vistiplau dia: " + reserves.getVistiplauString() + "</span>"; 
			} else if(!reserves.getRebutjarVistiplauString().isEmpty()) {
				htmlReserves += "<span>    Vistiplau rebutjat dia: " + reserves.getRebutjarVistiplauString() + "</span>"; 
			}
			htmlReserves += "</div>";
			htmlReserves += "</div>";
		}
		return htmlReserves;
	}
	
	public static String getVacancesDisfrutadesUsuari(Connection conn, int idUsuari) throws SQLException {
		String htmlReserves = "";
		String sql = "SELECT idsolicitud, idusuari, tipus, inici, fi, motiu, departament, vistiplau, autoritzacio"
					+ " FROM public.tbl_vacances"
					+ " WHERE idusuari = ? AND inici < localtimestamp;";
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, idUsuari);
		ResultSet rs = pstm.executeQuery();
		Reserves reserves = null;
		while (rs.next()) {
			reserves = new Vacances().new Reserves();
			reserves.setDataInici(rs.getDate("inici"));
			reserves.setDataFi(rs.getDate("fi"));
			reserves.setVistiplau(rs.getDate("vistiplau"));
			reserves.setAutoritzacio(rs.getDate("autoritzacio"));
			htmlReserves += "<div class='row'>";
			htmlReserves += "<div class='col-md-6'><span>De " + reserves.getDataIniciString() + " a " + reserves.getDataFiString() + " motiu: " + rs.getString("motiu") + "</span></div>";
			htmlReserves += "<div class='col-md-3'>";
			if (!reserves.getAutoritzacioString().isEmpty())  {
				htmlReserves += "<span>    Autoritzat dia: " + reserves.getAutoritzacioString() + "</span>"; 
			}
			htmlReserves += "</div><div class='col-md-3'>";
			if (!reserves.getVistiplauString().isEmpty())  {
				htmlReserves += "<span>    Vistiplau dia: " + reserves.getVistiplauString() + "</span>"; 
			}
			htmlReserves += "</div>";
			htmlReserves += "</div>";
		}
		return htmlReserves;
	}
	
	public static int getDiesDisponibles(Connection conn, int idUsuari, Vacances vacances, String tipus) throws SQLException {		
		Calendar dateIniAny = Calendar.getInstance();
		dateIniAny.set(Calendar.MONTH, 0);
		dateIniAny.set(Calendar.DAY_OF_MONTH, 1);
		String sql = "SELECT idsolicitud, idusuari, tipus, inici, fi, motiu, departament"
				+ " FROM public.tbl_vacances"
				+ " WHERE idusuari = ? AND inici >= ? AND tipus = ? AND rebutjarautoritzacio IS NULL AND rebutjarvistiplau IS NULL;";
		PreparedStatement pstm;
		pstm = conn.prepareStatement(sql);
		pstm.setInt(1, idUsuari);
		pstm.setDate(2, new java.sql.Date(dateIniAny.getTime().getTime()));
		pstm.setString(3, tipus);
		ResultSet rs = pstm.executeQuery();
		User usuari = UsuariCore.findUsuariByID(conn, idUsuari);
		int diesDisponibles = usuari.getVacances();
		if (tipus.equals("p7")) diesDisponibles = usuari.getPermisos();
		while (rs.next()) {
			diesDisponibles -= getDiesHabils(rs.getDate("inici"), rs.getDate("fi"), vacances, idUsuari);
		}
		return diesDisponibles;
	}
	
	public static int getNewReservaCode(Connection conn) throws SQLException {
		int idReserva = 1;
		String sql = "SELECT idsolicitud"
					+ " FROM public.tbl_vacances"
					+ " ORDER BY idsolicitud DESC" 
					+ " LIMIT 1";
		PreparedStatement pstm;	
		pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) idReserva = rs.getInt("idsolicitud") + 1;
		return idReserva;
	}
	
	private static int getDiesHabils(Date inici, Date fi, Vacances vacances, int idUsuari) {
		int diesHabils = 0;
		
		Calendar diaActual = Calendar.getInstance();		
		diaActual.setTime(inici);
		
		Calendar diaFinal = Calendar.getInstance();
		diaFinal.setTime(fi);
		while (diaActual.before(diaFinal) || diaActual.equals(diaFinal)) {
			if (diaActual.get(Calendar.DAY_OF_WEEK) > 1 && diaActual.get(Calendar.DAY_OF_WEEK) < 7) {
				if (! vacances.getEventDay(diaActual.get(Calendar.DAY_OF_MONTH), diaActual.get(Calendar.MONTH) + 1, diaActual.get(Calendar.YEAR), idUsuari).equals("festiu")) diesHabils += 1;
			}
			diaActual.add(Calendar.DATE, 1);
		}
		return diesHabils;
	}
}
