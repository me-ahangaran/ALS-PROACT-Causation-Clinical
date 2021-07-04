package sdu;
//this class defines vital signs such as: blood pressure heart pulse and so on
public class VitalSignSDU {

	private int pulse = -1;	//heart pulse (always positive)
	private double respiratoryRate = -1;	//always positive
	private double temprature = -1;	//always positive
	private double weight = -1;	//always positive
	private int bloodPressureDiastolic = -1;	//always positive
	private int bloodPressureSystolic = -1;	//always positive
	
	public int getPulse() {
		return pulse;
	}
	public void setPulse(int pulse) {
		this.pulse = pulse;
	}
	public double getRespiratoryRate() {
		return respiratoryRate;
	}
	public void setRespiratoryRate(double respiratoryRate) {
		this.respiratoryRate = respiratoryRate;
	}
	public double getTemprature() {
		return temprature;
	}
	public void setTemprature(double temprature) {
		this.temprature = temprature;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public int getBloodPressureDiastolic() {
		return bloodPressureDiastolic;
	}
	public void setBloodPressureDiastolic(int bloodPressureDiastolic) {
		this.bloodPressureDiastolic = bloodPressureDiastolic;
	}
	public int getBloodPressureSystolic() {
		return bloodPressureSystolic;
	}
	public void setBloodPressureSystolic(int bloodPressureSystolic) {
		this.bloodPressureSystolic = bloodPressureSystolic;
	}
	
}//class VitalSign
