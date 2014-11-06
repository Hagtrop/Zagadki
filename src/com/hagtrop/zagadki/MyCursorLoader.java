package com.hagtrop.zagadki;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;

public class MyCursorLoader extends CursorLoader{
	public static final int SIMPLE_GAME_QUES = 0;
	public static final int QUE_AND_ANSWER = 1;
	public static final int VARIANTS = 2;
	public static final int TEST_GAME_QUES = 3;
	private int queryIndex;
	
	
	
	private static final String QUESTIONS_TABLE = "questions";
	private static final String QUESTION_COLUMN = "questions.question";
	private static final String QUESTION_ID_COLUMN = "questions._id";
	private static final String QUESTION_LEVEL_COLUMN = "questions.level";
	private static final String QANSWER_ID_COLUMN = "questions.answer_id";
	
	private static final String ANSWERS_TABLE = "answers";
	private static final String ANSWER_COLUMN = "answers.answer";
	private static final String ANSWER_ID_COLUMN = "answers._id";
	
	static final String SIMPLE_GAME_TABLE = "simple_game";
	private static final String SIMPLE_GAME_ID_COLUMN = "simple_game.question_id";
	private static final String SIMPLE_GAME_STATUS_COLUMN = "simple_game.status";
	
	private static final String TEST_GAME_TABLE = "test_game";
	
	private static final String GET_QA_SQL = "SELECT questions.question, questions.level, answers.answer FROM questions INNER JOIN answers ON questions.answer_id=answers._id WHERE questions._id=?";
	private static final String GET_VARIANTS_SQL = 
			"SELECT answers.answer FROM answers " +
			"INNER JOIN variants_matching " +
			"ON answers._id=variants_matching.variant_id " +
			"WHERE variants_matching.question_id=?";
	
	private SQLiteDatabase database;
	private int questionId;
	
	/*public MyCursorLoader(Context context, SQLiteDatabase database){
		super(context);
		queryIndex = SORTED_QUES;
		this.database = database;
	}
	
	public MyCursorLoader(Context context, SQLiteDatabase database, int queId){
		super(context);
		queryIndex = QUE_AND_ANSWER;
		this.database = database;
		this.questionId = queId;
	}*/
	
	public MyCursorLoader(Context context, SQLiteDatabase database, int queryIndex, Bundle params){
		super(context);
		this.database = database;
		this.queryIndex = queryIndex;
		switch(queryIndex){
		case SIMPLE_GAME_QUES:
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
