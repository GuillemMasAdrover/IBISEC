package bean;

import java.util.Date;
import java.util.List;

public class Projecte {
	private String idProjecte;
	private String nomProjecte;
	private Actuacio actuacio;
	private List<User> usuarisList;
	private Date dataCreacio;
	
	public Projecte() {
		
	}

	public String getIdProjecte() {
		return idProjecte;
	}

	public void setIdProjecte(String idProjecte) {
		this.idProjecte = idProjecte;
	}

	public List<User> getUsuarisList() {
		return usuarisList;
	}
	
	public String getUsuarisListString() {
		String usuarisString = "";
		for (User usuari: this.usuarisList) {
			usuarisString += usuari.getIdUsuari() + "#";
		}
		return usuarisString;
	}

	public void setUsuarisList(List<User> usuarisList) {
		this.usuarisList = usuarisList;
	}

	public Date getDataCreacio() {
		return dataCreacio;
	}

	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}

	public String getNomProjecte() {
		return nomProjecte;
	}

	public void setNomProjecte(String nomProjecte) {
		this.nomProjecte = nomProjecte;
	}

	public Actuacio getActuacio() {
		return actuacio;
	}

	public void setActuacio(Actuacio actuacio) {
		this.actuacio = actuacio;
	}
}
