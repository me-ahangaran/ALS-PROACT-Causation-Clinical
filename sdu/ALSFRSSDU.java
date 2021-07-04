package sdu;
//this class defines the ALS functional rating scale that shows the target of each patient event
public class ALSFRSSDU {

	//each parameter has double value [0, 4]
	private double speech = -1;
	private double salivation = -1;
	private double swallowing = -1;
	private double handwriting = -1;
	private double cuttingWithoutGastrostomy = -1;
	private double cuttingWithGastrostomy = -1;
	private double dressingAndHygiene = -1;
	private double turningInBed = -1;
	private double walking = -1;
	private double climbingStairs = -1;
	private double respiratory = -1;
	private double R_1_Dyspnea = -1;
	private double R_2_Orthopnea = -1;
	private double R_3_Respiratory_Insufficiency = -1;
	private double ALSFRSTotal = -1;	//total score for ALSFRS (max is 40)
	private double ALSFRSTotalR = -1;	//total score for ALSFRS-R (max is 48)
	
	
	public double getR_1_Dyspnea() {
		return R_1_Dyspnea;
	}
	public void setR_1_Dyspnea(double r_1_Dyspnea) {
		R_1_Dyspnea = r_1_Dyspnea;
	}
	public double getR_2_Orthopnea() {
		return R_2_Orthopnea;
	}
	public void setR_2_Orthopnea(double r_2_Orthopnea) {
		R_2_Orthopnea = r_2_Orthopnea;
	}
	public double getR_3_Respiratory_Insufficiency() {
		return R_3_Respiratory_Insufficiency;
	}
	public void setR_3_Respiratory_Insufficiency(double r_3_Respiratory_Insufficiency) {
		R_3_Respiratory_Insufficiency = r_3_Respiratory_Insufficiency;
	}
	public double getSpeech() {
		return speech;
	}
	public void setSpeech(double speech) {
		this.speech = speech;
	}
	public double getRespiratory() {
		return respiratory;
	}
	public void setRespiratory(double respiratory) {
		this.respiratory = respiratory;
	}
	public double getSalivation() {
		return salivation;
	}
	public void setSalivation(double salivation) {
		this.salivation = salivation;
	}
	public double getSwallowing() {
		return swallowing;
	}
	public void setSwallowing(double swallowing) {
		this.swallowing = swallowing;
	}
	public double getHandwriting() {
		return handwriting;
	}
	public void setHandwriting(double handwriting) {
		this.handwriting = handwriting;
	}
	public double getCuttingWithoutGastrostomy() {
		return cuttingWithoutGastrostomy;
	}
	public void setCuttingWithoutGastrostomy(double cuttingWithoutGastrostomy) {
		this.cuttingWithoutGastrostomy = cuttingWithoutGastrostomy;
	}
	public double getCuttingWithGastrostomy() {
		return cuttingWithGastrostomy;
	}
	public void setCuttingWithGastrostomy(double cuttingWithGastrostomy) {
		this.cuttingWithGastrostomy = cuttingWithGastrostomy;
	}
	public double getDressingAndHygiene() {
		return dressingAndHygiene;
	}
	public void setDressingAndHygiene(double dressingAndHygiene) {
		this.dressingAndHygiene = dressingAndHygiene;
	}
	public double getTurningInBed() {
		return turningInBed;
	}
	public void setTurningInBed(double turningInBed) {
		this.turningInBed = turningInBed;
	}
	public double getWalking() {
		return walking;
	}
	public void setWalking(double walking) {
		this.walking = walking;
	}
	public double getClimbingStairs() {
		return climbingStairs;
	}
	public void setClimbingStairs(double climbingStairs) {
		this.climbingStairs = climbingStairs;
	}
	public double getALSFRSTotal() {
		if(ALSFRSTotal != -1)
			return ALSFRSTotal;
		else
			return ALSFRSTotalR;
	}
	public void setALSFRSTotal(double aLSFRSTotal) {
		ALSFRSTotal = aLSFRSTotal;
	}
	public double getALSFRSTotalR() {
		return ALSFRSTotalR;
	}
	public void setALSFRSTotalR(double aLSFRSTotalR) {
		ALSFRSTotalR = aLSFRSTotalR;
	}
	
	
}//class ALSFRS
