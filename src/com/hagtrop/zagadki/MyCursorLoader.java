package com.hagtrop.zagadki;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.CursorLoader;

public class MyCursorLoader extends CursorLoader{
	private SQLiteDatabase database;
	private String tableName;
	private String[] columns;
	private String selection;
	private String[] selectionArgs;
	private String sortOrder;

	public MyCursorLoader(Context context, SQLiteDatabase database, String tableName, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
		super(context);
		// TODO Auto-generated constructor stub
		this.database = database;
		this.tableName = tableName;
		this.columns = columns;
		this.selection = selection;
		this.selectionArgs = selectionArgs;
		this.sortOrder = sortOrder;
	}
	
	public Cursor loadInBackground(){
		Cursor cursor = database.query(tableName, columns, selection, selectionArgs, null, null, sortOrder);
		return cursor;
	}

}
