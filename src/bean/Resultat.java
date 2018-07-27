package bean;

import java.util.List;

public class Resultat {
	public class Estadistiques {
		private int aprovadesPA;
		private int aprovadesPT;
		private int pendents;
		private int tancades;
		
		public Estadistiques() {
			
		}
		
		public int getAprovadesPA() {
			return aprovadesPA;
		}

		public void setAprovadesPA(int aprovadesPA) {
			this.aprovadesPA = aprovadesPA;
		}

		public int getPendents() {
			return pendents;
		}

		public void setPendents(int pendents) {
			this.pendents = pendents;
		}

		public int getTancades() {
			return tancades;
		}

		public void setTancades(int tancades) {
			this.tancades = tancades;
		}

		public int getAprovadesPT() {
			return aprovadesPT;
		}

		public void setAprovadesPT(int aprovadesPT) {
			this.aprovadesPT = aprovadesPT;
		}
	}
	
	
	private Estadistiques estad;
	private List<Actuacio> llistaActuacions;
	public Resultat(){
		
	}
	public Estadistiques getEstad() {
		return estad;
	}
	public void setEstad(Estadistiques estad) {
		this.estad = estad;
	}
	public List<Actuacio> getLlistaActuacions() {
		return llistaActuacions;
	}
	public void setLlistaActuacions(List<Actuacio> llistaActuacions) {
		this.llistaActuacions = llistaActuacions;
	}
	
}
