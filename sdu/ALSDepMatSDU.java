package sdu;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import contouring.KernelDensityEstimator2D;

//Main class that create PGM of ALS from feature dependency matrix
public class ALSDepMatSDU {

	private String firstTypeClass = ALSUtilsSDU.progression;	//Gender OR Progression OR Riluzole
	private String secondTypeClass = ALSUtilsSDU.fastSlope;	//Male, female OR Slow, Intermediate, Fast OR Yes, No; According to the firstTypeClass
	//number of patients is 10723 (men=6464, w=4259)
	
//	private int numOfDynamicFeaturs = 87;	//number of dynamic features = 87	
	//matrix of causal features dependency (CFD)
	private DependencyMatrixIndexSDU[][] causalFeaturesDependencyMatrix = new DependencyMatrixIndexSDU[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
//	private ALSDensity[][] causalFeatureDensityMatrix = new ALSDensity[numOfDynamicFeaturs][numOfDynamicFeaturs];	//density matrix
	int bandWidth = 10;	//bandwidth of density estimation
	ArrayList<EventChange[]> allPatientsSeq;
	
	public ArrayList<EventChange[]> getAllPatientsSeq(){
		
		return this.allPatientsSeq;
	}
	
	public void createDependencyMatrix(ArrayList<ALSPatientSDU> patientsList, String firstTypeClass, String secondTypeClass){
		this.firstTypeClass = firstTypeClass;
		this.secondTypeClass = secondTypeClass;
		double menCtr = 0; double womenCtr = 0; double slowCtr = 0; double intermediateCtr = 0; double fastCtr = 0; double numOfPatients = 0; double patientsCtr = 0;
		double riluzoleYesCtr = 0; double riluzoleNoCtr = 0;
		for(int i=0; i<ALSUtilsSDU.numOfDynamicFeatures; i++)
			for(int j=0; j<ALSUtilsSDU.numOfDynamicFeatures; j++){
				DependencyMatrixIndexSDU dmi = new DependencyMatrixIndexSDU();
				causalFeaturesDependencyMatrix[i][j] = dmi;
			}//for(j)
		numOfPatients = patientsList.size();
		
		ALSPatientSDU patient;
		String sex = "";
		String age = "";
		int patientSeqSize;	//size of each patient events sequence
		ArrayList<ALSEventSDU> patientSequence;	//event sequence of each patient
		ArrayList<ALSEventSDU> sortedPatientSequence;	//sorted sequence of each patient
		ArrayList<ALSEventSDU> currentPatientSeq = new ArrayList<ALSEventSDU>();	//current event sequence after sorting
		ALSUtilsSDU util = new ALSUtilsSDU();
		FeaturesSDU prevFeatures;	//previous event of current patient sequence
		FeaturesSDU nextFeatures;	//next event of current patient sequence
		ALSEventSDU prevEvent;	//previous event of patient sequence
		ALSEventSDU nextEvent;	//next event of patient sequence
		
		//sorting each patients sequence based on delta time
		for(int i=0; i<numOfPatients; i++){
			patientSequence = patientsList.get(i).getPatientSequence();
			sortedPatientSequence = util.sortEventsSequence(patientSequence);
			patientsList.get(i).setPatientSequence(sortedPatientSequence);
		}//for

		//create dependency matrix
		System.out.println("Creating dependency matrix indexes for "+firstTypeClass+" = "+secondTypeClass);
		allPatientsSeq = new ArrayList<ALSDepMatSDU.EventChange[]>();	//all sequence of all patients for causal tendency
		for(int i=0; i<patientsList.size(); i++){
			patient = patientsList.get(i);
			
			//Check the type of classification (Gender OR Progression OR Riluzole)
			
			//Progression
			if(firstTypeClass.equalsIgnoreCase(ALSUtilsSDU.progression)) {
				if(patient.getSlopeType() == null)
					continue;
				if(secondTypeClass.equalsIgnoreCase(ALSUtilsSDU.slowSlope)) {	//Slow
					if(!patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.slowSlope))
						continue;
				}//if(secondTypeClass)
				if(secondTypeClass.equalsIgnoreCase(ALSUtilsSDU.intermediateSlope)) {	//Intermediate
					if(!patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.intermediateSlope))
						continue;
				}//if(secondTypeClass)
				if(secondTypeClass.equalsIgnoreCase(ALSUtilsSDU.fastSlope)) {	//fast
					if(!patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.fastSlope))
						continue;
				}//if(secondTypeClass)
			}//if(firstTypeClass)
			
			//Gender
			if(firstTypeClass.equalsIgnoreCase(ALSUtilsSDU.gender)) {
				if(patient.getDEMOGRAPHICS_sex() == null)
					continue;
				if(secondTypeClass.equalsIgnoreCase(ALSUtilsSDU.male)) {	//Male
					if(!patient.getDEMOGRAPHICS_sex().equalsIgnoreCase(ALSUtilsSDU.male))
						continue;
				}//if(secondTypeClass)
				if(secondTypeClass.equalsIgnoreCase(ALSUtilsSDU.female)) {	//Female
					if(!patient.getDEMOGRAPHICS_sex().equalsIgnoreCase(ALSUtilsSDU.female))
						continue;
				}//if(secondTypeClass)
			}//if(firstTypeClass)
			
			//Riluzole
			if(firstTypeClass.equalsIgnoreCase(ALSUtilsSDU.riluzole)) {
				if(patient.getRELUZOLE_subjectUseReluzole() == null)
					continue;
				if(secondTypeClass.equalsIgnoreCase(ALSUtilsSDU.riluzoleYes)) {	//Riluzole used (yes)
					if(!patient.getRELUZOLE_subjectUseReluzole().equalsIgnoreCase(ALSUtilsSDU.riluzoleYes))
						continue;
				}//if(secondTypeClass)
				if(secondTypeClass.equalsIgnoreCase(ALSUtilsSDU.riluzoleNo)) {	//Riluzole not used (no)
					if(!patient.getRELUZOLE_subjectUseReluzole().equalsIgnoreCase(ALSUtilsSDU.riluzoleNo))
						continue;
				}//if(secondTypeClass)
			}//if(firstTypeClass)
			
			String death = patient.getDEATH_DATA_subjectDied();	//yes or no
			currentPatientSeq = patient.getPatientSequence();	//event sequence for current patient
			patientSeqSize = currentPatientSeq.size();	//number of patient events
			
			if(patientSeqSize == 0){
				allPatientsSeq.add(null);
				continue;
			}
			patientsCtr++;	//number of patients for current classification
			
			//Progression
			if(firstTypeClass.equalsIgnoreCase(ALSUtilsSDU.progression)) {
				//Riluzole
				if(patient.getRELUZOLE_subjectUseReluzole() != null && 
						patient.getRELUZOLE_subjectUseReluzole().equalsIgnoreCase(ALSUtilsSDU.riluzoleYes)) 
					riluzoleYesCtr++;
				
				if(patient.getRELUZOLE_subjectUseReluzole() != null && 
						patient.getRELUZOLE_subjectUseReluzole().equalsIgnoreCase(ALSUtilsSDU.riluzoleNo)) 
					riluzoleNoCtr++;
				
				//Gender
				if(patient.getDEMOGRAPHICS_sex() != null && 
						patient.getDEMOGRAPHICS_sex().equalsIgnoreCase(ALSUtilsSDU.male))
					menCtr++;
				if(patient.getDEMOGRAPHICS_sex() != null && 
						patient.getDEMOGRAPHICS_sex().equalsIgnoreCase(ALSUtilsSDU.female))
					womenCtr++;
			}//if(firstTypeClass)
		
			//Gender
			if(firstTypeClass.equalsIgnoreCase(ALSUtilsSDU.gender)) {
				//Riluzole
				if(patient.getRELUZOLE_subjectUseReluzole() != null && 
						patient.getRELUZOLE_subjectUseReluzole().equalsIgnoreCase(ALSUtilsSDU.riluzoleYes)) 
					riluzoleYesCtr++;
				
				if(patient.getRELUZOLE_subjectUseReluzole() != null && 
						patient.getRELUZOLE_subjectUseReluzole().equalsIgnoreCase(ALSUtilsSDU.riluzoleNo)) 
					riluzoleNoCtr++;
				
				//Progression
				if(patient.getSlopeType() != null &&
						patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.fastSlope))
					fastCtr++;
				if(patient.getSlopeType() != null &&
						patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.intermediateSlope))
					intermediateCtr++;
				if(patient.getSlopeType() != null &&
						patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.slowSlope))
					slowCtr++;
			}//if(firstTypeClass)
			
			//Riluzole
			if(firstTypeClass.equalsIgnoreCase(ALSUtilsSDU.riluzole)) {
				//Gender
				if(patient.getDEMOGRAPHICS_sex() != null && 
						patient.getDEMOGRAPHICS_sex().equalsIgnoreCase(ALSUtilsSDU.male))
					menCtr++;
				if(patient.getDEMOGRAPHICS_sex() != null && 
						patient.getDEMOGRAPHICS_sex().equalsIgnoreCase(ALSUtilsSDU.female))
					womenCtr++;
				
				//Progression
				if(patient.getSlopeType() != null &&
						patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.fastSlope))
					fastCtr++;
				if(patient.getSlopeType() != null &&
						patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.intermediateSlope))
					intermediateCtr++;
				if(patient.getSlopeType() != null &&
						patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.slowSlope))
					slowCtr++;
			}//if(firstTypeClass)
			
			sex = patient.getDEMOGRAPHICS_sex();
			age = patient.getDEMOGRAPHICS_age();
			EventChange[] eventsChangeArray =  new EventChange[patientSeqSize];	//array of all changes for current patient
			EventChange evc = new EventChange();	//event change object for each event of current patient
			ArrayList<Integer> features = new ArrayList<Integer>();
			ArrayList<Double> changeVals = new ArrayList<Double>();
			features.add(0);	//add virtual feature
			changeVals.add(0.0);
			evc.setFeatures(features);
			evc.setChangeVals(changeVals);
			evc.setTime(currentPatientSeq.get(0).getDelta());
			evc.setTargetChange(0);
			eventsChangeArray[0] = evc;
			//for each event of current patient
			for(int j=1; j<patientSeqSize; j++){
				evc = new EventChange();
				prevEvent = currentPatientSeq.get(j-1);
				nextEvent = currentPatientSeq.get(j);
//				nextFeatures = new Features();
//				prevFeatures = new Features();
				double eventTime = nextEvent.getDelta();	//time of current event
				double nextTarget = nextEvent.getTarget().getALSFRSTotal();	//ALS-FRS value for current event
				double prevTarget = prevEvent.getTarget().getALSFRSTotal();	//ALS-FRS value for previous event
				
				//ALSFRS or ALSFRS-R
				if(nextEvent.getTarget().getALSFRSTotalR() != -1)
					nextTarget = nextEvent.getTarget().getALSFRSTotalR();
					else
						nextTarget = nextEvent.getTarget().getALSFRSTotal();
				
				if(prevEvent.getTarget().getALSFRSTotalR() != -1)
					prevTarget = prevEvent.getTarget().getALSFRSTotalR();
					else
						prevTarget = prevEvent.getTarget().getALSFRSTotal();
				
				
				evc.setTargetChangeAbs(nextTarget - prevTarget);//absolute value of ALSFRS change
				evc.setTargetChange((nextTarget - prevTarget)/prevTarget);	//set ALS-FRS for current event
				if(prevTarget == 0)
					evc.setTargetChange((nextTarget - prevTarget));	//at first event change
				evc.setTime(eventTime);	//set current event time to current event change object
				features = new ArrayList<Integer>();	//all feature indexes that changed
				changeVals = new ArrayList<Double>();	//all change values for features
				/******* create Feature objects for compare previous and next events for causal chain analysis
				 and set dependency matrix *******/
				
				/////////////////////////Vital sign///////////////////////
				VitalSignSDU prevVitalSign = prevEvent.getVitalSign();
				VitalSignSDU nextVitalSign = nextEvent.getVitalSign();
				if(prevVitalSign != null && nextVitalSign != null){
					
					int prevPulse = prevVitalSign.getPulse();
					int nextPulse = nextVitalSign.getPulse();					
					if(prevPulse != -1 && nextPulse != -1){
//						prevFeatures.setPulse(prevPulse);
//						nextFeatures.setPulse(nextPulse);
						int pulseChange = nextPulse - prevPulse;	//amount of pulse change
						if(Math.abs(pulseChange) >= FeatureChangeSDU.pulse){
							features.add(FeatureNumberSDU.pulse);	//add index to featuresChange list
							double pulseChangePercent = (nextPulse - prevPulse) / prevPulse;	//amount of pulse change in percent of previous value
							if(prevPulse == 0)
								pulseChangePercent =  nextPulse;
							changeVals.add(pulseChangePercent);	//add amount of pulse change to change values list
						}//if(Math.abs)
					}//if
					
					double prevRepositoryRate = prevVitalSign.getRespiratoryRate();
					double nextRepositoryRate = nextVitalSign.getRespiratoryRate();
					if(prevRepositoryRate != -1 && nextRepositoryRate != -1){
//						prevFeatures.setRespiratoryRate(prevRepositoryRate);
//						nextFeatures.setRespiratoryRate(nextRepositoryRate);
						double respChange = nextRepositoryRate - prevRepositoryRate;
						if(Math.abs(respChange) >= FeatureChangeSDU.respiratoryRate){
							features.add(FeatureNumberSDU.respiratoryRate);
							double respChangePercent = (nextRepositoryRate - prevRepositoryRate) / prevRepositoryRate;
							if(prevRepositoryRate == 0)
								respChangePercent =  nextRepositoryRate;
							changeVals.add(respChangePercent);
						}//if(Math.abs)
					}//if
					
					double prevTemperature = prevVitalSign.getTemprature();
					double nextTemperature = nextVitalSign.getTemprature();
					if(prevTemperature != -1 && nextTemperature != -1){
//						prevFeatures.setTemprature(prevTemprature);
//						nextFeatures.setTemprature(nextTemprature);
						double tempChange = nextTemperature - prevTemperature;
						if(Math.abs(tempChange) >= FeatureChangeSDU.temperature){
							features.add(FeatureNumberSDU.temperature);
							double tempChangePercent = (nextTemperature - prevTemperature) / prevTemperature;
							if(prevTemperature == 0)
								tempChangePercent =  nextTemperature;
							changeVals.add(tempChangePercent);
						}//if(Math.abs)
					}//if
					
					double prevWeight = prevVitalSign.getWeight();
					double nextWeight = nextVitalSign.getWeight();
					if(prevWeight != -1 && nextWeight != -1){
//						prevFeatures.setWeight(prevWeight);
//						nextFeatures.setWeight(nextWeight);
						double weightChange = nextWeight - prevWeight;
						if(Math.abs(weightChange) >= FeatureChangeSDU.weight){
							features.add(FeatureNumberSDU.weight);
							double weightChangePercent = (nextWeight - prevWeight) / prevWeight;
							if(prevWeight == 0)
								weightChangePercent =  nextWeight;
							changeVals.add(weightChangePercent);
						}//if(Math.abs)
					}//if
					
					int prevBloodPressureDiastolic = prevVitalSign.getBloodPressureDiastolic();
					int nextBloodPressureDiastolic = nextVitalSign.getBloodPressureDiastolic();
					if(prevBloodPressureDiastolic != -1 && nextBloodPressureDiastolic != -1){
//						prevFeatures.setBloodPressureDiastolic(prevBloodPressureDiastolic);
//						nextFeatures.setBloodPressureDiastolic(nextBloodPressureDiastolic);
						int bloodPressureDiastolicChange = nextBloodPressureDiastolic - prevBloodPressureDiastolic;
						if(Math.abs(bloodPressureDiastolicChange) >= FeatureChangeSDU.bloodPressureDiastolic){
							features.add(FeatureNumberSDU.bloodPressureDiastolic);
							double bloodPressureDiastolicChangePercent = (nextBloodPressureDiastolic - prevBloodPressureDiastolic) / prevBloodPressureDiastolic;
							if(prevBloodPressureDiastolic == 0)
								bloodPressureDiastolicChangePercent =  nextBloodPressureDiastolic;
							changeVals.add(bloodPressureDiastolicChangePercent);
						}//if(Math.abs)
					}//if
					
					int prevBloodPressureSystolic = prevVitalSign.getBloodPressureSystolic();
					int nextBloodPressureSystolic = nextVitalSign.getBloodPressureSystolic();
					if(prevBloodPressureSystolic != -1 && nextBloodPressureSystolic != -1){
//						prevFeatures.setBloodPressureSystolic(prevBloodPressureSystolic);
//						nextFeatures.setBloodPressureSystolic(nextBloodPressureSystolic);
						int bloodPressureSystolicChange = nextBloodPressureSystolic - prevBloodPressureSystolic;
						if(Math.abs(bloodPressureSystolicChange) >= FeatureChangeSDU.bloodPressureSystolic){
							features.add(FeatureNumberSDU.bloodPressureSystolic);
							double bloodPressureSystolicChangePercent = (nextBloodPressureSystolic - prevBloodPressureSystolic) / prevBloodPressureSystolic;
							if(prevBloodPressureSystolic == 0)
								bloodPressureSystolicChangePercent =  nextBloodPressureSystolic;
							changeVals.add(bloodPressureSystolicChangePercent);
						}//if(Math.abs)
					}//if
					
				}//if (prevVitalSign)
			
				
				//////////////////Forced Vital Capacity//////////////////////////
				double prevFVC = prevEvent.getFVC_subjectLiters();
				double nextFVC = nextEvent.getFVC_subjectLiters();
				if(prevFVC != -1 && nextFVC != -1){
//					prevFeatures.setFVC_subjectLiters(prevFVC);
//					nextFeatures.setFVC_subjectLiters(nextFVC);
					double fvcChange = nextFVC - prevFVC;
					if(Math.abs(fvcChange) >= FeatureChangeSDU.FVC_subjectLiters){
						features.add(FeatureNumberSDU.FVC_subjectLiters);
						double fvcChangePercent = (nextFVC - prevFVC) / prevFVC;
						if(prevFVC == 0)
							fvcChangePercent =  nextFVC;
						changeVals.add(fvcChangePercent);
					}//if(Math.abs)
				}//if
				
				/*************************************Laboratory data********************************************/
				
				////////////////////////Urine//////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrinePH = prevEvent.getLab().getURINE_urinePH();
					String nextUrinePH = nextEvent.getLab().getURINE_urinePH();
					if(!prevUrinePH.equalsIgnoreCase("") && !nextUrinePH.equalsIgnoreCase("")){
						double prevUrinPHNumber = Double.parseDouble(prevUrinePH);
						double nextUrinPHNumber = Double.parseDouble(nextUrinePH);
						double urinePHChange = nextUrinPHNumber - prevUrinPHNumber;
						if(Math.abs(urinePHChange) >= FeatureChangeSDU.URINE_urinePH){
							features.add(FeatureNumberSDU.URINE_urinePH);
							double urinePhChangePercent = (nextUrinPHNumber - prevUrinPHNumber) / prevUrinPHNumber;
							if(prevUrinPHNumber == 0)
								urinePhChangePercent =  nextUrinPHNumber;
							changeVals.add(urinePhChangePercent);
						}//if(Math.abs)
					}//if(prevUrinePH)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineProtein = prevEvent.getLab().getURINE_urineProtein();
					String nextUrineProtein = nextEvent.getLab().getURINE_urineProtein();
					double prevUrinProteinNumber;
					double nextUrinProteinNumber;
					if(!prevUrineProtein.equalsIgnoreCase("") && !nextUrineProtein.equalsIgnoreCase("")
							&& !prevUrineProtein.equalsIgnoreCase("-") && !nextUrineProtein.equalsIgnoreCase("-")
							&& !prevUrineProtein.equalsIgnoreCase("negative") && !nextUrineProtein.equalsIgnoreCase("negative")){
						
						if(prevUrineProtein.equalsIgnoreCase("trace") && nextUrineProtein.equalsIgnoreCase("trace")){
							prevUrinProteinNumber = 0;
							nextUrinProteinNumber = 0;
						}//if
						else if(prevUrineProtein.equalsIgnoreCase("trace") && !nextUrineProtein.equalsIgnoreCase("trace")){
							prevUrinProteinNumber = 0;
							nextUrinProteinNumber = Double.parseDouble(nextUrineProtein);
						}//else if
						else if(!prevUrineProtein.equalsIgnoreCase("trace") && nextUrineProtein.equalsIgnoreCase("trace")){
							prevUrinProteinNumber = Double.parseDouble(prevUrineProtein);
							nextUrinProteinNumber = 0;
						}//else if
						else{
							prevUrinProteinNumber = Double.parseDouble(prevUrineProtein);
							nextUrinProteinNumber = Double.parseDouble(nextUrineProtein);
						}//else							
						 
						double urineProteinChange = nextUrinProteinNumber - prevUrinProteinNumber;
						if(Math.abs(urineProteinChange) >= FeatureChangeSDU.URINE_urineProtein){
							features.add(FeatureNumberSDU.URINE_urineProtein);
							double urineProteinChangePercent = (nextUrinProteinNumber - prevUrinProteinNumber) / prevUrinProteinNumber;
							if(prevUrinProteinNumber == 0)
								urineProteinChangePercent =  nextUrinProteinNumber;
							changeVals.add(urineProteinChangePercent);
						}//if(Math.abs)
					}//if(prevUrineProtein)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineSpecificGravity = prevEvent.getLab().getURINE_urineSpecificGravity();
					String nextUrineSpecificGravity = nextEvent.getLab().getURINE_urineSpecificGravity();
					double prevUrineSpecificGravityNumber;
					double nextUrineSpecificGravityNumber;
					if(!prevUrineSpecificGravity.equalsIgnoreCase("") && !nextUrineSpecificGravity.equalsIgnoreCase("")){
						if(prevUrineSpecificGravity.equalsIgnoreCase(">1.030") && nextUrineSpecificGravity.equalsIgnoreCase(">1.030")){
							prevUrineSpecificGravityNumber = 1.03;
							nextUrineSpecificGravityNumber = 1.03;
						}//if
						else if(prevUrineSpecificGravity.equalsIgnoreCase(">1.030") && !nextUrineSpecificGravity.equalsIgnoreCase(">1.030")){
							prevUrineSpecificGravityNumber = 1.03;
							nextUrineSpecificGravityNumber = Double.parseDouble(nextUrineSpecificGravity);
						}//else if
						else if(!prevUrineSpecificGravity.equalsIgnoreCase(">1.030") && nextUrineSpecificGravity.equalsIgnoreCase(">1.030")){
							prevUrineSpecificGravityNumber = Double.parseDouble(prevUrineSpecificGravity);
							nextUrineSpecificGravityNumber = 1.03;
						}//else if
						else{
							prevUrineSpecificGravityNumber = Double.parseDouble(prevUrineSpecificGravity);
							nextUrineSpecificGravityNumber = Double.parseDouble(nextUrineSpecificGravity);
						}//else
						
						double urineSpecificGravityChange = nextUrineSpecificGravityNumber - prevUrineSpecificGravityNumber;
						if(Math.abs(urineSpecificGravityChange) >= FeatureChangeSDU.URINE_urineSpecificGravity){
							features.add(FeatureNumberSDU.URINE_urineSpecificGravity);
							double urineSpecificGravityChangePercent = (nextUrineSpecificGravityNumber - prevUrineSpecificGravityNumber) / prevUrineSpecificGravityNumber;
							if(prevUrineSpecificGravityNumber == 0)
								urineSpecificGravityChangePercent =  nextUrineSpecificGravityNumber;
							changeVals.add(urineSpecificGravityChangePercent);
						}//if(Math.abs)
					}//if(prevUrineSpecificGravity)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineGlucose = prevEvent.getLab().getURINE_urineGlucose();
					String nextUrineGlucose = nextEvent.getLab().getURINE_urineGlucose();
					double prevUrineGlucoseNumber;
					double nextUrineGlucoseNumber;
					
					if(!prevUrineGlucose.equalsIgnoreCase("") && !nextUrineGlucose.equalsIgnoreCase("")
							&& !prevUrineGlucose.equalsIgnoreCase("-") && !nextUrineGlucose.equalsIgnoreCase("-")){
						
						if((prevUrineGlucose.equalsIgnoreCase("trace") || prevUrineGlucose.equalsIgnoreCase("normal"))
								&& (nextUrineGlucose.equalsIgnoreCase("trace") || nextUrineGlucose.equalsIgnoreCase("normal"))){
							prevUrineGlucoseNumber = 0;
							nextUrineGlucoseNumber = 0;
						}//if
						else if((!prevUrineGlucose.equalsIgnoreCase("trace") && !prevUrineGlucose.equalsIgnoreCase("normal"))
								&& (nextUrineGlucose.equalsIgnoreCase("trace") || nextUrineGlucose.equalsIgnoreCase("normal"))){
							prevUrineGlucoseNumber = Double.parseDouble(prevUrineGlucose);
							nextUrineGlucoseNumber = 0;
						}//else if
						else if((prevUrineGlucose.equalsIgnoreCase("trace") || prevUrineGlucose.equalsIgnoreCase("normal"))
								&& (!nextUrineGlucose.equalsIgnoreCase("trace") && !nextUrineGlucose.equalsIgnoreCase("normal"))){
							prevUrineGlucoseNumber = 0;
							nextUrineGlucoseNumber = Double.parseDouble(nextUrineGlucose);
						}//else if
						else{
							prevUrineGlucoseNumber = Double.parseDouble(prevUrineGlucose);
							nextUrineGlucoseNumber = Double.parseDouble(nextUrineGlucose);
						}//else	
						
						double urineGlucoseChange = nextUrineGlucoseNumber - prevUrineGlucoseNumber;
						if(Math.abs(urineGlucoseChange) >= FeatureChangeSDU.URINE_urineGlucose){
							features.add(FeatureNumberSDU.URINE_urineGlucose);
							double urineGlucoseChangePercent = (nextUrineGlucoseNumber - prevUrineGlucoseNumber) / prevUrineGlucoseNumber;
							if(prevUrineGlucoseNumber == 0)
								urineGlucoseChangePercent =  nextUrineGlucoseNumber;
							changeVals.add(urineGlucoseChangePercent);
						}//if(Math.abs)
					}//if(prevurineGlucose)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineWBC = prevEvent.getLab().getURINE_urineWBC();
					String nextUrineWBC = nextEvent.getLab().getURINE_urineWBC();
					double prevUrineWBCNumber;
					double nextUrineWBCNumber;
					if(!prevUrineWBC.equalsIgnoreCase("") && !nextUrineWBC.equalsIgnoreCase("")){
						
						if(prevUrineWBC.equalsIgnoreCase("-") && nextUrineWBC.equalsIgnoreCase("-")){
							prevUrineWBCNumber = 0;
							nextUrineWBCNumber = 0;
						}//if
						else if(prevUrineWBC.equalsIgnoreCase("-") && !nextUrineWBC.equalsIgnoreCase("-")){
							prevUrineWBCNumber = 0;
							nextUrineWBCNumber = Double.parseDouble(nextUrineWBC);
						}//else if
						else if(!prevUrineWBC.equalsIgnoreCase("-") && nextUrineWBC.equalsIgnoreCase("-")){
							prevUrineWBCNumber = Double.parseDouble(prevUrineWBC);
							nextUrineWBCNumber = 0;
						}//else if
						else{
							prevUrineWBCNumber = Double.parseDouble(prevUrineWBC);
							nextUrineWBCNumber = Double.parseDouble(nextUrineWBC);
						}//else							
						 
						double urineWBCChange = nextUrineWBCNumber - prevUrineWBCNumber;
						if(Math.abs(urineWBCChange) >= FeatureChangeSDU.URINE_urineWBC){
							features.add(FeatureNumberSDU.URINE_urineWBC);
							double urineWBCChangePercent = (nextUrineWBCNumber - prevUrineWBCNumber) / prevUrineWBCNumber;
							if(prevUrineWBCNumber == 0)
								urineWBCChangePercent =  nextUrineWBCNumber;
							changeVals.add(urineWBCChangePercent);
						}//if(Math.abs)
					}//if(prevUrineWBC)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineLeukesterase = prevEvent.getLab().getURINE_urineLeukesterase();
					String nextUrineLeukesterase = nextEvent.getLab().getURINE_urineLeukesterase();
					double prevUrineLeukesteraseNumber = 0;
					double nextUrineLeukesteraseNumber = 0;
					boolean prevUrineLeukesteraseSelect = false;
					boolean nextUrineLeukesteraseSelect = false;
					
					if(!prevUrineLeukesterase.equalsIgnoreCase("") && !nextUrineLeukesterase.equalsIgnoreCase("")){
						if(prevUrineLeukesterase.equalsIgnoreCase("-")){
							prevUrineLeukesteraseNumber = 0;
							prevUrineLeukesteraseSelect = true;
						}//if(prevUrine)
						if(!prevUrineLeukesteraseSelect && prevUrineLeukesterase.equalsIgnoreCase("trace")){
							prevUrineLeukesteraseNumber = 1;
							prevUrineLeukesteraseSelect = true;
						}//if(prevUrine)
						if(!prevUrineLeukesteraseSelect && prevUrineLeukesterase.equalsIgnoreCase("small")){
							prevUrineLeukesteraseNumber = 2;
							prevUrineLeukesteraseSelect = true;
						}//if(prevUrine)
						if(!prevUrineLeukesteraseSelect && prevUrineLeukesterase.equalsIgnoreCase("moderate")){
							prevUrineLeukesteraseNumber = 5;
							prevUrineLeukesteraseSelect = true;
						}//if(prevUrine)
						if(!prevUrineLeukesteraseSelect && prevUrineLeukesterase.equalsIgnoreCase("large")){
							prevUrineLeukesteraseNumber = 10;
							prevUrineLeukesteraseSelect = true;
						}//if(prevUrine)
						if(nextUrineLeukesterase.equalsIgnoreCase("-")){
							nextUrineLeukesteraseNumber = 0;
							nextUrineLeukesteraseSelect = true;
						}//if(nextUrine)
						if(!nextUrineLeukesteraseSelect && nextUrineLeukesterase.equalsIgnoreCase("trace")){
							nextUrineLeukesteraseNumber = 1;
							nextUrineLeukesteraseSelect = true;
						}//if(nextUrine)
						if(!nextUrineLeukesteraseSelect && nextUrineLeukesterase.equalsIgnoreCase("small")){
							nextUrineLeukesteraseNumber = 2;
							nextUrineLeukesteraseSelect = true;
						}//if(nextUrine)
						if(!nextUrineLeukesteraseSelect && nextUrineLeukesterase.equalsIgnoreCase("moderate")){
							nextUrineLeukesteraseNumber = 5;
							nextUrineLeukesteraseSelect = true;
							
						}//if(nextUrine)
						if(!nextUrineLeukesteraseSelect && nextUrineLeukesterase.equalsIgnoreCase("large")){
							nextUrineLeukesteraseNumber = 10;
							nextUrineLeukesteraseSelect = true;
							
						}//if(nextUrine)
						if(!prevUrineLeukesteraseSelect)
							
							prevUrineLeukesteraseNumber = Double.parseDouble(prevUrineLeukesterase);
						
						if(!nextUrineLeukesteraseSelect)
							
							nextUrineLeukesteraseNumber = Double.parseDouble(nextUrineLeukesterase);
						
						double urineLeukesteraseChange = nextUrineLeukesteraseNumber - prevUrineLeukesteraseNumber;
						if(Math.abs(urineLeukesteraseChange) >= FeatureChangeSDU.URINE_urineLeukesterase){
							features.add(FeatureNumberSDU.URINE_urineLeukesterase);
							double urineLeukesteraseChangePercent = (nextUrineLeukesteraseNumber - prevUrineLeukesteraseNumber) / prevUrineLeukesteraseNumber;
							if(prevUrineLeukesteraseNumber == 0)
								urineLeukesteraseChangePercent =  nextUrineLeukesteraseNumber;
							changeVals.add(urineLeukesteraseChangePercent);
						}//if(Math.abs)
					}//if(!prevUrineLeukesterase)
				}//if(prevEvent)
				
				//////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineBlood = prevEvent.getLab().getURINE_urineBlood();
					String nextUrineBlood = nextEvent.getLab().getURINE_urineBlood();
					double prevUrineBloodNumber = 0;
					double nextUrineBloodNumber = 0;
					boolean prevUrineBloodSelect = false;
					boolean nextUrineBloodSelect = false;
					if(!prevUrineBlood.equalsIgnoreCase("") && !nextUrineBlood.equalsIgnoreCase("")){
						if(prevUrineBlood.equalsIgnoreCase("-") || prevUrineBlood.equalsIgnoreCase("negative")){
							prevUrineBloodNumber = 0;
							prevUrineBloodSelect = true;
						}//if
						if(!prevUrineBloodSelect && (prevUrineBlood.equalsIgnoreCase("trace") || 
								prevUrineBlood.equalsIgnoreCase("hemolyzed trace"))){
							prevUrineBloodNumber = 0.1;
							prevUrineBloodSelect = true;
						}//if
						if(!prevUrineBloodSelect && (prevUrineBlood.equalsIgnoreCase("small") ||
								prevUrineBlood.equalsIgnoreCase("hemolyzed small"))){
							prevUrineBloodNumber = 0.3;
							prevUrineBloodSelect = true;
						}//if
						if(!prevUrineBloodSelect && (prevUrineBlood.equalsIgnoreCase("moderate") ||
								prevUrineBlood.equalsIgnoreCase("hemolyzed moderate"))){
							prevUrineBloodNumber = 0.5;
							prevUrineBloodSelect = true;
						}//if
						if(!prevUrineBloodSelect && prevUrineBlood.equalsIgnoreCase("hemolyzed large")){
							prevUrineBloodNumber = 5;
							prevUrineBloodSelect = true;
						}//if
						
						if(nextUrineBlood.equalsIgnoreCase("-") || nextUrineBlood.equalsIgnoreCase("negative")){
							nextUrineBloodNumber = 0;
							nextUrineBloodSelect = true;
						}//if
						if(!nextUrineBloodSelect && (nextUrineBlood.equalsIgnoreCase("trace") || 
								nextUrineBlood.equalsIgnoreCase("hemolyzed trace"))){
							nextUrineBloodNumber = 0.1;
							nextUrineBloodSelect = true;
						}//if
						if(!nextUrineBloodSelect && (nextUrineBlood.equalsIgnoreCase("small") ||
								nextUrineBlood.equalsIgnoreCase("hemolyzed small"))){
							nextUrineBloodNumber = 0.3;
							nextUrineBloodSelect = true;
						}//if
						if(!nextUrineBloodSelect && (nextUrineBlood.equalsIgnoreCase("moderate") ||
								nextUrineBlood.equalsIgnoreCase("hemolyzed moderate"))){
							nextUrineBloodNumber = 0.5;
							nextUrineBloodSelect = true;
						}//if
						if(!nextUrineBloodSelect && nextUrineBlood.equalsIgnoreCase("hemolyzed large")){
							nextUrineBloodNumber = 5;
							nextUrineBloodSelect = true;
						}//if
						if(!prevUrineBloodSelect)
							
							prevUrineBloodNumber = Double.parseDouble(prevUrineBlood);
						
						if(!nextUrineBloodSelect)
							
							nextUrineBloodNumber = Double.parseDouble(nextUrineBlood);
						
						double urineBloodChange = nextUrineBloodNumber - prevUrineBloodNumber;
						if(Math.abs(urineBloodChange) >= FeatureChangeSDU.URINE_urineBlood){
							features.add(FeatureNumberSDU.URINE_urineBlood);
							double urineBloodChangePercent = (nextUrineBloodNumber - prevUrineBloodNumber) / prevUrineBloodNumber;
							if(prevUrineBloodNumber == 0)
								urineBloodChangePercent =  nextUrineBloodNumber;
							changeVals.add(urineBloodChangePercent);
						}//if(Math.abs)
					}//if(!prevUrineBlood)
				}//if(prevEvent)
				
				//////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineRBCs = prevEvent.getLab().getURINE_urineRBCs();
					String nextUrineRBCs = nextEvent.getLab().getURINE_urineRBCs();
					double prevUrineRBCsNumber = 0;
					double nextUrineRBCsNumber = 0;
					if(!prevUrineRBCs.equalsIgnoreCase("") && !nextUrineRBCs.equalsIgnoreCase("") &&
							!prevUrineRBCs.equalsIgnoreCase("-") && !nextUrineRBCs.equalsIgnoreCase("-")){
						prevUrineRBCsNumber = Double.parseDouble(prevUrineRBCs);
						nextUrineRBCsNumber = Double.parseDouble(nextUrineRBCs);
						double urineRBCsChange = nextUrineRBCsNumber - prevUrineRBCsNumber;
						if(Math.abs(urineRBCsChange) >= FeatureChangeSDU.URINE_urineRBCs){
							features.add(FeatureNumberSDU.URINE_urineRBCs);
							double urineRBCsChangePercent = (nextUrineRBCsNumber - prevUrineRBCsNumber) / prevUrineRBCsNumber;
							if(prevUrineRBCsNumber == 0)
								urineRBCsChangePercent =  nextUrineRBCsNumber;
							changeVals.add(urineRBCsChangePercent);
						}//if(Math.abs)
					}//if(!prevUrineRBCs)
				}//if(prevEvent)
				
				/////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineCasts = prevEvent.getLab().getURINE_urineCasts();
					String nextUrineCasts = nextEvent.getLab().getURINE_urineCasts();
					double prevUrineCastsNumber = 0;
					double nextUrineCastsNumber = 0;
					boolean prevUrineCastsSelect = false;
					boolean nextUrineCastsSelect = false;
					if(!prevUrineCasts.equalsIgnoreCase("") && !nextUrineCasts.equalsIgnoreCase("")){
						if(prevUrineCasts.equalsIgnoreCase("-")){
							prevUrineCastsNumber = 0;
							prevUrineCastsSelect = true;
						}//if
						if(nextUrineCasts.equalsIgnoreCase("-")){
							nextUrineCastsNumber = 0;
							nextUrineCastsSelect = true;
						}//if
						if(!prevUrineCastsSelect)
							prevUrineCastsNumber = Double.parseDouble(prevUrineCasts);
						if(!nextUrineCastsSelect)
							nextUrineCastsNumber = Double.parseDouble(nextUrineCasts);
						double urineCastsChange = nextUrineCastsNumber - prevUrineCastsNumber;
						if(Math.abs(urineCastsChange) >= FeatureChangeSDU.URINE_urineCasts){
							features.add(FeatureNumberSDU.URINE_urineCasts);
							double urineCastsChangePercent = (nextUrineCastsNumber - prevUrineCastsNumber) / prevUrineCastsNumber;
							if(prevUrineCastsNumber == 0)
								urineCastsChangePercent =  nextUrineCastsNumber;
							changeVals.add(urineCastsChangePercent);
						}//if(Math.abs)
					}//if(!prevUrineCasts)
				}//if(prevEvent)
				
				/////////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineKetones = prevEvent.getLab().getURINE_urineKetones();
					String nextUrineKetones = nextEvent.getLab().getURINE_urineKetones();
					double prevUrineKetonesNumber = 0;
					double nextUrineKetonesNumber = 0;
					boolean prevUrineKetonesSelect = false;
					boolean nextUrineKetonesSelect = false;
					if(!prevUrineKetones.equalsIgnoreCase("") && !nextUrineKetones.equalsIgnoreCase("")){
						if(prevUrineKetones.equalsIgnoreCase("-") || prevUrineKetones.equalsIgnoreCase("negative")){
							prevUrineKetonesNumber = 0;
							prevUrineKetonesSelect = true;
						}//if
						if(nextUrineKetones.equalsIgnoreCase("-") || nextUrineKetones.equalsIgnoreCase("negative")){
							nextUrineKetonesNumber = 0;
							nextUrineKetonesSelect = true;
						}//if
						if(prevUrineKetones.equalsIgnoreCase("trace")){
							prevUrineKetonesNumber = 0.2;
							prevUrineKetonesSelect = true;
						}//if
						if(nextUrineKetones.equalsIgnoreCase("trace")){
							nextUrineKetonesNumber = 0.2;
							nextUrineKetonesSelect = true;
						}//if
						if(!prevUrineKetonesSelect)
							prevUrineKetonesNumber = Double.parseDouble(prevUrineKetones);
						if(!nextUrineKetonesSelect)
							nextUrineKetonesNumber = Double.parseDouble(nextUrineKetones);
						double urineKetonesChange = nextUrineKetonesNumber - prevUrineKetonesNumber;
						if(Math.abs(urineKetonesChange) >= FeatureChangeSDU.URINE_urineKetones){
							features.add(FeatureNumberSDU.URINE_urineKetones);
							double urineKetonesChangePercent = (nextUrineKetonesNumber - prevUrineKetonesNumber) / prevUrineKetonesNumber;
							if(prevUrineKetonesNumber == 0)
								urineKetonesChangePercent =  nextUrineKetonesNumber;
							changeVals.add(urineKetonesChangePercent);
						}//if(Math.abs)
					}//if(!prevUrineKetones)
				}//if(prevEvent)
				
				////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineAppearance = prevEvent.getLab().getURINE_urineAppearance();
					String nextUrineAppearance = nextEvent.getLab().getURINE_urineAppearance();
					double prevUrineAppearanceNumber = 0;
					double nextUrineAppearanceNumber = 0;
					boolean prevUrineAppearanceSelect = false;
					boolean nextUrineAppearanceSelect = false;
					if(!prevUrineAppearance.equalsIgnoreCase("") && !nextUrineAppearance.equalsIgnoreCase("")){
						if(prevUrineAppearance.equalsIgnoreCase("clear")){
							prevUrineAppearanceNumber = 1;
							prevUrineAppearanceSelect = true;
						}//if
						if(nextUrineAppearance.equalsIgnoreCase("clear")){
							nextUrineAppearanceNumber = 1;
							nextUrineAppearanceSelect = true;
						}//if
						if(prevUrineAppearance.equalsIgnoreCase("normal")){
							prevUrineAppearanceNumber = 2;
							prevUrineAppearanceSelect = true;
						}//if
						if(nextUrineAppearance.equalsIgnoreCase("normal")){
							nextUrineAppearanceNumber = 2;
							nextUrineAppearanceSelect = true;
						}//if
						if(prevUrineAppearance.equalsIgnoreCase("cloudy")){
							prevUrineAppearanceNumber = 3;
							prevUrineAppearanceSelect = true;
						}//if
						if(nextUrineAppearance.equalsIgnoreCase("cloudy")){
							nextUrineAppearanceNumber = 3;
							nextUrineAppearanceSelect = true;
						}//if
						if(prevUrineAppearance.equalsIgnoreCase("hazy")){
							prevUrineAppearanceNumber = 4;
							prevUrineAppearanceSelect = true;
						}//if
						if(nextUrineAppearance.equalsIgnoreCase("hazy")){
							nextUrineAppearanceNumber = 4;
							nextUrineAppearanceSelect = true;
						}//if
						if(prevUrineAppearance.equalsIgnoreCase("turbid")){
							prevUrineAppearanceNumber = 5;
							prevUrineAppearanceSelect = true;
						}//if
						if(nextUrineAppearance.equalsIgnoreCase("turbid")){
							nextUrineAppearanceNumber = 5;
							nextUrineAppearanceSelect = true;
						}//if
						if(!prevUrineAppearanceSelect)
							prevUrineAppearanceNumber = Double.parseDouble(prevUrineAppearance);
						if(!nextUrineAppearanceSelect)
							nextUrineAppearanceNumber = Double.parseDouble(nextUrineAppearance);
						double urineAppearanceChange = nextUrineAppearanceNumber - prevUrineAppearanceNumber;
						if(Math.abs(urineAppearanceChange) >= FeatureChangeSDU.URINE_urineAppearance){
							features.add(FeatureNumberSDU.URINE_urineAppearance);
							double urineAppearanceChangePercent = (nextUrineAppearanceNumber - prevUrineAppearanceNumber) / prevUrineAppearanceNumber;
							if(prevUrineAppearanceNumber == 0)
								urineAppearanceChangePercent =  nextUrineAppearanceNumber;
							changeVals.add(urineAppearanceChangePercent);
						}//if(Math.abs)
					}//if(!prevUrineAppearance)
				}//if(prevEvent)
				
				/////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineColor = prevEvent.getLab().getURINE_urineColor();
					String nextUrineColor = nextEvent.getLab().getURINE_urineColor();
					double prevUrineColorNumber = 0;
					double nextUrineColorNumber = 0;
					boolean prevUrineColorSelect = false;
					boolean nextUrineColorSelect = false;
					if(!prevUrineColor.equalsIgnoreCase("") && !nextUrineColor.equalsIgnoreCase("")){
						if(prevUrineColor.equalsIgnoreCase("yellow")){
							prevUrineColorNumber = 3;
							prevUrineColorSelect = true;
						}//if
						if(nextUrineColor.equalsIgnoreCase("yellow")){
							nextUrineColorNumber = 3;
							nextUrineColorSelect = true;
						}//if
						if(prevUrineColor.equalsIgnoreCase("straw")){
							prevUrineColorNumber = 2;
							prevUrineColorSelect = true;
						}//if
						if(nextUrineColor.equalsIgnoreCase("straw")){
							nextUrineColorNumber = 2;
							nextUrineColorSelect = true;
						}//if
						if(prevUrineColor.equalsIgnoreCase("red")){
							prevUrineColorNumber = 6;
							prevUrineColorSelect = true;
						}//if
						if(nextUrineColor.equalsIgnoreCase("red")){
							nextUrineColorNumber = 6;
							nextUrineColorSelect = true;
						}//if
						if(prevUrineColor.equalsIgnoreCase("amber")){
							prevUrineColorNumber = 5;
							prevUrineColorSelect = true;
						}//if
						if(nextUrineColor.equalsIgnoreCase("amber")){
							nextUrineColorNumber = 5;
							nextUrineColorSelect = true;
						}//if
						if(prevUrineColor.equalsIgnoreCase("colorless")){
							prevUrineColorNumber = 1;
							prevUrineColorSelect = true;
						}//if
						if(nextUrineColor.equalsIgnoreCase("colorless")){
							nextUrineColorNumber = 1;
							nextUrineColorSelect = true;
						}//if
						if(prevUrineColor.equalsIgnoreCase("orange")){
							prevUrineColorNumber = 4;
							prevUrineColorSelect = true;
						}//if
						if(nextUrineColor.equalsIgnoreCase("orange")){
							nextUrineColorNumber = 4;
							nextUrineColorSelect = true;
						}//if
						if(prevUrineColor.equalsIgnoreCase("brown")){
							prevUrineColorNumber = 7;
							prevUrineColorSelect = true;
						}//if
						if(nextUrineColor.equalsIgnoreCase("brown")){
							nextUrineColorNumber = 7;
							nextUrineColorSelect = true;
						}//if
						if(prevUrineColor.equalsIgnoreCase("green")){
							prevUrineColorNumber = 8;
							prevUrineColorSelect = true;
						}//if
						if(nextUrineColor.equalsIgnoreCase("green")){
							nextUrineColorNumber = 8;
							nextUrineColorSelect = true;
						}//if
						if(!prevUrineColorSelect)
							prevUrineColorNumber = Double.parseDouble(prevUrineColor);
						if(!nextUrineColorSelect)
							nextUrineColorNumber = Double.parseDouble(nextUrineColor);
						double urineColorChange = nextUrineColorNumber - prevUrineColorNumber;
						if(Math.abs(urineColorChange) >= FeatureChangeSDU.URINE_urineColor){
							features.add(FeatureNumberSDU.URINE_urineColor);
							double urineColorChangePercent = (nextUrineColorNumber - prevUrineColorNumber) / prevUrineColorNumber;
							if(prevUrineColorNumber == 0)
								urineColorChangePercent =  nextUrineColorNumber;
							changeVals.add(urineColorChangePercent);
						}//if(Math.abs)
					}//if(!prevUrineColor)
				}//if(prevEvent)
				
				////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineBacteria = prevEvent.getLab().getURINE_urineBacteria();
					String nextUrineBacteria = nextEvent.getLab().getURINE_urineBacteria();
					double prevUrineBacteriaNumber = 0;
					double nextUrineBacteriaNumber = 0;
					boolean prevUrineBacteriaSelect = false;
					boolean nextUrineBacteriaSelect = false;
					if(!prevUrineBacteria.equalsIgnoreCase("-") && !nextUrineBacteria.equalsIgnoreCase("-")
							&& !prevUrineBacteria.equalsIgnoreCase("") && !nextUrineBacteria.equalsIgnoreCase("")){
						if(prevUrineBacteria.equalsIgnoreCase("trace") || prevUrineBacteria.equalsIgnoreCase("1+")){
							prevUrineBacteriaNumber = 1;
							prevUrineBacteriaSelect = true;
						}//if
						if(nextUrineBacteria.equalsIgnoreCase("trace") || nextUrineBacteria.equalsIgnoreCase("1+")){
							nextUrineBacteriaNumber = 1;
							nextUrineBacteriaSelect = true;
						}//if
						
						if(prevUrineBacteria.equalsIgnoreCase("small") || prevUrineBacteria.equalsIgnoreCase("2+")){
							prevUrineBacteriaNumber = 2;
							prevUrineBacteriaSelect = true;
						}//if
						if(nextUrineBacteria.equalsIgnoreCase("small") || nextUrineBacteria.equalsIgnoreCase("2+")){
							nextUrineBacteriaNumber = 2;
							nextUrineBacteriaSelect = true;
						}//if
						
						if(prevUrineBacteria.equalsIgnoreCase("moderate") || prevUrineBacteria.equalsIgnoreCase("3+")
								|| prevUrineBacteria.equalsIgnoreCase("present") || prevUrineBacteria.equalsIgnoreCase("+")){
							prevUrineBacteriaNumber = 3;
							prevUrineBacteriaSelect = true;
						}//if
						if(nextUrineBacteria.equalsIgnoreCase("moderate") || nextUrineBacteria.equalsIgnoreCase("3+")
								|| nextUrineBacteria.equalsIgnoreCase("present") || nextUrineBacteria.equalsIgnoreCase("+")){
							nextUrineBacteriaNumber = 3;
							nextUrineBacteriaSelect = true;
						}//if
						
						if(prevUrineBacteria.equalsIgnoreCase("large") || prevUrineBacteria.equalsIgnoreCase("4+")){
							prevUrineBacteriaNumber = 4;
							prevUrineBacteriaSelect = true;
						}//if
						if(nextUrineBacteria.equalsIgnoreCase("large") || nextUrineBacteria.equalsIgnoreCase("4+")){
							nextUrineBacteriaNumber = 4;
							nextUrineBacteriaSelect = true;
						}//if
						
						
						if(!prevUrineBacteriaSelect)
							prevUrineBacteriaNumber = Double.parseDouble(prevUrineBacteria);
						if(!nextUrineBacteriaSelect)
							nextUrineBacteriaNumber = Double.parseDouble(nextUrineBacteria);
						double urineBacteriaChange = nextUrineBacteriaNumber - prevUrineBacteriaNumber;
						if(Math.abs(urineBacteriaChange) >= FeatureChangeSDU.URINE_urineBacteria){
							features.add(FeatureNumberSDU.URINE_urineBacteria);
							double urineBacteriaChangePercent = (nextUrineBacteriaNumber - prevUrineBacteriaNumber) / prevUrineBacteriaNumber;
							if(prevUrineBacteriaNumber == 0)
								urineBacteriaChangePercent =  nextUrineBacteriaNumber;
							changeVals.add(urineBacteriaChangePercent);
						}//if(Math.abs)
					}//if(!prevUrineBacteria)
				}//if(prevEvent)
				
				////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineMucus = prevEvent.getLab().getURINE_urineMucus();
					String nextUrineMucus = nextEvent.getLab().getURINE_urineMucus();
					double prevUrineMucusNumber = 0;
					double nextUrineMucusNumber = 0;
					boolean prevUrineMucusSelect = false;
					boolean nextUrineMucusSelect = false;
					if(!prevUrineMucus.equalsIgnoreCase("-") && !nextUrineMucus.equalsIgnoreCase("-")
							&& !prevUrineMucus.equalsIgnoreCase("") && !nextUrineMucus.equalsIgnoreCase("")){
						if(prevUrineMucus.equalsIgnoreCase("trace") || prevUrineMucus.equalsIgnoreCase("1+")){
							prevUrineMucusNumber = 1;
							prevUrineMucusSelect = true;
						}//if
						if(nextUrineMucus.equalsIgnoreCase("trace") || nextUrineMucus.equalsIgnoreCase("1+")){
							nextUrineMucusNumber = 1;
							nextUrineMucusSelect = true;
						}//if
						
						if(prevUrineMucus.equalsIgnoreCase("small") || prevUrineMucus.equalsIgnoreCase("2+")){
							prevUrineMucusNumber = 2;
							prevUrineMucusSelect = true;
						}//if
						if(nextUrineMucus.equalsIgnoreCase("small") || nextUrineMucus.equalsIgnoreCase("2+")){
							nextUrineMucusNumber = 2;
							nextUrineMucusSelect = true;
						}//if
						
						if(prevUrineMucus.equalsIgnoreCase("moderate") || prevUrineMucus.equalsIgnoreCase("3+")
								|| prevUrineMucus.equalsIgnoreCase("present") || prevUrineMucus.equalsIgnoreCase("+")){
							prevUrineMucusNumber = 3;
							prevUrineMucusSelect = true;
						}//if
						if(nextUrineMucus.equalsIgnoreCase("moderate") || nextUrineMucus.equalsIgnoreCase("3+")
								|| nextUrineMucus.equalsIgnoreCase("present") || nextUrineMucus.equalsIgnoreCase("+")){
							nextUrineMucusNumber = 3;
							nextUrineMucusSelect = true;
						}//if
						
						if(prevUrineMucus.equalsIgnoreCase("large") || prevUrineMucus.equalsIgnoreCase("4+")){
							prevUrineMucusNumber = 4;
							prevUrineMucusSelect = true;
						}//if
						if(nextUrineMucus.equalsIgnoreCase("large") || nextUrineMucus.equalsIgnoreCase("4+")){
							nextUrineMucusNumber = 4;
							nextUrineMucusSelect = true;
						}//if
						
						
						if(!prevUrineMucusSelect)
							prevUrineMucusNumber = Double.parseDouble(prevUrineMucus);
						if(!nextUrineMucusSelect)
							nextUrineMucusNumber = Double.parseDouble(nextUrineMucus);
						double urineMucusChange = nextUrineMucusNumber - prevUrineMucusNumber;
						if(Math.abs(urineMucusChange) >= FeatureChangeSDU.URINE_urineMucus){
							features.add(FeatureNumberSDU.URINE_urineMucus);
							double urineMucusChangePercent = (nextUrineMucusNumber - prevUrineMucusNumber) / prevUrineMucusNumber;
							if(prevUrineMucusNumber == 0)
								urineMucusChangePercent =  nextUrineMucusNumber;
							changeVals.add(urineMucusChangePercent);
						}//if(Math.abs)
					}//if(!prevUrineMucus)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineAlbumin = prevEvent.getLab().getURINE_urineAlbumin();
					String nextUrineAlbumin = nextEvent.getLab().getURINE_urineAlbumin();
					double prevUrineAlbuminNumber = 0;
					double nextUrineAlbuminNumber = 0;
					boolean prevUrineAlbuminSelect = false;
					boolean nextUrineAlbuminSelect = false;
					if(!prevUrineAlbumin.equalsIgnoreCase("-") && !nextUrineAlbumin.equalsIgnoreCase("-")
							&& !prevUrineAlbumin.equalsIgnoreCase("") && !nextUrineAlbumin.equalsIgnoreCase("")){
						if(prevUrineAlbumin.equalsIgnoreCase("trace")){
							prevUrineAlbuminNumber = 1;
							prevUrineAlbuminSelect = true;
						}//if
						
						if(nextUrineAlbumin.equalsIgnoreCase("trace")){
							nextUrineAlbuminNumber = 1;
							nextUrineAlbuminSelect = true;
						}//if
						
						if(prevUrineAlbumin.equalsIgnoreCase("<0.0003")){
							prevUrineAlbuminNumber = 0.0003;
							prevUrineAlbuminSelect = true;
						}//if
						
						if(nextUrineAlbumin.equalsIgnoreCase("<0.0003")){
							nextUrineAlbuminNumber = 0.0003;
							nextUrineAlbuminSelect = true;
						}//if
						
						if(prevUrineAlbumin.equalsIgnoreCase("<3.0")){
							prevUrineAlbuminNumber = 3;
							prevUrineAlbuminSelect = true;
						}//if
						
						if(nextUrineAlbumin.equalsIgnoreCase("<3.0")){
							nextUrineAlbuminNumber = 3;
							nextUrineAlbuminSelect = true;
						}//if
												
						if(!prevUrineAlbuminSelect)
							prevUrineAlbuminNumber = Double.parseDouble(prevUrineAlbumin);
						if(!nextUrineAlbuminSelect)
							nextUrineAlbuminNumber = Double.parseDouble(nextUrineAlbumin);
						double urineAlbuminChange = nextUrineAlbuminNumber - prevUrineAlbuminNumber;
						if(Math.abs(urineAlbuminChange) >= FeatureChangeSDU.URINE_urineAlbumin){
							features.add(FeatureNumberSDU.URINE_urineAlbumin);
							double urineAlbuminChangePercent = (nextUrineAlbuminNumber - prevUrineAlbuminNumber) / prevUrineAlbuminNumber;
							if(prevUrineAlbuminNumber == 0)
								urineAlbuminChangePercent =  nextUrineAlbuminNumber;
							changeVals.add(urineAlbuminChangePercent);
						}//if(Math.abs)
					}//if(!prevUrineAlbumin)
				}//if(prevEvent)
				
				//////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineUricAcidCrystals = prevEvent.getLab().getURINE_urineUricAcidCrystals();
					String nextUrineUricAcidCrystals = nextEvent.getLab().getURINE_urineUricAcidCrystals();
					double prevUrineUricAcidCrystalsNumber = 0;
					double nextUrineUricAcidCrystalsNumber = 0;
					boolean prevUrineUricAcidCrystalsSelect = false;
					boolean nextUrineUricAcidCrystalsSelect = false;
					if(!prevUrineUricAcidCrystals.equalsIgnoreCase("-") && !nextUrineUricAcidCrystals.equalsIgnoreCase("-")
							&& !prevUrineUricAcidCrystals.equalsIgnoreCase("") && !nextUrineUricAcidCrystals.equalsIgnoreCase("")){
						if(prevUrineUricAcidCrystals.equalsIgnoreCase("trace")){
							prevUrineUricAcidCrystalsNumber = 1;
							prevUrineUricAcidCrystalsSelect = true;
						}//if
						if(nextUrineUricAcidCrystals.equalsIgnoreCase("trace")){
							nextUrineUricAcidCrystalsNumber = 1;
							nextUrineUricAcidCrystalsSelect = true;
						}//if
						
						if(prevUrineUricAcidCrystals.equalsIgnoreCase("small") || prevUrineUricAcidCrystals.equalsIgnoreCase("few")){
							prevUrineUricAcidCrystalsNumber = 2;
							prevUrineUricAcidCrystalsSelect = true;
						}//if
						if(nextUrineUricAcidCrystals.equalsIgnoreCase("small") || nextUrineUricAcidCrystals.equalsIgnoreCase("few")){
							nextUrineUricAcidCrystalsNumber = 2;
							nextUrineUricAcidCrystalsSelect = true;
						}//if
						
						if(prevUrineUricAcidCrystals.equalsIgnoreCase("moderate") || prevUrineUricAcidCrystals.equalsIgnoreCase("mod")
								|| prevUrineUricAcidCrystals.equalsIgnoreCase("present") || prevUrineUricAcidCrystals.equalsIgnoreCase("occ")){
							prevUrineUricAcidCrystalsNumber = 3;
							prevUrineUricAcidCrystalsSelect = true;
						}//if
						
						if(nextUrineUricAcidCrystals.equalsIgnoreCase("moderate") || nextUrineUricAcidCrystals.equalsIgnoreCase("mod")
								|| nextUrineUricAcidCrystals.equalsIgnoreCase("present") || nextUrineUricAcidCrystals.equalsIgnoreCase("occ")){
							nextUrineUricAcidCrystalsNumber = 3;
							nextUrineUricAcidCrystalsSelect = true;
						}//if
						
						
						if(prevUrineUricAcidCrystals.equalsIgnoreCase("large") || prevUrineUricAcidCrystals.equalsIgnoreCase("many")){
							prevUrineUricAcidCrystalsNumber = 4;
							prevUrineUricAcidCrystalsSelect = true;
						}//if
						if(nextUrineUricAcidCrystals.equalsIgnoreCase("large") || nextUrineUricAcidCrystals.equalsIgnoreCase("many")){
							nextUrineUricAcidCrystalsNumber = 4;
							nextUrineUricAcidCrystalsSelect = true;
						}//if
						
						
						if(!prevUrineUricAcidCrystalsSelect)
							prevUrineUricAcidCrystalsNumber = Double.parseDouble(prevUrineUricAcidCrystals);
						if(!nextUrineUricAcidCrystalsSelect)
							nextUrineUricAcidCrystalsNumber = Double.parseDouble(nextUrineUricAcidCrystals);
						double urineUricAcidCrystalsChange = nextUrineUricAcidCrystalsNumber - prevUrineUricAcidCrystalsNumber;
						if(Math.abs(urineUricAcidCrystalsChange) >= FeatureChangeSDU.URINE_urineUricAcidCrystals){
							features.add(FeatureNumberSDU.URINE_urineUricAcidCrystals);
							double urineUricAcidCrystalsChangePercent = (nextUrineUricAcidCrystalsNumber - prevUrineUricAcidCrystalsNumber) / prevUrineUricAcidCrystalsNumber;
							if(prevUrineUricAcidCrystalsNumber == 0)
								urineUricAcidCrystalsChangePercent =  nextUrineUricAcidCrystalsNumber;
							changeVals.add(urineUricAcidCrystalsChangePercent);
						}//if(Math.abs)
					}//if(!prevUrineUricAcidCrystals)
				}//if(prevEvent)
				
				//////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevUrineCalciumOxalateCrystals = prevEvent.getLab().getURINE_urineCalciumOxalateCrystals();
					String nextUrineCalciumOxalateCrystals = nextEvent.getLab().getURINE_urineCalciumOxalateCrystals();
					double prevUrineCalciumOxalateCrystalsNumber = 0;
					double nextUrineCalciumOxalateCrystalsNumber = 0;
					boolean prevUrineCalciumOxalateCrystalsSelect = false;
					boolean nextUrineCalciumOxalateCrystalsSelect = false;
					if(!prevUrineCalciumOxalateCrystals.equalsIgnoreCase("-") && !nextUrineCalciumOxalateCrystals.equalsIgnoreCase("-")
							&& !prevUrineCalciumOxalateCrystals.equalsIgnoreCase("") && !nextUrineCalciumOxalateCrystals.equalsIgnoreCase("")){
						if(prevUrineCalciumOxalateCrystals.equalsIgnoreCase("trace")){
							prevUrineCalciumOxalateCrystalsNumber = 1;
							prevUrineCalciumOxalateCrystalsSelect = true;
						}//if
						if(nextUrineCalciumOxalateCrystals.equalsIgnoreCase("trace")){
							nextUrineCalciumOxalateCrystalsNumber = 1;
							nextUrineCalciumOxalateCrystalsSelect = true;
						}//if
						
						if(prevUrineCalciumOxalateCrystals.equalsIgnoreCase("small") || prevUrineCalciumOxalateCrystals.equalsIgnoreCase("few")){
							prevUrineCalciumOxalateCrystalsNumber = 2;
							prevUrineCalciumOxalateCrystalsSelect = true;
						}//if
						if(nextUrineCalciumOxalateCrystals.equalsIgnoreCase("small") || nextUrineCalciumOxalateCrystals.equalsIgnoreCase("few")){
							nextUrineCalciumOxalateCrystalsNumber = 2;
							nextUrineCalciumOxalateCrystalsSelect = true;
						}//if
						
						if(prevUrineCalciumOxalateCrystals.equalsIgnoreCase("moderate") || prevUrineCalciumOxalateCrystals.equalsIgnoreCase("mod")
								|| prevUrineCalciumOxalateCrystals.equalsIgnoreCase("present") || prevUrineCalciumOxalateCrystals.equalsIgnoreCase("occ")){
							prevUrineCalciumOxalateCrystalsNumber = 3;
							prevUrineCalciumOxalateCrystalsSelect = true;
						}//if
						
						if(nextUrineCalciumOxalateCrystals.equalsIgnoreCase("moderate") || nextUrineCalciumOxalateCrystals.equalsIgnoreCase("mod")
								|| nextUrineCalciumOxalateCrystals.equalsIgnoreCase("present") || nextUrineCalciumOxalateCrystals.equalsIgnoreCase("occ")){
							nextUrineCalciumOxalateCrystalsNumber = 3;
							nextUrineCalciumOxalateCrystalsSelect = true;
						}//if
						
						
						if(prevUrineCalciumOxalateCrystals.equalsIgnoreCase("large") || prevUrineCalciumOxalateCrystals.equalsIgnoreCase("many")){
							prevUrineCalciumOxalateCrystalsNumber = 4;
							prevUrineCalciumOxalateCrystalsSelect = true;
						}//if
						if(nextUrineCalciumOxalateCrystals.equalsIgnoreCase("large") || nextUrineCalciumOxalateCrystals.equalsIgnoreCase("many")){
							nextUrineCalciumOxalateCrystalsNumber = 4;
							nextUrineCalciumOxalateCrystalsSelect = true;
						}//if
						
						
						if(!prevUrineCalciumOxalateCrystalsSelect)
							prevUrineCalciumOxalateCrystalsNumber = Double.parseDouble(prevUrineCalciumOxalateCrystals);
						if(!nextUrineCalciumOxalateCrystalsSelect)
							nextUrineCalciumOxalateCrystalsNumber = Double.parseDouble(nextUrineCalciumOxalateCrystals);
						double urineCalciumOxalateCrystalsChange = nextUrineCalciumOxalateCrystalsNumber - prevUrineCalciumOxalateCrystalsNumber;
						if(Math.abs(urineCalciumOxalateCrystalsChange) >= FeatureChangeSDU.URINE_urineCalciumOxalateCrystals){
							features.add(FeatureNumberSDU.URINE_urineCalciumOxalateCrystals);
							double urineCalciumOxalateCrystalsChangePercent = (nextUrineCalciumOxalateCrystalsNumber - prevUrineCalciumOxalateCrystalsNumber) / prevUrineCalciumOxalateCrystalsNumber;
							if(prevUrineCalciumOxalateCrystalsNumber == 0)
								urineCalciumOxalateCrystalsChangePercent =  nextUrineCalciumOxalateCrystalsNumber;
							changeVals.add(urineCalciumOxalateCrystalsChangePercent);
						}//if(Math.abs)
					}//if(!prevUrineCalciumOxalateCrystals)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevBLOOD_PROTEINS_albumin = prevEvent.getLab().getBLOOD_PROTEINS_albumin();
					String nextBLOOD_PROTEINS_albumin = nextEvent.getLab().getBLOOD_PROTEINS_albumin();
					double prevBLOOD_PROTEINS_albuminNumber = 0;
					double nextBLOOD_PROTEINS_albuminNumber = 0;
					boolean prevBLOOD_PROTEINS_albuminSelect = false;
					boolean nextBLOOD_PROTEINS_albuminSelect = false;
					if(!prevBLOOD_PROTEINS_albumin.equalsIgnoreCase("-") && !nextBLOOD_PROTEINS_albumin.equalsIgnoreCase("-")
							&& !prevBLOOD_PROTEINS_albumin.equalsIgnoreCase("") && !nextBLOOD_PROTEINS_albumin.equalsIgnoreCase("")){
						if(prevBLOOD_PROTEINS_albumin.equalsIgnoreCase("trace")){
							prevBLOOD_PROTEINS_albuminNumber = 1;
							prevBLOOD_PROTEINS_albuminSelect = true;
						}//if
						if(nextBLOOD_PROTEINS_albumin.equalsIgnoreCase("trace")){
							nextBLOOD_PROTEINS_albuminNumber = 1;
							nextBLOOD_PROTEINS_albuminSelect = true;
						}//if
						
						if(!prevBLOOD_PROTEINS_albuminSelect)
							prevBLOOD_PROTEINS_albuminNumber = Double.parseDouble(prevBLOOD_PROTEINS_albumin);
						if(!nextBLOOD_PROTEINS_albuminSelect)
							nextBLOOD_PROTEINS_albuminNumber = Double.parseDouble(nextBLOOD_PROTEINS_albumin);
						double BLOOD_PROTEINS_albuminChange = nextBLOOD_PROTEINS_albuminNumber - prevBLOOD_PROTEINS_albuminNumber;
						if(Math.abs(BLOOD_PROTEINS_albuminChange) >= FeatureChangeSDU.BLOOD_PROTEINS_albumin){
							features.add(FeatureNumberSDU.BLOOD_PROTEINS_albumin);
							double BLOOD_PROTEINS_albuminChangePercent = (nextBLOOD_PROTEINS_albuminNumber - prevBLOOD_PROTEINS_albuminNumber) / prevBLOOD_PROTEINS_albuminNumber;
							if(prevBLOOD_PROTEINS_albuminNumber == 0)
								BLOOD_PROTEINS_albuminChangePercent =  nextBLOOD_PROTEINS_albuminNumber;
							changeVals.add(BLOOD_PROTEINS_albuminChangePercent);
						}//if(Math.abs)
					}//if(!prevBLOOD_PROTEINS_albumin)
				}//if(prevEvent)
				
				//////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevBLOOD_PROTEINS_protein = prevEvent.getLab().getBLOOD_PROTEINS_protein();
					String nextBLOOD_PROTEINS_protein = nextEvent.getLab().getBLOOD_PROTEINS_protein();
					double prevBLOOD_PROTEINS_proteinNumber = 0;
					double nextBLOOD_PROTEINS_proteinNumber = 0;
					boolean prevBLOOD_PROTEINS_proteinSelect = false;
					boolean nextBLOOD_PROTEINS_proteinSelect = false;
					if(!prevBLOOD_PROTEINS_protein.equalsIgnoreCase("-") && !nextBLOOD_PROTEINS_protein.equalsIgnoreCase("-")
							&& !prevBLOOD_PROTEINS_protein.equalsIgnoreCase("") && !nextBLOOD_PROTEINS_protein.equalsIgnoreCase("")){
						if(prevBLOOD_PROTEINS_protein.equalsIgnoreCase("trace") || prevBLOOD_PROTEINS_protein.equalsIgnoreCase("1+")){
							prevBLOOD_PROTEINS_proteinNumber = 1;
							prevBLOOD_PROTEINS_proteinSelect = true;
						}//if
						if(nextBLOOD_PROTEINS_protein.equalsIgnoreCase("trace") || nextBLOOD_PROTEINS_protein.equalsIgnoreCase("1+")){
							nextBLOOD_PROTEINS_proteinNumber = 1;
							nextBLOOD_PROTEINS_proteinSelect = true;
						}//if
						
						if(prevBLOOD_PROTEINS_protein.equalsIgnoreCase("2+")){
							prevBLOOD_PROTEINS_proteinNumber = 2;
							prevBLOOD_PROTEINS_proteinSelect = true;
						}//if
						if(nextBLOOD_PROTEINS_protein.equalsIgnoreCase("2+")){
							nextBLOOD_PROTEINS_proteinNumber = 2;
							nextBLOOD_PROTEINS_proteinSelect = true;
						}//if
						if(prevBLOOD_PROTEINS_protein.equalsIgnoreCase("4+")){
							prevBLOOD_PROTEINS_proteinNumber = 4;
							prevBLOOD_PROTEINS_proteinSelect = true;
						}//if
						if(nextBLOOD_PROTEINS_protein.equalsIgnoreCase("4+")){
							nextBLOOD_PROTEINS_proteinNumber = 4;
							nextBLOOD_PROTEINS_proteinSelect = true;
						}//if
						
						if(!prevBLOOD_PROTEINS_proteinSelect)
							prevBLOOD_PROTEINS_proteinNumber = Double.parseDouble(prevBLOOD_PROTEINS_protein);
						if(!nextBLOOD_PROTEINS_proteinSelect)
							nextBLOOD_PROTEINS_proteinNumber = Double.parseDouble(nextBLOOD_PROTEINS_protein);
						double BLOOD_PROTEINS_proteinChange = nextBLOOD_PROTEINS_proteinNumber - prevBLOOD_PROTEINS_proteinNumber;
						if(Math.abs(BLOOD_PROTEINS_proteinChange) >= FeatureChangeSDU.BLOOD_PROTEINS_protein){
							features.add(FeatureNumberSDU.BLOOD_PROTEINS_protein);
							double BLOOD_PROTEINS_proteinChangePercent = (nextBLOOD_PROTEINS_proteinNumber - prevBLOOD_PROTEINS_proteinNumber) / prevBLOOD_PROTEINS_proteinNumber;
							if(prevBLOOD_PROTEINS_proteinNumber == 0)
								BLOOD_PROTEINS_proteinChangePercent =  nextBLOOD_PROTEINS_proteinNumber;
							changeVals.add(BLOOD_PROTEINS_proteinChangePercent);
						}//if(Math.abs)
					}//if(!prevBLOOD_PROTEINS_protein)
				}//if(prevEvent)
				
				//////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevELECTROLYTES_sodium = prevEvent.getLab().getELECTROLYTES_sodium();
					String nextELECTROLYTES_sodium = nextEvent.getLab().getELECTROLYTES_sodium();
					double prevELECTROLYTES_sodiumNumber = 0;
					double nextELECTROLYTES_sodiumNumber = 0;
					if(!prevELECTROLYTES_sodium.equalsIgnoreCase("-") && !nextELECTROLYTES_sodium.equalsIgnoreCase("-")
							&& !prevELECTROLYTES_sodium.equalsIgnoreCase("") && !nextELECTROLYTES_sodium.equalsIgnoreCase("")){
							prevELECTROLYTES_sodiumNumber = Double.parseDouble(prevELECTROLYTES_sodium);
							nextELECTROLYTES_sodiumNumber = Double.parseDouble(nextELECTROLYTES_sodium);
							double ELECTROLYTES_sodiumChange = nextELECTROLYTES_sodiumNumber - prevELECTROLYTES_sodiumNumber;
							if(Math.abs(ELECTROLYTES_sodiumChange) >= FeatureChangeSDU.ELECTROLYTES_sodium){
								features.add(FeatureNumberSDU.ELECTROLYTES_sodium);
								double ELECTROLYTES_sodiumChangePercent = (nextELECTROLYTES_sodiumNumber - prevELECTROLYTES_sodiumNumber) / prevELECTROLYTES_sodiumNumber;
								if(prevELECTROLYTES_sodiumNumber == 0)
									ELECTROLYTES_sodiumChangePercent =  nextELECTROLYTES_sodiumNumber;
								changeVals.add(ELECTROLYTES_sodiumChangePercent);
						}//if(Math.abs)
					}//if(!prevELECTROLYTES_sodium)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevELECTROLYTES_potassium = prevEvent.getLab().getELECTROLYTES_potassium();
					String nextELECTROLYTES_potassium = nextEvent.getLab().getELECTROLYTES_potassium();
					double prevELECTROLYTES_potassiumNumber = 0;
					double nextELECTROLYTES_potassiumNumber = 0;
					if(!prevELECTROLYTES_potassium.equalsIgnoreCase("-") && !nextELECTROLYTES_potassium.equalsIgnoreCase("-")
							&& !prevELECTROLYTES_potassium.equalsIgnoreCase("") && !nextELECTROLYTES_potassium.equalsIgnoreCase("")){
							prevELECTROLYTES_potassiumNumber = Double.parseDouble(prevELECTROLYTES_potassium);
							nextELECTROLYTES_potassiumNumber = Double.parseDouble(nextELECTROLYTES_potassium);
							double ELECTROLYTES_potassiumChange = nextELECTROLYTES_potassiumNumber - prevELECTROLYTES_potassiumNumber;
							if(Math.abs(ELECTROLYTES_potassiumChange) >= FeatureChangeSDU.ELECTROLYTES_potassium){
								features.add(FeatureNumberSDU.ELECTROLYTES_potassium);
								double ELECTROLYTES_potassiumChangePercent = (nextELECTROLYTES_potassiumNumber - prevELECTROLYTES_potassiumNumber) / prevELECTROLYTES_potassiumNumber;
								if(prevELECTROLYTES_potassiumNumber == 0)
									ELECTROLYTES_potassiumChangePercent =  nextELECTROLYTES_potassiumNumber;
								changeVals.add(ELECTROLYTES_potassiumChangePercent);
						}//if(Math.abs)
					}//if(!prevELECTROLYTES_potassium)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevELECTROLYTES_bicarbonate = prevEvent.getLab().getELECTROLYTES_bicarbonate();
					String nextELECTROLYTES_bicarbonate = nextEvent.getLab().getELECTROLYTES_bicarbonate();
					double prevELECTROLYTES_bicarbonateNumber = 0;
					double nextELECTROLYTES_bicarbonateNumber = 0;
					if(!prevELECTROLYTES_bicarbonate.equalsIgnoreCase("-") && !nextELECTROLYTES_bicarbonate.equalsIgnoreCase("-")
							&& !prevELECTROLYTES_bicarbonate.equalsIgnoreCase("") && !nextELECTROLYTES_bicarbonate.equalsIgnoreCase("")){
							prevELECTROLYTES_bicarbonateNumber = Double.parseDouble(prevELECTROLYTES_bicarbonate);
							nextELECTROLYTES_bicarbonateNumber = Double.parseDouble(nextELECTROLYTES_bicarbonate);
							double ELECTROLYTES_bicarbonateChange = nextELECTROLYTES_bicarbonateNumber - prevELECTROLYTES_bicarbonateNumber;
							if(Math.abs(ELECTROLYTES_bicarbonateChange) >= FeatureChangeSDU.ELECTROLYTES_bicarbonate){
								features.add(FeatureNumberSDU.ELECTROLYTES_bicarbonate);
								double ELECTROLYTES_bicarbonateChangePercent = (nextELECTROLYTES_bicarbonateNumber - prevELECTROLYTES_bicarbonateNumber) / prevELECTROLYTES_bicarbonateNumber;
								if(prevELECTROLYTES_bicarbonateNumber == 0)
									ELECTROLYTES_bicarbonateChangePercent =  nextELECTROLYTES_bicarbonateNumber;
								changeVals.add(ELECTROLYTES_bicarbonateChangePercent);
						}//if(Math.abs)
					}//if(!prevELECTROLYTES_bicarbonate)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevELECTROLYTES_chloride = prevEvent.getLab().getELECTROLYTES_chloride();
					String nextELECTROLYTES_chloride = nextEvent.getLab().getELECTROLYTES_chloride();
					double prevELECTROLYTES_chlorideNumber = 0;
					double nextELECTROLYTES_chlorideNumber = 0;
					if(!prevELECTROLYTES_chloride.equalsIgnoreCase("-") && !nextELECTROLYTES_chloride.equalsIgnoreCase("-")
							&& !prevELECTROLYTES_chloride.equalsIgnoreCase("") && !nextELECTROLYTES_chloride.equalsIgnoreCase("")){
							prevELECTROLYTES_chlorideNumber = Double.parseDouble(prevELECTROLYTES_chloride);
							nextELECTROLYTES_chlorideNumber = Double.parseDouble(nextELECTROLYTES_chloride);
							double ELECTROLYTES_chlorideChange = nextELECTROLYTES_chlorideNumber - prevELECTROLYTES_chlorideNumber;
							if(Math.abs(ELECTROLYTES_chlorideChange) >= FeatureChangeSDU.ELECTROLYTES_chloride){
								features.add(FeatureNumberSDU.ELECTROLYTES_chloride);
								double ELECTROLYTES_chlorideChangePercent = (nextELECTROLYTES_chlorideNumber - prevELECTROLYTES_chlorideNumber) / prevELECTROLYTES_chlorideNumber;
								if(prevELECTROLYTES_chlorideNumber == 0)
									ELECTROLYTES_chlorideChangePercent =  nextELECTROLYTES_chlorideNumber;
								changeVals.add(ELECTROLYTES_chlorideChangePercent);
						}//if(Math.abs)
					}//if(!prevELECTROLYTES_chloride)
				}//if(prevEvent)
				
				////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevELECTROLYTES_anionGap = prevEvent.getLab().getELECTROLYTES_anionGap();
					String nextELECTROLYTES_anionGap = nextEvent.getLab().getELECTROLYTES_anionGap();
					double prevELECTROLYTES_anionGapNumber = 0;
					double nextELECTROLYTES_anionGapNumber = 0;
					if(!prevELECTROLYTES_anionGap.equalsIgnoreCase("-") && !nextELECTROLYTES_anionGap.equalsIgnoreCase("-")
							&& !prevELECTROLYTES_anionGap.equalsIgnoreCase("") && !nextELECTROLYTES_anionGap.equalsIgnoreCase("")){
							prevELECTROLYTES_anionGapNumber = Double.parseDouble(prevELECTROLYTES_anionGap);
							nextELECTROLYTES_anionGapNumber = Double.parseDouble(nextELECTROLYTES_anionGap);
							double ELECTROLYTES_anionGapChange = nextELECTROLYTES_anionGapNumber - prevELECTROLYTES_anionGapNumber;
							if(Math.abs(ELECTROLYTES_anionGapChange) >= FeatureChangeSDU.ELECTROLYTES_anionGap){
								features.add(FeatureNumberSDU.ELECTROLYTES_anionGap);
								double ELECTROLYTES_anionGapChangePercent = (nextELECTROLYTES_anionGapNumber - prevELECTROLYTES_anionGapNumber) / prevELECTROLYTES_anionGapNumber;
								if(prevELECTROLYTES_anionGapNumber == 0)
									ELECTROLYTES_anionGapChangePercent =  nextELECTROLYTES_anionGapNumber;
								changeVals.add(ELECTROLYTES_anionGapChangePercent);
						}//if(Math.abs)
					}//if(!prevELECTROLYTES_anionGap)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevELECTROLYTES_magnesium = prevEvent.getLab().getELECTROLYTES_magnesium();
					String nextELECTROLYTES_magnesium = nextEvent.getLab().getELECTROLYTES_magnesium();
					double prevELECTROLYTES_magnesiumNumber = 0;
					double nextELECTROLYTES_magnesiumNumber = 0;
					if(!prevELECTROLYTES_magnesium.equalsIgnoreCase("-") && !nextELECTROLYTES_magnesium.equalsIgnoreCase("-")
							&& !prevELECTROLYTES_magnesium.equalsIgnoreCase("") && !nextELECTROLYTES_magnesium.equalsIgnoreCase("")){
							prevELECTROLYTES_magnesiumNumber = Double.parseDouble(prevELECTROLYTES_magnesium);
							nextELECTROLYTES_magnesiumNumber = Double.parseDouble(nextELECTROLYTES_magnesium);
							double ELECTROLYTES_magnesiumChange = nextELECTROLYTES_magnesiumNumber - prevELECTROLYTES_magnesiumNumber;
							if(Math.abs(ELECTROLYTES_magnesiumChange) >= FeatureChangeSDU.ELECTROLYTES_magnesium){
								features.add(FeatureNumberSDU.ELECTROLYTES_magnesium);
								double ELECTROLYTES_magnesiumChangePercent = (nextELECTROLYTES_magnesiumNumber - prevELECTROLYTES_magnesiumNumber) / prevELECTROLYTES_magnesiumNumber;
								if(prevELECTROLYTES_magnesiumNumber == 0)
									ELECTROLYTES_magnesiumChangePercent =  nextELECTROLYTES_magnesiumNumber;
								changeVals.add(ELECTROLYTES_magnesiumChangePercent);
						}//if(Math.abs)
					}//if(!prevELECTROLYTES_magnesium)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevKIDNEY_bloodUreaNitrogen = prevEvent.getLab().getKIDNEY_bloodUreaNitrogen();
					String nextKIDNEY_bloodUreaNitrogen = nextEvent.getLab().getKIDNEY_bloodUreaNitrogen();
					double prevKIDNEY_bloodUreaNitrogenNumber = 0;
					double nextKIDNEY_bloodUreaNitrogenNumber = 0;
					if(!prevKIDNEY_bloodUreaNitrogen.equalsIgnoreCase("-") && !nextKIDNEY_bloodUreaNitrogen.equalsIgnoreCase("-")
							&& !prevKIDNEY_bloodUreaNitrogen.equalsIgnoreCase("") && !nextKIDNEY_bloodUreaNitrogen.equalsIgnoreCase("")){
							prevKIDNEY_bloodUreaNitrogenNumber = Double.parseDouble(prevKIDNEY_bloodUreaNitrogen);
							nextKIDNEY_bloodUreaNitrogenNumber = Double.parseDouble(nextKIDNEY_bloodUreaNitrogen);
							double KIDNEY_bloodUreaNitrogenChange = nextKIDNEY_bloodUreaNitrogenNumber - prevKIDNEY_bloodUreaNitrogenNumber;
							if(Math.abs(KIDNEY_bloodUreaNitrogenChange) >= FeatureChangeSDU.KIDNEY_bloodUreaNitrogen){
								features.add(FeatureNumberSDU.KIDNEY_bloodUreaNitrogen);
								double KIDNEY_bloodUreaNitrogenChangePercent = (nextKIDNEY_bloodUreaNitrogenNumber - prevKIDNEY_bloodUreaNitrogenNumber) / prevKIDNEY_bloodUreaNitrogenNumber;
								if(prevKIDNEY_bloodUreaNitrogenNumber == 0)
									KIDNEY_bloodUreaNitrogenChangePercent =  nextKIDNEY_bloodUreaNitrogenNumber;
								changeVals.add(KIDNEY_bloodUreaNitrogenChangePercent);
						}//if(Math.abs)
					}//if(!prevKIDNEY_bloodUreaNitrogen)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevKIDNEY_uricAcid = prevEvent.getLab().getKIDNEY_uricAcid();
					String nextKIDNEY_uricAcid = nextEvent.getLab().getKIDNEY_uricAcid();
					double prevKIDNEY_uricAcidNumber = 0;
					double nextKIDNEY_uricAcidNumber = 0;
					if(!prevKIDNEY_uricAcid.equalsIgnoreCase("-") && !nextKIDNEY_uricAcid.equalsIgnoreCase("-")
							&& !prevKIDNEY_uricAcid.equalsIgnoreCase("") && !nextKIDNEY_uricAcid.equalsIgnoreCase("")){
							prevKIDNEY_uricAcidNumber = Double.parseDouble(prevKIDNEY_uricAcid);
							nextKIDNEY_uricAcidNumber = Double.parseDouble(nextKIDNEY_uricAcid);
							double KIDNEY_uricAcidChange = nextKIDNEY_uricAcidNumber - prevKIDNEY_uricAcidNumber;
							if(Math.abs(KIDNEY_uricAcidChange) >= FeatureChangeSDU.KIDNEY_uricAcid){
								features.add(FeatureNumberSDU.KIDNEY_uricAcid);
								double KIDNEY_uricAcidChangePercent = (nextKIDNEY_uricAcidNumber - prevKIDNEY_uricAcidNumber) / prevKIDNEY_uricAcidNumber;
								if(prevKIDNEY_uricAcidNumber == 0)
									KIDNEY_uricAcidChangePercent =  nextKIDNEY_uricAcidNumber;
								changeVals.add(KIDNEY_uricAcidChangePercent);
						}//if(Math.abs)
					}//if(!prevKIDNEY_uricAcid)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevKIDNEY_creatinine = prevEvent.getLab().getKIDNEY_creatinine();
					String nextKIDNEY_creatinine = nextEvent.getLab().getKIDNEY_creatinine();
					double prevKIDNEY_creatinineNumber = 0;
					double nextKIDNEY_creatinineNumber = 0;
					if(!prevKIDNEY_creatinine.equalsIgnoreCase("-") && !nextKIDNEY_creatinine.equalsIgnoreCase("-")
							&& !prevKIDNEY_creatinine.equalsIgnoreCase("") && !nextKIDNEY_creatinine.equalsIgnoreCase("")){
							prevKIDNEY_creatinineNumber = Double.parseDouble(prevKIDNEY_creatinine);
							nextKIDNEY_creatinineNumber = Double.parseDouble(nextKIDNEY_creatinine);
							double KIDNEY_creatinineChange = nextKIDNEY_creatinineNumber - prevKIDNEY_creatinineNumber;
							if(Math.abs(KIDNEY_creatinineChange) >= FeatureChangeSDU.KIDNEY_creatinine){
								features.add(FeatureNumberSDU.KIDNEY_creatinine);
								double KIDNEY_creatinineChangePercent = (nextKIDNEY_creatinineNumber - prevKIDNEY_creatinineNumber) / prevKIDNEY_creatinineNumber;
								if(prevKIDNEY_creatinineNumber == 0)
									KIDNEY_creatinineChangePercent =  nextKIDNEY_creatinineNumber;
								changeVals.add(KIDNEY_creatinineChangePercent);
						}//if(Math.abs)
					}//if(!prevKIDNEY_creatinine)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevLIVER_alkalinePhosphatase = prevEvent.getLab().getLIVER_alkalinePhosphatase();
					String nextLIVER_alkalinePhosphatase = nextEvent.getLab().getLIVER_alkalinePhosphatase();
					double prevLIVER_alkalinePhosphataseNumber = 0;
					double nextLIVER_alkalinePhosphataseNumber = 0;
					if(!prevLIVER_alkalinePhosphatase.equalsIgnoreCase("-") && !nextLIVER_alkalinePhosphatase.equalsIgnoreCase("-")
							&& !prevLIVER_alkalinePhosphatase.equalsIgnoreCase("") && !nextLIVER_alkalinePhosphatase.equalsIgnoreCase("")){
							prevLIVER_alkalinePhosphataseNumber = Double.parseDouble(prevLIVER_alkalinePhosphatase);
							nextLIVER_alkalinePhosphataseNumber = Double.parseDouble(nextLIVER_alkalinePhosphatase);
							double LIVER_alkalinePhosphataseChange = nextLIVER_alkalinePhosphataseNumber - prevLIVER_alkalinePhosphataseNumber;
							if(Math.abs(LIVER_alkalinePhosphataseChange) >= FeatureChangeSDU.LIVER_alkalinePhosphatase){
								features.add(FeatureNumberSDU.LIVER_alkalinePhosphatase);
								double LIVER_alkalinePhosphataseChangePercent = (nextLIVER_alkalinePhosphataseNumber - prevLIVER_alkalinePhosphataseNumber) / prevLIVER_alkalinePhosphataseNumber;
								if(prevLIVER_alkalinePhosphataseNumber == 0)
									LIVER_alkalinePhosphataseChangePercent =  nextLIVER_alkalinePhosphataseNumber;
								changeVals.add(LIVER_alkalinePhosphataseChangePercent);
						}//if(Math.abs)
					}//if(!prevLIVER_alkalinePhosphatase)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevLIVER_alt = prevEvent.getLab().getLIVER_alt();
					String nextLIVER_alt = nextEvent.getLab().getLIVER_alt();
					double prevLIVER_altNumber = 0;
					double nextLIVER_altNumber = 0;
					if(!prevLIVER_alt.equalsIgnoreCase("-") && !nextLIVER_alt.equalsIgnoreCase("-")
							&& !prevLIVER_alt.equalsIgnoreCase("") && !nextLIVER_alt.equalsIgnoreCase("")){
							if(prevLIVER_alt.equalsIgnoreCase("normal"))
								prevLIVER_altNumber = 10;
							else
								prevLIVER_altNumber = Double.parseDouble(prevLIVER_alt);
							
							if(nextLIVER_alt.equalsIgnoreCase("normal"))
								nextLIVER_altNumber = 10;
							else
								nextLIVER_altNumber = Double.parseDouble(nextLIVER_alt);
							double LIVER_altChange = nextLIVER_altNumber - prevLIVER_altNumber;
							if(Math.abs(LIVER_altChange) >= FeatureChangeSDU.LIVER_alt){
								features.add(FeatureNumberSDU.LIVER_alt);
								double LIVER_altChangePercent = (nextLIVER_altNumber - prevLIVER_altNumber) / prevLIVER_altNumber;
								if(prevLIVER_altNumber == 0)
									LIVER_altChangePercent =  nextLIVER_altNumber;
								changeVals.add(LIVER_altChangePercent);
						}//if(Math.abs)
					}//if(!prevLIVER_alt)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevLIVER_gammaGlutamylTransferase = prevEvent.getLab().getLIVER_gammaGlutamylTransferase();
					String nextLIVER_gammaGlutamylTransferase = nextEvent.getLab().getLIVER_gammaGlutamylTransferase();
					double prevLIVER_gammaGlutamylTransferaseNumber = 0;
					double nextLIVER_gammaGlutamylTransferaseNumber = 0;
					if(!prevLIVER_gammaGlutamylTransferase.equalsIgnoreCase("-") && !nextLIVER_gammaGlutamylTransferase.equalsIgnoreCase("-")
							&& !prevLIVER_gammaGlutamylTransferase.equalsIgnoreCase("") && !nextLIVER_gammaGlutamylTransferase.equalsIgnoreCase("")){
							if(prevLIVER_gammaGlutamylTransferase.equalsIgnoreCase("<4"))
								prevLIVER_gammaGlutamylTransferaseNumber = 4;
							else
								prevLIVER_gammaGlutamylTransferaseNumber = Double.parseDouble(prevLIVER_gammaGlutamylTransferase);
							
							if(nextLIVER_gammaGlutamylTransferase.equalsIgnoreCase("<4"))
								nextLIVER_gammaGlutamylTransferaseNumber = 4;
							else
								nextLIVER_gammaGlutamylTransferaseNumber = Double.parseDouble(nextLIVER_gammaGlutamylTransferase);
							double LIVER_gammaGlutamylTransferaseChange = nextLIVER_gammaGlutamylTransferaseNumber - prevLIVER_gammaGlutamylTransferaseNumber;
							if(Math.abs(LIVER_gammaGlutamylTransferaseChange) >= FeatureChangeSDU.LIVER_gammaGlutamylTransferase){
								features.add(FeatureNumberSDU.LIVER_gammaGlutamylTransferase);
								double LIVER_gammaGlutamylTransferaseChangePercent = (nextLIVER_gammaGlutamylTransferaseNumber - prevLIVER_gammaGlutamylTransferaseNumber) / prevLIVER_gammaGlutamylTransferaseNumber;
								if(prevLIVER_gammaGlutamylTransferaseNumber == 0)
									LIVER_gammaGlutamylTransferaseChangePercent =  nextLIVER_gammaGlutamylTransferaseNumber;
								changeVals.add(LIVER_gammaGlutamylTransferaseChangePercent);
						}//if(Math.abs)
					}//if(!prevLIVER_gammaGlutamylTransferase)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevLIVER_ast = prevEvent.getLab().getLIVER_ast();
					String nextLIVER_ast = nextEvent.getLab().getLIVER_ast();
					double prevLIVER_astNumber = 0;
					double nextLIVER_astNumber = 0;
					if(!prevLIVER_ast.equalsIgnoreCase("-") && !nextLIVER_ast.equalsIgnoreCase("-")
							&& !prevLIVER_ast.equalsIgnoreCase("") && !nextLIVER_ast.equalsIgnoreCase("")){
							prevLIVER_astNumber = Double.parseDouble(prevLIVER_ast);
							nextLIVER_astNumber = Double.parseDouble(nextLIVER_ast);
							double LIVER_astChange = nextLIVER_astNumber - prevLIVER_astNumber;
							if(Math.abs(LIVER_astChange) >= FeatureChangeSDU.LIVER_ast){
								features.add(FeatureNumberSDU.LIVER_ast);
								double LIVER_astChangePercent = (nextLIVER_astNumber - prevLIVER_astNumber) / prevLIVER_astNumber;
								if(prevLIVER_astNumber == 0)
									LIVER_astChangePercent =  nextLIVER_astNumber;
								changeVals.add(LIVER_astChangePercent);
						}//if(Math.abs)
					}//if(!prevLIVER_ast)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevLIVER_bilirubinTotal = prevEvent.getLab().getLIVER_bilirubinTotal();
					String nextLIVER_bilirubinTotal = nextEvent.getLab().getLIVER_bilirubinTotal();
					double prevLIVER_bilirubinTotalNumber = 0;
					double nextLIVER_bilirubinTotalNumber = 0;
					if(!prevLIVER_bilirubinTotal.equalsIgnoreCase("-") && !nextLIVER_bilirubinTotal.equalsIgnoreCase("-")
							&& !prevLIVER_bilirubinTotal.equalsIgnoreCase("") && !nextLIVER_bilirubinTotal.equalsIgnoreCase("")){
							if(prevLIVER_bilirubinTotal.equalsIgnoreCase("<3") || prevLIVER_bilirubinTotal.equalsIgnoreCase("<3.42"))
								prevLIVER_bilirubinTotalNumber = 3;
							else if(prevLIVER_bilirubinTotal.equalsIgnoreCase("<0.2"))
								prevLIVER_bilirubinTotalNumber = 0.2;
							else
								prevLIVER_bilirubinTotalNumber = Double.parseDouble(prevLIVER_bilirubinTotal);
							
							if(nextLIVER_bilirubinTotal.equalsIgnoreCase("<3") || nextLIVER_bilirubinTotal.equalsIgnoreCase("<3.42"))
								nextLIVER_bilirubinTotalNumber = 3;
							else if(nextLIVER_bilirubinTotal.equalsIgnoreCase("<0.2"))
								nextLIVER_bilirubinTotalNumber = 0.2;
							else
								nextLIVER_bilirubinTotalNumber = Double.parseDouble(nextLIVER_bilirubinTotal);
							
							double LIVER_bilirubinTotalChange = nextLIVER_bilirubinTotalNumber - prevLIVER_bilirubinTotalNumber;
							if(Math.abs(LIVER_bilirubinTotalChange) >= FeatureChangeSDU.LIVER_bilirubinTotal){
								features.add(FeatureNumberSDU.LIVER_bilirubinTotal);
								double LIVER_bilirubinTotalChangePercent = (nextLIVER_bilirubinTotalNumber - prevLIVER_bilirubinTotalNumber) / prevLIVER_bilirubinTotalNumber;
								if(prevLIVER_bilirubinTotalNumber == 0)
									LIVER_bilirubinTotalChangePercent =  nextLIVER_bilirubinTotalNumber;
								changeVals.add(LIVER_bilirubinTotalChangePercent);
						}//if(Math.abs)
					}//if(!prevLIVER_bilirubinTotal)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_whiteBloodCell = prevEvent.getLab().getCOMPLETE_BLOOD_whiteBloodCell();
					String nextCOMPLETE_BLOOD_whiteBloodCell = nextEvent.getLab().getCOMPLETE_BLOOD_whiteBloodCell();
					double prevCOMPLETE_BLOOD_whiteBloodCellNumber = 0;
					double nextCOMPLETE_BLOOD_whiteBloodCellNumber = 0;
					if(!prevCOMPLETE_BLOOD_whiteBloodCell.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_whiteBloodCell.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_whiteBloodCell.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_whiteBloodCell.equalsIgnoreCase("")){
						
							if(prevCOMPLETE_BLOOD_whiteBloodCell.equalsIgnoreCase("very large"))
								prevCOMPLETE_BLOOD_whiteBloodCellNumber = 15;
							else
								prevCOMPLETE_BLOOD_whiteBloodCellNumber = Double.parseDouble(prevCOMPLETE_BLOOD_whiteBloodCell);
							
							if(nextCOMPLETE_BLOOD_whiteBloodCell.equalsIgnoreCase("very large"))
								nextCOMPLETE_BLOOD_whiteBloodCellNumber = 15;
							else
								nextCOMPLETE_BLOOD_whiteBloodCellNumber = Double.parseDouble(nextCOMPLETE_BLOOD_whiteBloodCell);
							
							double COMPLETE_BLOOD_whiteBloodCellChange = nextCOMPLETE_BLOOD_whiteBloodCellNumber - prevCOMPLETE_BLOOD_whiteBloodCellNumber;
							if(Math.abs(COMPLETE_BLOOD_whiteBloodCellChange) >= FeatureChangeSDU.COMPLETE_BLOOD_whiteBloodCell){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_whiteBloodCell);
								double COMPLETE_BLOOD_whiteBloodCellChangePercent = (nextCOMPLETE_BLOOD_whiteBloodCellNumber - prevCOMPLETE_BLOOD_whiteBloodCellNumber) / prevCOMPLETE_BLOOD_whiteBloodCellNumber;
								if(prevCOMPLETE_BLOOD_whiteBloodCellNumber == 0)
									COMPLETE_BLOOD_whiteBloodCellChangePercent =  nextCOMPLETE_BLOOD_whiteBloodCellNumber;
								changeVals.add(COMPLETE_BLOOD_whiteBloodCellChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_whiteBloodCell)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_neutrophils = prevEvent.getLab().getCOMPLETE_BLOOD_neutrophils();
					String nextCOMPLETE_BLOOD_neutrophils = nextEvent.getLab().getCOMPLETE_BLOOD_neutrophils();
					double prevCOMPLETE_BLOOD_neutrophilsNumber = 0;
					double nextCOMPLETE_BLOOD_neutrophilsNumber = 0;
					if(!prevCOMPLETE_BLOOD_neutrophils.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_neutrophils.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_neutrophils.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_neutrophils.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_neutrophilsNumber = Double.parseDouble(prevCOMPLETE_BLOOD_neutrophils);
							nextCOMPLETE_BLOOD_neutrophilsNumber = Double.parseDouble(nextCOMPLETE_BLOOD_neutrophils);
							double COMPLETE_BLOOD_neutrophilsChange = nextCOMPLETE_BLOOD_neutrophilsNumber - prevCOMPLETE_BLOOD_neutrophilsNumber;
							if(Math.abs(COMPLETE_BLOOD_neutrophilsChange) >= FeatureChangeSDU.COMPLETE_BLOOD_neutrophils){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_neutrophils);
								double COMPLETE_BLOOD_neutrophilsChangePercent = (nextCOMPLETE_BLOOD_neutrophilsNumber - prevCOMPLETE_BLOOD_neutrophilsNumber) / prevCOMPLETE_BLOOD_neutrophilsNumber;
								if(prevCOMPLETE_BLOOD_neutrophilsNumber == 0)
									COMPLETE_BLOOD_neutrophilsChangePercent =  nextCOMPLETE_BLOOD_neutrophilsNumber;
								changeVals.add(COMPLETE_BLOOD_neutrophilsChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_neutrophils)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_absoluteNeutrophilCount = prevEvent.getLab().getCOMPLETE_BLOOD_absoluteNeutrophilCount();
					String nextCOMPLETE_BLOOD_absoluteNeutrophilCount = nextEvent.getLab().getCOMPLETE_BLOOD_absoluteNeutrophilCount();
					double prevCOMPLETE_BLOOD_absoluteNeutrophilCountNumber = 0;
					double nextCOMPLETE_BLOOD_absoluteNeutrophilCountNumber = 0;
					if(!prevCOMPLETE_BLOOD_absoluteNeutrophilCount.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_absoluteNeutrophilCount.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_absoluteNeutrophilCount.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_absoluteNeutrophilCount.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_absoluteNeutrophilCountNumber = Double.parseDouble(prevCOMPLETE_BLOOD_absoluteNeutrophilCount);
							nextCOMPLETE_BLOOD_absoluteNeutrophilCountNumber = Double.parseDouble(nextCOMPLETE_BLOOD_absoluteNeutrophilCount);
							double COMPLETE_BLOOD_absoluteNeutrophilCountChange = nextCOMPLETE_BLOOD_absoluteNeutrophilCountNumber - prevCOMPLETE_BLOOD_absoluteNeutrophilCountNumber;
							if(Math.abs(COMPLETE_BLOOD_absoluteNeutrophilCountChange) >= FeatureChangeSDU.COMPLETE_BLOOD_absoluteNeutrophilCount){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_absoluteNeutrophilCount);
								double COMPLETE_BLOOD_absoluteNeutrophilCountChangePercent = (nextCOMPLETE_BLOOD_absoluteNeutrophilCountNumber - prevCOMPLETE_BLOOD_absoluteNeutrophilCountNumber) / prevCOMPLETE_BLOOD_absoluteNeutrophilCountNumber;
								if(prevCOMPLETE_BLOOD_absoluteNeutrophilCountNumber == 0)
									COMPLETE_BLOOD_absoluteNeutrophilCountChangePercent =  nextCOMPLETE_BLOOD_absoluteNeutrophilCountNumber;
								changeVals.add(COMPLETE_BLOOD_absoluteNeutrophilCountChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_absoluteNeutrophilCount)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_bandNeutrophils = prevEvent.getLab().getCOMPLETE_BLOOD_bandNeutrophils();
					String nextCOMPLETE_BLOOD_bandNeutrophils = nextEvent.getLab().getCOMPLETE_BLOOD_bandNeutrophils();
					double prevCOMPLETE_BLOOD_bandNeutrophilsNumber = 0;
					double nextCOMPLETE_BLOOD_bandNeutrophilsNumber = 0;
					if(!prevCOMPLETE_BLOOD_bandNeutrophils.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_bandNeutrophils.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_bandNeutrophils.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_bandNeutrophils.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_bandNeutrophilsNumber = Double.parseDouble(prevCOMPLETE_BLOOD_bandNeutrophils);
							nextCOMPLETE_BLOOD_bandNeutrophilsNumber = Double.parseDouble(nextCOMPLETE_BLOOD_bandNeutrophils);
							double COMPLETE_BLOOD_bandNeutrophilsChange = nextCOMPLETE_BLOOD_bandNeutrophilsNumber - prevCOMPLETE_BLOOD_bandNeutrophilsNumber;
							if(Math.abs(COMPLETE_BLOOD_bandNeutrophilsChange) >= FeatureChangeSDU.COMPLETE_BLOOD_bandNeutrophils){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_bandNeutrophils);
								double COMPLETE_BLOOD_bandNeutrophilsChangePercent = (nextCOMPLETE_BLOOD_bandNeutrophilsNumber - prevCOMPLETE_BLOOD_bandNeutrophilsNumber) / prevCOMPLETE_BLOOD_bandNeutrophilsNumber;
								if(prevCOMPLETE_BLOOD_bandNeutrophilsNumber == 0)
									COMPLETE_BLOOD_bandNeutrophilsChangePercent =  nextCOMPLETE_BLOOD_bandNeutrophilsNumber;
								changeVals.add(COMPLETE_BLOOD_bandNeutrophilsChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_bandNeutrophils)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_absoluteBandNeutrophilCount = prevEvent.getLab().getCOMPLETE_BLOOD_absoluteBandNeutrophilCount();
					String nextCOMPLETE_BLOOD_absoluteBandNeutrophilCount = nextEvent.getLab().getCOMPLETE_BLOOD_absoluteBandNeutrophilCount();
					double prevCOMPLETE_BLOOD_absoluteBandNeutrophilCountNumber = 0;
					double nextCOMPLETE_BLOOD_absoluteBandNeutrophilCountNumber = 0;
					if(!prevCOMPLETE_BLOOD_absoluteBandNeutrophilCount.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_absoluteBandNeutrophilCount.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_absoluteBandNeutrophilCount.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_absoluteBandNeutrophilCount.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_absoluteBandNeutrophilCountNumber = Double.parseDouble(prevCOMPLETE_BLOOD_absoluteBandNeutrophilCount);
							nextCOMPLETE_BLOOD_absoluteBandNeutrophilCountNumber = Double.parseDouble(nextCOMPLETE_BLOOD_absoluteBandNeutrophilCount);
							double COMPLETE_BLOOD_absoluteBandNeutrophilCountChange = nextCOMPLETE_BLOOD_absoluteBandNeutrophilCountNumber - prevCOMPLETE_BLOOD_absoluteBandNeutrophilCountNumber;
							if(Math.abs(COMPLETE_BLOOD_absoluteBandNeutrophilCountChange) >= FeatureChangeSDU.COMPLETE_BLOOD_absoluteBandNeutrophilCount){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_absoluteBandNeutrophilCount);
								double COMPLETE_BLOOD_absoluteBandNeutrophilCountChangePercent = (nextCOMPLETE_BLOOD_absoluteBandNeutrophilCountNumber - prevCOMPLETE_BLOOD_absoluteBandNeutrophilCountNumber) / prevCOMPLETE_BLOOD_absoluteBandNeutrophilCountNumber;
								if(prevCOMPLETE_BLOOD_absoluteBandNeutrophilCountNumber == 0)
									COMPLETE_BLOOD_absoluteBandNeutrophilCountChangePercent =  nextCOMPLETE_BLOOD_absoluteBandNeutrophilCountNumber;
								changeVals.add(COMPLETE_BLOOD_absoluteBandNeutrophilCountChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_absoluteBandNeutrophilCount)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_lymphocytes = prevEvent.getLab().getCOMPLETE_BLOOD_lymphocytes();
					String nextCOMPLETE_BLOOD_lymphocytes = nextEvent.getLab().getCOMPLETE_BLOOD_lymphocytes();
					double prevCOMPLETE_BLOOD_lymphocytesNumber = 0;
					double nextCOMPLETE_BLOOD_lymphocytesNumber = 0;
					if(!prevCOMPLETE_BLOOD_lymphocytes.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_lymphocytes.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_lymphocytes.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_lymphocytes.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_lymphocytesNumber = Double.parseDouble(prevCOMPLETE_BLOOD_lymphocytes);
							nextCOMPLETE_BLOOD_lymphocytesNumber = Double.parseDouble(nextCOMPLETE_BLOOD_lymphocytes);
							double COMPLETE_BLOOD_lymphocytesChange = nextCOMPLETE_BLOOD_lymphocytesNumber - prevCOMPLETE_BLOOD_lymphocytesNumber;
							if(Math.abs(COMPLETE_BLOOD_lymphocytesChange) >= FeatureChangeSDU.COMPLETE_BLOOD_lymphocytes){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_lymphocytes);
								double COMPLETE_BLOOD_lymphocytesChangePercent = (nextCOMPLETE_BLOOD_lymphocytesNumber - prevCOMPLETE_BLOOD_lymphocytesNumber) / prevCOMPLETE_BLOOD_lymphocytesNumber;
								if(prevCOMPLETE_BLOOD_lymphocytesNumber == 0)
									COMPLETE_BLOOD_lymphocytesChangePercent =  nextCOMPLETE_BLOOD_lymphocytesNumber;
								changeVals.add(COMPLETE_BLOOD_lymphocytesChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_lymphocytes)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_absoluteLymphocyteCount = prevEvent.getLab().getCOMPLETE_BLOOD_absoluteLymphocyteCount();
					String nextCOMPLETE_BLOOD_absoluteLymphocyteCount = nextEvent.getLab().getCOMPLETE_BLOOD_absoluteLymphocyteCount();
					double prevCOMPLETE_BLOOD_absoluteLymphocyteCountNumber = 0;
					double nextCOMPLETE_BLOOD_absoluteLymphocyteCountNumber = 0;
					if(!prevCOMPLETE_BLOOD_absoluteLymphocyteCount.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_absoluteLymphocyteCount.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_absoluteLymphocyteCount.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_absoluteLymphocyteCount.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_absoluteLymphocyteCountNumber = Double.parseDouble(prevCOMPLETE_BLOOD_absoluteLymphocyteCount);
							nextCOMPLETE_BLOOD_absoluteLymphocyteCountNumber = Double.parseDouble(nextCOMPLETE_BLOOD_absoluteLymphocyteCount);
							double COMPLETE_BLOOD_absoluteLymphocyteCountChange = nextCOMPLETE_BLOOD_absoluteLymphocyteCountNumber - prevCOMPLETE_BLOOD_absoluteLymphocyteCountNumber;
							if(Math.abs(COMPLETE_BLOOD_absoluteLymphocyteCountChange) >= FeatureChangeSDU.COMPLETE_BLOOD_absoluteLymphocyteCount){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_absoluteLymphocyteCount);
								double COMPLETE_BLOOD_absoluteLymphocyteCountChangePercent = (nextCOMPLETE_BLOOD_absoluteLymphocyteCountNumber - prevCOMPLETE_BLOOD_absoluteLymphocyteCountNumber) / prevCOMPLETE_BLOOD_absoluteLymphocyteCountNumber;
								if(prevCOMPLETE_BLOOD_absoluteLymphocyteCountNumber == 0)
									COMPLETE_BLOOD_absoluteLymphocyteCountChangePercent =  nextCOMPLETE_BLOOD_absoluteLymphocyteCountNumber;
								changeVals.add(COMPLETE_BLOOD_absoluteLymphocyteCountChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_absoluteLymphocyteCount)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_monocytes = prevEvent.getLab().getCOMPLETE_BLOOD_monocytes();
					String nextCOMPLETE_BLOOD_monocytes = nextEvent.getLab().getCOMPLETE_BLOOD_monocytes();
					double prevCOMPLETE_BLOOD_monocytesNumber = 0;
					double nextCOMPLETE_BLOOD_monocytesNumber = 0;
					if(!prevCOMPLETE_BLOOD_monocytes.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_monocytes.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_monocytes.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_monocytes.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_monocytesNumber = Double.parseDouble(prevCOMPLETE_BLOOD_monocytes);
							nextCOMPLETE_BLOOD_monocytesNumber = Double.parseDouble(nextCOMPLETE_BLOOD_monocytes);
							double COMPLETE_BLOOD_monocytesChange = nextCOMPLETE_BLOOD_monocytesNumber - prevCOMPLETE_BLOOD_monocytesNumber;
							if(Math.abs(COMPLETE_BLOOD_monocytesChange) >= FeatureChangeSDU.COMPLETE_BLOOD_monocytes){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_monocytes);
								double COMPLETE_BLOOD_monocytesChangePercent = (nextCOMPLETE_BLOOD_monocytesNumber - prevCOMPLETE_BLOOD_monocytesNumber) / prevCOMPLETE_BLOOD_monocytesNumber;
								if(prevCOMPLETE_BLOOD_monocytesNumber == 0)
									COMPLETE_BLOOD_monocytesChangePercent =  nextCOMPLETE_BLOOD_monocytesNumber;
								changeVals.add(COMPLETE_BLOOD_monocytesChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_monocytes)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_absoluteMonocyteCount = prevEvent.getLab().getCOMPLETE_BLOOD_absoluteMonocyteCount();
					String nextCOMPLETE_BLOOD_absoluteMonocyteCount = nextEvent.getLab().getCOMPLETE_BLOOD_absoluteMonocyteCount();
					double prevCOMPLETE_BLOOD_absoluteMonocyteCountNumber = 0;
					double nextCOMPLETE_BLOOD_absoluteMonocyteCountNumber = 0;
					if(!prevCOMPLETE_BLOOD_absoluteMonocyteCount.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_absoluteMonocyteCount.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_absoluteMonocyteCount.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_absoluteMonocyteCount.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_absoluteMonocyteCountNumber = Double.parseDouble(prevCOMPLETE_BLOOD_absoluteMonocyteCount);
							nextCOMPLETE_BLOOD_absoluteMonocyteCountNumber = Double.parseDouble(nextCOMPLETE_BLOOD_absoluteMonocyteCount);
							double COMPLETE_BLOOD_absoluteMonocyteCountChange = nextCOMPLETE_BLOOD_absoluteMonocyteCountNumber - prevCOMPLETE_BLOOD_absoluteMonocyteCountNumber;
							if(Math.abs(COMPLETE_BLOOD_absoluteMonocyteCountChange) >= FeatureChangeSDU.COMPLETE_BLOOD_absoluteMonocyteCount){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_absoluteMonocyteCount);
								double COMPLETE_BLOOD_absoluteMonocyteCountChangePercent = (nextCOMPLETE_BLOOD_absoluteMonocyteCountNumber - prevCOMPLETE_BLOOD_absoluteMonocyteCountNumber) / prevCOMPLETE_BLOOD_absoluteMonocyteCountNumber;
								if(prevCOMPLETE_BLOOD_absoluteMonocyteCountNumber == 0)
									COMPLETE_BLOOD_absoluteMonocyteCountChangePercent =  nextCOMPLETE_BLOOD_absoluteMonocyteCountNumber;
								changeVals.add(COMPLETE_BLOOD_absoluteMonocyteCountChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_absoluteMonocyteCount)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_eosinophils = prevEvent.getLab().getCOMPLETE_BLOOD_eosinophils();
					String nextCOMPLETE_BLOOD_eosinophils = nextEvent.getLab().getCOMPLETE_BLOOD_eosinophils();
					double prevCOMPLETE_BLOOD_eosinophilsNumber = 0;
					double nextCOMPLETE_BLOOD_eosinophilsNumber = 0;
					if(!prevCOMPLETE_BLOOD_eosinophils.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_eosinophils.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_eosinophils.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_eosinophils.equalsIgnoreCase("")){
							if(prevCOMPLETE_BLOOD_eosinophils.equalsIgnoreCase("normal"))
								prevCOMPLETE_BLOOD_eosinophilsNumber = 2;
							else
								prevCOMPLETE_BLOOD_eosinophilsNumber = Double.parseDouble(prevCOMPLETE_BLOOD_eosinophils);
							
							if(nextCOMPLETE_BLOOD_eosinophils.equalsIgnoreCase("normal"))
								nextCOMPLETE_BLOOD_eosinophilsNumber = 2;
							else
								nextCOMPLETE_BLOOD_eosinophilsNumber = Double.parseDouble(nextCOMPLETE_BLOOD_eosinophils);
							double COMPLETE_BLOOD_eosinophilsChange = nextCOMPLETE_BLOOD_eosinophilsNumber - prevCOMPLETE_BLOOD_eosinophilsNumber;
							if(Math.abs(COMPLETE_BLOOD_eosinophilsChange) >= FeatureChangeSDU.COMPLETE_BLOOD_eosinophils){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_eosinophils);
								double COMPLETE_BLOOD_eosinophilsChangePercent = (nextCOMPLETE_BLOOD_eosinophilsNumber - prevCOMPLETE_BLOOD_eosinophilsNumber) / prevCOMPLETE_BLOOD_eosinophilsNumber;
								if(prevCOMPLETE_BLOOD_eosinophilsNumber == 0)
									COMPLETE_BLOOD_eosinophilsChangePercent =  nextCOMPLETE_BLOOD_eosinophilsNumber;
								changeVals.add(COMPLETE_BLOOD_eosinophilsChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_eosinophils)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_absoluteEosinophilCount = prevEvent.getLab().getCOMPLETE_BLOOD_absoluteEosinophilCount();
					String nextCOMPLETE_BLOOD_absoluteEosinophilCount = nextEvent.getLab().getCOMPLETE_BLOOD_absoluteEosinophilCount();
					double prevCOMPLETE_BLOOD_absoluteEosinophilCountNumber = 0;
					double nextCOMPLETE_BLOOD_absoluteEosinophilCountNumber = 0;
					if(!prevCOMPLETE_BLOOD_absoluteEosinophilCount.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_absoluteEosinophilCount.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_absoluteEosinophilCount.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_absoluteEosinophilCount.equalsIgnoreCase("")){
							if(prevCOMPLETE_BLOOD_absoluteEosinophilCount.equalsIgnoreCase("normal"))
								prevCOMPLETE_BLOOD_absoluteEosinophilCountNumber = 0.25;
							else
								prevCOMPLETE_BLOOD_absoluteEosinophilCountNumber = Double.parseDouble(prevCOMPLETE_BLOOD_absoluteEosinophilCount);
							
							if(nextCOMPLETE_BLOOD_absoluteEosinophilCount.equalsIgnoreCase("normal"))
								nextCOMPLETE_BLOOD_absoluteEosinophilCountNumber = 0.25;
							else
								nextCOMPLETE_BLOOD_absoluteEosinophilCountNumber = Double.parseDouble(nextCOMPLETE_BLOOD_absoluteEosinophilCount);
							double COMPLETE_BLOOD_absoluteEosinophilCountChange = nextCOMPLETE_BLOOD_absoluteEosinophilCountNumber - prevCOMPLETE_BLOOD_absoluteEosinophilCountNumber;
							if(Math.abs(COMPLETE_BLOOD_absoluteEosinophilCountChange) >= FeatureChangeSDU.COMPLETE_BLOOD_absoluteEosinophilCount){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_absoluteEosinophilCount);
								double COMPLETE_BLOOD_absoluteEosinophilCountChangePercent = (nextCOMPLETE_BLOOD_absoluteEosinophilCountNumber - prevCOMPLETE_BLOOD_absoluteEosinophilCountNumber) / prevCOMPLETE_BLOOD_absoluteEosinophilCountNumber;
								if(prevCOMPLETE_BLOOD_absoluteEosinophilCountNumber == 0)
									COMPLETE_BLOOD_absoluteEosinophilCountChangePercent =  nextCOMPLETE_BLOOD_absoluteEosinophilCountNumber;
								changeVals.add(COMPLETE_BLOOD_absoluteEosinophilCountChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_absoluteEosinophilCount)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_basophils = prevEvent.getLab().getCOMPLETE_BLOOD_basophils();
					String nextCOMPLETE_BLOOD_basophils = nextEvent.getLab().getCOMPLETE_BLOOD_basophils();
					double prevCOMPLETE_BLOOD_basophilsNumber = 0;
					double nextCOMPLETE_BLOOD_basophilsNumber = 0;
					if(!prevCOMPLETE_BLOOD_basophils.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_basophils.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_basophils.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_basophils.equalsIgnoreCase("")){
							
							if(prevCOMPLETE_BLOOD_basophils.equalsIgnoreCase("normal"))
								prevCOMPLETE_BLOOD_basophilsNumber = 0.37;
							else
								prevCOMPLETE_BLOOD_basophilsNumber = Double.parseDouble(prevCOMPLETE_BLOOD_basophils);
							
							if(nextCOMPLETE_BLOOD_basophils.equalsIgnoreCase("normal"))
								nextCOMPLETE_BLOOD_basophilsNumber = 0.37;
							else
								nextCOMPLETE_BLOOD_basophilsNumber = Double.parseDouble(nextCOMPLETE_BLOOD_basophils);
							double COMPLETE_BLOOD_basophilsChange = nextCOMPLETE_BLOOD_basophilsNumber - prevCOMPLETE_BLOOD_basophilsNumber;
							if(Math.abs(COMPLETE_BLOOD_basophilsChange) >= FeatureChangeSDU.COMPLETE_BLOOD_basophils){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_basophils);
								double COMPLETE_BLOOD_basophilsChangePercent = (nextCOMPLETE_BLOOD_basophilsNumber - prevCOMPLETE_BLOOD_basophilsNumber) / prevCOMPLETE_BLOOD_basophilsNumber;
								if(prevCOMPLETE_BLOOD_basophilsNumber == 0)
									COMPLETE_BLOOD_basophilsChangePercent =  nextCOMPLETE_BLOOD_basophilsNumber;
								changeVals.add(COMPLETE_BLOOD_basophilsChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_basophils)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_absoluteBasophilCount = prevEvent.getLab().getCOMPLETE_BLOOD_absoluteBasophilCount();
					String nextCOMPLETE_BLOOD_absoluteBasophilCount = nextEvent.getLab().getCOMPLETE_BLOOD_absoluteBasophilCount();
					double prevCOMPLETE_BLOOD_absoluteBasophilCountNumber = 0;
					double nextCOMPLETE_BLOOD_absoluteBasophilCountNumber = 0;
					if(!prevCOMPLETE_BLOOD_absoluteBasophilCount.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_absoluteBasophilCount.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_absoluteBasophilCount.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_absoluteBasophilCount.equalsIgnoreCase("")){
							
							if(prevCOMPLETE_BLOOD_absoluteBasophilCount.equalsIgnoreCase("normal"))
								prevCOMPLETE_BLOOD_absoluteBasophilCountNumber = 0.2;
							else if(prevCOMPLETE_BLOOD_absoluteBasophilCount.equalsIgnoreCase("trace"))
								prevCOMPLETE_BLOOD_absoluteBasophilCountNumber = 0.01;
							else
								prevCOMPLETE_BLOOD_absoluteBasophilCountNumber = Double.parseDouble(prevCOMPLETE_BLOOD_absoluteBasophilCount);
							
							if(nextCOMPLETE_BLOOD_absoluteBasophilCount.equalsIgnoreCase("normal"))
								nextCOMPLETE_BLOOD_absoluteBasophilCountNumber = 0.2;
							else if(nextCOMPLETE_BLOOD_absoluteBasophilCount.equalsIgnoreCase("trace"))
								nextCOMPLETE_BLOOD_absoluteBasophilCountNumber = 0.01;
							else
								nextCOMPLETE_BLOOD_absoluteBasophilCountNumber = Double.parseDouble(nextCOMPLETE_BLOOD_absoluteBasophilCount);
							double COMPLETE_BLOOD_absoluteBasophilCountChange = nextCOMPLETE_BLOOD_absoluteBasophilCountNumber - prevCOMPLETE_BLOOD_absoluteBasophilCountNumber;
							if(Math.abs(COMPLETE_BLOOD_absoluteBasophilCountChange) >= FeatureChangeSDU.COMPLETE_BLOOD_absoluteBasophilCount){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_absoluteBasophilCount);
								double COMPLETE_BLOOD_absoluteBasophilCountChangePercent = (nextCOMPLETE_BLOOD_absoluteBasophilCountNumber - prevCOMPLETE_BLOOD_absoluteBasophilCountNumber) / prevCOMPLETE_BLOOD_absoluteBasophilCountNumber;
								if(prevCOMPLETE_BLOOD_absoluteBasophilCountNumber == 0)
									COMPLETE_BLOOD_absoluteBasophilCountChangePercent =  nextCOMPLETE_BLOOD_absoluteBasophilCountNumber;
								changeVals.add(COMPLETE_BLOOD_absoluteBasophilCountChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_absoluteBasophilCount)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_redBloodCells = prevEvent.getLab().getCOMPLETE_BLOOD_redBloodCells();
					String nextCOMPLETE_BLOOD_redBloodCells = nextEvent.getLab().getCOMPLETE_BLOOD_redBloodCells();
					double prevCOMPLETE_BLOOD_redBloodCellsNumber = 0;
					double nextCOMPLETE_BLOOD_redBloodCellsNumber = 0;
					if(!prevCOMPLETE_BLOOD_redBloodCells.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_redBloodCells.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_redBloodCells.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_redBloodCells.equalsIgnoreCase("")){
							if(prevCOMPLETE_BLOOD_redBloodCells.equalsIgnoreCase("very large"))
								prevCOMPLETE_BLOOD_redBloodCellsNumber = 7000;
							else
								prevCOMPLETE_BLOOD_redBloodCellsNumber = Double.parseDouble(prevCOMPLETE_BLOOD_redBloodCells);
							
							if(nextCOMPLETE_BLOOD_redBloodCells.equalsIgnoreCase("very large"))
								nextCOMPLETE_BLOOD_redBloodCellsNumber = 7000;
							else
								nextCOMPLETE_BLOOD_redBloodCellsNumber = Double.parseDouble(nextCOMPLETE_BLOOD_redBloodCells);
							double COMPLETE_BLOOD_redBloodCellsChange = nextCOMPLETE_BLOOD_redBloodCellsNumber - prevCOMPLETE_BLOOD_redBloodCellsNumber;
							if(Math.abs(COMPLETE_BLOOD_redBloodCellsChange) >= FeatureChangeSDU.COMPLETE_BLOOD_redBloodCells){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_redBloodCells);
								double COMPLETE_BLOOD_redBloodCellsChangePercent = (nextCOMPLETE_BLOOD_redBloodCellsNumber - prevCOMPLETE_BLOOD_redBloodCellsNumber) / prevCOMPLETE_BLOOD_redBloodCellsNumber;
								if(prevCOMPLETE_BLOOD_redBloodCellsNumber == 0)
									COMPLETE_BLOOD_redBloodCellsChangePercent =  nextCOMPLETE_BLOOD_redBloodCellsNumber;
								changeVals.add(COMPLETE_BLOOD_redBloodCellsChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_redBloodCells)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_hemoglobin = prevEvent.getLab().getCOMPLETE_BLOOD_hemoglobin();
					String nextCOMPLETE_BLOOD_hemoglobin = nextEvent.getLab().getCOMPLETE_BLOOD_hemoglobin();
					double prevCOMPLETE_BLOOD_hemoglobinNumber = 0;
					double nextCOMPLETE_BLOOD_hemoglobinNumber = 0;
					if(!prevCOMPLETE_BLOOD_hemoglobin.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_hemoglobin.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_hemoglobin.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_hemoglobin.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_hemoglobinNumber = Double.parseDouble(prevCOMPLETE_BLOOD_hemoglobin);
							nextCOMPLETE_BLOOD_hemoglobinNumber = Double.parseDouble(nextCOMPLETE_BLOOD_hemoglobin);
							double COMPLETE_BLOOD_hemoglobinChange = nextCOMPLETE_BLOOD_hemoglobinNumber - prevCOMPLETE_BLOOD_hemoglobinNumber;
							if(Math.abs(COMPLETE_BLOOD_hemoglobinChange) >= FeatureChangeSDU.COMPLETE_BLOOD_hemoglobin){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_hemoglobin);
								double COMPLETE_BLOOD_hemoglobinChangePercent = (nextCOMPLETE_BLOOD_hemoglobinNumber - prevCOMPLETE_BLOOD_hemoglobinNumber) / prevCOMPLETE_BLOOD_hemoglobinNumber;
								if(prevCOMPLETE_BLOOD_hemoglobinNumber == 0)
									COMPLETE_BLOOD_hemoglobinChangePercent =  nextCOMPLETE_BLOOD_hemoglobinNumber;
								changeVals.add(COMPLETE_BLOOD_hemoglobinChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_hemoglobin)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_hematocrit = prevEvent.getLab().getCOMPLETE_BLOOD_hematocrit();
					String nextCOMPLETE_BLOOD_hematocrit = nextEvent.getLab().getCOMPLETE_BLOOD_hematocrit();
					double prevCOMPLETE_BLOOD_hematocritNumber = 0;
					double nextCOMPLETE_BLOOD_hematocritNumber = 0;
					if(!prevCOMPLETE_BLOOD_hematocrit.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_hematocrit.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_hematocrit.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_hematocrit.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_hematocritNumber = Double.parseDouble(prevCOMPLETE_BLOOD_hematocrit);
							nextCOMPLETE_BLOOD_hematocritNumber = Double.parseDouble(nextCOMPLETE_BLOOD_hematocrit);
							double COMPLETE_BLOOD_hematocritChange = nextCOMPLETE_BLOOD_hematocritNumber - prevCOMPLETE_BLOOD_hematocritNumber;
							if(Math.abs(COMPLETE_BLOOD_hematocritChange) >= FeatureChangeSDU.COMPLETE_BLOOD_hematocrit){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_hematocrit);
								double COMPLETE_BLOOD_hematocritChangePercent = (nextCOMPLETE_BLOOD_hematocritNumber - prevCOMPLETE_BLOOD_hematocritNumber) / prevCOMPLETE_BLOOD_hematocritNumber;
								if(prevCOMPLETE_BLOOD_hematocritNumber == 0)
									COMPLETE_BLOOD_hematocritChangePercent =  nextCOMPLETE_BLOOD_hematocritNumber;
								changeVals.add(COMPLETE_BLOOD_hematocritChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_hematocrit)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_meanCorpuscularHemoglobin = prevEvent.getLab().getCOMPLETE_BLOOD_meanCorpuscularHemoglobin();
					String nextCOMPLETE_BLOOD_meanCorpuscularHemoglobin = nextEvent.getLab().getCOMPLETE_BLOOD_meanCorpuscularHemoglobin();
					double prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinNumber = 0;
					double nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinNumber = 0;
					if(!prevCOMPLETE_BLOOD_meanCorpuscularHemoglobin.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_meanCorpuscularHemoglobin.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_meanCorpuscularHemoglobin.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_meanCorpuscularHemoglobin.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinNumber = Double.parseDouble(prevCOMPLETE_BLOOD_meanCorpuscularHemoglobin);
							nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinNumber = Double.parseDouble(nextCOMPLETE_BLOOD_meanCorpuscularHemoglobin);
							double COMPLETE_BLOOD_meanCorpuscularHemoglobinChange = nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinNumber - prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinNumber;
							if(Math.abs(COMPLETE_BLOOD_meanCorpuscularHemoglobinChange) >= FeatureChangeSDU.COMPLETE_BLOOD_meanCorpuscularHemoglobin){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_meanCorpuscularHemoglobin);
								double COMPLETE_BLOOD_meanCorpuscularHemoglobinChangePercent = (nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinNumber - prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinNumber) / prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinNumber;
								if(prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinNumber == 0)
									COMPLETE_BLOOD_meanCorpuscularHemoglobinChangePercent =  nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinNumber;
								changeVals.add(COMPLETE_BLOOD_meanCorpuscularHemoglobinChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_meanCorpuscularHemoglobin)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration = prevEvent.getLab().getCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration();
					String nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration = nextEvent.getLab().getCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration();
					double prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber = 0;
					double nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber = 0;
					if(!prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber = Double.parseDouble(prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration);
							nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber = Double.parseDouble(nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration);
							if(prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber > 100)
								prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber = prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber / 10;
							if(nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber > 100)
								nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber = nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber / 10;
							double COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationChange = nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber - prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber;
							if(Math.abs(COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationChange) >= FeatureChangeSDU.COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration);
								double COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationChangePercent = (nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber - prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber) / prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber;
								if(prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber == 0)
									COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationChangePercent =  nextCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationNumber;
								changeVals.add(COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentrationChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_meanCorpuscularVolume = prevEvent.getLab().getCOMPLETE_BLOOD_meanCorpuscularVolume();
					String nextCOMPLETE_BLOOD_meanCorpuscularVolume = nextEvent.getLab().getCOMPLETE_BLOOD_meanCorpuscularVolume();
					double prevCOMPLETE_BLOOD_meanCorpuscularVolumeNumber = 0;
					double nextCOMPLETE_BLOOD_meanCorpuscularVolumeNumber = 0;
					if(!prevCOMPLETE_BLOOD_meanCorpuscularVolume.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_meanCorpuscularVolume.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_meanCorpuscularVolume.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_meanCorpuscularVolume.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_meanCorpuscularVolumeNumber = Double.parseDouble(prevCOMPLETE_BLOOD_meanCorpuscularVolume);
							nextCOMPLETE_BLOOD_meanCorpuscularVolumeNumber = Double.parseDouble(nextCOMPLETE_BLOOD_meanCorpuscularVolume);
							
							double COMPLETE_BLOOD_meanCorpuscularVolumeChange = nextCOMPLETE_BLOOD_meanCorpuscularVolumeNumber - prevCOMPLETE_BLOOD_meanCorpuscularVolumeNumber;
							if(Math.abs(COMPLETE_BLOOD_meanCorpuscularVolumeChange) >= FeatureChangeSDU.COMPLETE_BLOOD_meanCorpuscularVolume){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_meanCorpuscularVolume);
								double COMPLETE_BLOOD_meanCorpuscularVolumeChangePercent = (nextCOMPLETE_BLOOD_meanCorpuscularVolumeNumber - prevCOMPLETE_BLOOD_meanCorpuscularVolumeNumber) / prevCOMPLETE_BLOOD_meanCorpuscularVolumeNumber;
								if(prevCOMPLETE_BLOOD_meanCorpuscularVolumeNumber == 0)
									COMPLETE_BLOOD_meanCorpuscularVolumeChangePercent =  nextCOMPLETE_BLOOD_meanCorpuscularVolumeNumber;
								changeVals.add(COMPLETE_BLOOD_meanCorpuscularVolumeChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_meanCorpuscularVolume)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOMPLETE_BLOOD_platelets = prevEvent.getLab().getCOMPLETE_BLOOD_platelets();
					String nextCOMPLETE_BLOOD_platelets = nextEvent.getLab().getCOMPLETE_BLOOD_platelets();
					double prevCOMPLETE_BLOOD_plateletsNumber = 0;
					double nextCOMPLETE_BLOOD_plateletsNumber = 0;
					if(!prevCOMPLETE_BLOOD_platelets.equalsIgnoreCase("-") && !nextCOMPLETE_BLOOD_platelets.equalsIgnoreCase("-")
							&& !prevCOMPLETE_BLOOD_platelets.equalsIgnoreCase("") && !nextCOMPLETE_BLOOD_platelets.equalsIgnoreCase("")){
							prevCOMPLETE_BLOOD_plateletsNumber = Double.parseDouble(prevCOMPLETE_BLOOD_platelets);
							nextCOMPLETE_BLOOD_plateletsNumber = Double.parseDouble(nextCOMPLETE_BLOOD_platelets);
							
							double COMPLETE_BLOOD_plateletsChange = nextCOMPLETE_BLOOD_plateletsNumber - prevCOMPLETE_BLOOD_plateletsNumber;
							if(Math.abs(COMPLETE_BLOOD_plateletsChange) >= FeatureChangeSDU.COMPLETE_BLOOD_platelets){
								features.add(FeatureNumberSDU.COMPLETE_BLOOD_platelets);
								double COMPLETE_BLOOD_plateletsChangePercent = (nextCOMPLETE_BLOOD_plateletsNumber - prevCOMPLETE_BLOOD_plateletsNumber) / prevCOMPLETE_BLOOD_plateletsNumber;
								if(prevCOMPLETE_BLOOD_plateletsNumber == 0)
									COMPLETE_BLOOD_plateletsChangePercent =  nextCOMPLETE_BLOOD_plateletsNumber;
								changeVals.add(COMPLETE_BLOOD_plateletsChangePercent);
						}//if(Math.abs)
					}//if(!prevCOMPLETE_BLOOD_platelets)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevHEART_DISEASE_ck = prevEvent.getLab().getHEART_DISEASE_ck();
					String nextHEART_DISEASE_ck = nextEvent.getLab().getHEART_DISEASE_ck();
					double prevHEART_DISEASE_ckNumber = 0;
					double nextHEART_DISEASE_ckNumber = 0;
					if(!prevHEART_DISEASE_ck.equalsIgnoreCase("-") && !nextHEART_DISEASE_ck.equalsIgnoreCase("-")
							&& !prevHEART_DISEASE_ck.equalsIgnoreCase("") && !nextHEART_DISEASE_ck.equalsIgnoreCase("")){
							if(prevHEART_DISEASE_ck.equalsIgnoreCase("normal"))
								prevHEART_DISEASE_ckNumber = 100;
							else
								prevHEART_DISEASE_ckNumber = Double.parseDouble(prevHEART_DISEASE_ck);
							
							if(nextHEART_DISEASE_ck.equalsIgnoreCase("normal"))
								nextHEART_DISEASE_ckNumber = 100;
							else
								nextHEART_DISEASE_ckNumber = Double.parseDouble(nextHEART_DISEASE_ck);
							
							double HEART_DISEASE_ckChange = nextHEART_DISEASE_ckNumber - prevHEART_DISEASE_ckNumber;
							if(Math.abs(HEART_DISEASE_ckChange) >= FeatureChangeSDU.HEART_DISEASE_ck){
								features.add(FeatureNumberSDU.HEART_DISEASE_ck);
								double HEART_DISEASE_ckChangePercent = (nextHEART_DISEASE_ckNumber - prevHEART_DISEASE_ckNumber) / prevHEART_DISEASE_ckNumber;
								if(prevHEART_DISEASE_ckNumber == 0)
									HEART_DISEASE_ckChangePercent =  nextHEART_DISEASE_ckNumber;
								changeVals.add(HEART_DISEASE_ckChangePercent);
						}//if(Math.abs)
					}//if(!prevHEART_DISEASE_ck)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevHEART_DISEASE_triglycerides = prevEvent.getLab().getHEART_DISEASE_triglycerides();
					String nextHEART_DISEASE_triglycerides = nextEvent.getLab().getHEART_DISEASE_triglycerides();
					double prevHEART_DISEASE_triglyceridesNumber = 0;
					double nextHEART_DISEASE_triglyceridesNumber = 0;
					if(!prevHEART_DISEASE_triglycerides.equalsIgnoreCase("-") && !nextHEART_DISEASE_triglycerides.equalsIgnoreCase("-")
							&& !prevHEART_DISEASE_triglycerides.equalsIgnoreCase("") && !nextHEART_DISEASE_triglycerides.equalsIgnoreCase("")){
							prevHEART_DISEASE_triglyceridesNumber = Double.parseDouble(prevHEART_DISEASE_triglycerides);
							nextHEART_DISEASE_triglyceridesNumber = Double.parseDouble(nextHEART_DISEASE_triglycerides);
							
							double HEART_DISEASE_triglyceridesChange = nextHEART_DISEASE_triglyceridesNumber - prevHEART_DISEASE_triglyceridesNumber;
							if(Math.abs(HEART_DISEASE_triglyceridesChange) >= FeatureChangeSDU.HEART_DISEASE_triglycerides){
								features.add(FeatureNumberSDU.HEART_DISEASE_triglycerides);
								double HEART_DISEASE_triglyceridesChangePercent = (nextHEART_DISEASE_triglyceridesNumber - prevHEART_DISEASE_triglyceridesNumber) / prevHEART_DISEASE_triglyceridesNumber;
								if(prevHEART_DISEASE_triglyceridesNumber == 0)
									HEART_DISEASE_triglyceridesChangePercent =  nextHEART_DISEASE_triglyceridesNumber;
								changeVals.add(HEART_DISEASE_triglyceridesChangePercent);
						}//if(Math.abs)
					}//if(!prevHEART_DISEASE_triglycerides)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevHEART_DISEASE_totalCholesterol = prevEvent.getLab().getHEART_DISEASE_totalCholesterol();
					String nextHEART_DISEASE_totalCholesterol = nextEvent.getLab().getHEART_DISEASE_totalCholesterol();
					double prevHEART_DISEASE_totalCholesterolNumber = 0;
					double nextHEART_DISEASE_totalCholesterolNumber = 0;
					if(!prevHEART_DISEASE_totalCholesterol.equalsIgnoreCase("-") && !nextHEART_DISEASE_totalCholesterol.equalsIgnoreCase("-")
							&& !prevHEART_DISEASE_totalCholesterol.equalsIgnoreCase("") && !nextHEART_DISEASE_totalCholesterol.equalsIgnoreCase("")){
							prevHEART_DISEASE_totalCholesterolNumber = Double.parseDouble(prevHEART_DISEASE_totalCholesterol);
							nextHEART_DISEASE_totalCholesterolNumber = Double.parseDouble(nextHEART_DISEASE_totalCholesterol);
							
							double HEART_DISEASE_totalCholesterolChange = nextHEART_DISEASE_totalCholesterolNumber - prevHEART_DISEASE_totalCholesterolNumber;
							if(Math.abs(HEART_DISEASE_totalCholesterolChange) >= FeatureChangeSDU.HEART_DISEASE_totalCholesterol){
								features.add(FeatureNumberSDU.HEART_DISEASE_totalCholesterol);
								double HEART_DISEASE_totalCholesterolChangePercent = (nextHEART_DISEASE_totalCholesterolNumber - prevHEART_DISEASE_totalCholesterolNumber) / prevHEART_DISEASE_totalCholesterolNumber;
								if(prevHEART_DISEASE_totalCholesterolNumber == 0)
									HEART_DISEASE_totalCholesterolChangePercent =  nextHEART_DISEASE_totalCholesterolNumber;
								changeVals.add(HEART_DISEASE_totalCholesterolChangePercent);
						}//if(Math.abs)
					}//if(!prevHEART_DISEASE_totalCholesterol)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevHEART_DISEASE_lactateDehydrogenase = prevEvent.getLab().getHEART_DISEASE_lactateDehydrogenase();
					String nextHEART_DISEASE_lactateDehydrogenase = nextEvent.getLab().getHEART_DISEASE_lactateDehydrogenase();
					double prevHEART_DISEASE_lactateDehydrogenaseNumber = 0;
					double nextHEART_DISEASE_lactateDehydrogenaseNumber = 0;
					if(!prevHEART_DISEASE_lactateDehydrogenase.equalsIgnoreCase("-") && !nextHEART_DISEASE_lactateDehydrogenase.equalsIgnoreCase("-")
							&& !prevHEART_DISEASE_lactateDehydrogenase.equalsIgnoreCase("") && !nextHEART_DISEASE_lactateDehydrogenase.equalsIgnoreCase("")){
							prevHEART_DISEASE_lactateDehydrogenaseNumber = Double.parseDouble(prevHEART_DISEASE_lactateDehydrogenase);
							nextHEART_DISEASE_lactateDehydrogenaseNumber = Double.parseDouble(nextHEART_DISEASE_lactateDehydrogenase);
							
							double HEART_DISEASE_lactateDehydrogenaseChange = nextHEART_DISEASE_lactateDehydrogenaseNumber - prevHEART_DISEASE_lactateDehydrogenaseNumber;
							if(Math.abs(HEART_DISEASE_lactateDehydrogenaseChange) >= FeatureChangeSDU.HEART_DISEASE_lactateDehydrogenase){
								features.add(FeatureNumberSDU.HEART_DISEASE_lactateDehydrogenase);
								double HEART_DISEASE_lactateDehydrogenaseChangePercent = (nextHEART_DISEASE_lactateDehydrogenaseNumber - prevHEART_DISEASE_lactateDehydrogenaseNumber) / prevHEART_DISEASE_lactateDehydrogenaseNumber;
								if(prevHEART_DISEASE_lactateDehydrogenaseNumber == 0)
									HEART_DISEASE_lactateDehydrogenaseChangePercent =  nextHEART_DISEASE_lactateDehydrogenaseNumber;
								changeVals.add(HEART_DISEASE_lactateDehydrogenaseChangePercent);
						}//if(Math.abs)
					}//if(!prevHEART_DISEASE_lactateDehydrogenase)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevBLOOD_SUGAR_glucose = prevEvent.getLab().getBLOOD_SUGAR_glucose();
					String nextBLOOD_SUGAR_glucose = nextEvent.getLab().getBLOOD_SUGAR_glucose();
					double prevBLOOD_SUGAR_glucoseNumber = 0;
					double nextBLOOD_SUGAR_glucoseNumber = 0;
					boolean prevBLOOD_SUGAR_glucoseSelect = false;
					boolean nextBLOOD_SUGAR_glucoseSelect = false;
					if(!prevBLOOD_SUGAR_glucose.equalsIgnoreCase("-") && !nextBLOOD_SUGAR_glucose.equalsIgnoreCase("-")
							&& !prevBLOOD_SUGAR_glucose.equalsIgnoreCase("") && !nextBLOOD_SUGAR_glucose.equalsIgnoreCase("")){
						if(prevBLOOD_SUGAR_glucose.equalsIgnoreCase("trace")){
							prevBLOOD_SUGAR_glucoseNumber = 1;
							prevBLOOD_SUGAR_glucoseSelect = true;
						}//if
						if(nextBLOOD_SUGAR_glucose.equalsIgnoreCase("trace")){
							nextBLOOD_SUGAR_glucoseNumber = 1;
							nextBLOOD_SUGAR_glucoseSelect = true;
						}//if
						
						if(prevBLOOD_SUGAR_glucose.equalsIgnoreCase("small")){
							prevBLOOD_SUGAR_glucoseNumber = 2;
							prevBLOOD_SUGAR_glucoseSelect = true;
						}//if
						if(nextBLOOD_SUGAR_glucose.equalsIgnoreCase("small")){
							nextBLOOD_SUGAR_glucoseNumber = 2;
							nextBLOOD_SUGAR_glucoseSelect = true;
						}//if
						
						if(prevBLOOD_SUGAR_glucose.equalsIgnoreCase("moderate") || prevBLOOD_SUGAR_glucose.equalsIgnoreCase("normal")){
							prevBLOOD_SUGAR_glucoseNumber = 5;
							prevBLOOD_SUGAR_glucoseSelect = true;
						}//if
						
						if(nextBLOOD_SUGAR_glucose.equalsIgnoreCase("moderate") || nextBLOOD_SUGAR_glucose.equalsIgnoreCase("normal")){
							nextBLOOD_SUGAR_glucoseNumber = 5;
							nextBLOOD_SUGAR_glucoseSelect = true;
						}//if
						
						
						if(prevBLOOD_SUGAR_glucose.equalsIgnoreCase("large")){
							prevBLOOD_SUGAR_glucoseNumber = 30;
							prevBLOOD_SUGAR_glucoseSelect = true;
						}//if
						if(nextBLOOD_SUGAR_glucose.equalsIgnoreCase("large")){
							nextBLOOD_SUGAR_glucoseNumber = 30;
							nextBLOOD_SUGAR_glucoseSelect = true;
						}//if
												
						if(!prevBLOOD_SUGAR_glucoseSelect)
							prevBLOOD_SUGAR_glucoseNumber = Double.parseDouble(prevBLOOD_SUGAR_glucose);
						if(!nextBLOOD_SUGAR_glucoseSelect)
							nextBLOOD_SUGAR_glucoseNumber = Double.parseDouble(nextBLOOD_SUGAR_glucose);
						double BLOOD_SUGAR_glucoseChange = nextBLOOD_SUGAR_glucoseNumber - prevBLOOD_SUGAR_glucoseNumber;
						if(Math.abs(BLOOD_SUGAR_glucoseChange) >= FeatureChangeSDU.BLOOD_SUGAR_glucose){
							features.add(FeatureNumberSDU.BLOOD_SUGAR_glucose);
							double BLOOD_SUGAR_glucoseChangePercent = (nextBLOOD_SUGAR_glucoseNumber - prevBLOOD_SUGAR_glucoseNumber) / prevBLOOD_SUGAR_glucoseNumber;
							if(prevBLOOD_SUGAR_glucoseNumber == 0)
								BLOOD_SUGAR_glucoseChangePercent =  nextBLOOD_SUGAR_glucoseNumber;
							changeVals.add(BLOOD_SUGAR_glucoseChangePercent);
						}//if(Math.abs)
					}//if(!prevBLOOD_SUGAR_glucose)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevBLOOD_SUGAR_HbA1c = prevEvent.getLab().getBLOOD_SUGAR_HbA1c();
					String nextBLOOD_SUGAR_HbA1c = nextEvent.getLab().getBLOOD_SUGAR_HbA1c();
					double prevBLOOD_SUGAR_HbA1cNumber = 0;
					double nextBLOOD_SUGAR_HbA1cNumber = 0;
					if(!prevBLOOD_SUGAR_HbA1c.equalsIgnoreCase("-") && !nextBLOOD_SUGAR_HbA1c.equalsIgnoreCase("-")
							&& !prevBLOOD_SUGAR_HbA1c.equalsIgnoreCase("") && !nextBLOOD_SUGAR_HbA1c.equalsIgnoreCase("")){
							if(prevBLOOD_SUGAR_HbA1c.equalsIgnoreCase("normal"))
								prevBLOOD_SUGAR_HbA1cNumber = 6;
							else
								prevBLOOD_SUGAR_HbA1cNumber = Double.parseDouble(prevBLOOD_SUGAR_HbA1c);
							
							if(nextBLOOD_SUGAR_HbA1c.equalsIgnoreCase("normal"))
								nextBLOOD_SUGAR_HbA1cNumber = 6;
							else
								nextBLOOD_SUGAR_HbA1cNumber = Double.parseDouble(nextBLOOD_SUGAR_HbA1c);
							
							double BLOOD_SUGAR_HbA1cChange = nextBLOOD_SUGAR_HbA1cNumber - prevBLOOD_SUGAR_HbA1cNumber;
							if(Math.abs(BLOOD_SUGAR_HbA1cChange) >= FeatureChangeSDU.BLOOD_SUGAR_HbA1c){
								features.add(FeatureNumberSDU.BLOOD_SUGAR_HbA1c);
								double BLOOD_SUGAR_HbA1cChangePercent = (nextBLOOD_SUGAR_HbA1cNumber - prevBLOOD_SUGAR_HbA1cNumber) / prevBLOOD_SUGAR_HbA1cNumber;
								if(prevBLOOD_SUGAR_HbA1cNumber == 0)
									BLOOD_SUGAR_HbA1cChangePercent =  nextBLOOD_SUGAR_HbA1cNumber;
								changeVals.add(BLOOD_SUGAR_HbA1cChangePercent);
						}//if(Math.abs)
					}//if(!prevBLOOD_SUGAR_HbA1c)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevMINERAL_BALANCE_calcium = prevEvent.getLab().getMINERAL_BALANCE_calcium();
					String nextMINERAL_BALANCE_calcium = nextEvent.getLab().getMINERAL_BALANCE_calcium();
					double prevMINERAL_BALANCE_calciumNumber = 0;
					double nextMINERAL_BALANCE_calciumNumber = 0;
					if(!prevMINERAL_BALANCE_calcium.equalsIgnoreCase("-") && !nextMINERAL_BALANCE_calcium.equalsIgnoreCase("-")
							&& !prevMINERAL_BALANCE_calcium.equalsIgnoreCase("") && !nextMINERAL_BALANCE_calcium.equalsIgnoreCase("")){
							prevMINERAL_BALANCE_calciumNumber = Double.parseDouble(prevMINERAL_BALANCE_calcium);
							nextMINERAL_BALANCE_calciumNumber = Double.parseDouble(nextMINERAL_BALANCE_calcium);
							
							double MINERAL_BALANCE_calciumChange = nextMINERAL_BALANCE_calciumNumber - prevMINERAL_BALANCE_calciumNumber;
							if(Math.abs(MINERAL_BALANCE_calciumChange) >= FeatureChangeSDU.MINERAL_BALANCE_calcium){
								features.add(FeatureNumberSDU.MINERAL_BALANCE_calcium);
								double MINERAL_BALANCE_calciumChangePercent = (nextMINERAL_BALANCE_calciumNumber - prevMINERAL_BALANCE_calciumNumber) / prevMINERAL_BALANCE_calciumNumber;
								if(prevMINERAL_BALANCE_calciumNumber == 0)
									MINERAL_BALANCE_calciumChangePercent =  nextMINERAL_BALANCE_calciumNumber;
								changeVals.add(MINERAL_BALANCE_calciumChangePercent);
						}//if(Math.abs)
					}//if(!prevMINERAL_BALANCE_calcium)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevMINERAL_BALANCE_phosphorus = prevEvent.getLab().getMINERAL_BALANCE_phosphorus();
					String nextMINERAL_BALANCE_phosphorus = nextEvent.getLab().getMINERAL_BALANCE_phosphorus();
					double prevMINERAL_BALANCE_phosphorusNumber = 0;
					double nextMINERAL_BALANCE_phosphorusNumber = 0;
					if(!prevMINERAL_BALANCE_phosphorus.equalsIgnoreCase("-") && !nextMINERAL_BALANCE_phosphorus.equalsIgnoreCase("-")
							&& !prevMINERAL_BALANCE_phosphorus.equalsIgnoreCase("") && !nextMINERAL_BALANCE_phosphorus.equalsIgnoreCase("")){
							prevMINERAL_BALANCE_phosphorusNumber = Double.parseDouble(prevMINERAL_BALANCE_phosphorus);
							nextMINERAL_BALANCE_phosphorusNumber = Double.parseDouble(nextMINERAL_BALANCE_phosphorus);
							
							double MINERAL_BALANCE_phosphorusChange = nextMINERAL_BALANCE_phosphorusNumber - prevMINERAL_BALANCE_phosphorusNumber;
							if(Math.abs(MINERAL_BALANCE_phosphorusChange) >= FeatureChangeSDU.MINERAL_BALANCE_phosphorus){
								features.add(FeatureNumberSDU.MINERAL_BALANCE_phosphorus);
								double MINERAL_BALANCE_phosphorusChangePercent = (nextMINERAL_BALANCE_phosphorusNumber - prevMINERAL_BALANCE_phosphorusNumber) / prevMINERAL_BALANCE_phosphorusNumber;
								if(prevMINERAL_BALANCE_phosphorusNumber == 0)
									MINERAL_BALANCE_phosphorusChangePercent =  nextMINERAL_BALANCE_phosphorusNumber;
								changeVals.add(MINERAL_BALANCE_phosphorusChangePercent);
						}//if(Math.abs)
					}//if(!prevMINERAL_BALANCE_phosphorus)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevIMMUNE_RESPONSE_immunoglobulinA = prevEvent.getLab().getIMMUNE_RESPONSE_immunoglobulinA();
					String nextIMMUNE_RESPONSE_immunoglobulinA = nextEvent.getLab().getIMMUNE_RESPONSE_immunoglobulinA();
					double prevIMMUNE_RESPONSE_immunoglobulinANumber = 0;
					double nextIMMUNE_RESPONSE_immunoglobulinANumber = 0;
					if(!prevIMMUNE_RESPONSE_immunoglobulinA.equalsIgnoreCase("-") && !nextIMMUNE_RESPONSE_immunoglobulinA.equalsIgnoreCase("-")
							&& !prevIMMUNE_RESPONSE_immunoglobulinA.equalsIgnoreCase("") && !nextIMMUNE_RESPONSE_immunoglobulinA.equalsIgnoreCase("")){
							prevIMMUNE_RESPONSE_immunoglobulinANumber = Double.parseDouble(prevIMMUNE_RESPONSE_immunoglobulinA);
							nextIMMUNE_RESPONSE_immunoglobulinANumber = Double.parseDouble(nextIMMUNE_RESPONSE_immunoglobulinA);
							
							double IMMUNE_RESPONSE_immunoglobulinAChange = nextIMMUNE_RESPONSE_immunoglobulinANumber - prevIMMUNE_RESPONSE_immunoglobulinANumber;
							if(Math.abs(IMMUNE_RESPONSE_immunoglobulinAChange) >= FeatureChangeSDU.IMMUNE_RESPONSE_immunoglobulinA){
								features.add(FeatureNumberSDU.IMMUNE_RESPONSE_immunoglobulinA);
								double IMMUNE_RESPONSE_immunoglobulinAChangePercent = (nextIMMUNE_RESPONSE_immunoglobulinANumber - prevIMMUNE_RESPONSE_immunoglobulinANumber) / prevIMMUNE_RESPONSE_immunoglobulinANumber;
								if(prevIMMUNE_RESPONSE_immunoglobulinANumber == 0)
									IMMUNE_RESPONSE_immunoglobulinAChangePercent =  nextIMMUNE_RESPONSE_immunoglobulinANumber;
								changeVals.add(IMMUNE_RESPONSE_immunoglobulinAChangePercent);
						}//if(Math.abs)
					}//if(!prevIMMUNE_RESPONSE_immunoglobulinA)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevIMMUNE_RESPONSE_immunoglobulinG = prevEvent.getLab().getIMMUNE_RESPONSE_immunoglobulinG();
					String nextIMMUNE_RESPONSE_immunoglobulinG = nextEvent.getLab().getIMMUNE_RESPONSE_immunoglobulinG();
					double prevIMMUNE_RESPONSE_immunoglobulinGNumber = 0;
					double nextIMMUNE_RESPONSE_immunoglobulinGNumber = 0;
					if(!prevIMMUNE_RESPONSE_immunoglobulinG.equalsIgnoreCase("-") && !nextIMMUNE_RESPONSE_immunoglobulinG.equalsIgnoreCase("-")
							&& !prevIMMUNE_RESPONSE_immunoglobulinG.equalsIgnoreCase("") && !nextIMMUNE_RESPONSE_immunoglobulinG.equalsIgnoreCase("")){
							prevIMMUNE_RESPONSE_immunoglobulinGNumber = Double.parseDouble(prevIMMUNE_RESPONSE_immunoglobulinG);
							nextIMMUNE_RESPONSE_immunoglobulinGNumber = Double.parseDouble(nextIMMUNE_RESPONSE_immunoglobulinG);
							
							double IMMUNE_RESPONSE_immunoglobulinGChange = nextIMMUNE_RESPONSE_immunoglobulinGNumber - prevIMMUNE_RESPONSE_immunoglobulinGNumber;
							if(Math.abs(IMMUNE_RESPONSE_immunoglobulinGChange) >= FeatureChangeSDU.IMMUNE_RESPONSE_immunoglobulinG){
								features.add(FeatureNumberSDU.IMMUNE_RESPONSE_immunoglobulinG);
								double IMMUNE_RESPONSE_immunoglobulinGChangePercent = (nextIMMUNE_RESPONSE_immunoglobulinGNumber - prevIMMUNE_RESPONSE_immunoglobulinGNumber) / prevIMMUNE_RESPONSE_immunoglobulinGNumber;
								if(prevIMMUNE_RESPONSE_immunoglobulinGNumber == 0)
									IMMUNE_RESPONSE_immunoglobulinGChangePercent =  nextIMMUNE_RESPONSE_immunoglobulinGNumber;
								changeVals.add(IMMUNE_RESPONSE_immunoglobulinGChangePercent);
						}//if(Math.abs)
					}//if(!prevIMMUNE_RESPONSE_immunoglobulinG)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevIMMUNE_RESPONSE_immunoglobulinM = prevEvent.getLab().getIMMUNE_RESPONSE_immunoglobulinM();
					String nextIMMUNE_RESPONSE_immunoglobulinM = nextEvent.getLab().getIMMUNE_RESPONSE_immunoglobulinM();
					double prevIMMUNE_RESPONSE_immunoglobulinMNumber = 0;
					double nextIMMUNE_RESPONSE_immunoglobulinMNumber = 0;
					if(!prevIMMUNE_RESPONSE_immunoglobulinM.equalsIgnoreCase("-") && !nextIMMUNE_RESPONSE_immunoglobulinM.equalsIgnoreCase("-")
							&& !prevIMMUNE_RESPONSE_immunoglobulinM.equalsIgnoreCase("") && !nextIMMUNE_RESPONSE_immunoglobulinM.equalsIgnoreCase("")){
							prevIMMUNE_RESPONSE_immunoglobulinMNumber = Double.parseDouble(prevIMMUNE_RESPONSE_immunoglobulinM);
							nextIMMUNE_RESPONSE_immunoglobulinMNumber = Double.parseDouble(nextIMMUNE_RESPONSE_immunoglobulinM);
							
							double IMMUNE_RESPONSE_immunoglobulinMChange = nextIMMUNE_RESPONSE_immunoglobulinMNumber - prevIMMUNE_RESPONSE_immunoglobulinMNumber;
							if(Math.abs(IMMUNE_RESPONSE_immunoglobulinMChange) >= FeatureChangeSDU.IMMUNE_RESPONSE_immunoglobulinM){
								features.add(FeatureNumberSDU.IMMUNE_RESPONSE_immunoglobulinM);
								double IMMUNE_RESPONSE_immunoglobulinMChangePercent = (nextIMMUNE_RESPONSE_immunoglobulinMNumber - prevIMMUNE_RESPONSE_immunoglobulinMNumber) / prevIMMUNE_RESPONSE_immunoglobulinMNumber;
								if(prevIMMUNE_RESPONSE_immunoglobulinMNumber == 0)
									IMMUNE_RESPONSE_immunoglobulinMChangePercent =  nextIMMUNE_RESPONSE_immunoglobulinMNumber;
								changeVals.add(IMMUNE_RESPONSE_immunoglobulinMChangePercent);
						}//if(Math.abs)
					}//if(!prevIMMUNE_RESPONSE_immunoglobulinM)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevIMMUNE_RESPONSE_gammaGlobulin = prevEvent.getLab().getIMMUNE_RESPONSE_gammaGlobulin();
					String nextIMMUNE_RESPONSE_gammaGlobulin = nextEvent.getLab().getIMMUNE_RESPONSE_gammaGlobulin();
					double prevIMMUNE_RESPONSE_gammaGlobulinNumber = 0;
					double nextIMMUNE_RESPONSE_gammaGlobulinNumber = 0;
					if(!prevIMMUNE_RESPONSE_gammaGlobulin.equalsIgnoreCase("-") && !nextIMMUNE_RESPONSE_gammaGlobulin.equalsIgnoreCase("-")
							&& !prevIMMUNE_RESPONSE_gammaGlobulin.equalsIgnoreCase("") && !nextIMMUNE_RESPONSE_gammaGlobulin.equalsIgnoreCase("")){
							prevIMMUNE_RESPONSE_gammaGlobulinNumber = Double.parseDouble(prevIMMUNE_RESPONSE_gammaGlobulin);
							nextIMMUNE_RESPONSE_gammaGlobulinNumber = Double.parseDouble(nextIMMUNE_RESPONSE_gammaGlobulin);
							
							double IMMUNE_RESPONSE_gammaGlobulinChange = nextIMMUNE_RESPONSE_gammaGlobulinNumber - prevIMMUNE_RESPONSE_gammaGlobulinNumber;
							if(Math.abs(IMMUNE_RESPONSE_gammaGlobulinChange) >= FeatureChangeSDU.IMMUNE_RESPONSE_gammaGlobulin){
								features.add(FeatureNumberSDU.IMMUNE_RESPONSE_gammaGlobulin);
								double IMMUNE_RESPONSE_gammaGlobulinChangePercent = (nextIMMUNE_RESPONSE_gammaGlobulinNumber - prevIMMUNE_RESPONSE_gammaGlobulinNumber) / prevIMMUNE_RESPONSE_gammaGlobulinNumber;
								if(prevIMMUNE_RESPONSE_gammaGlobulinNumber == 0)
									IMMUNE_RESPONSE_gammaGlobulinChangePercent =  nextIMMUNE_RESPONSE_gammaGlobulinNumber;
								changeVals.add(IMMUNE_RESPONSE_gammaGlobulinChangePercent);
						}//if(Math.abs)
					}//if(!prevIMMUNE_RESPONSE_gammaGlobulin)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevIMMUNE_RESPONSE_alpha1Globulin = prevEvent.getLab().getIMMUNE_RESPONSE_alpha1Globulin();
					String nextIMMUNE_RESPONSE_alpha1Globulin = nextEvent.getLab().getIMMUNE_RESPONSE_alpha1Globulin();
					double prevIMMUNE_RESPONSE_alpha1GlobulinNumber = 0;
					double nextIMMUNE_RESPONSE_alpha1GlobulinNumber = 0;
					if(!prevIMMUNE_RESPONSE_alpha1Globulin.equalsIgnoreCase("-") && !nextIMMUNE_RESPONSE_alpha1Globulin.equalsIgnoreCase("-")
							&& !prevIMMUNE_RESPONSE_alpha1Globulin.equalsIgnoreCase("") && !nextIMMUNE_RESPONSE_alpha1Globulin.equalsIgnoreCase("")){
							prevIMMUNE_RESPONSE_alpha1GlobulinNumber = Double.parseDouble(prevIMMUNE_RESPONSE_alpha1Globulin);
							nextIMMUNE_RESPONSE_alpha1GlobulinNumber = Double.parseDouble(nextIMMUNE_RESPONSE_alpha1Globulin);
							
							double IMMUNE_RESPONSE_alpha1GlobulinChange = nextIMMUNE_RESPONSE_alpha1GlobulinNumber - prevIMMUNE_RESPONSE_alpha1GlobulinNumber;
							if(Math.abs(IMMUNE_RESPONSE_alpha1GlobulinChange) >= FeatureChangeSDU.IMMUNE_RESPONSE_alpha1Globulin){
								features.add(FeatureNumberSDU.IMMUNE_RESPONSE_alpha1Globulin);
								double IMMUNE_RESPONSE_alpha1GlobulinChangePercent = (nextIMMUNE_RESPONSE_alpha1GlobulinNumber - prevIMMUNE_RESPONSE_alpha1GlobulinNumber) / prevIMMUNE_RESPONSE_alpha1GlobulinNumber;
								if(prevIMMUNE_RESPONSE_alpha1GlobulinNumber == 0)
									IMMUNE_RESPONSE_alpha1GlobulinChangePercent =  nextIMMUNE_RESPONSE_alpha1GlobulinNumber;
								changeVals.add(IMMUNE_RESPONSE_alpha1GlobulinChangePercent);
						}//if(Math.abs)
					}//if(!prevIMMUNE_RESPONSE_alpha1Globulin)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevIMMUNE_RESPONSE_alpha2Globulin = prevEvent.getLab().getIMMUNE_RESPONSE_alpha2Globulin();
					String nextIMMUNE_RESPONSE_alpha2Globulin = nextEvent.getLab().getIMMUNE_RESPONSE_alpha2Globulin();
					double prevIMMUNE_RESPONSE_alpha2GlobulinNumber = 0;
					double nextIMMUNE_RESPONSE_alpha2GlobulinNumber = 0;
					if(!prevIMMUNE_RESPONSE_alpha2Globulin.equalsIgnoreCase("-") && !nextIMMUNE_RESPONSE_alpha2Globulin.equalsIgnoreCase("-")
							&& !prevIMMUNE_RESPONSE_alpha2Globulin.equalsIgnoreCase("") && !nextIMMUNE_RESPONSE_alpha2Globulin.equalsIgnoreCase("")){
							prevIMMUNE_RESPONSE_alpha2GlobulinNumber = Double.parseDouble(prevIMMUNE_RESPONSE_alpha2Globulin);
							nextIMMUNE_RESPONSE_alpha2GlobulinNumber = Double.parseDouble(nextIMMUNE_RESPONSE_alpha2Globulin);
							
							double IMMUNE_RESPONSE_alpha2GlobulinChange = nextIMMUNE_RESPONSE_alpha2GlobulinNumber - prevIMMUNE_RESPONSE_alpha2GlobulinNumber;
							if(Math.abs(IMMUNE_RESPONSE_alpha2GlobulinChange) >= FeatureChangeSDU.IMMUNE_RESPONSE_alpha2Globulin){
								features.add(FeatureNumberSDU.IMMUNE_RESPONSE_alpha2Globulin);
								double IMMUNE_RESPONSE_alpha2GlobulinChangePercent = (nextIMMUNE_RESPONSE_alpha2GlobulinNumber - prevIMMUNE_RESPONSE_alpha2GlobulinNumber) / prevIMMUNE_RESPONSE_alpha2GlobulinNumber;
								if(prevIMMUNE_RESPONSE_alpha2GlobulinNumber == 0)
									IMMUNE_RESPONSE_alpha2GlobulinChangePercent =  nextIMMUNE_RESPONSE_alpha2GlobulinNumber;
								changeVals.add(IMMUNE_RESPONSE_alpha2GlobulinChangePercent);
						}//if(Math.abs)
					}//if(!prevIMMUNE_RESPONSE_alpha2Globulin)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevIMMUNE_RESPONSE_betaGlobulin = prevEvent.getLab().getIMMUNE_RESPONSE_betaGlobulin();
					String nextIMMUNE_RESPONSE_betaGlobulin = nextEvent.getLab().getIMMUNE_RESPONSE_betaGlobulin();
					double prevIMMUNE_RESPONSE_betaGlobulinNumber = 0;
					double nextIMMUNE_RESPONSE_betaGlobulinNumber = 0;
					if(!prevIMMUNE_RESPONSE_betaGlobulin.equalsIgnoreCase("-") && !nextIMMUNE_RESPONSE_betaGlobulin.equalsIgnoreCase("-")
							&& !prevIMMUNE_RESPONSE_betaGlobulin.equalsIgnoreCase("") && !nextIMMUNE_RESPONSE_betaGlobulin.equalsIgnoreCase("")){
							prevIMMUNE_RESPONSE_betaGlobulinNumber = Double.parseDouble(prevIMMUNE_RESPONSE_betaGlobulin);
							nextIMMUNE_RESPONSE_betaGlobulinNumber = Double.parseDouble(nextIMMUNE_RESPONSE_betaGlobulin);
							
							double IMMUNE_RESPONSE_betaGlobulinChange = nextIMMUNE_RESPONSE_betaGlobulinNumber - prevIMMUNE_RESPONSE_betaGlobulinNumber;
							if(Math.abs(IMMUNE_RESPONSE_betaGlobulinChange) >= FeatureChangeSDU.IMMUNE_RESPONSE_betaGlobulin){
								features.add(FeatureNumberSDU.IMMUNE_RESPONSE_betaGlobulin);
								double IMMUNE_RESPONSE_betaGlobulinChangePercent = (nextIMMUNE_RESPONSE_betaGlobulinNumber - prevIMMUNE_RESPONSE_betaGlobulinNumber) / prevIMMUNE_RESPONSE_betaGlobulinNumber;
								if(prevIMMUNE_RESPONSE_betaGlobulinNumber == 0)
									IMMUNE_RESPONSE_betaGlobulinChangePercent =  nextIMMUNE_RESPONSE_betaGlobulinNumber;
								changeVals.add(IMMUNE_RESPONSE_betaGlobulinChangePercent);
						}//if(Math.abs)
					}//if(!prevIMMUNE_RESPONSE_betaGlobulin)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevIMMUNE_RESPONSE_albuminGlobulinRatio = prevEvent.getLab().getIMMUNE_RESPONSE_albuminGlobulinRatio();
					String nextIMMUNE_RESPONSE_albuminGlobulinRatio = nextEvent.getLab().getIMMUNE_RESPONSE_albuminGlobulinRatio();
					double prevIMMUNE_RESPONSE_albuminGlobulinRatioNumber = 0;
					double nextIMMUNE_RESPONSE_albuminGlobulinRatioNumber = 0;
					if(!prevIMMUNE_RESPONSE_albuminGlobulinRatio.equalsIgnoreCase("-") && !nextIMMUNE_RESPONSE_albuminGlobulinRatio.equalsIgnoreCase("-")
							&& !prevIMMUNE_RESPONSE_albuminGlobulinRatio.equalsIgnoreCase("") && !nextIMMUNE_RESPONSE_albuminGlobulinRatio.equalsIgnoreCase("")){
							prevIMMUNE_RESPONSE_albuminGlobulinRatioNumber = Double.parseDouble(prevIMMUNE_RESPONSE_albuminGlobulinRatio);
							nextIMMUNE_RESPONSE_albuminGlobulinRatioNumber = Double.parseDouble(nextIMMUNE_RESPONSE_albuminGlobulinRatio);
							
							double IMMUNE_RESPONSE_albuminGlobulinRatioChange = nextIMMUNE_RESPONSE_albuminGlobulinRatioNumber - prevIMMUNE_RESPONSE_albuminGlobulinRatioNumber;
							if(Math.abs(IMMUNE_RESPONSE_albuminGlobulinRatioChange) >= FeatureChangeSDU.IMMUNE_RESPONSE_albuminGlobulinRatio){
								features.add(FeatureNumberSDU.IMMUNE_RESPONSE_albuminGlobulinRatio);
								double IMMUNE_RESPONSE_albuminGlobulinRatioChangePercent = (nextIMMUNE_RESPONSE_albuminGlobulinRatioNumber - prevIMMUNE_RESPONSE_albuminGlobulinRatioNumber) / prevIMMUNE_RESPONSE_albuminGlobulinRatioNumber;
								if(prevIMMUNE_RESPONSE_albuminGlobulinRatioNumber == 0)
									IMMUNE_RESPONSE_albuminGlobulinRatioChangePercent =  nextIMMUNE_RESPONSE_albuminGlobulinRatioNumber;
								changeVals.add(IMMUNE_RESPONSE_albuminGlobulinRatioChangePercent);
						}//if(Math.abs)
					}//if(!prevIMMUNE_RESPONSE_albuminGlobulinRatio)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevHORMONES_TSH = prevEvent.getLab().getHORMONES_TSH();
					String nextHORMONES_TSH = nextEvent.getLab().getHORMONES_TSH();
					double prevHORMONES_TSHNumber = 0;
					double nextHORMONES_TSHNumber = 0;
					if(!prevHORMONES_TSH.equalsIgnoreCase("-") && !nextHORMONES_TSH.equalsIgnoreCase("-")
							&& !prevHORMONES_TSH.equalsIgnoreCase("") && !nextHORMONES_TSH.equalsIgnoreCase("")){
							prevHORMONES_TSHNumber = Double.parseDouble(prevHORMONES_TSH);
							nextHORMONES_TSHNumber = Double.parseDouble(nextHORMONES_TSH);
							
							double HORMONES_TSHChange = nextHORMONES_TSHNumber - prevHORMONES_TSHNumber;
							if(Math.abs(HORMONES_TSHChange) >= FeatureChangeSDU.HORMONES_TSH){
								features.add(FeatureNumberSDU.HORMONES_TSH);
								double HORMONES_TSHChangePercent = (nextHORMONES_TSHNumber - prevHORMONES_TSHNumber) / prevHORMONES_TSHNumber;
								if(prevHORMONES_TSHNumber == 0)
									HORMONES_TSHChangePercent =  nextHORMONES_TSHNumber;
								changeVals.add(HORMONES_TSHChangePercent);
						}//if(Math.abs)
					}//if(!prevHORMONES_TSH)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevHORMONES_FreeT3 = prevEvent.getLab().getHORMONES_FreeT3();
					String nextHORMONES_FreeT3 = nextEvent.getLab().getHORMONES_FreeT3();
					double prevHORMONES_FreeT3Number = 0;
					double nextHORMONES_FreeT3Number = 0;
					if(!prevHORMONES_FreeT3.equalsIgnoreCase("-") && !nextHORMONES_FreeT3.equalsIgnoreCase("-")
							&& !prevHORMONES_FreeT3.equalsIgnoreCase("") && !nextHORMONES_FreeT3.equalsIgnoreCase("")){
							prevHORMONES_FreeT3Number = Double.parseDouble(prevHORMONES_FreeT3);
							nextHORMONES_FreeT3Number = Double.parseDouble(nextHORMONES_FreeT3);
							
							double HORMONES_FreeT3Change = nextHORMONES_FreeT3Number - prevHORMONES_FreeT3Number;
							if(Math.abs(HORMONES_FreeT3Change) >= FeatureChangeSDU.HORMONES_FreeT3){
								features.add(FeatureNumberSDU.HORMONES_FreeT3);
								double HORMONES_FreeT3ChangePercent = (nextHORMONES_FreeT3Number - prevHORMONES_FreeT3Number) / prevHORMONES_FreeT3Number;
								if(prevHORMONES_FreeT3Number == 0)
									HORMONES_FreeT3ChangePercent =  nextHORMONES_FreeT3Number;
								changeVals.add(HORMONES_FreeT3ChangePercent);
						}//if(Math.abs)
					}//if(!prevHORMONES_FreeT3)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevHORMONES_FreeT4 = prevEvent.getLab().getHORMONES_FreeT4();
					String nextHORMONES_FreeT4 = nextEvent.getLab().getHORMONES_FreeT4();
					double prevHORMONES_FreeT4Number = 0;
					double nextHORMONES_FreeT4Number = 0;
					if(!prevHORMONES_FreeT4.equalsIgnoreCase("-") && !nextHORMONES_FreeT4.equalsIgnoreCase("-")
							&& !prevHORMONES_FreeT4.equalsIgnoreCase("") && !nextHORMONES_FreeT4.equalsIgnoreCase("")){
							prevHORMONES_FreeT4Number = Double.parseDouble(prevHORMONES_FreeT4);
							nextHORMONES_FreeT4Number = Double.parseDouble(nextHORMONES_FreeT4);
							
							double HORMONES_FreeT4Change = nextHORMONES_FreeT4Number - prevHORMONES_FreeT4Number;
							if(Math.abs(HORMONES_FreeT4Change) >= FeatureChangeSDU.HORMONES_FreeT4){
								features.add(FeatureNumberSDU.HORMONES_FreeT4);
								double HORMONES_FreeT4ChangePercent = (nextHORMONES_FreeT4Number - prevHORMONES_FreeT4Number) / prevHORMONES_FreeT4Number;
								if(prevHORMONES_FreeT4Number == 0)
									HORMONES_FreeT4ChangePercent =  nextHORMONES_FreeT4Number;
								changeVals.add(HORMONES_FreeT4ChangePercent);
						}//if(Math.abs)
					}//if(!prevHORMONES_FreeT4)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevHORMONES_betaHCG = prevEvent.getLab().getHORMONES_betaHCG();
					String nextHORMONES_betaHCG = nextEvent.getLab().getHORMONES_betaHCG();
					double prevHORMONES_betaHCGNumber = 0;
					double nextHORMONES_betaHCGNumber = 0;
					if(!prevHORMONES_betaHCG.equalsIgnoreCase("-") && !nextHORMONES_betaHCG.equalsIgnoreCase("-")
							&& !prevHORMONES_betaHCG.equalsIgnoreCase("") && !nextHORMONES_betaHCG.equalsIgnoreCase("")
							&& !prevHORMONES_betaHCG.equalsIgnoreCase("normal") && !nextHORMONES_betaHCG.equalsIgnoreCase("normal")
							&& !prevHORMONES_betaHCG.equalsIgnoreCase("negative") && !nextHORMONES_betaHCG.equalsIgnoreCase("negative")){
							prevHORMONES_betaHCGNumber = Double.parseDouble(prevHORMONES_betaHCG);
							nextHORMONES_betaHCGNumber = Double.parseDouble(nextHORMONES_betaHCG);
							
							double HORMONES_betaHCGChange = nextHORMONES_betaHCGNumber - prevHORMONES_betaHCGNumber;
							if(Math.abs(HORMONES_betaHCGChange) >= FeatureChangeSDU.HORMONES_betaHCG){
								features.add(FeatureNumberSDU.HORMONES_betaHCG);
								double HORMONES_betaHCGChangePercent = (nextHORMONES_betaHCGNumber - prevHORMONES_betaHCGNumber) / prevHORMONES_betaHCGNumber;
								if(prevHORMONES_betaHCGNumber == 0)
									HORMONES_betaHCGChangePercent =  nextHORMONES_betaHCGNumber;
								changeVals.add(HORMONES_betaHCGChangePercent);
						}//if(Math.abs)
					}//if(!prevHORMONES_betaHCG)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOAGULATION_prothrombinTime = prevEvent.getLab().getCOAGULATION_prothrombinTime();
					String nextCOAGULATION_prothrombinTime = nextEvent.getLab().getCOAGULATION_prothrombinTime();
					double prevCOAGULATION_prothrombinTimeNumber = 0;
					double nextCOAGULATION_prothrombinTimeNumber = 0;
					if(!prevCOAGULATION_prothrombinTime.equalsIgnoreCase("-") && !nextCOAGULATION_prothrombinTime.equalsIgnoreCase("-")
							&& !prevCOAGULATION_prothrombinTime.equalsIgnoreCase("") && !nextCOAGULATION_prothrombinTime.equalsIgnoreCase("")){
							prevCOAGULATION_prothrombinTimeNumber = Double.parseDouble(prevCOAGULATION_prothrombinTime);
							nextCOAGULATION_prothrombinTimeNumber = Double.parseDouble(nextCOAGULATION_prothrombinTime);
							
							double COAGULATION_prothrombinTimeChange = nextCOAGULATION_prothrombinTimeNumber - prevCOAGULATION_prothrombinTimeNumber;
							if(Math.abs(COAGULATION_prothrombinTimeChange) >= FeatureChangeSDU.COAGULATION_prothrombinTime){
								features.add(FeatureNumberSDU.COAGULATION_prothrombinTime);
								double COAGULATION_prothrombinTimeChangePercent = (nextCOAGULATION_prothrombinTimeNumber - prevCOAGULATION_prothrombinTimeNumber) / prevCOAGULATION_prothrombinTimeNumber;
								if(prevCOAGULATION_prothrombinTimeNumber == 0)
									COAGULATION_prothrombinTimeChangePercent =  nextCOAGULATION_prothrombinTimeNumber;
								changeVals.add(COAGULATION_prothrombinTimeChangePercent);
						}//if(Math.abs)
					}//if(!prevCOAGULATION_prothrombinTime)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevCOAGULATION_internationalNormalizedRatio = prevEvent.getLab().getCOAGULATION_internationalNormalizedRatio();
					String nextCOAGULATION_internationalNormalizedRatio = nextEvent.getLab().getCOAGULATION_internationalNormalizedRatio();
					double prevCOAGULATION_internationalNormalizedRatioNumber = 0;
					double nextCOAGULATION_internationalNormalizedRatioNumber = 0;
					if(!prevCOAGULATION_internationalNormalizedRatio.equalsIgnoreCase("-") && !nextCOAGULATION_internationalNormalizedRatio.equalsIgnoreCase("-")
							&& !prevCOAGULATION_internationalNormalizedRatio.equalsIgnoreCase("") && !nextCOAGULATION_internationalNormalizedRatio.equalsIgnoreCase("")){
							prevCOAGULATION_internationalNormalizedRatioNumber = Double.parseDouble(prevCOAGULATION_internationalNormalizedRatio);
							nextCOAGULATION_internationalNormalizedRatioNumber = Double.parseDouble(nextCOAGULATION_internationalNormalizedRatio);
							
							double COAGULATION_internationalNormalizedRatioChange = nextCOAGULATION_internationalNormalizedRatioNumber - prevCOAGULATION_internationalNormalizedRatioNumber;
							if(Math.abs(COAGULATION_internationalNormalizedRatioChange) >= FeatureChangeSDU.COAGULATION_internationalNormalizedRatio){
								features.add(FeatureNumberSDU.COAGULATION_internationalNormalizedRatio);
								double COAGULATION_internationalNormalizedRatioChangePercent = (nextCOAGULATION_internationalNormalizedRatioNumber - prevCOAGULATION_internationalNormalizedRatioNumber) / prevCOAGULATION_internationalNormalizedRatioNumber;
								if(prevCOAGULATION_internationalNormalizedRatioNumber == 0)
									COAGULATION_internationalNormalizedRatioChangePercent =  nextCOAGULATION_internationalNormalizedRatioNumber;
								changeVals.add(COAGULATION_internationalNormalizedRatioChangePercent);
						}//if(Math.abs)
					}//if(!prevCOAGULATION_internationalNormalizedRatio)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevOTHERS_amylase = prevEvent.getLab().getOTHERS_amylase();
					String nextOTHERS_amylase = nextEvent.getLab().getOTHERS_amylase();
					double prevOTHERS_amylaseNumber = 0;
					double nextOTHERS_amylaseNumber = 0;
					if(!prevOTHERS_amylase.equalsIgnoreCase("-") && !nextOTHERS_amylase.equalsIgnoreCase("-")
							&& !prevOTHERS_amylase.equalsIgnoreCase("") && !nextOTHERS_amylase.equalsIgnoreCase("")){
							prevOTHERS_amylaseNumber = Double.parseDouble(prevOTHERS_amylase);
							nextOTHERS_amylaseNumber = Double.parseDouble(nextOTHERS_amylase);
							
							double OTHERS_amylaseChange = nextOTHERS_amylaseNumber - prevOTHERS_amylaseNumber;
							if(Math.abs(OTHERS_amylaseChange) >= FeatureChangeSDU.OTHERS_amylase){
								features.add(FeatureNumberSDU.OTHERS_amylase);
								double OTHERS_amylaseChangePercent = (nextOTHERS_amylaseNumber - prevOTHERS_amylaseNumber) / prevOTHERS_amylaseNumber;
								if(prevOTHERS_amylaseNumber == 0)
									OTHERS_amylaseChangePercent =  nextOTHERS_amylaseNumber;
								changeVals.add(OTHERS_amylaseChangePercent);
						}//if(Math.abs)
					}//if(!prevOTHERS_amylase)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevOTHERS_salivaryAmylase = prevEvent.getLab().getOTHERS_salivaryAmylase();
					String nextOTHERS_salivaryAmylase = nextEvent.getLab().getOTHERS_salivaryAmylase();
					double prevOTHERS_salivaryAmylaseNumber = 0;
					double nextOTHERS_salivaryAmylaseNumber = 0;
					if(!prevOTHERS_salivaryAmylase.equalsIgnoreCase("-") && !nextOTHERS_salivaryAmylase.equalsIgnoreCase("-")
							&& !prevOTHERS_salivaryAmylase.equalsIgnoreCase("") && !nextOTHERS_salivaryAmylase.equalsIgnoreCase("")){
							prevOTHERS_salivaryAmylaseNumber = Double.parseDouble(prevOTHERS_salivaryAmylase);
							nextOTHERS_salivaryAmylaseNumber = Double.parseDouble(nextOTHERS_salivaryAmylase);
							
							double OTHERS_salivaryAmylaseChange = nextOTHERS_salivaryAmylaseNumber - prevOTHERS_salivaryAmylaseNumber;
							if(Math.abs(OTHERS_salivaryAmylaseChange) >= FeatureChangeSDU.OTHERS_salivaryAmylase){
								features.add(FeatureNumberSDU.OTHERS_salivaryAmylase);
								double OTHERS_salivaryAmylaseChangePercent = (nextOTHERS_salivaryAmylaseNumber - prevOTHERS_salivaryAmylaseNumber) / prevOTHERS_salivaryAmylaseNumber;
								if(prevOTHERS_salivaryAmylaseNumber == 0)
									OTHERS_salivaryAmylaseChangePercent =  nextOTHERS_salivaryAmylaseNumber;
								changeVals.add(OTHERS_salivaryAmylaseChangePercent);
						}//if(Math.abs)
					}//if(!prevOTHERS_salivaryAmylase)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				if(prevEvent.getLab() != null && nextEvent.getLab() != null){
					String prevOTHERS_pancreaticAmylase = prevEvent.getLab().getOTHERS_pancreaticAmylase();
					String nextOTHERS_pancreaticAmylase = nextEvent.getLab().getOTHERS_pancreaticAmylase();
					double prevOTHERS_pancreaticAmylaseNumber = 0;
					double nextOTHERS_pancreaticAmylaseNumber = 0;
					if(!prevOTHERS_pancreaticAmylase.equalsIgnoreCase("-") && !nextOTHERS_pancreaticAmylase.equalsIgnoreCase("-")
							&& !prevOTHERS_pancreaticAmylase.equalsIgnoreCase("") && !nextOTHERS_pancreaticAmylase.equalsIgnoreCase("")){
							prevOTHERS_pancreaticAmylaseNumber = Double.parseDouble(prevOTHERS_pancreaticAmylase);
							nextOTHERS_pancreaticAmylaseNumber = Double.parseDouble(nextOTHERS_pancreaticAmylase);
							
							double OTHERS_pancreaticAmylaseChange = nextOTHERS_pancreaticAmylaseNumber - prevOTHERS_pancreaticAmylaseNumber;
							if(Math.abs(OTHERS_pancreaticAmylaseChange) >= FeatureChangeSDU.OTHERS_pancreaticAmylase){
								features.add(FeatureNumberSDU.OTHERS_pancreaticAmylase);
								double OTHERS_pancreaticAmylaseChangePercent = (nextOTHERS_pancreaticAmylaseNumber - prevOTHERS_pancreaticAmylaseNumber) / prevOTHERS_pancreaticAmylaseNumber;
								if(prevOTHERS_pancreaticAmylaseNumber == 0)
									OTHERS_pancreaticAmylaseChangePercent =  nextOTHERS_pancreaticAmylaseNumber;
								changeVals.add(OTHERS_pancreaticAmylaseChangePercent);
						}//if(Math.abs)
					}//if(!prevOTHERS_pancreaticAmylase)
				}//if(prevEvent)
				
				///////////////////////////////////////////////////////////////////////////////////////////
				
				evc.setFeatures(features);	//add all feature numbers to current event change
				evc.setChangeVals(changeVals);	//add all changes to current event change
				eventsChangeArray[j] = evc;	//set current event change to event change array of current patient 
			}//for(j)
			allPatientsSeq.add(eventsChangeArray);
			updateDependencyMatrix(eventsChangeArray, sex, age, death);	//set each index of dependency matrix
			
		}//for(i)
//		createDensityMatrix();	//kernel density estimation function with java implementation
		File statisticsFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"
				+firstTypeClass+"\\"+secondTypeClass+"\\"+"Statistics_"+secondTypeClass+".txt");
		PrintWriter pwStatistics = null;
		try {
			pwStatistics = new PrintWriter(statisticsFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//create progression statistics file
		if(firstTypeClass.equalsIgnoreCase(ALSUtilsSDU.progression)) {
			double patientsPercent = patientsCtr / numOfPatients;
			double menPercent = menCtr / patientsCtr;
			double womenPercent = womenCtr / patientsCtr;
			double riluzoleYesPercent = riluzoleYesCtr / patientsCtr;
			double riluzoleNoPercent = riluzoleNoCtr / patientsCtr;
			pwStatistics.println("Number of patients = "+patientsCtr+" | Percentage = "+patientsPercent);
			pwStatistics.println("Number of men patients = "+menCtr+" | Percentage = "+menPercent);
			pwStatistics.println("Number of women patients = "+womenCtr+" | Percentage = "+womenPercent);
			pwStatistics.println("Number of patients used Riluzole = "+riluzoleYesCtr+" | Percentage = "+riluzoleYesPercent);
			pwStatistics.println("Number of patients not used Riluzole = "+riluzoleNoCtr+" | Percentage = "+riluzoleNoPercent);
			pwStatistics.close();
			
			System.out.println("Number of patients = "+patientsCtr+" | Percentage = "+patientsPercent);
			System.out.println("Number of men patients = "+menCtr+" | Percentage = "+menPercent);
			System.out.println("Number of women patients = "+womenCtr+" | Percentage = "+womenPercent);
			System.out.println("Number of patients used Riluzole = "+riluzoleYesCtr+" | Percentage = "+riluzoleYesPercent);
			System.out.println("Number of patients not used Riluzole = "+riluzoleNoCtr+" | Percentage = "+riluzoleNoPercent);
		}//if(firstTypeClass)
		
		//create gender statistics file
				if(firstTypeClass.equalsIgnoreCase(ALSUtilsSDU.gender)) {
					double patientsPercent = patientsCtr / numOfPatients;
					double slowPercent = slowCtr / patientsCtr;
					double intermediatePercent = intermediateCtr / patientsCtr;
					double fastPercent = fastCtr / patientsCtr;
					double riluzoleYesPercent = riluzoleYesCtr / patientsCtr;
					double riluzoleNoPercent = riluzoleNoCtr / patientsCtr;
					pwStatistics.println("Number of patients = "+patientsCtr+" | Percentage = "+patientsPercent);
					pwStatistics.println("Number of slow patients = "+slowCtr+" | Percentage = "+slowPercent);
					pwStatistics.println("Number of intermediate patients = "+intermediateCtr+" | Percentage = "+intermediatePercent);
					pwStatistics.println("Number of fast patients = "+fastCtr+" | Percentage = "+fastPercent);
					pwStatistics.println("Number of patients used Riluzole = "+riluzoleYesCtr+" | Percentage = "+riluzoleYesPercent);
					pwStatistics.println("Number of patients not used Riluzole = "+riluzoleNoCtr+" | Percentage = "+riluzoleNoPercent);
					pwStatistics.close();
					
					System.out.println("Number of patients = "+patientsCtr+" | Percentage = "+patientsPercent);
					System.out.println("Number of slow patients = "+slowCtr+" | Percentage = "+slowPercent);
					System.out.println("Number of intermediate patients = "+intermediateCtr+" | Percentage = "+intermediatePercent);
					System.out.println("Number of fast patients = "+fastCtr+" | Percentage = "+fastPercent);
					System.out.println("Number of patients used Riluzole = "+riluzoleYesCtr+" | Percentage = "+riluzoleYesPercent);
					System.out.println("Number of patients not used Riluzole = "+riluzoleNoCtr+" | Percentage = "+riluzoleNoPercent);
				}//if(firstTypeClass)
				
				//create Riluzole statistics file
				if(firstTypeClass.equalsIgnoreCase(ALSUtilsSDU.riluzole)) {
					double patientsPercent = patientsCtr / numOfPatients;
					double slowPercent = slowCtr / patientsCtr;
					double intermediatePercent = intermediateCtr / patientsCtr;
					double fastPercent = fastCtr / patientsCtr;
					double menPercent = menCtr / patientsCtr;
					double womenPercent = womenCtr / patientsCtr;
					pwStatistics.println("Number of patients = "+patientsCtr+" | Percentage = "+patientsPercent);
					pwStatistics.println("Number of slow patients = "+slowCtr+" | Percentage = "+slowPercent);
					pwStatistics.println("Number of intermediate patients = "+intermediateCtr+" | Percentage = "+intermediatePercent);
					pwStatistics.println("Number of fast patients = "+fastCtr+" | Percentage = "+fastPercent);
					pwStatistics.println("Number of men patients = "+menCtr+" | Percentage = "+menPercent);
					pwStatistics.println("Number of women patients = "+womenCtr+" | Percentage = "+womenPercent);
					pwStatistics.close();
					
					System.out.println("Number of patients = "+patientsCtr+" | Percentage = "+patientsPercent);
					System.out.println("Number of slow patients = "+slowCtr+" | Percentage = "+slowPercent);
					System.out.println("Number of intermediate patients = "+intermediateCtr+" | Percentage = "+intermediatePercent);
					System.out.println("Number of fast patients = "+fastCtr+" | Percentage = "+fastPercent);
					System.out.println("Number of men patients = "+menCtr+" | Percentage = "+menPercent);
					System.out.println("Number of women patients = "+womenCtr+" | Percentage = "+womenPercent);
				}//if(firstTypeClass)
		
		System.out.println("Normalizing dependency matrix . . .");
		normalizeDependencyMatrix();
		
		System.out.println("Creating dependency matrix files . . .");		
		printDependencyMatrixFile();	//print all matrix elements to files
		System.out.println("Dependency matrix files has been created successfully.");
		System.out.println("--------------------------------------------------------------------------------------------");
		
	}//createDependencyMatrix
	
	private double getCausality(int row, int col){
		//calculate causal tendency of row-->col based on log [p(col'|row')/p(col'|row)]
		EventChange[] events;
		int ctrSorat = 0;
		int ctrMakhraj = 0;
		double probSorat = 0;
		double probMakhraj = 0;
		ArrayList<Integer> rowArr, colArr;
		
		//soorat
		for(int i=0; i<allPatientsSeq.size(); i++){
			events = allPatientsSeq.get(i);
			if(events == null)
				continue;
			for(int j=1; j<events.length-1; j++){
				rowArr = events[j].getFeatures();
				colArr = events[j+1].getFeatures();
				if(!isExistIdx(rowArr, row)){
					ctrSorat++;
					if(!isExistIdx(colArr, col))
						probSorat++;
				}//if(!isExistIdx)
			}//for(j)
		}//for(i)
		
		if(ctrSorat==0)
			probSorat = 0;
		else
			probSorat = probSorat/ctrSorat;
		
		//makhraj
		for(int i=0; i<allPatientsSeq.size(); i++){
			events = allPatientsSeq.get(i);
			if(events == null)
				continue;
			for(int j=1; j<events.length-1; j++){
				rowArr = events[j].getFeatures();
				colArr = events[j+1].getFeatures();
				if(isExistIdx(rowArr, row)){
					ctrMakhraj++;
					if(!isExistIdx(colArr, col))
						probMakhraj++;
				}//if(!isExistIdx)
			}//for(j)
		}//for(i)
		double answer = 0;
		if(ctrMakhraj == 0)
			probMakhraj = 0;
		else	
			probMakhraj = probMakhraj/ctrMakhraj;
		
		if(probMakhraj == 0)
			return 0;
		else
			answer = Math.log10(probSorat/probMakhraj);
		if(answer<0)
			return 0;
		else
			return Math.log10(probSorat/probMakhraj);
	}//getCausality
	
	private double getCausalTendency(int row, int col){
		//calculate causal tendency of row-->col based on log [p(T-->T)/p(T-->F)]
		EventChange[] events;
		int ctrSorat = 0;
		int ctrMakhraj = 0;
		double probSorat = 0;
		double probMakhraj = 0;
		ArrayList<Integer> rowArr, colArr;
		
		//soorat
		for(int i=0; i<allPatientsSeq.size(); i++){
			events = allPatientsSeq.get(i);
			if(events == null)
				continue;
			for(int j=1; j<events.length-1; j++){
				rowArr = events[j].getFeatures();
				colArr = events[j+1].getFeatures();
				if(isExistIdx(colArr, col)){
					ctrSorat++;
					if(isExistIdx(rowArr, row))
						probSorat++;
				}//if(!isExistIdx)
			}//for(j)
		}//for(i)
		
		if(ctrSorat==0)
			probSorat = 0;
		else
			probSorat = probSorat/ctrSorat;//P(T-->T)
		
		//makhraj
		for(int i=0; i<allPatientsSeq.size(); i++){
			events = allPatientsSeq.get(i);
			if(events == null)
				continue;
			for(int j=1; j<events.length-1; j++){
				rowArr = events[j].getFeatures();
				colArr = events[j+1].getFeatures();
				if(!isExistIdx(colArr, col)){
					ctrMakhraj++;
					if(isExistIdx(rowArr, row))
						probMakhraj++;
				}//if(!isExistIdx)
			}//for(j)
		}//for(i)
		
		if(ctrMakhraj == 0)
			probMakhraj = 0;
		else	
			probMakhraj = probMakhraj/ctrMakhraj;
		
		if(probMakhraj == 0)
			return 0;
		double answer = Math.log10(probSorat/probMakhraj);
		
		if(answer<0)
			return 0;
		
		return answer;
		
	}//getCausalityNewVer
	
	private boolean isExistIdx(ArrayList<Integer> list, int idx){
		for(int i=0; i<list.size(); i++)
			if(list.get(i) == idx)
				return true;
		return false;
	}//isExistIdx 
	
	//CFD matrix indexes file
	private void printDepMatIdxFile(){
		File fileIdx = new File(getCFDIdxFileLocation());
		DependencyMatrixIndexSDU depMat;
		try {
			PrintWriter pwIdx = new PrintWriter(fileIdx);
			//dependency matrix index file
			int idx = 0;
			int listSize = 0;
			for(int k=0; k<ALSUtilsSDU.numOfDynamicFeatures; k++){
				for(int t=0; t<ALSUtilsSDU.numOfDynamicFeatures; t++){
					depMat = causalFeaturesDependencyMatrix[k][t];
					listSize = depMat.getNextChanges().size();
					if(listSize == 0){
						pwIdx.println(k+","+t+",-1,-1");
						continue;
					}//if
					pwIdx.println(k+","+t+","+(idx+1)+","+(idx+listSize));
					idx = idx + listSize;
				}//for(t)
			}//for(k)
			pwIdx.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//printDepMatIdxFile
	
	
	private void printDepMatFile(){
		File fileDepMat = new File(getCFDFileLocation());
		DependencyMatrixIndexSDU depMat;
		try {
			PrintWriter pwDepMat = new PrintWriter(fileDepMat);
			double prev, next;
			//dependency matrix file
			for(int i=0; i<ALSUtilsSDU.numOfDynamicFeatures; i++){
				for(int j=0; j<ALSUtilsSDU.numOfDynamicFeatures; j++){
					depMat = causalFeaturesDependencyMatrix[i][j];
					for(int k=0; k<depMat.getNextChanges().size(); k++){
						prev = depMat.getPrevChanges().get(k);
						next = depMat.getNextChanges().get(k);
						pwDepMat.printf("%.2f", prev);
						pwDepMat.print(",");
						pwDepMat.printf("%.2f", next);
						pwDepMat.println();
					}//for(k)
				}//for(j)
			}//for(i)
			pwDepMat.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//printDepMatFile
	
	
	//print ALSFRS relative changes
	private void printTargetFile(){
		File fileTarget = new File(getTargetFileLocation());
		DependencyMatrixIndexSDU depMat;
		try {
			PrintWriter pwTarget = new PrintWriter(fileTarget);
			double target;
			//target changes file
			for(int i=0; i<ALSUtilsSDU.numOfDynamicFeatures; i++){
				for(int j=0; j<ALSUtilsSDU.numOfDynamicFeatures; j++){
					depMat = causalFeaturesDependencyMatrix[i][j];
					for(int k=0; k<depMat.getTargetChanges().size(); k++){
						target = depMat.getTargetChanges().get(k);
						pwTarget.printf("%.5f", target);
						pwTarget.println();
					}//for(k)
				}//for(j)
			}//for(i)
			pwTarget.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}//printTargetFile
	
	
	//print ALSFRS absolute changes
	private void printTargetAbsFile(){
		File fileTarget = new File(getTargetAbsFileLocation());
		DependencyMatrixIndexSDU depMat;
		try {
			PrintWriter pwTarget = new PrintWriter(fileTarget);
			double target;
			//target changes file
			for(int i=0; i<ALSUtilsSDU.numOfDynamicFeatures; i++){
				for(int j=0; j<ALSUtilsSDU.numOfDynamicFeatures; j++){
					depMat = causalFeaturesDependencyMatrix[i][j];
					for(int k=0; k<depMat.getTargetChangesAbs().size(); k++){
						target = depMat.getTargetChangesAbs().get(k);
						pwTarget.print(target);
						pwTarget.println();
					}//for(k)
				}//for(j)
			}//for(i)
			pwTarget.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}//printTargetAbsFile
	
	//print causal tendency file
	private void printCausalTendencyFile(){
		File fileCausalProb = new File(getCausalTendencyFileLocation());
		DependencyMatrixIndexSDU depMat;
		try {
			PrintWriter pwCausalProb = new PrintWriter(fileCausalProb);
			//causal probability file
			for(int i=0; i<ALSUtilsSDU.numOfDynamicFeatures; i++){
				for(int j=0; j<ALSUtilsSDU.numOfDynamicFeatures; j++){
					depMat = causalFeaturesDependencyMatrix[i][j];
					double causalProb = depMat.getCausalityProbability();
					pwCausalProb.printf("%.2f", causalProb);
					pwCausalProb.println();					
				}//for(j)
			}//for(i)
			pwCausalProb.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}//printCausalTendencyFile
	
	
	private void printMenWomenFile(){
		File fileMenWomen = new File(getMenWomenFileLocation());
		DependencyMatrixIndexSDU depMat;
		try {
			PrintWriter pwMenWomen = new PrintWriter(fileMenWomen);
			//men and women probability file
			for(int i=0; i<ALSUtilsSDU.numOfDynamicFeatures; i++){
				for(int j=0; j<ALSUtilsSDU.numOfDynamicFeatures; j++){
					depMat = causalFeaturesDependencyMatrix[i][j];
					double men = depMat.getMenPercent();
					double women = depMat.getWomenPercent();
					pwMenWomen.printf("%.2f", men);
					pwMenWomen.print(",");
					pwMenWomen.printf("%.2f", women);
					pwMenWomen.println();					
				}//for(j)
			}//for(i)
			pwMenWomen.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//printMenWomenFile
	
	
	private void printDeathFile(){
		File fileDeath = new File(getDeathFileLocation());
		DependencyMatrixIndexSDU depMat;
		try {
			PrintWriter pwDeath = new PrintWriter(fileDeath);
			//death probability file
			for(int i=0; i<ALSUtilsSDU.numOfDynamicFeatures; i++){
				for(int j=0; j<ALSUtilsSDU.numOfDynamicFeatures; j++){
					depMat = causalFeaturesDependencyMatrix[i][j];
					double death = depMat.getDeathProb();
					pwDeath.printf("%.2f", death);
					pwDeath.println();					
				}//for(j)
			}//for(i)
			pwDeath.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//printDeathFile

	
	private void printAgeFile(){
		File fileDeath = new File(getAgeFileLocation());
		DependencyMatrixIndexSDU depMat;
		try {
			PrintWriter pwAge = new PrintWriter(fileDeath);
			//death probability file
			for(int i=0; i<ALSUtilsSDU.numOfDynamicFeatures; i++){
				for(int j=0; j<ALSUtilsSDU.numOfDynamicFeatures; j++){
					depMat = causalFeaturesDependencyMatrix[i][j];
					double age = depMat.getAvgAge();
					pwAge.printf("%.2f", age);
					pwAge.println();					
				}//for(j)
			}//for(i)
			pwAge.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//printAgeFile
	
	private void printTimeDistFile(){
		File fileTimeDist = new File(getTimeDistanceFileLocation());
		DependencyMatrixIndexSDU depMat;
		try {
			PrintWriter pwTimeDist = new PrintWriter(fileTimeDist);
			//time distance file
			for(int i=0; i<ALSUtilsSDU.numOfDynamicFeatures; i++){
				for(int j=0; j<ALSUtilsSDU.numOfDynamicFeatures; j++){
					depMat = causalFeaturesDependencyMatrix[i][j];
					double timeDist = depMat.getAvgTimeDist();
					pwTimeDist.printf("%.2f", timeDist);
					pwTimeDist.println();					
				}//for(j)
			}//for(i)
			pwTimeDist.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}//printTimeDistFile
	
	private void printTimePosFile(){
		File fileTimePos = new File(getTimePositionFileLocation());
		DependencyMatrixIndexSDU depMat;
		try {
			PrintWriter pwTimePos = new PrintWriter(fileTimePos);
			double timePos;
			//time position file
			for(int i=0; i<ALSUtilsSDU.numOfDynamicFeatures; i++){
				for(int j=0; j<ALSUtilsSDU.numOfDynamicFeatures; j++){
					depMat = causalFeaturesDependencyMatrix[i][j];
					for(int k=0; k<depMat.getTimePositions().size(); k++){
						timePos = depMat.getTimePositions().get(k);
						pwTimePos.printf("%.2f", timePos);
						pwTimePos.println();
					}//for(k)
				}//for(j)
			}//for(i)
			pwTimePos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//printTimePosFile
	
	public String getCFDIdxFileLocation() {
		return "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+firstTypeClass+"\\"+secondTypeClass+"\\CFD\\"+"CFDIdx.txt";
	}//getCFDIdxFileLocation
	
	public String getCFDFileLocation() {
		return "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+firstTypeClass+"\\"+secondTypeClass+"\\CFD\\"+"CFD.txt";
	}//getCFDFileLocation
	
	public String getTargetFileLocation() {
		return "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+firstTypeClass+"\\"+secondTypeClass+"\\CFD\\"+"Target.txt";
	}//getTargetFileLocation
	
	public String getTargetAbsFileLocation() {
		return "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+firstTypeClass+"\\"+secondTypeClass+"\\CFD\\"+"TargetAbs.txt";
	}//getTargetAbsFileLocation
	
	public String getCausalTendencyFileLocation() {
		return "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+firstTypeClass+"\\"+secondTypeClass+"\\CFD\\"+"CausalTendency.txt";
	}//getCausalTendencyFileLocation
	
	public String getMenWomenFileLocation() {
		return "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+firstTypeClass+"\\"+secondTypeClass+"\\CFD\\"+"MenWomen.txt";
	}//getMenWomenFileLocation
	
	public String getDeathFileLocation() {
		return "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+firstTypeClass+"\\"+secondTypeClass+"\\CFD\\"+"Death.txt";
	}//getDeathFileLocation
	
	public String getAgeFileLocation() {
		return "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+firstTypeClass+"\\"+secondTypeClass+"\\CFD\\"+"Age.txt";
	}//getAgeFileLocation
	
	public String getTimeDistanceFileLocation() {
		return "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+firstTypeClass+"\\"+secondTypeClass+"\\CFD\\"+"TimeDistance.txt";
	}//getTimeDistanceFileLocation
	
	public String getTimePositionFileLocation() {
		return "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+firstTypeClass+"\\"+secondTypeClass+"\\CFD\\"+"TimePosition.txt";
	}//getTimePositionFileLocation
	
	private void printDependencyMatrixFile(){
		printDepMatIdxFile();
		printDepMatFile();
		printCausalTendencyFile();
		printMenWomenFile();
		printDeathFile();
		printTargetFile();
		printTargetAbsFile();
		printTimeDistFile();
		printTimePosFile();
		printAgeFile();

	}//printDependencyMatrixFile
	
//	private void createDensityMatrix(){
//		DependencyMatrixIndex dmi;
//		double[] x;
//		double[] y;
//		ALSDensity density;
//		int arraySize = 0;
//		KernelDensityEstimator2D kde2D;
//		for(int i=0; i<numOfDynamicFeaturs; i++){
//			for(int j=0; j<numOfDynamicFeaturs; j++){
//				dmi = causalFeaturesDependencyMatrix[i][j];
//				if(dmi.getPrevChanges().size() == 0 || dmi.getNextChanges().size() == 0)
//					continue;
//				arraySize = dmi.getNextChanges().size();
//				x = new double[arraySize];
//				y = new double[arraySize];
//				density = new ALSDensity(bandWidth);
//				for(int k=0; k<arraySize; k++){
//					x[k] = dmi.getPrevChanges().get(k);
//					y[k] = dmi.getNextChanges().get(k);
//				}//for(k)
//				kde2D = new KernelDensityEstimator2D(x,y,bandWidth);	//run kernel density estimation
//				density.setX(kde2D.getXGrid());
//				density.setY(kde2D.getYGrid());
//				density.setDensity(kde2D.getKDE());
//				causalFeatureDensityMatrix[i][j] = density;
//			}//for(j)
//		}//for(i)
//	}//createDensityMatrix
	

	private void normalizeDependencyMatrix() {
		// TODO Auto-generated method stub
		ArrayList<Double> list = new ArrayList<Double>();
		double males, females;
		double causalProb = 0;
		double sum = 0;
		ArrayList<String> deathList = new ArrayList<String>();
		for(int i=0; i<ALSUtilsSDU.numOfDynamicFeatures; i++){
			for(int j=0; j<ALSUtilsSDU.numOfDynamicFeatures; j++){
				
				//set men and women percent and causal probability
				males = causalFeaturesDependencyMatrix[i][j].getMenPercent();
				females = causalFeaturesDependencyMatrix[i][j].getWomenPercent();
				if(males+females == 0){
					causalFeaturesDependencyMatrix[i][j].setMenPercent(0);
					causalFeaturesDependencyMatrix[i][j].setWomenPercent(0);
				}//if
				else{	
					causalFeaturesDependencyMatrix[i][j].setMenPercent(males/(males+females));
					causalFeaturesDependencyMatrix[i][j].setWomenPercent(females/(males+females));
				}//else
//				causalProb = causalFeaturesDependencyMatrix[i][j].getCausalityProbability();
//				causalFeaturesDependencyMatrix[i][j].setCausalityProbability(causalProb/(menCtr+womenCtr));
				causalFeaturesDependencyMatrix[i][j].setCausalityProbability(getCausalTendency(i, j));
				
				//set average time position
				sum = 0;
				list = causalFeaturesDependencyMatrix[i][j].getTimePositions();				
				for(int k=0; k<list.size(); k++){
					sum = sum + list.get(k);
				}//for(k)
				sum = sum / (list.size());
				causalFeaturesDependencyMatrix[i][j].setAvgTimePos(sum);
				
				//set average time distance
				sum = 0;
				list = causalFeaturesDependencyMatrix[i][j].getTimeDistances();				
				for(int k=0; k<list.size(); k++){
					sum = sum + list.get(k);
				}//for(k)
				sum = sum / (list.size());
				if(list.size() == 0)
					sum = 0;
				causalFeaturesDependencyMatrix[i][j].setAvgTimeDist(sum);
				
				//set average feature change
//				sum = 0;
//				list = causalFeaturesDependencyMatrix[i][j].getNextChanges();				
//				for(int k=0; k<list.size(); k++){
//					sum = sum + list.get(k);
//				}//for(k)
//				sum = sum / (list.size());
//				causalFeaturesDependencyMatrix[i][j].setAvgChange(sum);
				
				//set average age
				sum = 0;
				list = causalFeaturesDependencyMatrix[i][j].getAges();				
				for(int k=0; k<list.size(); k++){
					sum = sum + list.get(k);
				}//for(k)
				sum = sum / (list.size());
				if(list.size() == 0)
					sum = 0;
				causalFeaturesDependencyMatrix[i][j].setAvgAge(sum);
				
				//set average target change
//				sum = 0;
//				list = causalFeaturesDependencyMatrix[i][j].getTargetChanges();				
//				for(int k=0; k<list.size(); k++){
//					sum = sum + list.get(k);
//				}//for(k)
//				sum = sum / (list.size());
//				causalFeaturesDependencyMatrix[i][j].setAvgTargetChange(sum);
				
				//set death probability
				sum = 0;
				deathList = causalFeaturesDependencyMatrix[i][j].getDeathProbability();
				
				for(int k=0; k< deathList.size(); k++){
					if(deathList.get(k) != null)
						if(deathList.get(k).equalsIgnoreCase("yes"))
							sum = sum + 1;
				}//for(k)
				sum = sum / (deathList.size());
				if(list.size() == 0)
					sum = 0;
				causalFeaturesDependencyMatrix[i][j].setDeathProb(sum);
				
			}//for(j)
		}//for(i)
		
	}//normalizeDependencyMatrix

	//update dependency matrix based on each patient events change array
	private void updateDependencyMatrix(EventChange[] eventsChangeArray, String sex, String age, String death) {
		// TODO Auto-generated method stub
		ArrayList<Integer> nextFeatures = new ArrayList<Integer>();
		ArrayList<Integer> prevFeatures = new ArrayList<Integer>();
		ArrayList<Double> nextChangeVals = new ArrayList<Double>();
		ArrayList<Double> prevChangeVals = new ArrayList<Double>();
		EventChange prevEvent, nextEvent;
		double targetChange = 0;	//relative value of ALSFRS change
		double targetChangeAbs = 0;	//absolute value of ALSFRS change
		int patientEventsLength = eventsChangeArray.length;
		double firstEventTime = eventsChangeArray[0].getTime();
		double lastEventTime = eventsChangeArray[patientEventsLength-1].getTime();
		boolean maleFemale = false;
		
		ArrayList<MatIdx> matIdxs= new ArrayList<MatIdx>();
		MatIdx idx = null;
		for(int i=0; i<patientEventsLength-1; i++){	//compare each event with next event
			prevEvent = eventsChangeArray[i];
			nextEvent = eventsChangeArray[i+1];
			prevFeatures = prevEvent.getFeatures();
			prevChangeVals = prevEvent.getChangeVals();
			nextFeatures = nextEvent.getFeatures();
			nextChangeVals = nextEvent.getChangeVals();
			if(prevEvent == null || nextEvent == null || prevFeatures.size() == 0 || nextFeatures.size() == 0
					|| nextChangeVals.size() == 0 || age.equalsIgnoreCase(""))
				continue;
			for(int prev=0; prev<prevFeatures.size(); prev++){
				for(int next=0; next<nextFeatures.size(); next++){
					int row = prevFeatures.get(prev);
					int col = nextFeatures.get(next);
					
					causalFeaturesDependencyMatrix[row][col].getNextChanges().add(nextChangeVals.get(next));
					causalFeaturesDependencyMatrix[row][col].getPrevChanges().add(prevChangeVals.get(prev));
					causalFeaturesDependencyMatrix[row][col].getTimeDistances().add(nextEvent.getTime()-prevEvent.getTime());
					causalFeaturesDependencyMatrix[row][col].getAges().add(Double.parseDouble(age));
					causalFeaturesDependencyMatrix[row][col].getTimePositions().add((nextEvent.getTime()-firstEventTime)/(lastEventTime-firstEventTime));
					causalFeaturesDependencyMatrix[row][col].getDeathProbability().add(death);
					if(!idxExist(matIdxs, row, col)){
						double causalProb = causalFeaturesDependencyMatrix[row][col].getCausalityProbability();
						causalFeaturesDependencyMatrix[row][col].setCausalityProbability(causalProb+1);
						if(sex.equalsIgnoreCase("Male")){
							double malePerc = causalFeaturesDependencyMatrix[row][col].getMenPercent();
							causalFeaturesDependencyMatrix[row][col].setMenPercent(malePerc+1);
						}//if(sex)
						else if(sex.equalsIgnoreCase("Female")){
							double femalePerc = causalFeaturesDependencyMatrix[row][col].getWomenPercent();
							causalFeaturesDependencyMatrix[row][col].setWomenPercent(femalePerc+1);
						}//else if(sex)
						idx = new MatIdx(row, col);
						matIdxs.add(idx);
					}//if(!idxExist)
					
//					if(sex.equalsIgnoreCase("Male") && !(maleFemale && idxExist(matIdxs, row, col))){
//						double malePerc = causalFeaturesDependencyMatrix[row][col].getMenPercent();
//						causalFeaturesDependencyMatrix[row][col].setMenPercent(malePerc+1);
//						maleFemale = true;
//					}//if(sex)
//					else if(sex.equalsIgnoreCase("Female") && !(maleFemale && idxExist(matIdxs, row, col))){
//						double femalePerc = causalFeaturesDependencyMatrix[row][col].getWomenPercent();
//						causalFeaturesDependencyMatrix[row][col].setWomenPercent(femalePerc+1);
//						maleFemale = true;
//					}//else if(sex)
					targetChange = nextEvent.getTargetChange();
					targetChangeAbs = nextEvent.getTargetChangeAbs();
					causalFeaturesDependencyMatrix[row][col].getTargetChanges().add(targetChange);
					causalFeaturesDependencyMatrix[row][col].getTargetChangesAbs().add(targetChangeAbs);
				}//for(next)
			}//for(prev)
		}//for(i)
	}//updateDependencyMatrix
	
	private boolean idxExist(ArrayList<MatIdx> list, int row, int col){
		for(int i=0; i<list.size(); i++)
			if(list.get(i).getRow() == row && list.get(i).getCol() == col)
				return true;
		return false;
	}//idxExist
	
	private class MatIdx{
		int row = -1;
		int col = -1;
		
		MatIdx(int row, int col) {
			super();
			this.row = row;
			this.col = col;
		}
		int getRow() {
			return row;
		}
		void setRow(int row) {
			this.row = row;
		}
		int getCol() {
			return col;
		}
		void setCol(int col) {
			this.col = col;
		}
		
	}//class MatIdx

	//This class defines change of consecutive events
	private class EventChange{
		ArrayList<Integer> features = new ArrayList<Integer>();	//all features that has been changed
		double time = -1;	//time of current event
		ArrayList<Double> changeVals = new ArrayList<Double>();	//amount of feature change
	    double targetChange = 0;	//amount of relative ALS-FRS change
	    double targetChangeAbs = 0;	//absolute value of ALSFRS change
	    
	    
	    
		double getTargetChangeAbs() {
			return targetChangeAbs;
		}
		void setTargetChangeAbs(double targetChangeAbs) {
			this.targetChangeAbs = targetChangeAbs;
		}
		double getTargetChange() {
			return targetChange;
		}
		void setTargetChange(double targetChange) {
			this.targetChange = targetChange;
		}
		ArrayList<Integer> getFeatures() {
			return features;
		}
		void setFeatures(ArrayList<Integer> features) {
			this.features = features;
		}
		double getTime() {
			return time;
		}
		void setTime(double time) {
			this.time = time;
		}
		ArrayList<Double> getChangeVals() {
			return changeVals;
		}
		void setChangeVals(ArrayList<Double> changeVals) {
			this.changeVals = changeVals;
		}
		
	}//class EventChange
	
}//class MainALS


