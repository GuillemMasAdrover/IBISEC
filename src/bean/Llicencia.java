package bean;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import utils.Fitxers;

public class Llicencia {
	private String codiExpedient;
	private String idPartida;
	private String codi;
	private String tipus;
	private double taxa;
	private double ico;
	private String observacio;
	private Date peticio;
	private Date concesio;
	private Date pagamentTaxa;
	private Date pagamentICO;
	private List<Fitxers.Fitxer> arxius;
	private List<Fitxers.Fitxer> documentSolLlicencia;
	private List<Fitxers.Fitxer> documentConcessioLlicencia;
	private List<Fitxers.Fitxer> documentPagamentLlicencia;
	private List<Fitxers.Fitxer> documentTitolHabilitant;
	
	public Llicencia() {
		
	}

	public String getCodiExpedient() {
		return codiExpedient;
	}

	public void setCodiExpedient(String codiExpedient) {
		this.codiExpedient = codiExpedient;
	}

	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getTipus() {
		return tipus;
	}
	
	public String getTipusFormat(){		
		if (this.tipus != null) {
			switch(this.tipus) {
			case "llicencia":
				return "llicència";
			case "comun":
				return "comunicació prèvia";
			}		
		}
		
		return "";
	}

	public void setTipus(String tipus) {
		this.tipus = tipus;
	}

	public String getObservacio() {
		return observacio;
	}

	public void setObservacio(String observacio) {
		this.observacio = observacio;
	}

	public Date getPeticio() {
		return peticio;
	}
	
	public String getDataPeticioString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.peticio != null) dataString = df.format(this.peticio);
		return dataString;
	}

	public void setPeticio(Date peticio) {
		this.peticio = peticio;
	}

	public Date getConcesio() {
		return concesio;
	}
	
	public String getDataConcesioString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.concesio != null) dataString = df.format(this.concesio);
		return dataString;
	}

	public void setConcesio(Date concesio) {
		this.concesio = concesio;
	}

	public double getTaxa() {
		return taxa;
	}

	public String getTaxaFormat(){
		DecimalFormat num = new DecimalFormat("#,##0.00");
	    return num.format(this.taxa) + '€';
	}
	
	public void setTaxa(double taxa) {
		this.taxa = taxa;
	}

	public double getIco() {
		return ico;
	}
	
	public String getIcoFormat(){
		DecimalFormat num = new DecimalFormat("#,##0.00");
	    return num.format(this.ico) + '€';
	}

	public void setIco(double ico) {
		this.ico = ico;
	}

	public List<Fitxers.Fitxer> getArxius() {
		return arxius;
	}

	public void setArxius(List<Fitxers.Fitxer> arxius) {
		this.arxius = arxius;
	}

	public Date getPagamentTaxa() {
		return pagamentTaxa;
	}

	public String getDataPagamentTaxaString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.pagamentTaxa != null) dataString = df.format(this.pagamentTaxa);
		return dataString;
	}
	
	public void setPagamentTaxa(Date pagamentTaxa) {
		this.pagamentTaxa = pagamentTaxa;
	}

	public Date getPagamentICO() {
		return pagamentICO;
	}

	public String getDataPagamentICOString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.pagamentICO != null) dataString = df.format(this.pagamentICO);
		return dataString;
	}
	
	public void setPagamentICO(Date pagamentICO) {
		this.pagamentICO = pagamentICO;
	}

	public List<Fitxers.Fitxer> getDocumentSolLlicencia() {
		return documentSolLlicencia;
	}

	public void setDocumentSolLlicencia(List<Fitxers.Fitxer> documentSolLlicencia) {
		this.documentSolLlicencia = documentSolLlicencia;
	}

	public List<Fitxers.Fitxer> getDocumentConcessioLlicencia() {
		return documentConcessioLlicencia;
	}

	public void setDocumentConcessioLlicencia(List<Fitxers.Fitxer> documentConcessioLlicencia) {
		this.documentConcessioLlicencia = documentConcessioLlicencia;
	}

	public List<Fitxers.Fitxer> getDocumentPagamentLlicencia() {
		return documentPagamentLlicencia;
	}

	public void setDocumentPagamentLlicencia(List<Fitxers.Fitxer> documentPagamentLlicencia) {
		this.documentPagamentLlicencia = documentPagamentLlicencia;
	}

	public List<Fitxers.Fitxer> getDocumentTitolHabilitant() {
		return documentTitolHabilitant;
	}

	public void setDocumentTitolHabilitant(List<Fitxers.Fitxer> documentTitolHabilitant) {
		this.documentTitolHabilitant = documentTitolHabilitant;
	}

	public String getIdPartida() {
		return idPartida;
	}

	public void setIdPartida(String idPartida) {
		this.idPartida = idPartida;
	}
}
