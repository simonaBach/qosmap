package com.example.qosdashboard;

import android.telephony.CellLocation;

public class SubscriberInfo implements MetricField{
	public String name = "Subscriber Information";
	public String IMSI = "null";
	public String phoneNumber = "null";
	public int error=-1;
	public int cellID=-1;
	public int lac=-1;
	public int psc=-1;
	public double longitud=0;
	public double latitude=0;
	
	
	public void setName(String _name2) {
		name=_name2;
	}

	public String getName() {
		return name;
	}

	public void setIMSI(String i) {
		IMSI=i;
	}

	public String getIMSI() {
		return IMSI;
	}
	
	public void setphoneNumber(String _phoneNumber) {
		phoneNumber=_phoneNumber;
	}

	public String getphoneNumber() {
		return phoneNumber;
	}
}
