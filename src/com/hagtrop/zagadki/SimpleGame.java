package com.hagtrop.zagadki;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.TextView;

public class SimpleGame extends FragmentActivity implements LoaderCallbacks<Cursor> {
	TextView questionTV;
	private static final Uri QUESTIONS_URI = Uri.parse("content://com.hagtrop.zagadki.zagadkiDB/questions");
	private static final int ARRAY_LOADER = 0;
	private ArrayList<QueParams> quesOrder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a1_simple_game);
		
		questionTV = (TextView) findViewById(R.id.a1_questionTV);
				
		BaseHelper baseHelper = BaseHelper.getInstance(this);
		
		//Если игра сохранена - загружаем, если нет - создаём новую
		if(baseHelper.simpleGameExists()){
			//
		}
		else{
			baseHelper.newSimpleGame();
			Log.d("mLog", "STEP: getSupportLoaderManager().initLoader(ARRAY_LOADER, null, this)");
			getSupportLoaderManager().initLoader(ARRAY_LOADER, null, this);
		}
		
		/*BaseHelper bh = BaseHelper.getInstance(this);
        SQLiteDatabase db = bh.getWritableDatabase();
        Cursor cursor = db.query("questions", null, null, null, null, null, null);
        cursor.moveToFirst();
        String question = cursor.getString(1);
        question = question.replace("\\n", System.getProperty("line.separator"));
        cursor.close();
        db.close();
        questionTV.setText(question);*/
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		// TODO Auto-generated method stub
		switch(loaderID){
		case ARRAY_LOADER:
			Log.d("mLog", "STEP: case ARRAY_LOADER");
			return new CursorLoader(this, QUESTIONS_URI, new String[]{"_id", "level", "answer_id"}, null, null, null);
		default: return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		cursor.moveToFirst();
        String id = cursor.getString(0);
        String level = cursor.getString(1);
        String answerId = cursor.getString(2);
        questionTV.setText("Id=" + id);
        cursor.close();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}

class QueParams{
	public final int queId;
	public final int queLvel;
	public final int answerId;
	
	public QueParams(int queId, int queLvel, int answerId){
		this.queId = queId;
		this.queLvel = queLvel;
		this.answerId = answerId;
	}
}
