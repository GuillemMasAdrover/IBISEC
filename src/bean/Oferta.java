package bean;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import utils.Fitxers;

public class Oferta {
	private String idOferta;
	private String idActuacio;
	private Actuacio actuacio;
	private String cifEmpresa;
	private String nomEmpresa;
	private String capDobra;
	private Fitxers.Fitxer personalInscrit;
	private double pbase;
	private double iva;
	private double plic;
	private String termini;
	private boolean seleccionada;
	private boolean descalificada;
	private String comentari;
	private User usuariCreacio;
	private Date dataCreacio;
	private User usuariAprovacio;
	private Date dataAprovacio;
	private User usuariCapValidacio;
	private Date dataCapValidacio;
	private String idInforme;
	private Fitxers.Fitxer presupost;
		
	public Oferta(){
		
	}

	public String getIdOferta() {
		return idOferta;
	}

	public void setIdOferta(String idOferta) {
		this.idOferta = idOferta;
	}

	public String getIdActuacio() {
		return idActuacio;
	}

	public void setIdActuacio(String idActuacio) {
		this.idActuacio = idActuacio;
	}

	public String getCifEmpresa() {
		return cifEmpresa;
	}

	public void setCifEmpresa(String cifEmpresa) {
		this.cifEmpresa = cifEmpresa;
	}

	public String getPbaseFormat(){
		DecimalFormat num = new DecimalFormat("#,##0.00");
	    return num.format(this.pbase) + '€';
	}
	
	public double getIva() {
		return iva;
	}

	public String getIvaFormat(){
		DecimalFormat num = new DecimalFormat("#,##0.00");
	    return num.format(this.iva) + '€';
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
	
	public String getPlicFormat(){
		if (this.plic == 0) return "No presentada";
		DecimalFormat num = new DecimalFormat("#,##0.00");
	    return num.format(this.plic) + '€';
	}
	
	public String getPlicFormatNormal(){
		DecimalFormat num = new DecimalFormat("#,##0.00");
	    return num.format(this.plic) + '€';
	}

	public String getTermini() {
		return termini;
	}

	public void setTermini(String termini) {
		this.termini = termini;
	}

	public boolean isSeleccionada() {
		return seleccionada;
	}

	public void setSeleccionada(boolean seleccionada) {
		this.seleccionada = seleccionada;
	}

	public boolean isDescalificada() {
		return descalificada;
	}

	public void setDescalificada(boolean descalificada) {
		this.descalificada = descalificada;
	}

	public String getComentari() {
		return comentari;
	}

	public void setComentari(String comentari) {
		this.comentari = comentari;
	}

	public String getNomEmpresa() {
		return nomEmpresa;
	}

	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}

	public Date getDataCreacio() {
		return dataCreacio;
	}
	
	public String getDataCreacioString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");	
		String dataString = "";
		if (this.dataCreacio != null) dataString = df.format(this.dataCreacio);
		return dataString;	
	}

	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}

	public User getUsuariCreacio() {
		return usuariCreacio;
	}

	public void setUsuariCreacio(User usuariCreacio) {
		this.usuariCreacio = usuariCreacio;
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
	
	public String getDataAprovacioString(){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");	
		String dataString = "";
		if (this.dataAprovacio != null) dataString = df.format(this.dataAprovacio);
		return dataString;		 
	}

	public void setDataAprovacio(Date dataAprovacio) {
		this.dataAprovacio = dataAprovacio;
	}

	public String getIdInforme() {
		return idInforme;
	}

	public void setIdInforme(String idInforme) {
		this.idInforme = idInforme;
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
	
	public String getDataCapValidacioString(){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");	
		String dataString = "";
		if (this.dataCapValidacio != null) dataString = df.format(this.dataCapValidacio);
		return dataString;		 
	}

	public void setDataCapValidacio(Date dataCapValidacio) {
		this.dataCapValidacio = dataCapValidacio;
	}

	public Actuacio getActuacio() {
		return actuacio;
	}

	public void setActuacio(Actuacio actuacio) {
		this.actuacio = actuacio;
	}

	public Fitxers.Fitxer getPresupost() {
		return presupost;
	}

	public void setPresupost(Fitxers.Fitxer presupost) {
		this.presupost = presupost;
	}

	public String getCapDobra() {
		return capDobra;
	}

	public void setCapDobra(String capDobra) {
		this.capDobra = capDobra;
	}

	public Fitxers.Fitxer getPersonalInscrit() {
		return personalInscrit;
	}

	public void setPersonalInscrit(Fitxers.Fitxer personalInscrit) {
		this.personalInscrit = personalInscrit;
	}

	public double getPbase() {
		return pbase;
	}

	public void setPbase(double pbase) {
		this.pbase = pbase;
	}
}
