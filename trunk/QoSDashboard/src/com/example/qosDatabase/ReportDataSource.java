package com.example.qosDatabase;

import java.util.ArrayList;
import java.util.List;

import com.example.qosdashboard.QoSData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class ReportDataSource {
	// Database fields
	private SQLiteDatabase database;
	private SQLHelper dbHelper;
	private String[] allColumns = { 
			SQLHelper.COLUMN_ID,SQLHelper.COLUMN_TIME,
			SQLHelper.COLUMN_IMSI, SQLHelper.COLUMN_OPERATOR,
			SQLHelper.COLUMN_NETWORK_TYPE, SQLHelper.COLUMN_MCC,
			SQLHelper.COLUMN_PHONE_NUMBER, SQLHelper.COLUMN_MANUFACTURER,
			SQLHelper.COLUMN_MODEL, SQLHelper.COLUMN_SIGNAL_STRENGHT,
			SQLHelper.COLUMN_PHONE_TYPE, SQLHelper.COLUMN_DATE_TIME,
			SQLHelper.COLUMN_ROAMING, SQLHelper.COLUMN_BER,
			SQLHelper.COLUMN_SUCCESSFUL_CALL, SQLHelper.COLUMN_DROPPED_CALL,
			SQLHelper.COLUMN_SMS_DELIVERY_TIME, SQLHelper.COLUMN_SMS_DELIVERY,
			SQLHelper.COLUMN_SMS_INTEGRITY, SQLHelper.COLUMN_MEAN_DATA_RATE,
			SQLHelper.COLUMN_LAC, SQLHelper.COLUMN_CELL_ID,
			SQLHelper.COLUMN_PSC, SQLHelper.COLUMN_LONGITUDE,
			SQLHelper.COLUMN_LATITUDE, SQLHelper.COLUMN_SOFTWARE,
			SQLHelper.COLUMN_NON_ACCESSIBILITY, SQLHelper.COLUMN_SETUP_TIME };

	public ReportDataSource(Context context) {
		dbHelper = new SQLHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	/*
	 * This function is used to generates an entry in the database
	 */
	public void newReport(QoSData newData) {
		ContentValues values = new ContentValues();
		values.put(SQLHelper.COLUMN_IMSI, newData.imsi);
		values.put(SQLHelper.COLUMN_OPERATOR, newData.operatorName);
		values.put(SQLHelper.COLUMN_NETWORK_TYPE, Integer.toString(newData.networkTYPE));
		values.put(SQLHelper.COLUMN_MCC, newData.mcc);
		values.put(SQLHelper.COLUMN_PHONE_NUMBER, newData.phoneNumber);
		values.put(SQLHelper.COLUMN_MANUFACTURER, newData.phoneManufacturer);
		values.put(SQLHelper.COLUMN_MODEL, newData.phoneModel);
		values.put(SQLHelper.COLUMN_SIGNAL_STRENGHT, Integer.toString(newData.signalStrength));
		values.put(SQLHelper.COLUMN_PHONE_TYPE, Integer.toString(newData.phoneType));
		values.put(SQLHelper.COLUMN_DATE_TIME, newData.dateTime);
		values.put(SQLHelper.COLUMN_ROAMING, String.valueOf(newData.roaming));
		values.put(SQLHelper.COLUMN_BER, Integer.toString(newData.error));
		values.put(SQLHelper.COLUMN_SUCCESSFUL_CALL, String.valueOf(newData.callSuccessful));
		values.put(SQLHelper.COLUMN_DROPPED_CALL, String.valueOf(newData.callDropped));
		values.put(SQLHelper.COLUMN_SMS_DELIVERY_TIME, newData.eteDeliveryTime);
		values.put(SQLHelper.COLUMN_SMS_DELIVERY, Integer.toString(newData.smsDelivery));
		values.put(SQLHelper.COLUMN_SMS_INTEGRITY, String.valueOf(newData.smsIntegrity));
		values.put(SQLHelper.COLUMN_MEAN_DATA_RATE, Integer.toString(newData.meanDAta));
		values.put(SQLHelper.COLUMN_NON_ACCESSIBILITY, Integer.toString(newData.usuccessfulAttemp));
		values.put(SQLHelper.COLUMN_LAC, String.valueOf(newData.lac));
		values.put(SQLHelper.COLUMN_PSC, String.valueOf(newData.psc));
		values.put(SQLHelper.COLUMN_CELL_ID, String.valueOf(newData.cellID));
		values.put(SQLHelper.COLUMN_LONGITUDE, String.valueOf(newData.longitud));
		values.put(SQLHelper.COLUMN_LATITUDE, String.valueOf(newData.latitude));
		values.put(SQLHelper.COLUMN_MANUFACTURER, newData.phoneManufacturer);
		values.put(SQLHelper.COLUMN_MODEL, newData.phoneModel);
		values.put(SQLHelper.COLUMN_SOFTWARE, newData.build);
		values.put(SQLHelper.COLUMN_SETUP_TIME, Integer.toString(newData.setupTime));

		database.insert(SQLHelper.TABLE_REPORT, null, values);
	}
	/* NOT SURE IF I'LL USE THIS!!!
	public List<QoSData> getAllProducts() {
		List<QoSData> reports = new ArrayList<QoSData>();

		Cursor cursor = database.query(SQLHelper.TABLE_REPORT, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			QoSData comment = cursorToReport(cursor);
			reports.add(comment);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return reports;
	}
	
	private QoSData cursorToReport(Cursor cursor) {
		QoSData report = new QoSData();
		
		report.imsi=cursor.getString(0);
		report.operatorName=cursor.getString(1);
		report.networkTYPE=cursor.getInt(2);
		report.mcc=cursor.getString(3);
		report.phoneNumber=cursor.getString(4);
		report.phoneManufacturer=cursor.getString(5);
		report.phoneModel=cursor.getString(6);
		report.signalStrength=cursor.getInt(7);
		report.phoneType=cursor.getInt(8);
		report.dateTime=cursor.getString(9);
		report.roaming=Boolean.valueOf(cursor.getString(10));
		report.error=cursor.getInt(11);
		report.callSuccessful=Boolean.valueOf(cursor.getString(12));
		report.callDropped=Boolean.valueOf(cursor.getString(13));
		report.eteDeliveryTime=cursor.getInt(14);
		report.smsDelivery=cursor.getInt(15);
		report.smsIntegrity=Boolean.valueOf(cursor.getString(16));
		report.meanDAta=cursor.getInt(17);
		report.usuccessfulAttemp=cursor.getInt(18);
		report.setupTime=cursor.getInt(19);
		
		return report;
	}
	*/
}
