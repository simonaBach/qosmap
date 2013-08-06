package com.example.qosdashboard;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.example.qosDatabase.ReportDataSource;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;


public class MetricsService extends Service{
	private final Messenger mMessenger = new Messenger(new IncomingMessageHandler()); // Target we publish for clients to send messages to IncomingHandler.
	private static boolean isRunning = false;
	private List<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track of all current registered clients.
	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_SET_INT_VALUE = 3;
	private int lastCallState=0;//idle =0, offhook=1 and ringing =3
	Intent s;
	static final String ACTION ="android.provider.Telephony.";

	private QoSData qosData=  new QoSData();
	private ReportDataSource reportDS= new ReportDataSource(this);
	private long sendTimestamp;
	private long receiveTimestamp;



	/**********Telephony****************/
	TelephonyManager telephonyManager;
	PhoneStateListener listener;

	


	@Override
	public void onCreate() {
		super.onCreate();
		isRunning = true;
		s=new Intent(this, IsDroppedCall.class);//activity that asks if the call was dropped
		s.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//flag to allow the service to launch an activity * must check


		qosData.phoneManufacturer = Build.MANUFACTURER;
		qosData.phoneModel = Build.MODEL;
		qosData.build = String.valueOf(android.os.Build.VERSION.SDK_INT);


		/***************DB Stuff*******/
		reportDS.open();
		// Get the telephony manager
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		getParameters();//get info on the network (operator, mnc, roaming...) and subscriber (imsi, phone...)


		/********Location****/
		// Acquire a reference to the system Location Manager
		LocationManager locationManager =  (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener =  new MyLocationListener();//can also be implemented like the PhoneStateListener
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);


		// Create a new PhoneStateListener
		listener = new PhoneStateListener() {

			// listener for cellSignalChanges
			public void onSignalStrengthsChanged(SignalStrength signalStrength) {
				qosData.signalStrength = signalStrength.getGsmSignalStrength();
				qosData.error=signalStrength.getGsmBitErrorRate();
				if(qosData.networkTypeString.equals("UMTS"))
					qosData.error=signalStrength.getEvdoSnr();

				getParameters();//get info on the network (operator, mnc, roaming...) and subscriber (imsi, phone...)
				sendMessageToUI();
			}


			/** Callback invoked when device cell location changes. */
			public void onCellLocationChanged(CellLocation location)
			{
				getParameters();//get info on the network (operator, mnc, roaming...) and subscriber (imsi, phone...)
				GsmCellLocation gsmCellLocation = (GsmCellLocation) location;
				qosData.cellID=gsmCellLocation.getCid();// Cell ID
				qosData.lac=gsmCellLocation.getLac();//Location Area Code
				qosData.psc=gsmCellLocation.getPsc();//UMTS Primary scrambling code

				sendMessageToUI();
			}


			//CALL_STATE_OFFHOOK: Off-hook. At least one call exists that is dialing, active, or on hold, and no calls are ringing or waiting.
			//CALL_STATE_RINGING: Ringing. A new call arrived and is ringing or waiting. In the latter case, another call is already active.
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {

				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					//mCallState = 0;
					if(lastCallState==1){//offhook
						startActivity(s);
					}
					lastCallState=0;
					//qosData.mnc=0;
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					//mCallState = 2;
					lastCallState=1;
					//qosData.mnc=2;
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					//mCallState = 1;
					lastCallState=2;
					//qosData.mnc=1;
					break;
				}

				getParameters();//get info on the network (operator, mnc, roaming...) and subscriber (imsi, phone...)
				sendMessageToUI();
			}


		};

		// Register the listener wit the telephony manager
		telephonyManager.listen(listener,PhoneStateListener.LISTEN_SERVICE_STATE
				| PhoneStateListener.LISTEN_SIGNAL_STRENGTHS |PhoneStateListener.LISTEN_CALL_STATE
				|PhoneStateListener.LISTEN_CELL_LOCATION|PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);

		/*
		 * SMS daily metric scheduled at a desired time
		 */
		int simState = telephonyManager.getSimState();
		if (simState==TelephonyManager.SIM_STATE_READY) {
			Calendar calendar = Calendar.getInstance();
			long currentTimestamp = calendar.getTimeInMillis();
			calendar.set(Calendar.HOUR_OF_DAY, 13);
			calendar.set(Calendar.MINUTE, 47);
			calendar.set(Calendar.SECOND, 0);
			long diffTimestamp = calendar.getTimeInMillis() - currentTimestamp;
			long myDelay = (diffTimestamp < 0 ? 0 : diffTimestamp);

			new Handler().postDelayed(smsTask, myDelay);

		}


	}

	private Runnable smsTask = new Runnable() {
		public void run() {
			Toast.makeText(MetricsService.this, "task running",
					Toast.LENGTH_SHORT).show();
			Calendar calendar = Calendar.getInstance();
			String message="SMS Task: " + calendar.get(Calendar.HOUR_OF_DAY) + " " 
					+ calendar.get(Calendar.MINUTE) + " "
					+ calendar.get(Calendar.SECOND);

			//sendSMS(qosData.phoneNumber, message);
			Log.d("test", "started at " 
					+ calendar.get(Calendar.HOUR_OF_DAY) + " " 
					+ calendar.get(Calendar.MINUTE) + " "
					+ calendar.get(Calendar.SECOND)
					);
		}
	};

	/**
	 * Send the data to all clients.
	 * @param intvaluetosend The value to send.
	 */
	private void sendMessageToUI() {
		Iterator<Messenger> messengerIterator = mClients.iterator();            
		while(messengerIterator.hasNext()) {
			Messenger messenger = messengerIterator.next();
			try {

				reportDS.newReport(qosData);//insert the data to the database
				Message msg = Message.obtain();
				msg.obj = qosData; 
				messenger.send(msg);

			} catch (RemoteException e) {
				// The client is dead. Remove it from the list.
				mClients.remove(messenger);
			}
		}
	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return Service.START_STICKY; // Run until explicitly stopped.
	}

	@Override
	public IBinder onBind(Intent intent) {

		return mMessenger.getBinder();
	}

	public static boolean isRunning() {
		// TODO Auto-generated method stub
		return true;
	}



	/*
	 * This function retrieves the info that doesn't change often (mcc, mnc, network type... ) and should be called every time a QoS metric changes
	 * Changes the global variable qosData which contains the telecom context (mcc, signal strength, operator , imsi ...)
	 */
	private void getParameters(){
		LocationManager dummyLM =  (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//Set latitude an longitude to 0 so it doesn't store the last value when the locat
		if ( !dummyLM.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
			
			//buildAlertMessageNoGps();// TO DOOOOO!!!
		}


		int phoneType = telephonyManager.getPhoneType();
		switch (phoneType) {
		case (TelephonyManager.PHONE_TYPE_CDMA): break;
		case (TelephonyManager.PHONE_TYPE_SIP) : break;
		case (TelephonyManager.PHONE_TYPE_GSM) : break;
		case (TelephonyManager.PHONE_TYPE_NONE): break;
		default: break;
		}

		// This part is used to listen for properties of the neighboring cells
		List<NeighboringCellInfo> neighboringCellInfos = this.telephonyManager.getNeighboringCellInfo();
		for(NeighboringCellInfo neighboringCellInfo : neighboringCellInfos)
		{
			neighboringCellInfo.getCid();
			neighboringCellInfo.getLac();
			neighboringCellInfo.getPsc();
			neighboringCellInfo.getNetworkType();
			neighboringCellInfo.getRssi();

			Log.d("cellp",neighboringCellInfo.toString());
		}

		// Get connected network country ISO code
		String networkCountry = telephonyManager.getNetworkCountryIso();

		// Get the connected network operator ID (MCC + MNC)
		String networkOperatorId = telephonyManager.getNetworkOperator();

		// Get the connected network operator name
		String networkName = telephonyManager.getNetworkOperatorName();

		// Get the type of network you are connected with 
		int networkType = telephonyManager.getNetworkType();
		switch (networkType) {
		case (TelephonyManager.NETWORK_TYPE_1xRTT) :
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_1xRTT;
		qosData.networkTypeString="1xRTT";
		break;
		case (TelephonyManager.NETWORK_TYPE_CDMA) :
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_CDMA;
		qosData.networkTypeString="CDMA";
		break;
		case (TelephonyManager.NETWORK_TYPE_EDGE) : 
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_EDGE;
		qosData.networkTypeString="EDGE";
		break;
		case (TelephonyManager.NETWORK_TYPE_EVDO_0) :
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_EVDO_0;
		qosData.networkTypeString="EVDO_0";
		break;
		case (TelephonyManager.NETWORK_TYPE_EVDO_A) :
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_EVDO_A;
		qosData.networkTypeString="EVDO_A";
		break;
		case (TelephonyManager.NETWORK_TYPE_EVDO_B) :
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_EVDO_B;
		qosData.networkTypeString="EVDO_B";
		break;
		case (TelephonyManager.NETWORK_TYPE_GPRS) :
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_GPRS;
		qosData.networkTypeString="GPRS";
		break;
		case (TelephonyManager.NETWORK_TYPE_HSDPA) : 
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_HSDPA;
		qosData.networkTypeString="HSDPA";
		break;
		case (TelephonyManager.NETWORK_TYPE_HSPA) :
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_HSPA;
		qosData.networkTypeString="HSPA";
		break;
		case (TelephonyManager.NETWORK_TYPE_HSPAP) :
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_HSPAP;
		qosData.networkTypeString="HSPAP";
		break;
		case (TelephonyManager.NETWORK_TYPE_IDEN) :
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_IDEN;
		qosData.networkTypeString="IDEN";
		break;
		case (TelephonyManager.NETWORK_TYPE_LTE) :
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_LTE;
		qosData.networkTypeString="LTE";
		break;
		case (TelephonyManager.NETWORK_TYPE_UMTS) :
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_UMTS;
		qosData.networkTypeString="UMTS";
		break;
		case (TelephonyManager.NETWORK_TYPE_UNKNOWN) :
			qosData.networkTYPE=TelephonyManager.NETWORK_TYPE_UNKNOWN;
		qosData.networkTypeString="Unknown";
		break;
		}


		int simState = telephonyManager.getSimState();
		switch (simState) {
		case (TelephonyManager.SIM_STATE_ABSENT): break;
		case (TelephonyManager.SIM_STATE_NETWORK_LOCKED): break;
		case (TelephonyManager.SIM_STATE_PIN_REQUIRED): break;
		case (TelephonyManager.SIM_STATE_PUK_REQUIRED): break;
		case (TelephonyManager.SIM_STATE_UNKNOWN): break;
		case (TelephonyManager.SIM_STATE_READY): 

			/***************Network Data***************************/
			// get the ISO country code equivalent of the current registered operator's MCC (Mobile Country Code
			qosData.mcc=telephonyManager.getNetworkCountryIso();
		// Get the SIM country ISO code
		String simCountry = telephonyManager.getSimCountryIso();
		// Get the operator code of the active SIM (MCC + MNC)
		String simOperatorCode = telephonyManager.getSimOperator();
		//get  the alphabetic name of current registered operator.
		qosData.operatorName=telephonyManager.getNetworkOperatorName();
		// Get the name of the SIM operator
		String simOperatorName = telephonyManager.getSimOperatorName();
		// -- Requires READ_PHONE_STATE uses-permission --
		// Get the SIM’s serial number
		String simSerial = telephonyManager.getSimSerialNumber();
		//get  if the device is considered roaming on the current network, for GSM purposes.
		qosData.roaming=telephonyManager.isNetworkRoaming();

		/***************Subscriber Data***************************/
		//get  the unique subscriber ID, for example, the IMSI for a GSM phone.
		qosData.imsi=telephonyManager.getSubscriberId();
		//get phone number
		qosData.phoneNumber= telephonyManager.getLine1Number();

		/***************Data Connection Data***************************/
		//gets a constant indicating the type of activity on a data connection (cellular)
		qosData.dataActivity = telephonyManager.getDataActivity();
		//gets a constant indicating the current data connection state (cellular).
		qosData.dataState=telephonyManager.getDataState();
		}

	}

	//////////////////////////////////////////
	// Nested classes
	/////////////////////////////////////////

	/**
	 * Handle incoming messages from MainActivity
	 */
	private class IncomingMessageHandler extends Handler { // Handler of incoming messages from clients.
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				sendMessageToUI();
				break;
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
			case MSG_SET_INT_VALUE:

				break;
			default:
				super.handleMessage(msg);
			}
		}
	}


	/*******************Detect incoming SMS method 2*******************/
	/*private void getSMS(String phoneNumber, String message) {
		registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) 
			{   
				Calendar calendar3 = Calendar.getInstance();
				receiveTimestamp = calendar3.getTimeInMillis();
				qosData.eteDeliveryTime=(int) (receiveTimestamp-sendTimestamp);
				sendMessageToUI();
				Toast.makeText(MetricsService.this, "SMS delivered, delay: "+ (receiveTimestamp-sendTimestamp),
						Toast.LENGTH_SHORT).show();

				//---get the SMS message passed in---
				Bundle bundle = intent.getExtras();   
				SmsMessage[] msgs = null;         
				String fromSMS="";
				if (bundle != null)
				{//---retrieve the SMS message received---
					Object[] pdus = (Object[]) bundle.get("pdus");
					msgs = new SmsMessage[pdus.length];            
					for (int i=0; i<msgs.length; i++){
						msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
						fromSMS = msgs[i].getOriginatingAddress();

					}
					if(fromSMS.equals(qosData.phoneNumber)){
						qosData.eteDeliveryTime=(int) (receiveTimestamp-sendTimestamp);
						sendMessageToUI();
						Toast.makeText(MetricsService.this, "SMS delivered, delay: "+ (receiveTimestamp-sendTimestamp),
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		}, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

	}*/

	private void sendSMS(String phoneNumber, String message) {
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";
		Calendar calendar2 = Calendar.getInstance();


		PendingIntent sentPI = PendingIntent.getBroadcast(MetricsService.this, 0,
				new Intent(SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(MetricsService.this,
				0, new Intent(DELIVERED), 0);

		// ---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:


					Toast.makeText(MetricsService.this, "SMS sent",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(MetricsService.this, "Generic failure",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(MetricsService.this, "No service",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(MetricsService.this, "Null PDU",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio off",
							Toast.LENGTH_SHORT).show();
					break;

				}
			}
		}, new IntentFilter(SENT));


		// ---when the SMS has been delivered---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				Calendar calendar3 = Calendar.getInstance();
				receiveTimestamp = calendar3.getTimeInMillis();
				getParameters();
				qosData.eteDeliveryTime=(int) (receiveTimestamp-sendTimestamp);
				sendMessageToUI();
				Toast.makeText(MetricsService.this, "SMS delivered, delay: "+ (receiveTimestamp-sendTimestamp),
						Toast.LENGTH_SHORT).show();


			}
		}, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));


		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(qosData.phoneNumber, null, message, sentPI, deliveredPI);
		sendTimestamp = calendar2.getTimeInMillis();

	}

	/*----------Listener class to get coordinates ------------- */
	private  class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			//editLocation.setText("");
			//pb.setVisibility(View.INVISIBLE);
			Toast.makeText(getBaseContext(),"Location changed: Lat: " + loc.getLatitude() + " Lng: "+ loc.getLongitude(), Toast.LENGTH_SHORT).show();
			getParameters();
			qosData.longitud =  loc.getLongitude();
			qosData.latitude =  loc.getLatitude();
			sendMessageToUI();
			/*
			//-------to get City-Name from coordinates -------- 
			String cityName = null;
			Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
			List<Address> addresses;
			try {
				addresses = gcd.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
				if (addresses.size() > 0)
					System.out.println(addresses.get(0).getLocality());
				cityName = addresses.get(0).getLocality();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//String s = longitude + "\n" + latitude + "\n\nMy Current City is: "+ cityName;
			 */
		}//onLocationChanged

		@Override
		public void onProviderDisabled(String provider) {
			qosData.longitud = 0.0;
			qosData.latitude =  0.0;
		}
		@Override
		public void onProviderEnabled(String provider) {}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	}//MyLocationListener

	@Override
	public void onDestroy() {
		reportDS.close();
	}


}