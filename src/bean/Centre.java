package bean;

import java.util.List;

public class Centre {	
	private String idCentre;
	private String nom;
	private String tipo;
	private String illa;
	private String municipi;
	private String localitat;
	private String adreca;
	private String cp;
	private double lat;
	private double lng;
	private Resultat llistaActuacions;
	private List<Incidencia> llistaIncidencies;
	public Centre() {
		 
	}

	public Centre(String idCentre, String nom) {		 
		this.idCentre = idCentre;
		this.nom = nom;		 
	}
	
	public String getIdCentre() {
		return idCentre;
	}
	public void setIdCentre(String idCentre) {
		this.idCentre = idCentre;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getNomComplet(){
		return this.nom + " (" + this.localitat + ", " + this.municipi + ")";
	}
	
	public void setActuacions(Resultat actuacions){
		this.llistaActuacions = actuacions;
	}
	
	public Resultat getLlistaActuacions(){
		return this.llistaActuacions;
	}
	
	public String getLlistaIdActuacions(){
		String llistaIds = "";
		String estat = "danger";
		if (this.llistaActuacions != null) {
			for (Actuacio actuacio: this.llistaActuacions.getLlistaActuacions()) {
				if (actuacio.isActiva()) estat = "warning";
				if (actuacio.isActiva() && actuacio.isAprovada()) estat = "success";
				llistaIds += "<a style='margin:2px;' class='btn btn-" + estat + "' target='_blank' href='actuacionsDetalls?ref=" + actuacio.getReferencia() + "'>" + actuacio.getReferencia() + "</a>";
			}
		}
		return llistaIds;
	}
	
	public void setIncidencies(List<Incidencia> incidencies){
		this.llistaIncidencies = incidencies;
	}
	
	public List<Incidencia> getLlistaIncidencies(){
		return this.llistaIncidencies;
	}
	
	public String getLlistaIdIncidencies(){
		String llistaIds = "";
		String estat = "success";
		if (this.llistaIncidencies != null) {
			for (Incidencia incidencia: this.llistaIncidencies) {
				llistaIds += "<a style='margin:2px;' class='btn btn-" + estat + "' target='_blank' href='incidenciaDetalls?ref=" + incidencia.getIdIncidencia() + "'>" + incidencia.getIdIncidencia() + "</a>";
			}
		}
		return llistaIds;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getIlla() {
		return illa;
	}

	public void setIlla(String illa) {
		this.illa = illa;
	}

	public String getMunicipi() {
		return municipi;
	}

	public void setMunicipi(String municipi) {
		this.municipi = municipi;
	}

	public String getLocalitat() {
		return localitat;
	}

	public void setLocalitat(String localitat) {
		this.localitat = localitat;
	}

	public String getAdreca() {
		return adreca;
	}

	public void setAdreca(String adreca) {
		this.adreca = adreca;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}
