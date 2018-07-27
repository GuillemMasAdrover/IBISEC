package bean;

public class User {
	private int idUsuari;
	private String usuari;
	private String name;
	private String llinatges;
	private String rol;
	private String password;
	private String carreg;
	private String departament;
	private String alias;
	private int vacances;
	private int permisos;
	private boolean actiu;
	   public User() {
	 
	   }

	   public void setIdUsuari(int idUsuari){
		   this.idUsuari = idUsuari;
	   }
	   
	   public int getIdUsuari(){
		   return idUsuari;
	   }
	   
	   public String getName() {
	       return name;
	   }
	 
	   public void setName(String name) {
	       this.name = name;
	   }
	   
	   public String getLlinatges() {
	       return llinatges;
	   }
	 
	   public void setLlinatges(String llinatges) {
	       this.llinatges = llinatges;
	   }
	   
	   public String getNomComplet() {
		   if (this.alias == null || this.alias.isEmpty()){
			   return this.name + " " + this.llinatges;
		   } else {
			   return this.alias;
		   }
	   }
	   
	   public String getNomCompletReal() {
		   return this.name + " " + this.llinatges;		  
	   }
	   
	   public String getRol() {
	       return rol;
	   }
	 
	   public void setRol(String rol) {
	       this.rol = rol;
	   }
	   
	   public String getPassword() {
	       return password;
	   }
	 
	   public void setPassword(String password) {
	       this.password = password;
	   }
	   
	   public String getCarreg() {
	       return carreg;
	   }
	 
	   public void setCarreg(String carreg) {
	       this.carreg = carreg;
	   }

	public String getUsuari() {
		return usuari;
	}

	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}

	public String getDepartament() {
		return departament;
	}

	public void setDepartament(String departament) {
		this.departament = departament;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getVacances() {
		return vacances;
	}

	public void setVacances(int vacances) {
		this.vacances = vacances;
	}

	public int getPermisos() {
		return permisos;
	}

	public void setPermisos(int permisos) {
		this.permisos = permisos;
	}

	public boolean isActiu() {
		return actiu;
	}

	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}
}
