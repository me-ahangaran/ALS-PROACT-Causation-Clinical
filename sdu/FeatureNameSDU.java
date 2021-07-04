package sdu;

public class FeatureNameSDU {

	public String getFeatureName(int featureNum){
		String fName = "";
		switch (featureNum) {
		
		case 0:
			fName = "virtualFeature";
			break;			
		case 1:
			fName = "pulse";
			break;			
		case 2:
			fName = "respiratoryRate";
			break;	
		case 3:
			fName = "temperature";
			break;
		case 4:
			fName = "weight";
			break;
		case 5:
			fName = "bloodPressureDiastolic";
			break;
		case 6:
			fName = "bloodPressureSystolic";
			break;
		case 7:
			fName = "FVC_subjectLiters";
			break;
		case 8:
			fName = "SVC_subjectLiters";
			break;
		case 9:
			fName = "URINE_urinePH";
			break;
		case 10:
			fName = "URINE_urineProtein";
			break;
		case 11:
			fName = "URINE_urineSpecificGravity";
			break;
		case 12:
			fName = "URINE_urineGlucose";
			break;
		case 13:
			fName = "URINE_urineWBC";
			break;
		case 14:
			fName = "URINE_urineLeukesterase";
			break;
		case 15:
			fName = "URINE_urineBlood";
			break;
		case 16:
			fName = "URINE_urineRBCs";
			break;
		case 17:
			fName = "URINE_urineCasts";
			break;
		case 18:
			fName = "URINE_urineKetones";
			break;
		case 19:
			fName = "URINE_urineAppearance";
			break;
		case 20:
			fName = "URINE_urineColor";
			break;
		case 21:
			fName = "URINE_urineBacteria";
			break;
		case 22:
			fName = "URINE_urineMucus";
			break;
		case 23:
			fName = "URINE_urineAlbumin";
			break;
		case 24:
			fName = "URINE_urineUricAcidCrystals";
			break;
		case 25:
			fName = "URINE_urineCalciumOxalateCrystals";
			break;
		case 26:
			fName = "BLOOD_PROTEINS_albumin";
			break;
		case 27:
			fName = "BLOOD_PROTEINS_protein";
			break;
		case 28:
			fName = "ELECTROLYTES_sodium";
			break;
		case 29:
			fName = "ELECTROLYTES_potassium";
			break;
		case 30:
			fName = "ELECTROLYTES_bicarbonate";
			break;
		case 31:
			fName = "ELECTROLYTES_chloride";
			break;
		case 32:
			fName = "ELECTROLYTES_anionGap";
			break;
		case 33:
			fName = "ELECTROLYTES_magnesium";
			break;
		case 34:
			fName = "KIDNEY_bloodUreaNitrogen";
			break;
		case 35:
			fName = "KIDNEY_uricAcid";
			break;
		case 36:
			fName = "KIDNEY_creatinine";
			break;
		case 37:
			fName = "LIVER_alkalinePhosphatase";
			break;
		case 38:
			fName = "LIVER_alt";
			break;
		case 39:
			fName = "LIVER_gammaGlutamylTransferase";
			break;
		case 40:
			fName = "LIVER_ast";
			break;
		case 41:
			fName = "LIVER_bilirubinTotal";
			break;
		case 42:
			fName = "COMPLETE_BLOOD_whiteBloodCell";
			break;
		case 43:
			fName = "COMPLETE_BLOOD_neutrophils";
			break;
		case 44:
			fName = "COMPLETE_BLOOD_absoluteNeutrophilCount";
			break;
		case 45:
			fName = "COMPLETE_BLOOD_bandNeutrophils";
			break;
		case 46:
			fName = "COMPLETE_BLOOD_absoluteBandNeutrophilCount";
			break;
		case 47:
			fName = "COMPLETE_BLOOD_lymphocytes";
			break;
		case 48:
			fName = "COMPLETE_BLOOD_absoluteLymphocyteCount";
			break;
		case 49:
			fName = "COMPLETE_BLOOD_monocytes";
			break;
		case 50:
			fName = "COMPLETE_BLOOD_absoluteMonocyteCount";
			break;
		case 51:
			fName = "COMPLETE_BLOOD_eosinophils";
			break;
		case 52:
			fName = "COMPLETE_BLOOD_absoluteEosinophilCount";
			break;
		case 53:
			fName = "COMPLETE_BLOOD_basophils";
			break;
		case 54:
			fName = "COMPLETE_BLOOD_absoluteBasophilCount";
			break;
		case 55:
			fName = "COMPLETE_BLOOD_redBloodCells";
			break;
		case 56:
			fName = "COMPLETE_BLOOD_hemoglobin";
			break;
		case 57:
			fName = "COMPLETE_BLOOD_hematocrit";
			break;
		case 58:
			fName = "COMPLETE_BLOOD_meanCorpuscularHemoglobin";
			break;
		case 59:
			fName = "COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration";
			break;
		case 60:
			fName = "COMPLETE_BLOOD_meanCorpuscularVolume";
			break;
		case 61:
			fName = "COMPLETE_BLOOD_platelets";
			break;
		case 62:
			fName = "HEART_DISEASE_ck";
			break;
		case 63:
			fName = "HEART_DISEASE_triglycerides";
			break;
		case 64:
			fName = "HEART_DISEASE_totalCholesterol";
			break;
		case 65:
			fName = "HEART_DISEASE_lactateDehydrogenase";
			break;
		case 66:
			fName = "BLOOD_SUGAR_glucose";
			break;
		case 67:
			fName = "BLOOD_SUGAR_HbA1c";
			break;
		case 68:
			fName = "MINERAL_BALANCE_calcium";
			break;
		case 69:
			fName = "MINERAL_BALANCE_phosphorus";
			break;
		case 70:
			fName = "IMMUNE_RESPONSE_immunoglobulinA";
			break;
		case 71:
			fName = "IMMUNE_RESPONSE_immunoglobulinG";
			break;
		case 72:
			fName = "IMMUNE_RESPONSE_immunoglobulinM";
			break;
		case 73:
			fName = "IMMUNE_RESPONSE_gammaGlobulin";
			break;
		case 74:
			fName = "IMMUNE_RESPONSE_alpha1Globulin";
			break;
		case 75:
			fName = "IMMUNE_RESPONSE_alpha2Globulin";
			break;
		case 76:
			fName = "IMMUNE_RESPONSE_betaGlobulin";
			break;
		case 77:
			fName = "IMMUNE_RESPONSE_albuminGlobulinRatio";
			break;
		case 78:
			fName = "HORMONES_TSH";
			break;
		case 79:
			fName = "HORMONES_FreeT3";
			break;
		case 80:
			fName = "HORMONES_FreeT4";
			break;
		case 81:
			fName = "HORMONES_betaHCG";
			break;
		case 82:
			fName = "COAGULATION_prothrombinTime";
			break;
		case 83:
			fName = "COAGULATION_internationalNormalizedRatio";
			break;
		case 84:
			fName = "OTHERS_amylase";
			break;
		case 85:
			fName = "OTHERS_salivaryAmylase";
			break;
		case 86:
			fName = "OTHERS_pancreaticAmylase";
			break;
			
		default:
			break;
		}//switch
		
		return fName;
	}//getFeatureName
	
}//FeatureName
