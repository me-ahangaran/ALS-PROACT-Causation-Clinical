package sdu;

//class for patient medication including dose, frequency and so on (File: ConMedicationUse)
public class MedicationSDU {

	private String medicationName = null;	//name of medication
	private int startDelta = 0;	//start time of use (+ or - or null)
	private int stopDelta = 0;	//end time of use (+ or - or null)
	private String unit = null;	//unit of medication
	private String dose = null;	//amount of usage (double or null or UNK)
	private String route = null;	//terms of use (eg. oral, topical, ...)
	private String frequency = null; //frequency of usage (eg. twice a day, every day, ...)
	
	
	public String getMedicationName() {
		return medicationName;
	}
	public void setMedicationName(String medicationName) {
		this.medicationName = medicationName;
	}
	public int getStartDelta() {
		return startDelta;
	}
	public void setStartDelta(int startDelta) {
		this.startDelta = startDelta;
	}
	public int getStopDelta() {
		return stopDelta;
	}
	public void setStopDelta(int stopDelta) {
		this.stopDelta = stopDelta;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getDose() {
		return dose;
	}
	public void setDose(String dose) {
		this.dose = dose;
	}
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	
	
}//class Medication
