package com.hagtrop.zagadki;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BaseHelper extends SQLiteOpenHelper {

	private Context mContext;
	private static final String BASE_NAME = "zagadkiDB";
	private static File BASE_FILE;
	private static BaseHelper bhInstance;
	private boolean simpleGameExists = false;
	private boolean testGameExists = false;
	
	synchronized static public BaseHelper getInstance(Context context){
		Log.d("mLog", "BaseHelper getInstance");
		if(bhInstance == null) bhInstance = new BaseHelper(context.getApplicationContext());
		return bhInstance;
	}
	
	private BaseHelper(Context context) {
		super(context, BASE_NAME, null, 1);
		Log.d("mLog", "private BaseHelper");
		mContext = context;
		SQLiteDatabase database = null;
		try{
			database = getReadableDatabase();
			if(database != null) database.close();
			BASE_FILE = context.getDatabasePath(BASE_NAME);
			copyBaseFromAssets();
		}
		catch (SQLiteException e){Log.d("mLog", e.toString());}
		finally{
			if(database != null && database.isOpen()) database.close();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public void deleteSimpleGame(){
		simpleGameExists = false;
	}
	
	public void newSimpleGame(){
		String deleteQuery, createQuery;
		deleteQuery = "DROP TABLE IF EXISTS simple_game";
		createQuery = "CREATE TABLE simple_game(question_id INTEGER, status INTEGER DEFAULT 0)";
		SQLiteDatabase database = getWritableDatabase();
		database.execSQL(deleteQuery);
		database.execSQL(createQuery);
		
		//��������� ������ �� ������� ��������, ��������� ������� � ��������� ��������������� ������� ������� simple_game
		Cursor cursor = database.query("questions", new String[]{"questions._id", "questions.level", "questions.answer_id"}, null, null, null, null, null);
		if(cursor.moveToFirst()){
			ArrayList<QueParams> quesParams = new ArrayList<QueParams>();
			int queId, queLevel, answerId;
			do{
				queId = cursor.getInt(cursor.getColumnIndex("_id"));
		        queLevel = cursor.getInt(cursor.getColumnIndex("level"));
		        answerId = cursor.getInt(cursor.getColumnIndex("answer_id"));
		        quesParams.add(new QueParams(queId, queLevel, answerId));
			} while(cursor.moveToNext());
			Collections.sort(quesParams);
			ContentValues cv;
			database.beginTransaction();
			try{
				for(QueParams params : quesParams){
					cv = new ContentValues();
					cv.put("question_id", params.queId);
					database.insert("simple_game", null, cv);
				}
				database.setTransactionSuccessful();
			}
			finally{
				database.endTransaction();
			}
			
		}
		database.close();
		simpleGameExists = true;
	}
	
	public void newTestGame(){
		String deleteQuery, createQuery;
		deleteQuery = "DROP TABLE IF EXISTS test_game";
		createQuery = "CREATE TABLE test_game(question_id INTEGER, status INTEGER DEFAULT 0)";
		SQLiteDatabase database = getWritableDatabase();
		database.execSQL(deleteQuery);
		database.execSQL(createQuery);
		
		//��������� ������ �� ������� ��������, ��������� ������� � ��������� ��������������� ������� ������� simple_game
		Cursor cursor = database.query("questions", new String[]{"questions._id", "questions.level", "questions.answer_id"}, null, null, null, null, null);
		if(cursor.moveToFirst()){
			ArrayList<QueParams> quesParams = new ArrayList<QueParams>();
			int queId, queLevel, answerId;
			do{
				queId = cursor.getInt(cursor.getColumnIndex("_id"));
		        queLevel = cursor.getInt(cursor.getColumnIndex("level"));
		        answerId = cursor.getInt(cursor.getColumnIndex("answer_id"));
		        quesParams.add(new QueParams(queId, queLevel, answerId));
			} while(cursor.moveToNext());
			Collections.sort(quesParams);
			ContentValues cv;
			database.beginTransaction();
			try{
				for(QueParams params : quesParams){
					cv = new ContentValues();
					cv.put("question_id", params.queId);
					database.insert("test_game", null, cv);
				}
				database.setTransactionSuccessful();
			}
			finally{
				database.endTransaction();
			}
			
		}
		database.close();
		testGameExists = true;
	}
	
	
	
	public boolean simpleGameExists(){
		return simpleGameExists;
	}
	
	public boolean testGameExists(){
		return testGameExists;
	}
	
	public void printData(){
		Log.d("mLog", "BaseName=" + getDatabaseName());
		Log.d("mLog", "BaseFile=" + mContext.getDatabasePath(getDatabaseName()));
	}
	
	private void copyBaseFromAssets(){
		Log.d("mLog", "copyBaseFromAssets()");
		AssetManager assetManager = mContext.getResources().getAssets();
		InputStream input = null;
	    OutputStream output = null;
	    try{
	    	input = assetManager.open(BASE_NAME);
	    	output = new FileOutputStream(BASE_FILE);
	    	byte[] buffer = new byte[1024];
	        int read = 0;
	        while ((read = input.read(buffer)) != -1) {
	            output.write(buffer, 0, read);
	        }
	        Log.d("mLog", "copyBaseFromAssets: try read");
	    } catch(IOException e){Log.d("mLog", "copyBaseFromAssets: Read error " + e.toString());}
	    finally{
	    	if(input != null)
	    		try{
	    			input.close();
	    		} catch (IOException e){Log.d("mLog", e.toString());}
	    	if(output != null)
	    		try{
	    			output.close();
	    		} catch (IOException e){Log.d("mLog", e.toString());}
	    }
	}
	
	void updateQueStatus(int queId){
		SQLiteDatabase database = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("status", 1);
		database.update("simple_game", cv, "question_id=?", new String[]{String.valueOf(queId)});
	}

}
