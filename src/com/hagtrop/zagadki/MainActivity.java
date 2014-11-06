package com.hagtrop.zagadki;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{
	Button simpleBtn, variantsBtn, countdownBtn, sandsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        simpleBtn = (Button) findViewById(R.id.a0_simpleBtn);
        simpleBtn.setOnClickListener(this);
        
        variantsBtn = (Button) findViewById(R.id.a0_variantsBtn);
        variantsBtn.setOnClickListener(this);
        
        countdownBtn = (Button) findViewById(R.id.true_dialog);
        countdownBtn.setOnClickListener(this);
        
        sandsBtn = (Button) findViewById(R.id.a0_sandsBtn);
        sandsBtn.setOnClickListener(this);
        
        BaseHelper bh = BaseHelper.getInstance(this);
        bh.printData();
        SQLiteDatabase db = bh.getWritableDatabase();
        Cursor cursor = db.query("questions", null, null, null, null, null, null);
        cursor.moveToFirst();
        String question = cursor.getString(1);
        Log.d("myLog", "Question: " + question);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.a0_simpleBtn:
			startActivity(new Intent(this, SimpleGame.class));
			break;
		case R.id.a0_variantsBtn:
			startActivity(new Intent(this, TestGame.class));
			break;
		case R.id.true_dialog:
			break;
		case R.id.a0_sandsBtn:
			break;
		default: break;
		}
	}
}
