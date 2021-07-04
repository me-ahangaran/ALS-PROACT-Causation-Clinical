package sdu;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import als.ALSUtils;


//This class implements some utility function for ALS dataset
public class ALSUtilsSDU {

	public static int numOfDynamicFeatures = 87;	//number of dynamic features (including virtual feature)
	public static int minNumOfEvents = 3;	//minimum number of time points for the patients
	public static double slopeFastThreshold = -1.1;	//slope < threshold is fast patient
	public static double slopeSlowThreshold = -0.5;	//slope > threshold is slow patient (remaining patients are intermediate)
	public static String fastSlope = "Fast";
	public static String slowSlope = "Slow";
	public static String intermediateSlope = "Intermediate";
	public static String gender = "Gender";
	public static String progression = "Progression";
	public static String riluzole = "Riluzole";
	public static String male = "Male";
	public static String female = "Female";
	public static String riluzoleYes = "Yes";
	public static String riluzoleNo = "No";
	public static String alsfrs = "ALSFRS";
	public static String alsfrsR = "ALSFRS-R";
	
	//return the value of feature testNum
	public double getTestVal(ArrayList<String> testNames, ArrayList<String> testVals, int testNum, String[][] featuresNumNameChange){
		ALSUtilsSDU util = new ALSUtilsSDU();
		FeatureValueSDU fv = new FeatureValueSDU();
		String testName = util.getFeatureName(testNum, featuresNumNameChange);
		for(int i=0; i<testNames.size(); i++){
			if(testNames.get(i).equalsIgnoreCase(testName))
				return fv.getFeatureValue(testName, testVals.get(i));
//				return Double.parseDouble(testVals.get(i));
		}//for
		return -1;
	}//getTestVal
	
	//if list has repeated element return true else returen false
	public boolean hasRepeatedFeautre(ArrayList<Integer> list){
		int fNum;
		for(int i=0; i<list.size()-1; i++){
			fNum = list.get(i);
			for(int j=i+1; j<list.size(); j++){
				if(list.get(j) == fNum)
					return true;
			}//for(j)
		}//for(i)
		return false;
	}//hasRepeatedFeature
	
	public String getFeatureName(int featureNum, String[][] featuresNumNameChange){
		for(int i=0; i<featuresNumNameChange.length; i++){
			if(featureNum == Integer.parseInt(featuresNumNameChange[i][0]))
				return featuresNumNameChange[i][1];
		}//for(i)
		return null;
	}//getFeatureName
	
	public int getFeatureNum(String featureName, String[][] featuresNumNameChange){
		for(int i=0; i<featuresNumNameChange.length; i++){
			if(featureName.equalsIgnoreCase(featuresNumNameChange[i][1]))
				return Integer.parseInt(featuresNumNameChange[i][0]);
		}//for(i)
		return -1;
	}//getFeatureNum
	
	public double getFeatureChange(String featureName, String[][] featuresNumNameChange){
		for(int i=0; i<featuresNumNameChange.length; i++){
			if(featureName.equalsIgnoreCase(featuresNumNameChange[i][1]))
				return Double.parseDouble((featuresNumNameChange[i][2]));
		}//for(i)
		return 0;
	}//getFeatureChange
	
	public String[][] getALSFeaturesNumNameChange(){
		//each row contains: feature number, feature name, feature change
		String[][] answer = new String[numOfDynamicFeatures][3];
		try {
			BufferedReader input = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\FeaturesNumNameChange.txt"));
			String str = null;
			String[] line = null;
			int ctr = 0;	//counter of line in the file
			try {
				while ((str = input.readLine()) != null) {
				       line = getStrArray(str, 3);
				       answer[ctr][0] = line[0];
				       answer[ctr][1] = line[1];
				       answer[ctr][2] = line[2];
				       ctr++;
				}//while
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return answer;
	}//getALSFeaturesNumNameChange
	
	//return string component of a comma separated string
	public String[] getStrArray(String str, int numOfElements){
		
		char[] charStr = str.toCharArray();
		int[] commaIdx = new int[numOfElements-1];
		int ctr = 0;
		for(int i=0; i<charStr.length; i++){
			if(charStr[i] == '"'){
				i++;
				while(charStr[i] != '"')
					i++;
			}//if
			if(charStr[i] == ','){
				commaIdx[ctr] = i;
				ctr++;
			}//if
		}//for
		String[] strArray = new String[numOfElements];
		strArray[0] = str.substring(0,commaIdx[0]);
		strArray[numOfElements-1] = str.substring(commaIdx[numOfElements-2]+1,charStr.length);
		for(int i=1; i < strArray.length-1; i++){
			strArray[i] = str.substring(commaIdx[i-1]+1,commaIdx[i]);
		}
		return strArray;
		
	}//getStrArray
	
		//return string at the specific index position in the string with a specific separator 'separator char'
		public String getStrArrayElemIdx(String str, int numOfElements, int elemIdx, char separatorChar){
			
			char[] charStr = str.toCharArray();
			int[] commaIdx = new int[numOfElements-1];
			int ctr = 0;
			for(int i=0; i<charStr.length; i++){
				if(charStr[i] == '"'){
					i++;
					while(charStr[i] != '"')
						i++;
				}//if
				if(charStr[i] == separatorChar){
					commaIdx[ctr] = i;
					ctr++;
				}//if
			}//for
			String[] strArray = new String[numOfElements];
			strArray[0] = str.substring(0,commaIdx[0]);
			strArray[numOfElements-1] = str.substring(commaIdx[numOfElements-2]+1,charStr.length);
			
			for(int i=1; i < strArray.length-1; i++){
				strArray[i] = str.substring(commaIdx[i-1]+1,commaIdx[i]);
			}
			return strArray[elemIdx];
			
		}//getStrArray
	
	public ArrayList<ALSEventSDU> sortEventsSequence(ArrayList<ALSEventSDU> sequence){
		ALSEventSDU prev;
		ALSEventSDU next;
		ALSEventSDU temp;
		for(int i=0; i<sequence.size()-1; i++)
			for(int j=0; j<sequence.size()-i-1; j++){
				prev = new ALSEventSDU();
				prev = sequence.get(j);
				next = new ALSEventSDU();
				next = sequence.get(j+1);
				if(prev.getDelta() > next.getDelta()){
					temp = new ALSEventSDU();
					temp = prev;
					sequence.set(j, next);
					sequence.set(j+1, temp);
				}//if
			}//for(j)
				
		return sequence;
	}//sortEventsSequence
	
	
	//sort causal chains based on CTER parameter in descending order
	public ArrayList<CausalChainSDU> sortCausalChainsCTERDescending(ArrayList<CausalChainSDU> sequence){
		CausalChainSDU prev;
		CausalChainSDU next;
		CausalChainSDU temp;
		
		///////////////////////////////////////////////
		int numOfCC = sequence.size();
		double[] cterVals = new double[numOfCC];//list of CTER values
		for(int s=0; s<numOfCC; s++)
			cterVals[s] = sequence.get(s).getAvgCTER();
		
		for(int i=0; i<cterVals.length-1; i++){
			for(int j=0; j<cterVals.length-1-i; j++){
				if(cterVals[j] < cterVals[j+1]){
					double tempVal = cterVals[j];
					cterVals[j] = cterVals[j+1];
					cterVals[j+1] = tempVal;
				}//if
			}
		}
		
		ArrayList<CausalChainSDU> answer = new ArrayList<CausalChainSDU>();
		for(int i=0; i<cterVals.length; i++){
			double val = cterVals[i];
			for(int j=0; j<sequence.size(); j++){
				CausalChainSDU cc = sequence.get(j);
				if(cc.getAvgCTER() == val){
					answer.add(cc);
					break;
				}//if
			}
		}
		
		return answer;
		////////////////////////////////////////////////
		
//		for(int i=0; i<sequence.size()-1; i++)
//			for(int j=0; j<sequence.size()-i-1; j++){
//				prev = new CausalChain();
//				prev = sequence.get(j);
//				next = new CausalChain();
//				next = sequence.get(j+1);
//				if(prev.getAvgCTER() < next.getAvgCTER()){
//					temp = new CausalChain();
//					temp = prev;
//					sequence.set(j, next);
//					sequence.set(j+1, temp);
//				}//if
//			}//for(j)
//				
//		return sequence;

	}//sortCausalChainsCTERDescending
	
	//sort causal chains based on target entropy parameter in ascending order
		public ArrayList<CausalChainSDU> sortCausalChainsTargetEntropyAscending(ArrayList<CausalChainSDU> sequence){
			CausalChainSDU prev;
			CausalChainSDU next;
			CausalChainSDU temp;
			
			int numOfCC = sequence.size();
			double[] targetEntVals = new double[numOfCC];//list of CTER values
			for(int s=0; s<numOfCC; s++)
				targetEntVals[s] = sequence.get(s).getAvgTargetEntropy();
			
			for(int i=0; i<targetEntVals.length-1; i++){
				for(int j=0; j<targetEntVals.length-1-i; j++){
					if(targetEntVals[j] > targetEntVals[j+1]){
						double tempVal = targetEntVals[j];
						targetEntVals[j] = targetEntVals[j+1];
						targetEntVals[j+1] = tempVal;
					}//if
				}
			}
			
			ArrayList<CausalChainSDU> answer = new ArrayList<CausalChainSDU>();
			for(int i=0; i<targetEntVals.length; i++){
				double val = targetEntVals[i];
				for(int j=0; j<sequence.size(); j++){
					CausalChainSDU cc = sequence.get(j);
					if(cc.getAvgTargetEntropy() == val){
						answer.add(cc);
						break;
					}//if
				}
			}
			
			return answer;
			
//			for(int i=0; i<sequence.size()-1; i++)
//				for(int j=0; j<sequence.size()-i-1; j++){
//					prev = new CausalChain();
//					prev = sequence.get(j);
//					next = new CausalChain();
//					next = sequence.get(j+1);
//					if(prev.getAvgTargetEntropy() > next.getAvgTargetEntropy()){
//						temp = new CausalChain();
//						temp = prev;
//						sequence.set(j, next);
//						sequence.set(j+1, temp);
//					}//if
//				}//for(j)
//					
//			return sequence;
		}//sortCausalChainsTargetEntropyAscending
	
	//return list of all sequence of all patients in sorted format
	public ArrayList<ALSPatientSDU> getSortedPatientsSeq(){
		ALSDatasetSDU dataset = new ALSDatasetSDU();
		ArrayList<ALSPatientSDU> patientsList = dataset.createDataset();	//main patients dataset
		int numOfPatients = patientsList.size();
		ArrayList<ALSEventSDU> patientSequence;	//event sequence of each patient
		ArrayList<ALSEventSDU> sortedPatientSequence;	//sorted sequence of each patient
		ALSUtilsSDU util = new ALSUtilsSDU();
		for(int i=0; i<numOfPatients; i++){
			patientSequence = patientsList.get(i).getPatientSequence();
			sortedPatientSequence = util.sortEventsSequence(patientSequence);
			patientsList.get(i).setPatientSequence(sortedPatientSequence);
		}//for
		return patientsList;
	}//getSortedPatientsSeq	
	
	public double getMinVal(ArrayList<Double> list){
		double min = list.get(0);
		for(int i=1; i<list.size(); i++){
			if(list.get(i) < min)
				min = list.get(i);
		}//for
		return min;
	}//getMinVal
	
	public double getMaxVal(ArrayList<Double> list){
		double max = list.get(0);
		for(int i=1; i<list.size(); i++){
			if(list.get(i) > max)
				max = list.get(i);
		}//for
		return max;
	}//getMaxVal
	
		//convert a comma separated CSV file to a matrix (number of skip rows should be given)  
		public String[][] CSVFileToMatrix(String fileName, int numOfColumns){
			int numOfRows = getNumOfRowsFile(fileName);
			String[][] matrix = new String[numOfRows][numOfColumns];
			BufferedReader reader;
			ALSUtils util = new ALSUtils();
			String str;
			String[] line;
			int lineCtr = 0;//counter for lines
			try {
				reader = new BufferedReader(new FileReader(fileName));
				
				while((str = reader.readLine()) != null){
					line = util.getStrArray(str, numOfColumns);//read a line
					for(int i=0; i<numOfColumns; i++){
						matrix[lineCtr][i] = line[i];
					}//for
					lineCtr++;//go to next row of matrix
				}//while
				reader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return matrix;
		}//CSVFileToMatrix
		
		//convert a comma separated CSV file to an array (number of skip rows should be given)  
				public String[] CSVFileToArray(String fileName){
					int numOfRows = getNumOfRowsFile(fileName);
					String[] array = new String[numOfRows];
					BufferedReader reader;
					ALSUtils util = new ALSUtils();
					String str;
					String[] line;
					int lineCtr = 0;//counter for lines
					try {
						reader = new BufferedReader(new FileReader(fileName));
						
						while((str = reader.readLine()) != null){
							
							array[lineCtr] = str;
							
							lineCtr++;//go to next row of matrix
						}//while
						reader.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return array;
				}//CSVFileToArray
		
		private int getNumOfRowsFile(String filePath){
			int rows = 0;
			try {
				BufferedReader reader = new BufferedReader(new FileReader(filePath));
				String str;
				while((str = reader.readLine()) != null)
					rows++;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return rows;
		}//getNumOfRowsFile
		
		public static void main(String[] args) {
			ALSUtilsSDU util = new ALSUtilsSDU();
			String[] output = util.CSVFileToArray(
					"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\Progression\\Slow\\EntropyDensity.txt");
			ArrayList<Double> nums = new ArrayList<Double>();
			for(int i=0; i<output.length; i++) {
				double d = Double.valueOf(output[i]);
				if(d != -1)
					nums.add(d);
			}
			System.out.println("Min = "+util.getMinVal(nums));
			System.out.println("Max = "+util.getMaxVal(nums));
		}
}//class ALSUtils
