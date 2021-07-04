package sdu;

import java.util.ArrayList;

//this class defines all laboratory data
public class LabDataSDU {

	ArrayList<String> testNames = new ArrayList<String>();
	ArrayList<String> testResults = new ArrayList<String>();
	
	public ArrayList<String> getTestNames() {
		return testNames;
	}
	public void setTestNames(ArrayList<String> testNames) {
		this.testNames = testNames;
	}
	public ArrayList<String> getTestResults() {
		return testResults;
	}
	public void setTestResults(ArrayList<String> testResults) {
		this.testResults = testResults;
	}
	//Urine (17 features)
	private String URINE_urinePH = "";	//range 4.5-8 (optimal is 6)
	private String URINE_urineProtein = "";	//range 0-20 (mg/dL)
	private String URINE_urineSpecificGravity = "";	//range 1-1.03
	private String URINE_urineGlucose = "";	//normally is zero (mg/dL) or trace
	private String URINE_urineWBC = "";	// white blood cells. should be negative
	private String URINE_urineLeukesterase = "";	//should be <10 U/L. values: Trace, Small, Moderate, large or -
	private String URINE_urineBlood = "";	//should be negative. values: Hemolyzed Moderate, Small, Trace or -
	private String URINE_urineRBCs = "";	//Should be < 3.0 (/HPF)
	private String URINE_urineCasts = "";	//should be negative
	private String URINE_urineKetones = "";	//should be negative
	private String URINE_urineAppearance = "";	//values: Normal, HAZY, CLOUDY
	private String URINE_urineColor = "";	 //YELLOW, STRAW
	private String URINE_urineBacteria = "";	//values: Small, +, Moderate, Trace, Large
	private String URINE_urineMucus = "";	//values: Small, Large, Moderate
	private String URINE_urineAlbumin = "";	//values: Trace or double number (mg/24hrs)
	private String URINE_urineUricAcidCrystals = "";	//values: moderate, large, small, trace
	private String URINE_urineCalciumOxalateCrystals = "";	//values: moderate, large, small
	
	//Blood proteins (2 features)
	private String BLOOD_PROTEINS_albumin = "";	//Normal ranges are 35-50 g/L
	private String BLOOD_PROTEINS_protein = "";	//Normal ranges are 60-84 g/L (Trace or integer value)
	
	//Electrolytes (6 features)
	private String ELECTROLYTES_sodium = "";	//Normal ranges are 133-146 mmol/L
	private String ELECTROLYTES_potassium = "";	//Normal ranges are 3.5-5.4 mmol/L
	private String ELECTROLYTES_bicarbonate = "";	//Normal ranges are 18-23 mmol/L
	private String ELECTROLYTES_chloride = "";	//Normal ranges are 98-106 mmol/L
	private String ELECTROLYTES_anionGap = "";	//Normal ranges are <11 mmol/L
	private String ELECTROLYTES_magnesium = "";	//Normal ranges are or 0.6-0.82 mmol/L
	
	//Kidney tests (3 features)
	private String KIDNEY_bloodUreaNitrogen = "";	//Normal ranges are 1.2-3 mmol/L
	private String KIDNEY_uricAcid = "";	//Normal ranges are 180-480 umol/L
	private String KIDNEY_creatinine = "";	//normal ranges are 53-106 mmol/L for males
	
	//Liver tests (5 features)
	private String LIVER_alkalinePhosphatase = "";	//Normal ranges 50-160 U/L
	private String LIVER_alt = "";	//Normal ranges are 1-21 U/L (also called SGPT)
	private String LIVER_gammaGlutamylTransferase = "";	//Normal ranges are 5-40 U/L
	private String LIVER_ast = "";	//Normal ranges are 7-27 U/L (also called SGOT)
	private String LIVER_bilirubinTotal = "";	//Normal ranges are 5-17 umol/L
	
	//Complete blood count (20 features)
	private String COMPLETE_BLOOD_whiteBloodCell = "";	//Normal ranges are 4.3-10.8 * 10E9/L cells
	private String COMPLETE_BLOOD_neutrophils = "";	//Normal ranges are 45-62 %
	private String COMPLETE_BLOOD_absoluteNeutrophilCount = "";	//Normal ranges are 1.3-5.4 * 10E9/L
	private String COMPLETE_BLOOD_bandNeutrophils = "";	//Normal ranges are 3-5 %
	private String COMPLETE_BLOOD_absoluteBandNeutrophilCount = "";	//Normal ranges are 0-0.7 * 10E9/L cells
	private String COMPLETE_BLOOD_lymphocytes = "";	//Normal ranges are 16-33 %
	private String COMPLETE_BLOOD_absoluteLymphocyteCount ="";	//Normal ranges are 0.7-3.9 *10E9/L cells
	private String COMPLETE_BLOOD_monocytes = "";	//Normal ranges are 3-7 %
	private String COMPLETE_BLOOD_absoluteMonocyteCount = "";	//Normal ranges are 0.1-0.8 * 10E9/L cells 
	private String COMPLETE_BLOOD_eosinophils = "";	//Normal ranges are 1-3 %
	private String COMPLETE_BLOOD_absoluteEosinophilCount = "";	//Normal ranges are 0-0.5 * 10E9/L cells
	private String COMPLETE_BLOOD_basophils = "";	//Normal ranges are 0-0.75 %
	private String COMPLETE_BLOOD_absoluteBasophilCount = "";	//Normal ranges are 0-0.4 * 10E9/L
	private String COMPLETE_BLOOD_redBloodCells = "";	//Normal ranges are 4.2-6.9 * 10E12/L
	private String COMPLETE_BLOOD_hemoglobin = "";	//Normal ranges are Male: 130-180 g/L and Female: 120-160 g/L
	private String COMPLETE_BLOOD_hematocrit = "";	//Normal ranges are Male: 45-62 % and Female: 37-48 % 
	private String COMPLETE_BLOOD_meanCorpuscularHemoglobin = "";	//Normal levels are 27-31 pg/cell
	private String COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration = "";	//Normal levels are 32-36% or 320-360 g/L.
	private String COMPLETE_BLOOD_meanCorpuscularVolume = "";	//Normal levels are 80-99 fL
	private String COMPLETE_BLOOD_platelets = "";	//Normal ranges are 150-350 *10E9/L cells
	
	//Heart disease and muscle degradation (4 features)
	private String HEART_DISEASE_ck = "";	//(Creatine Kinase) Normal ranges are Male: 38-174 U/L; Female: 96-140 U/L
	private String HEART_DISEASE_triglycerides = "";	//Normal ranges depend on age: Ages 10-39 0.61-1.3 mmol/L, ages 40-59 0.77-1.7 mmol/L, age 60+ 0.9-1.7 mmol/L 
	private String HEART_DISEASE_totalCholesterol = "";	//Normal ranges are 3-5 mmol/L
	private String HEART_DISEASE_lactateDehydrogenase = "";	//Normal ranges are 50-150 U/L
	
	//Blood sugar (2 features)
	private String BLOOD_SUGAR_glucose = "";	//Normal ranges are 3.8-6 mmol/L
	private String BLOOD_SUGAR_HbA1c = "";	//(Glycated Hemoglobin) Level >= 6.5% serves as a criterion for the diagnosis of diabetes
	
	//Mineral balance (2 features)
	private String MINERAL_BALANCE_calcium = "";	//Normal ranges are 2.2-2.5 mmol/L 
	private String MINERAL_BALANCE_phosphorus = "";	//Normal ranges are 1-1.5 mmol/L
	
	//Immune response (8 features)
	private String IMMUNE_RESPONSE_immunoglobulinA = "";	//Normal range are 85-385 mg/dL
	private String IMMUNE_RESPONSE_immunoglobulinG = "";	//Normal range are 565-1765 mg/dL
	private String IMMUNE_RESPONSE_immunoglobulinM = "";	//Normal range are 55-375 mg/dL
	private String IMMUNE_RESPONSE_gammaGlobulin = "";	//Normal levels are 2-3 g/dL
	private String IMMUNE_RESPONSE_alpha1Globulin = "";	//Unit is g/dL
	private String IMMUNE_RESPONSE_alpha2Globulin = "";	//Unit is g/dL
	private String IMMUNE_RESPONSE_betaGlobulin = "";	//Unit is g/L
	private String IMMUNE_RESPONSE_albuminGlobulinRatio = "";	// Albumin/globulin ratio (has not unit)
	
	//Hormones
	private String HORMONES_TSH = "";	//(Thyroid Stimulating Hormone) Normal ranges are 0.4-3 U/L
	private String HORMONES_FreeT3 = "";	//Normal ranges are 3.1-7.7 pmol/L
	private String HORMONES_FreeT4 = "";	//Normal ranges are 9-18 pmol/L
	private String HORMONES_betaHCG = "";	//Should be <5 U/L in non-pregnant pre-menopausal women, and <9.5 U/L in post-menopausal women 
	
	//Coagulation measures
	private String COAGULATION_prothrombinTime = "";	//(clotting) Normal ranges are 10-13 seconds
	private String COAGULATION_internationalNormalizedRatio = "";	//(clotting) Normal ranges are 0.8-1.2
	
	//Others
	private String OTHERS_amylase = "";	//Normal ranges are 30-110 U/L
	private String OTHERS_salivaryAmylase = "";	//Unit is %
	private String OTHERS_pancreaticAmylase = "";	//Unit is %
	
	public String getURINE_urinePH() {
		return URINE_urinePH;
	}
	public void setURINE_urinePH(String uRINE_urinePH) {
		URINE_urinePH = uRINE_urinePH;
	}
	public String getURINE_urineProtein() {
		return URINE_urineProtein;
	}
	public void setURINE_urineProtein(String uRINE_urineProtein) {
		URINE_urineProtein = uRINE_urineProtein;
	}
	public String getURINE_urineSpecificGravity() {
		return URINE_urineSpecificGravity;
	}
	public void setURINE_urineSpecificGravity(String uRINE_urineSpecificGravity) {
		URINE_urineSpecificGravity = uRINE_urineSpecificGravity;
	}
	public String getURINE_urineGlucose() {
		return URINE_urineGlucose;
	}
	public void setURINE_urineGlucose(String uRINE_urineGlucose) {
		URINE_urineGlucose = uRINE_urineGlucose;
	}
	public String getURINE_urineWBC() {
		return URINE_urineWBC;
	}
	public void setURINE_urineWBC(String uRINE_urineWBC) {
		URINE_urineWBC = uRINE_urineWBC;
	}
	public String getURINE_urineLeukesterase() {
		return URINE_urineLeukesterase;
	}
	public void setURINE_urineLeukesterase(String uRINE_urineLeukesterase) {
		URINE_urineLeukesterase = uRINE_urineLeukesterase;
	}
	public String getURINE_urineBlood() {
		return URINE_urineBlood;
	}
	public void setURINE_urineBlood(String uRINE_urineBlood) {
		URINE_urineBlood = uRINE_urineBlood;
	}
	public String getURINE_urineRBCs() {
		return URINE_urineRBCs;
	}
	public void setURINE_urineRBCs(String uRINE_urineRBCs) {
		URINE_urineRBCs = uRINE_urineRBCs;
	}
	public String getURINE_urineCasts() {
		return URINE_urineCasts;
	}
	public void setURINE_urineCasts(String uRINE_urineCasts) {
		URINE_urineCasts = uRINE_urineCasts;
	}
	public String getURINE_urineKetones() {
		return URINE_urineKetones;
	}
	public void setURINE_urineKetones(String uRINE_urineKetones) {
		URINE_urineKetones = uRINE_urineKetones;
	}
	public String getURINE_urineAppearance() {
		return URINE_urineAppearance;
	}
	public void setURINE_urineAppearance(String uRINE_urineAppearance) {
		URINE_urineAppearance = uRINE_urineAppearance;
	}
	public String getURINE_urineColor() {
		return URINE_urineColor;
	}
	public void setURINE_urineColor(String uRINE_urineColor) {
		URINE_urineColor = uRINE_urineColor;
	}
	public String getURINE_urineBacteria() {
		return URINE_urineBacteria;
	}
	public void setURINE_urineBacteria(String uRINE_urineBacteria) {
		URINE_urineBacteria = uRINE_urineBacteria;
	}
	public String getURINE_urineMucus() {
		return URINE_urineMucus;
	}
	public void setURINE_urineMucus(String uRINE_urineMucus) {
		URINE_urineMucus = uRINE_urineMucus;
	}
	public String getURINE_urineAlbumin() {
		return URINE_urineAlbumin;
	}
	public void setURINE_urineAlbumin(String uRINE_urineAlbumin) {
		URINE_urineAlbumin = uRINE_urineAlbumin;
	}
	public String getURINE_urineUricAcidCrystals() {
		return URINE_urineUricAcidCrystals;
	}
	public void setURINE_urineUricAcidCrystals(String uRINE_urineUricAcidCrystals) {
		URINE_urineUricAcidCrystals = uRINE_urineUricAcidCrystals;
	}
	public String getURINE_urineCalciumOxalateCrystals() {
		return URINE_urineCalciumOxalateCrystals;
	}
	public void setURINE_urineCalciumOxalateCrystals(
			String uRINE_urineCalciumOxalateCrystals) {
		URINE_urineCalciumOxalateCrystals = uRINE_urineCalciumOxalateCrystals;
	}
	public String getBLOOD_PROTEINS_albumin() {
		return BLOOD_PROTEINS_albumin;
	}
	public void setBLOOD_PROTEINS_albumin(String bLOOD_PROTEINS_albumin) {
		BLOOD_PROTEINS_albumin = bLOOD_PROTEINS_albumin;
	}
	public String getBLOOD_PROTEINS_protein() {
		return BLOOD_PROTEINS_protein;
	}
	public void setBLOOD_PROTEINS_protein(String bLOOD_PROTEINS_protein) {
		BLOOD_PROTEINS_protein = bLOOD_PROTEINS_protein;
	}
	public String getELECTROLYTES_sodium() {
		return ELECTROLYTES_sodium;
	}
	public void setELECTROLYTES_sodium(String eLECTROLYTES_sodium) {
		ELECTROLYTES_sodium = eLECTROLYTES_sodium;
	}
	public String getELECTROLYTES_potassium() {
		return ELECTROLYTES_potassium;
	}
	public void setELECTROLYTES_potassium(String eLECTROLYTES_potassium) {
		ELECTROLYTES_potassium = eLECTROLYTES_potassium;
	}
	public String getELECTROLYTES_bicarbonate() {
		return ELECTROLYTES_bicarbonate;
	}
	public void setELECTROLYTES_bicarbonate(String eLECTROLYTES_bicarbonate) {
		ELECTROLYTES_bicarbonate = eLECTROLYTES_bicarbonate;
	}
	public String getELECTROLYTES_chloride() {
		return ELECTROLYTES_chloride;
	}
	public void setELECTROLYTES_chloride(String eLECTROLYTES_chloride) {
		ELECTROLYTES_chloride = eLECTROLYTES_chloride;
	}
	public String getELECTROLYTES_anionGap() {
		return ELECTROLYTES_anionGap;
	}
	public void setELECTROLYTES_anionGap(String eLECTROLYTES_anionGap) {
		ELECTROLYTES_anionGap = eLECTROLYTES_anionGap;
	}
	public String getELECTROLYTES_magnesium() {
		return ELECTROLYTES_magnesium;
	}
	public void setELECTROLYTES_magnesium(String eLECTROLYTES_magnesium) {
		ELECTROLYTES_magnesium = eLECTROLYTES_magnesium;
	}
	public String getKIDNEY_bloodUreaNitrogen() {
		return KIDNEY_bloodUreaNitrogen;
	}
	public void setKIDNEY_bloodUreaNitrogen(String kIDNEY_bloodUreaNitrogen) {
		KIDNEY_bloodUreaNitrogen = kIDNEY_bloodUreaNitrogen;
	}
	public String getKIDNEY_uricAcid() {
		return KIDNEY_uricAcid;
	}
	public void setKIDNEY_uricAcid(String kIDNEY_uricAcid) {
		KIDNEY_uricAcid = kIDNEY_uricAcid;
	}
	public String getKIDNEY_creatinine() {
		return KIDNEY_creatinine;
	}
	public void setKIDNEY_creatinine(String kIDNEY_creatinine) {
		KIDNEY_creatinine = kIDNEY_creatinine;
	}
	public String getLIVER_alkalinePhosphatase() {
		return LIVER_alkalinePhosphatase;
	}
	public void setLIVER_alkalinePhosphatase(String lIVER_alkalinePhosphatase) {
		LIVER_alkalinePhosphatase = lIVER_alkalinePhosphatase;
	}
	public String getLIVER_alt() {
		return LIVER_alt;
	}
	public void setLIVER_alt(String lIVER_alt) {
		LIVER_alt = lIVER_alt;
	}
	public String getLIVER_gammaGlutamylTransferase() {
		return LIVER_gammaGlutamylTransferase;
	}
	public void setLIVER_gammaGlutamylTransferase(String lIVER_gammaGlutamylTransferase) {
		LIVER_gammaGlutamylTransferase = lIVER_gammaGlutamylTransferase;
	}
	public String getLIVER_ast() {
		return LIVER_ast;
	}
	public void setLIVER_ast(String lIVER_ast) {
		LIVER_ast = lIVER_ast;
	}
	public String getLIVER_bilirubinTotal() {
		return LIVER_bilirubinTotal;
	}
	public void setLIVER_bilirubinTotal(String lIVER_bilirubinTotal) {
		LIVER_bilirubinTotal = lIVER_bilirubinTotal;
	}
	public String getCOMPLETE_BLOOD_whiteBloodCell() {
		return COMPLETE_BLOOD_whiteBloodCell;
	}
	public void setCOMPLETE_BLOOD_whiteBloodCell(
			String cOMPLETE_BLOOD_whiteBloodCell) {
		COMPLETE_BLOOD_whiteBloodCell = cOMPLETE_BLOOD_whiteBloodCell;
	}
	public String getCOMPLETE_BLOOD_neutrophils() {
		return COMPLETE_BLOOD_neutrophils;
	}
	public void setCOMPLETE_BLOOD_neutrophils(String cOMPLETE_BLOOD_neutrophils) {
		COMPLETE_BLOOD_neutrophils = cOMPLETE_BLOOD_neutrophils;
	}
	public String getCOMPLETE_BLOOD_absoluteNeutrophilCount() {
		return COMPLETE_BLOOD_absoluteNeutrophilCount;
	}
	public void setCOMPLETE_BLOOD_absoluteNeutrophilCount(
			String cOMPLETE_BLOOD_absoluteNeutrophilCount) {
		COMPLETE_BLOOD_absoluteNeutrophilCount = cOMPLETE_BLOOD_absoluteNeutrophilCount;
	}
	public String getCOMPLETE_BLOOD_bandNeutrophils() {
		return COMPLETE_BLOOD_bandNeutrophils;
	}
	public void setCOMPLETE_BLOOD_bandNeutrophils(
			String cOMPLETE_BLOOD_bandNeutrophils) {
		COMPLETE_BLOOD_bandNeutrophils = cOMPLETE_BLOOD_bandNeutrophils;
	}
	public String getCOMPLETE_BLOOD_absoluteBandNeutrophilCount() {
		return COMPLETE_BLOOD_absoluteBandNeutrophilCount;
	}
	public void setCOMPLETE_BLOOD_absoluteBandNeutrophilCount(
			String cOMPLETE_BLOOD_absoluteBandNeutrophilCount) {
		COMPLETE_BLOOD_absoluteBandNeutrophilCount = cOMPLETE_BLOOD_absoluteBandNeutrophilCount;
	}
	public String getCOMPLETE_BLOOD_lymphocytes() {
		return COMPLETE_BLOOD_lymphocytes;
	}
	public void setCOMPLETE_BLOOD_lymphocytes(String cOMPLETE_BLOOD_lymphocytes) {
		COMPLETE_BLOOD_lymphocytes = cOMPLETE_BLOOD_lymphocytes;
	}
	public String getCOMPLETE_BLOOD_absoluteLymphocyteCount() {
		return COMPLETE_BLOOD_absoluteLymphocyteCount;
	}
	public void setCOMPLETE_BLOOD_absoluteLymphocyteCount(
			String cOMPLETE_BLOOD_absoluteLymphocyteCount) {
		COMPLETE_BLOOD_absoluteLymphocyteCount = cOMPLETE_BLOOD_absoluteLymphocyteCount;
	}
	public String getCOMPLETE_BLOOD_monocytes() {
		return COMPLETE_BLOOD_monocytes;
	}
	public void setCOMPLETE_BLOOD_monocytes(String cOMPLETE_BLOOD_monocytes) {
		COMPLETE_BLOOD_monocytes = cOMPLETE_BLOOD_monocytes;
	}
	public String getCOMPLETE_BLOOD_absoluteMonocyteCount() {
		return COMPLETE_BLOOD_absoluteMonocyteCount;
	}
	public void setCOMPLETE_BLOOD_absoluteMonocyteCount(
			String cOMPLETE_BLOOD_absoluteMonocyteCount) {
		COMPLETE_BLOOD_absoluteMonocyteCount = cOMPLETE_BLOOD_absoluteMonocyteCount;
	}
	public String getCOMPLETE_BLOOD_eosinophils() {
		return COMPLETE_BLOOD_eosinophils;
	}
	public void setCOMPLETE_BLOOD_eosinophils(String cOMPLETE_BLOOD_eosinophils) {
		COMPLETE_BLOOD_eosinophils = cOMPLETE_BLOOD_eosinophils;
	}
	public String getCOMPLETE_BLOOD_absoluteEosinophilCount() {
		return COMPLETE_BLOOD_absoluteEosinophilCount;
	}
	public void setCOMPLETE_BLOOD_absoluteEosinophilCount(
			String cOMPLETE_BLOOD_absoluteEosinophilCount) {
		COMPLETE_BLOOD_absoluteEosinophilCount = cOMPLETE_BLOOD_absoluteEosinophilCount;
	}
	public String getCOMPLETE_BLOOD_basophils() {
		return COMPLETE_BLOOD_basophils;
	}
	public void setCOMPLETE_BLOOD_basophils(String cOMPLETE_BLOOD_basophils) {
		COMPLETE_BLOOD_basophils = cOMPLETE_BLOOD_basophils;
	}
	public String getCOMPLETE_BLOOD_absoluteBasophilCount() {
		return COMPLETE_BLOOD_absoluteBasophilCount;
	}
	public void setCOMPLETE_BLOOD_absoluteBasophilCount(
			String cOMPLETE_BLOOD_absoluteBasophilCount) {
		COMPLETE_BLOOD_absoluteBasophilCount = cOMPLETE_BLOOD_absoluteBasophilCount;
	}
	public String getCOMPLETE_BLOOD_redBloodCells() {
		return COMPLETE_BLOOD_redBloodCells;
	}
	public void setCOMPLETE_BLOOD_redBloodCells(String cOMPLETE_BLOOD_redBloodCells) {
		COMPLETE_BLOOD_redBloodCells = cOMPLETE_BLOOD_redBloodCells;
	}
	public String getCOMPLETE_BLOOD_hemoglobin() {
		return COMPLETE_BLOOD_hemoglobin;
	}
	public void setCOMPLETE_BLOOD_hemoglobin(String cOMPLETE_BLOOD_hemoglobin) {
		COMPLETE_BLOOD_hemoglobin = cOMPLETE_BLOOD_hemoglobin;
	}
	public String getCOMPLETE_BLOOD_hematocrit() {
		return COMPLETE_BLOOD_hematocrit;
	}
	public void setCOMPLETE_BLOOD_hematocrit(String cOMPLETE_BLOOD_hematocrit) {
		COMPLETE_BLOOD_hematocrit = cOMPLETE_BLOOD_hematocrit;
	}
	public String getCOMPLETE_BLOOD_meanCorpuscularHemoglobin() {
		return COMPLETE_BLOOD_meanCorpuscularHemoglobin;
	}
	public void setCOMPLETE_BLOOD_meanCorpuscularHemoglobin(
			String cOMPLETE_BLOOD_meanCorpuscularHemoglobin) {
		COMPLETE_BLOOD_meanCorpuscularHemoglobin = cOMPLETE_BLOOD_meanCorpuscularHemoglobin;
	}
	public String getCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration() {
		return COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration;
	}
	public void setCOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration(
			String cOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration) {
		COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration = cOMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration;
	}
	public String getCOMPLETE_BLOOD_meanCorpuscularVolume() {
		return COMPLETE_BLOOD_meanCorpuscularVolume;
	}
	public void setCOMPLETE_BLOOD_meanCorpuscularVolume(
			String cOMPLETE_BLOOD_meanCorpuscularVolume) {
		COMPLETE_BLOOD_meanCorpuscularVolume = cOMPLETE_BLOOD_meanCorpuscularVolume;
	}
	public String getCOMPLETE_BLOOD_platelets() {
		return COMPLETE_BLOOD_platelets;
	}
	public void setCOMPLETE_BLOOD_platelets(String cOMPLETE_BLOOD_platelets) {
		COMPLETE_BLOOD_platelets = cOMPLETE_BLOOD_platelets;
	}
	public String getHEART_DISEASE_ck() {
		return HEART_DISEASE_ck;
	}
	public void setHEART_DISEASE_ck(String hEART_DISEASE_ck) {
		HEART_DISEASE_ck = hEART_DISEASE_ck;
	}
	public String getHEART_DISEASE_triglycerides() {
		return HEART_DISEASE_triglycerides;
	}
	public void setHEART_DISEASE_triglycerides(String hEART_DISEASE_triglycerides) {
		HEART_DISEASE_triglycerides = hEART_DISEASE_triglycerides;
	}
	public String getHEART_DISEASE_totalCholesterol() {
		return HEART_DISEASE_totalCholesterol;
	}
	public void setHEART_DISEASE_totalCholesterol(
			String hEART_DISEASE_totalCholesterol) {
		HEART_DISEASE_totalCholesterol = hEART_DISEASE_totalCholesterol;
	}
	public String getHEART_DISEASE_lactateDehydrogenase() {
		return HEART_DISEASE_lactateDehydrogenase;
	}
	public void setHEART_DISEASE_lactateDehydrogenase(
			String hEART_DISEASE_lactateDehydrogenase) {
		HEART_DISEASE_lactateDehydrogenase = hEART_DISEASE_lactateDehydrogenase;
	}
	public String getBLOOD_SUGAR_glucose() {
		return BLOOD_SUGAR_glucose;
	}
	public void setBLOOD_SUGAR_glucose(String bLOOD_SUGAR_glucose) {
		BLOOD_SUGAR_glucose = bLOOD_SUGAR_glucose;
	}
	public String getBLOOD_SUGAR_HbA1c() {
		return BLOOD_SUGAR_HbA1c;
	}
	public void setBLOOD_SUGAR_HbA1c(String bLOOD_SUGAR_HbA1c) {
		BLOOD_SUGAR_HbA1c = bLOOD_SUGAR_HbA1c;
	}
	public String getMINERAL_BALANCE_calcium() {
		return MINERAL_BALANCE_calcium;
	}
	public void setMINERAL_BALANCE_calcium(String mINERAL_BALANCE_calcium) {
		MINERAL_BALANCE_calcium = mINERAL_BALANCE_calcium;
	}
	public String getMINERAL_BALANCE_phosphorus() {
		return MINERAL_BALANCE_phosphorus;
	}
	public void setMINERAL_BALANCE_phosphorus(String mINERAL_BALANCE_phosphorus) {
		MINERAL_BALANCE_phosphorus = mINERAL_BALANCE_phosphorus;
	}
	
	public String getIMMUNE_RESPONSE_immunoglobulinA() {
		return IMMUNE_RESPONSE_immunoglobulinA;
	}
	public void setIMMUNE_RESPONSE_immunoglobulinA(
			String iMMUNE_RESPONSE_immunoglobulinA) {
		IMMUNE_RESPONSE_immunoglobulinA = iMMUNE_RESPONSE_immunoglobulinA;
	}
	public String getIMMUNE_RESPONSE_immunoglobulinG() {
		return IMMUNE_RESPONSE_immunoglobulinG;
	}
	public void setIMMUNE_RESPONSE_immunoglobulinG(
			String iMMUNE_RESPONSE_immunoglobulinG) {
		IMMUNE_RESPONSE_immunoglobulinG = iMMUNE_RESPONSE_immunoglobulinG;
	}
	public String getIMMUNE_RESPONSE_immunoglobulinM() {
		return IMMUNE_RESPONSE_immunoglobulinM;
	}
	public void setIMMUNE_RESPONSE_immunoglobulinM(
			String iMMUNE_RESPONSE_immunoglobulinM) {
		IMMUNE_RESPONSE_immunoglobulinM = iMMUNE_RESPONSE_immunoglobulinM;
	}
	public String getIMMUNE_RESPONSE_gammaGlobulin() {
		return IMMUNE_RESPONSE_gammaGlobulin;
	}
	public void setIMMUNE_RESPONSE_gammaGlobulin(
			String iMMUNE_RESPONSE_gammaGlobulin) {
		IMMUNE_RESPONSE_gammaGlobulin = iMMUNE_RESPONSE_gammaGlobulin;
	}
	public String getIMMUNE_RESPONSE_alpha1Globulin() {
		return IMMUNE_RESPONSE_alpha1Globulin;
	}
	public void setIMMUNE_RESPONSE_alpha1Globulin(
			String iMMUNE_RESPONSE_alpha1Globulin) {
		IMMUNE_RESPONSE_alpha1Globulin = iMMUNE_RESPONSE_alpha1Globulin;
	}
	public String getIMMUNE_RESPONSE_alpha2Globulin() {
		return IMMUNE_RESPONSE_alpha2Globulin;
	}
	public void setIMMUNE_RESPONSE_alpha2Globulin(
			String iMMUNE_RESPONSE_alpha2Globulin) {
		IMMUNE_RESPONSE_alpha2Globulin = iMMUNE_RESPONSE_alpha2Globulin;
	}
	public String getIMMUNE_RESPONSE_betaGlobulin() {
		return IMMUNE_RESPONSE_betaGlobulin;
	}
	public void setIMMUNE_RESPONSE_betaGlobulin(String iMMUNE_RESPONSE_betaGlobulin) {
		IMMUNE_RESPONSE_betaGlobulin = iMMUNE_RESPONSE_betaGlobulin;
	}
	public String getIMMUNE_RESPONSE_albuminGlobulinRatio() {
		return IMMUNE_RESPONSE_albuminGlobulinRatio;
	}
	public void setIMMUNE_RESPONSE_albuminGlobulinRatio(
			String iMMUNE_RESPONSE_albuminGlobulinRatio) {
		IMMUNE_RESPONSE_albuminGlobulinRatio = iMMUNE_RESPONSE_albuminGlobulinRatio;
	}
	public String getHORMONES_TSH() {
		return HORMONES_TSH;
	}
	public void setHORMONES_TSH(String hORMONES_TSH) {
		HORMONES_TSH = hORMONES_TSH;
	}
	public String getHORMONES_FreeT3() {
		return HORMONES_FreeT3;
	}
	public void setHORMONES_FreeT3(String hORMONES_FreeT3) {
		HORMONES_FreeT3 = hORMONES_FreeT3;
	}
	public String getHORMONES_FreeT4() {
		return HORMONES_FreeT4;
	}
	public void setHORMONES_FreeT4(String hORMONES_FreeT4) {
		HORMONES_FreeT4 = hORMONES_FreeT4;
	}
	public String getHORMONES_betaHCG() {
		return HORMONES_betaHCG;
	}
	public void setHORMONES_betaHCG(String hORMONES_betaHCG) {
		HORMONES_betaHCG = hORMONES_betaHCG;
	}
	public String getCOAGULATION_prothrombinTime() {
		return COAGULATION_prothrombinTime;
	}
	public void setCOAGULATION_prothrombinTime(String cOAGULATION_prothrombinTime) {
		COAGULATION_prothrombinTime = cOAGULATION_prothrombinTime;
	}
	public String getCOAGULATION_internationalNormalizedRatio() {
		return COAGULATION_internationalNormalizedRatio;
	}
	public void setCOAGULATION_internationalNormalizedRatio(
			String cOAGULATION_internationalNormalizedRatio) {
		COAGULATION_internationalNormalizedRatio = cOAGULATION_internationalNormalizedRatio;
	}
	public String getOTHERS_amylase() {
		return OTHERS_amylase;
	}
	public void setOTHERS_amylase(String oTHERS_amylase) {
		OTHERS_amylase = oTHERS_amylase;
	}
	public String getOTHERS_salivaryAmylase() {
		return OTHERS_salivaryAmylase;
	}
	public void setOTHERS_salivaryAmylase(String oTHERS_salivaryAmylase) {
		OTHERS_salivaryAmylase = oTHERS_salivaryAmylase;
	}
	public String getOTHERS_pancreaticAmylase() {
		return OTHERS_pancreaticAmylase;
	}
	public void setOTHERS_pancreaticAmylase(String oTHERS_pancreaticAmylase) {
		OTHERS_pancreaticAmylase = oTHERS_pancreaticAmylase;
	}
	
}//class LabData
