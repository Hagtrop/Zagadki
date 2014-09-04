package com.hagtrop.zagadki;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
        SQLiteDatabase db = bh.getWritableDatabase();
        Cursor cursor = db.query("questions", null, null, null, null, null, null);
        cursor.moveToFirst();
        String question = cursor.getString(1);
        Log.d("myLog", "Question: " + question);
    }
}
