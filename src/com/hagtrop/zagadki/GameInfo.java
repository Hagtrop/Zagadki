package com.hagtrop.zagadki;

import android.util.Log;

public class GameInfo {
	final boolean USE_TIMER;
	private long timeLimit, timePassed, queTimePassed, timeLeft;
	private int queIndex;
	private boolean goodAnswer;
	
	GameInfo(boolean useTimer){
		USE_TIMER = useTimer;
		timeLimit = 0;
		timePassed = 0;
		queTimePassed = 0;
		timeLeft = 0;
		queIndex = 0;
		goodAnswer = false;
	}
	
	void setTimeLimit(long millis){
		timeLimit = millis;
		timeLeft = timeLimit - timePassed;
	}
	
	long getTimeLimit(){
		return timeLimit;
	}
	
	void setTimePassed(long millis){
		timePassed = millis;
		timeLeft = timeLimit-timePassed;
	}
	
	void addQueTimePassed(long millis){
		setQueTimePassed(getQueTimePassed() + millis);
		setTimePassed(getTimePassed() + millis);
	}
	
	long getTimePassed(){
		return timePassed;
	}
	
	void setQueTimePassed(long millis){
		queTimePassed = millis;
		Log.d("mLog", "setQueTimePassed = " + queTimePassed);
	}
	
	long getQueTimePassed(){
		return queTimePassed;
	}
	
	long getTimeLeft(){
		return timeLeft;
	}
	
	void setQueIndex(int index){
		queIndex = index;
	}
	
	int getQueIndex(){
		return queIndex;
	}
	
	void nextQue(){
		setQueIndex(getQueIndex()+1);
		setQueTimePassed(0);
		goodAnswer(false);
	}
	
	void goodAnswer(boolean result){
		goodAnswer = result;
	}
	
	boolean goodAnswer(){
		return goodAnswer;
	}
}
