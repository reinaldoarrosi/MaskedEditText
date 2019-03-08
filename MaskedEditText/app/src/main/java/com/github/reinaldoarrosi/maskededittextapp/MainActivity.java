package com.github.reinaldoarrosi.maskededittextapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.github.reinaldoarrosi.maskededittext.MaskedEditText;

public class MainActivity extends AppCompatActivity {
    private int textWatcherCount = 0;
    private int textWatcherCount2 = 0;
    private TextView lblTextWatcherCount;
    private MaskedEditText txtMaskedEditText;
    private TextView lblTextWatcherCount2;
    private EditText txtEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.lblTextWatcherCount = (TextView)this.findViewById(R.id.lblTextWatcherCount);
        this.lblTextWatcherCount.setText("0");

        this.txtMaskedEditText = (MaskedEditText)this.findViewById(R.id.txtMaskedEditText);
        this.txtMaskedEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                txtMaskedEditText.setMask("9999 9999 9999 9999");
                textWatcherCount++;
                lblTextWatcherCount.setText(String.valueOf(textWatcherCount));
            }
        });

        this.lblTextWatcherCount2 = (TextView)this.findViewById(R.id.lblTextWatcherCount2);
        this.lblTextWatcherCount2.setText("0");

        this.txtEditText = (EditText)this.findViewById(R.id.txtEditText);
        this.txtEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textWatcherCount2++;
                lblTextWatcherCount2.setText(String.valueOf(textWatcherCount2));
            }
        });
    }
}
