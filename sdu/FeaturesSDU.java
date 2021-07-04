package sdu;
//this class defines all dynamic features 
public class FeaturesSDU {

		//Vital sign
		private int pulse = -1;
		private double respiratoryRate = -1;
		private double temperature = -1;
		private double weight = -1;
		private int bloodPressureDiastolic = -1;
		private int bloodPressureSystolic = -1;
		
		//Forced Vital Capacity
		private double FVC_subjectLiters = -1;
		
		//Slow Vital Capacity
		private double SVC_subjectLiters = -1;
		
		/************Laboratory data******************/
		
		//Urine
		private String URINE_urinePH = "";
		private String URINE_urineProtein = "";
		private String URINE_urineSpecificGravity = "";
		private String URINE_urineGlucose = "";
		private String URINE_urineWBC = "";
		private String URINE_urineLeukesterase = "";
		private String URINE_urineBlood = "";
		private String URINE_urineRBCs = "";
		private String URINE_urineCasts = "";
		private String URINE_urineKetones = "";
		private String URINE_urineAppearance = "";
		private String URINE_urineColor = "";
		private String URINE_urineBacteria = "";
		private String URINE_urineMucus = "";
		private String URINE_urineAlbumin = "";
		private String URINE_urineUricAcidCrystals = "";
		private String URINE_urineCalciumOxalateCrystals = "";
		
		//Blood proteins
		private String BLOOD_PROTEINS_albumin = "";
		private String BLOOD_PROTEINS_protein = "";
		
		//Electrolytes
		private String ELECTROLYTES_sodium = "";
		private String ELECTROLYTES_potassium = "";
		private String ELECTROLYTES_bicarbonate = "";
		private String ELECTROLYTES_chloride = "";
		private String ELECTROLYTES_anionGap = "";
		private String ELECTROLYTES_magnesium = "";
		
		//Kidney tests
		private String KIDNEY_bloodUreaNitrogen = "";
		private String KIDNEY_uricAcid = "";
		private String KIDNEY_creatinine = "";
		
		//Liver tests
		private String LIVER_alkalinePhosphatase = "";
		private String LIVER_alt = "";
		private String LIVER_gammaGlutamylTransferase = "";
		private String LIVER_ast = "";
		private String LIVER_bilirubinTotal = "";
		
		//Complete blood count
		private String COMPLETE_BLOOD_whiteBloodCell = "";
		private String COMPLETE_BLOOD_neutrophils = "";
		private String COMPLETE_BLOOD_absoluteNeutrophilCount = "";
		private String COMPLETE_BLOOD_bandNeutrophils = "";
		private String COMPLETE_BLOOD_absoluteBandNeutrophilCount = "";
		private String COMPLETE_BLOOD_lymphocytes = "";
		private String COMPLETE_BLOOD_absoluteLymphocyteCount = "";
		private String COMPLETE_BLOOD_monocytes = "";
		private String COMPLETE_BLOOD_absoluteMonocyteCount = "";
		private String COMPLETE_BLOOD_eosinophils = "";
		private String COMPLETE_BLOOD_absoluteEosinophilCount = "";
		private String COMPLETE_BLOOD_basophils = "";
		private String COMPLETE_BLOOD_absoluteBasophilCount = "";
		private String COMPLETE_BLOOD_redBloodCells = "";
		private String COMPLETE_BLOOD_hemoglobin = "";
		private String COMPLETE_BLOOD_hematocrit = "";
		private String COMPLETE_BLOOD_meanCorpuscularHemoglobin = "";
		private String COMPLETE_BLOOD_meanCorpuscularHemoglobinConcentration = "";
		private String COMPLETE_BLOOD_meanCorpuscularVolume = "";
		private String COMPLETE_BLOOD_platelets = "";
		
		//Heart disease and muscle degradation
		private String HEART_DISEASE_ck = "";
		private String HEART_DISEASE_triglycerides = "";
		private String HEART_DISEASE_totalCholesterol = "";
		private String HEART_DISEASE_lactateDehydrogenase = "";
		
		//Blood sugar
		private String BLOOD_SUGAR_glucose = "";
		private String BLOOD_SUGAR_HbA1c = "";
		
		//Mineral balance
		private String MINERAL_BALANCE_calcium = "";
		private String MINERAL_BALANCE_phosphorus = "";
		
		//Immune response
		private String IMMUNE_RESPONSE_immunoglobulinA = "";
		private String IMMUNE_RESPONSE_immunoglobulinG = "";
		private String IMMUNE_RESPONSE_immunoglobulinM = "";
		private String IMMUNE_RESPONSE_gammaGlobulin = "";
		private String IMMUNE_RESPONSE_alpha1Globulin = "";
		private String IMMUNE_RESPONSE_alpha2Globulin = "";
		private String IMMUNE_RESPONSE_betaGlobulin = "";
		private String IMMUNE_RESPONSE_albuminGlobulinRatio = "";
		
		//Hormones
		private String HORMONES_TSH = "";
		private String HORMONES_FreeT3 = "";
		private String HORMONES_FreeT4 = "";
		private String HORMONES_betaHCG = "";
		
		//Coagulation measures
		private String COAGULATION_prothrombinTime = "";
		private String COAGULATION_internationalNormalizedRatio = "";
		
		//Others
		private String OTHERS_amylase = "";
		private String OTHERS_salivaryAmylase = "";
		private String OTHERS_pancreaticAmylase = "";
		
		public int getPulse() {
			return pulse;
		}
		public void setPulse(int pulse) {
			this.pulse = pulse;
		}
		public double getRespiratoryRate() {
			return respiratoryRate;
		}
		public void setRespiratoryRate(double respiratoryRate) {
			this.respiratoryRate = respiratoryRate;
		}
		public double getTemperature() {
			return temperature;
		}
		public void setTemperature(double temperature) {
			this.temperature = temperature;
		}
		public double getWeight() {
			return weight;
		}
		public void setWeight(double weight) {
			this.weight = weight;
		}
		public int getBloodPressureDiastolic() {
			return bloodPressureDiastolic;
		}
		public void setBloodPressureDiastolic(int bloodPressureDiastolic) {
			this.bloodPressureDiastolic = bloodPressureDiastolic;
		}
		public int getBloodPressureSystolic() {
			return bloodPressureSystolic;
		}
		public void setBloodPressureSystolic(int bloodPressureSystolic) {
			this.bloodPressureSystolic = bloodPressureSystolic;
		}
		public double getFVC_subjectLiters() {
			return FVC_subjectLiters;
		}
		public void setFVC_subjectLiters(double fVC_subjectLiters) {
			FVC_subjectLiters = fVC_subjectLiters;
		}
		public double getSVC_subjectLiters() {
			return SVC_subjectLiters;
		}
		public void setSVC_subjectLiters(double sVC_subjectLiters) {
			SVC_subjectLiters = sVC_subjectLiters;
		}
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
		public void setLIVER_gammaGlutamylTransferase(
				String lIVER_gammaGlutamylTransferase) {
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
	
	
}//class Features
