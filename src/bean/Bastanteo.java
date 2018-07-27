package bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Bastanteo {
	public class Escritura{
		private int codi;
		private String escritura;
		private Date dataEscritura;
		private String numProtocol;
		private String notari;
		
		public Escritura() {}
		
		public int getCodi() {
			return codi;
		}
		public void setCodi(int codi) {
			this.codi = codi;
		}
		
		public String getEscritura() {
			return escritura;
		}
		public void setEscritura(String escritura) {
			this.escritura = escritura;
		}
		public Date getDataEscritura() {
			return dataEscritura;
		}
		public String getDataEscrituraString() {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
			String dataString = "";
			if (this.dataEscritura != null) dataString = df.format(this.dataEscritura);
			return dataString;
		}
		public void setDataEscritura(Date dataEscritura) {
			this.dataEscritura = dataEscritura;
		}
		public String getNumProtocol() {
			return numProtocol;
		}
		public void setNumProtocol(String numProtocol) {
			this.numProtocol = numProtocol;
		}
		public String getNotari() {
			return notari;
		}
		public void setNotari(String notari) {
			this.notari = notari;
		}
	}
	
	private String ref;
	private Date databastanteo;
	private Empresa empresa;	
	private String personaFacultada;
	private String carrec;
	private String anyBastanteo;
	private List<Escritura> escrituresList;
	
	public Bastanteo() {
		 
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public Date getDatabastanteo() {
		return databastanteo;
	}
	public String getDatabastanteoString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.databastanteo != null) dataString = df.format(this.databastanteo);
		return dataString;
	}
	public void setDatabastanteo(Date databastanteo) {
		this.databastanteo = databastanteo;
	}
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	public String getPersonaFacultada() {
		return personaFacultada;
	}
	public void setPersonaFacultada(String personaFacultada) {
		this.personaFacultada = personaFacultada;
	}
	public String getCarrec() {
		return carrec;
	}
	public void setCarrec(String carrec) {
		this.carrec = carrec;
	}	
	public String getAnyBastanteo() {
		return anyBastanteo;
	}
	public void setAnyBastanteo(String anyBastanteo) {
		this.anyBastanteo = anyBastanteo;
	}
	public List<Escritura> getEscrituresList() {
		return escrituresList;
	}
	public void setEscrituresList(List<Escritura> escrituresList) {
		this.escrituresList = escrituresList;
	}
}
