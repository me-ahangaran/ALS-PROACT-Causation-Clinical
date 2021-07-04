package sdu;
//this class define a number for each dynamic feature
public class FeatureNumberSDU {

//	public static final int numOfDynamicFeatures = 87;	//number of dynamic features are 87
	
	public static final int virtualFeature = 0;	//this is not actual feature and shows the state of patient before trial
	
	//Vital sign
	public static final int pulse = 1;
	public static final int respiratoryRate = 2;
	public static final int temperature = 3;
	public static final int weight = 4;
	public static final int bloodPressureDiastolic = 5;
	public static final int bloodPressureSystolic = 6;
	
	//Forced Vital Capacity
	public static final int FVC_subjectLiters = 7;
	
	//Slow Vital Capacity
	public static final int SVC_subjectLiters = 8;
	
	/************Laboratory data******************/
	
	//Urine
	public static final int URINE_urinePH = 9;
	public static final int URINE_urineProtein = 10;
	public static final int URINE_urineSpecificGravity = 11;
	public static final int URINE_urineGlucose = 12;
	public static final int URINE_urineWBC = 13;
	public static final int URINE_urineLeukesterase = 14;
	public static final int URINE_urineBlood = 15;
	public static final int URINE_urineRBCs = 16;
	public static final int URINE_urineCasts = 17;
	public static final int URINE_urineKetones = 18;
	public static final int URINE_urineAppearance = 19;
	public static final int URINE_urineColor = 20;
	public static final int URINE_urineBacteria = 21;
	public static final int URINE_urineMucus = 22;
	public static final int URINE_urineAlbumin = 23;
	public static final int URINE_urineUricAcidCrystals = 24;
	public static final int URINE_urineCalciumOxalateCrystals = 25;
	
	//Blood proteins
	public static final int BLOOD_PROTEINS_albumin = 26;
	public static final int BLOOD_PROTEINS_protein = 27;
	
	//Electrolytes
	public static final int ELECTROLYTES_sodium = 28;
	public static final int ELECTROLYTES_potassium = 29;
	public static final int ELECTROLYTES_bicarbonate = 30;
	public static final int ELECTROLYTES_chloride = 31;
	public static final int ELECTROLYTES_anionGap = 32;
	public static final int ELECTROLYTES_magnesium = 33;
	
	//Kidney tests
	public static final int KIDNEY_bloodUreaNitrogen = 34;
	public static final int KIDNEY_uricAcid = 35;
	public static final int KIDNEY_creatinine = 36;
	
	//Liver tests
	public static final int LIVER_alkalinePhosphatase = 37;
	public static final int LIVER_alt = 38;
	public static final int LIVER_gammaGlutamylTransferase = 39;
	public static final int LIVER_ast = 40;
	public static final int LIVER_bilirubinTotal = 41;
	
	//Complete blood count
	public static final int COMPLETE_BLOOD_whiteBloodCell = 42;
	public static final int COMPLETE_BLOOD_neutrophils = 43;
	public static final int COMPLETE_BLOOD_absoluteNeutrophilCount = 44;
	public static final int COMPLETE_BLOOD_bandNeutrophils = 45;
	public static final int COMPLETE_BLOOD_absoluteBandNeutrophilCount = 46;
	public static final int COMPLETE_BLOOD_lymphocytes = 47;
	public static final int COMPLETE_BLOOD_absoluteLymphocyteCount = 48;
	public static final int COMPLETE_BLOOD_monocytes = 49;
	public static final int COMPLETE_BLOOD_absoluteMonocyteCount = 50;
	public static final int COMPLETE_BLOOD_eosinophils = 51;
	public static final int COMPLETE_BLOOD_absoluteEosinophilCount = 52;
	public static final int COMPLETE_BLOOD_basophils = 53;
	public static final int COMPLETE_BLOOD_absoluteBasophilCount = 54;
	public static final int COMPLETE_BLOOD_redBloodCells = 55;
	public static final int COMPLETE_BLOOD_hemoglobin = 56;
	public static final int COMPLETE_BLOOD_hematocrit = 57;
	public static final int COMPLETE_BLOOD_meanCorpuscularHemoglobin = 58;
	public static final int COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration = 59;
	public static final int COMPLETE_BLOOD_meanCorpuscularVolume = 60;
	public static final int COMPLETE_BLOOD_platelets = 61;
	
	//Heart disease and muscle degradation
	public static final int HEART_DISEASE_ck = 62;
	public static final int HEART_DISEASE_triglycerides = 63;
	public static final int HEART_DISEASE_totalCholesterol = 64;
	public static final int HEART_DISEASE_lactateDehydrogenase = 65;
	
	//Blood sugar
	public static final int BLOOD_SUGAR_glucose = 66;
	public static final int BLOOD_SUGAR_HbA1c = 67;
	
	//Mineral balance
	public static final int MINERAL_BALANCE_calcium = 68;
	public static final int MINERAL_BALANCE_phosphorus = 69;
	
	//Immune response
	public static final int IMMUNE_RESPONSE_immunoglobulinA = 70;
	public static final int IMMUNE_RESPONSE_immunoglobulinG = 71;
	public static final int IMMUNE_RESPONSE_immunoglobulinM = 72;
	public static final int IMMUNE_RESPONSE_gammaGlobulin = 73;
	public static final int IMMUNE_RESPONSE_alpha1Globulin = 74;
	public static final int IMMUNE_RESPONSE_alpha2Globulin = 75;
	public static final int IMMUNE_RESPONSE_betaGlobulin = 76;
	public static final int IMMUNE_RESPONSE_albuminGlobulinRatio = 77;
	
	//Hormones
	public static final int HORMONES_TSH = 78;
	public static final int HORMONES_FreeT3 = 79;
	public static final int HORMONES_FreeT4 = 80;
	public static final int HORMONES_betaHCG = 81;
	
	//Coagulation measures
	public static final int COAGULATION_prothrombinTime = 82;
	public static final int COAGULATION_internationalNormalizedRatio = 83;
	
	//Others
	public static final int OTHERS_amylase = 84;
	public static final int OTHERS_salivaryAmylase = 85;
	public static final int OTHERS_pancreaticAmylase = 86;
	
	
}//class FeatureNumber
