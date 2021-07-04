package sdu;
//this class defines each patient record and its events sequence (static and dynamic features)
import java.util.ArrayList;


public class ALSPatientSDU {

	private String id = null;	//Unique identifier for each patient in the dataset

	//Demographics data
	private String DEMOGRAPHICS_sex = null;
	private String DEMOGRAPHICS_age = null;
	
	//Subject ALS History
	private ArrayList<SymptomLocationSDU> SUBJECT_ALS_HISTORY_symptLoc = new ArrayList<SymptomLocationSDU>();
	private int SUBJECT_ALS_HISTORY_onsetDelta = 0;	//start time of disease (always negative)
	private int SUBJECT_ALS_HISTORY_diagnosisDelta = 0;	//clinical diagnosis time of disease (always negative)
	private String SUBJECT_ALS_HISTORY_siteOfOnset = null;	//onset with limb or bulbar
	
	//Concomitant medication use
	private ArrayList<MedicationSDU> CONCOMITANT_MEDICATION_medication = new ArrayList<MedicationSDU>();
	
	//Death data
	private String DEATH_DATA_subjectDied = null;	//yes or no
	private int DEATH_DATA_deathDay = 0;	//day of death (always positive)
	
	//Treatment group
	private String TREATMENT_GROUP_studyArm = null;	//placebo or active
	private int TREATMENT_GROUP_treatmentGroupDelta = -1;	//time between diagnosis and first medication (always positive)
	
	//Riluzole use
	private String RELUZOLE_subjectUseReluzole = null;	//yes or no
	private int RELUZOLE_reluzoleUseDelta = -1;	//time of use reluzole (zero or positive)
	
	//sequence of dynamic features for each patient
	private ArrayList<ALSEventSDU> patientSequence = new ArrayList<ALSEventSDU>();
	
	private double slope = 0;	//slope of the ALSFRS score
	private String slopeType = null;	//fast, slow, intermediate
	private String alsfrsType = null; //ALSFRS or ALSFRS-R

	//Constructor
	public ALSPatientSDU(String id, String dEMOGRAPHICS_sex, String dEMOGRAPHICS_age) {
		super();
		this.id = id;
		DEMOGRAPHICS_sex = dEMOGRAPHICS_sex; // male or female
		DEMOGRAPHICS_age = dEMOGRAPHICS_age;
	}
	
	

	public String getAlsfrsType() {
		return alsfrsType;
	}



	public void setAlsfrsType(String alsfrsType) {
		this.alsfrsType = alsfrsType;
	}



	public String getSlopeType() {
		return slopeType;
	}



	public void setSlopeType(String slopeType) {
		this.slopeType = slopeType;
	}



	public double getSlope() {
		return slope;
	}


	public void setSlope(double slope) {
		this.slope = slope;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDEMOGRAPHICS_sex() {
		return DEMOGRAPHICS_sex;
	}

	public void setDEMOGRAPHICS_sex(String dEMOGRAPHICS_sex) {
		DEMOGRAPHICS_sex = dEMOGRAPHICS_sex;
	}

	public String getDEMOGRAPHICS_age() {
		return DEMOGRAPHICS_age;
	}

	public void setDEMOGRAPHICS_age(String dEMOGRAPHICS_age) {
		DEMOGRAPHICS_age = dEMOGRAPHICS_age;
	}

	public ArrayList<SymptomLocationSDU> getSUBJECT_ALS_HISTORY_symptLoc() {
		return SUBJECT_ALS_HISTORY_symptLoc;
	}

	public void setSUBJECT_ALS_HISTORY_symptLoc(
			ArrayList<SymptomLocationSDU> sUBJECT_ALS_HISTORY_symptLoc) {
		SUBJECT_ALS_HISTORY_symptLoc = sUBJECT_ALS_HISTORY_symptLoc;
	}

	public int getSUBJECT_ALS_HISTORY_onsetDelta() {
		return SUBJECT_ALS_HISTORY_onsetDelta;
	}

	public void setSUBJECT_ALS_HISTORY_onsetDelta(int sUBJECT_ALS_HISTORY_onsetDelta) {
		SUBJECT_ALS_HISTORY_onsetDelta = sUBJECT_ALS_HISTORY_onsetDelta;
	}

	public int getSUBJECT_ALS_HISTORY_diagnosisDelta() {
		return SUBJECT_ALS_HISTORY_diagnosisDelta;
	}

	public void setSUBJECT_ALS_HISTORY_diagnosisDelta(
			int sUBJECT_ALS_HISTORY_diagnosisDelta) {
		SUBJECT_ALS_HISTORY_diagnosisDelta = sUBJECT_ALS_HISTORY_diagnosisDelta;
	}

	public String getSUBJECT_ALS_HISTORY_siteOfOnset() {
		return SUBJECT_ALS_HISTORY_siteOfOnset;
	}

	public void setSUBJECT_ALS_HISTORY_siteOfOnset(
			String sUBJECT_ALS_HISTORY_siteOfOnset) {
		SUBJECT_ALS_HISTORY_siteOfOnset = sUBJECT_ALS_HISTORY_siteOfOnset;
	}

	public ArrayList<MedicationSDU> getCONCOMITANT_MEDICATION_medication() {
		return CONCOMITANT_MEDICATION_medication;
	}

	public void setCONCOMITANT_MEDICATION_medication(
			ArrayList<MedicationSDU> cONCOMITANT_MEDICATION_medication) {
		CONCOMITANT_MEDICATION_medication = cONCOMITANT_MEDICATION_medication;
	}

	public String getDEATH_DATA_subjectDied() {
		return DEATH_DATA_subjectDied;
	}

	public void setDEATH_DATA_subjectDied(String dEATH_DATA_subjectDied) {
		DEATH_DATA_subjectDied = dEATH_DATA_subjectDied;
	}

	public int getDEATH_DATA_deathDay() {
		return DEATH_DATA_deathDay;
	}

	public void setDEATH_DATA_deathDay(int dEATH_DATA_deathDay) {
		DEATH_DATA_deathDay = dEATH_DATA_deathDay;
	}

	public String getTREATMENT_GROUP_studyArm() {
		return TREATMENT_GROUP_studyArm;
	}

	public void setTREATMENT_GROUP_studyArm(String tREATMENT_GROUP_studyArm) {
		TREATMENT_GROUP_studyArm = tREATMENT_GROUP_studyArm;
	}

	public int getTREATMENT_GROUP_treatmentGroupDelta() {
		return TREATMENT_GROUP_treatmentGroupDelta;
	}

	public void setTREATMENT_GROUP_treatmentGroupDelta(
			int tREATMENT_GROUP_treatmentGroupDelta) {
		TREATMENT_GROUP_treatmentGroupDelta = tREATMENT_GROUP_treatmentGroupDelta;
	}

	public String getRELUZOLE_subjectUseReluzole() {
		return RELUZOLE_subjectUseReluzole;
	}

	public void setRELUZOLE_subjectUseReluzole(String rELUZOLE_subjectUseReluzole) {
		RELUZOLE_subjectUseReluzole = rELUZOLE_subjectUseReluzole;
	}

	public int getRELUZOLE_reluzoleUseDelta() {
		return RELUZOLE_reluzoleUseDelta;
	}

	public void setRELUZOLE_reluzoleUseDelta(int rELUZOLE_reluzoleUseDelta) {
		RELUZOLE_reluzoleUseDelta = rELUZOLE_reluzoleUseDelta;
	}

	public ArrayList<ALSEventSDU> getPatientSequence() {
		return patientSequence;
	}

	public void setPatientSequence(ArrayList<ALSEventSDU> patientSequence) {
		this.patientSequence = patientSequence;
	}
	
	
	
	
}//class ALSPatient


