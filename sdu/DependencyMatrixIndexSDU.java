package sdu;
import java.util.ArrayList;

//this class define each component of causal feature matrix
public class DependencyMatrixIndexSDU {

	private double menPercent = 0;	//percent of men that define corresponding dependency
	private double womenPercent = 0;	//percent of women define corresponding dependency
	private double causalityProbability = 0;	//percent of all patients that create this causality 
	private ArrayList<Double> timePositions = new ArrayList<Double>();	//average relative time position of related causality in all patients (range is [0, 1])
	private ArrayList<Double> timeDistances = new ArrayList<Double>();	//time distance from previous causal change (unit : day)
	private ArrayList<Double> nextChanges = new ArrayList<Double>();	// change level for each next feature
	private ArrayList<Double> prevChanges = new ArrayList<Double>();	// change level for each previous feature
	
	
	private ArrayList<String> deathProbability = new ArrayList<String>();	//percent of patients that died from this causality change
	private ArrayList<Double> ages = new ArrayList<Double>();	//average age of patients that have this causality
	private ArrayList<Double> targetChanges = new ArrayList<Double>();	//average change of relative ALS-FRS
	private ArrayList<Double> targetChangesAbs = new ArrayList<Double>();	//average change of absolute ALS-FRS

	
	private double avgTimePos = 0;
	private double avgTimeDist = 0;
	private double avgChange = 0;
	private double deathProb = 0;
	private double avgAge = 0;
	private double avgTargetChange = 0;
	
	public ArrayList<Double> getTargetChangesAbs() {
		return targetChangesAbs;
	}
	public void setTargetChangesAbs(ArrayList<Double> targetChangesAbs) {
		this.targetChangesAbs = targetChangesAbs;
	}
	
	public ArrayList<Double> getPrevChanges() {
		return prevChanges;
	}
	public void setPrevChanges(ArrayList<Double> prevChanges) {
		this.prevChanges = prevChanges;
	}
	
	public double getAvgTimePos() {
		return avgTimePos;
	}
	public void setAvgTimePos(double avgTimePos) {
		this.avgTimePos = avgTimePos;
	}
	public double getAvgTimeDist() {
		return avgTimeDist;
	}
	public void setAvgTimeDist(double avgTimeDist) {
		this.avgTimeDist = avgTimeDist;
	}
	public double getAvgChange() {
		return avgChange;
	}
	public void setAvgChange(double avgChange) {
		this.avgChange = avgChange;
	}
	public double getDeathProb() {
		return deathProb;
	}
	public void setDeathProb(double deathProb) {
		this.deathProb = deathProb;
	}
	public double getAvgAge() {
		return avgAge;
	}
	public void setAvgAge(double avgAge) {
		this.avgAge = avgAge;
	}
	public double getAvgTargetChange() {
		return avgTargetChange;
	}
	public void setAvgTargetChange(double avgTargetChange) {
		this.avgTargetChange = avgTargetChange;
	}
	public ArrayList<Double> getTargetChanges() {
		return targetChanges;
	}
	public void setTargetChanges(ArrayList<Double> targetChanges) {
		this.targetChanges = targetChanges;
	}
	public double getMenPercent() {
		return menPercent;
	}
	public void setMenPercent(double menPercent) {
		this.menPercent = menPercent;
	}
	public double getWomenPercent() {
		return womenPercent;
	}
	public void setWomenPercent(double womenPercent) {
		this.womenPercent = womenPercent;
	}
	public double getCausalityProbability() {
		return causalityProbability;
	}
	public void setCausalityProbability(double causalityProbability) {
		this.causalityProbability = causalityProbability;
	}
	public ArrayList<Double> getTimePositions() {
		return timePositions;
	}
	public void setTimePositions(ArrayList<Double> timePositions) {
		this.timePositions = timePositions;
	}
	public ArrayList<Double> getTimeDistances() {
		return timeDistances;
	}
	public void setTimeDistances(ArrayList<Double> timeDistances) {
		this.timeDistances = timeDistances;
	}
	public ArrayList<Double> getNextChanges() {
		return nextChanges;
	}
	public void setNextChanges(ArrayList<Double> changes) {
		this.nextChanges = changes;
	}
	public ArrayList<String> getDeathProbability() {
		return deathProbability;
	}
	public void setDeathProbability(ArrayList<String> deathProbability) {
		this.deathProbability = deathProbability;
	}
	public ArrayList<Double> getAges() {
		return ages;
	}
	public void setAges(ArrayList<Double> ages) {
		this.ages = ages;
	}
	
	
	
	
	
}//class DependencyMatrixIndex
