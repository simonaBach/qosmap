package com.example.qosdashboard;

public class SMSService implements MetricField{
	public String name = "SMS Service";
	public int accessDelay = -1;
	public int completionFailureRatio = -1;
	public int eteDeliveryTime = -1;
	
	public void setName(String _name2) {
		name=_name2;
	}

	public String getName() {
		return name;
	}


}
