package com.example.qosdashboard;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyCustomBaseAdapter extends BaseAdapter {
	private static ArrayList<MetricField> searchArrayList;

	private LayoutInflater mInflater;

	public MyCustomBaseAdapter(Context context, ArrayList<MetricField> metrics) {
		searchArrayList = metrics;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return searchArrayList.size();
	}

	public Object getItem(int position) {
		return searchArrayList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		MetricField obj = searchArrayList.get(position);
		holder = new ViewHolder();
		if (convertView == null) {


			/***************GET THE RESOURCES FROM THE LAYOUT*******************/
			if(obj.getClass().equals(NetworkFields.class)){
				convertView = mInflater.inflate(R.layout.custom_row_network_info, null);
				holder.field6 = (TextView) convertView.findViewById(R.id.Operator_name);
				holder.field7 = (TextView) convertView.findViewById(R.id.Phone_Type);
				holder.field8 = (TextView) convertView.findViewById(R.id.Roaming);
				holder.field1 = (TextView) convertView.findViewById(R.id.name_network);
				holder.field2 = (TextView) convertView.findViewById(R.id.MCC);
				holder.field3 = (TextView) convertView.findViewById(R.id.MNC);
				holder.field4 = (TextView) convertView.findViewById(R.id.SignalStrenght);//this needs to be solved, just being initialized
				holder.field5 = (TextView) convertView.findViewById(R.id.Network_Type);
			}

			if(obj.getClass().equals(SubscriberInfo.class)){
				convertView = mInflater.inflate(R.layout.custom_row_subscriber_info, null);
				holder.field1 = (TextView) convertView.findViewById(R.id.name_subscriber);
				holder.field2 = (TextView) convertView.findViewById(R.id.IMSI);
				holder.field3 = (TextView) convertView.findViewById(R.id.Telephone_number);
				holder.field4 = (TextView) convertView.findViewById(R.id.cellID);
				holder.field5 = (TextView) convertView.findViewById(R.id.LAC);
				holder.field6 = (TextView) convertView.findViewById(R.id.PSC);
				holder.field7 = (TextView) convertView.findViewById(R.id.errorDb);
				holder.field8 = (TextView) convertView.findViewById(R.id.Longitude);
				holder.field9 = (TextView) convertView.findViewById(R.id.Latitude);
			}

			if(obj.getClass().equals(TelephonyService.class)){
				convertView = mInflater.inflate(R.layout.custom_row_telephony_info, null);
				holder.field1 = (TextView) convertView.findViewById(R.id.name_telephony);
				holder.field2 = (TextView) convertView.findViewById(R.id.serviceAccessibility);
				holder.field3 = (TextView) convertView.findViewById(R.id.cutOffCallRatio);
			}
			if(obj.getClass().equals(SMSService.class)){
				convertView = mInflater.inflate(R.layout.custom_row_sms_info, null);
				holder.field1 = (TextView) convertView.findViewById(R.id.name_sms);
				holder.field2 = (TextView) convertView.findViewById(R.id.accessDelay);
				holder.field3 = (TextView) convertView.findViewById(R.id.completionFailureRatio);
				holder.field4 = (TextView) convertView.findViewById(R.id.eteDeliveryTime);
				
			}

			if(obj.getClass().equals(DataService.class)){
				convertView = mInflater.inflate(R.layout.custom_row_data_info, null);
				holder.field1 = (TextView) convertView.findViewById(R.id.name_data);
				holder.field2 = (TextView) convertView.findViewById(R.id.serviceNonAccessibility);
				holder.field3 = (TextView) convertView.findViewById(R.id.SetupTime);
				holder.field4 = (TextView) convertView.findViewById(R.id.MeanDataRate);

			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/*****************SET THE VALUES***********************/

		/*********Network Info*******/
		if(obj.getClass().equals(NetworkFields.class)){
			holder.field1.setText(obj.getName());
			holder.field2.setText("MCC: "+((NetworkFields) obj).mcc);
			holder.field3.setText("MNC: " +((NetworkFields) obj).getMNC());
			holder.field4.setText("Signal Strenght: " +((NetworkFields) obj).getSignalStrength());
			holder.field5.setText("Network Type: " +((NetworkFields) obj).networkTypeString);
			holder.field6.setText("Operator: " +((NetworkFields) obj).operatorName);
			holder.field7.setText("Phone type: " +((NetworkFields) obj).phoneType);
			holder.field8.setText("Roaming: " +((NetworkFields) obj).roaming);
		}

		/*********Subscriber Info*******/
		if(obj.getClass().equals(SubscriberInfo.class)){
			holder.field1.setText(obj.getName());
			holder.field2.setText("IMSI: "+((SubscriberInfo) obj).getIMSI());
			holder.field3.setText("Phone Numer: "+((SubscriberInfo) obj).getphoneNumber());
			holder.field4.setText("Cell ID: "+((SubscriberInfo) obj).cellID);
			holder.field5.setText("LAC: "+((SubscriberInfo) obj).lac);
			holder.field6.setText("PSC: "+((SubscriberInfo) obj).psc);
			holder.field7.setText("Error: "+((SubscriberInfo) obj).error);
			holder.field8.setText("Lon: "+((SubscriberInfo) obj).longitud);
			holder.field9.setText("Lat: "+((SubscriberInfo) obj).latitude);
		}
		
		/*********Telephony Service*******/
		if(searchArrayList.get(position).getClass().equals(TelephonyService.class)){
			holder.field1.setText(obj.getName());
			holder.field2.setText("Service Accessibility: "+((TelephonyService) obj).getServiceAccessibility());
			holder.field3.setText("Cut Off Call Ratio: "+((TelephonyService) obj).getCutOffCallRatio());
		}
		
		/*********SMS Service*******/
		if(obj.getClass().equals(SMSService.class)){
			holder.field1.setText(obj.getName());
			holder.field2.setText("Access Delay: "+((SMSService) obj).accessDelay);
			holder.field3.setText("Completion Failure Ratio:  "+((SMSService) obj).completionFailureRatio);
			holder.field4.setText("End to End delivery time: "+((SMSService) obj).eteDeliveryTime);
		}
		/*********Data Service*******/
		if(obj.getClass().equals(DataService.class)){
			holder.field1.setText(obj.getName());
			holder.field2.setText("Mean Data Rate: "+((DataService) obj).getMeanDataRate());
			holder.field3.setText("Service non Accessibility: "+((DataService) obj).getserviceNonAccessibility());
			holder.field4.setText("Setup Time: "+((DataService) obj).getSetupTime());

		}


		return convertView;
	}

	static class ViewHolder {
		TextView field1;
		TextView field2;
		TextView field3;
		TextView field4;
		TextView field5;
		TextView field6;
		TextView field7;
		TextView field8;
		TextView field9;
		TextView field10;
		

	}
}
