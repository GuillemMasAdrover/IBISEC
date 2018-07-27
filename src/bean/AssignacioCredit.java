package bean;

import java.text.DecimalFormat;
import java.util.Date;

public class AssignacioCredit {
	private String idAssignacio;	
	private String idInforme;
	private InformeActuacio informe;
	private boolean reserva;
	private User usuReserva;
	private Date datareserva;
	private boolean assignacio;
	private User usuAssignacio;
	private Date dataAssignacio;
	private String comentari;
	private User usuCre;
	private Date dataCre;
	private Partida partida;
	private double valorPA;
	private double valorPD;
	private boolean feder;
	private boolean bei;
	  
	public AssignacioCredit(){
		
	}

	public String getIdAssignacio() {
		return idAssignacio;
	}

	public void setIdAssignacio(String idAssignacio) {
		this.idAssignacio = idAssignacio;
	}

	public boolean isReserva() {
		return reserva;
	}

	public void setReserva(boolean reserva) {
		this.reserva = reserva;
	}

	public User getUsuReserva() {
		return usuReserva;
	}

	public void setUsuReserva(User usuReserva) {
		this.usuReserva = usuReserva;
	}

	public Date getDatareserva() {
		return datareserva;
	}

	public void setDatareserva(Date datareserva) {
		this.datareserva = datareserva;
	}

	public boolean isAssignacio() {
		return assignacio;
	}

	public void setAssignacio(boolean assignacio) {
		this.assignacio = assignacio;
	}

	public User getUsuAssignacio() {
		return usuAssignacio;
	}

	public void setUsuAssignacio(User usuAssignacio) {
		this.usuAssignacio = usuAssignacio;
	}

	public Date getDataAssignacio() {
		return dataAssignacio;
	}

	public void setDataAssignacio(Date dataAssignacio) {
		this.dataAssignacio = dataAssignacio;
	}

	public String getComentari() {
		return comentari;
	}

	public void setComentari(String comentari) {
		this.comentari = comentari;
	}

	public User getUsuCre() {
		return usuCre;
	}

	public void setUsuCre(User usuCre) {
		this.usuCre = usuCre;
	}

	public Date getDataCre() {
		return dataCre;
	}

	public void setDataCre(Date dataCre) {
		this.dataCre = dataCre;
	}

	public Partida getPartida() {
		return partida;
	}

	public void setPartida(Partida partida) {
		this.partida = partida;
	}

	public double getValorPA() {
		return valorPA;
	}
	
	public String getValorPAFormat() {
		DecimalFormat num = new DecimalFormat("#,##0.00");
	    return num.format(this.valorPA) + '€';
	}

	public void setValorPA(double valorPA) {
		this.valorPA = valorPA;
	}

	public double getValorPD() {
		return valorPD;
	}
	
	public String getValorPDFormat() {
		DecimalFormat num = new DecimalFormat("#,##0.00");
	    return num.format(this.valorPD) + '€';
	}

	public void setValorPD(double valorPD) {
		this.valorPD = valorPD;
	}

	public boolean isFeder() {
		return feder;
	}

	public void setFeder(boolean feder) {
		this.feder = feder;
	}

	public boolean isBei() {
		return bei;
	}

	public void setBei(boolean bei) {
		this.bei = bei;
	}

	public String getIdInforme() {
		return idInforme;
	}

	public void setIdInforme(String idInforme) {
		this.idInforme = idInforme;
	}

	public InformeActuacio getInforme() {
		return informe;
	}

	public void setInforme(InformeActuacio informe) {
		this.informe = informe;
	}
}
