package com.hagtrop.zagadki;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class SimpleGame extends FragmentActivity implements LoaderCallbacks<Cursor>, OnClickListener, NoticeDialogListener {
	TextView questionTV, answerTV, progressTV, levelTV;
	Button nextBtn, checkBtn;
	LinearLayout answerLayout;
	
	private static final int ARRAY_LOADER = 0;
	private static final int QUESTION_LOADER = 1;
	private ArrayList<QueStatus> queStatusList;
	private SQLiteDatabase database;
	private int currentQueIndex = 0;
	private ArrayList<Button> lettersBtns;
	private AnswerButtonsArray answerBtns;
	private char[] answerLetters;
	private static final char[] RUS_ALPHABET = new char[]{'�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�'};
	private Random random = new Random();
	private int focusBtnNum = 0;
	private boolean playerAnswerTrue;
	private BaseHelper baseHelper;
	private Question currentQuestion;
	private boolean useTimer = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a1_simple_game);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) useTimer = extras.getBoolean("timer");
		
		progressTV = (TextView) findViewById(R.id.a1_progressTV);
		levelTV = (TextView) findViewById(R.id.a1_levelTV);
		questionTV = (TextView) findViewById(R.id.a1_questionTV);
		answerTV = (TextView) findViewById(R.id.a1_answerTV);
		nextBtn = (Button) findViewById(R.id.a1_nextBtn);
		nextBtn.setOnClickListener(this);
		checkBtn = (Button) findViewById(R.id.a1_checkBtn);
		checkBtn.setOnClickListener(this);
		answerLayout = (LinearLayout) findViewById(R.id.a1_answerLayout);
		
		//���������� � ������ ������ ������ ������
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
		
		//���������� � ������ ������ � �������
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
		
		//���� ���� ��������� - ���������, ���� ��� - ������ �����
		if(!baseHelper.simpleGameExists()){
			baseHelper.newSimpleGame();
			Log.d("mLog", "STEP: getSupportLoaderManager().initLoader(ARRAY_LOADER, null, this)");	
		}
		database = baseHelper.getWritableDatabase();
		getSupportLoaderManager().initLoader(ARRAY_LOADER, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		Log.d("mLog", "onCreateLoader");
		Bundle params;
		switch(loaderID){
		case ARRAY_LOADER:
			return new MyCursorLoader(this, database, MyCursorLoader.SIMPLE_GAME_QUES, null);
		case QUESTION_LOADER:
			Log.d("mLog", "onCreateLoader, queId=" + bundle.getInt("queId"));
			params = new Bundle();
			params.putInt("questionId", bundle.getInt("queId"));
			return new MyCursorLoader(this, database, MyCursorLoader.QUE_AND_ANSWER, params);
		default: return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch(loader.getId()){
		//��������� � ArrayList ��������� � �������������� �������� ��� ���������� ����������
		case ARRAY_LOADER:
			if(cursor.moveToFirst()){
				queStatusList = new ArrayList<QueStatus>();
				int queId, queStatus;
				do{
					queId = cursor.getInt(cursor.getColumnIndex("question_id"));
					queStatus = cursor.getInt(cursor.getColumnIndex("status"));
					queStatusList.add(new QueStatus(queId, queStatus));
				} while(cursor.moveToNext());
			}
			while(currentQueIndex < queStatusList.size() && queStatusList.get(currentQueIndex).getStatus() != 0){
				currentQueIndex++;
			}
			printArray(queStatusList);
			loadQuestion(queStatusList.get(currentQueIndex).getId());
			break;
		//��������� ������ � �����
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
				answerLetters = currentQuestion.getAnswer().toCharArray();
				answerBtns.setVisible(answerLetters.length);
				
				//������ ������ ��������� ����
				char[] allLetters = new char[15];
				System.arraycopy(answerLetters, 0, allLetters, 0, answerLetters.length);
				for(int i=answerLetters.length; i < allLetters.length; i++){
					char letter = RUS_ALPHABET[random.nextInt(33)];
					allLetters[i] = letter;
				}
				//������������ ������ ����
				ArrayShuffle.reshuffle(allLetters);
				//������� ����� �� ������
				for(int i=0; i<lettersBtns.size(); i++){
					lettersBtns.get(i).setText(String.valueOf(allLetters[i]));
				}
				
				//���������� ��� ������
				for(Button btn : lettersBtns){
					btn.setVisibility(View.VISIBLE);
				}
				//������� ������ ������
				answerBtns.clearText();
				
				//������������� ����� � ������� 0
				focusBtnNum = 0;
				
				//��������� ������
				
				break;
			}
		default: break;
		}
	}
	
	//���������� ��������� ������, ��������� LoaderManager
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
	//��������� ��������� ������ �� ����� �� ������
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.a1_checkBtn:
			Log.d("mLog", "Answer: " + answerBtns.getPlayerAnswer());
			if(answerBtns.getPlayerAnswer().equals(currentQuestion.getAnswer())){
				playerAnswerTrue = true;
				//����� ���������� ������� � ������ onDialogDismiss
			}
			else{
				playerAnswerTrue = false;
			}
			FragmentManager fManager = getSupportFragmentManager();
			TrueFalseDialog dialog = new TrueFalseDialog(playerAnswerTrue);
			dialog.show(fManager, "answer_result_dialog");
			break;
		case R.id.a1_nextBtn:
			if(currentQueIndex < queStatusList.size()-1){
				currentQueIndex++;
				loadQuestion(queStatusList.get(currentQueIndex).getId());
				Log.d("mLog", "currentQueIndex=" + currentQueIndex);
			}
			break;
		default: break;
		}
	}
	
	//���������� ��� ������� ����
	class LettersOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Log.d("mLog", "pressed id: " + v.getId());
			Button btn = (Button) v;
			Log.d("mLog", "pressed button id: " + btn.getId());
			//�������� ������� ������ � ����� � ����� �� ������ � ������ ������
			btn.setVisibility(View.INVISIBLE);
			answerBtns.setLetter(focusBtnNum, btn);
			//���� ������ ������ ������� � ������ ������ � ���������� ����� �� ��
			int emtyBtnNum = answerBtns.getFirstEmptyBtn();
			if(emtyBtnNum > -1){
				focusBtnNum = emtyBtnNum;
				checkBtn.setEnabled(false);
			}
			else checkBtn.setEnabled(true);
		}
		
	}
	
	//���������� ��� ������ ������ ������
	class AnswerLettersOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			//������� ����� � ������ � ������ ������ � ������������� ����� � ��� �������
			answerBtns.deleteLetter(btn);
			focusBtnNum = answerBtns.indexOf(btn);
			if(answerBtns.getFirstEmptyBtn() > -1) checkBtn.setEnabled(false);
		}
		
	}

	@Override
	public void onDialogDismiss(DialogFragment dialog, String dialogType) {
		Log.d("mLog", "Dialog dismissed");
		if(dialogType.equals(TrueFalseDialog.DIALOG_TYPE)){
			if(playerAnswerTrue){
				baseHelper.updateSimpleGame(queStatusList.get(currentQueIndex).getId());
				//��������� ��������� ������
				if(currentQueIndex < queStatusList.size()-1){
					currentQueIndex++;
					loadQuestion(queStatusList.get(currentQueIndex).getId());
					checkBtn.setEnabled(false);
					Log.d("mLog", "currentQueIndex=" + currentQueIndex);
				}
				else{
					//���� ������� ���������, ������� ������� ������� ����
					baseHelper.deleteSimpleGame();
					//������� ��������� � ���, ��� ���� ���������
					FragmentManager fManager = getSupportFragmentManager();
					GameoverDialog gameoverDialog = new GameoverDialog();
					gameoverDialog.show(fManager, "gameover_dialog");
					//��������� ���������� � ��
					baseHelper.close();
				}
			}
		}
		else if(dialogType.equals(GameoverDialog.DIALOG_TYPE)){
			//������������ � ���� ������ ���� ����
			finish();
		}
	}
}

//����� ��� �������� ������ ������ ������ � ���������� �������� ��� ����
class AnswerButtonsArray{
	private ArrayList<Button> buttons; //������ ������ ������, ������� �������
	private Button[] letters; //������ �� ������� �����
	
	public AnswerButtonsArray(){
		buttons = new ArrayList<Button>();
	}
	
	void add(Button button){
		buttons.add(button);
	}
	
	void setLetter(int position, Button letter){
		//���� ������� � ������ ������ �� �����, �� ������� ������� �, � ����� ������� ����� �����
		if(letters[position] != null) deleteLetter(position);
		letters[position] = letter;
		buttons.get(position).setText(letter.getText());
	}
	
	private void deleteLetter(int position){
		//�� ������� �������� �� letters ������ �� ������� ������, ������ � �������
		if(!isEmpty(position)){
			getPressedBtn(position).setVisibility(View.VISIBLE);
			buttons.get(position).setText(null);
			letters[position] = null;
		}
	}
	
	void deleteLetter(Button btn){
		//�������� ������� ������� ������ � ������ ������
		int position = indexOf(btn);
		deleteLetter(position);
	}
	
	int size(){
		return buttons.size();
	}
	
	//���������� ���������� ������ � ������ ������ ��������������� ���������� ���� � �����
	void setVisible(int count){
		for(int i=0; i<count; i++) buttons.get(i).setVisibility(View.VISIBLE);
		letters = new Button[count];
		for(int i=count; i<buttons.size(); i++) buttons.get(i).setVisibility(View.GONE);
	}
	
	//������� ����� � ������ ������ ������
	void clearText(){
		for(Button button : buttons){
			button.setText(null);
		}
	}
	
	//���������� ������� � ������ ������, �� ������� ������ ������� �����
	int getLetterPosition(Button button){
		Log.d("mLog", "button id: " + button.getId());
		Log.d("mLog", "----------------");
		for(int i=0; i<letters.length; i++){
			if(letters[i] != null) Log.d("mLog", i+": "+letters[i].getId());
		}
		Log.d("mLog", "----------------");
		return Arrays.asList(letters).indexOf(button);
	}
	
	//���������� ������ �� ������ �� ������ ����, ��������������� ������� ����� � ������ ������
	Button getPressedBtn(int position){
		if(letters[position] != null) return letters[position];
		else return null;
	}
	
	//���������� ���������� ����� ������ � ������ ������
	int indexOf(Button button){
		return buttons.indexOf(button);
	}
	
	//��������� ����� ���������� ���� ������� ������ ������
	void setOnClickListener(OnClickListener listener){
		for(Button button : buttons) button.setOnClickListener(listener);
	}
	
	//��������� ������� � ������ ������ �� ������� ������ � ������� letters
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
