package bean;

public class Credit {
	private String codi;
	private double presupost;
	
	public Credit() {
		 
	}
 
	public Credit(String codi, double presupost) {
		this.codi = codi;
		this.presupost = presupost;
	}
	
	public String getCodi() {
	    return codi;
	}
	 
	public void setCodi(String codi) {
		this.codi = codi;
	}
	
	public double getPresupost() {
	    return presupost;
	}
	 
	public void setPresupost(double presupost) {
		this.presupost = presupost;
	}
}
