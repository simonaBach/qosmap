package com.example.qosdashboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;




public class MainDashboard extends Activity implements ServiceConnection  {


	Intent service;
	
	private Messenger mServiceMessenger = null;
	boolean mIsBound;
	private ServiceConnection mConnection = this;
	private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());//for  messages from the service 
	ListView lv1 ;
	ArrayList<MetricField> MetricField;
	
	
	private int reportCount=0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		lv1 = (ListView) findViewById(R.id.ListView01);
		
		
		
		
		
		MetricField = GetMetricField(new QoSData());

		//final ListView lv1 = (ListView) findViewById(R.id.ListView01);
		lv1.setAdapter(new MyCustomBaseAdapter(this, MetricField));

		lv1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
				Object o = lv1.getItemAtPosition(position);
				MetricField fullObject = (MetricField)o;
				Toast.makeText(MainDashboard.this, "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
			}  
		});

		automaticBind();
	}
	/**
	 * Check if the service is running. If the service is running 
	 * when the activity starts, we want to automatically bind to it.
	 */
	private void automaticBind() {
		if (MetricsService.isRunning()) 
			doBindService();


	}

	private void doBindService() {
		bindService(new Intent(this, MetricsService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}


	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mServiceMessenger = new Messenger(service);

		try {
			Message msg = Message.obtain(null, MetricsService.MSG_REGISTER_CLIENT);
			msg.replyTo = mMessenger;
			mServiceMessenger.send(msg);
		} 
		catch (RemoteException e) {
			// In this case the service has crashed before we could even do anything with it
		} 

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mServiceMessenger = null;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			doUnbindService();
		} catch (Throwable t) {

		}
	}

	/**
	 * Send data to the service
	 * @param intvaluetosend The data to send
	 */
	private void sendMessageToService(int intvaluetosend) {
		if (mIsBound) {
			if (mServiceMessenger != null) {
				try {
					Message msg = Message.obtain(null, MetricsService.MSG_SET_INT_VALUE, intvaluetosend, 0);
					msg.replyTo = mMessenger;
					mServiceMessenger.send(msg);
				} catch (RemoteException e) {
				}
			}
		}
	}
	/**
	 * Un-bind this Activity to MyService
	 */     
	private void doUnbindService() {
		if (mIsBound) {
			// If we have received the service, and hence registered with it, then now is the time to unregister.
			if (mServiceMessenger != null) {
				try {
					Message msg = Message.obtain(null, MetricsService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = mMessenger;
					mServiceMessenger.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service has crashed.
				}
			}
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	/**
	 * Handle incoming messages from MyService
	 */
	private class IncomingMessageHandler extends Handler {          
		@Override
		public void handleMessage(Message msg) {
			// Log.d(LOGTAG,"IncomingHandler:handleMessage");
			QoSData data = (QoSData) msg.obj;  
						

			//signalStrength.setText(data.gsmDbm);
			//networkType.setText(data.networkTYPE);
			reportCount++;
			Toast.makeText(getApplicationContext(),"Report #: "+reportCount, Toast.LENGTH_SHORT).show();
			MetricField = GetMetricField(data);
			lv1.setAdapter(new MyCustomBaseAdapter(MainDashboard.this, MetricField));
		}
	}
	private ArrayList<MetricField> GetMetricField(QoSData data){
		ArrayList<MetricField> results = new ArrayList<MetricField>();

		NetworkFields sr1 = new NetworkFields();
		sr1.setMCC(data.mcc);
		sr1.setMNC(Integer.toString(data.mnc));
		sr1.setSignalStrength(Integer.toString(data.signalStrength));
		sr1.networkTypeString=data.networkTypeString;
		sr1.operatorName=data.operatorName;
		sr1.phoneType=data.phoneType;
		sr1.roaming=data.roaming;
		results.add(sr1);

		SubscriberInfo sr3 = new SubscriberInfo();
		sr3.setIMSI(data.imsi);
		sr3.setphoneNumber(data.phoneNumber);
		sr3.cellID=data.cellID;
		sr3.lac=data.lac;
		sr3.psc=data.psc;
		sr3.error=data.error;
		sr3.latitude=data.latitude;
		sr3.longitud=data.longitud;
		results.add(sr3);


		TelephonyService sr4 = new TelephonyService();
		sr4.setCutOffCallRatio("123");
		sr4.setServiceAccessibility("345");
		results.add(sr4);

		SMSService sr5= new SMSService();
		sr5.accessDelay=data.accessDelay;
		sr5.eteDeliveryTime=data.eteDeliveryTime;
	
		results.add(sr5);

		DataService sr6 = new DataService();
		sr6.setSetupTime("0");
		results.add(sr6);

		return results;
	}
	
	/*
	 * Gets called by onclick atribute of  a button in the layout
	 */
	public void backupDB(View v) {
		copyDatabase(getApplicationContext(), "QoSMap.db");
		
	}
	
	// Copy to sdcard for debug use
    public static void copyDatabase(Context c, String DATABASE_NAME) {
        String databasePath = c.getDatabasePath(DATABASE_NAME).getPath();
        File f = new File(databasePath);
        OutputStream myOutput = null;
        InputStream myInput = null;
       
        if (f.exists()) {
            try {

                File directory = new File("/mnt/sdcard/DB_DEBUG");
                if (!directory.exists())
                    directory.mkdir();

                myOutput = new FileOutputStream(directory.getAbsolutePath()
                        + "/" + DATABASE_NAME);
                myInput = new FileInputStream(databasePath);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                Toast.makeText(c, "Database copied", Toast.LENGTH_LONG).show();

                myOutput.flush();
            } catch (Exception e) {
            } finally {
                try {
                    if (myOutput != null) {
                        myOutput.close();
                        myOutput = null;
                    }
                    if (myInput != null) {
                        myInput.close();
                        myInput = null;
                    }
                } catch (Exception e) {
                }
            }
        }
    }

}
