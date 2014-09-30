package com.hagtrop.zagadki;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

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
	private ArrayList<Button> lettersBtns;
	private AnswerButtonsArray answerBtns;
	private String question, answer;
	private char[] answerLetters;
	private static final char[] RUS_ALPHABET = new char[]{'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'};
	private Random random = new Random();
	private int focusBtnNum = 0;
	
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
		
		//Объединяем в массив пустые кнопки ответа
		answerBtns = new AnswerButtonsArray();
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn1));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn2));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn3));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn4));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn5));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn6));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn7));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn8));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn9));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn10));
		
		//Объединяем в массив кнопки с буквами
		lettersBtns = new ArrayList<Button>();
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn1));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn2));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn3));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn4));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn5));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn6));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn7));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn8));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn9));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn10));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn11));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn12));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn13));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn14));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn15));
		LettersOnClickListener lettersOnClickListener = new LettersOnClickListener();
		for(Button btn : lettersBtns) btn.setOnClickListener(lettersOnClickListener);
		
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
				question = cursor.getString(cursor.getColumnIndex("question")).replace("\\n", "\n");
				answer = cursor.getString(cursor.getColumnIndex("answer"));
				answer = answer.trim().toUpperCase(new Locale("ru"));
				Log.d("mLog", question);
				Log.d("mLog", answer);
				questionTV.setText(question);
				answerTV.setText(answer);
				Log.d("mLog", "QUESTION_LOADER");
				answerLetters = answer.toCharArray();
				answerBtns.setVisible(answerLetters.length);
				
				/*for(int i=0; i<answerBtns.size(); i++){
					Button btn = answerBtns.get(i);
					if(i < answerLetters.length)
						btn.setVisibility(View.VISIBLE);
					else
						btn.setVisibility(View.GONE);
				}*/
				
				//Создаём массив вариантов букв
				char[] allLetters = new char[15];
				System.arraycopy(answerLetters, 0, allLetters, 0, answerLetters.length);
				for(int i=answerLetters.length; i < allLetters.length; i++){
					char letter = RUS_ALPHABET[random.nextInt(33)];
					allLetters[i] = letter;
				}
				//Перемешиваем массив букв
				ArrayShuffle.reshuffle(allLetters);
				//Выводим буквы на кнопки
				for(int i=0; i<lettersBtns.size(); i++){
					lettersBtns.get(i).setText(String.valueOf(allLetters[i]));
				}
				
				//Возвращаем нажатые кнопки
				for(Button btn : lettersBtns){
					btn.setVisibility(View.VISIBLE);
				}
				//Очищаем кнопки ответа
				answerBtns.clearText();
				
				//Устанавливаем фокус в позицию 0
				focusBtnNum = 0;
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
	
	class LettersOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Button btn = (Button) v;
			btn.setVisibility(View.INVISIBLE);
			//answerBtns.get(focusBtnNum).setText(btn.getText());
			answerBtns.setLetter(focusBtnNum, btn);
			if(focusBtnNum < answerLetters.length-1) focusBtnNum++;
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

class ArrayShuffle{
	static void reshuffle(char[] array){
		Random random = new Random();
		for(int i=array.length-1; i>0; i--){
			int j = random.nextInt(i+1);
			swap(array, i, j);
		}
	}
	private static void swap(char[] array, int i, int j){
		char temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
}

class AnswerButtonsArray{
	private ArrayList<Button> buttons;
	private Button[] letters;
	
	public AnswerButtonsArray(){
		buttons = new ArrayList<Button>();
	}
	
	void add(Button button){
		buttons.add(button);
	}
	
	void setLetter(int position, Button letter){
		//letters.set(position, letter);
		letters[position] = letter;
		buttons.get(position).setText(letter.getText());
	}
	
	int size(){
		return buttons.size();
	}
	
	void setVisible(int count){
		for(int i=0; i<count; i++){
			buttons.get(i).setVisibility(View.VISIBLE);
			letters = new Button[count];
		}
		for(int i=count; i<buttons.size(); i++) buttons.get(i).setVisibility(View.GONE);
	}
	
	void clearText(){
		for(Button button : buttons){
			button.setText(null);
		}
	}
}
