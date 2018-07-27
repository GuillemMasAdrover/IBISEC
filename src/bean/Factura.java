package bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import utils.Fitxers.Fitxer;

public class Factura {
	private String idFactura;
	private String idInforme;
	private InformeActuacio informe;
	private String idActuacio;
	private Actuacio actuacio;
	private String idProveidor;
	private String nomProveidor;
	private Date dataFactura;
	private String concepte;
	private double valor;
	private String tipusFactura;
	private String nombreFactura;
	private User usuariCreador;
	private Date dataEntrada;
	private User usuariConformador;
	private Date dataConformacio;
	private String notes;
	private Date dataEnviatConformador;
	private Date dataEnviatComptabilitat;
	private Date dataDescarregadaConformada;
	private Fitxer factura;
	private List<Fitxer> certificacions;
	private List<Fitxer> altres;
	private boolean anulada;
	private String motiuAnulada;
	
	public Factura() {
	
	}

	public String getIdFactura() {
		return idFactura;
	}

	public void setIdFactura(String idFactura) {
		this.idFactura = idFactura;
	}

	public String getIdInforme() {
		return idInforme;
	}

	public void setIdInforme(String idInforme) {
		this.idInforme = idInforme;
	}

	public Actuacio getActuacio() {
		return actuacio;
	}

	public void setActuacio(Actuacio actuacio) {
		this.actuacio = actuacio;
	}

	public String getIdProveidor() {
		return idProveidor;
	}

	public void setIdProveidor(String idProveidor) {
		this.idProveidor = idProveidor;
	}

	public Date getDataFactura() {
		return dataFactura;
	}
	
	public String getDataFacturaString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataFactura != null) dataString = df.format(this.dataFactura);
		return dataString;
	}

	public void setDataFactura(Date dataFactura) {
		this.dataFactura = dataFactura;
	}

	public String getConcepte() {
		return concepte;
	}

	public void setConcepte(String concempte) {
		this.concepte = concempte;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public String getTipusFactura() {
		return tipusFactura;
	}

	public void setTipusFactura(String tipusFactura) {
		this.tipusFactura = tipusFactura;
	}

	public String getNombreFactura() {
		return nombreFactura;
	}

	public void setNombreFactura(String nombreFactura) {
		this.nombreFactura = nombreFactura;
	}

	public Date getDataEntrada() {
		return dataEntrada;
	}
	
	public String getDataEntradaString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataEntrada != null) dataString = df.format(this.dataEntrada);
		return dataString;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public User getUsuariConformador() {
		return usuariConformador;
	}

	public void setUsuariConformador(User usuariConformador) {
		this.usuariConformador = usuariConformador;
	}

	public Date getDataConformacio() {
		return dataConformacio;
	}
	
	public String getDataConformacioString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataConformacio != null) dataString = df.format(this.dataConformacio);
		return dataString;
	}

	public void setDataConformacio(Date dataConformacio) {
		this.dataConformacio = dataConformacio;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getIdActuacio() {
		return idActuacio;
	}

	public void setIdActuacio(String idActuacio) {
		this.idActuacio = idActuacio;
	}

	public InformeActuacio getInforme() {
		return informe;
	}

	public void setInforme(InformeActuacio informe) {
		this.informe = informe;
	}

	public Date getDataEnviatConformador() {
		return dataEnviatConformador;
	}
	
	public String getDataEnviatConformadorString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataEnviatConformador != null) dataString = df.format(this.dataEnviatConformador);
		return dataString;
	}

	public void setDataEnviatConformador(Date dataEnviatConformador) {
		this.dataEnviatConformador = dataEnviatConformador;
	}

	public Date getDataEnviatComptabilitat() {
		return dataEnviatComptabilitat;
	}

	public String getDataEnviatComptabilitatString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataEnviatComptabilitat != null) dataString = df.format(this.dataEnviatComptabilitat);
		return dataString;
	}
	
	public void setDataEnviatComptabilitat(Date dataEnviatComptabilitat) {
		this.dataEnviatComptabilitat = dataEnviatComptabilitat;
	}

	public User getUsuariCreador() {
		return usuariCreador;
	}

	public void setUsuariCreador(User usuariCreador) {
		this.usuariCreador = usuariCreador;
	}

	public String getNomProveidor() {
		return nomProveidor;
	}

	public void setNomProveidor(String nomProveidor) {
		this.nomProveidor = nomProveidor;
	}

	public Date getDataDescarregadaConformada() {
		return dataDescarregadaConformada;
	}
	
	public String getDataDescarregadaConformadaString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataDescarregadaConformada != null) dataString = df.format(this.dataDescarregadaConformada);
		return dataString;
	}

	public void setDataDescarregadaConformada(Date dataDescarregadaConformada) {
		this.dataDescarregadaConformada = dataDescarregadaConformada;
	}

	public boolean isAnulada() {
		return anulada;
	}

	public void setAnulada(boolean anulada) {
		this.anulada = anulada;
	}

	public List<Fitxer> getAltres() {
		return altres;
	}

	public void setAltres(List<Fitxer> altres) {
		this.altres = altres;
	}

	public String getMotiuAnulada() {
		return motiuAnulada;
	}

	public void setMotiuAnulada(String motiuAnulada) {
		this.motiuAnulada = motiuAnulada;
	}

	public Fitxer getFactura() {
		return factura;
	}

	public void setFactura(Fitxer factura) {
		this.factura = factura;
	}

	public List<Fitxer> getCertificacions() {
		return certificacions;
	}

	public void setCertificacions(List<Fitxer> certificacions) {
		this.certificacions = certificacions;
	}
	
}
