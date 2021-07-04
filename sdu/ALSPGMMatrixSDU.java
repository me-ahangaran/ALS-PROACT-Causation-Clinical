package sdu;

//this class defines each index of PGM matrix including edgs of PGM
public class ALSPGMMatrixSDU {

	private double causalProb;	//causal probability (percent of patients that has this causality)
	private double menPercent;	//percent of mens from this causality
	private double entropyDensity;	
	private double entropyTarget;
	private double entropyTimePosition;
	private double prevChange;	//prev change at max value in density function
	private double nextChange;	//next change at max value in density function
	private double densProb;	//probability of density fuction at max point
	private double targetChange;	//target change at max value in target density function
	private double targetChangeAbs;	//absolute value of target change
	private double timePosChange;	//time position value at max value in time position density function
	private double timePosProb;
	private int row, col;	//row and column of matrix
	private double targetProb;	// probability of target at max value in target density function
	private double targetProbAbs;	//absolute value of target probability
	private double deathProb;	//probability of death
	private double avgAge;
	
	
	
	public double getTargetProbAbs() {
		return targetProbAbs;
	}
	public void setTargetProbAbs(double targetProbAbs) {
		this.targetProbAbs = targetProbAbs;
	}
	public double getTargetChangeAbs() {
		return targetChangeAbs;
	}
	public void setTargetChangeAbs(double targetChangeAbs) {
		this.targetChangeAbs = targetChangeAbs;
	}
	public double getAvgAge() {
		return avgAge;
	}
	public void setAvgAge(double avgAge) {
		this.avgAge = avgAge;
	}
	public double getDeathProb() {
		return deathProb;
	}
	public void setDeathProb(double deathProb) {
		this.deathProb = deathProb;
	}
	public double getTimePosProb() {
		return timePosProb;
	}
	public void setTimePosProb(double timePosProb) {
		this.timePosProb = timePosProb;
	}
	public double getCausalProb() {
		return causalProb;
	}
	public void setCausalProb(double causalProb) {
		this.causalProb = causalProb;
	}
	public double getMenPercent() {
		return menPercent;
	}
	public void setMenPercent(double menPercent) {
		this.menPercent = menPercent;
	}
	public double getEntropyDensity() {
		return entropyDensity;
	}
	public void setEntropyDensity(double entropyDensity) {
		this.entropyDensity = entropyDensity;
	}
	public double getEntropyTarget() {
		return entropyTarget;
	}
	public void setEntropyTarget(double entropyTarget) {
		this.entropyTarget = entropyTarget;
	}
	public double getEntropyTimePosition() {
		return entropyTimePosition;
	}
	public void setEntropyTimePosition(double entropyTimePosition) {
		this.entropyTimePosition = entropyTimePosition;
	}
	public double getPrevChange() {
		return prevChange;
	}
	public void setPrevChange(double prevChange) {
		this.prevChange = prevChange;
	}
	public double getNextChange() {
		return nextChange;
	}
	public void setNextChange(double nextChange) {
		this.nextChange = nextChange;
	}
	public double getDensProb() {
		return densProb;
	}
	public void setDensProb(double densProb) {
		this.densProb = densProb;
	}
	public double getTargetChange() {
		return targetChange;
	}
	public void setTargetChange(double targetChange) {
		this.targetChange = targetChange;
	}
	public double getTimePosChange() {
		return timePosChange;
	}
	public void setTimePosChange(double timePosChange) {
		this.timePosChange = timePosChange;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public double getTargetProb() {
		return targetProb;
	}
	public void setTargetProb(double targetProb) {
		this.targetProb = targetProb;
	}
	
	
	
	
}//classALSPGMMatrix
