package com.example.qosdashboard;

public class DataService implements MetricField{
	public String name = "Data Service";
	public String serviceNonAccessibility = "0";
	public String SetupTime = "0";
	public String MeanDataRate = "0";
	
	public void setName(String _name2) {
		name=_name2;
	}

	public String getName() {
		return name;
	}

	public void setSetupTime(String string) {
		SetupTime=string;
		
	}
	public String getSetupTime() {
		return name;
	}
	public void setServiceNonAccessibility(String string) {
		serviceNonAccessibility=string;
		
	}
	public String getserviceNonAccessibility() {
		return serviceNonAccessibility;
	}
	public void setMeanDataRate(String string) {
		MeanDataRate=string;
		
	}
	public String getMeanDataRate() {
		return MeanDataRate;
	}



}