package sdu;

//class for each symptom and its location for patients (File: SubjectALSHistory)
public class SymptomLocationSDU{
		
		private String symptom = null;	//symptom of disease (eg. speech, weakness, ...)
		private String location = null;	//location of symptom (eg. leg, arm, ...)
		
		
		//Constructor
		public SymptomLocationSDU(String symptom, String location) {
			super();
			this.symptom = symptom;
			this.location = location;
		}
		
		public String getSymptom() {
			return symptom;
		}
		public void setSymptom(String symptom) {
			this.symptom = symptom;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		
	}//class SymptomLocation