package sdu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ALSPGMSDU {
	
	private double entropyDensityThresh = 4;	//max value of entropy density (Epsilon)
	private double entropyTargetThresh = 3;	//max value of entropy target
	private double causalProbThresh = 0.55;	//min value for causal probability (Delta)
	private double targetMaxChange = 100;	//max change value of ALS progression rate. it is normally negative(= -0.2)
	private int numOfMaxPoints = 1;	//number of max points in density function
	private double branchFactorPercent = 0.01;	//percent of features in PGM for every row in dependency matrix
	private ArrayList<ALSPGMMatrixSDU> pgmMat = new ArrayList<ALSPGMMatrixSDU>();//main PGM matrix
	private ArrayList<DepMatIdx> depMatIdxList = new ArrayList<DepMatIdx>();	//list of dependency matrix indexes
	private ArrayList<Integer> pgmFeatures = new ArrayList<Integer>();
	private int minNumOfPatients = 100;//min number of patients for creating PGM for each causal link
	private String classType1 = ALSUtilsSDU.progression;// first type of classification
	private String classType2 = ALSUtilsSDU.slowSlope;//second type of classification
	
	public ArrayList<Integer> getPgmFeatures() {
		return pgmFeatures;
	}

	public ArrayList<ALSPGMMatrixSDU> getPgmMat() {
		return pgmMat;
	}

	public ArrayList<ALSPGMMatrixSDU> createALSPGMMatrix(){
		System.out.println("Start creation of PGM.");
		File filePGM = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\CausalGraph\\CausalGraph.csv");
		BufferedReader inputDepMatIdx, inputCausalProb, inputEntropyDensity, inputEntropyTarget,inputDeathProb,
		inputMenWomen, inputDensMaxPts, inputTargetMaxPts, inputTargetMaxPtsAbs, inputTimePosMaxPts, inputAvgAge;
		String strDepMatIdx = "";
		String strCausalProb= "";
		String strEntropyDensity = "";
		String strEntropyTarget = "";
		String strMenWomen = "";
		String strDensMaxPt = "";
		String strTargetMaxPt = "";
		String strTargetMaxPtAbs = "";
		String strTimePosMaxPt = "";
		String strDeathProb = "";
		String strAvgAge = "";
		String[] line;
		ALSUtilsSDU util = new ALSUtilsSDU();
		double causalProb, entropyDensity, entropyTarget, menPercent, prevChange, nextChange,
		densMaxPtProb, targetChange, targetChangeAbs, targetMaxPtProb, targetMaxPtProbAbs, timePosMaxPt, timePosMaxPtProb, deathProb, avgAge;
		ALSPGMMatrixSDU pgmMatIdx;
		int rowInt, colInt;
		int numOfIdx = 0;
		
		int idxCtr = -1;	//counter for dependency matrix index list
		
		//set all pgm matrix indexes as null
//		for(int i=0; i<FeatureNumber.numOfDynamicFeaturs; i++)
//			for(int j=0; j<FeatureNumber.numOfDynamicFeaturs; j++)
//				pgmMat[i][j] = null;
		
		try {
			inputDepMatIdx = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\CFD\\CFDIdx.txt"));
			try {
				DepMatIdx depIdx = null;
				String row, col, startIdx, endIdx;
//				depMatIdxList.add(depIdx);	//first index is null
				while ((strDepMatIdx = inputDepMatIdx.readLine()) != null){
					line = util.getStrArray(strDepMatIdx, 4);	//read dependency matrix  index values
					row = line[0]; 
					col = line[1];
					startIdx = line[2];
					endIdx = line[3];
					depIdx = new DepMatIdx(Integer.parseInt(row), Integer.parseInt(col), Integer.parseInt(startIdx), Integer.parseInt(endIdx));
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

			
			while((strCausalProb = inputCausalProb.readLine()) != null && (strEntropyDensity = inputEntropyDensity.readLine()) != null
					&& (strEntropyTarget = inputEntropyTarget.readLine()) != null && (strMenWomen = inputMenWomen.readLine()) != null
					&&(strDeathProb = inputDeathProb.readLine()) != null &&(strAvgAge = inputAvgAge.readLine()) != null){
				idxCtr++;
				
				strDensMaxPt = inputDensMaxPts.readLine();
				strTargetMaxPt = inputTargetMaxPts.readLine();
				strTargetMaxPtAbs = inputTargetMaxPtsAbs.readLine();
				strTimePosMaxPt = inputTimePosMaxPts.readLine();
				causalProb = Double.parseDouble(strCausalProb);
				if(causalProb >= causalProbThresh && depMatIdxList.get(idxCtr).getStartIdx() != -1 &&
						depMatIdxList.get(idxCtr).getEndIdx() != -1 && 
						depMatIdxList.get(idxCtr).getEndIdx() - depMatIdxList.get(idxCtr).getStartIdx() > minNumOfPatients){	
					entropyDensity = Double.parseDouble(strEntropyDensity);
					entropyTarget = Double.parseDouble(strEntropyTarget);
					deathProb = Double.parseDouble(strDeathProb);
					avgAge = Double.parseDouble(strAvgAge);
					String[] targetMaxPtStr = util.getStrArray(strTargetMaxPt, 3);
					String[] targetMaxPtStrAbs = util.getStrArray(strTargetMaxPtAbs, 3);
					targetChange = Double.parseDouble(targetMaxPtStr[0]);
					targetMaxPtProb = Double.parseDouble(targetMaxPtStr[1]);
					targetChangeAbs = Double.parseDouble(targetMaxPtStrAbs[0]);
					targetMaxPtProbAbs = Double.parseDouble(targetMaxPtStrAbs[1]);
					
					if(entropyDensity <= entropyDensityThresh && entropyDensity != -1 && targetChange < targetMaxChange){
						pgmMatIdx = new ALSPGMMatrixSDU();						
						String[] menWomenStr = util.getStrArray(strMenWomen, 2);
						String[] densMaxPtStr = util.getStrArray(strDensMaxPt,5);
						String[] timePosMaxPtStr = util.getStrArray(strTimePosMaxPt, 3);
						
						if(menWomenStr[0].equalsIgnoreCase("NaN") || menWomenStr[1].equalsIgnoreCase("NaN"))
							pgmMatIdx.setMenPercent(0);
						
						else{
							menPercent = Double.parseDouble(menWomenStr[0]);
							pgmMatIdx.setMenPercent(menPercent);
						}
						prevChange = Double.parseDouble(densMaxPtStr[0]);
						nextChange = Double.parseDouble(densMaxPtStr[1]);
						densMaxPtProb = Double.parseDouble(densMaxPtStr[2]);
						timePosMaxPt = Double.parseDouble(timePosMaxPtStr[0]);
						timePosMaxPtProb = Double.parseDouble(timePosMaxPtStr[1]);
						
						pgmMatIdx.setCausalProb(causalProb);
						pgmMatIdx.setEntropyDensity(entropyDensity);
						pgmMatIdx.setEntropyTarget(entropyTarget);
						pgmMatIdx.setPrevChange(prevChange);
						pgmMatIdx.setNextChange(nextChange);
						pgmMatIdx.setDensProb(densMaxPtProb);
						pgmMatIdx.setTargetChange(targetChange);
						pgmMatIdx.setTargetProb(targetMaxPtProb);
						pgmMatIdx.setTargetChangeAbs(targetChangeAbs);
						pgmMatIdx.setTargetProbAbs(targetMaxPtProbAbs);
						pgmMatIdx.setTimePosChange(timePosMaxPt);
						pgmMatIdx.setTimePosProb(timePosMaxPtProb);
						pgmMatIdx.setDeathProb(deathProb);
						pgmMatIdx.setAvgAge(avgAge);
						
						rowInt = depMatIdxList.get(idxCtr).getRow();
						colInt = depMatIdxList.get(idxCtr).getCol();
						pgmMatIdx.setRow(rowInt);
						pgmMatIdx.setCol(colInt);
						pgmMat.add(pgmMatIdx);
//						System.out.println("**************idxCtr = "+idxCtr+" = ("+rowInt+","+colInt+")***************");
//						pgmMat[rowInt][colInt] = pgmMatIdx;
						numOfIdx++;
					}//if(entropyDensity)
				}//if(causalProb)
				
			}//while
			
			inputCausalProb.close();
			inputDensMaxPts.close();
			inputEntropyDensity.close();
			inputEntropyTarget.close();
			inputMenWomen.close();
			inputTargetMaxPts.close();
			inputTargetMaxPtsAbs.close();
			inputTimePosMaxPts.close();
			inputDeathProb.close();
			inputAvgAge.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FeatureNameSDU fn = new FeatureNameSDU();
		try {
			PrintWriter pwPGM = new PrintWriter(filePGM);
			System.out.println("number of causal links in PGM is : "+numOfIdx);
			pwPGM.println("Row-->Col,Causal tendency,Prev,Next,Dens prob,Entropy density,Target change (absolute),Target change (relative),Entropy target,Men percent,"
					+"Time pos,Time pos prob,Death prob,Average age,CTER");
//			pwPGM.println();
			for(int i=0; i<pgmMat.size(); i++){
				ALSPGMMatrixSDU pgm = pgmMat.get(i);
//				pwPGM.print((i+1)+"-");
				pwPGM.print("("+pgm.getRow()+"="+fn.getFeatureName(pgm.getRow())+"-->"+pgm.getCol()+"="+fn.getFeatureName(pgm.getCol())+"),");
				pwPGM.printf("%.2f", pgm.getCausalProb());
				pwPGM.print(",");
				pwPGM.printf("%.2f", pgm.getPrevChange());
				pwPGM.print(",");
				pwPGM.printf("%.2f", pgm.getNextChange());
				pwPGM.print(",");
				pwPGM.printf("%.2f", pgm.getDensProb());
				pwPGM.print(",");
				pwPGM.printf("%.2f", pgm.getEntropyDensity());
				pwPGM.print(",");
				pwPGM.printf("%.2f", pgm.getTargetChangeAbs());
				pwPGM.print(",");
				pwPGM.printf("%.2f", pgm.getTargetChange());
				pwPGM.print(",");
				pwPGM.printf("%.2f", pgm.getEntropyTarget());
				pwPGM.print(",");
				pwPGM.printf("%.2f", pgm.getMenPercent());
				pwPGM.print(",");
				pwPGM.printf("%.2f", pgm.getTimePosChange());
				pwPGM.print(",");
				pwPGM.printf("%.2f", pgm.getTimePosProb());
				pwPGM.print(",");
				pwPGM.printf("%.2f", pgm.getDeathProb());
				pwPGM.print(",");
				pwPGM.printf("%.2f", pgm.getAvgAge());
				pwPGM.print(",");
				pwPGM.printf("%.2f", pgm.getCausalProb()/pgm.getEntropyDensity());
				pwPGM.println();
				
//				pwPGM.println("("+fn.getFeatureName(pgm.getRow())+","+fn.getFeatureName(pgm.getCol())+")|"+pgm.getCausalProb()+"|"
//						+pgm.getPrevChange()+"|"+pgm.getNextChange()+"|"+pgm.getDensProb()+"|"+pgm.getEntropyDensity()+"|"
//						+pgm.getTargetChange()+"|"+pgm.getTargetProb()+"|"+pgm.getEntropyTarget()+"|"
//								+pgm.getMenPercent()+"|"+pgm.getTimePosChange()+"|"
//								+pgm.getTimePosProb()+"|");

			}//for
			
			pwPGM.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		printOutputFeatures();
//		System.out.print("Causal Prob = [");
//		for(int i=0; i<pgmMat.size();i++){
//			if(i==pgmMat.size()-1)
//				System.out.print(pgmMat.get(i).getCausalProb());
//			else
//				System.out.print(pgmMat.get(i).getCausalProb()+",");
//		}
//		System.out.print("]");
//		System.out.println();
//		
//		System.out.print("Target changes = [");
//		for(int i=0; i<pgmMat.size();i++){
//			if(i==pgmMat.size()-1)
//				System.out.print(pgmMat.get(i).getTargetChange());
//			else
//				System.out.print(pgmMat.get(i).getTargetChange()+",");
//		}
//		System.out.print("]");
		
		return pgmMat;
			
	}//createALSPGMMatrix
	
	private void printOutputFeatures(){
		
		for(int i=0; i<pgmMat.size();i++){
			if(!pgmFeatures.contains(pgmMat.get(i).getRow()))
				pgmFeatures.add(pgmMat.get(i).getRow());
			if(!pgmFeatures.contains(pgmMat.get(i).getCol()))
				pgmFeatures.add(pgmMat.get(i).getCol());			
		}//for(i)
		File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\"+classType1+"\\"+classType2+"\\CausalGraph\\CausalGraphFeatures.csv");
		PrintWriter pw = null;
		FeatureNameSDU fn = new FeatureNameSDU();
		
		try {
			pw = new PrintWriter(file);
			for(int i=0; i<pgmFeatures.size(); i++)
				pw.println(pgmFeatures.get(i)+"-"+fn.getFeatureName(pgmFeatures.get(i)));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pw.close();
		System.out.println("Number of output features in PGM is : "+pgmFeatures.size());
	}//printOutputFeatures
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
	
	
}//class MainPGM


