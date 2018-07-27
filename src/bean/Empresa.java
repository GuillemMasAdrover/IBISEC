package bean;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import utils.Fitxers;
import utils.Fitxers.Fitxer;

public class Empresa {
 
   public class Classificacio {
	   private String grup;
	   private String subGrup;
	   private String categoria;
	public String getGrup() {
		return grup;
	}
	public void setGrup(String grup) {
		this.grup = grup;
	}
	public String getSubGrup() {
		return subGrup;
	}
	public void setSubGrup(String subGrup) {
		this.subGrup = subGrup;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
   }
	
   public class Administrador{
	   private String nom;
	   private String dni;
	   private Date dataValidesaFins;
	   private Date dataModificacio;
	   private String notariModificacio;
	   private String protocolModificacio;
	   private Date dataValidacio;
	   private String entitatValidacio;
	   private String tipus;
	   private Fitxer documentAdministrador;
		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
		public String getDni() {
			return dni;
		}
		public void setDni(String dni) {
			this.dni = dni;
		}
		public Date getDataValidesaFins() {
			return dataValidesaFins;
		}
		public String getDataValidesaFinsString() {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
			String dataString = "";
			if (this.dataValidesaFins != null) dataString = df.format(this.dataValidesaFins);
			return dataString;
		}
		public void setDataValidesaFins(Date dataValidesaFins) {
			this.dataValidesaFins = dataValidesaFins;
		}
		
		public boolean isCaducat(){
			boolean caducat = false;
			Calendar cad = Calendar.getInstance();
			if (this.dataValidesaFins != null) {
				cad.setTime(this.dataValidesaFins);		
				if (cad.before(Calendar.getInstance())) caducat = true;
			}			
			return caducat;
		}
		
		public Date getDataModificacio() {
			return dataModificacio;
		}
		public String getDataModificacioString() {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
			String dataString = "";
			if (this.dataModificacio != null) dataString = df.format(this.dataModificacio);
			return dataString;
		}
		public void setDataModificacio(Date dataModificacio) {
			this.dataModificacio = dataModificacio;
		}
		public String getNotariModificacio() {
			return notariModificacio;
		}
		public void setNotariModificacio(String notariModificacio) {
			this.notariModificacio = notariModificacio;
		}
		public String getProtocolModificacio() {
			return protocolModificacio;
		}
		public void setProtocolModificacio(String protocolModificacio) {
			this.protocolModificacio = protocolModificacio;
		}
		public String getTipus() {
			return tipus;
		}
		public void setTipus(String tipus) {
			this.tipus = tipus;
		}
		public Date getDataValidacio() {
			return dataValidacio;
		}
		public void setDataValidacio(Date dataValidacio) {
			this.dataValidacio = dataValidacio;
		}
		public String getDataValidacioString(){
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
			String dataString = "";
			if (this.dataValidacio != null) dataString = df.format(this.dataValidacio);
			return dataString;
		}
		public String getEntitatValidacio() {
			return entitatValidacio;
		}
		public String getEntitatValidacioString() {
			if (this.entitatValidacio != null) {
				if(this.entitatValidacio.equals("caib")) {
					return "Advocacia CAIB";
				} else if (this.entitatValidacio.equals("estat")) {
					return "Advocacia Estat";
				} else if (this.entitatValidacio.equals("ibisec")) {
					return "Assessoria jurídica IBISEC";
				} else {
					return this.entitatValidacio;
				}
			} else {
				return "";
			}
		}
		public void setEntitatValidacio(String entitatValidacio) {
			this.entitatValidacio = entitatValidacio;
		}
		public Fitxer getDocumentAdministrador() {
			return documentAdministrador;
		}
		public void setDocumentAdministrador(Fitxer documentAdministrador) {
			this.documentAdministrador = documentAdministrador;
		}
   }

   public class UTE {
	   	private List<Empresa> empreses;
		public List<Empresa> getEmpreses() {
			return empreses;
		}
		public String getEmpresesString() {
			String empreses = "";
			if (this.getEmpreses() != null) {
				for (Empresa e: this.getEmpreses()) {
					empreses += e.getCif() + "#";			
				}
			}
			return empreses;
		}
		public void setEmpreses(List<Empresa> empreses) {
			this.empreses = empreses;
		}
   }
      
   private String cif;
   private String name;
   private String direccio;
   private String cp;
   private String ciutat;
   private String provincia;
   private String telefon;
   private String fax;
   private String email;
   private Date dataConstitucio;
   private List<Fitxers.Fitxer> documentsEscrituraList;
   private Fitxers.Fitxer documentREA;
   private String classificacioString;
   private Fitxers.Fitxer classificacioFileROLECE;
   private Fitxers.Fitxer classificacioFileJCCaib;
   private Fitxers.Fitxer classificacioFileJCA;
   private Date dataVigenciaClassificacioROLECE;
   private Date dataVigenciaClassificacioJCCaib;
   private Date dataVigenciaClassificacioJCA;
   private String administradorsString;
   private List<Administrador> administradors;   
   private Date dateExpAcreditacio1;
   private Date dateExpAcreditacio2;
   private Date dateExpAcreditacio3;
   private Fitxers.Fitxer solEconomica;
   private Date exerciciEconomic;
   private Date registreMercantilData;
   private Fitxers.Fitxer solTecnica;
   private double ratioAP;
   private String informacioAdicional;
   private UTE ute;
   private boolean isPime;
   private boolean activa;
   private Empresa succesora;
   private Fitxers.Fitxer succesoraFile;
   private String motiuExtincio;
   private Fitxers.Fitxer extincioFile;
   private double totalPbasePeriodeAdjudicat;
   private double totalPbasePeriode;
   private double totalPLicPeriode;
   private boolean prohibicioContractar;
   private List<Fitxers.Fitxer> documentsProhibicioContractarList;
   
   public Empresa() {
 
   }
 
   public String getCif() {
       return cif;
   }
 
   public void setCif(String cif) {
       this.cif = cif;
   }
 
   public String getName() {
       return name;
   }
 
   public void setName(String name) {
       this.name = name;
   }
   
   public String getDireccio() {
       return direccio;
   }
 
   public void setDireccio(String direccio) {
       this.direccio = direccio;
   }
   
   public String getCP() {
       return cp;
   }
 
   public void setCP(String cp) {
       this.cp = cp;
   }
   
   public String getCiutat() {
       return ciutat;
   }
 
   public void setCiutat(String ciutat) {
       this.ciutat = ciutat;
   }
   
   public String getProvincia() {
       return provincia;
   }
 
   public void setProvincia(String provincia) {
       this.provincia = provincia;
   }
   
   public String getTelefon() {
       return telefon;
   }
 
   public void setTelefon(String telefon) {
       this.telefon = telefon;
   }
   
   public String getEmail() {
       return email;
   }
 
   public void setEmail(String email) {
       this.email = email;
   }

	public String getFax() {
		return fax;
	}
	
	public void setFax(String fax) {
		this.fax = fax;
	}
	
	public Date getDataConstitucio() {
		return dataConstitucio;
	}
	
	public String getDataConstitucioString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataConstitucio != null) dataString = df.format(this.dataConstitucio);
		return dataString;
	}
	
	public void setDataConstitucio(Date dataConstitucio) {
		this.dataConstitucio = dataConstitucio;
	}
	
	public List<Classificacio> getClassificacio() {
		List<Empresa.Classificacio> classificacioList = new ArrayList<Empresa.Classificacio>();
		if (this.getClassificacioString() != null && !this.getClassificacioString().isEmpty()) {
			String[] classificacioString = this.getClassificacioString().split(";");
			Empresa.Classificacio classificacio = new Classificacio();
			for (String element : classificacioString) {
				classificacio = new Classificacio();
			    classificacio.setGrup(element.split("#")[0]);
			    classificacio.setSubGrup(element.split("#")[1]);
			    classificacio.setCategoria(element.split("#")[2]);
			    classificacioList.add(classificacio);
			}
		}
		return classificacioList;
	}
	
	public List<Administrador> getAdministradors() {
		return administradors;
	}
	
	public void setAdministradors(List<Administrador> administradors) {
		this.administradors = administradors;
	}
	
	public Date getDateExpAcreditacio1() {
		return dateExpAcreditacio1;
	}
	
	public String getDateExpAcreditacio1String() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dateExpAcreditacio1 != null) dataString = df.format(this.dateExpAcreditacio1);
		return dataString;
	}
	
	public void setDateExpAcreditacio1(Date dateExpAcreditacio1) {
		this.dateExpAcreditacio1 = dateExpAcreditacio1;
	}
	
	public boolean isCaducadaAcreditacio1() {
		boolean caducat = false;
		if (this.dateExpAcreditacio1 != null) {
			Calendar cad = Calendar.getInstance();
			cad.setTime(this.dateExpAcreditacio1);
			cad.add(Calendar.MONTH, 6);
			if (cad.before(Calendar.getInstance())) caducat = true;
		}
		return caducat;
	}
		
	public Date getDateExpAcreditacio2() {
		return dateExpAcreditacio2;
	}
	
	public String getDateExpAcreditacio2String() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dateExpAcreditacio2 != null) dataString = df.format(this.dateExpAcreditacio2);
		return dataString;
	}
	
	public void setDateExpAcreditacio2(Date dateExpAcreditacio2) {
		this.dateExpAcreditacio2 = dateExpAcreditacio2;
	}
	
	public boolean isCaducadaAcreditacio2() {
		boolean caducat = false;
		if (this.dateExpAcreditacio2 != null) {
			Calendar cad = Calendar.getInstance();
			cad.setTime(this.dateExpAcreditacio2);
			cad.add(Calendar.MONTH, 6);
			if (cad.before(Calendar.getInstance())) caducat = true;
		}
		return caducat;
	}
	
	public Date getDateExpAcreditacio3() {
		return dateExpAcreditacio3;
	}
	
	public String getDateExpAcreditacio3String() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dateExpAcreditacio3 != null) dataString = df.format(this.dateExpAcreditacio3);
		return dataString;
	}
	
	public void setDateExpAcreditacio3(Date dateExpAcreditacio3) {
		this.dateExpAcreditacio3 = dateExpAcreditacio3;
	}
	
	public boolean isCaducadaAcreditacio3() {
		boolean caducat = false;
		if (this.dateExpAcreditacio3 != null) {
			Calendar cad = Calendar.getInstance();
			cad.setTime(this.dateExpAcreditacio3);
			cad.add(Calendar.MONTH, 6);
			if (cad.before(Calendar.getInstance())) caducat = true;
		}
		return caducat;
	}
	
	public String getClassificacioString() {
		return classificacioString;
	}
	
	public void setClassificacioString(String getClassificacioString) {
		this.classificacioString = getClassificacioString;
	}
	
	public Fitxers.Fitxer getSolEconomica() {
		return solEconomica;
	}
	
	public void setSolEconomica(Fitxers.Fitxer solEconomica) {
		this.solEconomica = solEconomica;
	}
	
	public Fitxers.Fitxer getSolTecnica() {
		return solTecnica;
	}
	
	public void setSolTecnica(Fitxers.Fitxer solTecnica) {
		this.solTecnica = solTecnica;
	}
	
	public String getAdministradorsString() {
		return administradorsString;
	}
	
	public void setAdministradorsString(String administradorsString) {
		this.administradorsString = administradorsString;
	}
	
	public String getInformacioAdicional() {
		return informacioAdicional;
	}
	
	public void setInformacioAdicional(String informacioAdicional) {
		this.informacioAdicional = informacioAdicional;
	}
	
	public Date getExerciciEconomic() {
		return exerciciEconomic;
	}
	
	public String getExerciciEconomicString() {
		DateFormat df = new SimpleDateFormat("yyyy");	
		String dataString = "";
		if (this.exerciciEconomic != null) dataString = df.format(this.exerciciEconomic);
		return dataString;
	}
	
	public void setExerciciEconomic(Date exerciciEconomic) {
		this.exerciciEconomic = exerciciEconomic;
	}
	
	public Date getRegistreMercantilData() {
		return registreMercantilData;
	}
	
	public String getRegistreMercantilDataString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.registreMercantilData != null) dataString = df.format(this.registreMercantilData);
		return dataString;
	}
	
	public void setRegistreMercantilData(Date registreMercantilData) {
		this.registreMercantilData = registreMercantilData;
	}
	
	public double getRatioAP() {
		return ratioAP;
	}
	
	public void setRatioAP(double ratioAP) {
		this.ratioAP = ratioAP;
	}
	
	public UTE getUte() {
		return ute;
	}
	
	public void setUte(UTE ute) {
		this.ute = ute;
	}
	public boolean isUte() {
		return this.ute != null;
	}
	
	public boolean isPime() {
		return isPime;
	}

	public void setPime(boolean isPime) {
		this.isPime = isPime;
	}

	public Fitxers.Fitxer getClassificacioFileROLECE() {
		return classificacioFileROLECE;
	}

	public void setClassificacioFileROLECE(Fitxers.Fitxer classificacioFileROLECE) {
		this.classificacioFileROLECE = classificacioFileROLECE;
	}

	public Fitxers.Fitxer getClassificacioFileJCCaib() {
		return classificacioFileJCCaib;
	}

	public void setClassificacioFileJCCaib(Fitxers.Fitxer classificacioFileJCCaib) {
		this.classificacioFileJCCaib = classificacioFileJCCaib;
	}

	public Fitxers.Fitxer getClassificacioFileJCA() {
		return classificacioFileJCA;
	}

	public void setClassificacioFileJCA(Fitxers.Fitxer classificacioFileJCA) {
		this.classificacioFileJCA = classificacioFileJCA;
	}

	public Date getDataVigenciaClassificacioROLECE() {
		return dataVigenciaClassificacioROLECE;
	}

	public String getDataVigenciaClassificacioROLECEString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataVigenciaClassificacioROLECE != null) dataString = df.format(this.dataVigenciaClassificacioROLECE);
		return dataString;
	}
	
	public void setDataVigenciaClassificacioROLECE(Date dataVigenciaClassificacioROLECE) {
		this.dataVigenciaClassificacioROLECE = dataVigenciaClassificacioROLECE;
	}

	public Date getDataVigenciaClassificacioJCCaib() {
		return dataVigenciaClassificacioJCCaib;
	}
	
	public String getDataVigenciaClassificacioJCCaibString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataVigenciaClassificacioJCCaib != null) dataString = df.format(this.dataVigenciaClassificacioJCCaib);
		return dataString;
	}

	public void setDataVigenciaClassificacioJCCaib(Date dataVigenciaClassificacioJCCaib) {
		this.dataVigenciaClassificacioJCCaib = dataVigenciaClassificacioJCCaib;
	}

	public Date getDataVigenciaClassificacioJCA() {
		return dataVigenciaClassificacioJCA;
	}
	
	public String getDataVigenciaClassificacioJCAString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		String dataString = "";
		if (this.dataVigenciaClassificacioJCA != null) dataString = df.format(this.dataVigenciaClassificacioJCA);
		return dataString;
	}

	public void setDataVigenciaClassificacioJCA(Date dataVigenciaClassificacioJCA) {
		this.dataVigenciaClassificacioJCA = dataVigenciaClassificacioJCA;
	}

	public List<Fitxers.Fitxer> getDocumentsEscrituraList() {
		return documentsEscrituraList;
	}

	public void setDocumentsEscrituraList(List<Fitxers.Fitxer> documentsEscrituraList) {
		this.documentsEscrituraList = documentsEscrituraList;
	}

	public boolean isActiva() {
		return activa;
	}

	public void setActiva(boolean activa) {
		this.activa = activa;
	}

	public Empresa getSuccesora() {
		return succesora;
	}

	public void setSuccesora(Empresa succesora) {
		this.succesora = succesora;
	}

	public String getMotiuExtincio() {
		return motiuExtincio;
	}

	public void setMotiuExtincio(String motiuExtincio) {
		this.motiuExtincio = motiuExtincio;
	}

	public Fitxers.Fitxer getSuccesoraFile() {
		return succesoraFile;
	}

	public void setSuccesoraFile(Fitxers.Fitxer succesoraFile) {
		this.succesoraFile = succesoraFile;
	}

	public Fitxers.Fitxer getExtincioFile() {
		return extincioFile;
	}

	public void setExtincioFile(Fitxers.Fitxer extincioFile) {
		this.extincioFile = extincioFile;
	}

	public Fitxers.Fitxer getDocumentREA() {
		return documentREA;
	}

	public void setDocumentREA(Fitxers.Fitxer documentREA) {
		this.documentREA = documentREA;
	}

	public double getTotalPbasePeriode() {
		return totalPbasePeriode;
	}
	
	public String getTotalPbasePeriodeString() {
		DecimalFormat num = new DecimalFormat("#,##0.00");
	    return num.format(this.totalPbasePeriode) + '€';
	}

	public void setTotalPbasePeriode(double totalPbasePeriode) {
		this.totalPbasePeriode = totalPbasePeriode;
	}

	public double getTotalPLicPeriode() {
		return totalPLicPeriode;
	}
	
	public String getTotalPLicPeriodeString() {
		DecimalFormat num = new DecimalFormat("#,##0.00");
	    return num.format(this.totalPLicPeriode) + '€';
	}

	public void setTotalPLicPeriode(double totalPLicPeriode) {
		this.totalPLicPeriode = totalPLicPeriode;
	}

	public boolean isProhibicioContractar() {
		return prohibicioContractar;
	}

	public void setProhibicioContractar(boolean prohibicioContractar) {
		this.prohibicioContractar = prohibicioContractar;
	}

	public List<Fitxers.Fitxer> getDocumentsProhibicioContractarList() {
		return documentsProhibicioContractarList;
	}

	public void setDocumentsProhibicioContractarList(List<Fitxers.Fitxer> documentsProhibicioContractarList) {
		this.documentsProhibicioContractarList = documentsProhibicioContractarList;
	}

	public double getTotalPbasePeriodeAdjudicat() {
		return totalPbasePeriodeAdjudicat;
	}
	
	public String getTotalPbasePeriodeAdjudicatString() {
		DecimalFormat num = new DecimalFormat("#,##0.00");
	    return num.format(this.totalPbasePeriodeAdjudicat) + '€';
	}

	public void setTotalPbasePeriodeAdjudicat(double totalPbasePeriodeAdjudicat) {
		this.totalPbasePeriodeAdjudicat = totalPbasePeriodeAdjudicat;
	}

}