package com.hagtrop.zagadki;

import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class MyTimer implements Runnable{
	private Handler handler;
	private TextView textView;
	private long hours, minutes, seconds;
	private GameInfo gameInfo;
	
	MyTimer(Handler handler, TextView textView, GameInfo gameInfo){
		this.handler = handler;
		this.textView = textView;
		this.gameInfo = gameInfo;
	}
	
	@Override
	public void run() {
		gameInfo.addQueTimePassed(1000);
		long timeLeft = gameInfo.getTimeLeft();
		
		if(timeLeft <= 0){
			textView.setText("00:00:00");
			handler.sendEmptyMessage(0);
		}
		else {
			//��������� � ����
			hours = TimeUnit.MILLISECONDS.toHours(timeLeft);
			
			//��������� ������� � ��������� � ������
			timeLeft -= TimeUnit.HOURS.toMillis(hours);
			minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft);
			
			//��������� ������� � ��������� � �������
			timeLeft -= TimeUnit.MINUTES.toMillis(minutes);
			seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft);
			
			//��������� ������ �� ������
			textView.setText(hours + ":" + minutes + ":" + seconds);
			handler.postDelayed(this, 1000);
		}
	}
	
}
