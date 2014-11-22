package com.hagtrop.zagadki;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Log;

public class MyCursorLoader extends CursorLoader{
	public static final int SIMPLE_GAME_QUES = 0;
	public static final int QUE_AND_ANSWER = 1;
	public static final int VARIANTS = 2;
	public static final int TEST_GAME_QUES = 3;
	public static final int SIMPLE_TIMER_GAME_QUES = 4;
	private int queryIndex;
	
	private static final String SIMPLE_GAME_TABLE = "simple_game";
	private static final String SIMPLE_TIMER_GAME_TABLE = "simple_timer_game";
	private static final String TEST_GAME_TABLE = "test_game";
	
	private static final String GET_QA_SQL = 
			"SELECT questions.question, questions.level, answers.answer " +
			"FROM questions " +
			"INNER JOIN answers " +
			"ON questions.answer_id=answers._id " +
			"WHERE questions._id=?";
	
	private static final String GET_VARIANTS_SQL = 
			"SELECT answers.answer FROM answers " +
			"INNER JOIN variants_matching " +
			"ON answers._id=variants_matching.variant_id " +
			"WHERE variants_matching.question_id=?";
	
	private SQLiteDatabase database;
	private int questionId;
	
	public MyCursorLoader(Context context, SQLiteDatabase database, int queryIndex, Bundle params){
		super(context);
		this.database = database;
		this.queryIndex = queryIndex;
		switch(queryIndex){
		case SIMPLE_GAME_QUES:
			break;
		case SIMPLE_TIMER_GAME_QUES:
			break;
		case TEST_GAME_QUES:
			break;
		case QUE_AND_ANSWER:
			this.questionId = params.getInt("questionId");
			break;
		case VARIANTS:
			this.questionId = params.getInt("questionId");
			break;
		default: break;
		}
	}
	
	public Cursor loadInBackground(){
		Cursor cursor = null;
		switch(queryIndex){
		case SIMPLE_GAME_QUES:
			cursor = database.query(SIMPLE_GAME_TABLE, null, null, null, null, null, null);
			break;
		case SIMPLE_TIMER_GAME_QUES:
			cursor = database.query(SIMPLE_TIMER_GAME_TABLE, null, null, null, null, null, null);
			break;
		case TEST_GAME_QUES:
			cursor = database.query(TEST_GAME_TABLE, null, null, null, null, null, null);
			break;
		case QUE_AND_ANSWER:
			cursor = database.rawQuery(GET_QA_SQL, new String[]{String.valueOf(questionId)});
			break;
		case VARIANTS:
			cursor = database.rawQuery(GET_VARIANTS_SQL, new String[]{String.valueOf(questionId)});
			break;
		default: break;
		}
		
		return cursor;
	}

}
