package com.hagtrop.zagadki;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.R.drawable;

public class SimpleGame extends FragmentActivity implements LoaderCallbacks<Cursor>, OnClickListener, TrueFalseDialog.NoticeDialogListener {
	TextView questionTV, answerTV;
	Button nextBtn, checkBtn;
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
	private static final char[] RUS_ALPHABET = new char[]{'�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�'};
	private Random random = new Random();
	private int focusBtnNum = 0;
	private Toast toastTrue;
	private Toast toastFalse;
	private boolean playerAnswerTrue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a1_simple_game);
		
		toastTrue = Toast.makeText(this, R.string.a1_toast_true, Toast.LENGTH_SHORT);
		toastFalse = Toast.makeText(this, R.string.a1_toast_false, Toast.LENGTH_SHORT);
		
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
		
		//������ ������ �� ����� ������
		//Button btnA = new Button(this, null, R.style.AnswerLetterBtn);
		
		
				
		BaseHelper baseHelper = BaseHelper.getInstance(this);
		
		//���� ���� ��������� - ���������, ���� ��� - ������ �����
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
		//��������� � ArrayList ��������� � �������������� �������� ��� ���������� ����������
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
			Collections.sort(quesParams);
			printArray(quesParams);
			loadQuestion(quesParams.get(0).queId);
			break;
		//��������� ������ � �����
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
	
	private void printArray(ArrayList<QueParams> mArray){
		for(QueParams mParams : mArray){
			Log.d("mLog", "queId=" + mParams.queId + " queLevel=" + mParams.queLevel + " answerId=" + mParams.answerId);
		}
	}

	@Override
	//��������� ��������� ������ �� ����� �� ������
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.a1_checkBtn:
			Log.d("mLog", "Answer: " + answerBtns.getPlayerAnswer());
			if(answerBtns.getPlayerAnswer().equals(answer)){
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
			if(currentQueIndex < quesParams.size()-1){
				currentQueIndex++;
				loadQuestion(quesParams.get(currentQueIndex).queId);
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
	public void onDialogDismiss(DialogFragment dialog) {
		Log.d("mLog", "Dialog dismissed");
		if(playerAnswerTrue){
			//��������� ��������� ������
			if(currentQueIndex < quesParams.size()-1){
				currentQueIndex++;
				loadQuestion(quesParams.get(currentQueIndex).queId);
				Log.d("mLog", "currentQueIndex=" + currentQueIndex);
			}
		}
		else{
			
		}
		
	}
}

//������ id �������, ������� ������� � id ������
class QueParams implements Comparable<QueParams>{
	public final int queId;
	public final int queLevel;
	public final int answerId;
	
	public QueParams(int queId, int queLevel, int answerId){
		this.queId = queId;
		this.queLevel = queLevel;
		this.answerId = answerId;
	}

	@Override
	public int compareTo(QueParams another) {
		// TODO Auto-generated method stub
		int result = 0;
		if(this.queLevel > another.queLevel){
			result = 1;
		}
		else if(this.queLevel < another.queLevel){
			result = -1;
		}
		return result;
	}
}

//������������ ������ ����
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
