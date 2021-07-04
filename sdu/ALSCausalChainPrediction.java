package sdu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class ALSCausalChainPrediction {
	
	ArrayList<PredictEventCausalChain[]> patientsPrediction = new ArrayList<ALSCausalChainPrediction.
			PredictEventCausalChain[]>();//list of all prediction events of all patients
	ArrayList<ALSPatientSDU> patientsList = new ArrayList<ALSPatientSDU>();
	private double errorThresh = 1;//threshold of feature changes, for ex. if change is 0.4 and errorThresh = 0.5
	//then the interval is 0.4*0.5 = 0.2, so the range of changes is [0.4-0.2,0.4+0.2]=[0.2,0.6].
	private int numOfPredictEvents = 9;//number of time points for prediction
	private int numOfGivenEvents = 3;	//number of given events
	//According to output causal chains predict the ALS progression rate and effect features
	public void predictCausalChain(ArrayList<CausalChainSDU> causalChains, ArrayList<ALSPatientSDU> patients,
			ArrayList<Integer> causalChainsFeatures){
		int[] ccIdxUsedForPredFeatures = new int[causalChains.size()];
		patientsList = patients;
		int numOfPatients = patients.size();//number of patients
		ALSUtilsSDU util = new ALSUtilsSDU();
		String[][] featuresNumNameChange = util.getALSFeaturesNumNameChange();
		FeatureValueSDU fv = new FeatureValueSDU();//for calculate the double value of features		
		
		for(int i=0; i<numOfPatients; i++){
			ALSPatientSDU currPatient = patients.get(i);//current patient
			String patientId = currPatient.getId();//patient identifier
			ArrayList<ALSEventSDU> currEventSeq = currPatient.getPatientSequence();//sequence of current patient
			int currPatientEventsSize = currEventSeq.size();
			if(currPatientEventsSize <= 1){	//we should have at least 2 events in each sequence of patient for prediction
				PredictEventCausalChain[] nullPredictEventSeq = new PredictEventCausalChain[0];//null predict event sequence array
				patientsPrediction.add(nullPredictEventSeq);
				continue;
			}//if(currPatientEventsSize <= 1)
			PredictEventCausalChain[] currPatientPredictEventSeq = new PredictEventCausalChain[currPatientEventsSize];//event seq of current patient for prediction			
			ALSEventSDU prevEvent;
			ALSEventSDU currEvent;
			
			double secondDelta;
			double firstDelta;
			
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
//			ArrayList<Double> featuresChangeFirst = new ArrayList<Double>();
			ArrayList<Double> featuresValFirst = new ArrayList<Double>();
			double targetChange = 0;//change value of target at current event
			PredictEventCausalChain peFirst = new PredictEventCausalChain();
			
			//////////////////////////////////for first event//////////////////////////////////////
			 currEvent = currEventSeq.get(0);
			 firstDelta = currEvent.getDelta();
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
				 PredictEventCausalChain nullPe = new PredictEventCausalChain();
				 nullPe.setTargetVal(currTarget);
				 nullPe.setDelta(firstDelta);
				 nullPe.setPatientId(patientId);
				 currPatientPredictEventSeq[0] = nullPe;
			 }//if
			 
			 else{	//current lab is not null
				 
				 currTestNames = currLab.getTestNames();
				 currTestResults = currLab.getTestResults();					 
				 
				 if(causalChainsFeatures.contains(util.getFeatureNum("pulse", featuresNumNameChange))){
					featuresNumFirst.add(util.getFeatureNum("pulse", featuresNumNameChange));
					featuresValFirst.add(currPulse);
				 }//if(causalChainsFeatures)
				 if(causalChainsFeatures.contains(util.getFeatureNum("respiratoryRate", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("respiratoryRate", featuresNumNameChange));
						featuresValFirst.add(currRespRate);	
				 }//if(causalChainsFeatures)
				 if(causalChainsFeatures.contains(util.getFeatureNum("temperature", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("temperature", featuresNumNameChange));
						featuresValFirst.add(currTemp);
				 }//if(causalChainsFeatures)
				 if(causalChainsFeatures.contains(util.getFeatureNum("weight", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("weight", featuresNumNameChange));
						featuresValFirst.add(currWeight);
				 }//if(causalChainsFeatures)
				 if(causalChainsFeatures.contains(util.getFeatureNum("bloodPressureDiastolic", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("bloodPressureDiastolic", featuresNumNameChange));
						featuresValFirst.add(currBldPrsDias);	
				 }//if(causalChainsFeatures)
				 if(causalChainsFeatures.contains(util.getFeatureNum("bloodPressureSystolic", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("bloodPressureSystolic", featuresNumNameChange));
						featuresValFirst.add(currBldPrsSyst);
				 }//if(causalChainsFeatures)
				 if(causalChainsFeatures.contains(util.getFeatureNum("FVC_subjectLiters", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("FVC_subjectLiters", featuresNumNameChange));
						featuresValFirst.add(currFVC);
				 }//if(causalChainsFeatures)
				 if(causalChainsFeatures.contains(util.getFeatureNum("SVC_subjectLiters", featuresNumNameChange))){
						featuresNumFirst.add(util.getFeatureNum("SVC_subjectLiters", featuresNumNameChange));
						featuresValFirst.add(currSVC);
				 }//if(causalChainsFeatures)
				 
				 //add features of lab data based on PGM features
				 for(int s=0; s<currTestNames.size(); s++){
					 String testName = currTestNames.get(s);
					 int testNum = util.getFeatureNum(testName, featuresNumNameChange);
					 if(causalChainsFeatures.contains(testNum)){
						 featuresNumFirst.add(testNum);
						 featuresValFirst.add(fv.getFeatureValue(testName, currTestResults.get(s)));
					 }//if(causalChainsFeatures)
				 }//for(s)
				 //add current predict event to the list of predict events
				 
				 peFirst.setFeaturesNum(featuresNumFirst);
				 peFirst.setFeaturesVal(featuresValFirst);
				 peFirst.setTargetVal(currTarget);
				 peFirst.setDelta(firstDelta);
				 peFirst.setPatientId(patientId);
				 currPatientPredictEventSeq[0] = peFirst;
			 }//else
			 
			//////////////////////////////////End for first event//////////////////////////////////////

			 
			//////////////////////////////////for second event//////////////////////////////////////
			 ArrayList<Integer> featuresNumSecond = new ArrayList<Integer>();
			 ArrayList<Double> featuresChangeSecond = new ArrayList<Double>();
			 ArrayList<Double> featuresValSecond = new ArrayList<Double>();
			 PredictEventCausalChain peSecond = new PredictEventCausalChain();
			 prevEvent = currEventSeq.get(0);	//first event
			 currEvent = currEventSeq.get(1);	//second event
			 secondDelta = currEvent.getDelta();
			 firstDelta = prevEvent.getDelta();
			 
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
			 
			 prevFVC = prevEvent.getFVC_subjectLiters();
			 prevSVC = prevEvent.getSVC_subjectLiters();
			 
			//Lab data
			 currLab = currEvent.getLab();
			 prevLab = prevEvent.getLab();
			 
			 //current or previous lab is null 
			 if(currLab == null || prevLab == null){
				 PredictEventCausalChain nullPe = new PredictEventCausalChain();
				 nullPe.setTargetVal(currTarget);
				 nullPe.setPatientId(patientId);
				 nullPe.setDelta(secondDelta);
				 currPatientPredictEventSeq[1] = nullPe;
			 }//if
			 else{//current lab is not null
				 currTestNames = currLab.getTestNames();
				 currTestResults = currLab.getTestResults();					 
				
				 prevTestNames = prevLab.getTestNames();
				 prevTestResults = prevLab.getTestResults();
				 
				//clear all features list(num, val, change)
//				 featuresNum.clear();
//				 featuresVal.clear();
//				 featuresChange.clear();
				 
				 //features of Vital Sign
				 if(causalChainsFeatures.contains(util.getFeatureNum("pulse", featuresNumNameChange))){
						featuresNumSecond.add(util.getFeatureNum("pulse", featuresNumNameChange));
						featuresValSecond.add(currPulse);
						double pulseChange = (currPulse - prevPulse)/prevPulse;
						if(prevPulse == 0 || prevPulse == -1)
							pulseChange = 0;
						featuresChangeSecond.add(pulseChange);
					 }//if(causalChainsFeatures)
					 if(causalChainsFeatures.contains(util.getFeatureNum("respiratoryRate", featuresNumNameChange))){
							featuresNumSecond.add(util.getFeatureNum("respiratoryRate", featuresNumNameChange));
							featuresValSecond.add(currRespRate);	
							double respRateChange = (currRespRate - prevRespRate)/prevRespRate;
							if(prevRespRate == 0 || prevRespRate == -1)
								respRateChange = 0;
							featuresChangeSecond.add(respRateChange);
					 }//if(causalChainsFeatures)
					 if(causalChainsFeatures.contains(util.getFeatureNum("temperature", featuresNumNameChange))){
							featuresNumSecond.add(util.getFeatureNum("temperature", featuresNumNameChange));
							featuresValSecond.add(currTemp);
							double tempChange = (currTemp - prevTemp)/prevTemp;
							if(prevTemp == 0 || prevTemp == -1)
								tempChange = 0;
							featuresChangeSecond.add(tempChange);
					 }//if(causalChainsFeatures)
					 if(causalChainsFeatures.contains(util.getFeatureNum("weight", featuresNumNameChange))){
							featuresNumSecond.add(util.getFeatureNum("weight", featuresNumNameChange));
							featuresValSecond.add(currWeight);
							double weightChange = (currWeight - prevWeight)/prevWeight;
							if(prevWeight == 0 || prevWeight == -1)
								weightChange = 0;
							featuresChangeSecond.add(weightChange);
					 }//if(causalChainsFeatures)
					 if(causalChainsFeatures.contains(util.getFeatureNum("bloodPressureDiastolic", featuresNumNameChange))){
							featuresNumSecond.add(util.getFeatureNum("bloodPressureDiastolic", featuresNumNameChange));
							featuresValSecond.add(currBldPrsDias);	
							double bldPrsDiasChange = (currBldPrsDias - prevBldPrsDias)/prevBldPrsDias;
							if(prevBldPrsDias == 0 || prevBldPrsDias == -1)
								bldPrsDiasChange = 0;
							featuresChangeSecond.add(bldPrsDiasChange);
					 }//if(causalChainsFeatures)
					 if(causalChainsFeatures.contains(util.getFeatureNum("bloodPressureSystolic", featuresNumNameChange))){
							featuresNumSecond.add(util.getFeatureNum("bloodPressureSystolic", featuresNumNameChange));
							featuresValSecond.add(currBldPrsSyst);
							double bldPrsSystChange = (currBldPrsSyst - prevBldPrsSyst)/prevBldPrsSyst;
							if(prevBldPrsSyst == 0 || prevBldPrsSyst == -1)
								bldPrsSystChange = 0;
							featuresChangeSecond.add(bldPrsSystChange);
					 }//if(causalChainsFeatures)
					 if(causalChainsFeatures.contains(util.getFeatureNum("FVC_subjectLiters", featuresNumNameChange))){
							featuresNumSecond.add(util.getFeatureNum("FVC_subjectLiters", featuresNumNameChange));
							featuresValSecond.add(currFVC);
							double FVCChange = (currFVC - prevFVC)/prevFVC;
							if(prevFVC == 0 || prevFVC == -1)
								FVCChange = 0;
							featuresChangeSecond.add(FVCChange);
					 }//if(causalChainsFeatures)
					 if(causalChainsFeatures.contains(util.getFeatureNum("SVC_subjectLiters", featuresNumNameChange))){
							featuresNumSecond.add(util.getFeatureNum("SVC_subjectLiters", featuresNumNameChange));
							featuresValSecond.add(currSVC);
							double SVCChange = (currSVC - prevSVC)/prevSVC;
							if(prevSVC == 0 || prevSVC == -1)
								SVCChange = 0;
							featuresChangeSecond.add(SVCChange);
					 }//if(causalChainsFeatures)
					 
					//add features of lab data based on PGM features
					 for(int s=0; s<currTestNames.size(); s++){
						 String testName = currTestNames.get(s);
						 int testNum = util.getFeatureNum(testName, featuresNumNameChange);
	
						 if(causalChainsFeatures.contains(testNum)){
							 featuresNumSecond.add(testNum);
							 featuresValSecond.add(fv.getFeatureValue(testName, currTestResults.get(s)));
							 double prevFeatureVal=0;
							 for(int p=0; p<prevTestNames.size(); p++){
								 if(prevTestNames.get(p).equalsIgnoreCase(testName)){
									 prevFeatureVal = fv.getFeatureValue(testName, prevTestResults.get(p));
									 break;
								 }//if
							 }//for(int p)
							 double testChange = (fv.getFeatureValue(testName,currTestResults.get(s))- prevFeatureVal)/prevFeatureVal;
							 if(prevFeatureVal == 0)
								 testChange = fv.getFeatureValue(testName,currTestResults.get(s));
							 featuresChangeSecond.add(testChange);							 
						 }//if(causalChainsFeatures)
					 }//for(s)
					 targetChange = (currTarget-prevTarget)/prevTarget;
					 if(prevTarget == 0)
						 targetChange = currTarget;
					 //add current predict event to the list of predict events
					 peSecond = new PredictEventCausalChain();
					 peSecond.setFeaturesNum(featuresNumSecond);
					 peSecond.setFeaturesVal(featuresValSecond);
					 peSecond.setFeaturesChange(featuresChangeSecond);
					 peSecond.setTargetVal(currTarget);
					 peSecond.setTargetChange(targetChange);
					 peSecond.setDelta(secondDelta);
					 peSecond.setPatientId(patientId);
					 currPatientPredictEventSeq[1] = peSecond;
			 }//else current lab is not null
			 
			//////////////////////////////////End for second event//////////////////////////////////////
			 
			//////////////////////////////////Prediction of next events from third event to n'th event //////////////////////////////////////
			 
			 int[] ccPointers = initializeCCPtrs(causalChains);	//array of pointers to causal chains
			
			 for(int j=1; j<currPatientEventsSize-1; j++){
				 
				 	//next prediction event
				    PredictEventCausalChain nextPredictEvent = new PredictEventCausalChain();	//next prediction event object
					ArrayList<Integer> nextFeaturesNum = new ArrayList<Integer>();
					ArrayList<Double> nextFeaturesVal = new ArrayList<Double>();
					ArrayList<Double> nextFeaturesChange = new ArrayList<Double>();
					ArrayList<Integer> nextFeaturesCauseNum = new ArrayList<Integer>();
					ArrayList<Double> nextFeaturesCauseChange = new ArrayList<Double>();
					ArrayList<Double> nextCausalEntropyRatio = new ArrayList<Double>();
					ArrayList<Double> nextFeaturesTargetVal = new ArrayList<Double>();
					ArrayList<Double> nextFeaturesTargetChange = new ArrayList<Double>(); 
					ArrayList<Double> nextFeaturesTargetEntropy = new ArrayList<Double>();
					
					double nextDelta = currEventSeq.get(j+1).getDelta();					
					
					double nextTargetVal = 0;
					double nextTargetChange = 0;
					double nextTargetEntropy = 0;
					
					//previous prediction event
					PredictEventCausalChain prevPredictEvent = currPatientPredictEventSeq[j-1];	//previous prediction event object
					ArrayList<Integer> prevFeaturesNum = prevPredictEvent.getFeaturesNum();	//previous event features number
					ArrayList<Double> prevFeaturesVal = prevPredictEvent.getFeaturesVal();	//previous event features value
					
					//current prediction event
				    PredictEventCausalChain currPredictEvent = currPatientPredictEventSeq[j];	//current patient predict event
					ArrayList<Integer> currFeaturesNum = currPredictEvent.getFeaturesNum();	//current event features number
					ArrayList<Double> currFeaturesVal = currPredictEvent.getFeaturesVal();	//current event features value
					ArrayList<Double> currFeaturesChange = currPredictEvent.getFeaturesChange();	//current event features change
					double currTargetVal = currPredictEvent.getTargetVal();
					double currTargetChange = currPredictEvent.getTargetChange();
					double currTargetEntropy = currPredictEvent.getTargetEntropy();
					
//					ArrayList<Integer> ccFeaturePtrChgIdxs = new ArrayList<Integer>();//index of causal chains that pointer feature is changed
					CausalChainSDU currCC = null;	//current causal chain
					
					//current event is null
					if(currFeaturesNum.size() == 0){
						 PredictEventCausalChain nullPe = new PredictEventCausalChain();
						 nullPe.setTargetVal(currTargetVal);
						 nullPe.setPatientId(patientId);
						 nullPe.setDelta(nextDelta);
						 currPatientPredictEventSeq[j+1] = nullPe;
					}//if
					
					else{//current prediction object is not empty
//						ccFeaturePtrChgIdxs = getccFeaturePtrChgIdxs(ccPointers, causalChains, currFeaturesNum, 
//								currFeaturesChange, currFeaturesVal); //index of causal chains that ptr features are changed
						
						//calculate the best target value based on target entropy of changed features in CC
						double minTargetEntropy = Double.MAX_VALUE;//min value for target entropy
						double bestTargetVal = 0;//best target value so far based on target entropy
						double bestTargetChange = 0;//best target change so far based on target entropy
						int bestCCIdxTarget = -1;	//best index of causal chain for FRS prediction
//						for(int p=0; p<ccFeaturePtrChgIdxs.size(); p++){
						for(int p=0; p<causalChains.size(); p++){
//							int idx = ccFeaturePtrChgIdxs.get(p);//causal chain index
							int ptr = ccPointers[p];//pointer of current CC
							CausalChainSDU ccTargetPredict = causalChains.get(p);//current CC
							
							//////////test///////////
							if(ptr == ccTargetPredict.getFeatureNums().size()-1)
								continue;
							////////////test///////////
							
							double targetEntPred = ccTargetPredict.getTargetEntropy().get(ptr);//target entropy for current CC
							if(targetEntPred != -1 && targetEntPred < minTargetEntropy){
								minTargetEntropy = targetEntPred;
								bestTargetChange = ccTargetPredict.getTargetChanges().get(ptr);
								bestTargetVal = currTargetVal + (currTargetVal*bestTargetChange);
								bestCCIdxTarget = p;
							}//if
						}//for(p=ccFeaturePtrChgIdxs)
						
						nextTargetChange = bestTargetChange;
						nextTargetVal = bestTargetVal;
						nextTargetEntropy = minTargetEntropy;
						
						if(nextTargetVal == 0){
							nextTargetVal = currTargetVal;
							nextTargetChange = currTargetChange;
							nextTargetEntropy = currTargetEntropy;
						}//if
						
						nextPredictEvent.setTargetChange(nextTargetChange);
						nextPredictEvent.setTargetVal(nextTargetVal);
						nextPredictEvent.setTargetEntropy(nextTargetEntropy);
						nextPredictEvent.setCcPredTargetIdx(bestCCIdxTarget);
							
						for(int s=0; s<causalChains.size(); s++){
							currCC = causalChains.get(s);	//current causal chain
							int ccPtr = ccPointers[s];	//pointer of current causal chain
							///////////TEST//////////
							if(ccPtr == currCC.getFeatureNums().size()-1)
								continue;
							///////////TEST//////////
							
							
							int currCCFeaturePtrNum = currCC.getFeatureNums().get(ccPtr);	//curr FNum for current causal chain
							double currCCPtrCTER = currCC.getCTER().get(ccPtr);	//CTER for current CC ptr
							boolean featureIsChanged = featureIsChangedThresh(currCCFeaturePtrNum, currFeaturesNum
									, currFeaturesChange, currFeaturesVal);	//feature is changed or not?
							if(featureIsChanged == false)	//feature did not change, so go to next causal chain
								continue;
							int currCCEffectFeaturePtrNum = currCC.getFeatureNums().get(ccPtr+1);	//effect feature of curr cc
							
//							if(currCCEffectFeaturePtrNum == 55)
//								System.out.println("RBC is predicted");
							
							if(nextFeaturesNum.contains(currCCEffectFeaturePtrNum))//this effect feature is predicted before 
									continue;
							
							int bestCCIdx = s;	//best CC idx is current CC
							double maxCTER = currCCPtrCTER;	//max value for CTER so far
							CausalChainSDU winnerCC = currCC;	//winner causal chain

							//search all causal chains to find effect feature ptr
							for(int c=0; c<causalChains.size(); c++){
								if(c==s)	//current causal chain
									continue;
								CausalChainSDU ccSearch = causalChains.get(c);
								int ccPtrSearch = ccPointers[c];
								////////////test////////////
								if(ccPtrSearch == ccSearch.getFeatureNums().size()-1)
									continue;
								////////test///////////////
								int currCCEffectFeaturePtrNumSearch = ccSearch.getFeatureNums().get(ccPtrSearch+1);
								if(currCCEffectFeaturePtrNumSearch == currCCEffectFeaturePtrNum){//find effect feature ptr in CC
									double currCCPtrSearchCTER = ccSearch.getCTER().get(ccPtrSearch);	//CTER value for curr CC
									if(currCCPtrSearchCTER > maxCTER){	//this CC is better for prediction of effect feature
										maxCTER = currCCPtrSearchCTER;	//update CTER value
										bestCCIdx = c;	//update best CC
									}//if(currCCPtrSearchCTER)
								}//if(currCCEffectFeaturePtrNumSearch)								
							}//for(c=causalChains)
							
							winnerCC = causalChains.get(bestCCIdx);	//winner CC for predicting effect feature
							ccIdxUsedForPredFeatures[bestCCIdx]++;	//add causal chain number of use for predicting features 
							nextFeaturesNum.add(currCCEffectFeaturePtrNum);	//effect feature for prediction
							double currEffectFeatureVal = getFeatureVal(currCCEffectFeaturePtrNum,
									currFeaturesNum, currFeaturesVal);//value of effect feature in current event
							double currPredictEffectFeatureChange = winnerCC.getFeatureChanges().
									get(ccPointers[bestCCIdx]+1);//predicted effect change (relative) based on winner causal chain
							nextFeaturesVal.add(currEffectFeatureVal+
									(currEffectFeatureVal*currPredictEffectFeatureChange));//predicted effect value based on winner causal chain
							nextFeaturesChange.add(currPredictEffectFeatureChange);//predicted effect change based on winner CC
							nextFeaturesCauseNum.add(winnerCC.getFeatureNums().get(ccPointers[bestCCIdx]));//cause feature value
							nextFeaturesCauseChange.add(winnerCC.getFeatureChanges().get(ccPointers[bestCCIdx]));//cause feature change
							nextCausalEntropyRatio.add(winnerCC.getCTER().get(ccPointers[bestCCIdx]));//CTER value for causal link of winner CC
							double nextTargetChgPredictCC = winnerCC.getTargetChanges()
									.get(ccPointers[bestCCIdx]);//next target change for winner CC
							nextFeaturesTargetChange.add(nextTargetChgPredictCC);
							nextFeaturesTargetVal.add(currTargetVal+
									(currTargetVal*nextTargetChgPredictCC));//next predicted target value of winner CC
							nextFeaturesTargetEntropy.add(winnerCC.getTargetEntropy()
									.get(ccPointers[bestCCIdx]));//next target entropy of winner CC
							
							ccPointers[bestCCIdx]++;
							//Repeat causal chain from the beginning
							if(ccPointers[bestCCIdx] == winnerCC.getFeatureNums().size()-1)	//ptr is the last idx of CC
								ccPointers[bestCCIdx] = 0;	//set winner CC ptr at the first idx of CC
						}//for(int s=causalChains)
						
						//set lost features of causal chains with values in previous event
						for(int p=0; p<causalChainsFeatures.size(); p++){
							int fNumLost = causalChainsFeatures.get(p);
							if(!nextFeaturesNum.contains(fNumLost)){//feature does not exist in next prediction event
								nextFeaturesNum.add(fNumLost);
								double fLostVal = getFeatureVal(fNumLost, currFeaturesNum, currFeaturesVal);//lost feature value
								nextFeaturesVal.add(fLostVal);
								nextFeaturesChange.add(0.0);
							}//if
						}//for(p=causalChainsFeatures)
						
						//set the next prediction event
						nextPredictEvent.setFeaturesNum(nextFeaturesNum);
						nextPredictEvent.setFeaturesChange(nextFeaturesChange);
						nextPredictEvent.setFeaturesVal(nextFeaturesVal);
						nextPredictEvent.setCausalEntropyRatio(nextCausalEntropyRatio);
						nextPredictEvent.setPatientId(patientId);
						nextPredictEvent.setFeaturesTargetChange(nextFeaturesTargetChange);
						nextPredictEvent.setFeaturesTargetVal(nextFeaturesTargetVal);
						nextPredictEvent.setFeaturesTargetEntropy(nextFeaturesTargetEntropy);
						nextPredictEvent.setFeautreCauseNum(nextFeaturesCauseNum);
						nextPredictEvent.setFeatureCauseChange(nextFeaturesCauseChange);
						nextPredictEvent.setDelta(nextDelta);
						currPatientPredictEventSeq[j+1] = nextPredictEvent;//set the next predict event
					}//else current prediction object is not empty
				 
			 }//for(j=currPatientEventsSize)
			 
			//////////////////////////////////End prediction of next events from third event to n'th event//////////////////////////////////////
			 patientsPrediction.add(currPatientPredictEventSeq);//add current patient prediction to the list of all predictions 
			 if(i%100 == 0)
				 System.out.println("prediction of patient "+i+" is finished.");
		}//for(i=numOfPatients)
		System.out.println("ALS prediction based on causal chains has been finished.");
		getTargetPredictErrorCC();	//prediction error for ALSFRS value
		getFeaturesPredictErrorCC(causalChainsFeatures);	//prediction error of causal chains features
		getCausalChainsTargetPredictError(causalChains);	//prediction error of causal chains
		
		for(int i=0; i<ccIdxUsedForPredFeatures.length; i++){
			System.out.println("CC-"+(i+1)+" = "+ccIdxUsedForPredFeatures[i]);
		}//for
		
	}//predictCausalChain
	
	private double[] getTargetPredictErrorCC(){
		
		int numOfPatients = patientsList.size();
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
			ALSPatientSDU patient = patientsList.get(i);
			ArrayList<ALSEventSDU> sequence = patient.getPatientSequence();
			PredictEventCausalChain[] predict = patientsPrediction.get(i);
			
			for(int j=0; j<numOfPredictEvents; j++){
				if(j+numOfGivenEvents>=predict.length){
					targetErrorList[i][j] = 0;
					targetErrorListFlags[i][j] = false;
					continue;
				}//if
				PredictEventCausalChain pe = predict[j+numOfGivenEvents];
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
//			answer[k] = sum/numOfPatients;
			answer[k] = sum/ctr;
		}//for(int k)
		System.out.println("Calculate the target error value successfully.");
		File file = new File("E:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\CausalChain\\PredictTargetErrorCC.txt");
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
//		System.out.println("ctr = "+ctr);
		return answer;
	}//getTargetPredictErrorCC
	
	//get average error for causal chains in all prediction levels
		private double[][] getCausalChainsTargetPredictError(ArrayList<CausalChainSDU> causalChains){
			int numOfCausalChains = causalChains.size();
			double[][] answer = new double[numOfCausalChains][numOfPredictEvents];
			int numOfPatients = patientsList.size();
			CausalChainErrorListCC [][] ccErrorList = new 
					CausalChainErrorListCC[numOfCausalChains][numOfPredictEvents];//list of all error list for all causal chains in all time points
			
			//initialize the list
			for(int i=0; i<numOfCausalChains; i++){
				for(int j=0; j<numOfPredictEvents; j++){
					ArrayList<Double> ccErrList = new ArrayList<Double>();
					CausalChainErrorListCC ccel = new CausalChainErrorListCC(ccErrList);
					ccErrorList[i][j] = ccel;
				}//for(int j)
			}//for(int i)
			
			for(int i=0; i<numOfPatients; i++){
				ALSPatientSDU patient = patientsList.get(i);
				ArrayList<ALSEventSDU> sequence = patient.getPatientSequence();
				PredictEventCausalChain[] predict = patientsPrediction.get(i);
				for(int j=0; j<numOfPredictEvents; j++){
					if(j+numOfGivenEvents>=predict.length)
						continue;
					
					PredictEventCausalChain pe = predict[j+numOfGivenEvents];
					ALSEventSDU event = sequence.get(j+numOfGivenEvents);
					double realFrs = event.getTarget().getALSFRSTotal();
					double predictedFrs = pe.getTargetVal();
					double targetPredictErr = Math.abs((realFrs-predictedFrs)/realFrs);
					if(realFrs == 0)
						targetPredictErr = 0;
					int ccIdx = pe.getCcPredTargetIdx();//index of causal chain that predicted frs value
					if(ccIdx != -1)
						ccErrorList[ccIdx][j].getCCErrorList().add(targetPredictErr);//add error value to the list of current Idx
				}//for(j=numOfPredictEvents)
			}//for(i=numberOfPatients)
			
			for(int k=0; k<numOfPredictEvents; k++){
				for(int s=0; s<numOfCausalChains; s++){
					ArrayList<Double> errList = ccErrorList[s][k].getCCErrorList();
					double sum = 0;
					for(int t=0; t<errList.size(); t++){
						sum = sum + errList.get(t);
					}//for(int t)
					sum = sum / errList.size();
					if(errList.size() == 0)
						sum = -1;
					answer[s][k] = sum;
				}//for(int s)
			}//for(int k)
			
			System.out.println("Calculate the causal chain prediction error successfully.");
			File file = new File("E:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\CausalChain\\PredictCausalChainTargetErrorCC.txt");
			try {
				PrintWriter pw = new PrintWriter(file);
				for(int i=0; i<answer.length; i++){
					
					for(int j=0; j<answer[i].length; j++){
						pw.printf("%.2f",answer[i][j]);
						pw.print(",");
					}//for(int j)
					double predStrength = calculateCCTargetPredStrength(answer, i);
					double avgPredError = calculateAvgCCTargetPredError(answer, i);
					pw.print(" | Average error of prediction = "+avgPredError);
					pw.print(" | Prediction strength = "+predStrength);					
					pw.println();
				}//for(int i)
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return answer;
		}//getCCPredictError
	
	private double calculateAvgCCTargetPredError(double[][] answer, int ccIdx) {
			// TODO Auto-generated method stub
		double avg = 0;
		int nonZeroIdxCtr = 0;
		int ccLength = answer[0].length;
		for(int i=0; i<ccLength; i++){
			if(answer[ccIdx][i] != -1){
				avg = avg + answer[ccIdx][i];
				nonZeroIdxCtr++;
			}//if
		}
		if(nonZeroIdxCtr == 0)	
			return 0;
		else
			return avg/nonZeroIdxCtr;
		}

	private double calculateCCTargetPredStrength(double[][] answer, int ccIdx) {
			// TODO Auto-generated method stub
		double avg = 0;
		int nonZeroIdxCtr = 0;
		int ccLength = answer[0].length;
		for(int i=0; i<ccLength; i++){
			if(answer[ccIdx][i] != -1){
				avg = avg + answer[ccIdx][i];
				nonZeroIdxCtr++;
			}//if
		}
		if(nonZeroIdxCtr == 0)	
			return 0;
		else
			return Math.pow(nonZeroIdxCtr, 2)/avg;
	}//calculateCCTargetPredStrength

	private double[][] getFeaturesPredictErrorCC(ArrayList<Integer> causalChainsFeatures){
		int numOfPatients = patientsList.size();
		int numOfFeatures = causalChainsFeatures.size();
		double[][] answer = new double[numOfFeatures][numOfPredictEvents];
		ALSUtilsSDU util = new ALSUtilsSDU();
		String [][] featuresNumNameChange = util.getALSFeaturesNumNameChange();
//		double[][] featureErrorList = new double[numOfPatients][numOfPredictEvents];
		FeatureErrorListCC [][] featuresErrorList = new FeatureErrorListCC[numOfFeatures][numOfPredictEvents];	//list of all features error list in each event
		
		//initialize the list
		for(int i=0; i<numOfFeatures; i++){
			for(int j=0; j<numOfPredictEvents; j++){
				ArrayList<Double> featureErrList = new ArrayList<Double>();
				FeatureErrorListCC fel = new FeatureErrorListCC(featureErrList);
				featuresErrorList[i][j] = fel;
			}//for(int j)
		}//for(int i)

		for(int i=0; i<numOfPatients; i++){
			ALSPatientSDU patient = patientsList.get(i);
			ArrayList<ALSEventSDU> sequence = patient.getPatientSequence();
			PredictEventCausalChain[] predict = patientsPrediction.get(i);
			for(int j=0; j<numOfPredictEvents; j++){
				if(j+numOfGivenEvents>=predict.length)
					continue;
				
				PredictEventCausalChain pe = predict[j+numOfGivenEvents];
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
					int featureNum = causalChainsFeatures.get(k);	//current feature number
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
		File file = new File("E:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\CausalChain\\PredictFeaturesErrorCC.csv");
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
	}//getFeaturesPredictErrorCC
	
	private class FeatureErrorListCC{
		ArrayList<Double> featureErrorList = new ArrayList<Double>();

		FeatureErrorListCC(ArrayList<Double> featureErrorList) {
			super();
			this.featureErrorList = featureErrorList;
		}

		ArrayList<Double> getFeatureErrorList() {
			return featureErrorList;
		}
		
	}//class FeatureErrorListCC
	
	private class CausalChainErrorListCC{
		ArrayList<Double> ccErrorList = new ArrayList<Double>();

		CausalChainErrorListCC(ArrayList<Double> ccErrorList) {
			super();
			this.ccErrorList = ccErrorList;
		}

		ArrayList<Double> getCCErrorList() {
			return ccErrorList;
		}
		
	}//class CausalChainErrorListCC
	
	private double calculateRMSD(RMSD[] rmsd){
		double realSlope, predictedSlope;
		double sigma = 0;
		ArrayList<Double> realALSFRS = new ArrayList<Double>();
		ArrayList<Double> predictedALSFRS = new ArrayList<Double>();
		
		int ctr = 0;	//number of patients for RMSD
		for(int i=0; i<rmsd.length; i++){
			RMSD currRMSD = rmsd[i];
			realALSFRS = currRMSD.getRealALSFRS();
			predictedALSFRS = currRMSD.getPredictedALSFRS();
			int listFRSSize = realALSFRS.size();
//			if(realALSFRS.size() == 0 || predictedALSFRS.size() == 0)
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
	
	private double getFeatureVal(int fNum, ArrayList<Integer> fNums, ArrayList<Double> fVals){
		for(int i=0; i<fNums.size(); i++){
			if(fNums.get(i) == fNum)
				return fVals.get(i);
		}//for
		return 0;
	}//getFeatureVal
	
	//specify that a feature is changed or not based on its threshold value
	private boolean featureIsChangedThresh(int fNum, ArrayList<Integer> currFeaturesNum, 
			ArrayList<Double> currFeaturesChange, ArrayList<Double> currFeaturesVal){
		double fChgThresh = getCCFeaturesChangeThresh(fNum);	//threshold value for feature
		for(int i=0; i<currFeaturesNum.size(); i++){
			if(currFeaturesNum.get(i) == fNum){
				double fChgRatio = currFeaturesChange.get(i);	//relative change value of current feature
				double fChgAbs = Math.abs(currFeaturesVal.get(i) - (currFeaturesVal.get(i)/(fChgRatio+1)));
				if(fChgAbs > fChgThresh)
					return true;
				else return false;
			}//if
		}//for(i=currFeaturesNum)
		return false;
	}//featureIsChangedThresh
	
	//get indexes of causal chains that pointer features has been changed (based on threshold value of features)
	private ArrayList<Integer> getccFeaturePtrChgIdxs(int[] ccPointers, ArrayList<CausalChainSDU> causalChains,
			ArrayList<Integer> currFeaturesNum, ArrayList<Double> currFeaturesChange, ArrayList<Double> currFeaturesVal){
		ArrayList<Integer> answer = new ArrayList<Integer>();	
		for(int i=0; i<causalChains.size(); i++){
			CausalChainSDU cc = causalChains.get(i);	//current causal chain
			int ccPtr = ccPointers[i];
			int fPtrNum = cc.getFeatureNums().get(ccPtr);	//feature number pointer of current causal chain
			
			for(int j=0; j<currFeaturesNum.size(); j++){
				if(currFeaturesNum.get(j) == fPtrNum){
					double fChgRatio = currFeaturesChange.get(j);	//relative change value of current feature
					double fChgAbs = Math.abs(currFeaturesVal.get(j) - (currFeaturesVal.get(j)/(fChgRatio+1)));
					double fChgThresh = getCCFeaturesChangeThresh(fPtrNum);	//threshold value for current features
					if(fChgAbs > fChgThresh){	//add this causal chain index to the answer list
						answer.add(i);
					}//if
					break;
				}//if
			}//for(j=currFeaturesNumn
		}//for(i=causalChains.size)
			
		return answer;
	}//getccFeaturePtrChgIdxs
	
	//get threshold value for changing of features of causal chains
	private double getCCFeaturesChangeThresh(int fNum){
		ALSUtilsSDU util = new ALSUtilsSDU();
		String[][] featuresNumNameChange = util.getALSFeaturesNumNameChange();
		
		String fName = util.getFeatureName(fNum, featuresNumNameChange);
	    double thresh = util.getFeatureChange(fName, featuresNumNameChange);
		
		return thresh;
	}//getCCFeaturesChangeThresh
	
	//get array of causal chains pointers, and set all elements with zero (pointer to the first element of causal chains)
	private int[] initializeCCPtrs(ArrayList<CausalChainSDU> ccList){
		int ccListSize = ccList.size();
		int[] ccPointers = new int[ccListSize];
		for(int i=0; i<ccPointers.length; i++)
			ccPointers[i] = 0;
		return ccPointers;
	}//getCCPointers
	
	//get the best causal chain (index and level of it) for predicting ALSFRS based on minimum ALSFRS rate
	 private CausalChainIdx getMostProbCCForPredictFRS(ArrayList<Integer> featuresNums, 
			 ArrayList<CausalChainSDU> causalChains, ArrayList<Integer> causalChainsFeaures){
		 CausalChainIdx[] causalChainFeaturesIdx = new CausalChainIdx[featuresNums.size()];	//each index of array shows the index of most probable causal chain
		 int maxLengthCC = 0;	//maximum length of all causal chains based on the number of features in causal chain
		 for(int i=0; i<causalChains.size(); i++){
			 if(causalChains.get(i).getFeatureNums().size() > maxLengthCC)
				 maxLengthCC = causalChains.get(i).getFeatureNums().size();
		 }//for(i=causalChains)
		 //related to each feature.
		 //initialize the array indexes with value null
		 for(int i=0; i<causalChainFeaturesIdx.length; i++)
			 causalChainFeaturesIdx[i] = null;
		 
		 for(int i=0; i<featuresNums.size(); i++){	//select the best causal chain for each feature
			 if(causalChainsFeaures.contains(featuresNums.get(i)))
				 causalChainFeaturesIdx[i] = getMostProbCCForPredictFRS_Feature(featuresNums.get(i),causalChains, maxLengthCC);//find the best causal chain for each feature 
		 }//for(i=featuresNums)
		 
		 //if algorithm did not find any causal chain for features return null. it means that prediction process could not be done.
		 if(!findCausalChain(causalChainFeaturesIdx))
			 return null;		 
		 
		 //find the best causal chain between all features based on minimum ALSFRS entropy
		 CausalChainIdx cc;
		 int ccIdx, ccLevel;
		 double targetEntropy;	//entropy value of ALSFRS
		 //best causal chain for the first feature
		 CausalChainIdx bestCc = causalChainFeaturesIdx[0];
		 int bestCcIdx = bestCc.getCcIdx();
		 int bestCcLevel = bestCc.getCcLevel();
		 double bestTargetEntropy = causalChains.get(bestCcIdx).getTargetEntropy().get(bestCcLevel);
		 for(int i=1; i<causalChainFeaturesIdx.length; i++){
			 cc = causalChainFeaturesIdx[i];
			 ccIdx = cc.getCcIdx();
			 ccLevel = cc.getCcLevel();
			 targetEntropy = causalChains.get(ccIdx).getTargetEntropy().get(ccLevel);	//entropy of the causal link
			 //corresponding to the best causal chain of current feature
			 if(targetEntropy < bestTargetEntropy){	//this causal link for current feature is better than the best causal link founded so far
				 bestCc = cc;
			 }//if
		 }//for(i=causalChainFeaturesIdx)
		 
		 return bestCc;
		 
	 }//getMostProbCCForPredictFRS
	 
	 //if there is at least one causal chain return true else return false
	 public boolean findCausalChain(CausalChainIdx[] ccIdxs){
		 
		 for(int i=0; i<ccIdxs.length; i++)
			 if(ccIdxs[i] != null)
				 return true;
		 return false;
	 }//findCausalChian
	 
	 //find the best causal chain based on changing feature for predicting ALSFRS rate according to minimum ALSFRS rate
	 private CausalChainIdx getMostProbCCForPredictFRS_Feature(int fNum, ArrayList<CausalChainSDU> causalChains, int maxLengthCC){
		 double minFRSRate = Double.MAX_VALUE;
		 CausalChainIdx causalChainIdx = null;	//index of best causal chain (= -1, if it did not find any causal chain)
		 //search all levels of causal chains at most to the maximum length of causal chains
		 boolean existFeature = false;	//specifies that fNum is exist in any level of causal chains
		 for(int i=0; i<maxLengthCC-1; i++){	//repeat for maximum levels of causal chains 
			 for(int j=0; j<causalChains.size(); j++){	//repeat for all causal chains
				 CausalChainSDU cc = causalChains.get(j);	//index of current causal chain
				 if(cc.getFeatureNums().get(i) == fNum){	//the level 'i' of current causal chain
					 existFeature = true;	//find such causal chain
					 if(cc.getTargetEntropy().get(i) < minFRSRate){	//this causal chain has entropy lower than previous causal chain
						 causalChainIdx = new CausalChainIdx();
						 causalChainIdx.setCcIdx(j);
						 causalChainIdx.setCcLevel(i);
						 minFRSRate = cc.getTargetEntropy().get(i);	//update min value of RFS entropy
					 }//if
				 }//if
			 }//for(j=causalChains)
			 if (existFeature == true)	//find best causal chain at the level 'i', so it is not needed to check other levels
					 break;
		 }//for(i=maxLengthCC-1)
		 
		 return causalChainIdx;
	 }//getMostProbCCForPredictFRS_Feature
	 
	//get the best causal chain (index and level of it) for predicting effect feature based on maximum value of CTER
		 private CausalChainIdx getMostProbCCForPredictEffect(ArrayList<Integer> featuresNums, 
				 ArrayList<CausalChainSDU> causalChains, ArrayList<Integer> causalChainsFeaures){
			 CausalChainIdx[] causalChainFeaturesIdx = new CausalChainIdx[featuresNums.size()];	//each index of array shows the index of most probable causal chain
			 int maxLengthCC = 0;	//maximum length of all causal chains based on the number of features in causal chain
			 for(int i=0; i<causalChains.size(); i++){
				 if(causalChains.get(i).getFeatureNums().size() > maxLengthCC)
					 maxLengthCC = causalChains.get(i).getFeatureNums().size();
			 }//for(i=causalChains)
			 //related to each feature.
			 //initialize the array indexes with value -1
			 for(int i=0; i<causalChainFeaturesIdx.length; i++)
				 causalChainFeaturesIdx[i] = null;
			 
			 for(int i=0; i<featuresNums.size(); i++){	//select the best causal chain for each feature
				 if(causalChainsFeaures.contains(featuresNums.get(i)))
					 causalChainFeaturesIdx[i] = getMostProbCCForPredictEffect_Feature(featuresNums.get(i),causalChains, maxLengthCC);//find the best causal chain for each feature 
			 }//for(i=featuresNums)
			 
			 //if algorithm did not find any causal chain for features return null. it means that prediction process could not be done.
			 if(!findCausalChain(causalChainFeaturesIdx))
				 return null;		 
			 
			 //find the best causal chain between all features based on minimum ALSFRS entropy
			 CausalChainIdx cc;
			 int ccIdx, ccLevel;
			 double cterVal;	//CTER
			 //best causal chain for the first feature
			 CausalChainIdx bestCc = causalChainFeaturesIdx[0];
			 int bestCcIdx = bestCc.getCcIdx();
			 int bestCcLevel = bestCc.getCcLevel();
			 double bestCTER = causalChains.get(bestCcIdx).getCTER().get(bestCcLevel);
			 for(int i=1; i<causalChainFeaturesIdx.length; i++){
				 cc = causalChainFeaturesIdx[i];
				 ccIdx = cc.getCcIdx();
				 ccLevel = cc.getCcLevel();
				 cterVal = causalChains.get(ccIdx).getCTER().get(ccLevel);	//CTER of the causal link
				 //corresponding to the best causal chain of current feature
				 if(cterVal > bestCTER){	//this causal link for current feature is better than the best causal link founded so far
					 bestCc = cc;
				 }//if
			 }//for(i=causalChainFeaturesIdx)
			 
			 return bestCc;
			 
		 }//getMostProbCCForPredictEffect
	 
	//find the best causal chain based on changing feature for predicting effect feature according to maximum value of CTER
		 private CausalChainIdx getMostProbCCForPredictEffect_Feature(int fNum, ArrayList<CausalChainSDU> causalChains, int maxLengthCC){
			 double maxCTER = Double.MIN_VALUE;
			 CausalChainIdx causalChainIdx = null;	//index of best causal chain (= -1, if it did not find any causal chain)
			 //search all levels of causal chains at most to the maximum length of causal chains
			 boolean existFeature = false;	//specifies that fNum is exist in any level of causal chains
			 for(int i=0; i<maxLengthCC-1; i++){	//repeat for maximum levels of causal chains 
				 for(int j=0; j<causalChains.size(); j++){	//repeat for all causal chains
					 CausalChainSDU cc = causalChains.get(j);	//index of current causal chain
					 if(cc.getFeatureNums().get(i) == fNum){	//the level 'i' of current causal chain
						 existFeature = true;	//find such causal chain
						 if(cc.getCTER().get(i) > maxCTER){	//this causal chain has CTER higher than previous causal chain
							 causalChainIdx = new CausalChainIdx();
							 causalChainIdx.setCcIdx(j);
							 causalChainIdx.setCcLevel(i);
							 maxCTER = cc.getCTER().get(i);	//update max value of CTER
						 }//if
					 }//if
				 }//for(j=causalChains)
				 if (existFeature == true)	//find best causal chain at the level 'i', so it is not needed to check other levels
						 break;
			 }//for(i=maxLengthCC-1)
			 
			 return causalChainIdx;
		 }//getMostProbCCForPredictEffect_Feature
	 
	 //determine the causal chain index and its level
	 private class CausalChainIdx{
		 int ccIdx = -1;	//index of causal chain
		 int ccLevel = -1;	//specifies the level of causal chain
		 
		int getCcIdx() {
			return ccIdx;
		}
		void setCcIdx(int ccIdx) {
			this.ccIdx = ccIdx;
		}
		int getCcLevel() {
			return ccLevel;
		}
		void setCcLevel(int ccLevel) {
			this.ccLevel = ccLevel;
		}
		 
	 }//class CausalChainIdx
	
	//specifies all prediction events
		private class PredictEventCausalChain{
			ArrayList<Integer> featuresNum = new ArrayList<Integer>();
			ArrayList<Double> featuresChange = new ArrayList<Double>();
			ArrayList<Double> featuresVal = new ArrayList<Double>();
			ArrayList<Integer> feautreCauseNum = new ArrayList<Integer>();//cause feature
			ArrayList<Double> featureCauseChange = new ArrayList<Double>();//change value of cause feature
			ArrayList<Double> causalEntropyRatio = new ArrayList<Double>();//CausalTendency/Entropy ratio for each causal edge
			ArrayList<Integer> lostFeaturesNum = new ArrayList<Integer>();//lost features number that dose not exist in the current event
			ArrayList<Double> lostFeaturesVal = new ArrayList<Double>();//lost features value
			double targetChange = 0;//change value of target at current event
			double targetVal = 0;//value of target(als-frs) at current event
			double targetEntropy = -1;//value of target entropy
			ArrayList<Double> featuresTargetChange = new ArrayList<Double>();
			ArrayList<Double> featuresTargetVal = new ArrayList<Double>();
			ArrayList<Double> featuresTargetEntropy = new ArrayList<Double>();//list of entropy of target of current causal edge
			String patientId = null;
			double delta = -1;	//time of event
			int ccPredTargetIdx = -1;	//index of causal chain for prediction of target (ALSFRS)
			
			
			public double getDelta() {
				return delta;
			}
			public void setDelta(double delta) {
				this.delta = delta;
			}
			public int getCcPredTargetIdx() {
				return ccPredTargetIdx;
			}
			public void setCcPredTargetIdx(int ccPredTargetIdx) {
				this.ccPredTargetIdx = ccPredTargetIdx;
			}
			public double getTargetEntropy() {
				return targetEntropy;
			}
			public void setTargetEntropy(double targetEntropy) {
				this.targetEntropy = targetEntropy;
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
	public static void main(String[] args) {
		ALSCausalChainPrediction ccPrediction = new ALSCausalChainPrediction();
		ALSCausalChainSDU cc = new ALSCausalChainSDU();
		ArrayList<CausalChainSDU> causalChains = cc.getCausalChain();
		ArrayList<Integer> causalChainFeatures = cc.getCausalChainsFeatures(causalChains);
		int ctrTargetEnt = 0;
		int ctrCTER = 0;
		for(int i=0; i<causalChains.size(); i++){
			CausalChainSDU ccIdx = causalChains.get(i);
			for(int j=0; j<ccIdx.getTargetEntropy().size(); j++){
				if(ccIdx.getTargetEntropy().get(j) == -1){
					ctrTargetEnt++;
					System.out.println("Causal chain number "+(i+1)+
							" with value -1 of target entropy: "
					+ccIdx.getFeatureNums().get(j)+
							"-->"+ccIdx.getFeatureNums().get(j+1));
				}//if
				if(ccIdx.getCTER().get(j) == 0){
					ctrCTER++;
					System.out.println("Causal chain number "+(i+1)+
							" with value 0 of CTER: "
					+ccIdx.getFeatureNums().get(j)+
							"-->"+ccIdx.getFeatureNums().get(j+1));
				}//if
			}//for(j)
		}//for(i)
		
		System.out.println("Number of -1 value target entropy for causal links is "+ctrTargetEnt);
		System.out.println("Number of 0 value CTER for causal links is "+ctrCTER);
		
		ALSDatasetSDU dataset = new ALSDatasetSDU();
		ArrayList<ALSPatientSDU> patients = dataset.createDataset();
		
		/*select patients that its event sequence is not null
		 only 6000 patients of 10000 patients have non-empty event sequence*/
		ArrayList<ALSPatientSDU> selectedPatients = new ArrayList<ALSPatientSDU>();
//		MainALS main = new MainALS();
		for(int i=0; i<patients.size(); i++){
			ALSPatientSDU currPatient = patients.get(i);
			if(currPatient.getPatientSequence().size() != 0 /*&& ! main.emptyLab(currPatient)*/)
				selectedPatients.add(currPatient);
		}//for
		List<ALSPatientSDU> testPatients = selectedPatients.subList(3000, 4000);
		ArrayList<ALSPatientSDU> testPatientsList = new ArrayList<ALSPatientSDU>(testPatients);
		ccPrediction.predictCausalChain(causalChains, testPatientsList, causalChainFeatures);
	}//main
}// class ALSCausalChainPrediction
