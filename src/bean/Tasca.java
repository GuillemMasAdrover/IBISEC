package bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import utils.Fitxers.Fitxer;

public class Tasca {
	private int idTasca;
	private User usuari;
	private Actuacio actuacio;
	private String idActuacio;
	private Incidencia incidencia;
	private String descripcio;
	private String tipus;
	private boolean activa;
	private Date dataCreacio;
	private String idinforme;
	private String idinformeOriginal;
	private InformeActuacio informe;
	private boolean llegida;
	private String primerComentari;
	private Date darreraModificacio;
	private String duradaTasca;
	private String departament;
	private boolean seguiment;
	private int prioritat;
	private List<Fitxer> documents;
	
	public Tasca() {

	}
	
	public int getIdTasca() {
		return idTasca;
	}
	
	public void setIdTasca(int idTasca) {
		this.idTasca = idTasca;
	}
	
	public Actuacio getActuacio() {
		return actuacio;
	}

	public void setActuacio(Actuacio actuacio) {
		this.actuacio = actuacio;
	}

	public boolean isActiva() {
		return activa;
	}

	public void setActiva(boolean activa) {
		this.activa = activa;
	}

	public String getTipus() {
		return tipus;
	}

	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	
	public String getTipusFormat() {
		switch (this.tipus) {
			case "conformarFactura":
				return "Conformar factura";
			case "resPartidaModificacio":
				return "Reserva partida modificació";
			case "autoritzacioDespesa":
				return "Autorització de la despesa";
			case "autoritModificacio":
				return "Autoritzar modificació";
			case "infPrev":
				return "Realitzar proposta d'actuació";
			case "resPartida":
				return "Reserva de partida";
			case "autoritzacioActuacio":
				return "Autoritzar actuació";
			case "autoritzacioModificacio":
				return "Autoritzar modificació";
			case "facturaConformada":
				return "Factures per pasar a contabilitat";
		case "modificacio":
				return "Modificació informe";
			case "generic":
				return "";
			case "liciMenor":
				return "Realitzar proposta tècnica";
		}
		return this.tipus;
	}

	public Date getDataCreacio() {
		return dataCreacio;
	}
	
	public String getDataCreacioString(){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return df.format(this.dataCreacio);
	}

	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}

	public Incidencia getIncidencia() {
		return incidencia;
	}

	public void setIncidencia(Incidencia incidencia) {
		this.incidencia = incidencia;
	}

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public String getIdinforme() {
		return idinforme;
	}

	public void setIdinforme(String idinforme) {
		this.idinforme = idinforme;
	}

	public boolean isLlegida() {
		return llegida;
	}

	public void setLlegida(boolean llegida) {
		this.llegida = llegida;
	}

	public String getPrimerComentari() {
		return primerComentari;
	}

	public void setPrimerComentari(String primerComentari) {
		this.primerComentari = primerComentari;
	}

	public String getDepartament() {
		return departament;
	}

	public void setDepartament(String departament) {
		this.departament = departament;
	}

	public User getUsuari() {
		return usuari;
	}

	public void setUsuari(User usuari) {
		this.usuari = usuari;
	}

	public boolean isSeguiment() {
		return seguiment;
	}

	public void setSeguiment(boolean seguiment) {
		this.seguiment = seguiment;
	}
	
	public void setSeguimentActuacio(boolean seguiment) {
		this.actuacio.setSeguiment(seguiment);
	}

	public Date getDarreraModificacio() {
		return darreraModificacio;
	}

	public String getDarreraModificacioString(){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return df.format(this.darreraModificacio);
	}
	
	public void setDarreraModificacio(Date darreraModificacio) {
		this.darreraModificacio = darreraModificacio;
	}

	public InformeActuacio getInforme() {
		return informe;
	}

	public void setInforme(InformeActuacio informe) {
		this.informe = informe;
	}

	public int getPrioritat() {
		return prioritat;
	}

	public void setPrioritat(int prioritat) {
		this.prioritat = prioritat;
	}

	public String getIdinformeOriginal() {
		return idinformeOriginal;
	}

	public void setIdinformeOriginal(String idinformeOriginal) {
		this.idinformeOriginal = idinformeOriginal;
	}

	public List<Fitxer> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Fitxer> documents) {
		this.documents = documents;
	}

	public String getIdActuacio() {
		return idActuacio;
	}

	public void setIdActuacio(String idActuacio) {
		this.idActuacio = idActuacio;
	}

	public String getDuradaTasca() {		
		return String.valueOf((this.darreraModificacio.getTime() - this.dataCreacio.getTime())/86400000);
	}

	public void setDuradaTasca(String duradaTasca) {
		this.duradaTasca = duradaTasca;
	}
}
