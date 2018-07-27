package bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import utils.Fitxers;

public class Actuacio {
	 public class Feina {
		 private String idFeina;
		 private String nomRemitent;
		 private String nomDestinatari;
		 private Date data;
		 private String contingut;
		 private String notes;
		 
		 public Feina() {
			 
		 }

		public String getIdFeina() {
			return idFeina;
		}

		public void setIdFeina(String idFeina) {
			this.idFeina = idFeina;
		}

		public String getNomRemitent() {
			return nomRemitent;
		}

		public void setNomRemitent(String nomRemitent) {
			this.nomRemitent = nomRemitent;
		}

		public String getNomDestinatari() {
			return nomDestinatari;
		}

		public void setNomDestinatari(String nomDestinatari) {
			this.nomDestinatari = nomDestinatari;
		}

		public Date getData() {
			return data;
		}
		
		public String getDataString() {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
			String dataString = "";
			if (this.data != null) dataString = df.format(this.data);
			return dataString;
		}

		public void setData(Date data) {
			this.data = data;
		}

		public String getContingut() {
			return contingut;
		}

		public void setContingut(String contingut) {
			this.contingut = contingut;
		}

		public String getNotes() {
			return notes;
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}
	 }
	
	 public class ArxiusAdjunts {
		 private List<Fitxers.Fitxer> arxiusRegistre = new ArrayList<Fitxers.Fitxer>();
		 private List<Fitxers.Fitxer> arxiusAltres = new ArrayList<Fitxers.Fitxer>();
		 public ArxiusAdjunts() {
			 
		 }
		public List<Fitxers.Fitxer> getArxiusRegistre() {
			this.arxiusRegistre.sort((Fitxers.Fitxer f1, Fitxers.Fitxer f2) -> f1.getData().compareTo(f2.getData()));
			return arxiusRegistre;
		}
		public void setArxiusRegistre(List<Fitxers.Fitxer> arxiusRegistre) {
			this.arxiusRegistre = arxiusRegistre;
		}
		public List<Fitxers.Fitxer> getArxiusAltres() {
			this.arxiusAltres.sort((Fitxers.Fitxer f1, Fitxers.Fitxer f2) -> f1.getData().compareTo(f2.getData()));
			return arxiusAltres;
		}
		public void setArxiusAltres(List<Fitxers.Fitxer> arxiusAltres) {
			this.arxiusAltres = arxiusAltres;
		}
		 
	 }
	 
	 private String referencia;
	 private String descripcio;
	 private String notes;
	 private Date dataCreacio;
	 private int idUsuariCreacio;
	 private Centre centre;
	 private Date dataTancament;
	 private String motiuTancament;
	 private Date dataAprovacio;
	 private InformeActuacio informePrevi;
	 private String idIncidencia;
	 private Date darreraModificacio;
	 private String modificacio;
	 private Date dataAprovarPa;
	 private String refExt;
	 private ArxiusAdjunts arxiusAdjunts = new ArxiusAdjunts();
	 private List<Feina> Feines = new ArrayList<Feina>();
	 private boolean seguiment;
	 
	 public Actuacio() {
		 this.referencia = "-1";
	 }
 
	 public String getReferencia() {
		 return referencia;
	 }
 
	 public void setReferencia(String referencia) {
		 this.referencia = referencia;
	 }
	 	 	 
	 public String getDescripcio() {
		 return descripcio;
	 }
 
	 public void setDescripcio(String descripcio) {
		 this.descripcio = descripcio;
	 }
	 	 
	public boolean isActiva() {
		return this.dataTancament == null;
	}
	
	public String estatNom(){
		if (this.dataTancament == null) {
			return "Activa";
		}else{
			return "Tancada";
		}
	}

	public Date getDataTancament() {
		return dataTancament;
	}
	
	public String getDataTancamentString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");	
		String dataString = "";
		if (this.dataTancament != null) dataString = df.format(this.dataTancament);
		return dataString;
	}

	public void setDataTancament(Date dataTancament) {
		this.dataTancament = dataTancament;
	}

	public boolean isAprovada() {
		return this.dataAprovacio != null;
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

	public String getIdIncidencia() {
		return idIncidencia;
	}

	public void setIdIncidencia(String idIncidencia) {
		this.idIncidencia = idIncidencia;
	}

	public Date getDarreraModificacio() {
		return darreraModificacio;
	}
	
	public String getDarreraModificacioString(){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");	
		String dataString = "";
		if (this.darreraModificacio != null) dataString = df.format(this.darreraModificacio);
		return dataString;		 
	}

	public void setDarreraModificacio(Date darreraModificacio) {
		this.darreraModificacio = darreraModificacio;
	}

	public String getModificacio() {
		return modificacio;
	}

	public void setModificacio(String modificacio) {
		this.modificacio = modificacio;
	}

	public Date getDataCreacio() {
		return this.dataCreacio;
	}

	public void setDataCreacio(Date creacio) {
		this.dataCreacio = creacio;
	}
	
	public String getDataCreacioString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");	
		String dataString = "";
		if (this.dataCreacio != null) dataString = df.format(this.dataCreacio);
		return dataString;		 
	}

	public int getIdUsuariCreacio() {
		return idUsuariCreacio;
	}

	public void setIdUsuariCreacio(int idUsuariCreacio) {
		this.idUsuariCreacio = idUsuariCreacio;
	}

	public InformeActuacio getInformePrevi() {
		return informePrevi;
	}

	public void setInformePrevi(InformeActuacio informePrevi) {
		this.informePrevi = informePrevi;
	}

	public boolean isPaAprovada() {
		return this.dataAprovarPa != null;
	}
	
	public Date getDataAprovarPa() {
		return dataAprovarPa;
	}

	public String getDataAprovarPaString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");	
		String dataString = "";
		if (this.dataAprovarPa != null) dataString = df.format(this.dataAprovarPa);
		return dataString;
	}
	
	public void setDataAprovarPa(Date dataAprovarPa) {
		this.dataAprovarPa = dataAprovarPa;
	}

	public ArxiusAdjunts getArxiusAdjunts() {		
		return arxiusAdjunts;
	}

	public void setArxiusAdjunts(ArxiusAdjunts arxiusAdjunts) {
		this.arxiusAdjunts = arxiusAdjunts;
	}
	
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getRefExt() {
		return refExt;
	}

	public void setRefExt(String refExt) {
		this.refExt = refExt;
	}

	public List<Feina> getFeines() {
		return Feines;
	}

	public void setFeines(List<Feina> feines) {
		Feines = feines;
	}
	
	public String getEstat() {
		return "";
	}

	public boolean isSeguiment() {
		return seguiment;
	}

	public void setSeguiment(boolean seguiment) {
		this.seguiment = seguiment;
	}

	public String getMotiuTancament() {
		return motiuTancament;
	}

	public void setMotiuTancament(String motiuTancament) {
		this.motiuTancament = motiuTancament;
	}

	public Centre getCentre() {
		return centre;
	}

	public void setCentre(Centre centre) {
		this.centre = centre;
	}
}
