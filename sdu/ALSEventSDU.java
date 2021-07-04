package sdu;

//this class defines the events of each patient consist of dynamic features with a time corresponding to it
public class ALSEventSDU {

	private double delta = -1;	//time of event in days (current time is 0)
	
	//ALSFRS
	private ALSFRSSDU target = null;	//target value for each patient event (file: ALSFRS)
	
	//Vital sign
	private VitalSignSDU vitalSign;	//vital sign data (file: VitalSign)
	
	//Forced vital capacity
	private double FVC_subjectLiters = -1;	//unit is liters
	
	//Slow vital capacity
	private double SVC_subjectLiters = -1;	//unit is liters (Not considered in this project)
	
	//Lab data. normal, trace, small, moderate, large (without values)
	private LabDataSDU lab = null;

	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

	public ALSFRSSDU getTarget() {
		return target;
	}

	public void setTarget(ALSFRSSDU target) {
		this.target = target;
	}

	public VitalSignSDU getVitalSign() {
		return vitalSign;
	}

	public void setVitalSign(VitalSignSDU vitalSign) {
		this.vitalSign = vitalSign;
	}

	public double getFVC_subjectLiters() {
		return FVC_subjectLiters;
	}

	public void setFVC_subjectLiters(double fVC_subjectLiters) {
		FVC_subjectLiters = fVC_subjectLiters;
	}

	public double getSVC_subjectLiters() {
		return SVC_subjectLiters;
	}

	public void setSVC_subjectLiters(double sVC_subjectLiters) {
		SVC_subjectLiters = sVC_subjectLiters;
	}

	public LabDataSDU getLab() {
		return lab;
	}

	public void setLab(LabDataSDU lab) {
		this.lab = lab;
	}
	
	
	
}//class ALSEvent
