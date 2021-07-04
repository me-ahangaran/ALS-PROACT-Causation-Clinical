package sdu;
//this class define the threshold for each feature changes (should be defines by physician)
public class FeatureChangeSDU {
	
		public static final double target = 1;	//ALS-FRS total change 

		//Vital sign
		public static final double pulse = 5;	//zaraban ghalb
		public static final double respiratoryRate = 2;	//nerkhe tanaffos
		public static final double temperature = 0.5;
		public static final double weight = 2;
		public static final double bloodPressureDiastolic = 3;	//lower bound
		public static final double bloodPressureSystolic = 3;	//upper bound
		
		//Forced Vital Capacity
		public static final double FVC_subjectLiters = 0.2;
		
		//Slow Vital Capacity
		public static final double SVC_subjectLiters = 0;	//delete this feature (because of no delta time)
		
		/************Laboratory data******************/
		
		//Urine
		public static final double URINE_urinePH = 0.1;
		public static final double URINE_urineProtein = 1;
		public static final double URINE_urineSpecificGravity = 0.01;
		public static final double URINE_urineGlucose = 1;
		public static final double URINE_urineWBC = 0.1;
		public static final double URINE_urineLeukesterase = 1;
		public static final double URINE_urineBlood = 0.1;
		public static final double URINE_urineRBCs = 0.2;
		public static final double URINE_urineCasts = 0.1;
		public static final double URINE_urineKetones = 0.1;
		public static final double URINE_urineAppearance = 1;
		public static final double URINE_urineColor = 1;
		public static final double URINE_urineBacteria = 1;
		public static final double URINE_urineMucus = 1;
		public static final double URINE_urineAlbumin = 0.5;
		public static final double URINE_urineUricAcidCrystals = 1;
		public static final double URINE_urineCalciumOxalateCrystals = 1;
		
		//Blood proteins
		public static final double BLOOD_PROTEINS_albumin = 2;
		public static final double BLOOD_PROTEINS_protein = 2;
		
		//Electrolytes
		public static final double ELECTROLYTES_sodium = 1;
		public static final double ELECTROLYTES_potassium = 0.2;
		public static final double ELECTROLYTES_bicarbonate = 1;
		public static final double ELECTROLYTES_chloride = 1;
		public static final double ELECTROLYTES_anionGap = 1;
		public static final double ELECTROLYTES_magnesium = 0.02;
		
		//Kidney tests
		public static final double KIDNEY_bloodUreaNitrogen = 0.1;
		public static final double KIDNEY_uricAcid = 5;
		public static final double KIDNEY_creatinine = 3;
		
		//Liver tests
		public static final double LIVER_alkalinePhosphatase = 5;
		public static final double LIVER_alt = 2;
		public static final double LIVER_gammaGlutamylTransferase = 2;
		public static final double LIVER_ast = 2;
		public static final double LIVER_bilirubinTotal = 1;
		
		//Complete blood count
		public static final double COMPLETE_BLOOD_whiteBloodCell = 1;
		public static final double COMPLETE_BLOOD_neutrophils = 2;
		public static final double COMPLETE_BLOOD_absoluteNeutrophilCount = 0.2;
		public static final double COMPLETE_BLOOD_bandNeutrophils = 0.2;
		public static final double COMPLETE_BLOOD_absoluteBandNeutrophilCount = 0.1;
		public static final double COMPLETE_BLOOD_lymphocytes = 2;
		public static final double COMPLETE_BLOOD_absoluteLymphocyteCount = 0.2;
		public static final double COMPLETE_BLOOD_monocytes = 0.2;
		public static final double COMPLETE_BLOOD_absoluteMonocyteCount = 0.02;
		public static final double COMPLETE_BLOOD_eosinophils = 0.1;
		public static final double COMPLETE_BLOOD_absoluteEosinophilCount = 0.02;
		public static final double COMPLETE_BLOOD_basophils = 0.02;
		public static final double COMPLETE_BLOOD_absoluteBasophilCount = 0.02;
		public static final double COMPLETE_BLOOD_redBloodCells = 50;	//should be modified.
		public static final double COMPLETE_BLOOD_hemoglobin = 3;
		public static final double COMPLETE_BLOOD_hematocrit = 2;
		public static final double COMPLETE_BLOOD_meanCorpuscularHemoglobin = 1;
		public static final double COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration = 1;
		public static final double COMPLETE_BLOOD_meanCorpuscularVolume = 2;
		public static final double COMPLETE_BLOOD_platelets = 10;
		
		//Heart disease and muscle degradation
		public static final double HEART_DISEASE_ck = 3;
		public static final double HEART_DISEASE_triglycerides = 0.1;
		public static final double HEART_DISEASE_totalCholesterol = 0.1;
		public static final double HEART_DISEASE_lactateDehydrogenase = 5;
		
		//Blood sugar
		public static final double BLOOD_SUGAR_glucose = 0.1;
		public static final double BLOOD_SUGAR_HbA1c = 0.2;
		
		//Mineral balance
		public static final double MINERAL_BALANCE_calcium = 0.01;
		public static final double MINERAL_BALANCE_phosphorus = 0.01;
		
		//Immune response
		public static final double IMMUNE_RESPONSE_immunoglobulinA = 5;
		public static final double IMMUNE_RESPONSE_immunoglobulinG = 1;
		public static final double IMMUNE_RESPONSE_immunoglobulinM = 5;
		public static final double IMMUNE_RESPONSE_gammaGlobulin = 1;
		public static final double IMMUNE_RESPONSE_alpha1Globulin = 1;
		public static final double IMMUNE_RESPONSE_alpha2Globulin = 1;
		public static final double IMMUNE_RESPONSE_betaGlobulin = 1;
		public static final double IMMUNE_RESPONSE_albuminGlobulinRatio = 1;
		
		//Hormones
		public static final double HORMONES_TSH = 1;
		public static final double HORMONES_FreeT3 = 0.001;
		public static final double HORMONES_FreeT4 = 1;
		public static final double HORMONES_betaHCG = 1;
		
		//Coagulation measures
		public static final double COAGULATION_prothrombinTime = 0.2;
		public static final double COAGULATION_internationalNormalizedRatio = 0.02;
		
		//Others
		public static final double OTHERS_amylase = 3;
		public static final double OTHERS_salivaryAmylase = 2;
		public static final double OTHERS_pancreaticAmylase = 2;
	
}//class FeatureChange
