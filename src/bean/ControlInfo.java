package bean;

import java.util.List;

public class ControlInfo {
	
	private Centre centre;
	private List<Tasca> tasquesList;
	
	public ControlInfo() {
		 
	}

	public List<Tasca> getTasquesList() {
		return tasquesList;
	}

	public void setTasquesList(List<Tasca> tasquesList) {
		this.tasquesList = tasquesList;
	}

	public Centre getCentre() {
		return centre;
	}

	public void setCentre(Centre centre) {
		this.centre = centre;
	}
}
