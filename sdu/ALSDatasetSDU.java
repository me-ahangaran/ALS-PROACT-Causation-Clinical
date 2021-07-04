package sdu;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

//create dataset of all patients in includes: Demographics, AlsHistory, ConMeds, DeathData, Treatment, Riluzole, alsfrs, VitalSigns, Fvc, Labs
//in this dataset AdverseEvents, FamilyHistory, SVC are excluded
public class ALSDatasetSDU {
	
	//hash table of all patients including static and dynamic features for each patient (main dataset)
	private Hashtable<String, ALSPatientSDU> patients = new Hashtable<String, ALSPatientSDU>();
	private File statisticsFile;//statistics file including all information about dataset
	private PrintWriter pwStatistics;
	
	//Create dataset of ALS patients
	public ArrayList<ALSPatientSDU> createDataset(){
		BufferedReader input;	//read input file
		String str;	//string of each line
		String[] line;	//each line in the file
		ALSPatientSDU patient;	//patient record
		ALSUtilsSDU util = new ALSUtilsSDU();	//ALSUtils object
		String key = "";	//patient id as hash key
		
		/********************* Read "Demographics" file *****************************/
    	//(id, sex, age)
		try {
            input = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS\\ALS-Dataset\\Demographics.csv"));
            
            str = input.readLine();	//skip first line
            while ((str = input.readLine()) != null) {
               line = util.getStrArray(str, 3);
               key = line[0];	//patient id
               patient = new ALSPatientSDU(line[0], line[1], line[2]);	//create patient record
               patients.put(key, patient);	//add current patient to patients hash table               
            }//while
            input.close();
            System.out.println("Read \"Demographics\" file successfully.");
        } catch (IOException e) {
            System.out.println("\"Demographics\" file Read Error!");
        }		
		
		System.out.println("___________________________________________________");
		
		/********************* Read "SubjectALSHistory" file *****************************/
		//(SubjectID, Symptom,Location, Site of Onset, Onset Delta, Diagnosis Delta)
		try {
            input = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS\\ALS-Dataset\\AlsHistory.csv"));
            
            str = input.readLine();	//skip first line
            String symptom = "";
            String location = "";
            String siteOfOnset = "";
            String onsetDalta = "";
            String diagnosisDelta = "";
            while ((str = input.readLine()) != null) {
               line = util.getStrArray(str, 6);
               key = line[0];	//patient id
               symptom = line[1];
               location = line[2];
               siteOfOnset = line[3];
               onsetDalta = line[4];
               diagnosisDelta = line[5];
               patient = patients.get(key);	//get patient record from hash table
               if(!symptom.equalsIgnoreCase("") && !location.equalsIgnoreCase("")){
            	   SymptomLocationSDU sympLoc = new SymptomLocationSDU(symptom, location);
            	   patient.getSUBJECT_ALS_HISTORY_symptLoc().add(sympLoc);	//add a SymptomLocation object
               }//if
               if(!siteOfOnset.equalsIgnoreCase(""))
            	   patient.setSUBJECT_ALS_HISTORY_siteOfOnset(siteOfOnset);
               if(!onsetDalta.equalsIgnoreCase(""))
            	   patient.setSUBJECT_ALS_HISTORY_onsetDelta(Integer.parseInt(onsetDalta));
               if(!diagnosisDelta.equalsIgnoreCase(""))
            	   patient.setSUBJECT_ALS_HISTORY_diagnosisDelta(Integer.parseInt(diagnosisDelta));
            }//while
            input.close();
            System.out.println("Read \"SubjectALSHistory\" file successfully.");
        } catch (IOException e) {
            System.out.println("\"SubjectALSHistory\" file Read Error!");
        }
		
		System.out.println("___________________________________________________");
		
		/********************** Read "Concomitant Medication" file ****************************/
		//(SubjectID,Medication (Coded),Start Delta,Stop Delta,Unit,Dose,Route,Frequency)
		try {
            input = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS\\ALS-Dataset\\ConMeds.csv"));
            str = input.readLine();	//skip first line
            String medicationName = "";
            String startDelta = "";
            String stopDelta = "";
            String unit = "";
            String dose = "";
            String route = "";
            String frequency = "";
            MedicationSDU med;
            while ((str = input.readLine()) != null) {
               line = util.getStrArray(str, 8);
               key = line[0];	//patient id
               medicationName = line[1];
               startDelta = line[2];
               stopDelta = line[3];
               unit = line[4];
               dose = line[5];
               route = line[6];
               frequency = line[7];
               patient = patients.get(key);	//get patient record from hash table
               med = new MedicationSDU();
               
               if(!medicationName.equalsIgnoreCase("") && !medicationName.equalsIgnoreCase("NULL"))
            	   med.setMedicationName(medicationName);              
               if(!startDelta.equalsIgnoreCase("") && !startDelta.equalsIgnoreCase("NULL"))
            	   med.setStartDelta(Integer.parseInt(startDelta));
               if(!stopDelta.equalsIgnoreCase("") && !stopDelta.equalsIgnoreCase("NULL"))
            	   med.setStopDelta(Integer.parseInt(stopDelta));
               if(!unit.equalsIgnoreCase("") && !unit.equalsIgnoreCase("NULL"))
            	   med.setUnit(unit);
               if(!dose.equalsIgnoreCase("") && !dose.equalsIgnoreCase("NULL"))
            	   med.setDose(dose);
               if(!route.equalsIgnoreCase("") && !route.equalsIgnoreCase("NULL"))
            	   med.setRoute(route);
               if(!frequency.equalsIgnoreCase("") && !frequency.equalsIgnoreCase("NULL"))
            	   med.setFrequency(frequency);
               patient.getCONCOMITANT_MEDICATION_medication().add(med);
            }//while
            input.close();
            System.out.println("Read \"Concomitant Medication\" file successfully.");
        } catch (IOException e) {
            System.out.println("\"Concomitant Medication\" file Read Error!");
        }
		
		System.out.println("___________________________________________________");
		
		/********************** Read "Death Data" file ****************************/
		//(SubjectID, Subject Died, Death Days)
		try {
            input = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS\\ALS-Dataset\\DeathData.csv"));
            str = input.readLine();	//skip first line
            String subjectDied = "";
            String deathDay = "";
            while ((str = input.readLine()) != null) {
               line = util.getStrArray(str, 3);
               key = line[0];	//patient id
               subjectDied = line[1];
               deathDay = line[2];
               patient = patients.get(key);	//get patient record from hash table
               if(!subjectDied.equals(""))
            	   patient.setDEATH_DATA_subjectDied(subjectDied);
               if(subjectDied.equalsIgnoreCase("Yes"))
            	   if(!deathDay.equalsIgnoreCase(""))
            		   patient.setDEATH_DATA_deathDay(Integer.parseInt(deathDay));
            }//while
            input.close();
            System.out.println("Read \"Death Data\" file successfully.");
        } catch (IOException e) {
            System.out.println("\"Death Data\" file Read Error!");
        }
		
		System.out.println("___________________________________________________");
		
		/********************** Read "Treatment Group" file ****************************/
		//(SubjectID,Study Arm,Treatment Group Delta)
		try {
            input = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS\\ALS-Dataset\\Treatment.csv"));
            str = input.readLine();	//skip first line
            String studyArm = "";
            String treatmentGroupDelta = "";
            while ((str = input.readLine()) != null) {
               line = util.getStrArray(str, 3);
               key = line[0];	//patient id
               studyArm = line[1];
               treatmentGroupDelta = line[2];
               patient = patients.get(key);	//get patient record from hash table
               if(!studyArm.equals(""))
            	   patient.setTREATMENT_GROUP_studyArm(studyArm);
               if(!treatmentGroupDelta.equalsIgnoreCase(""))
            	   patient.setTREATMENT_GROUP_treatmentGroupDelta(Integer.parseInt(treatmentGroupDelta));
            }//while
            input.close();
            System.out.println("Read \"Treatment Group\" file successfully.");
        } catch (IOException e) {
            System.out.println("\"Treatment Group\" file Read Error!");
        }
		
		System.out.println("___________________________________________________");
		
		/********************** Read "Riluzole" file ****************************/
		//(SubjectID, Riluzole use Delta, Subject used Riluzole)
		try {
            input = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS\\ALS-Dataset\\Riluzole.csv"));
            str = input.readLine();	//skip first line
            String reluzoleUseDelta = "";
            String subjectUseReluzole = "";
            while ((str = input.readLine()) != null) {
               line = util.getStrArray(str, 3);
               key = line[0];	//patient id
               reluzoleUseDelta = line[1];
               subjectUseReluzole = line[2];
               patient = patients.get(key);	//get patient record from hash table
               if(!reluzoleUseDelta.equals(""))
            	   patient.setRELUZOLE_reluzoleUseDelta(Integer.parseInt(reluzoleUseDelta));
               if(!subjectUseReluzole.equalsIgnoreCase(""))
            	   patient.setRELUZOLE_subjectUseReluzole(subjectUseReluzole);
            }//while
            input.close();
            System.out.println("Read \"Riluzole\" file successfully.");
        } catch (IOException e) {
            System.out.println("\"Riluzole\" file Read Error!");
        }
		
		///////////////////////// Read dynamic files ///////////////////////////
		
		System.out.println("___________________________________________________");
		
		/************************ Read "ALS-FRS" file********************************/
		//(SubjectID, 1. Speech, 10. Respiratory, 2. Salivation, 3. Swallowing, 4. Handwriting,
		//5a. Cutting without Gastrostomy, 5b. Cutting with Gastrostomy, 6. Dressing and Hygiene,
		//7. Turning in Bed, 8. Walking, 9. Climbing Stairs, ALSFRS Delta, ALSFRS Total, ALSFRS-R Total)
		String delta = "";	//time of data
		ALSEventSDU event;
		ALSFRSSDU frs;
		try {
            input = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS\\ALS-Dataset\\ALSFRS.csv"));
             str = input.readLine();	//skip first line
             String speech = "";
             String respiratory = "";
             String salivation = "";
             String swallowing = "";
             String handwriting = "";
             String cuttingWithoutGastrostomy = "";
             String cuttingWithGastrostomy = "";
             String dressingAndHygiene = "";
             String turningInBed = "";
             String walking = "";
             String climbingStairs = "";
             String ALSFRSTotal = "";	//total score for ALSFRS (max is 40)
             String ALSFRSTotalR = ""; //total score for ALSFRS (max is 48)
             String R_1_Dyspnea = "";
             String R_2_Orthopnea = "";
             String R_3_Respiratory_Insufficiency = "";
          
            while ((str = input.readLine()) != null) {
               line = util.getStrArray(str, 18);
               key = line[0];	//patient id
               speech = line[1];
               salivation = line[2];
               swallowing = line[3];
               handwriting = line[4];
               cuttingWithoutGastrostomy = line[5];
               cuttingWithGastrostomy = line[6];
               dressingAndHygiene = line[7];
               turningInBed = line[8];
               walking = line[9];
               climbingStairs = line[10];
               respiratory = line[11];
               delta = line[12];
               ALSFRSTotal = line[13];
               ALSFRSTotalR = line[14];
               R_1_Dyspnea = line[15];
               R_2_Orthopnea = line[16];
               R_3_Respiratory_Insufficiency = line[17];
               patient = patients.get(key);	//get patient record from hash table
               event = new ALSEventSDU();	//create new event object
               if(!delta.equalsIgnoreCase(""))
            	   event.setDelta(Integer.parseInt(delta));	//set event time
               frs = new ALSFRSSDU();
               if(!speech.equalsIgnoreCase(""))
            	   frs.setSpeech(Double.parseDouble(speech));
               if(!respiratory.equalsIgnoreCase(""))
            	   frs.setRespiratory(Double.parseDouble(respiratory));
               if(!salivation.equalsIgnoreCase(""))
            	   frs.setSalivation(Double.parseDouble(salivation));
               if(!swallowing.equalsIgnoreCase(""))
            	   frs.setSwallowing(Double.parseDouble(swallowing));
               if(!handwriting.equalsIgnoreCase(""))
            	   frs.setHandwriting(Double.parseDouble(handwriting));
               if(!cuttingWithoutGastrostomy.equalsIgnoreCase(""))
            	   frs.setCuttingWithoutGastrostomy(Double.parseDouble(cuttingWithoutGastrostomy));
               if(!cuttingWithGastrostomy.equalsIgnoreCase(""))
            	   frs.setCuttingWithGastrostomy(Double.parseDouble(cuttingWithGastrostomy));
               if(!dressingAndHygiene.equalsIgnoreCase(""))
            	   frs.setDressingAndHygiene(Double.parseDouble(dressingAndHygiene));
               if(!turningInBed.equalsIgnoreCase(""))
            	   frs.setTurningInBed(Double.parseDouble(turningInBed));
               if(!walking.equalsIgnoreCase(""))
            	   frs.setWalking(Double.parseDouble(walking));
               if(!climbingStairs.equalsIgnoreCase(""))
            	   frs.setClimbingStairs(Double.parseDouble(climbingStairs));
               if(!ALSFRSTotal.equalsIgnoreCase(""))
            	   frs.setALSFRSTotal(Double.parseDouble(ALSFRSTotal));
               if(!ALSFRSTotalR.equalsIgnoreCase(""))
            	   frs.setALSFRSTotalR(Double.parseDouble(ALSFRSTotalR));
               if(!R_1_Dyspnea.equalsIgnoreCase(""))
            	   frs.setR_1_Dyspnea(Double.parseDouble(R_1_Dyspnea));
               if(!R_2_Orthopnea.equalsIgnoreCase(""))
            	   frs.setR_2_Orthopnea(Double.parseDouble(R_2_Orthopnea));
               if(!R_3_Respiratory_Insufficiency.equalsIgnoreCase(""))
            	   frs.setR_3_Respiratory_Insufficiency(Double.parseDouble(R_3_Respiratory_Insufficiency));
            	   
               event.setTarget(frs);	//set current ALSFRS object to the event target
               patient.getPatientSequence().add(event);	//add current event to the current patient record
               if(!ALSFRSTotalR.equalsIgnoreCase("")) 
            	   patient.setAlsfrsType(ALSUtilsSDU.alsfrsR);
               
               else 
            	   patient.setAlsfrsType(ALSUtilsSDU.alsfrs);
            	   
               
            }//while
            input.close();
            
            System.out.println("Read \"ALS-FRS\" file successfully.");
        } catch (IOException e) {
            System.out.println("\"ALS-FRS\" file Read Error!");
        }
		
		System.out.println("___________________________________________________");
		
		/********************** Read "Vital Sign" file ****************************/
		//(SubjectID, Blood Pressure (Diastolic), Blood Pressure (Systolic), Vital Signs Delta,
		//Pulse, Respiratory Rate, Temperature, Weight)
		VitalSignSDU vital;
		ArrayList<ALSEventSDU> patientSequence;	//event sequence for each patient
		try {
            input = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS\\ALS-Dataset\\VitalSigns.csv"));
            str = input.readLine();	//skip first line
            String bloodPressureDiastolic = "";
            String bloodPressureSystolic = "";
            String pulse = "";
            String respiratoryRate = "";
            String temprature = "";
            String weight = "";
            while ((str = input.readLine()) != null) {
               line = util.getStrArray(str, 8);
               key = line[0];	//patient id
               bloodPressureDiastolic = line[1];
               bloodPressureSystolic = line[2];
               delta = line[3];
               pulse = line[4];
               respiratoryRate = line[5];
               temprature = line[6];
               weight = line[7];
               patient = patients.get(key);	//get patient record from hash table
               vital = new VitalSignSDU();
               if(!bloodPressureDiastolic.equalsIgnoreCase(""))
            	   vital.setBloodPressureDiastolic(Integer.parseInt(bloodPressureDiastolic));
               if(!bloodPressureSystolic.equalsIgnoreCase(""))
            	   vital.setBloodPressureSystolic(Integer.parseInt(bloodPressureSystolic));
               if(!pulse.equalsIgnoreCase(""))
            	   vital.setPulse(Integer.parseInt(pulse));
               if(!respiratoryRate.equalsIgnoreCase(""))
            	   vital.setRespiratoryRate(Double.parseDouble(respiratoryRate));
               if(!temprature.equalsIgnoreCase(""))
            	   vital.setTemprature(Double.parseDouble(temprature));
               if(!weight.equalsIgnoreCase(""))
            	   vital.setWeight(Double.parseDouble(weight));
               
               patientSequence = patient.getPatientSequence();	//current patient sequence
               //add vital sign data to the corresponding patient record
               if(!delta.equalsIgnoreCase(""))
	               for(int i=0; i<patientSequence.size(); i++){
	            	   event = patientSequence.get(i);
	            	   if(event.getDelta() == Integer.parseInt(delta))
	            		   event.setVitalSign(vital);
	               }//for            		   
            }//while
            input.close();
            System.out.println("Read \"Vital Sign\" file successfully.");
        } catch (IOException e) {
            System.out.println("\"Vital Sign\" file Read Error!");
        }
		
		System.out.println("___________________________________________________");
		
		/********************** Read "FVC" file ****************************/
		//(SubjectID,Subject Liters (Trial 1),Forced Vital Capacity Delta)
		try {
            input = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS\\ALS-Dataset\\Fvc.csv"));
            str = input.readLine();	//skip first line
            String FVC_subjectLiters = "";
            while ((str = input.readLine()) != null) {
               line = util.getStrArray(str, 3);
               key = line[0];	//patient id
               FVC_subjectLiters = line[1];
               delta = line[2];
               patient = patients.get(key);	//get patient record from hash table
               patientSequence = patient.getPatientSequence();	//current patient sequence
               //add vital sign data to the corresponding patient record
               if(!delta.equalsIgnoreCase(""))
	               for(int i=0; i<patientSequence.size(); i++){
	            	   event = patientSequence.get(i);
	            	   if(event.getDelta() == Integer.parseInt(delta))
	            		   if(!FVC_subjectLiters.equalsIgnoreCase(""))
	            			   event.setFVC_subjectLiters(Double.parseDouble(FVC_subjectLiters));
	               }//for
            }//while
            input.close();
            System.out.println("Read \"FVC\" file successfully.");
        } catch (IOException e) {
            System.out.println("\"FVC\" file Read Error!");
        }
		
		System.out.println("___________________________________________________");
		
		/********************** Read "Lab Data" file ****************************/
		//("SubjectID", "Test Name", "Test Result", "Test Unit", "Laboratory Delta")
		LabDataSDU lab = null;	//LabData object
		String testName = "";
		String testResult = "";
		try {
            input = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS\\ALS-Dataset\\Labs.csv"));
            str = input.readLine();	//skip first line
            while ((str = input.readLine()) != null) {
               line = util.getStrArray(str, 5);
               key = line[0];	//patient id
               testName = line[1];
               testResult = line[2];
               String testUnit = line[3];//this line is added in Jan 2019 (unit of test, ex. 10E9/L)
               delta = line[4];
               //if test result is null get another line from file
               if(testResult.equalsIgnoreCase(""))
            	   continue;
               //////////////////////////////This code is added in Jan 2019////////////////////
               if(testResult.equalsIgnoreCase("0") || testResult.equalsIgnoreCase("-"))
            	   continue;               
               ///////////////////////////////////////////////////////////////////////////////
               if(delta.equalsIgnoreCase(""))
            	   continue;
               patient = patients.get(key);	//get patient record from hash table
               patientSequence = patient.getPatientSequence();	//current patient sequence
               for(int i=0; i<patientSequence.size(); i++){
	            	   event = patientSequence.get(i);
	            	   if(event.getDelta() == Integer.parseInt(delta)){
	            		   if(event.getLab() == null){
	            			   lab = new LabDataSDU();
	            			   event.setLab(lab);
	            		   }//if
	            		   lab = event.getLab();
	            		   break;	//find related patient record
	               	   }//if
	            	   //there is not current delta for patient
	            	   if(i==patientSequence.size()){
	            		   lab = new LabDataSDU();
	            		   event.setLab(lab);
	            		   event.setDelta(Integer.parseInt(delta));
	            	   }//if
	            }//for
               
               //////////////////// Urine ///////////////////
               
               if(testName.equalsIgnoreCase("Urine Ph")){
            	   lab.setURINE_urinePH(testResult);
            	   lab.getTestNames().add("URINE_urinePH");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine Protein")){
            	   lab.setURINE_urineProtein(testResult);
            	   lab.getTestNames().add("URINE_urineProtein");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine Specific Gravity")){
            	   lab.setURINE_urineSpecificGravity(testResult);
            	   lab.getTestNames().add("URINE_urineSpecificGravity");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine Glucose")){
            	   lab.setURINE_urineGlucose(testResult);
            	   lab.getTestNames().add("URINE_urineGlucose");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine WBCs")){
            	   lab.setURINE_urineWBC(testResult);
            	   lab.getTestNames().add("URINE_urineWBC");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine Leukesterase")){
            	   lab.setURINE_urineLeukesterase(testResult);
            	   lab.getTestNames().add("URINE_urineLeukesterase");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine blood")){
            	   lab.setURINE_urineBlood(testResult);
            	   lab.getTestNames().add("URINE_urineBlood");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine RBCs")){
            	   lab.setURINE_urineRBCs(testResult);
            	   lab.getTestNames().add("URINE_urineRBCs");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine Casts")){	//always negative
            	   lab.setURINE_urineCasts(testResult);
            	   lab.getTestNames().add("URINE_urineCasts");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine Ketones")){	//always negative
            	   lab.setURINE_urineKetones(testResult);
            	   lab.getTestNames().add("URINE_urineKetones");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine Appearance")){	
            	   lab.setURINE_urineAppearance(testResult);
            	   lab.getTestNames().add("URINE_urineAppearance");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine Color")){	
            	   lab.setURINE_urineColor(testResult);
            	   lab.getTestNames().add("URINE_urineColor");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine Bacteria")){	
            	   lab.setURINE_urineBacteria(testResult);
            	   lab.getTestNames().add("URINE_urineBacteria");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine Mucus")){	
            	   lab.setURINE_urineMucus(testResult);
            	   lab.getTestNames().add("URINE_urineMucus");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine Albumin")){	
            	   lab.setURINE_urineAlbumin(testResult);
            	   lab.getTestNames().add("URINE_urineAlbumin");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine Uric Acid Crystals")){	
            	   lab.setURINE_urineUricAcidCrystals(testResult);
            	   lab.getTestNames().add("URINE_urineUricAcidCrystals");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Urine Calcium Oxalate Crystals")){	
            	   lab.setURINE_urineCalciumOxalateCrystals(testResult);
            	   lab.getTestNames().add("URINE_urineCalciumOxalateCrystals");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               
               /////////////////// Blood proteins ///////////////////////
               
               if(testName.equalsIgnoreCase("Albumin")){	
            	   lab.setBLOOD_PROTEINS_albumin(testResult);
            	   lab.getTestNames().add("BLOOD_PROTEINS_albumin");
            	   lab.getTestResults().add(testResult);            	   
            	   continue;
               }
               if(testName.equalsIgnoreCase("protein")){	
            	   lab.setBLOOD_PROTEINS_protein(testResult);
            	   lab.getTestNames().add("BLOOD_PROTEINS_protein");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               
               /////////////////// Electrolytes ///////////////////////
               
               if(testName.equalsIgnoreCase("Sodium")){	
            	   lab.setELECTROLYTES_sodium(testResult);
            	   lab.getTestNames().add("ELECTROLYTES_sodium");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Potassium")){	
            	   lab.setELECTROLYTES_potassium(testResult);
            	   lab.getTestNames().add("ELECTROLYTES_potassium");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Bicarbonate")){	
            	   lab.setELECTROLYTES_bicarbonate(testResult);
            	   lab.getTestNames().add("ELECTROLYTES_bicarbonate");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Chloride")){	
            	   lab.setELECTROLYTES_chloride(testResult);
            	   lab.getTestNames().add("ELECTROLYTES_chloride");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Anion Gap")){	
            	   lab.setELECTROLYTES_anionGap(testResult);
            	   lab.getTestNames().add("ELECTROLYTES_anionGap");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Magnesium")){	
            	   lab.setELECTROLYTES_magnesium(testResult);
            	   lab.getTestNames().add("ELECTROLYTES_magnesium");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               
               /////////////////// Kidney tests ///////////////////////
               
               if(testName.equalsIgnoreCase("Blood Urea Nitrogen (BUN)")){	
            	   lab.setKIDNEY_bloodUreaNitrogen(testResult);
            	   lab.getTestNames().add("KIDNEY_bloodUreaNitrogen");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Uric Acid")){	
            	   lab.setKIDNEY_uricAcid(testResult);
            	   lab.getTestNames().add("KIDNEY_uricAcid");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               if(testName.equalsIgnoreCase("Creatinine")){	
            	   lab.setKIDNEY_creatinine(testResult);
            	   lab.getTestNames().add("KIDNEY_creatinine");
            	   lab.getTestResults().add(testResult);
            	   continue;
               }
               
                /////////////////// Liver tests ///////////////////////
               
	            if(testName.equalsIgnoreCase("Alkaline Phosphatase")){	
	         	   lab.setLIVER_alkalinePhosphatase(testResult);
	         	   lab.getTestNames().add("LIVER_alkalinePhosphatase");
	         	   lab.getTestResults().add(testResult);
	         	   continue;
	            }
	            if(testName.equalsIgnoreCase("ALT(SGPT)")){	
	         	   lab.setLIVER_alt(testResult);
	         	   lab.getTestNames().add("LIVER_alt");
	         	   lab.getTestResults().add(testResult);
	         	   continue;
	            }
	            if(testName.equalsIgnoreCase("Gamma-glutamyltransferase")){	
	         	   lab.setLIVER_gammaGlutamylTransferase(testResult);
	         	   lab.getTestNames().add("LIVER_gammaGlutamylTransferase");
	         	   lab.getTestResults().add(testResult);
	         	   continue;
	            }
	            if(testName.equalsIgnoreCase("AST(SGOT)")){	
		         	   lab.setLIVER_ast(testResult);
		         	  lab.getTestNames().add("LIVER_ast");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Bilirubin (Total)")){	
		         	   lab.setLIVER_bilirubinTotal(testResult);
		         	  lab.getTestNames().add("LIVER_bilirubinTotal");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            
	            /////////////////// Complete blood count ///////////////////////
	            
	            if(testName.equalsIgnoreCase("White Blood Cell (WBC)")){	
		         	   lab.setCOMPLETE_BLOOD_whiteBloodCell(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_whiteBloodCell");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Neutrophils")){	
		         	   lab.setCOMPLETE_BLOOD_neutrophils(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_neutrophils");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Absolute Neutrophil Count")){	
		         	   lab.setCOMPLETE_BLOOD_absoluteNeutrophilCount(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_absoluteNeutrophilCount");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Band Neutrophils")){	
		         	   lab.setCOMPLETE_BLOOD_bandNeutrophils(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_bandNeutrophils");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Absolute Band Neutrophil Count")){	
		         	   lab.setCOMPLETE_BLOOD_absoluteBandNeutrophilCount(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_absoluteBandNeutrophilCount");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Lymphocytes")){	
		         	   lab.setCOMPLETE_BLOOD_lymphocytes(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_lymphocytes");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Absolute Lymphocyte Count")){	
		         	   lab.setCOMPLETE_BLOOD_absoluteLymphocyteCount(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_absoluteLymphocyteCount");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Monocytes")){	
		         	   lab.setCOMPLETE_BLOOD_monocytes(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_monocytes");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Absolute Monocyte Count")){	
		         	   lab.setCOMPLETE_BLOOD_absoluteMonocyteCount(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_absoluteMonocyteCount");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Eosinophils")){	
		         	   lab.setCOMPLETE_BLOOD_eosinophils(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_eosinophils");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Absolute Eosinophil Count")){	
		         	   lab.setCOMPLETE_BLOOD_absoluteEosinophilCount(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_absoluteEosinophilCount");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Basophils")){	
		         	   lab.setCOMPLETE_BLOOD_basophils(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_basophils");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Absolute Basophil Count")){	
		         	   lab.setCOMPLETE_BLOOD_absoluteBasophilCount(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_absoluteBasophilCount");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Red Blood Cells (RBC)")){
	            		////////////////////This code is added in Jan 2019////////////////
	            		double rbcVal;
	            		if(testUnit.equalsIgnoreCase("10E9")){
	            			rbcVal = Double.parseDouble(testResult);
	            			rbcVal = rbcVal / 1000;
	            			testResult = String.valueOf(rbcVal);
	            		}//if
	            		/////////////////////////////////////////////////////////////////
		         	   lab.setCOMPLETE_BLOOD_redBloodCells(testResult);
		         	   lab.getTestNames().add("COMPLETE_BLOOD_redBloodCells");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Hemoglobin")){	
		         	   lab.setCOMPLETE_BLOOD_hemoglobin(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_hemoglobin");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Hematocrit")){	
		         	   lab.setCOMPLETE_BLOOD_hematocrit(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_hematocrit");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Mean Corpuscular Hemoglobin")){	
		         	   lab.setCOMPLETE_BLOOD_meanCorpuscularHemoglobin(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_meanCorpuscularHemoglobin");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Mean Corpuscular Hemoglobin Concentration")){	
		         	   lab.setCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Mean Corpuscular Volume")){	
		         	   lab.setCOMPLETE_BLOOD_meanCorpuscularVolume(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_meanCorpuscularVolume");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Platelets")){	
		         	   lab.setCOMPLETE_BLOOD_platelets(testResult);
		         	  lab.getTestNames().add("COMPLETE_BLOOD_platelets");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            
	            /////////////////// Heart disease and muscle degradation ///////////////////////
	            
	            if(testName.equalsIgnoreCase("CK")){	
		         	   lab.setHEART_DISEASE_ck(testResult);
		         	  lab.getTestNames().add("HEART_DISEASE_ck");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Triglycerides")){	
		         	   lab.setHEART_DISEASE_triglycerides(testResult);
		         	  lab.getTestNames().add("HEART_DISEASE_triglycerides");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }	            
	            if(testName.equalsIgnoreCase("Total Cholesterol")){	
		         	   lab.setHEART_DISEASE_totalCholesterol(testResult);
		         	  lab.getTestNames().add("HEART_DISEASE_totalCholesterol");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Lactate Dehydrogenase")){	
		         	   lab.setHEART_DISEASE_lactateDehydrogenase(testResult);
		         	  lab.getTestNames().add("HEART_DISEASE_lactateDehydrogenase");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            
	            /////////////////// Blood sugar ///////////////////////
	            
	            if(testName.equalsIgnoreCase("Glucose")){	
		         	   lab.setBLOOD_SUGAR_glucose(testResult);
		         	  lab.getTestNames().add("BLOOD_SUGAR_glucose");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("HbA1c (Glycated Hemoglobin)")){	
		         	   lab.setBLOOD_SUGAR_HbA1c(testResult);
		         	  lab.getTestNames().add("BLOOD_SUGAR_HbA1c");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            
	            /////////////////// Mineral balance ///////////////////////
	            
	            if(testName.equalsIgnoreCase("Calcium")){	
		         	   lab.setMINERAL_BALANCE_calcium(testResult);
		         	  lab.getTestNames().add("MINERAL_BALANCE_calcium");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Phosphorus")){	
		         	   lab.setMINERAL_BALANCE_phosphorus(testResult);
		         	  lab.getTestNames().add("MINERAL_BALANCE_phosphorus");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            
	            /////////////////// Immune response ///////////////////////
	            
	            if(testName.equalsIgnoreCase("IMMUNOGLOBULIN A")){	
		         	   lab.setIMMUNE_RESPONSE_immunoglobulinA(testResult);
		         	  lab.getTestNames().add("IMMUNE_RESPONSE_immunoglobulinA");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("IMMUNOGLOBULIN G")){	
		         	   lab.setIMMUNE_RESPONSE_immunoglobulinG(testResult);
		         	  lab.getTestNames().add("IMMUNE_RESPONSE_immunoglobulinG");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("IMMUNOGLOBULIN M")){	
		         	   lab.setIMMUNE_RESPONSE_immunoglobulinM(testResult);
		         	  lab.getTestNames().add("IMMUNE_RESPONSE_immunoglobulinM");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("GAMMA-GLOBULIN")){	
		         	   lab.setIMMUNE_RESPONSE_gammaGlobulin(testResult);
		         	  lab.getTestNames().add("IMMUNE_RESPONSE_gammaGlobulin");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("ALPHA1-GLOBULIN")){	
		         	   lab.setIMMUNE_RESPONSE_alpha1Globulin(testResult);
		         	  lab.getTestNames().add("IMMUNE_RESPONSE_alpha1Globulin");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("ALPHA2-GLOBULIN")){	
		         	   lab.setIMMUNE_RESPONSE_alpha2Globulin(testResult);
		         	  lab.getTestNames().add("IMMUNE_RESPONSE_alpha2Globulin");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("BETA-GLOBULIN")){	
		         	   lab.setIMMUNE_RESPONSE_betaGlobulin(testResult);
		         	  lab.getTestNames().add("IMMUNE_RESPONSE_betaGlobulin");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Albumin/globulin ratio")){	
		         	   lab.setIMMUNE_RESPONSE_albuminGlobulinRatio(testResult);
		         	  lab.getTestNames().add("IMMUNE_RESPONSE_albuminGlobulinRatio");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            
	            /////////////////// Hormones ///////////////////////
	            
	            if(testName.equalsIgnoreCase("TSH")){	
		         	   lab.setHORMONES_TSH(testResult);
		         	  lab.getTestNames().add("HORMONES_TSH");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Free T3")){	
		         	   lab.setHORMONES_FreeT3(testResult);
		         	  lab.getTestNames().add("HORMONES_FreeT3");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Free T4")){	
		         	   lab.setHORMONES_FreeT4(testResult);
		         	  lab.getTestNames().add("HORMONES_FreeT4");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Beta HCG")){	
		         	   lab.setHORMONES_betaHCG(testResult);
		         	  lab.getTestNames().add("HORMONES_betaHCG");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            
	            /////////////////// Coagulation measures ///////////////////////
	            
	            if(testName.equalsIgnoreCase("Prothrombin Time (clotting)")){	
		         	   lab.setCOAGULATION_prothrombinTime(testResult);
		         	  lab.getTestNames().add("COAGULATION_prothrombinTime");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("International Normalized Ratio (clotting)")){	
		         	   lab.setCOAGULATION_internationalNormalizedRatio(testResult);
		         	  lab.getTestNames().add("COAGULATION_internationalNormalizedRatio");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            
	            /////////////////// Others ///////////////////////
	            
	            if(testName.equalsIgnoreCase("Amylase")){	
		         	   lab.setOTHERS_amylase(testResult);
		         	  lab.getTestNames().add("OTHERS_amylase");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Salivary Amylase")){	
		         	   lab.setOTHERS_salivaryAmylase(testResult);
		         	  lab.getTestNames().add("OTHERS_salivaryAmylase");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            if(testName.equalsIgnoreCase("Pancreatic Amylase")){	
		         	   lab.setOTHERS_pancreaticAmylase(testResult);
		         	  lab.getTestNames().add("OTHERS_pancreaticAmylase");
		         	   lab.getTestResults().add(testResult);
		         	   continue;
		        }
	            
            }//while
            input.close();
            System.out.println("Read \"Lab Data\" file successfully.");
        } catch (IOException e) {
            System.out.println("\"Lab Data\" file Read Error!");
        }
		System.out.println();
		System.out.println("*****************************************************************************");
		System.out.println();
		System.out.println("ALS dataset has been create successfully.");
		ArrayList<ALSPatientSDU> patientsList = new ArrayList<ALSPatientSDU>(patients.values());
		int alsfrsCtr = 0;
        int alsfrsRCtr = 0;
        int alsfrsNullCtr = 0;
        for(int i=0; i<patientsList.size(); i++) {
        	ALSPatientSDU patientFrs = patientsList.get(i);
        	if(patientFrs.getAlsfrsType() == null) {
        		alsfrsNullCtr++;
        		continue;
        	}
        	if(patientFrs.getAlsfrsType().equalsIgnoreCase(util.alsfrsR))
        		alsfrsRCtr++;
        	else
        		alsfrsCtr++;
        }
        statisticsFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\Progression\\Statistics.txt");
        try {
			pwStatistics = new PrintWriter(statisticsFile);
			pwStatistics.println("Number of patients with ALSFRS-R = "+alsfrsRCtr);
			pwStatistics.println("Number of patients with ALSFRS = "+alsfrsCtr);
			pwStatistics.println("Number of patients with NULL ALSFRS = "+alsfrsNullCtr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		setSlopes(patientsList);
		createSlopeFiles(patientsList);
		createALSFRSFiles(patientsList);
		pwStatistics.close();
		return patientsList;
	}//createDataset
	
	//calculate slopes of the patients
	private void setSlopes(ArrayList<ALSPatientSDU> patientsList) {
		int ctr = 0;
		int ctrFast = 0;
		int ctrSlow = 0;
		int ctrIntermediate = 0;
		ALSPatientSDU patient = null; 
		ArrayList<ALSEventSDU> eventsSequence= null;
		double firstDay, lastDay, months, slope;
		double firstFrs, lastFrs;
		for(int i=0; i<patientsList.size(); i++) {
			slope = 0; firstFrs = 0; lastFrs = 0;
			patient = patientsList.get(i);
			eventsSequence = patient.getPatientSequence();
			int eventsSeqSize = eventsSequence.size();
			
			if(eventsSeqSize < ALSUtilsSDU.minNumOfEvents)
				continue;
			
			if(eventsSequence.get(0).getTarget().getALSFRSTotal() != -1)
				firstFrs = eventsSequence.get(0).getTarget().getALSFRSTotal();
			else
				firstFrs = eventsSequence.get(0).getTarget().getALSFRSTotalR();
			
			if(eventsSequence.get(eventsSeqSize-1).getTarget().getALSFRSTotal() != -1)
				lastFrs = eventsSequence.get(eventsSeqSize-1).getTarget().getALSFRSTotal();
			else
				lastFrs = eventsSequence.get(eventsSeqSize-1).getTarget().getALSFRSTotalR();
			firstDay = eventsSequence.get(0).getDelta();
			lastDay = eventsSequence.get(eventsSeqSize-1).getDelta();
			months = Math.ceil((lastDay - firstDay) / 30);
			slope = (lastFrs - firstFrs) / months;
			patient.setSlope(slope);
			if(slope < ALSUtilsSDU.slopeFastThreshold) {
				patient.setSlopeType(ALSUtilsSDU.fastSlope);
				ctrFast++;
			}
			
			if(slope > ALSUtilsSDU.slopeSlowThreshold) {
					patient.setSlopeType(ALSUtilsSDU.slowSlope);
					ctrSlow++;
			}
			
			if(slope >= ALSUtilsSDU.slopeFastThreshold && slope <= ALSUtilsSDU.slopeSlowThreshold) {
				patient.setSlopeType(ALSUtilsSDU.intermediateSlope);
				ctrIntermediate++;
			}
					
			ctr++;
		}//for(i)
		pwStatistics.println("number of patients with number of events more than "
		+ALSUtilsSDU.minNumOfEvents+" = "+ctr);
		pwStatistics.println("Number of Fast patients = "+ctrFast);
		pwStatistics.println("Number of Intermediate patients = "+ctrIntermediate);
		pwStatistics.println("Number of Slow patients = "+ctrSlow);
	}//setSlopes
	
	private void createALSFRSFiles(ArrayList<ALSPatientSDU> patientsList) {
		File ALSFRSFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\Progression\\ALSFRS.csv");
		File fastSlopesALSFRSFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\Progression\\Fast\\FastALSFRS.csv");
		File IntermediateSlopesALSFRSFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\Progression\\Intermediate\\IntermediateALSFRS.csv");
		File slowSlopesALSFRSFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\Progression\\Slow\\SlowALSFRS.csv");
		
		PrintWriter pwALSFRS, pwSlopeFastALSFRS,pwSlopeIntermediateALSFRS,pwSlopeSlowALSFRS;
		
		ArrayList<ALSEventSDU> eventsSequence= null;
		ALSPatientSDU patient = null; 
		try {
			pwALSFRS = new PrintWriter(ALSFRSFile);
			pwSlopeFastALSFRS = new PrintWriter(fastSlopesALSFRSFile);
			pwSlopeIntermediateALSFRS = new PrintWriter(IntermediateSlopesALSFRSFile);
			pwSlopeSlowALSFRS = new PrintWriter(slowSlopesALSFRSFile);
			
			double currSlope;
			for(int i=0; i<patientsList.size(); i++) {
				patient = patientsList.get(i);
				eventsSequence = patient.getPatientSequence();
				int eventsSeqSize = eventsSequence.size();
				
				if(eventsSeqSize < ALSUtilsSDU.minNumOfEvents)
					continue;
				
				if(patient.getAlsfrsType().equalsIgnoreCase(ALSUtilsSDU.alsfrsR)) {
					for(int k=0; k< eventsSeqSize; k++) {
						pwALSFRS.print(eventsSequence.get(k).getTarget().getALSFRSTotalR()+",");
					}
					pwALSFRS.println();
					
				}
				else {
					for(int k=0; k< eventsSeqSize; k++) {
						pwALSFRS.print(eventsSequence.get(k).getTarget().getALSFRSTotal()+",");
					}
					pwALSFRS.println();
				}
				
					if(patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.fastSlope)) {
						if(patient.getAlsfrsType().equalsIgnoreCase(ALSUtilsSDU.alsfrsR)) {
							for(int j=0; j< eventsSeqSize; j++) {
								pwSlopeFastALSFRS.print(eventsSequence.get(j).getTarget().getALSFRSTotalR()+",");
							}
							pwSlopeFastALSFRS.println();
							
						}
						else {
							for(int j=0; j< eventsSeqSize; j++) {
								pwSlopeFastALSFRS.print(eventsSequence.get(j).getTarget().getALSFRSTotal()+",");
							}
							pwSlopeFastALSFRS.println();
						}
							
					}
					
					if(patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.intermediateSlope)) {
						if(patient.getAlsfrsType().equalsIgnoreCase(ALSUtilsSDU.alsfrsR)) {
							for(int j=0; j< eventsSeqSize; j++) {
								pwSlopeIntermediateALSFRS.print(eventsSequence.get(j).getTarget().getALSFRSTotalR()+",");
							}
							pwSlopeIntermediateALSFRS.println();
							
						}
						else {
							for(int j=0; j< eventsSeqSize; j++) {
								pwSlopeIntermediateALSFRS.print(eventsSequence.get(j).getTarget().getALSFRSTotal()+",");
							}
							pwSlopeIntermediateALSFRS.println();
						}
							
					}
					
					if(patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.slowSlope)) {
						if(patient.getAlsfrsType().equalsIgnoreCase(ALSUtilsSDU.alsfrsR)) {
							for(int j=0; j< eventsSeqSize; j++) {
								pwSlopeSlowALSFRS.print(eventsSequence.get(j).getTarget().getALSFRSTotalR()+",");
							}
							pwSlopeSlowALSFRS.println();
							
						}
						else {
							for(int j=0; j< eventsSeqSize; j++) {
								pwSlopeSlowALSFRS.print(eventsSequence.get(j).getTarget().getALSFRSTotal()+",");
							}
							pwSlopeSlowALSFRS.println();
						}
							
					}
					
			}//for(i)
			
			pwALSFRS.close();
			pwSlopeFastALSFRS.close();
			pwSlopeIntermediateALSFRS.close();
			pwSlopeSlowALSFRS.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//createALSFRSFile
	
	private void createSlopeFiles(ArrayList<ALSPatientSDU> patientsList) {
		File slopesFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\Progression\\Slopes.csv");
		File fastSlopesFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\Progression\\Fast\\FastSlopes.csv");
		File IntermediateSlopesFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\Progression\\Intermediate\\IntermediateSlopes.csv");
		File slowSlopesFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\SDU\\Progression\\Slow\\SlowSlopes.csv");
		
		
		
		PrintWriter pwSlope, pwSlopeFast,pwSlopeIntermediate,pwSlopeSlow;
		
		ArrayList<ALSEventSDU> eventsSequence= null;
		ALSPatientSDU patient = null; 
		try {
			pwSlope = new PrintWriter(slopesFile);
			pwSlopeFast = new PrintWriter(fastSlopesFile);
			pwSlopeIntermediate = new PrintWriter(IntermediateSlopesFile);
			pwSlopeSlow = new PrintWriter(slowSlopesFile);
			double currSlope;
			for(int i=0; i<patientsList.size(); i++) {
				patient = patientsList.get(i);
				eventsSequence = patient.getPatientSequence();
				int eventsSeqSize = eventsSequence.size();
				
				if(eventsSeqSize < ALSUtilsSDU.minNumOfEvents)
					continue;
				currSlope = patient.getSlope();
				pwSlope.println(currSlope);
				
				if(patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.fastSlope))
					pwSlopeFast.println(patient.getSlope());
				if(patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.intermediateSlope))
					pwSlopeIntermediate.println(patient.getSlope());
				if(patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.slowSlope))
					pwSlopeSlow.println(patient.getSlope());
				
				for(int j=0; j< eventsSeqSize; j++) {
					if(patient.getSlopeType().equalsIgnoreCase(ALSUtilsSDU.fastSlope)) {
						
					}
//					eventsSequence.get(j).getTarget()
				}//for(j)
			}//for(i)
			pwSlope.close();
			pwSlopeFast.close();
			pwSlopeIntermediate.close();
			pwSlopeSlow.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//createSlopesFile
	
}//class ALSDataset

