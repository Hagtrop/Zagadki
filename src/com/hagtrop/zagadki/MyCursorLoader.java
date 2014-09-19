package com.hagtrop.zagadki;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;

public class MyCursorLoader extends CursorLoader{
	public static final int QUES_PARAMS = 0;
	public static final int QUE_AND_ANSWER = 1;
	private int queryIndex;
	
	private static final String QUESTIONS_TABLE = "questions";
	private static final String QUESTION_COLUMN = "questions.question";
	private static final String QUESTION_ID_COLUMN = "questions._id";
	private static final String QUESTION_LEVEL_COLUMN = "questions.level";
	private static final String QANSWER_ID_COLUMN = "questions.answer_id";
	
	private static final String ANSWERS_TABLE = "answers";
	private static final String ANSWER_COLUMN = "answers.answer";
	private static final String ANSWER_ID_COLUMN = "answers._id";
	
	private static final String QA_SQL = "SELECT questions.question, answers.answer FROM questions INNER JOIN answers ON questions.answer_id=answers._id WHERE questions._id=?";
	
	private SQLiteDatabase database;
	private int questionId;

	/*public MyCursorLoader(Context context, SQLiteDatabase database, String tableName, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
		super(context);
		// TODO Auto-generated constructor stub
		this.database = database;
		this.tableName = tableName;
		this.columns = columns;
		this.selection = selection;
		this.selectionArgs = selectionArgs;
		this.sortOrder = sortOrder;
	}*/
	
	public MyCursorLoader(Context context, SQLiteDatabase database){
		super(context);
		queryIndex = QUES_PARAMS;
		this.database = database;
	}
	
	public MyCursorLoader(Context context, SQLiteDatabase database, int queId){
		super(context);
		queryIndex = QUE_AND_ANSWER;
		this.database = database;
		this.questionId = queId;
	}
	
	public Cursor loadInBackground(){
		Cursor cursor = null;
		switch(queryIndex){
		case QUES_PARAMS:
			cursor = database.query(QUESTIONS_TABLE, new String[]{QUESTION_ID_COLUMN, QUESTION_LEVEL_COLUMN, QANSWER_ID_COLUMN}, null, null, null, null, null);
			break;
		case QUE_AND_ANSWER:
			cursor = database.rawQuery(QA_SQL, new String[]{String.valueOf(questionId)});
			break;
		default: break;
		}
		
		return cursor;
	}

}
