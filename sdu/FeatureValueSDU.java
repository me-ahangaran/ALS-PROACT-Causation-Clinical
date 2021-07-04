package sdu;

public class FeatureValueSDU {
	
	public double getFeatureValue(String fName, String fVal){
		
		//Vital Sign
		if(fName.equalsIgnoreCase("pulse"))
			return Double.parseDouble(fVal);
		if(fName.equalsIgnoreCase("respiratoryRate"))
			return Double.parseDouble(fVal);
		if(fName.equalsIgnoreCase("temperature"))
			return Double.parseDouble(fVal);
		if(fName.equalsIgnoreCase("weight"))
			return Double.parseDouble(fVal);
		if(fName.equalsIgnoreCase("bloodPressureDiastolic"))
			return Double.parseDouble(fVal);
		if(fName.equalsIgnoreCase("bloodPressureSystolic"))
			return Double.parseDouble(fName);
		
		//Forced Vital Capacity (FVC)
		if(fName.equalsIgnoreCase("FVC_subjectLiters"))
			return Double.parseDouble(fVal);
		
		//Slow Vital Capacity (SVC)
		if(fName.equalsIgnoreCase("SVC_subjectLiters"))
			return Double.parseDouble(fVal);
				
		//Lab Data
		if(fName.equalsIgnoreCase("URINE_urinePH"))
			return Double.parseDouble(fVal);
		
		if(fName.equalsIgnoreCase("URINE_urineProtein")){
			if(fVal.equalsIgnoreCase("-") || fVal.equalsIgnoreCase("negative") || fVal.equalsIgnoreCase("trace"))
				return 0;
			if(fVal.equalsIgnoreCase("Normal"))
				return 20;
			if(fVal.equalsIgnoreCase("3+") || fVal.equalsIgnoreCase("3+++"))
				return 3;
			
			return Double.parseDouble(fVal);
		}//if URINE_urineProtein
		
		if(fName.equalsIgnoreCase("URINE_urineSpecificGravity")){
			if(fVal.equalsIgnoreCase(">1.030"))
				return 1.030;
			else
				return Double.parseDouble(fVal);
		}//if URINE_urineSpecificGravity
		
		if(fName.equalsIgnoreCase("URINE_urineGlucose")){
			if(fVal.equalsIgnoreCase("-") || fVal.equalsIgnoreCase("+") || fVal.equalsIgnoreCase("normal") || fVal.equalsIgnoreCase("trace"))
				return 0;
			if(fVal.equalsIgnoreCase("3+"))
				return 3;
			return Double.parseDouble(fVal);
		}//if URINE_urineGlucose
		
		if(fName.equalsIgnoreCase("URINE_urineWBC")){
			if(fVal.equalsIgnoreCase("-") || fVal.equalsIgnoreCase("Small"))
				return 0;
			else
				return Double.parseDouble(fVal);
		}//if URINE_urineWBC
		
		if(fName.equalsIgnoreCase("URINE_urineLeukesterase")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("trace"))
				return 1;
			if(fVal.equalsIgnoreCase("small"))
				return 2;
			if(fVal.equalsIgnoreCase("moderate"))
				return 5;
			if(fVal.equalsIgnoreCase("large"))
				return 10;
			return Double.parseDouble(fVal);
		}//if URINE_urineLeukesterase
		
		if(fName.equalsIgnoreCase("URINE_urineBlood")){
			if(fVal.equalsIgnoreCase("-") || fVal.equalsIgnoreCase("negative"))
				return 0;
			if(fVal.equalsIgnoreCase("trace") || fVal.equalsIgnoreCase("hemolyzed trace"))
				return 0.1;
			if(fVal.equalsIgnoreCase("small") || fVal.equalsIgnoreCase("hemolyzed small"))
				return 0.3;
			if(fVal.equalsIgnoreCase("moderate") || fVal.equalsIgnoreCase("+") || fVal.equalsIgnoreCase("Normal") || fVal.equalsIgnoreCase("hemolyzed moderate"))
				return 0.5;
			if(fVal.equalsIgnoreCase("hemolyzed large"))
				return 5;
			return Double.parseDouble(fVal);
		}//if URINE_urineBlood
		
		if(fName.equalsIgnoreCase("URINE_urineRBCs")){
			if(fVal.equalsIgnoreCase("-") || fVal.equalsIgnoreCase("None Found"))
				return 0;
			
			return Double.parseDouble(fVal);
		}//if URINE_urineRBCs
		
		if(fName.equalsIgnoreCase("URINE_urineCasts")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			else
				return Double.parseDouble(fVal);
		}//if URINE_urineRBCs
		
		if(fName.equalsIgnoreCase("URINE_urineKetones")){
			if(fVal.equalsIgnoreCase("-") || fVal.equalsIgnoreCase("negative"))
				return 0;
			if(fVal.equalsIgnoreCase("trace"))
				return 0.2;
			return Double.parseDouble(fVal);
		}//if URINE_urineKetones
		
		if(fName.equalsIgnoreCase("URINE_urineAppearance")){
			if(fVal.equalsIgnoreCase("clear"))
				return 1;
			if(fVal.equalsIgnoreCase("normal"))
				return 2;
			if(fVal.equalsIgnoreCase("cloudy"))
				return 3;
			if(fVal.equalsIgnoreCase("hazy"))
				return 4;
			if(fVal.equalsIgnoreCase("turbid"))
				return 5;
			return Double.parseDouble(fVal);
		}//if URINE_urineAppearance
		
		if(fName.equalsIgnoreCase("URINE_urineColor")){
			if(fVal.equalsIgnoreCase("colorless"))
				return 1;
			if(fVal.equalsIgnoreCase("straw"))
				return 2;
			if(fVal.equalsIgnoreCase("yellow"))
				return 3;
			if(fVal.equalsIgnoreCase("orange"))
				return 4;
			if(fVal.equalsIgnoreCase("amber"))
				return 5;
			if(fVal.equalsIgnoreCase("red"))
				return 6;
			if(fVal.equalsIgnoreCase("brown"))
				return 7;
			if(fVal.equalsIgnoreCase("green"))
				return 8;
			if(fVal.equalsIgnoreCase("Water Wht"))
				return 9;
			if(fVal.equalsIgnoreCase("Normal"))
				return 10;
			return Double.parseDouble(fVal);
		}//if URINE_urineColor
		
		if(fName.equalsIgnoreCase("URINE_urineBacteria")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("trace") || fVal.equalsIgnoreCase("1+"))
				return 1;
			if(fVal.equalsIgnoreCase("small") || fVal.equalsIgnoreCase("2+"))
				return 2;
			if(fVal.equalsIgnoreCase("moderate") || fVal.equalsIgnoreCase("3+") || fVal.equalsIgnoreCase("present") || fVal.equalsIgnoreCase("+"))
				return 3;
			if(fVal.equalsIgnoreCase("large") || fVal.equalsIgnoreCase("4+"))
				return 4;			
			return Double.parseDouble(fVal);
		}//if URINE_urineBacteria
		
		if(fName.equalsIgnoreCase("URINE_urineMucus")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("trace") || fVal.equalsIgnoreCase("1+"))
				return 1;
			if(fVal.equalsIgnoreCase("small") || fVal.equalsIgnoreCase("2+"))
				return 2;
			if(fVal.equalsIgnoreCase("moderate") || fVal.equalsIgnoreCase("3+") || fVal.equalsIgnoreCase("present") || fVal.equalsIgnoreCase("+"))
				return 3;
			if(fVal.equalsIgnoreCase("large") || fVal.equalsIgnoreCase("4+"))
				return 4;			
			return Double.parseDouble(fVal);
		}//if URINE_urineMucus
		
		if(fName.equalsIgnoreCase("URINE_urineAlbumin")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("trace"))
				return 1;
			if(fVal.equalsIgnoreCase("<0.0003"))
				return 0.0003;
			if(fVal.equalsIgnoreCase("<3.0"))
				return 3;
			return Double.parseDouble(fVal);
		}//if URINE_urineAlbumin
		
		if(fName.equalsIgnoreCase("URINE_urineUricAcidCrystals")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("trace"))
				return 1;
			if(fVal.equalsIgnoreCase("small") || fVal.equalsIgnoreCase("few"))
				return 2;
			if(fVal.equalsIgnoreCase("moderate") || fVal.equalsIgnoreCase("+") || fVal.equalsIgnoreCase("mod") || fVal.equalsIgnoreCase("present") || fVal.equalsIgnoreCase("occ"))
				return 3;
			if(fVal.equalsIgnoreCase("large") || fVal.equalsIgnoreCase("many"))
				return 4;			
			return Double.parseDouble(fVal);
		}//if URINE_urineUricAcidCrystals
		
		if(fName.equalsIgnoreCase("URINE_urineCalciumOxalateCrystals")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("trace"))
				return 1;
			if(fVal.equalsIgnoreCase("small") || fVal.equalsIgnoreCase("few"))
				return 2;
			if(fVal.equalsIgnoreCase("moderate") || fVal.equalsIgnoreCase("+") || fVal.equalsIgnoreCase("mod") || fVal.equalsIgnoreCase("present") || fVal.equalsIgnoreCase("occ"))
				return 3;
			if(fVal.equalsIgnoreCase("large") || fVal.equalsIgnoreCase("many"))
				return 4;			
			return Double.parseDouble(fVal);
		}//if URINE_urineCalciumOxalateCrystals
		
		if(fName.equalsIgnoreCase("BLOOD_PROTEINS_albumin")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("trace"))
				return 1;		
			return Double.parseDouble(fVal);
		}//if BLOOD_PROTEINS_albumin
		
		if(fName.equalsIgnoreCase("BLOOD_PROTEINS_protein")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("trace") || fVal.equalsIgnoreCase("1+"))
				return 1;
			if(fVal.equalsIgnoreCase("2+"))
				return 2;
			if(fVal.equalsIgnoreCase("4+"))
				return 4;
			return Double.parseDouble(fVal);
		}//if BLOOD_PROTEINS_protein
		
		if(fName.equalsIgnoreCase("ELECTROLYTES_sodium")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if ELECTROLYTES_sodium
		
		if(fName.equalsIgnoreCase("ELECTROLYTES_potassium")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if ELECTROLYTES_potassium
		
		if(fName.equalsIgnoreCase("ELECTROLYTES_bicarbonate")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if ELECTROLYTES_bicarbonate
		
		if(fName.equalsIgnoreCase("ELECTROLYTES_chloride")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if ELECTROLYTES_chloride
		
		if(fName.equalsIgnoreCase("ELECTROLYTES_anionGap")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if ELECTROLYTES_anionGap
		
		if(fName.equalsIgnoreCase("ELECTROLYTES_magnesium")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("<.2057"))
				return 0.2;
			return Double.parseDouble(fVal);
		}//if ELECTROLYTES_magnesium
		
		if(fName.equalsIgnoreCase("KIDNEY_bloodUreaNitrogen")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if KIDNEY_bloodUreaNitrogen
		
		if(fName.equalsIgnoreCase("KIDNEY_uricAcid")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if KIDNEY_uricAcid
		
		if(fName.equalsIgnoreCase("KIDNEY_creatinine")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("<18"))
				return 18;
			return Double.parseDouble(fVal);
		}//if KIDNEY_creatinine
		
		if(fName.equalsIgnoreCase("LIVER_alkalinePhosphatase")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if LIVER_alkalinePhosphatase
		
		if(fName.equalsIgnoreCase("LIVER_alt")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("normal"))
				return 10;
			return Double.parseDouble(fVal);
		}//if LIVER_alt
		
		if(fName.equalsIgnoreCase("LIVER_gammaGlutamylTransferase")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("<4"))
				return 4;
			if(fVal.equalsIgnoreCase("Normal"))
				return 40;
			return Double.parseDouble(fVal);
		}//if LIVER_gammaGlutamylTransferase
		
		if(fName.equalsIgnoreCase("LIVER_ast")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if LIVER_ast
		
		if(fName.equalsIgnoreCase("LIVER_bilirubinTotal")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("<3") || fVal.equalsIgnoreCase("<3.42"))
				return 3;
			if(fVal.equalsIgnoreCase("<0.2"))
				return 0.2;
			return Double.parseDouble(fVal);
		}//if LIVER_bilirubinTotal
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_whiteBloodCell")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("very large"))
				return 15;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_whiteBloodCell
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_neutrophils")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_neutrophils
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_absoluteNeutrophilCount")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_absoluteNeutrophilCount
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_bandNeutrophils")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_bandNeutrophils
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_absoluteBandNeutrophilCount")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("Normal"))
				return 0.7;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_absoluteBandNeutrophilCount
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_lymphocytes")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_lymphocytes
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_absoluteLymphocyteCount")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_absoluteLymphocyteCount
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_monocytes")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_monocytes
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_absoluteMonocyteCount")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_absoluteMonocyteCount
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_eosinophils")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("normal"))
				return 2;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_eosinophils
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_absoluteEosinophilCount")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("normal"))
				return 0.25;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_absoluteEosinophilCount
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_basophils")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("normal"))
				return 0.37;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_basophils
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_absoluteBasophilCount")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("normal"))
				return 0.2;
			if(fVal.equalsIgnoreCase("trace"))
				return 0.01;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_absoluteBasophilCount
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_redBloodCells")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("very large"))
				return 7000;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_redBloodCells
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_hemoglobin")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_hemoglobin

		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_hematocrit")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_hematocrit
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_meanCorpuscularHemoglobin")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_meanCorpuscularHemoglobin
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			double val = Double.parseDouble(fVal);
			if(val>100)
				return val/10;
			return val;
		}//if COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration

		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_meanCorpuscularVolume")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_meanCorpuscularVolume
		
		if(fName.equalsIgnoreCase("COMPLETE_BLOOD_platelets")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COMPLETE_BLOOD_platelets
		
		if(fName.equalsIgnoreCase("HEART_DISEASE_ck")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("normal"))
				return 100;
			return Double.parseDouble(fVal);
		}//if HEART_DISEASE_ck
		
		if(fName.equalsIgnoreCase("HEART_DISEASE_triglycerides")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if HEART_DISEASE_triglycerides
		
		if(fName.equalsIgnoreCase("HEART_DISEASE_totalCholesterol")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if HEART_DISEASE_totalCholesterol
		
		if(fName.equalsIgnoreCase("HEART_DISEASE_lactateDehydrogenase")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if HEART_DISEASE_lactateDehydrogenase
		
		if(fName.equalsIgnoreCase("BLOOD_SUGAR_glucose")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("trace"))
				return 1;
			if(fVal.equalsIgnoreCase("small"))
				return 2;
			if(fVal.equalsIgnoreCase("moderate") || fVal.equalsIgnoreCase("normal"))
				return 5;
			if(fVal.equalsIgnoreCase("large"))
				return 30;
			return Double.parseDouble(fVal);
		}//if BLOOD_SUGAR_glucose
		
		if(fName.equalsIgnoreCase("BLOOD_SUGAR_HbA1c")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			if(fVal.equalsIgnoreCase("normal"))
				return 6;
			return Double.parseDouble(fVal);
		}//if BLOOD_SUGAR_HbA1c
		
		if(fName.equalsIgnoreCase("MINERAL_BALANCE_calcium")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if MINERAL_BALANCE_calcium
		
		if(fName.equalsIgnoreCase("MINERAL_BALANCE_phosphorus")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if MINERAL_BALANCE_phosphorus
		
		if(fName.equalsIgnoreCase("IMMUNE_RESPONSE_immunoglobulinA")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if IMMUNE_RESPONSE_immunoglobulinA
		
		if(fName.equalsIgnoreCase("IMMUNE_RESPONSE_immunoglobulinG")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if IMMUNE_RESPONSE_immunoglobulinG
		
		if(fName.equalsIgnoreCase("IMMUNE_RESPONSE_immunoglobulinM")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if IMMUNE_RESPONSE_immunoglobulinM
		
		if(fName.equalsIgnoreCase("IMMUNE_RESPONSE_gammaGlobulin")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if IMMUNE_RESPONSE_gammaGlobulin
		
		if(fName.equalsIgnoreCase("IMMUNE_RESPONSE_alpha1Globulin")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if IMMUNE_RESPONSE_alpha1Globulin
		
		if(fName.equalsIgnoreCase("IMMUNE_RESPONSE_alpha2Globulin")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if IMMUNE_RESPONSE_alpha2Globulin
		
		if(fName.equalsIgnoreCase("IMMUNE_RESPONSE_alpha2Globulin")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if IMMUNE_RESPONSE_alpha2Globulin
		
		if(fName.equalsIgnoreCase("IMMUNE_RESPONSE_betaGlobulin")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if IMMUNE_RESPONSE_betaGlobulin
		
		if(fName.equalsIgnoreCase("IMMUNE_RESPONSE_albuminGlobulinRatio")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if IMMUNE_RESPONSE_albuminGlobulinRatio
		
		if(fName.equalsIgnoreCase("HORMONES_TSH")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if HORMONES_TSH
		
		if(fName.equalsIgnoreCase("HORMONES_FreeT3")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if HORMONES_FreeT3
		
		if(fName.equalsIgnoreCase("HORMONES_FreeT4")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if HORMONES_FreeT4
		
		if(fName.equalsIgnoreCase("HORMONES_betaHCG")){
			if(fVal.equalsIgnoreCase("-") || fVal.equalsIgnoreCase("Borderline") || fVal.equalsIgnoreCase("negative") || fVal.equalsIgnoreCase("normal"))
				return 0;
			return Double.parseDouble(fVal);
		}//if HORMONES_betaHCG
		
		if(fName.equalsIgnoreCase("COAGULATION_prothrombinTime")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COAGULATION_prothrombinTime
		
		if(fName.equalsIgnoreCase("COAGULATION_internationalNormalizedRatio")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if COAGULATION_internationalNormalizedRatio
		
		if(fName.equalsIgnoreCase("OTHERS_amylase")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if OTHERS_amylase
		
		if(fName.equalsIgnoreCase("OTHERS_salivaryAmylase")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if OTHERS_salivaryAmylase
		
		if(fName.equalsIgnoreCase("OTHERS_pancreaticAmylase")){
			if(fVal.equalsIgnoreCase("-"))
				return 0;
			return Double.parseDouble(fVal);
		}//if OTHERS_pancreaticAmylase
		
		return -1;
	}//getFeatureValue
	
	

	
	
}//class FeatureValue
