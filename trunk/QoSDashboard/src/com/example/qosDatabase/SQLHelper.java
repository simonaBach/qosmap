package com.example.qosDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLHelper extends SQLiteOpenHelper {

	public static final String TABLE_REPORT = "reports";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_IMSI = "imsi";
	public static final String COLUMN_OPERATOR = "operator";
	public static final String COLUMN_NETWORK_TYPE = "networkType";
	public static final String COLUMN_MCC = "mcc";
	public static final String COLUMN_PHONE_NUMBER = "phoneNumber";
	public static final String COLUMN_MANUFACTURER = "manufacturer";
	public static final String COLUMN_MODEL = "model";
	public static final String COLUMN_SIGNAL_STRENGHT = "signalStrength";
	public static final String COLUMN_PHONE_TYPE = "phoneType";
	public static final String COLUMN_DATE_TIME = "dateTime";
	public static final String COLUMN_ROAMING = "roaming";
	public static final String COLUMN_BER = "ber";
	public static final String COLUMN_SUCCESSFUL_CALL = "successfulCall";
	public static final String COLUMN_DROPPED_CALL = "dropedCall";
	public static final String COLUMN_SMS_DELIVERY_TIME = "smsDeliveryTime";
	public static final String COLUMN_SMS_DELIVERY = "smsDelivery";
	public static final String COLUMN_SMS_INTEGRITY = "smsIntegrity";
	public static final String COLUMN_MEAN_DATA_RATE = "meanDataRate";
	public static final String COLUMN_NON_ACCESSIBILITY = "nonAccessibility";
	public static final String COLUMN_SETUP_TIME = "setupTime";
	public static final String COLUMN_LAC = "LAC";
	public static final String COLUMN_PSC = "PSC";
	public static final String COLUMN_CELL_ID = "cellID";
	public static final String COLUMN_LONGITUDE = "Longitude";
	public static final String COLUMN_LATITUDE = "Latitude";
	public static final String COLUMN_SOFTWARE = "Software";

	private static final String DATABASE_NAME = "QoSMap.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private String DATABASE_CREATE = "create table " + TABLE_REPORT +"("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_TIME + " timestamp not null default current_timestamp, "
			+ COLUMN_IMSI + " text, "
			+ COLUMN_OPERATOR + " text , "
			+ COLUMN_NETWORK_TYPE + " text , "
			+ COLUMN_MCC + " text, "
			+ COLUMN_PHONE_NUMBER + " text, "
			+ COLUMN_MANUFACTURER + " text, "
			+ COLUMN_MODEL + " text, "
			+ COLUMN_SOFTWARE + " text, "
			+ COLUMN_SIGNAL_STRENGHT + " text, "
			+ COLUMN_PHONE_TYPE + " text, "
			+ COLUMN_DATE_TIME + " text, "
			+ COLUMN_ROAMING + " text, "
			+ COLUMN_BER + " text, "
			+ COLUMN_SUCCESSFUL_CALL + " text, "
			+ COLUMN_DROPPED_CALL + " text, "
			+ COLUMN_SMS_DELIVERY_TIME + " text, "
			+ COLUMN_SMS_DELIVERY + " text, "
			+ COLUMN_SMS_INTEGRITY + " text, "
			+ COLUMN_MEAN_DATA_RATE + " text, "
			+ COLUMN_NON_ACCESSIBILITY + " text, "
			+ COLUMN_SETUP_TIME + " text, "
			+ COLUMN_LAC + " text, "
			+ COLUMN_PSC + " text, "
			+ COLUMN_CELL_ID + " text, "
			+ COLUMN_LONGITUDE + " text, "
			+ COLUMN_LATITUDE + " text);";

	public SQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {

		database.execSQL(DATABASE_CREATE);

	}



	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORT);
		    onCreate(db);
	}

}
