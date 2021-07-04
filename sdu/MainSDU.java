package sdu;

import java.util.ArrayList;
import java.util.List;

public class MainSDU {
	
	public boolean emptyLab(ALSPatientSDU patient){
		ArrayList<ALSEventSDU> seq = patient.getPatientSequence();
		for(int i=0; i<seq.size(); i++){
			LabDataSDU lab = seq.get(i).getLab();
			if(lab == null)
				return true;
		}
		return false;
	}//emptyLab

	public static void main(String[] args) {

		//read ALS features num,name,change
		ALSUtilsSDU util = new ALSUtilsSDU();
		
		//create ALS dataset
		
//		ALSDatasetSDU dataset = new ALSDatasetSDU();
//		ArrayList<ALSPatientSDU> patients = dataset.createDataset();
//		
//		/*select patients that its event sequence is not null
//		 only 6844 patients of 10000 patients have non-empty event sequence*/
//		ArrayList<ALSPatientSDU> selectedPatients = new ArrayList<ALSPatientSDU>();
//		for(int i=0; i<patients.size(); i++){
//			ALSPatientSDU currPatient = patients.get(i);
//			if(currPatient.getPatientSequence().size() >= ALSUtilsSDU.minNumOfEvents)
//				selectedPatients.add(currPatient);
//		}//for
//		
//		
//		//create dependency matrix W (no need to run it)
//		
//		List<ALSPatientSDU> trainPatients = selectedPatients.subList(0, 5000);//train set for
//		ArrayList<ALSPatientSDU> trainPatientsList = new ArrayList<ALSPatientSDU>(trainPatients);		
//		ALSDepMatSDU depMat = new ALSDepMatSDU();
//
//		//Create CFD matrix based on first and second type classes
//		System.out.println();
//		System.out.println("********************************************************************");
//		System.out.println();
//		//Progression
//		System.out.println("Creating CFD matrix files based on Progression ...");
//		depMat.createDependencyMatrix(trainPatientsList, ALSUtilsSDU.progression, ALSUtilsSDU.fastSlope);
//		System.gc();
//		depMat.createDependencyMatrix(trainPatientsList, ALSUtilsSDU.progression, ALSUtilsSDU.slowSlope);
//		System.gc();
//		depMat.createDependencyMatrix(trainPatientsList, ALSUtilsSDU.progression, ALSUtilsSDU.intermediateSlope);
//		System.gc();
//		
//		//Gender
//		System.out.println("Creating CFD matrix files based on Gender ...");
//		depMat.createDependencyMatrix(trainPatientsList, ALSUtilsSDU.gender, ALSUtilsSDU.male);
//		System.gc();
//		depMat.createDependencyMatrix(trainPatientsList, ALSUtilsSDU.gender, ALSUtilsSDU.female);
//		System.gc();
//		
//		//Riluzole
//		System.out.println("Creating CFD matrix files based on Riluzole ...");
//		depMat.createDependencyMatrix(trainPatientsList, ALSUtilsSDU.riluzole, ALSUtilsSDU.riluzoleNo);
//		System.gc();
//		depMat.createDependencyMatrix(trainPatientsList, ALSUtilsSDU.riluzole, ALSUtilsSDU.riluzoleYes);
//		System.gc();
//		System.out.println("All CFD matrix files has been created successfully.");
		
		////////////////////////////////////////////////////////////////////////////////////////////
		
		//create PGM of ALS
		
		ALSPGMSDU pgm = new ALSPGMSDU();
		ArrayList<ALSPGMMatrixSDU> pgmList = pgm.createALSPGMMatrix();
		ArrayList<Integer> pgmFeatures = pgm.getPgmFeatures();
		System.out.println("PGM was created successfully.");
		System.out.println("______________________________________________");
		
		////////////////////////////////////////////////////////////////////////////////////////////
		
		//Prediction phase based on PGM
		
//		ALSPrediction prediction = new ALSPrediction();
//		List<ALSPatient> testPatients = selectedPatients.subList(3000, 4000);
//		ArrayList<ALSPatient> testPatientsList = new ArrayList<ALSPatient>(testPatients);
//		prediction.predict(testPatientsList, pgmList, pgmFeatures, featuresNumNameChange);
		
		/////////////////////////////////////////////////////////////////////////////////////////////
		
		//create causal chains'
		ALSCausalChainSDU cc = new ALSCausalChainSDU();
		ArrayList<CausalChainSDU> causalChains = cc.getCausalChain();
		System.out.println("Creation of causal chains is finished succesfully.");
		
	}//main
}//MainALS
