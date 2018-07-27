package bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Instalacions {
	private String idInf;
	private String expedientBaixaTensio;
	private Date dataOCABaixaTensio;
	private String expedientFotovoltaica;
	private Date dataOCAFotovoltaica;
	private String expedientContraincendis;
	private String expedientTermiques;
	private String expedientPetrolifers;
	private Date dataPetrolifers;
	private String instalacioPetrolifers;
	private String dipositsPetrolifers;
	private String capTotalPetrolifers;
	private String expedientGas;
	private String expedientAscensor;
	private String expedientEficienciaEnergetica;
	private Date dataRegistreEficienciaEnergetica;
	private String expedientPlaAutoproteccio;
	private Date dataCedulaHabitabilitat;
	
	public String getIdInf() {
		return idInf;
	}
	public void setIdInf(String idInf) {
		this.idInf = idInf;
	}
	public String getExpedientBaixaTensio() {
		return expedientBaixaTensio;
	}
	public void setExpedientBaixaTensio(String expedientBaixaTensio) {
		this.expedientBaixaTensio = expedientBaixaTensio;
	}
	public Date getDataOCABaixaTensio() {
		return dataOCABaixaTensio;
	}
	public String getDataOCABaixaTensioString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataOCABaixaTensio != null) dataString = df.format(this.dataOCABaixaTensio);
		return dataString;
	}
	public void setDataOCABaixaTensio(Date dataOCABaixaTensio) {
		this.dataOCABaixaTensio = dataOCABaixaTensio;
	}
	public String getExpedientFotovoltaica() {
		return expedientFotovoltaica;
	}
	public void setExpedientFotovoltaica(String expedientFotovoltaica) {
		this.expedientFotovoltaica = expedientFotovoltaica;
	}
	public Date getDataOCAFotovoltaica() {
		return dataOCAFotovoltaica;
	}
	public String getDataOCAFotovoltaicaString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataOCAFotovoltaica != null) dataString = df.format(this.dataOCAFotovoltaica);
		return dataString;
	}
	public void setDataOCAFotovoltaica(Date dataOCAFotovoltaica) {
		this.dataOCAFotovoltaica = dataOCAFotovoltaica;
	}
	public String getExpedientContraincendis() {
		return expedientContraincendis;
	}
	public void setExpedientContraincendis(String expedientContraincendis) {
		this.expedientContraincendis = expedientContraincendis;
	}
	public String getExpedientTermiques() {
		return expedientTermiques;
	}
	public void setExpedientTermiques(String expedientTermiques) {
		this.expedientTermiques = expedientTermiques;
	}
	public String getExpedientPetrolifers() {
		return expedientPetrolifers;
	}
	public void setExpedientPetrolifers(String expedientPetrolifers) {
		this.expedientPetrolifers = expedientPetrolifers;
	}
	public String getExpedientGas() {
		return expedientGas;
	}
	public void setExpedientGas(String expedientGas) {
		this.expedientGas = expedientGas;
	}
	public String getExpedientAscensor() {
		return expedientAscensor;
	}
	public void setExpedientAscensor(String expedientAscensor) {
		this.expedientAscensor = expedientAscensor;
	}
	public String getExpedientEficienciaEnergetica() {
		return expedientEficienciaEnergetica;
	}
	public void setExpedientEficienciaEnergetica(String expedientEficienciaEnergetica) {
		this.expedientEficienciaEnergetica = expedientEficienciaEnergetica;
	}
	public Date getDataRegistreEficienciaEnergetica() {
		return dataRegistreEficienciaEnergetica;
	}
	public String getDataRegistreEficienciaEnergeticaString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataRegistreEficienciaEnergetica != null) dataString = df.format(this.dataRegistreEficienciaEnergetica);
		return dataString;
	}
	public void setDataRegistreEficienciaEnergetica(Date dataRegistreEficienciaEnergetica) {
		this.dataRegistreEficienciaEnergetica = dataRegistreEficienciaEnergetica;
	}
	public String getExpedientPlaAutoproteccio() {
		return expedientPlaAutoproteccio;
	}
	public void setExpedientPlaAutoproteccio(String expedientPlaAutoproteccio) {
		this.expedientPlaAutoproteccio = expedientPlaAutoproteccio;
	}
	public Date getDataCedulaHabitabilitat() {
		return dataCedulaHabitabilitat;
	}
	public String getDataCedulaHabitabilitatString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataCedulaHabitabilitat != null) dataString = df.format(this.dataCedulaHabitabilitat);
		return dataString;
	}
	public void setDataCedulaHabitabilitat(Date dataCedulaHabitabilitat) {
		this.dataCedulaHabitabilitat = dataCedulaHabitabilitat;
	}
	public Date getDataPetrolifers() {
		return dataPetrolifers;
	}
	public String getDataPetrolifersString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataPetrolifers != null) dataString = df.format(this.dataPetrolifers);
		return dataString;
	}
	public void setDataPetrolifers(Date dataPetrolifers) {
		this.dataPetrolifers = dataPetrolifers;
	}
	public String getInstalacioPetrolifers() {
		return instalacioPetrolifers;
	}
	public void setInstalacioPetrolifers(String instalacioPetrolifers) {
		this.instalacioPetrolifers = instalacioPetrolifers;
	}
	public String getDipositsPetrolifers() {
		return dipositsPetrolifers;
	}
	public void setDipositsPetrolifers(String dipositsPetrolifers) {
		this.dipositsPetrolifers = dipositsPetrolifers;
	}
	public String getCapTotalPetrolifers() {
		return capTotalPetrolifers;
	}
	public void setCapTotalPetrolifers(String capTotalPetrolifers) {
		this.capTotalPetrolifers = capTotalPetrolifers;
	}
}
