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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleGame extends FragmentActivity implements LoaderCallbacks<Cursor>, OnClickListener {
	TextView questionTV, answerTV;
	Button nextBtn;
	LinearLayout answerLayout;
	
	private static final int ARRAY_LOADER = 0;
	private static final int QUESTION_LOADER = 1;
	private ArrayList<QueParams> quesParams;
	private SQLiteDatabase database;
	private int currentQueIndex = 0;
	private ArrayList<Button> answerBtns;
	private String question, answer;
	char[] answerLetters;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a1_simple_game);
		
		questionTV = (TextView) findViewById(R.id.a1_questionTV);
		answerTV = (TextView) findViewById(R.id.a1_answerTV);
		nextBtn = (Button) findViewById(R.id.a1_nextBtn);
		nextBtn.setOnClickListener(this);
		answerLayout = (LinearLayout) findViewById(R.id.a1_answerLayout);
		
		//Создаём кнопку со своим стилем
		//Button btnA = new Button(this, null, R.style.AnswerLetterBtn);
		
		
				
		BaseHelper baseHelper = BaseHelper.getInstance(this);
		
		//Если игра сохранена - загружаем, если нет - создаём новую
		if(baseHelper.simpleGameExists()){
			//
		}
		else{
			baseHelper.newSimpleGame();
			Log.d("mLog", "STEP: getSupportLoaderManager().initLoader(ARRAY_LOADER, null, this)");	
		}
		database = baseHelper.getWritableDatabase();
		getSupportLoaderManager().initLoader(ARRAY_LOADER, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		// TODO Auto-generated method stub
		Log.d("mLog", "onCreateLoader");
		switch(loaderID){
		case ARRAY_LOADER:
			return new MyCursorLoader(this, database);
		case QUESTION_LOADER:
			Log.d("mLog", "onCreateLoader, queId=" + bundle.getInt("queId"));
			return new MyCursorLoader(this, database, bundle.getInt("queId"));
		default: return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch(loader.getId()){
		//Сохраняем в ArrayList параметры и характеристики вопросов для дальнейшей сортировки
		case ARRAY_LOADER:
			if(cursor.moveToFirst()){
				quesParams = new ArrayList<QueParams>();
				int queId, queLevel, answerId;
				do{
					queId = cursor.getInt(cursor.getColumnIndex("_id"));
			        queLevel = cursor.getInt(cursor.getColumnIndex("level"));
			        answerId = cursor.getInt(cursor.getColumnIndex("answer_id"));
			        quesParams.add(new QueParams(queId, queLevel, answerId));
				} while(cursor.moveToNext());
			}
			printArray(quesParams);
			loadQuestion(quesParams.get(0).queId);
			break;
		//Извлекаем вопрос и ответ
		case QUESTION_LOADER:
			if(cursor.moveToFirst()){
				answerBtns = new ArrayList<Button>();
				question = cursor.getString(cursor.getColumnIndex("question")).replace("\\n", "\n");
				answer = cursor.getString(cursor.getColumnIndex("answer"));
				answer = answer.trim();
				Log.d("mLog", question);
				Log.d("mLog", answer);
				questionTV.setText(question);
				answerTV.setText(answer);
				Log.d("mLog", "QUESTION_LOADER");
				answerLetters = answer.toCharArray();
				answerLayout.removeAllViews();
				answerBtns.clear();
				for(int i=0; i<answerLetters.length; i++){
					Button letterBtn = new Button(this);
					letterBtn.setMinimumWidth(10);
					letterBtn.setWidth(10);
					letterBtn.setMaxWidth(10);
					answerLayout.addView(letterBtn);
					answerBtns.add(letterBtn);
				}
			}
		default: break;
		}
	}
	
	private void loadQuestion(int queId){
		Bundle bundle = new Bundle();
		bundle.putInt("queId", queId);
		getSupportLoaderManager().restartLoader(QUESTION_LOADER, bundle, this);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void printArray(ArrayList<QueParams> mArray){
		for(QueParams mParams : mArray){
			Log.d("mLog", "queId=" + mParams.queId + " queLevel=" + mParams.queLevel + " answerId=" + mParams.answerId);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(currentQueIndex < quesParams.size()-1){
			currentQueIndex++;
			loadQuestion(quesParams.get(currentQueIndex).queId);
			Log.d("mLog", "currentQueIndex=" + currentQueIndex);
		}
	}
}

class QueParams{
	public final int queId;
	public final int queLevel;
	public final int answerId;
	
	public QueParams(int queId, int queLevel, int answerId){
		this.queId = queId;
		this.queLevel = queLevel;
		this.answerId = answerId;
	}
}
