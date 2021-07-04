package sdu;

import java.util.ArrayList;

public class CausalChainSDU {

	ArrayList<Integer> featureNums = new ArrayList<Integer>();
	ArrayList<Double> causalEntropy = new ArrayList<Double>();
	ArrayList<Double> causalTendency = new ArrayList<Double>();
	ArrayList<Double> CTER = new ArrayList<Double>();
	ArrayList<String> featureNames = new ArrayList<String>();
	ArrayList<Double> death = new ArrayList<Double>();
	ArrayList<Double> age = new ArrayList<Double>();
	ArrayList<Double> featureChanges = new ArrayList<Double>();
	ArrayList<Double> targetChanges = new ArrayList<Double>();
	ArrayList<Double> targetChangesAbs = new ArrayList<Double>();
	ArrayList<Double> targetProb = new ArrayList<Double>();
	ArrayList<Double> densityProb = new ArrayList<Double>();
	ArrayList<Double> timePos = new ArrayList<Double>();
	ArrayList<Double> timePosProb = new ArrayList<Double>();
	ArrayList<Double> menPercent = new ArrayList<Double>();
	ArrayList<Double> targetEntropy = new ArrayList<Double>();
	double avgCTER = 0;	//average value of CTER parameter
	double avgTargetEntropy = 0;	//average of target entropy
	
	
	
	public ArrayList<Double> getTargetChangesAbs() {
		return targetChangesAbs;
	}

	public void setTargetChangesAbs(ArrayList<Double> targetChangesAbs) {
		this.targetChangesAbs = targetChangesAbs;
	}

	public double getAvgTargetEntropy() {
		return avgTargetEntropy;
	}

	public void setAvgTargetEntropy(double avgTargetEntropy) {
		this.avgTargetEntropy = avgTargetEntropy;
	}

	public double getAvgCTER() {
		return avgCTER;
	}

	public void setAvgCTER(double avgCTER) {
		this.avgCTER = avgCTER;
	}

	public ArrayList<Double> getTargetEntropy() {
		return targetEntropy;
	}

	public void setTargetEntropy(ArrayList<Double> targetEntropy) {
		this.targetEntropy = targetEntropy;
	}

	public ArrayList<String> getFeatureNames() {
		return featureNames;
	}
	
	public void setFeatureNames(ArrayList<String> featureNames){
		this.featureNames = featureNames;
	}
	public ArrayList<Double> getDeath(){
		return death;
	}

	public void setDeath(ArrayList<Double> death) {
		this.death = death;
	}
	public ArrayList<Double> getAge() {
		return age;
	}
	public void setAge(ArrayList<Double> age) {
		this.age = age;
	}
	public ArrayList<Double> getFeatureChanges() {
		return featureChanges;
	}
	public void setFeatureChanges(ArrayList<Double> featureChanges) {
		this.featureChanges = featureChanges;
	}
	public ArrayList<Double> getTargetChanges() {
		return targetChanges;
	}
	public void setTargetChanges(ArrayList<Double> targetChanges) {
		this.targetChanges = targetChanges;
	}
	public ArrayList<Double> getTargetProb() {
		return targetProb;
	}
	public void setTargetProb(ArrayList<Double> targetProb) {
		this.targetProb = targetProb;
	}
	public ArrayList<Double> getDensityProb() {
		return densityProb;
	}
	public void setDensityProb(ArrayList<Double> densityProb) {
		this.densityProb = densityProb;
	}
	public ArrayList<Double> getTimePos() {
		return timePos;
	}
	public void setTimePos(ArrayList<Double> timePos) {
		this.timePos = timePos;
	}
	public ArrayList<Double> getTimePosProb() {
		return timePosProb;
	}
	public void setTimePosProb(ArrayList<Double> timePosProb) {
		this.timePosProb = timePosProb;
	}
	public ArrayList<Double> getMenPercent() {
		return menPercent;
	}
	public void setMenPercent(ArrayList<Double> menPercent) {
		this.menPercent = menPercent;
	}
	public ArrayList<Integer> getFeatureNums() {
		return featureNums;
	}
	public void setFeatureNums(ArrayList<Integer> featureNums) {
		this.featureNums = featureNums;
	}
	public ArrayList<Double> getCausalEntropy() {
		return causalEntropy;
	}
	public void setCausalEntropy(ArrayList<Double> causalEntropy) {
		this.causalEntropy = causalEntropy;
	}
	public ArrayList<Double> getCausalTendency() {
		return causalTendency;
	}
	public void setCausalTendency(ArrayList<Double> causalTendency) {
		this.causalTendency = causalTendency;
	}
	public ArrayList<Double> getCTER() {
		return CTER;
	}
	public void setCTER(ArrayList<Double> cTER) {
		CTER = cTER;
	}
	
	
	
}//class CausalChainElement
