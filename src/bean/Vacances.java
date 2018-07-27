package bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Vacances {
	
	public class Reserves {
		private int idUsuari;
		private String nomUsuari;
		private String departament;
		private String tipus;
		private Date dataInici;
		private Date dataFi;
		private String motiu;
		private Date vistiplau;
		private Date rebutjarVistiplau;
		private Date autoritzacio;
		private Date rebutjarAutoritzacio;
		public Reserves() {
			
		}
		public int getIdUsuari() {
			return idUsuari;
		}
		public void setIdUsuari(int idUsuari) {
			this.idUsuari = idUsuari;
		}
		public String getDepartament() {
			return departament;
		}
		public void setDepartament(String departament) {
			this.departament = departament;
		}
		public String getTipus() {
			return tipus;
		}
		public void setTipus(String tipus) {
			this.tipus = tipus;
		}
		public Date getDataInici() {
			return dataInici;
		}
		public String getDataIniciString(){
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
			String dataString = "";
			if (this.dataInici != null) dataString = df.format(this.dataInici);
			return dataString;
		}
		public void setDataInici(Date dataInici) {
			this.dataInici = dataInici;
		}
		public Date getDataFi() {
			return dataFi;
		}
		public String getDataFiString(){
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
			String dataString = "";
			if (this.dataFi != null) dataString = df.format(this.dataFi);
			return dataString;
		}
		public void setDataFi(Date dataFi) {
			this.dataFi = dataFi;
		}
		public String getMotiu() {
			return motiu;
		}
		public void setMotiu(String motiu) {
			this.motiu = motiu;
		}
		public Date getVistiplau() {
			return vistiplau;
		}
		public String getVistiplauString() {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
			String dataString = "";
			if (this.vistiplau != null) dataString = df.format(this.vistiplau);
			return dataString;
		}
		public void setVistiplau(Date vistiplau) {
			this.vistiplau = vistiplau;
		}
		public Date getAutoritzacio() {
			return autoritzacio;
		}
		public String getAutoritzacioString() {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
			String dataString = "";
			if (this.autoritzacio != null) dataString = df.format(this.autoritzacio);
			return dataString;
		}
		public void setAutoritzacio(Date autoritzacio) {
			this.autoritzacio = autoritzacio;
		}
		public Date getRebutjarVistiplau() {
			return rebutjarVistiplau;
		}
		public String getRebutjarVistiplauString() {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
			String dataString = "";
			if (this.rebutjarVistiplau != null) dataString = df.format(this.rebutjarVistiplau);
			return dataString;
		}
		public void setRebutjarVistiplau(Date rebutjarVistiplau) {
			this.rebutjarVistiplau = rebutjarVistiplau;
		}
		public Date getRebutjarAutoritzacio() {
			return rebutjarAutoritzacio;
		}
		public String getRebutjarAutoritzacioString() {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
			String dataString = "";
			if (this.rebutjarAutoritzacio != null) dataString = df.format(this.rebutjarAutoritzacio);
			return dataString;
		}
		public void setRebutjarAutoritzacio(Date rebutjarAutoritzacio) {
			this.rebutjarAutoritzacio = rebutjarAutoritzacio;
		}
		public String getNomUsuari() {
			return nomUsuari;
		}
		public void setNomUsuari(String nomUsuari) {
			this.nomUsuari = nomUsuari;
		}
	}
	
	private String festius;
	private List<Reserves> reservesList;
	
	public Vacances() {
		
	}

	public String getFestius() {
		return festius;
	}
	
	public void setFestius(String festius) {
		this.festius = festius;
	}
	
	public String getEventDay(int dia, int mes, int any, int usuari){
		if (dia == 0) return "";
		String event = usuariHasReserva(usuari, dia, mes, any);
		if (festius.contains("#" + dia + "@" + mes + "#")) event = "festiu";
		return event;
	}

	private String usuariHasReserva(int usuari, int dia, int mes, int any) {
		String event = "";
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(any, mes - 1, dia, 0, 0, 0);
		Calendar calIni = Calendar.getInstance();
		Calendar calFi = Calendar.getInstance();
		if (this.reservesList != null) {
			for (Reserves reserva: this.reservesList) {
				if (reserva.idUsuari == usuari && reserva.rebutjarAutoritzacio == null && reserva.rebutjarVistiplau == null) {		
				    calIni.setTime(reserva.dataInici);
				    calFi.setTime(reserva.dataFi);
					if ((cal.after(calIni) && cal.before(calFi)) || cal.equals(calIni) || cal.equals(calFi)){
						event = reserva.getTipus();
						if (reserva.getAutoritzacio() != null) event += " autoritzat";
						if (reserva.getVistiplau() != null) event += " vistiplau";
						break;
					}
				}
			}
		}		
		return event;
	}
	
	public List<Reserves> getReservesList() {
		return reservesList;
	}

	public void setReservesList(List<Reserves> reservesList) {
		this.reservesList = reservesList;
	}
}
