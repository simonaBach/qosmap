package com.example.qosdashboard;

public class NetworkFields implements MetricField{
	public String name = "Network Info";
	public String mcc = "123";
	public String mnc = "321";
	public String signalStrength = "null";
	public String networkType = "0";
	public String networkTypeString = "unknown";
	String operatorName="unknown";
	boolean roaming=false;
	int phoneType=0;
	
	public void setName(String _name2) {
		name=_name2;
	}

	public String getName() {
		return name;
	}

	public void setMCC(String _mcc) {
		mcc=_mcc;
	}

	public String getMCC() {
		return mcc;
	}
	
	public void setMNC(String _mnc) {
		mnc=_mnc;
	}

	public String getMNC() {
		return mnc;
	}
	
	public void setSignalStrength(String _mnc) {
		signalStrength=_mnc;
	}

	public String getSignalStrength() {
		return signalStrength;
	}

}
