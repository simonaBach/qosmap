package com.example.qosdashboard;

public class TelephonyService implements MetricField{
	public String name = "Telephony Service";
	public String serviceAccessibility = "123";
	public String cutOffCallRatio = "321";
	
	public void setName(String _name2) {
		name=_name2;
	}

	public String getName() {
		return name;
	}

	public void setServiceAccessibility(String _mcc) {
		serviceAccessibility=_mcc;
	}

	public String getServiceAccessibility() {
		return serviceAccessibility;
	}
	
	public void setCutOffCallRatio(String _mnc) {
		cutOffCallRatio=_mnc;
	}

	public String getCutOffCallRatio() {
		return cutOffCallRatio;
	}

}
