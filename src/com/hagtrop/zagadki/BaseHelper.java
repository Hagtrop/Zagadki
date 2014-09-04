package com.hagtrop.zagadki;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BaseHelper extends SQLiteOpenHelper {

	private Context mContext;
	static final String BASE_NAME = "zagadkiDB";
	
	public BaseHelper(Context context) {
		super(context, BASE_NAME, null, 1);
		mContext = context;
		copyBaseFromAssets();
		Log.d("mLog", "Записи скопированы");
		SQLiteDatabase db = getReadableDatabase();
		db.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public void printData(){
		Log.d("mLog", "BaseName=" + getDatabaseName());
		Log.d("mLog", "BaseFile=" + mContext.getDatabasePath(getDatabaseName()));
	}
	
	private void copyBaseFromAssets(){
		AssetManager assetManager = mContext.getResources().getAssets();
		InputStream input = null;
	    OutputStream output = null;
	    try{
	    	input = assetManager.open("zagadki");
	    	output = new FileOutputStream(mContext.getDatabasePath(BASE_NAME));
	    	byte[] buffer = new byte[1024];
	        int read = 0;
	        while ((read = input.read(buffer)) != -1) {
	            output.write(buffer, 0, read);
	        }
	    } catch(IOException e){}
	    finally{
	    	if(input != null)
	    		try{
	    			input.close();
	    		} catch (IOException e){}
	    	if(output != null)
	    		try{
	    			output.close();
	    		} catch (IOException e){}
	    }
	}

}
