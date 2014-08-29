package com.hagtrop.zagadki;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {
	Button simpleBtn, variantsBtn, countdownBtn, sandsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        simpleBtn = (Button) findViewById(R.id.a0_simpleBtn);
        variantsBtn = (Button) findViewById(R.id.a0_variantsBtn);
        countdownBtn = (Button) findViewById(R.id.a0_countdownBtn);
        sandsBtn = (Button) findViewById(R.id.a0_sandsBtn);
        
        BaseHelper bh = new BaseHelper(this);
        bh.printData();
    }
}
