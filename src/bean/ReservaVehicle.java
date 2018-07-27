package bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReservaVehicle {
	
	public static final String[] horesCotxe = {"9:00","9:30","10:00","10:30","11:00","11:30","12:00","12:30","13:00","13:30","14:00"};
	public static final String[] horesCotxeElectric = {"9:00","9:30","10:00","10:30","11:00","11:30","12:00","12:30","13:00","13:30","14:00"};
	public static final String[] horesFurgoneta = {"7:30","8:00","8:30","9:00","9:30","10:00","10:30","11:00","11:30","12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30","16:00","16:30"};
	
	public class Reserva {
		private User usuari;
		private String motiu;
		private String vehicle;
		private int setmana;
		private int dia;
		private int hora;
		private int year;
		
		public User getUsuari() {
			return usuari;
		}
		public void setUsuari(User usuari) {
			this.usuari = usuari;
		}
		public String getMotiu() {
			return motiu;
		}
		public void setMotiu(String motiu) {
			this.motiu = motiu;
		}
		public String getVehicle() {
			return vehicle;
		}
		public void setVehicle(String vehicle) {
			this.vehicle = vehicle;
		}
		public int getSetmana() {
			return setmana;
		}
		public void setSetmana(int setmana) {
			this.setmana = setmana;
		}
		public int getDia() {
			return dia;
		}
		public void setDia(int dia) {
			this.dia = dia;
		}
		public int getHora() {
			return hora;
		}
		public void setHora(int hora) {
			this.hora = hora;
		}
		public int getYear() {
			return year;
		}
		public void setYear(int year) {
			this.year = year;
		}
	}
	private int setmana;
	private int dimarts;
	private int dimecres;
	private int dijous;
	private int divendres;
	private int dissabte;
	private int diumenge;
	private int dilluns;
	private Reserva[] reservesDimarts;
	private Reserva[] reservesDimecres;
	private Reserva[] reservesDijous;
	private Reserva[] reservesDivendres;
	private Reserva[] reservesDilluns;
	public int getSetmana() {
		return setmana;
	}
	public void setSetmana(int setmana) {
		this.setmana = setmana;
	}
	public int getDimarts() {
		return dimarts;
	}
	public void setDimarts(int dimarts) {
		this.dimarts = dimarts;
	}
	public int getDimecres() {
		return dimecres;
	}
	public void setDimecres(int dimecres) {
		this.dimecres = dimecres;
	}
	public int getDijous() {
		return dijous;
	}
	public void setDijous(int dijous) {
		this.dijous = dijous;
	}
	public int getDivendres() {
		return divendres;
	}
	public void setDivendres(int divendres) {
		this.divendres = divendres;
	}
	public int getDissabte() {
		return dissabte;
	}
	public void setDissabte(int dissabte) {
		this.dissabte = dissabte;
	}
	public int getDiumenge() {
		return diumenge;
	}
	public void setDiumenge(int diumenge) {
		this.diumenge = diumenge;
	}
	public int getDilluns() {
		return dilluns;
	}
	public void setDilluns(int dilluns) {
		this.dilluns = dilluns;
	}
	public Reserva[] getReservesDimarts() {
		return reservesDimarts;
	}
	public void setReservesDimarts(Reserva[] reservesDimarts) {
		this.reservesDimarts = reservesDimarts;
	}
	public Reserva[] getReservesDimecres() {
		return reservesDimecres;
	}
	public void setReservesDimecres(Reserva[] reservesDimecres) {
		this.reservesDimecres = reservesDimecres;
	}
	public Reserva[] getReservesDijous() {
		return reservesDijous;
	}
	public void setReservesDijous(Reserva[] reservesDijous) {
		this.reservesDijous = reservesDijous;
	}
	public Reserva[] getReservesDivendres() {
		return reservesDivendres;
	}
	public void setReservesDivendres(Reserva[] reservesDivendres) {
		this.reservesDivendres = reservesDivendres;
	}
	public Reserva[] getReservesDilluns() {
		return reservesDilluns;
	}
	public void setReservesDilluns(Reserva[] reservesDilluns) {
		this.reservesDilluns = reservesDilluns;
	}
}
