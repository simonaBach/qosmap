package com.example.qosdashboard;

import android.telephony.CellLocation;

public class QoSData {

	public String build="null";

	/*********Network Data*********************/
	public int gsmDbm=-1;
	public int networkTYPE=-1;//by default unknown
	public String networkTypeString="null";//by default unknown
	public int phoneType=-1;
	public String phoneTypeString="null";
	public String mcc=null;
	public String operatorName="null";
	public int mnc=888;
	
	

	
	/*********Subscriber Data******************/

	public String imsi="null";
	public String phoneNumber="null";
	public String phoneManufacturer="null";
	public String phoneModel="null";
	public int signalStrength=-1;
	public boolean roaming=false;
	public int error=-1;
	public int cellID=-1;
	public int lac=-1;
	public int psc=-1;
	public double longitud=0;
	public double latitude=0;
	
	/*****************TelephonyService*********************/
	public boolean callSuccessful=false;
	public boolean callDropped=false;
	
	/***************Data Connection Data***************************/
	public int dataActivity=-1;
	public int dataState=-1;
	public int meanDAta=-1;
	public int usuccessfulAttemp=-1;
	public int setupTime=-1;

	/*********SMS Metrics******************/
	public int accessDelay= -1;
	public int smsDelivery= -1;
	public boolean smsIntegrity= false;
	public int eteDeliveryTime=-1;
	
	/**************REPORT********************/
	public String dateTime="null";
	
	public QoSData(){

	}
}
