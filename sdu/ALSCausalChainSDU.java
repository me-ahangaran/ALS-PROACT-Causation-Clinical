package sdu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

//this class obtain the causal chain of PRO-ACT data set
public class ALSCausalChainSDU {

	private double[][] causalProbMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];	//causal tendency matrix
	private double[][] entropyDensMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] causalEntropyRatioMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] deathMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] entropyTargetMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] prevMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] nextMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] densProbMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] targetMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] targetProbMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] targetAbsMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] targetProbAbsMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] menPercentMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] timePosMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] timePosProbMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];
	private double[][] ageMat = new double[ALSUtilsSDU.numOfDynamicFeatures][ALSUtilsSDU.numOfDynamicFeatures];

	private int causalChainMinLength = 3;	//min length of causal chain
	
	private String row = "ROW";
	private String col = "COL";
	private String causalChainType = col;	//column or row major causal chain creation
	
	private String prev = "PREV";
	private String next = "NEXT";
	private String causalFChangeValType = next;	//type of feature values in causal chain
	
	private String densCausal = "DC";
	private String densEnt = "DE";
	private String densCausalEntRatio = "CTER";
	private String targetEnt = "TE";
	private String causalChainCriteria = densCausalEntRatio;	//type of criteria for creating causal chain
	
	ArrayList<ALSPGMMatrixSDU> pgmList = new ArrayList<ALSPGMMatrixSDU>();
	ArrayList<Integer> pgmFeatures = new ArrayList<Integer>();
	private String classType1 = ALSUtilsSDU.progression;// first type of classification
	private String classType2 = ALSUtilsSDU.slowSlope;//second type of classification
	
	public ArrayList<CausalChainSDU> getCausalChain(){
		System.out.println("Start creation of causal chains");
		ArrayList<CausalChainSDU> CausalChainList = new ArrayList<CausalChainSDU>();
		ALSUtilsSDU util = new ALSUtilsSDU();
		BufferedReader inputCausalProb = null;
		BufferedReader inputEntropyDensity = null;
		BufferedReader inputDepMatIdx = null;
		BufferedReader inputDeathProb = null;
		BufferedReader inputEntropyTarget = null;
		
		BufferedReader inputMenWomen = null;
		BufferedReader inputDensMaxPts = null;
		BufferedReader inputTargetMaxPts = null;
		BufferedReader inputTargetMaxPtsAbs = null;
		BufferedReader inputTimePosMaxPts = null;
		BufferedReader inputAvgAge = null;
		
		String causalProbStr, entropyDensStr, strDepMatIdx;
		String strEntropyTarget = "";
		String strMenWomen = "";
		String strDensMaxPt = "";
		String strTargetMaxPt = "";
		String strTargetMaxPtAbs = "";
		String strTimePosMaxPt = "";
		String strDeathProb = "";
		String strAvgAge = "";

		double causalProb, entropyDens, causalEntropyRatio, death, targetChange, targetMaxPtProb, 
		targetChangeAbs, targetMaxPtProbAbs, entropyTarget, prev, next, densProb;
		int row = 0;
		int col = 0;
		int idxCtr = -1;	//counter for dependency matrix index list
		ArrayList<DepMatIdx> depMatIdxList = new ArrayList<DepMatIdx>();	//list of dependency matrix indexes
		
		///////////////////////////////////////////////////////////////
		
		try {
			inputDepMatIdx = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\CFD\\CFDIdx.txt"));
			try {
				DepMatIdx depIdx = null;
				String rowIdx, colIdx, startIdx, endIdx;
				String[] line;
				
//				depMatIdxList.add(depIdx);	//first index is null
				while ((strDepMatIdx = inputDepMatIdx.readLine()) != null){
					line = util.getStrArray(strDepMatIdx, 4);	//read dependency matrix  index values
					rowIdx = line[0]; 
					colIdx = line[1];
					startIdx = line[2];
					endIdx = line[3];
					depIdx = new DepMatIdx(Integer.parseInt(rowIdx), Integer.parseInt(colIdx), Integer.parseInt(startIdx), Integer.parseInt(endIdx));
					depMatIdxList.add(depIdx);
				}//while
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				inputDepMatIdx.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//////////////////////////////////////////////////////////////
		
		try {
			inputCausalProb = new BufferedReader(new FileReader
					("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\CFD\\CausalTendency.txt"));
			inputEntropyDensity = new BufferedReader(new FileReader
					("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\EntropyDensity.txt"));
			inputEntropyTarget = new BufferedReader(new FileReader
					("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\Target\\EntropyTarget.txt"));
			inputMenWomen = new BufferedReader(new FileReader
					("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\CFD\\MenWomen.txt"));
			inputDensMaxPts = new BufferedReader(new FileReader
					("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\MaxPointsProb.txt"));
			inputTargetMaxPts = new BufferedReader(new FileReader
					("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\Target\\TargetMaxPointsProb.txt"));
			inputTargetMaxPtsAbs = new BufferedReader(new FileReader
					("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\Target\\TargetMaxPointsProbAbs.txt"));
			inputTimePosMaxPts = new BufferedReader(new FileReader
					("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\TimePosition\\TimePosMaxPointsProb.txt"));
			inputDeathProb = new BufferedReader(new FileReader
					("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\CFD\\Death.txt"));
			inputAvgAge = new BufferedReader(new FileReader
					("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\CFD\\Age.txt"));
			
			try {
				while((causalProbStr = inputCausalProb.readLine()) != null && (entropyDensStr = inputEntropyDensity.readLine()) != null
						&& (strEntropyTarget = inputEntropyTarget.readLine()) != null && (strMenWomen = inputMenWomen.readLine()) != null
						&&(strDeathProb = inputDeathProb.readLine()) != null &&(strAvgAge = inputAvgAge.readLine()) != null
						&& (strTargetMaxPt = inputTargetMaxPts.readLine()) != null &&(strDensMaxPt = inputDensMaxPts.readLine()) != null
						&&(strTimePosMaxPt = inputTimePosMaxPts.readLine()) != null && (strTargetMaxPtAbs = inputTargetMaxPtsAbs.readLine()) != null){
					idxCtr++;
					
					causalProb = Double.parseDouble(causalProbStr);
					entropyDens = Double.parseDouble(entropyDensStr);
					if(entropyDens == -1 || depMatIdxList.get(idxCtr).getStartIdx() == -1 || depMatIdxList.get(idxCtr).getEndIdx() == -1)
						causalEntropyRatio = 0;
					else
						causalEntropyRatio = causalProb/entropyDens;
					entropyTarget = Double.parseDouble(strEntropyTarget);
					String[] targetMaxPtStr = util.getStrArray(strTargetMaxPt, 3);
					String[] targetMaxPtAbsStr = util.getStrArray(strTargetMaxPtAbs, 3);
					String[] menWomenStr = util.getStrArray(strMenWomen, 2);
					String[] densMaxPtStr = util.getStrArray(strDensMaxPt,5);
					String[] timePosMaxPtStr = util.getStrArray(strTimePosMaxPt, 3);
					targetChange = Double.parseDouble(targetMaxPtStr[0]);
					targetMaxPtProb = Double.parseDouble(targetMaxPtStr[1]);
					targetChangeAbs = Double.parseDouble(targetMaxPtAbsStr[0]);
					targetMaxPtProbAbs = Double.parseDouble(targetMaxPtAbsStr[1]);
					prev = Double.parseDouble(densMaxPtStr[0]);
					next = Double.parseDouble(densMaxPtStr[1]);
					densProb = Double.parseDouble(densMaxPtStr[2]);
					death = Double.parseDouble(strDeathProb);
					if(menWomenStr[0].equalsIgnoreCase("NaN") || menWomenStr[1].equalsIgnoreCase("NaN"))
						menPercentMat[row][col] = 0;
					
					else
						menPercentMat[row][col] = Double.parseDouble(menWomenStr[0]);
					
					ageMat[row][col] = Double.parseDouble(strAvgAge);
					timePosMat[row][col] = Double.parseDouble(timePosMaxPtStr[0]);
					timePosProbMat[row][col] = Double.parseDouble(timePosMaxPtStr[1]);
					causalProbMat[row][col] = causalProb;
					entropyDensMat[row][col] = entropyDens;
					causalEntropyRatioMat[row][col] = causalEntropyRatio;
					deathMat[row][col] = death;
					targetMat[row][col] = targetChange;
					targetProbMat[row][col] = targetMaxPtProb;
					targetAbsMat[row][col] = targetChangeAbs;
					targetProbAbsMat[row][col] = targetMaxPtProbAbs;
					entropyTargetMat[row][col] = entropyTarget;
					prevMat[row][col] = prev;
					nextMat[row][col] = next;
					densProbMat[row][col] = densProb;
					col++;
					if(col%ALSUtilsSDU.numOfDynamicFeatures == 0){
						col = 0;
						row++;
					}//if
				}//while
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			inputCausalProb.close();
			inputDeathProb.close();
			inputEntropyDensity.close();
			inputEntropyTarget.close();
			inputMenWomen.close();
			inputAvgAge.close();
			inputDensMaxPts.close();
			inputTargetMaxPts.close();
			inputTargetMaxPtsAbs.close();
			inputTimePosMaxPts.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ALSPGMSDU pgm = new ALSPGMSDU();
		pgmList = pgm.createALSPGMMatrix();
		pgmFeatures = pgm.getPgmFeatures();
		
		File ccFileRow = new File(
				"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\CausalChain\\CausalChainsParamsRow.txt");
		File ccFileColumn = new File(
				"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\CausalChain\\CausalChainsParamsColumn.txt");
		try {
			PrintWriter pwCCRow = new PrintWriter(ccFileRow);
			pwCCRow.println("Row | Max CTER | Max causal tendency | Min causal entropy | Min target entropy");
			int maxCTER, maxCausalTendency, minCausalEntropy, minTargetEntropy;
			String[][] fNumNameChange = util.getALSFeaturesNumNameChange();
			String fName;
			for(int i=0; i<ALSUtilsSDU.numOfDynamicFeatures; i++){
				maxCTER = getMaxRow(causalEntropyRatioMat, i);
				maxCausalTendency = getMaxRow(causalProbMat, i);
				minCausalEntropy = getMinRow(entropyDensMat, i);
				minTargetEntropy = getMinRow(entropyTargetMat, i);
				fName = util.getFeatureName(i, fNumNameChange);
				pwCCRow.println("Row "+i+" = "+fName+" | "+maxCTER+" | "+maxCausalTendency+" | "+minCausalEntropy+" | "+minTargetEntropy);
			}//for(i)
			
			pwCCRow.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			PrintWriter pwCCCol = new PrintWriter(ccFileColumn);
			pwCCCol.println("Column | Max CTER | Max causal tendency | Min causal entropy | Min target entropy");
			int maxCTER, maxCausalTendency, minCausalEntropy, minTargetEntropy;
			
			String[][] fNumNameChange = util.getALSFeaturesNumNameChange();
			String fName;
			for(int i=0; i<ALSUtilsSDU.numOfDynamicFeatures; i++){
				maxCTER = getMaxCol(causalEntropyRatioMat, i);
				maxCausalTendency = getMaxCol(causalProbMat, i);
				minCausalEntropy = getMinCol(entropyDensMat, i);
				minTargetEntropy = getMinCol(entropyTargetMat, i);
				fName = util.getFeatureName(i, fNumNameChange);
				pwCCCol.println("Column "+i+" = "+fName+" | "+maxCTER+" | "+maxCausalTendency+" | "+minCausalEntropy+" | "+minTargetEntropy);
			}//for(i)
			pwCCCol.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//get causal chain with row or column major policy
		CausalChainSDU cc = new CausalChainSDU();
//		ArrayList<Integer> causalChainFeatures = new ArrayList<Integer>();
//		int nextRow = getMaxRow(causalEntropyRatioMat, 13);//max index of causal entropy ratio from first row
//		causalChainFeatures.add(nextRow);	//add first causal feature
//		
//		for(int idx=1; idx<ALSUtils.numOfDynamicFeatures; idx++){
//			nextRow = getMaxRow(causalEntropyRatioMat, nextRow);
//			if(causalChainFeatures.contains(nextRow))
//				break;
//			causalChainFeatures.add(nextRow);				
//		}//for(idx)
		
		
		
		if(causalChainType.equalsIgnoreCase("ROW")){
			for(int idx=0; idx<ALSUtilsSDU.numOfDynamicFeatures; idx++){
				if(!pgmFeatures.contains(idx))	//current feature does not exist in the PGM
					continue;
				cc = getCCRowStartIdx(idx);

				if(cc.getFeatureNums().size() >= causalChainMinLength)
					CausalChainList.add(getCausalChainParams(cc));
				
			}//for(idx)
		}//if
		
		if(causalChainType.equalsIgnoreCase("COL")){
			for(int idx=0; idx<ALSUtilsSDU.numOfDynamicFeatures; idx++){
				if(!pgmFeatures.contains(idx))	//current feature does not exist in the PGM
					continue;
				cc = getCCColStartIdx(idx);

				if(cc.getFeatureNums().size() >= causalChainMinLength)
					CausalChainList.add(getCausalChainParams(cc));
				
			}//for(idx)
		}//if
		
		
		ArrayList<CausalChainSDU> sortedCCList = util.sortCausalChainsCTERDescending(CausalChainList);
		createCCFiles(sortedCCList);
		return sortedCCList;

//		return CausalChainList;
		

	}//getCausalChain
	
	private CausalChainSDU getCausalChainParams(CausalChainSDU cc){
		ArrayList<Integer> fNums = cc.getFeatureNums();
		ArrayList<Double> targetChanges = new ArrayList<Double>();
		ArrayList<Double> targetChangesAbs = new ArrayList<Double>();
		ArrayList<Double> timePos = new ArrayList<Double>();
		ArrayList<Double> age = new ArrayList<Double>();
		ArrayList<Double> menPercent = new ArrayList<Double>();
		ArrayList<Double> causalEntropy = new ArrayList<Double>();
		ArrayList<Double> causalTendency = new ArrayList<Double>();
		ArrayList<Double> CTER = new ArrayList<Double>();
		ArrayList<Double> death = new ArrayList<Double>();
		ArrayList<Double> targetProb = new ArrayList<Double>();
		ArrayList<Double> densityProb = new ArrayList<Double>();
		ArrayList<Double> timePosProb = new ArrayList<Double>();
		ArrayList<Double> targetEntropy = new ArrayList<Double>();
		ArrayList<Double> fChanges = new ArrayList<Double>();
		
		//prev or next major for causal feature change value
		if(causalFChangeValType.equalsIgnoreCase("PREV"))
			fChanges = getCausalFeaturesChangesPrevMajor(fNums);
		if(causalFChangeValType.equalsIgnoreCase("NEXT"))
			fChanges = getCausalFeaturesChangesNextMajor(fNums);
		
		int row, col;
		for(int i=0; i<fNums.size()-1; i++){
			row = fNums.get(i);
			col = fNums.get(i+1);
			targetChanges.add(targetMat[row][col]);
			targetChangesAbs.add(targetAbsMat[row][col]);
			timePos.add(timePosMat[row][col]);
			age.add(ageMat[row][col]);
			menPercent.add(menPercentMat[row][col]);
			causalEntropy.add(entropyDensMat[row][col]);
			causalTendency.add(causalProbMat[row][col]);
			CTER.add(causalEntropyRatioMat[row][col]);
			death.add(deathMat[row][col]);
			targetProb.add(targetProbMat[row][col]);
			densityProb.add(densProbMat[row][col]);
			timePosProb.add(timePosProbMat[row][col]);
			targetEntropy.add(entropyTargetMat[row][col]);
			
		}//for(i)
		
		cc.setTargetChanges(targetChanges);
		cc.setTargetChangesAbs(targetChangesAbs);
		cc.setTimePos(timePos);
		cc.setAge(age);
		cc.setMenPercent(menPercent);
		cc.setCausalEntropy(causalEntropy);
		cc.setCausalTendency(causalTendency);
		cc.setCTER(CTER);
		cc.setDeath(death);
		cc.setTargetProb(targetProb);
		cc.setDensityProb(densityProb);
		cc.setTimePosProb(timePosProb);
		cc.setTargetEntropy(targetEntropy);
		cc.setFeatureChanges(fChanges);
		cc.setAvgCTER(calculateAvg(CTER));
		cc.setAvgTargetEntropy(calculateAvg(targetEntropy));
		
		return cc;
		
	}//setCausalChainParams
	
	//get change value of causal feature in causal chain based on prev value of causal links
	private ArrayList<Double> getCausalFeaturesChangesPrevMajor(ArrayList<Integer> fNums){
		ArrayList<Double> fChanges = new ArrayList<Double>();
		int row, col;
		int numOfFeatures = fNums.size();
		for(int i=0; i<numOfFeatures-1; i++){
			row = fNums.get(i);
			col = fNums.get(i+1);
			fChanges.add(prevMat[row][col]);
		}//for(i)
		fChanges.add(nextMat[fNums.get(numOfFeatures-2)][fNums.get(numOfFeatures-1)]);
		return fChanges;
	}//getCausalFeaturesChangesPrevMajor
	
	//get change value of causal feature in causal chain based on next value of causal links
		private ArrayList<Double> getCausalFeaturesChangesNextMajor(ArrayList<Integer> fNums){
			ArrayList<Double> fChanges = new ArrayList<Double>();
			int row, col;
			int numOfFeatures = fNums.size();
			fChanges.add(prevMat[fNums.get(0)][fNums.get(1)]);
			for(int i=0; i<numOfFeatures-1; i++){
				row = fNums.get(i);
				col = fNums.get(i+1);
				fChanges.add(nextMat[row][col]);
			}//for(i)

			return fChanges;
		}//getCausalFeaturesChangesNextMajor
	
	//get feature number and feature name in causal chain based on ROW major
	private CausalChainSDU getCCRowStartIdx(int startIdx){
		CausalChainSDU cc = new CausalChainSDU();
		ALSUtilsSDU util = new ALSUtilsSDU();
		ArrayList<Integer> causalChainFeatures = new ArrayList<Integer>();
		ArrayList<String> causalChainFeaturesName = new ArrayList<String>();
		causalChainFeatures.add(startIdx);
		String[][] fNumNameChange = util.getALSFeaturesNumNameChange();
//		String fName = util.getFeatureName(startIdx, fNumNameChange);
//		causalChainFeaturesName.add(fName);
		int nextRow = startIdx;
//		int nextRow = getMaxRow(causalEntropyRatioMat, startIdx);//max index of causal entropy ratio from first row
//		causalChainFeatures.add(nextRow);	//add first causal feature
		causalChainFeaturesName.add(util.getFeatureName(nextRow, fNumNameChange));
		for(int idx=1; idx<ALSUtilsSDU.numOfDynamicFeatures; idx++){
//			if(nextRow == -1)
//				break;
			//what is criteria for selecting next row
			if(causalChainCriteria.equalsIgnoreCase(densCausal))	//density causal criteria
				nextRow = getMaxRow(causalProbMat, nextRow);
			if(causalChainCriteria.equalsIgnoreCase(densEnt))	//density entropy criteria
				nextRow = getMinRow(entropyDensMat, nextRow);
			if(causalChainCriteria.equalsIgnoreCase(densCausalEntRatio))	//density causal entropy ratio criteria
				nextRow = getMaxRow(causalEntropyRatioMat, nextRow);
			if(causalChainCriteria.equalsIgnoreCase(targetEnt))	//target entropy criteria
				nextRow = getMinRow(entropyTargetMat, nextRow);
				
			if(causalChainFeatures.contains(nextRow) || nextRow == -1)
				break;
			causalChainFeatures.add(nextRow);
			causalChainFeaturesName.add(util.getFeatureName(nextRow, fNumNameChange));
		}//for(idx)
		cc.setFeatureNums(causalChainFeatures);
		cc.setFeatureNames(causalChainFeaturesName);
		return cc;
	}//getCCRowStartIdx
	
	//get feature number and feature name in causal chain based on COL major
	private CausalChainSDU getCCColStartIdx(int startIdx){
		CausalChainSDU cc = new CausalChainSDU();
		ALSUtilsSDU util = new ALSUtilsSDU();
		ArrayList<Integer> causalChainFeatures = new ArrayList<Integer>();
		ArrayList<String> causalChainFeaturesName = new ArrayList<String>();
		causalChainFeatures.add(startIdx);
		String[][] fNumNameChange = util.getALSFeaturesNumNameChange();
//		String fName = util.getFeatureName(startIdx, fNumNameChange);
//		causalChainFeaturesName.add(fName);
		int nextCol = startIdx;
//		int nextCol = getMaxCol(causalEntropyRatioMat, startIdx);//max index of causal entropy ratio from first row
//		causalChainFeatures.add(nextCol);	//add first causal feature
		causalChainFeaturesName.add(util.getFeatureName(nextCol, fNumNameChange));
		for(int idx=1; idx<ALSUtilsSDU.numOfDynamicFeatures; idx++){
//			if(nextCol == -1)
//				break;
			//what is criteria for selecting next row
			if(causalChainCriteria.equalsIgnoreCase(densCausal))	//density causal criteria
				nextCol = getMaxCol(causalProbMat, nextCol);
			if(causalChainCriteria.equalsIgnoreCase(densEnt))	//density entropy criteria
				nextCol = getMinCol(entropyDensMat, nextCol);
			if(causalChainCriteria.equalsIgnoreCase(densCausalEntRatio))	//density causal entropy ratio criteria
				nextCol = getMaxCol(causalEntropyRatioMat, nextCol);
			if(causalChainCriteria.equalsIgnoreCase(targetEnt))
				nextCol = getMinCol(entropyTargetMat, nextCol);	//target entropy criteria
			
			if(causalChainFeatures.contains(nextCol) || nextCol == -1)
				break;
			causalChainFeatures.add(nextCol);
			causalChainFeaturesName.add(util.getFeatureName(nextCol, fNumNameChange));
		}//for(idx)
		ArrayList<Integer> reverseFNums = new ArrayList<Integer>();
		ArrayList<String> reverseFNames = new ArrayList<String>();
		
		//reverse causal chain list
		for(int i=causalChainFeatures.size()-1; i>=0; i--){
			int featureNum = causalChainFeatures.get(i);
			String featureName = causalChainFeaturesName.get(i);
			reverseFNums.add(featureNum);
			reverseFNames.add(featureName);
		}//for(i)
		cc.setFeatureNums(reverseFNums);
		cc.setFeatureNames(reverseFNames);
		return cc;
	}//getCCColStartIdx
	
	private boolean allElemNullRow(double[][] matrix, int row){
		int numOfElem = matrix.length;
		for(int i=0; i<numOfElem; i++){
			if(matrix[row][i] != -1 && matrix[row][i] != 0)
				return false;
		}//for
		return true;
	}//allElemNullRow
	
	private boolean allElemNullCol(double[][] matrix, int col){
		int numOfElem = matrix.length;
		for(int i=0; i<numOfElem; i++){
			if(matrix[i][col] != -1 && matrix[i][col] != 0)
				return false;
		}//for
		return true;
	}//allElemNullCol
	
	private boolean existIdxInPGM(int row, int col){
		ALSPGMMatrixSDU pgm;
		int r,c;
		for(int i=0; i<pgmList.size(); i++){
			pgm = pgmList.get(i);
			r = pgm.getRow();
			c = pgm.getCol();
			if(r==row && c==col)
				return true;
		}//for
		return false;
	}//existIdxInPGM
	
	//TODO
	private int getMaxRow(double[][] matrix, int row){
		if(allElemNullRow(matrix, row) == true)
			return -1;
		int numOfCols = matrix[0].length;		
//		double max = matrix[row][1];
		double max = Double.MIN_VALUE;
		int maxIdx = 1;
		for(int i=1; i<numOfCols; i++){
			if(matrix[row][i] > max && i!=row && matrix[row][i] != -1 && matrix[row][i] != 0
					&& existIdxInPGM(row, i)){
				max = matrix[row][i];
				maxIdx = i;
			}//if
		}//for
		if(existIdxInPGM(row, maxIdx))
			return maxIdx;
		return -1;
	}//getMaxRow
	
	private int getMinRow(double[][] matrix, int row){
		if(allElemNullRow(matrix, row) == true)
			return -1;
		int numOfCols = matrix[0].length;
//		double min = matrix[row][1];
		double min = Double.MAX_VALUE;
		int minIdx = 1;
		for(int i=1; i<numOfCols; i++){
			if(matrix[row][i] < min && i!=row && matrix[row][i] != -1 && matrix[row][i] != 0
					&& existIdxInPGM(row, i)){
				min = matrix[row][i];
				minIdx = i;
			}//if
		}//for
		if(existIdxInPGM(row, minIdx))
			return minIdx;
		return -1;
	}//getMinRow
	
	private int getMaxCol(double[][] matrix, int col){
		if(allElemNullCol(matrix, col) == true)
			return -1;
		int numOfRows = matrix.length;		
//		double max = matrix[1][col];
		double max = Double.MIN_VALUE;
		int maxIdx = 1;
		for(int i=1; i<numOfRows; i++){
			if(matrix[i][col] > max && i!=col && matrix[i][col] != -1 && matrix[i][col] != 0
					&& existIdxInPGM(i, col)){
				max = matrix[i][col];
				maxIdx = i;
			}//if
		}//for
		if(existIdxInPGM(maxIdx,col))
			return maxIdx;
		return -1;
	}//getMaxCol
	
	private int getMinCol(double[][] matrix, int col){
		if(allElemNullCol(matrix, col) == true)
			return -1;
		int numOfRows = matrix.length;		
//		double min = matrix[1][col];
		double min = Double.MAX_VALUE;
		int minIdx = 1;
		for(int i=1; i<numOfRows; i++){
			if(matrix[i][col] < min && i!=col && matrix[i][col] != -1 && matrix[i][col] != 0
					&& existIdxInPGM(i, col)){
				min = matrix[i][col];
				minIdx = i;
			}//if
		}//for
		if(existIdxInPGM(minIdx,col))
			return minIdx;
		return -1;
	}//getMinCol
	
	public String getCausalChainType() {
		return causalChainType;
	}

	public void setCausalChainType(String causalChainType) {
		this.causalChainType = causalChainType;
	}
	
	

	public String getCausalFChangeValType() {
		return causalFChangeValType;
	}

	public void setCausalFChangeValType(String causalFChangeValType) {
		this.causalFChangeValType = causalFChangeValType;
	}



	public String getCausalChainCriteria() {
		return causalChainCriteria;
	}

	public void setCausalChainCriteria(String causalChainCriteria) {
		this.causalChainCriteria = causalChainCriteria;
	}

	public double calculateAvg(ArrayList<Double> list){
		double avg = 0;
		for(int i=0; i<list.size(); i++)
			avg = avg + list.get(i);
		avg = avg/list.size();
		return avg;
	}//calculateAvgCTER

	public void prediction(ArrayList<ALSPatientSDU> patients, ArrayList<CausalChainSDU> causalChains){
		int numOfPatients = patients.size();
		int numOfCausalChains = causalChains.size();
		double[] causalChainsError = new double[numOfCausalChains];	//error of each causal chain
		
		//for each causal chain calculate it's error
		for(int i=0; i<numOfCausalChains; i++){
			CausalChainSDU cc = causalChains.get(i);	//current causal chain
			ArrayList<Integer> ccFeaturesNum = cc.getFeatureNums();	
			ArrayList<String> ccFeaturesName = cc.getFeatureNames();
			ArrayList<Double> ccFeaturesChange = cc.getFeatureChanges();
			int causalChainLength = ccFeaturesChange.size();	//length of current causal chain
		}//for(i)
		
	}//prediction
	
	private class DepMatIdx{
		int row, col, startIdx, endIdx;

		public DepMatIdx(int row, int col, int startIdx, int endIdx) {
			super();
			this.row = row;
			this.col = col;
			this.startIdx = startIdx;
			this.endIdx = endIdx;
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

		public int getStartIdx() {
			return startIdx;
		}

		public void setStartIdx(int startIdx) {
			this.startIdx = startIdx;
		}

		public int getEndIdx() {
			return endIdx;
		}

		public void setEndIdx(int endIdx) {
			this.endIdx = endIdx;
		}
		
	}//class DepMatIdx
	
	private void createCCFiles(ArrayList<CausalChainSDU> cc){
		
		File ccFile = new File(
				"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\CausalChain\\CausalChains.txt");
		File ccFeaturesFile = new File(
				"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\CausalChain\\CausalChainsFeatures.txt");
		PrintWriter ccPW = null;
		PrintWriter ccFeaturesPW = null;
		try {
			ccPW = new PrintWriter(ccFile);
			ccFeaturesPW = new PrintWriter(ccFeaturesFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ALSUtilsSDU util = new ALSUtilsSDU();
		String [][] fNumNameChange = util.getALSFeaturesNumNameChange();
		ArrayList<Integer> ccFNums = getCausalChainsFeatures(cc);
		for(int i=0; i<ccFNums.size(); i++){
			int fNum = ccFNums.get(i);
			String fName = util.getFeatureName(fNum, fNumNameChange);
			ccFeaturesPW.println(fNum+" = "+fName);
		}//for
		
		ccPW.println("Causal chain | Features changes | Average CTER | Average target entropy");
		for(int i=0; i<cc.size(); i++)
			ccPW.println("C"+(i+1)+" = "+cc.get(i).getFeatureNums()+" | "+cc.get(i).getFeatureNames()+" | "+cc.get(i).getFeatureChanges()
					+" | "+cc.get(i).getAvgCTER()+" | "+cc.get(i).getAvgTargetEntropy());
		ccPW.close();
		ccFeaturesPW.close();
		System.out.println("Causal chains file is created successfully.");
		System.out.println("Number of causal chains "+causalChainType+" & "+causalFChangeValType+" & "+causalChainCriteria+" = "+cc.size());
		System.out.println("Number of causal chains features is : "+ccFNums.size());
	}//createCCFiles
	
	//get all features in all causal chains
	public ArrayList<Integer> getCausalChainsFeatures(ArrayList<CausalChainSDU> causalChains){
			ArrayList<Integer> causalChainsFeatrues = new ArrayList<Integer>();
			
			for(int i=0; i<causalChains.size(); i++){
				ArrayList<Integer> currentCCFeatures = causalChains.get(i).getFeatureNums();
				for(int j=0; j<currentCCFeatures.size(); j++){
					int fNum = currentCCFeatures.get(j);
					if(!causalChainsFeatrues.contains(fNum))
						causalChainsFeatrues.add(fNum);
				}//for(j)
			}//for(i)
			return causalChainsFeatrues;
	}//getCausalChainsFeatures
	


}//class ALSCausalChain
