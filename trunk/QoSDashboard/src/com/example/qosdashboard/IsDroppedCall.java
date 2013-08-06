package com.example.qosdashboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class IsDroppedCall extends Activity implements OnClickListener {
	public Button iEndedIt,wrongNumber, callDropped;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_end);
 
        iEndedIt = (Button) findViewById(R.id.iEndedIt);
        iEndedIt.setOnClickListener((android.view.View.OnClickListener) this);
		
        wrongNumber = (Button) findViewById(R.id.wrongNumber);
        wrongNumber.setOnClickListener((android.view.View.OnClickListener) this);
        
        callDropped = (Button) findViewById(R.id.callDropped);
        callDropped.setOnClickListener((android.view.View.OnClickListener) this);
		
    }
    
    @Override
	public void onClick(View v) {
    	
    	this.finish();
		/*if (v.getId() == R.id.yes) {
			this.finish();
		}
		if (v.getId() == R.id.no) {
			this.finish();
		}*/


	}
}
