package com.hagtrop.zagadki;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TestGame extends FragmentActivity implements OnClickListener, LoaderCallbacks<Cursor>, NoticeDialogListener{
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
	private int attemptsRemaining;
	private long totalTimeSpent = 0;
	
	

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
		
		//Выравниваем кнопки по ширине
		answerBtns.get(5).addOnLayoutChangeListener(new OnLayoutChangeListener() {
			int w0, w1;
			boolean changed = false;
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				Log.d("mLog", "onLayoutChange");
				w0 = answerBtns.get(0).getWidth();
				w1 = answerBtns.get(1).getWidth();
				if(w1 > w0) answerBtns.get(0).setWidth(w1);
				else if(w0 > w1) answerBtns.get(1).setWidth(w0);
			}
		});
		
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
			attemptsRemaining--;
			queStatusList.get(currentQueIndex).addAttempts(1);
			attemptsTV.setText(getString(R.string.a2_attemptsTV) + " " + attemptsRemaining);
			if(btn.getText().equals(currentQuestion.getAnswer())){
				baseHelper.updateTestGame(queStatusList.get(currentQueIndex).getId(), queStatusList.get(currentQueIndex).getAttempts(), 1);
				
				if(currentQueIndex < queStatusList.size()-1 && attemptsRemaining > 0){
					currentQueIndex++;
					loadQuestion(queStatusList.get(currentQueIndex).getId());
					Log.d("mLog", "currentQueIndex=" + currentQueIndex);
				}
				else if(currentQueIndex >= queStatusList.size()-1){
					//Выводим сообщение о том, что игра закончена
					FragmentManager fManager = getSupportFragmentManager();
					GameoverDialog gameoverDialog = new GameoverDialog();
					gameoverDialog.show(fManager, "gameover_dialog");
					//выходим из метода, иначе отработает if(attemptsRemaining < 1)
					return;
				}
			}
			else{
				baseHelper.updateTestGame(queStatusList.get(currentQueIndex).getId(), queStatusList.get(currentQueIndex).getAttempts(), 0);
			}
			if(attemptsRemaining < 1){
				//Выводим сообщение о том, что попытки закончились
				FragmentManager fManager = getSupportFragmentManager();
				NoAttemptsDialog noAttemptsDialog = new NoAttemptsDialog();
				noAttemptsDialog.show(fManager, "no_attempts_dialog");
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
				long queTimeSpent;
				int attemptsMade = 0;
				do{
					queAttempts = cursor.getInt(cursor.getColumnIndex("attempts"));
					attemptsMade += queAttempts;
					queTimeSpent = cursor.getLong(cursor.getColumnIndex("time"));
					totalTimeSpent += queTimeSpent;
					queStatusList.add(new QueStatus(
							cursor.getInt(cursor.getColumnIndex("question_id")), 
							cursor.getInt(cursor.getColumnIndex("status")), 
							queTimeSpent,
							queAttempts));
					
				} while(cursor.moveToNext());
				attemptsRemaining = queStatusList.size()*2-attemptsMade;
				attemptsTV.setText(getString(R.string.a2_attemptsTV) + " " + attemptsRemaining);
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
	
	void endGame(){
		baseHelper.deleteTestGame();
		baseHelper.close();
		finish();
	}

	@Override
	public void onDialogDismiss(DialogFragment dialog, String dialogType) {
		if(dialogType.equals(GameoverDialog.DIALOG_TYPE)){
			//Возвращаемся в меню выбора типа игры
			endGame();
		}
		else if(dialogType.equals(NoAttemptsDialog.DIALOG_TYPE)){
			endGame();
		}
	}
}
