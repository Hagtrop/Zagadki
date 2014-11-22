package com.hagtrop.zagadki;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleGame extends FragmentActivity implements LoaderCallbacks<Cursor>, OnClickListener, NoticeDialogListener {
	TextView questionTV, answerTV, progressTV, levelTV, timeTV;
	Button checkBtn;
	LinearLayout answerLayout;
	
	private static final int ARRAY_LOADER = 0;
	private static final int QUESTION_LOADER = 1;
	private static final char[] RUS_ALPHABET = new char[]{'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'};
	private Random random = new Random();
	private ArrayList<QueStatus> queStatusList;
	private SQLiteDatabase database;
	private ArrayList<Button> lettersBtns;
	private AnswerButtonsArray answerBtns;
	private char[] answerLetters;
	private int focusBtnNum = 0;
	private Question currentQuestion;
	private BaseHelper baseHelper;
	private Handler handler;
	private MyTimer timer;
	
	private GameInfo gameInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a1_simple_game);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) gameInfo = new GameInfo(extras.getBoolean("timer"));
		else gameInfo = new GameInfo(false);
		
		progressTV = (TextView) findViewById(R.id.a1_progressTV);
		levelTV = (TextView) findViewById(R.id.a1_levelTV);
		timeTV = (TextView) findViewById(R.id.a1_timeTV);
		
		//Отображаем таймер, если выбран режим с таймером
		if(gameInfo.USE_TIMER) timeTV.setVisibility(View.VISIBLE);
		else timeTV.setVisibility(View.GONE);
		
		questionTV = (TextView) findViewById(R.id.a1_questionTV);
		answerTV = (TextView) findViewById(R.id.a1_answerTV);
		checkBtn = (Button) findViewById(R.id.a1_checkBtn);
		checkBtn.setOnClickListener(this);
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
		answerBtns.setOnClickListener(new AnswerLettersOnClickListener());
		
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
				
		baseHelper = BaseHelper.getInstance(this);
		
		//Если игра сохранена - загружаем, если нет - создаём новую
		if(!baseHelper.simpleGameExists(gameInfo.USE_TIMER)){
			baseHelper.newSimpleGame(gameInfo.USE_TIMER);
		}
		database = baseHelper.getWritableDatabase();
		getSupportLoaderManager().initLoader(ARRAY_LOADER, null, this);
	}
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(timer);
		baseHelper.updateSimpleGame(
				queStatusList.get(gameInfo.getQueIndex()).getId(), 
				gameInfo.goodAnswer() ? 1 : 0, 
				gameInfo.getQueTimePassed(),
				gameInfo.USE_TIMER);
	}



	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		Bundle params;
		switch(loaderID){
		case ARRAY_LOADER:
			if(gameInfo.USE_TIMER) return new MyCursorLoader(this, database, MyCursorLoader.SIMPLE_TIMER_GAME_QUES, null);
			else return new MyCursorLoader(this, database, MyCursorLoader.SIMPLE_GAME_QUES, null);
		case QUESTION_LOADER:
			params = new Bundle();
			params.putInt("questionId", bundle.getInt("queId"));
			return new MyCursorLoader(this, database, MyCursorLoader.QUE_AND_ANSWER, params);
		default: return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch(loader.getId()){
		//Сохраняем в ArrayList параметры и характеристики вопросов для дальнейшей сортировки
		case ARRAY_LOADER:
			if(cursor.moveToFirst()){
				queStatusList = new ArrayList<QueStatus>();
				long queTimeSpent;
				int queStatus;
				int queIndex = -1;
				boolean nextFound = false;
				do{
					queIndex++;
					queTimeSpent = cursor.getLong(cursor.getColumnIndex("time"));
					gameInfo.setTimePassed(gameInfo.getTimePassed() + queTimeSpent);
					queStatus = cursor.getInt(cursor.getColumnIndex("status"));
					//Определяем номер первой неотвеченной загадки
					if(!nextFound && queStatus == 0){
						gameInfo.setQueIndex(queIndex);
						nextFound = true;
					}
					queStatusList.add(new QueStatus(
							cursor.getInt(cursor.getColumnIndex("question_id")),
							queStatus,
							queTimeSpent));
					
				} while(cursor.moveToNext());
				
				gameInfo.setTimeLimit(10 * 1000 * queStatusList.size());
			}
			printArray(queStatusList);
			loadQuestion(queStatusList.get(gameInfo.getQueIndex()).getId());
			break;
		//Извлекаем вопрос и ответ
		case QUESTION_LOADER:
			if(cursor.moveToFirst()){
				currentQuestion = new Question(
						cursor.getString(cursor.getColumnIndex("question")).replace("\\n", "\n"),
						cursor.getString(cursor.getColumnIndex("answer")).trim().toUpperCase(new Locale("ru")),
						cursor.getInt(cursor.getColumnIndex("level")));
				
				progressTV.setText(getString(R.string.a1_progressTV) + " " + (gameInfo.getQueIndex()+1) + "/" + queStatusList.size());
				levelTV.setText(getString(R.string.a1_levelTV) + " " + currentQuestion.getLevel());
				questionTV.setText(currentQuestion.getQuestion());
				answerTV.setText(currentQuestion.getAnswer());
				answerLetters = currentQuestion.getAnswer().toCharArray();
				answerBtns.setVisible(answerLetters.length);
				
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
				
				//Отображаем все кнопки
				for(Button btn : lettersBtns){
					btn.setVisibility(View.VISIBLE);
				}
				//Очищаем кнопки ответа
				answerBtns.clearText();
				
				//Устанавливаем фокус в позицию 0
				focusBtnNum = 0;
				
				//Запускаем таймер
				Handler.Callback hCallback = new Handler.Callback() {	
					@Override
					public boolean handleMessage(Message msg) {
						if(msg.what == 0 && gameInfo.USE_TIMER){
							Log.d("mLog", "TIME LEFT 0");
							endGame("Ваше время вышло");
						}
						return false;
					}
				};
				handler = new Handler(hCallback);
				timer = new MyTimer(handler, timeTV, gameInfo);
				handler.postDelayed(timer, 1000);
				
				break;
			}
		default: break;
		}
	}
	
	void endGame(String message){
		baseHelper.deleteSimpleGame(gameInfo.USE_TIMER);
		baseHelper.close();
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		FragmentManager fManager = getSupportFragmentManager();
		GameoverDialog gameoverDialog = new GameoverDialog();
		gameoverDialog.show(fManager, "gameover_dialog");
		finish();
	}
	
	//Асинхронно загружаем вопрос, используя LoaderManager
	private void loadQuestion(int queId){
		Bundle bundle = new Bundle();
		bundle.putInt("queId", queId);
		getSupportLoaderManager().restartLoader(QUESTION_LOADER, bundle, this);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub 
		
	}
	
	private void printArray(ArrayList<QueStatus> mArray){
		for(QueStatus qStatus : mArray){
			Log.d("mLog", "queId=" + qStatus.getId() + " queStatus=" + qStatus.getStatus());
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.a1_checkBtn:
			if(answerBtns.getPlayerAnswer().equals(currentQuestion.getAnswer())){
				gameInfo.goodAnswer(true);
				//Вывод следующего вопроса в методе onDialogDismiss
			}
			else{
				gameInfo.goodAnswer(false);
			}
			handler.removeCallbacks(timer);
			baseHelper.updateSimpleGame(
					queStatusList.get(gameInfo.getQueIndex()).getId(), 
					gameInfo.goodAnswer() ? 1 : 0, 
					gameInfo.getQueTimePassed(),
					gameInfo.USE_TIMER);
			FragmentManager fManager = getSupportFragmentManager();
			TrueFalseDialog dialog = new TrueFalseDialog(gameInfo.goodAnswer());
			dialog.show(fManager, "answer_result_dialog");
			break;
		default: break;
		}
	}
	
	//Обработчик для массива букв
	class LettersOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			//Скрываем нажатую кнопку и пишем её букву на кнопке в строке ответа
			btn.setVisibility(View.INVISIBLE);
			answerBtns.setLetter(focusBtnNum, btn);
			//Ищем первую пустую позицию в строке ответа и перемещаем фокус на неё
			int emtyBtnNum = answerBtns.getFirstEmptyBtn();
			if(emtyBtnNum > -1){
				focusBtnNum = emtyBtnNum;
				checkBtn.setEnabled(false);
			}
			else checkBtn.setEnabled(true);
		}
		
	}
	
	//Обработчик для кнопок строки ответа
	class AnswerLettersOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			//Удаляем букву с кнопки в строке ответа и устанавливаем фокус в эту позицию
			answerBtns.deleteLetter(btn);
			focusBtnNum = answerBtns.indexOf(btn);
			if(answerBtns.getFirstEmptyBtn() > -1) checkBtn.setEnabled(false);
		}
		
	}

	@Override
	public void onDialogDismiss(DialogFragment dialog, String dialogType) {
		if(dialogType.equals(TrueFalseDialog.DIALOG_TYPE)){
			if(gameInfo.goodAnswer()){
				//Загружаем следующий вопрос
				if(gameInfo.getQueIndex() < queStatusList.size()-1){
					gameInfo.nextQue();
					loadQuestion(queStatusList.get(gameInfo.getQueIndex()).getId());
					checkBtn.setEnabled(false);
					Log.d("mLog", "currentQueIndex=" + gameInfo.getQueIndex());
				}
				else{
					endGame("Вы ответили на все вопросы");
				}
			}
			else{
				handler.postDelayed(timer, 1000);
			}
		}
		else if(dialogType.equals(GameoverDialog.DIALOG_TYPE)){
			//Возвращаемся в меню выбора типа игры
			finish();
		}
	}
}

//Класс для хранения кнопок строки ответа и соверщения операций над ними
class AnswerButtonsArray{
	private ArrayList<Button> buttons; //кнопки строки ответа, включая скрытые
	private Button[] letters; //ссылки на нажатые буквы
	
	public AnswerButtonsArray(){
		buttons = new ArrayList<Button>();
	}
	
	void add(Button button){
		buttons.add(button);
	}
	
	void setLetter(int position, Button letter){
		//Если позиция в строке ответа не пуста, то сначала очищаем её, а потом заносим новую букву
		if(letters[position] != null) deleteLetter(position);
		letters[position] = letter;
		buttons.get(position).setText(letter.getText());
	}
	
	private void deleteLetter(int position){
		//По позиции получаем из letters ссылку на скрытую кнопку, делаем её видимой
		if(!isEmpty(position)){
			getPressedBtn(position).setVisibility(View.VISIBLE);
			buttons.get(position).setText(null);
			letters[position] = null;
		}
	}
	
	void deleteLetter(Button btn){
		//Получаем позицию нажатой кнопки в строке ответа
		int position = indexOf(btn);
		deleteLetter(position);
	}
	
	int size(){
		return buttons.size();
	}
	
	//Отображаем количество кнопок в строке ответа соответствующее количеству букв в слове
	void setVisible(int count){
		for(int i=0; i<count; i++) buttons.get(i).setVisibility(View.VISIBLE);
		letters = new Button[count];
		for(int i=count; i<buttons.size(); i++) buttons.get(i).setVisibility(View.GONE);
	}
	
	//Удаляем буквы с кнопок строки ответа
	void clearText(){
		for(Button button : buttons){
			button.setText(null);
		}
	}
	
	//Возвращаем позицию в строке ответа, на которую встала нажатая буква
	int getLetterPosition(Button button){
		Log.d("mLog", "button id: " + button.getId());
		Log.d("mLog", "----------------");
		for(int i=0; i<letters.length; i++){
			if(letters[i] != null) Log.d("mLog", i+": "+letters[i].getId());
		}
		Log.d("mLog", "----------------");
		return Arrays.asList(letters).indexOf(button);
	}
	
	//Возвращаем ссылку на кнопку из набора букв, соответствующую нажатой букве в строке ответа
	Button getPressedBtn(int position){
		if(letters[position] != null) return letters[position];
		else return null;
	}
	
	//Возвращаем порядковый номер кнопки в строке ответа
	int indexOf(Button button){
		return buttons.indexOf(button);
	}
	
	//Назначаем общий обработчик всем кнопкам строки ответа
	void setOnClickListener(OnClickListener listener){
		for(Button button : buttons) button.setOnClickListener(listener);
	}
	
	//Проверяем позицию в строке ответа по наличию ссылки в массиве letters
	boolean isEmpty(int position){
		if(letters[position] == null) return true;
		else return false;
	}
	
	int getFirstEmptyBtn(){
		int position = -1;
		for(int i=0; i<letters.length; i++){
			if(letters[i] == null){
				position = i;
				break;
			}
		}
		return position;
	}
	
	String getPlayerAnswer(){
		String result = "";
		for(Button btn : buttons){
			if(btn.getVisibility() == View.VISIBLE) result += btn.getText();
		}
		return result;
	}
	
	/*void setFocusedBg(int position){
		buttons.get(position).setBackground(android.R.drawable.btn_default_small_pressed);
	}*/
}