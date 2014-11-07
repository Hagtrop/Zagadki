package com.hagtrop.zagadki;

import java.util.ArrayList;
import java.util.Locale;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TestGame extends FragmentActivity implements OnClickListener, LoaderCallbacks<Cursor>{
	TextView progressTV, levelTV, attemptsTV, questionTV, answerTV;
	
	ArrayList<Button> answerBtns;
	
	private static final int ARRAY_LOADER = 0;
	private static final int QUESTION_LOADER = 1;
	private static final int VARIANTS_LOADER = 2;
	private BaseHelper baseHelper;
	private SQLiteDatabase database;
	private ArrayList<QueStatus> queStatusList;
	private int currentQueIndex = 0;
	private Question currentQuestion;
	private String[] variants;
	private int buttonsPressed = 0;
	private int maxAttemptsCount;
	private int attemptsMade;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a2_test_game);
		
		progressTV = (TextView) findViewById(R.id.a2_progressTV);
		levelTV = (TextView) findViewById(R.id.a2_levelTV);
		attemptsTV = (TextView) findViewById(R.id.a2_attemptsTV);
		questionTV = (TextView) findViewById(R.id.a2_questionTV);
		answerTV = (TextView) findViewById(R.id.a2_answerTV);
		
		answerBtns = new ArrayList<Button>();
		answerBtns.add((Button) findViewById(R.id.a2_answerBtn1));
		answerBtns.add((Button) findViewById(R.id.a2_answerBtn2));
		answerBtns.add((Button) findViewById(R.id.a2_answerBtn3));
		answerBtns.add((Button) findViewById(R.id.a2_answerBtn4));
		answerBtns.add((Button) findViewById(R.id.a2_answerBtn5));
		answerBtns.add((Button) findViewById(R.id.a2_answerBtn6));
		for(Button btn : answerBtns){
			btn.setOnClickListener(new AnswerBtnsOnClickListener());
		}
		
		baseHelper = BaseHelper.getInstance(this);
		
		//Если игра сохранена - загружаем, если нет - создаём новую
		if(!baseHelper.testGameExists()){
			baseHelper.newTestGame();
			Log.d("mLog", "STEP: getSupportLoaderManager().initLoader(ARRAY_LOADER, null, this)");	
		}
		database = baseHelper.getWritableDatabase();
		getSupportLoaderManager().initLoader(ARRAY_LOADER, null, this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	private class AnswerBtnsOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			btn.setEnabled(false);
			buttonsPressed++;
			if(btn.getText().equals(currentQuestion.getAnswer())){
				baseHelper.updateTestGame(queStatusList.get(currentQueIndex).getId(), buttonsPressed, 1);
				
				if(currentQueIndex < queStatusList.size()-1){
					currentQueIndex++;
					loadQuestion(queStatusList.get(currentQueIndex).getId());
					Log.d("mLog", "currentQueIndex=" + currentQueIndex);
				}
			}
			else{
				baseHelper.updateTestGame(queStatusList.get(currentQueIndex).getId(), buttonsPressed, 0);
			}
		}
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		Log.d("mLog", "onCreateLoader");
		Bundle params;
		switch(loaderID){
		case ARRAY_LOADER:
			return new MyCursorLoader(this, database, MyCursorLoader.TEST_GAME_QUES, null);
		case QUESTION_LOADER:
			Log.d("mLog", "onCreateLoader, queId=" + bundle.getInt("queId"));
			params = new Bundle();
			params.putInt("questionId", bundle.getInt("queId"));
			return new MyCursorLoader(this, database, MyCursorLoader.QUE_AND_ANSWER, params);
		case VARIANTS_LOADER:
			params = new Bundle();
			params.putInt("questionId", bundle.getInt("queId"));
			return new MyCursorLoader(this, database, MyCursorLoader.VARIANTS, params);
		default: return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch(loader.getId()){
		case ARRAY_LOADER:
			if(cursor.moveToFirst()){
				queStatusList = new ArrayList<QueStatus>();
				int queAttempts;
				do{
					queAttempts = cursor.getInt(cursor.getColumnIndex("attempts"));
					attemptsMade += queAttempts;
					queStatusList.add(new QueStatus(
							cursor.getInt(cursor.getColumnIndex("question_id")), 
							cursor.getInt(cursor.getColumnIndex("status")), 
							queAttempts));
					
				} while(cursor.moveToNext());
				maxAttemptsCount = queStatusList.size()*2;
				attemptsTV.setText(getString(R.string.a2_attemptsTV) + " " + (maxAttemptsCount-attemptsMade));
			}
			while(currentQueIndex < queStatusList.size() && queStatusList.get(currentQueIndex).getStatus() != 0){
				currentQueIndex++;
			}
			//printArray(queStatusList);
			loadQuestion(queStatusList.get(currentQueIndex).getId());
			break;
		case QUESTION_LOADER:
			if(cursor.moveToFirst()){
				currentQuestion = new Question(
						cursor.getString(cursor.getColumnIndex("question")).replace("\\n", "\n"),
						cursor.getString(cursor.getColumnIndex("answer")).trim().toUpperCase(new Locale("ru")),
						cursor.getInt(cursor.getColumnIndex("level")));
				
				Log.d("mLog", currentQuestion.getQuestion());
				Log.d("mLog", currentQuestion.getAnswer());
				progressTV.setText(getString(R.string.a1_progressTV) + " " + (currentQueIndex+1) + "/" + queStatusList.size());
				levelTV.setText(getString(R.string.a1_levelTV) + " " + currentQuestion.getLevel());
				questionTV.setText(currentQuestion.getQuestion());
				answerTV.setText(currentQuestion.getAnswer());
				Log.d("mLog", "QUESTION_LOADER");
				
				break;
			}
		case VARIANTS_LOADER:
			if(cursor.moveToFirst()){
				variants = new String[6];
				int i = 0;
				do{
					variants[i] = cursor.getString(cursor.getColumnIndex("answer")).toUpperCase(new Locale("ru"));
					i++;
				}
				while(cursor.moveToNext());
				ArrayShuffle.reshuffle(variants);
				//Выводим слова на кнопки
				for(int j=0; j<answerBtns.size(); j++){
					answerBtns.get(j).setEnabled(true);
					answerBtns.get(j).setText(variants[j]);
				}
			}
			break;
		default: break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}
	
	//Асинхронно загружаем вопрос, используя LoaderManager
		private void loadQuestion(int queId){
			Bundle bundle = new Bundle();
			bundle.putInt("queId", queId);
			getSupportLoaderManager().restartLoader(QUESTION_LOADER, bundle, this);
			getSupportLoaderManager().restartLoader(VARIANTS_LOADER, bundle, this);
		}
}
