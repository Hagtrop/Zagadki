package com.hagtrop.zagadki;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

public class MyCursorLoader extends CursorLoader{

	public MyCursorLoader(Context context, String tableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}
	
	public Cursor loadInBackground(){
		
		return null;
	}

}
