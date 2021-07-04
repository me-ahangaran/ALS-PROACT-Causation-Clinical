package sdu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ALSPGMPredictionSDU {

	private double errorThresh = 1;//threshold of feature changes, for ex. if change is 0.4 and errorThresh = 0.5
	//then the interval is 0.4*0.5 = 0.2, so the range of changes is [0.4-0.2,0.4+0.2]=[0.2,0.6].
	private int numOfGivenEvents = 3;	//number of events as input sequence
	private int numOfPredictEvents = 9;	//number of events as output sequence for prediction
//	private ArrayList<PredictEvent[]> patientsPredictEventSeq = new ArrayList<ALSPrediction.PredictEvent[]>();	//list of arrays of predict events for all patients test data
	ArrayList<PredictEvent[]> patientsPrediction = new ArrayList<ALSPGMPredictionSDU.PredictEvent[]>();//list of all prediction events of all patients
	
	public void predict(ArrayList<ALSPatientSDU> patients, ArrayList<ALSPGMMatrixSDU> pgm, ArrayList<Integer> pgmFeatures,
			String[][] featuresNumNameChange){
		//featuresNumNameChange: number, name, change
		int numOfPatients = patients.size();//size of patients test data
		ALSUtilsSDU util = new ALSUtilsSDU();
		FeatureValueSDU fv = new FeatureValueSDU();//for calculate the double value of features
		//loop for each patient sequence
		for(int i=0; i<numOfPatients; i++){
			ALSPatientSDU currPatient = patients.get(i);//current patient
			String patientId = currPatient.getId();//patient identifier
			ArrayList<ALSEventSDU> currEventSeq = currPatient.getPatientSequence();//sequence of current patient
			int currPatientEventsSize = currEventSeq.size();
			ALSEventSDU prevEvent;
			ALSEventSDU currEvent;			
			double firstDelta;
			currEvent = currEventSeq.get(0);
			firstDelta = currEvent.getDelta();
			
			if(currPatientEventsSize <= numOfGivenEvents){
				PredictEvent[] nullPredictEventSeq = new PredictEvent[0];//null predict event sequence array
				patientsPrediction.add(nullPredictEventSeq);
				continue;
			}//if
			PredictEvent[] currPatientPredictEventSeq = new PredictEvent[currPatientEventsSize];//event seq of current patient for prediction			
			
//			double prevDelta;
			
			//ALS-FRS
			ALSFRSSDU currFrs;
			double currTarget;
			
			ALSFRSSDU prevFrs;
			double prevTarget;
			
			//Vital Sign
			VitalSignSDU currVS;
			double currPulse =0;
			double currRespRate=0;
			double currTemp=0;
			double currWeight=0;
			double currBldPrsDias=0;
			double currBldPrsSyst=0;
			double currFVC=0;
			double currSVC=0;
			
			VitalSignSDU prevVS;
			double prevPulse=0;
			double prevRespRate=0;
			double prevTemp=0;
			double prevWeight=0;
			double prevBldPrsDias=0;
			double prevBldPrsSyst=0;
			double prevFVC=0;
			double prevSVC=0;
			
			//Lab data
			LabDataSDU currLab;
			ArrayList<String> currTestNames;
			ArrayList<String> currTestResults;
			
			LabDataSDU prevLab;
			ArrayList<String> prevTestNames;
			ArrayList<String> prevTestResults;
			
			//list of features of each event
			ArrayList<Integer> featuresNumFirst = new ArrayList<Integer>();
			ArrayList<Double> featuresChangeFirst = new ArrayList<Double>();
			ArrayList<Double> featuresValFirst = new ArrayList<Double>();
			ArrayList<Integer> feautreCauseNum = new ArrayList<Integer>();//cause feature
			ArrayList<Double> featureCauseChange = new ArrayList<Double>();//change value of cause feature
			ArrayList<Double> causalEntropyRatio = new ArrayList<Double>();//CausalTendency/Entropy ratio for each causal edge
			double targetChange = 0;//change value of target at current event
//			double targetVal = 0;//value of target(als-frs) at current event
			PredictEvent peFirst = new PredictEvent();
			////////////////////////////////////////////////////////////////////
			//for first event
			 
//			 if(currEvent == null){
//				 PredictEvent[] nullPredictEventSeq = new PredictEvent[0];//null predict event sequence array
//				 patientsPrediction.add(nullPredictEventSeq);
//				 continue;
//			 }
			//ALS-FRS
			 currFrs = currEvent.getTarget();
			 currTarget = currFrs.getALSFRSTotal();
			
			//Vital Sign
			 currVS = currEvent.getVitalSign();
			 
			 if(currVS != null){
				 currPulse = currVS.getPulse();
				 currRespRate = currVS.getRespiratoryRate();
				 currTemp = currVS.getTemprature();
				 currWeight = currVS.getWeight();
				 currBldPrsDias = currVS.getBloodPressureDiastolic();
				 currBldPrsSyst = currVS.getBloodPressureSystolic();
			 }//if
			 
			 currFVC = currEvent.getFVC_subjectLiters();
			 currSVC = currEvent.getSVC_subjectLiters();
			
			//Lab data
			 currLab = currEvent.getLab();
			 if(currLab == null){
				 PredictEvent nullPe = new PredictEvent();
				 nullPe.setTargetVal(currTarget);
				 nullPe.setPatientId(patientId);
				 nullPe.setDelta(firstDelta);
				 currPatientPredictEventSeq[0] = nullPe;
			 }//if
			 
			 else{	//current lab is not null
			 
				 currTestNames = currLab.getTestNames();
				 currTestResults = currLab.getTestResults();					 
				 
				 if(pgmFeatures.contains(util.getFeatureNum("pulse", featuresNumNameChange))){
					featuresNumFirst.add(util.getFeatureNum("pulse", featuresNumNameChange));
					featuresValFirst.add(currPulse);
				 }//if(pgmFeatures)
				 if(pgmFeatures.contains(util.getFeatureNum("respiratoryRate", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("respiratoryRate", featuresNumNameChange));
						featuresValFirst.add(currRespRate);	
				 }//if(pgmFeatures)
				 if(pgmFeatures.contains(util.getFeatureNum("temperature", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("temperature", featuresNumNameChange));
						featuresValFirst.add(currTemp);
				 }//if(pgmFeatures)
				 if(pgmFeatures.contains(util.getFeatureNum("weight", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("weight", featuresNumNameChange));
						featuresValFirst.add(currWeight);
				 }//if(pgmFeatures)
				 if(pgmFeatures.contains(util.getFeatureNum("bloodPressureDiastolic", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("bloodPressureDiastolic", featuresNumNameChange));
						featuresValFirst.add(currBldPrsDias);	
				 }//if(pgmFeatures)
				 if(pgmFeatures.contains(util.getFeatureNum("bloodPressureSystolic", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("bloodPressureSystolic", featuresNumNameChange));
						featuresValFirst.add(currBldPrsSyst);
				 }//if(pgmFeatures)
				 if(pgmFeatures.contains(util.getFeatureNum("FVC_subjectLiters", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("FVC_subjectLiters", featuresNumNameChange));
						featuresValFirst.add(currFVC);
				 }//if(pgmFeatures)
				 if(pgmFeatures.contains(util.getFeatureNum("SVC_subjectLiters", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("SVC_subjectLiters", featuresNumNameChange));
						featuresValFirst.add(currSVC);
				 }//if(pgmFeatures)
				 
				 //add features of lab data based on PGM features
				 for(int s=0; s<currTestNames.size(); s++){
					 String testName = currTestNames.get(s);
					 int testNum = util.getFeatureNum(testName, featuresNumNameChange);
					 if(pgmFeatures.contains(testNum)){
						 featuresNumFirst.add(testNum);
						 featuresValFirst.add(fv.getFeatureValue(testName, currTestResults.get(s)));
					 }//if(pgmFeatures)
				 }//for(s)
//				 targetVal = currTarget;
				 //add current predict event to the list of predict events
				 
				 peFirst.setFeaturesNum(featuresNumFirst);
				 peFirst.setFeaturesVal(featuresValFirst);
				 peFirst.setTargetVal(currTarget);
				 peFirst.setPatientId(patientId);
				 peFirst.setDelta(firstDelta);
				 currPatientPredictEventSeq[0] = peFirst;
			 }//else
			/////////////////////////////////////////////////////////////////////
			//for given events
			for(int k=1; k<numOfGivenEvents; k++){
				
				ArrayList<Integer> featuresNumGiven = new ArrayList<Integer>();
				ArrayList<Double> featuresChangeGiven = new ArrayList<Double>();
				ArrayList<Double> featuresValGiven = new ArrayList<Double>();
//				ArrayList<Integer> feautreCauseNumGiven = new ArrayList<Integer>();//cause feature
//				ArrayList<Double> featureCauseChangeGiven = new ArrayList<Double>();//change value of cause feature
//				ArrayList<Double> causalEntropyRatioGiven = new ArrayList<Double>();//CausalTendency/Entropy ratio for each causal edge
				PredictEvent peGiven = new PredictEvent();
				prevEvent = currEventSeq.get(k-1);
				currEvent = currEventSeq.get(k);
				
				double currDelta = currEvent.getDelta();
				
//				double prevDelta = -1;
//				 if(prevEvent == null || currEvent == null){
//					 PredictEvent nullPe = new PredictEvent();
//					 nullPe.setTargetVal(currTarget);
//					 currPatientPredictEventSeq[k] = nullPe;
//				 }
//				 currDelta = currEvent.getDelta();
//				 prevDelta = prevEvent.getDelta();
				
				//ALS-FRS
				 currFrs = currEvent.getTarget();
				 currTarget = currFrs.getALSFRSTotal();
				
				 prevFrs = prevEvent.getTarget();
				 prevTarget = prevFrs.getALSFRSTotal();
				
				//Vital Sign
				 currVS = currEvent.getVitalSign();
				 if(currVS != null){
					 currPulse = currVS.getPulse();
					 currRespRate = currVS.getRespiratoryRate();
					 currTemp = currVS.getTemprature();
					 currWeight = currVS.getWeight();
					 currBldPrsDias = currVS.getBloodPressureDiastolic();
					 currBldPrsSyst = currVS.getBloodPressureSystolic();
				 }//if
				 
				 currFVC = currEvent.getFVC_subjectLiters();
				 currSVC = currEvent.getSVC_subjectLiters();
				
				 prevVS = prevEvent.getVitalSign();//////////////
				 if(prevVS !=null){
					 prevPulse = prevVS.getPulse();
					 prevRespRate = prevVS.getRespiratoryRate();
					 prevTemp = prevVS.getTemprature();
					 prevWeight = prevVS.getWeight();
					 prevBldPrsDias = prevVS.getBloodPressureDiastolic();
					 prevBldPrsSyst = prevVS.getBloodPressureSystolic();
				 }//if
				 
				 prevFVC = prevEvent.getFVC_subjectLiters();/////////////
				 prevSVC = prevEvent.getSVC_subjectLiters();//////////////////////
				
				//Lab data
				 currLab = currEvent.getLab();
				 prevLab = prevEvent.getLab();
				 
				 if(currLab == null || prevLab == null){
					 PredictEvent nullPe = new PredictEvent();
					 nullPe.setTargetVal(currTarget);
					 nullPe.setDelta(currDelta);
					 nullPe.setPatientId(patientId);
					 currPatientPredictEventSeq[k] = nullPe;
				 }//if
				 
				 else{//current lab is not null
				 
					 currTestNames = currLab.getTestNames();
					 currTestResults = currLab.getTestResults();					 
					
					 prevTestNames = prevLab.getTestNames();
					 prevTestResults = prevLab.getTestResults();
					 
					 //clear all features list(num, val, change)
//					 featuresNum.clear();
//					 featuresVal.clear();
//					 featuresChange.clear();
					 
					 //Features of Vital Sign
					 if(pgmFeatures.contains(util.getFeatureNum("pulse", featuresNumNameChange))){
						featuresNumGiven.add(util.getFeatureNum("pulse", featuresNumNameChange));
						featuresValGiven.add(currPulse);
						double pulseChange = (currPulse - prevPulse)/prevPulse;
						if(prevPulse == 0 || prevPulse == -1)
							pulseChange = 0;
						featuresChangeGiven.add(pulseChange);
					 }//if(pgmFeatures)
					 if(pgmFeatures.contains(util.getFeatureNum("respiratoryRate", featuresNumNameChange))){
							featuresNumGiven.add(util.getFeatureNum("respiratoryRate", featuresNumNameChange));
							featuresValGiven.add(currRespRate);	
							double respRateChange = (currRespRate - prevRespRate)/prevRespRate;
							if(prevRespRate == 0 || prevRespRate == -1)
								respRateChange = 0;
							featuresChangeGiven.add(respRateChange);
					 }//if(pgmFeatures)
					 if(pgmFeatures.contains(util.getFeatureNum("temperature", featuresNumNameChange))){
							featuresNumGiven.add(util.getFeatureNum("temperature", featuresNumNameChange));
							featuresValGiven.add(currTemp);
							double tempChange = (currTemp - prevTemp)/prevTemp;
							if(prevTemp == 0 || prevTemp == -1)
								tempChange = 0;
							featuresChangeGiven.add(tempChange);
					 }//if(pgmFeatures)
					 if(pgmFeatures.contains(util.getFeatureNum("weight", featuresNumNameChange))){
							featuresNumGiven.add(util.getFeatureNum("weight", featuresNumNameChange));
							featuresValGiven.add(currWeight);
							double weightChange = (currWeight - prevWeight)/prevWeight;
							if(prevWeight == 0 || prevWeight == -1)
								weightChange = 0;
							featuresChangeGiven.add(weightChange);
					 }//if(pgmFeatures)
					 if(pgmFeatures.contains(util.getFeatureNum("bloodPressureDiastolic", featuresNumNameChange))){
							featuresNumGiven.add(util.getFeatureNum("bloodPressureDiastolic", featuresNumNameChange));
							featuresValGiven.add(currBldPrsDias);	
							double bldPrsDiasChange = (currBldPrsDias - prevBldPrsDias)/prevBldPrsDias;
							if(prevBldPrsDias == 0 || prevBldPrsDias == -1)
								bldPrsDiasChange = 0;
							featuresChangeGiven.add(bldPrsDiasChange);
					 }//if(pgmFeatures)
					 if(pgmFeatures.contains(util.getFeatureNum("bloodPressureSystolic", featuresNumNameChange))){
							featuresNumGiven.add(util.getFeatureNum("bloodPressureSystolic", featuresNumNameChange));
							featuresValGiven.add(currBldPrsSyst);
							double bldPrsSystChange = (currBldPrsSyst - prevBldPrsSyst)/prevBldPrsSyst;
							if(prevBldPrsSyst == 0 || prevBldPrsSyst == -1)
								bldPrsSystChange = 0;
							featuresChangeGiven.add(bldPrsSystChange);
					 }//if(pgmFeatures)
					 if(pgmFeatures.contains(util.getFeatureNum("FVC_subjectLiters", featuresNumNameChange))){
							featuresNumGiven.add(util.getFeatureNum("FVC_subjectLiters", featuresNumNameChange));
							featuresValGiven.add(currFVC);
							double FVCChange = (currFVC - prevFVC)/prevFVC;
							if(prevFVC == 0 || prevFVC == -1)
								FVCChange = 0;
							featuresChangeGiven.add(FVCChange);
					 }//if(pgmFeatures)
					 if(pgmFeatures.contains(util.getFeatureNum("SVC_subjectLiters", featuresNumNameChange))){
							featuresNumGiven.add(util.getFeatureNum("SVC_subjectLiters", featuresNumNameChange));
							featuresValGiven.add(currSVC);
							double SVCChange = (currSVC - prevSVC)/prevSVC;
							if(prevSVC == 0 || prevSVC == -1)
								SVCChange = 0;
							featuresChangeGiven.add(SVCChange);
					 }//if(pgmFeatures)
					 
					 //add features of lab data based on PGM features
					 for(int s=0; s<currTestNames.size(); s++){
						 String testName = currTestNames.get(s);
						 int testNum = util.getFeatureNum(testName, featuresNumNameChange);
	
						 if(pgmFeatures.contains(testNum)){
							 featuresNumGiven.add(testNum);
							 featuresValGiven.add(fv.getFeatureValue(testName, currTestResults.get(s)));
							 double prevFeatureVal=0;
	//						 boolean prevFeatureIsExist = false;
							 for(int p=0; p<prevTestNames.size(); p++){
								 if(prevTestNames.get(p).equalsIgnoreCase(testName)){
									 prevFeatureVal = fv.getFeatureValue(testName, prevTestResults.get(p));
	//								 prevFeatureIsExist = true;
									 break;
								 }//if
							 }//for(int p)
	//						 if(prevFeatureIsExist){
							 double testChange = (fv.getFeatureValue(testName,currTestResults.get(s))- prevFeatureVal)/prevFeatureVal;
							 if(prevFeatureVal == 0)
								 testChange = fv.getFeatureValue(testName,currTestResults.get(s));
							 featuresChangeGiven.add(testChange);	 
	//						 }//if
	//						 featuresVal.add(Double.parseDouble(currTestResults.get(s)));
	//						 if(s >= prevTestResults.size()){
	//							 featuresChange.add(fv.getFeatureValue(testName, currTestResults.get(s)));
	//							 continue;
	//						 }//if
	//						 if(currTestResults.get(s).equalsIgnoreCase("Negative") || prevTestResults.get(s).equalsIgnoreCase("Negative"))
	//							 System.out.println("Negative value");
							 
						 }//if(pgmFeatures)
					 }//for(s)
	//				 targetVal = currTarget;
					 targetChange = (currTarget-prevTarget)/prevTarget;
					 if(prevTarget == 0)
						 targetChange = currTarget;
					 //add current predict event to the list of predict events
					 peGiven = new PredictEvent();
					 peGiven.setFeaturesNum(featuresNumGiven);
					 peGiven.setFeaturesVal(featuresValGiven);
					 peGiven.setFeaturesChange(featuresChangeGiven);
					 peGiven.setTargetVal(currTarget);
					 peGiven.setTargetChange(targetChange);
					 peGiven.setPatientId(patientId);
					 peGiven.setDelta(currDelta);
					 currPatientPredictEventSeq[k] = peGiven;
				}//else
			}//for(k)
			
			/////////////////////////////////////////////////////////////////////////////////////////////////
			//prediction of future events. in each event we predict the next event.
			for(int j=numOfGivenEvents-1; j<currPatientEventsSize-1; j++){
				PredictEvent nextPredictEvent = new PredictEvent();	//next prediction event object
				ArrayList<Integer> nextFeaturesNum = new ArrayList<Integer>();
				ArrayList<Double> nextFeaturesVal = new ArrayList<Double>();
				ArrayList<Double> nextFeaturesChange = new ArrayList<Double>();
				ArrayList<Integer> nextFeaturesCauseNum = new ArrayList<Integer>();
				ArrayList<Double> nextFeaturesCauseChange = new ArrayList<Double>();
				ArrayList<Double> nextCausalEntropyRatio = new ArrayList<Double>();
				ArrayList<Integer> nextLostFeaturesNum = new ArrayList<Integer>();
				ArrayList<Double> nextLostFeaturesVal = new ArrayList<Double>();
				ArrayList<Double> nextFeaturesTargetVal = new ArrayList<Double>();
				ArrayList<Double> nextFeaturesTargetChange = new ArrayList<Double>(); 
				ArrayList<Double> nextFeaturesTargetEntropy = new ArrayList<Double>();
				
				double nextDelta = currEventSeq.get(j+1).getDelta();					
						
				double nextTargetVal = 0;
				double nextTargetChange = 0;
				double nextTargetEntropy = 0;
				PredictEvent pePrev = currPatientPredictEventSeq[j];	//current patient predict event
//				if(pe == null)
//					continue;
				ArrayList<Integer> featuresNum = pePrev.getFeaturesNum();	//current event features number
				ArrayList<Double> featuresVal = pePrev.getFeaturesVal();	//current event features value
				ArrayList<Double> featuresChange = pePrev.getFeaturesChange();	//current event features change
				currTarget = pePrev.getTargetVal();
				targetChange = pePrev.getTargetChange();
				ArrayList<Integer> lostFeaturesNum = pePrev.getLostFeaturesNum();
				ArrayList<Double> lostFeaturesVal = pePrev.getLostFeaturesVal();
				
				if(featuresNum.size() == 0){
					 PredictEvent nullPe = new PredictEvent();
					 nullPe.setTargetVal(currTarget);
					 nullPe.setPatientId(patientId);
					 nullPe.setDelta(nextDelta);
					 currPatientPredictEventSeq[j+1] = nullPe;
				}//if
				
				else{//prev prediction object is not empty
					
					//for each feature in the current event we find effect feature
					for(int s=0; s<featuresNum.size(); s++){
						double fChangeError = Double.MAX_VALUE;	//error of feature change for compare with another pgm edges
						int pgmIdxSelEdge = -1;	//edge with min error of delta f for current feature
						int fNum = featuresNum.get(s);	//current feature number
						double fChange = featuresChange.get(s);	//current feature change
						ALSPGMMatrixSDU pgmIdx;
						double errorInterval = errorThresh*fChange;	//interval of feature change based on error threshold
						double upBndChg = fChange+errorInterval;//upper bound of feature change
						double lowBndChg = fChange-errorInterval;//lower bound of feature change
						//find index of PGM based on min different of delta f for current feature
						for(int t=0; t<pgm.size(); t++){
							pgmIdx = pgm.get(t);	//current pgm edge
							if(pgmIdx.getRow() == fNum){
								//find the edge with minimum difference of cause change
								if(Math.abs(pgmIdx.getPrevChange()-fChange) < fChangeError /*&&
										pgmIdx.getPrevChange()<=upBndChg && pgmIdx.getPrevChange()>=lowBndChg*/){//nearest feature change
									fChangeError = Math.abs(pgmIdx.getPrevChange()-fChange);
									pgmIdxSelEdge = t;
								}//if(Math.abs)
							}//if(pgmIdx)
						}//for(int t)
						
						//current feature is not cause of any other feature
						if(pgmIdxSelEdge == -1)
							continue;
						
						pgmIdx = pgm.get(pgmIdxSelEdge);	//pgm index with min error of feature change
						int effectFeature = pgmIdx.getCol();//predict effect feature based on PGM
						double effectChange = pgmIdx.getNextChange();//predict effect feature change based on PGM
						nextTargetChange = pgmIdx.getTargetChange();//predict target change based on PGM
						nextTargetVal = currTarget + (nextTargetChange*currTarget);
						nextTargetEntropy = pgmIdx.getEntropyTarget();
						nextFeaturesTargetVal.add(nextTargetVal);
						nextFeaturesTargetChange.add(nextTargetChange);
						nextFeaturesNum.add(effectFeature);	//add effect feature to the next event
						nextFeaturesChange.add(effectChange);
						nextFeaturesTargetEntropy.add(nextTargetEntropy);
						double effectVal = 0;
						boolean effectFeatureExist = false;
						//find next value of effect feature
						for(int k=0; k<featuresNum.size(); k++){
							if(featuresNum.get(k) == effectFeature){
								effectVal = featuresVal.get(k)+
										(effectChange*featuresVal.get(k));	//!!!!!!!!!false code!!!!!!!!!! it is modified.
								nextFeaturesVal.add(effectVal);	//add new value of effect features(changed value)
								effectFeatureExist = true;
								break;
							}//if(featuresNum)
						}//for(int k)
							//feature dose not exist in the current event
							if(!effectFeatureExist){
								int fIdx = lostFeaturesNum.indexOf(effectFeature);	//index of current effect feature the lost features list
								if(fIdx == -1){
									nextFeaturesVal.add(effectChange);//default value
								}//if(fIdx)
								else{
									double prevFeatureVal = lostFeaturesVal.get(fIdx);//prev value of lost feature
									effectVal = prevFeatureVal + (effectChange*prevFeatureVal);//next value of effect feature
									nextFeaturesVal.add(effectVal);
								}//else
							}//if(!effectFeatureExist)
						
						nextFeaturesCauseNum.add(fNum);	//add cause feature number
						nextFeaturesCauseChange.add(fChange);	//add change value of cause feature
						nextCausalEntropyRatio.add(pgmIdx.getCausalProb()/pgmIdx.getEntropyDensity());//add causal/entropy ratio
						
					}//for(int s)
					
					//TODO final
					//find and add lost feature in the next event
					for(int p=0; p<pgmFeatures.size(); p++){
						int pgmFeatureNum = pgmFeatures.get(p);
						if(nextFeaturesNum.contains(pgmFeatureNum))
							continue;
						nextLostFeaturesNum.add(pgmFeatureNum);	//add lost feature number to the list in the next event
						int fIdx = featuresNum.indexOf(pgmFeatureNum);//index of lost feature in the feaures list
						double fVal = 0;
						if(fIdx == -1){
							fIdx = lostFeaturesNum.indexOf(pgmFeatureNum);	//index of lost feature in the lost features list.
							if(fIdx == -1)
								nextLostFeaturesVal.add(0.0);//default value
							else{
								fVal = lostFeaturesVal.get(fIdx);	//value of lost feature					
								nextLostFeaturesVal.add(fVal);	//add lost feature value to the list in the next event
							}//else
						}//if
						else{
							fVal = featuresVal.get(fIdx);
							nextLostFeaturesVal.add(fVal);
						}//else
						 
					}//for(int p)
					
					//delete repeated features and keep feature with highest value of causal entropy parameter
					for(int a=0; a<pgmFeatures.size(); a++){
						int fNum = pgmFeatures.get(a);	//current feature number
						int fCtr = 0;//number of current feature in the next features list
						for(int b=0; b<nextFeaturesNum.size(); b++){
							if(nextFeaturesNum.get(b) == fNum)
								fCtr++;
						}//for(int b)
						
						//repeated feature
						if(fCtr>1){
							int firstIdx = nextFeaturesNum.indexOf(fNum);//first index of current repeated feature
							double causalEntVal = nextCausalEntropyRatio.get(firstIdx);//causal entropy value of first occurrence of current repeated feature 
							int maxCausalEntFeatureIdx = firstIdx;//index of feature with max causal entropy value
							ArrayList<Integer> deleteIdxs = new ArrayList<Integer>();
//							if(nextFeaturesTargetEntropy.size() != nextFeaturesNum.size())
//								System.out.println("size is different!");
							for(int c=firstIdx+1; c<nextFeaturesNum.size(); c++){
								causalEntVal = nextCausalEntropyRatio.get(maxCausalEntFeatureIdx);
								if(nextFeaturesNum.get(c) == fNum){
									if(nextCausalEntropyRatio.get(c) > causalEntVal){
										deleteIdxs.add(maxCausalEntFeatureIdx);
										maxCausalEntFeatureIdx = c;
									}//if
									else{
										deleteIdxs.add(c);
									}//else
									
									//remove prev repeated feature
//									if(nextCausalEntropyRatio.get(c) > causalEntVal){
//										nextFeaturesNum.remove(maxCausalEntFeatureIdx);
//										nextFeaturesVal.remove(maxCausalEntFeatureIdx);
//										nextFeaturesChange.remove(maxCausalEntFeatureIdx);
//										nextFeaturesCauseChange.remove(maxCausalEntFeatureIdx);
//										nextFeaturesCauseNum.remove(maxCausalEntFeatureIdx);
//										nextFeaturesTargetVal.remove(maxCausalEntFeatureIdx);
//										nextFeaturesTargetChange.remove(maxCausalEntFeatureIdx);
//										nextFeaturesTargetEntropy.remove(maxCausalEntFeatureIdx);
//										nextCausalEntropyRatio.remove(maxCausalEntFeatureIdx);
//										maxCausalEntFeatureIdx = c-1;
//									}//if(nextCausalEntropyRatio)
//									//remove current repeated feature
//									else{
//										nextFeaturesNum.remove(c);
//										nextFeaturesVal.remove(c);
//										nextFeaturesChange.remove(c);
//										nextFeaturesCauseChange.remove(c);
//										nextFeaturesCauseNum.remove(c);
//										nextFeaturesTargetVal.remove(c);
//										nextFeaturesTargetChange.remove(c);
//										nextFeaturesTargetEntropy.remove(c);
//										nextCausalEntropyRatio.remove(c);
//									}//else
								}//if(nextFeaturesNum)
							}//for(int c)
							
							for(int p=0; p<deleteIdxs.size(); p++){
									nextFeaturesNum.set(deleteIdxs.get(p), -1);
									nextFeaturesCauseNum.set(deleteIdxs.get(p), -1);
							}//for(int p)

							
						}//if(fCtr)
					}//for(int a)
	
					//find the target value of the next event based on min entropy of of target between current causal edges
					if(nextFeaturesTargetEntropy.size()!=0){
						int minTargetEntIdx = 0;
						double minTargetEnt = nextFeaturesTargetEntropy.get(0);
						for(int x=1; x<nextFeaturesTargetEntropy.size(); x++){
							if(nextFeaturesTargetEntropy.get(x) < minTargetEnt)
								minTargetEntIdx = x;
						}//for(int x)
						
						nextTargetChange = nextFeaturesTargetChange.get(minTargetEntIdx);
						nextTargetVal = nextFeaturesTargetVal.get(minTargetEntIdx);
					}
					
					//create predict event object and add it to the current patient predict event sequence
					nextPredictEvent.setCausalEntropyRatio(nextCausalEntropyRatio);
					nextPredictEvent.setFeatureCauseChange(nextFeaturesCauseChange);
					nextPredictEvent.setFeautreCauseNum(nextFeaturesCauseNum);
					nextPredictEvent.setFeaturesChange(nextFeaturesChange);
					nextPredictEvent.setFeaturesVal(nextFeaturesVal);
					nextPredictEvent.setFeaturesNum(nextFeaturesNum);
					nextPredictEvent.setTargetChange(nextTargetChange);
					nextPredictEvent.setTargetVal(nextTargetVal);
					nextPredictEvent.setLostFeaturesNum(nextLostFeaturesNum);
					nextPredictEvent.setLostFeaturesVal(nextLostFeaturesVal);
					nextPredictEvent.setFeaturesTargetChange(nextFeaturesTargetChange);
					nextPredictEvent.setFeaturesTargetVal(nextFeaturesTargetVal);
					nextPredictEvent.setPatientId(patientId);
					nextPredictEvent.setDelta(nextDelta);
					
					currPatientPredictEventSeq[j+1] = nextPredictEvent;	//add current predict event to the current patient predict sequence
				}//else
			}//for(j)
			//add prediction sequence of current patient to the list of prediction sequences of patients
			patientsPrediction.add(currPatientPredictEventSeq);//add current patient prediction to the list of all predictions 
			
			if(i%100 == 0)
				System.out.println("prediction of patient "+i+" is finished.");
		}//for(i)
		System.out.println();
		System.out.println("ALS prediction has been finished.");
		getTargetPredictError(patients, pgm);
		getFeaturesPredictError(patients, pgm, pgmFeatures, featuresNumNameChange);
		getCausalPredictError(patients, pgm, pgmFeatures, featuresNumNameChange);
	}//predict
	
	private double[] getTargetPredictError(ArrayList<ALSPatientSDU> patients, ArrayList<ALSPGMMatrixSDU> pgm){
		
		int numOfPatients = patients.size();
		RMSD[] rmsd = new RMSD[numOfPatients];	//root mean square error array for all patients
		double slopeReal, slopePredict;	//value of slope = (ALSFRS(m2) - ALSFRS(m1))/(m2 - m1)
		ArrayList<Double> realALSFRS;
		ArrayList<Double> predictedALSFRS;
		double[] answer = new double[numOfPredictEvents];
		double[][] targetErrorList = new double[numOfPatients][numOfPredictEvents];
		boolean [][] targetErrorListFlags = new boolean[numOfPatients][numOfPredictEvents];
		//initialize target error list of flags
				for(int i=0; i<numOfPatients; i++){
					for(int j=0; j<numOfPredictEvents; j++){
						targetErrorListFlags[i][j] = true;
					}//for(j)
				}//for(i)
		for(int i=0; i<numOfPatients; i++){
			RMSD currRMSD = new RMSD();
			realALSFRS = new ArrayList<Double>();
			predictedALSFRS = new ArrayList<Double>();
			ALSPatientSDU patient = patients.get(i);
			ArrayList<ALSEventSDU> sequence = patient.getPatientSequence();
			PredictEvent[] predict = patientsPrediction.get(i);
			for(int j=0; j<numOfPredictEvents; j++){
				if(j+numOfGivenEvents>=predict.length){
					targetErrorList[i][j] = 0;
					targetErrorListFlags[i][j] = false;
					continue;
				}//if
				PredictEvent pe = predict[j+numOfGivenEvents];
				ALSEventSDU event = sequence.get(j+numOfGivenEvents);
				ALSFRSSDU frs = event.getTarget();
				double targetPredict = pe.getTargetVal();//target prediction
				double targetRealVal = frs.getALSFRSTotal();//real value of target
				if(targetPredict == 0 || targetRealVal == 0){
					targetErrorList[i][j] = 0;
					targetErrorListFlags[i][j] = false;
					continue;
				}//if
				predictedALSFRS.add(targetPredict);
				realALSFRS.add(targetRealVal);
				double targetErr = Math.abs((targetRealVal-targetPredict)/targetRealVal);//error of target prediction
				targetErrorList[i][j] = targetErr;
			}//for(int j)
			currRMSD.setPredictedALSFRS(predictedALSFRS);
			currRMSD.setRealALSFRS(realALSFRS);
			rmsd[i] = currRMSD;
		}//for(int i)
		
		double rmsdValueFinal = calculateRMSD(rmsd);
		System.out.println("RMSD value is "+rmsdValueFinal);

		for(int k=0; k<numOfPredictEvents; k++){
			double[] avgListIdx = new double[numOfPatients];
			int ctr = 0;
			for(int s=0; s<numOfPatients; s++){
				avgListIdx[s] = targetErrorList[s][k];
				if(targetErrorListFlags[s][k] == true)
					ctr++;
			}//for(int s)
			double sum = 0;
			for(int i=0; i<avgListIdx.length; i++){
				sum = sum + avgListIdx[i];
			}//for(int i)
			answer[k] = sum/ctr;
		}//for(int k)
		System.out.println("Calculate the target error value successfully.");
		File file = new File("E:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\PGM-Output\\PredictTargetError.txt");
		try {
			PrintWriter pw = new PrintWriter(file);
			for(int i=0; i<answer.length; i++){
				pw.printf("%.2f",answer[i]);
				pw.print(",");
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return answer;
	}//getTargetPredictError
	
	private double calculateRMSD(RMSD[] rmsd){
		double realSlope, predictedSlope;
		double sigma = 0;
		ArrayList<Double> realALSFRS = new ArrayList<Double>();
		ArrayList<Double> predictedALSFRS = new ArrayList<Double>();
		int ctr = 0;	//counter of patients for prediction
		
		for(int i=0; i<rmsd.length; i++){
			RMSD currRMSD = rmsd[i];
			realALSFRS = currRMSD.getRealALSFRS();
			predictedALSFRS = currRMSD.getPredictedALSFRS();
			int listFRSSize = realALSFRS.size();
//			if(realALSFRS.size() == 0 || predictedALSFRS.size() == 0)
			//only consider patients with specified length of prediction
			if(realALSFRS.size() < 6 || predictedALSFRS.size() < 6)
				continue;
			ctr++;
			realSlope = (realALSFRS.get(listFRSSize-1) - realALSFRS.get(0)) / (listFRSSize-1);
			predictedSlope = (predictedALSFRS.get(listFRSSize-1) - predictedALSFRS.get(0)) / (listFRSSize-1);
			
			double diff = Math.pow(Math.abs(realSlope-predictedSlope), 2);
			sigma = sigma + diff;
		}//for(i)
		sigma = sigma / rmsd.length;
		double answer = Math.sqrt(sigma);
		System.out.println("Number of patients for RMSD = "+ctr);
		return answer;
		
	}//calculateRMSD
	
	private class RMSD{
		ArrayList<Double> realALSFRS = new ArrayList<Double>();
		ArrayList<Double> predictedALSFRS = new ArrayList<Double>();
		
		public ArrayList<Double> getRealALSFRS() {
			return realALSFRS;
		}
		public void setRealALSFRS(ArrayList<Double> realALSFRS) {
			this.realALSFRS = realALSFRS;
		}
		public ArrayList<Double> getPredictedALSFRS() {
			return predictedALSFRS;
		}
		public void setPredictedALSFRS(ArrayList<Double> predictedALSFRS) {
			this.predictedALSFRS = predictedALSFRS;
		}
		
	}//class RMSD
	
	private double[][] getFeaturesPredictError(ArrayList<ALSPatientSDU> patients, ArrayList<ALSPGMMatrixSDU> pgm, ArrayList<Integer> pgmFeatures
			,String[][] featuresNumNameChange){
		int numOfPatients = patients.size();
		int numOfFeatures = pgmFeatures.size();
		double[][] answer = new double[numOfFeatures][numOfPredictEvents];
//		double[][] featureErrorList = new double[numOfPatients][numOfPredictEvents];
		FeatureErrorList [][] featuresErrorList = new FeatureErrorList[numOfFeatures][numOfPredictEvents];	//list of all features error list in each event
		ALSUtilsSDU util = new ALSUtilsSDU();
		//initialize the list
		for(int i=0; i<numOfFeatures; i++){
			for(int j=0; j<numOfPredictEvents; j++){
				ArrayList<Double> featureErrList = new ArrayList<Double>();
				FeatureErrorList fel = new FeatureErrorList(featureErrList);
				featuresErrorList[i][j] = fel;
			}//for(int j)
		}//for(int i)

		for(int i=0; i<numOfPatients; i++){
			ALSPatientSDU patient = patients.get(i);
			ArrayList<ALSEventSDU> sequence = patient.getPatientSequence();
			PredictEvent[] predict = patientsPrediction.get(i);
			for(int j=0; j<numOfPredictEvents; j++){
				if(j+numOfGivenEvents>=predict.length)
					continue;
				
				PredictEvent pe = predict[j+numOfGivenEvents];
				ALSEventSDU event = sequence.get(j+numOfGivenEvents);
				VitalSignSDU vs = event.getVitalSign();
				LabDataSDU lab = event.getLab();
				if(lab == null)
					continue;
				ArrayList<Integer> featuresNum = pe.getFeaturesNum();
				ArrayList<Double> featuresVal = pe.getFeaturesVal();
				ArrayList<String> testNamesReal = lab.getTestNames();
				ArrayList<String> testValsReal = lab.getTestResults();
				ArrayList<Integer> lostFeaturesNum = pe.getLostFeaturesNum();
				ArrayList<Double> lostFeaturesVal = pe.getLostFeaturesVal();
				//for each feature in the current prediction we calculate the error value
				for(int k=0; k<numOfFeatures; k++){
					int featureNum = pgmFeatures.get(k);	//current feature number
					//current feature in the PGM does not exist in the current prediction an so we skip it
					if(!featuresNum.contains(featureNum) && !lostFeaturesNum.contains(featureNum))
						continue;		
					int fIdx = featuresNum.indexOf(featureNum);
					if(fIdx == -1)//this feature exist in the lost features
						fIdx = lostFeaturesNum.indexOf(featureNum);
					double featureVal = 0;
					if(featuresNum.contains(featureNum))
						featureVal = featuresVal.get(fIdx);	//current prediction feature value
					else
						featureVal = lostFeaturesVal.get(fIdx);//current prediction feature value from lost features
					double featureValReal = 0;
					if(featureNum<=8){//vital signs features
						if(vs == null)
							continue;
						if(featureNum == 1)
							featureValReal = vs.getPulse();
						if(featureNum == 2)
							featureValReal = vs.getRespiratoryRate();
						if(featureNum == 3)
							featureValReal = vs.getTemprature();
						if(featureNum == 4)
							featureValReal = vs.getWeight();
						if(featureNum == 5)
							featureValReal = vs.getBloodPressureDiastolic();
						if(featureNum == 6)
							featureValReal = vs.getBloodPressureSystolic();
						if(featureNum == 7)
							featureValReal = event.getFVC_subjectLiters();
						if(featureNum == 8)
							featureValReal = event.getSVC_subjectLiters();
					}//if
					else
						featureValReal = util.getTestVal(testNamesReal, testValsReal, featureNum, featuresNumNameChange);//current test real value
					double featureError = Math.abs((featureVal - featureValReal)/featureValReal);	//current feature error
					if(featureValReal == 0)
						featureError = 0;
					featuresErrorList[k][j].getFeatureErrorList().add(featureError);
				}//for(int k)
			}//for(int j)
		}//for(int i)
		
		for(int k=0; k<numOfPredictEvents; k++){
			for(int s=0; s<numOfFeatures; s++){
				ArrayList<Double> featureErrList = featuresErrorList[s][k].getFeatureErrorList();
				double sum = 0;
				for(int t=0; t<featureErrList.size(); t++){
					sum = sum + featureErrList.get(t);
				}//for(int t)
				sum = sum / featureErrList.size();
				if(featureErrList.size() == 0)
					sum = -1;///////////////////////
				answer[s][k] = sum;
			}//for(int s)
		}//for(int k)
		System.out.println("Calculate the features prediction error successfully.");
		File file = new File("E:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\PGM-Output\\PredictFeaturesError.csv");
		try {
			PrintWriter pw = new PrintWriter(file);
			for(int i=0; i<answer.length; i++){
				for(int j=0; j<answer[i].length; j++){
					pw.printf("%.2f",answer[i][j]);
					pw.print(",");
				}//for(int j)
				pw.println();
			}//for(int i)
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return answer;
	}//getFeaturesPredictError
	
	
	private double[][] getCausalPredictError(ArrayList<ALSPatientSDU> patients, ArrayList<ALSPGMMatrixSDU> pgm, ArrayList<Integer> pgmFeatures
			,String[][] featuresNumNameChange){
		int numOfPatients = patients.size();
		int numOfFeatures = pgmFeatures.size();
		int numOfCausalLinks = pgm.size();
		double[][] answer = new double[numOfCausalLinks][numOfPredictEvents];
//		double[][] featureErrorList = new double[numOfPatients][numOfPredictEvents];
		FeatureErrorList [][] featuresErrorList = new FeatureErrorList[numOfCausalLinks][numOfPredictEvents];	//list of all features error list in each event
		ALSUtilsSDU util = new ALSUtilsSDU();
		//initialize the list
		for(int i=0; i<numOfCausalLinks; i++){
			for(int j=0; j<numOfPredictEvents; j++){
				ArrayList<Double> featureErrList = new ArrayList<Double>();
				FeatureErrorList fel = new FeatureErrorList(featureErrList);
				featuresErrorList[i][j] = fel;
			}//for(int j)
		}//for(int i)

		for(int i=0; i<numOfPatients; i++){
			ALSPatientSDU patient = patients.get(i);
			ArrayList<ALSEventSDU> sequence = patient.getPatientSequence();
			PredictEvent[] predict = patientsPrediction.get(i);
			for(int j=0; j<numOfPredictEvents; j++){
				if(j+numOfGivenEvents>=predict.length)
					continue;
				
				PredictEvent pe = predict[j+numOfGivenEvents];
				ALSEventSDU event = sequence.get(j+numOfGivenEvents);
				VitalSignSDU vs = event.getVitalSign();
				LabDataSDU lab = event.getLab();
				if(lab == null)
					continue;
				ArrayList<Integer> featuresNum = pe.getFeaturesNum();
				ArrayList<Double> featuresVal = pe.getFeaturesVal();
				ArrayList<Integer> featuresCauseNum = pe.getFeautreCauseNum();
				ArrayList<Integer> lostFeaturesNum = pe.getLostFeaturesNum();
				ArrayList<Double> lostFeaturesVal = pe.getLostFeaturesVal();
				ArrayList<String> testNamesReal = lab.getTestNames();
				ArrayList<String> testValsReal = lab.getTestResults();
				//for each feature in the current prediction we calculate the error value
				for(int k=0; k<numOfCausalLinks; k++){
					int featureCauseNum = pgm.get(k).getRow();	//current cause feature number
					int featureEffectNum = pgm.get(k).getCol();	//current effect feature number
					///////////////////////////////////
//					if(!featuresNum.contains(featureNum) && !lostFeaturesNum.contains(featureNum))
//						continue;		
//					int fIdx = featuresNum.indexOf(featureNum);
//					if(fIdx == -1)//this feature exist in the lost features
//						fIdx = lostFeaturesNum.indexOf(featureNum);
//					double featureVal = 0;
//					if(featuresNum.contains(featureNum))
//						featureVal = featuresVal.get(fIdx);	//current prediction feature value
//					else
//						featureVal = lostFeaturesVal.get(fIdx);//current prediction feature value from lost features
					//////////////////////////////////
					//current feature in the PGM does not exist in the current prediction an so we skip it
					if(!featuresNum.contains(featureEffectNum))
						continue;		
					int fIdx = featuresNum.indexOf(featureEffectNum);
					int fCauseNum = featuresCauseNum.get(fIdx);
					if(fCauseNum != featureCauseNum)//cause feature is not current cause feature
						continue;
					double featureVal = featuresVal.get(fIdx);	//current prediction feature value
					double featureValReal = 0;
					if(featureEffectNum<=8){//vital signs features
						if(vs == null)
							continue;
						if(featureEffectNum == 1)
							featureValReal = vs.getPulse();
						if(featureEffectNum == 2)
							featureValReal = vs.getRespiratoryRate();
						if(featureEffectNum == 3)
							featureValReal = vs.getTemprature();
						if(featureEffectNum == 4)
							featureValReal = vs.getWeight();
						if(featureEffectNum == 5)
							featureValReal = vs.getBloodPressureDiastolic();
						if(featureEffectNum == 6)
							featureValReal = vs.getBloodPressureSystolic();
						if(featureEffectNum == 7)
							featureValReal = event.getFVC_subjectLiters();
						if(featureEffectNum == 8)
							featureValReal = event.getSVC_subjectLiters();
					}//if
					else//lab feature
						featureValReal = util.getTestVal(testNamesReal,testValsReal, featureEffectNum, featuresNumNameChange);//current test real value
					double featureError = Math.abs((featureVal - featureValReal)/featureValReal);	//current feature error
					if(featureValReal == 0)
						featureError = 0;
					featuresErrorList[k][j].getFeatureErrorList().add(featureError);
				}//for(int k)
			}//for(int j)
		}//for(int i)
		
		for(int k=0; k<numOfPredictEvents; k++){
			for(int s=0; s<numOfCausalLinks; s++){
				ArrayList<Double> featureErrList = featuresErrorList[s][k].getFeatureErrorList();
				double sum = 0;
				for(int t=0; t<featureErrList.size(); t++){
					sum = sum + featureErrList.get(t);
				}//for(int t)
				sum = sum / featureErrList.size();
				if(featureErrList.size() == 0)
					sum = -1;//////////////////////
				answer[s][k] = sum;
			}//for(int s)
		}//for(int k)
		System.out.println("Calculate the causal links prediction error successfully.");
		File file = new File("E:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\PGM-Output\\PredictCausalError.csv");
		try {
			PrintWriter pw = new PrintWriter(file);
			for(int i=0; i<answer.length; i++){
				for(int j=0; j<answer[i].length; j++){
					pw.printf("%.2f",answer[i][j]);
					pw.print(",");
				}//for(int j)
				pw.println();
			}//for(int i)
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return answer;
	}//getCausalPredictError
	
	private class FeatureErrorList{
		ArrayList<Double> featureErrorList = new ArrayList<Double>();

		FeatureErrorList(ArrayList<Double> featureErrorList) {
			super();
			this.featureErrorList = featureErrorList;
		}

		ArrayList<Double> getFeatureErrorList() {
			return featureErrorList;
		}

		void setFeatureErrorList(ArrayList<Double> featureErrorList) {
			this.featureErrorList = featureErrorList;
		}
		
	}//class FeatureErrorList
	
	
//	private double[][] getCausalLinksPredictError(ArrayList<ALSPatient> patients, ArrayList<ALSPGMMatrix> pgm, ArrayList<Integer> pgmFeatures){
//		
//	}//getCausalLinksPredictError
	
	//specifies all prediction events
	private class PredictEvent{
		ArrayList<Integer> featuresNum = new ArrayList<Integer>();
		ArrayList<Double> featuresChange = new ArrayList<Double>();
		ArrayList<Double> featuresVal = new ArrayList<Double>();
		ArrayList<Integer> feautreCauseNum = new ArrayList<Integer>();//cause feature
		ArrayList<Double> featureCauseChange = new ArrayList<Double>();//change value of cause feature
		ArrayList<Double> causalEntropyRatio = new ArrayList<Double>();//CausalTendency/Entropy ratio for each causal edge
		ArrayList<Integer> lostFeaturesNum = new ArrayList<Integer>();//lost features number that dose not exist in the current event
		ArrayList<Double> lostFeaturesVal = new ArrayList<Double>();//lost features value
		double targetChange = -1;//change value of target at current event
		double targetVal = -1;//value of target(als-frs) at current event
		ArrayList<Double> featuresTargetChange = new ArrayList<Double>();
		ArrayList<Double> featuresTargetVal = new ArrayList<Double>();
		ArrayList<Double> featuresTargetEntropy = new ArrayList<Double>();//list of entropy of target of current causal edge
		String patientId = null;
		double delta = -1;
		
		
		public double getDelta() {
			return delta;
		}
		public void setDelta(double delta) {
			this.delta = delta;
		}
		public String getPatientId() {
			return patientId;
		}
		public void setPatientId(String patientId) {
			this.patientId = patientId;
		}
		public ArrayList<Double> getFeaturesTargetEntropy() {
			return featuresTargetEntropy;
		}
		public void setFeaturesTargetEntropy(ArrayList<Double> featuresTargetEntropy) {
			this.featuresTargetEntropy = featuresTargetEntropy;
		}
		public ArrayList<Double> getFeaturesTargetChange() {
			return featuresTargetChange;
		}
		public void setFeaturesTargetChange(ArrayList<Double> featuresTargetChange) {
			this.featuresTargetChange = featuresTargetChange;
		}
		public ArrayList<Double> getFeaturesTargetVal() {
			return featuresTargetVal;
		}
		public void setFeaturesTargetVal(ArrayList<Double> featuresTargetVal) {
			this.featuresTargetVal = featuresTargetVal;
		}
		public ArrayList<Integer> getLostFeaturesNum() {
			return lostFeaturesNum;
		}
		public void setLostFeaturesNum(ArrayList<Integer> lostFeaturesNum) {
			this.lostFeaturesNum = lostFeaturesNum;
		}
		public ArrayList<Double> getLostFeaturesVal() {
			return lostFeaturesVal;
		}
		public void setLostFeaturesVal(ArrayList<Double> lostFeaturesVal) {
			this.lostFeaturesVal = lostFeaturesVal;
		}
		
		public ArrayList<Integer> getFeaturesNum() {
			return featuresNum;
		}
		public void setFeaturesNum(ArrayList<Integer> featuresNum) {
			this.featuresNum = featuresNum;
		}
		public ArrayList<Double> getFeaturesChange() {
			return featuresChange;
		}
		public void setFeaturesChange(ArrayList<Double> featuresChange) {
			this.featuresChange = featuresChange;
		}
		public ArrayList<Double> getFeaturesVal() {
			return featuresVal;
		}
		public void setFeaturesVal(ArrayList<Double> featuresVal) {
			this.featuresVal = featuresVal;
		}
		public ArrayList<Integer> getFeautreCauseNum() {
			return feautreCauseNum;
		}
		public void setFeautreCauseNum(ArrayList<Integer> feautreCauseNum) {
			this.feautreCauseNum = feautreCauseNum;
		}
		public ArrayList<Double> getFeatureCauseChange() {
			return featureCauseChange;
		}
		public void setFeatureCauseChange(ArrayList<Double> featureCauseChange) {
			this.featureCauseChange = featureCauseChange;
		}
		public ArrayList<Double> getCausalEntropyRatio() {
			return causalEntropyRatio;
		}
		public void setCausalEntropyRatio(ArrayList<Double> causalEntropyRatio) {
			this.causalEntropyRatio = causalEntropyRatio;
		}
		public double getTargetChange() {
			return targetChange;
		}
		public void setTargetChange(double targetChange) {
			this.targetChange = targetChange;
		}
		public double getTargetVal() {
			return targetVal;
		}
		public void setTargetVal(double targetVal) {
			this.targetVal = targetVal;
		}
				
	}//inner class PredictEvent
}//class ALSPrediction
