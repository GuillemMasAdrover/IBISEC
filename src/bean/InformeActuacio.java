package bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import utils.Fitxers;

public class InformeActuacio {
	private String idInf;
	private int idTasca;
	private String idActuacio;
	private String idIncidencia;
	private String objecte;
	private String tipusObra;
	private boolean llicencia;
	private String tipusLlicencia;
	private boolean contracte;
	private double vec;
	private double iva;
	private double plic;
	private String termini;
	private String servei;
	private String comentari;
	private List<Fitxers.Fitxer> adjunts; 
	private User usuari;
	private Date dataCreacio;
	private String partida;
	private User usuariCapValidacio;
	private Date dataCapValidacio;
	private String comentariCap;
	private User usuariAprovacio;
	private Date dataAprovacio;
	private List<Oferta> llistaOfertes;
	private Oferta ofertaSeleccionada;
	private List<Factura> llistaFactures;
	private Date dataTancament;
	
	
	public InformeActuacio() {		
	}

	public String getIdInf() {
		return idInf;
	}

	public void setIdInf(String idInf) {
		this.idInf = idInf;
	}

	public int getIdTasca() {
		return idTasca;
	}

	public void setIdTasca(int idTasca) {
		this.idTasca = idTasca;
	}

	public String getIdActuacio() {
		return idActuacio;
	}

	public void setIdActuacio(String idActuacio) {
		this.idActuacio = idActuacio;
	}

	public String getObjecte() {
		return objecte;
	}

	public void setObjecte(String objecte) {
		this.objecte = objecte;
	}

	public String getTipusObra() {
		return tipusObra;
	}
	
	public String getTipusObraFormat() {
		if ("obr".equals(this.tipusObra)) return "Obra";
		if ("srv".equals(this.tipusObra)) return "Servei";
		if ("submi".equals(this.tipusObra)) return "Subministrament";
		return "";
	}

	public void setTipusObra(String tipusObra) {
		this.tipusObra = tipusObra;
	}

	public boolean isLlicencia() {
		return llicencia;
	}

	public void setLlicencia(boolean llicencia) {
		this.llicencia = llicencia;
	}

	public String getTipusLlicencia() {
		return tipusLlicencia;
	}

	public void setTipusLlicencia(String tipusLlicencia) {
		this.tipusLlicencia = tipusLlicencia;
	}

	public boolean isContracte() {
		return contracte;
	}

	public void setContracte(boolean contracte) {
		this.contracte = contracte;
	}

	public double getVec() {
		return vec;
	}

	public void setVec(double vec) {
		this.vec = vec;
	}

	public double getIva() {
		return iva;
	}

	public void setIva(double iva) {
		this.iva = iva;
	}

	public double getPlic() {
		return plic;
	}

	public void setPlic(double plic) {
		this.plic = plic;
	}

	public String getTermini() {
		return termini;
	}

	public void setTermini(String termini) {
		this.termini = termini;
	}

	public String getServei() {
		return servei;
	}

	public void setServei(String servei) {
		this.servei = servei;
	}

	public String getComentari() {
		return comentari;
	}

	public void setComentari(String comentari) {
		this.comentari = comentari;
	}

	public List<Fitxers.Fitxer> getAdjunts() {
		return adjunts;
	}

	public void setAdjunts(List<Fitxers.Fitxer> adjunts) {
		this.adjunts = adjunts;
	}

	public User getUsuari() {
		return usuari;
	}

	public void setUsuari(User usuari) {
		this.usuari = usuari;
	}

	public Date getDataCreacio() {
		return dataCreacio;
	}

	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	
	public String getDataCreacioString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");	
		String dataString = "";
		if (this.dataCreacio != null) dataString = df.format(this.dataCreacio);
		return dataString;
	}

	public String getPartida() {
		return partida;
	}

	public void setPartida(String partida) {
		this.partida = partida;
	}

	public String getIdIncidencia() {
		return idIncidencia;
	}

	public void setIdIncidencia(String idIncidencia) {
		this.idIncidencia = idIncidencia;
	}

	public User getUsuariAprovacio() {
		return usuariAprovacio;
	}

	public void setUsuariAprovacio(User usuariAprovacio) {
		this.usuariAprovacio = usuariAprovacio;
	}

	public Date getDataAprovacio() {
		return dataAprovacio;
	}

	public String getDataAprovacioString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");	
		String dataString = "";
		if (this.dataAprovacio != null) dataString = df.format(this.dataAprovacio);
		return dataString;
	}
	
	public void setDataAprovacio(Date dataAprovacio) {
		this.dataAprovacio = dataAprovacio;
	}

	public String getComentariCap() {
		return comentariCap;
	}

	public void setComentariCap(String comentariCap) {
		this.comentariCap = comentariCap;
	}

	public List<Oferta> getLlistaOfertes() {
		return llistaOfertes;
	}

	public void setLlistaOfertes(List<Oferta> llistaOfertes) {
		this.llistaOfertes = llistaOfertes;
	}

	public Oferta getOfertaSeleccionada() {
		return ofertaSeleccionada;
	}

	public void setOfertaSeleccionada(Oferta ofertaSeleccionada) {
		this.ofertaSeleccionada = ofertaSeleccionada;
	}

	public User getUsuariCapValidacio() {
		return usuariCapValidacio;
	}
	
	public void setUsuariCapValidacio(User usuariCapValidacio) {
		this.usuariCapValidacio = usuariCapValidacio;
	}

	public Date getDataCapValidacio() {
		return dataCapValidacio;
	}
	
	public String getDataCapValidacioString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");	
		String dataString = "";
		if (this.dataCapValidacio != null) dataString = df.format(this.dataCapValidacio);
		return dataString;
	}

	public void setDataCapValidacio(Date dataCapValidacio) {
		this.dataCapValidacio = dataCapValidacio;
	}

	public List<Factura> getLlistaFactures() {
		return llistaFactures;
	}

	public void setLlistaFactures(List<Factura> llistaFactures) {
		this.llistaFactures = llistaFactures;
	}

	public Date getDataTancament() {
		return dataTancament;
	}

	public void setDataTancament(Date dataTancament) {
		this.dataTancament = dataTancament;
	}
	
	public String getDataTancamentString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataTancament != null) dataString = df.format(this.dataTancament);
		return dataString;
	}
}