package com.example.maskededit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MaskedEditText m = (MaskedEditText)findViewById(R.id.maskEdit);
        
        TextView txt = (TextView)findViewById(R.id.textView);
        txt.setText("Mask: " + m.getMask());
        
        Button b1 = (Button)findViewById(R.id.button1);
        Button b2 = (Button)findViewById(R.id.button2);
        
        b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "Text without mask: " + m.getText(true), Toast.LENGTH_LONG).show();
			}
		});
        
        b2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "Text with mask: " + m.getText(false), Toast.LENGTH_LONG).show();
			}
		});
    }  
}
