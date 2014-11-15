package com.hagtrop.zagadki;

import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class MyTimer implements Runnable{
	private Handler handler;
	private TextView textView;
	private long timeLeft, startTime, hours, minutes, seconds;
	
	MyTimer(Handler handler, TextView textView, long timeLeft, Long timePassed){
		this.handler = handler;
		this.textView = textView;
		this.timeLeft = timeLeft;
		this.startTime = System.currentTimeMillis();
	}
	
	@Override
	public void run() {
		long tLeft = timeLeft - (System.currentTimeMillis() - startTime);
		Log.d("mLog", "timeLeft: " + timeLeft);
		
		if(tLeft <= 0){
			handler.sendEmptyMessage(0);
		}
		else {
			//��������� � ����
			hours = TimeUnit.MILLISECONDS.toHours(tLeft);
			
			//��������� ������� � ��������� � ������
			tLeft -= TimeUnit.HOURS.toMillis(hours);
			minutes = TimeUnit.MILLISECONDS.toMinutes(tLeft);
			
			//��������� ������� � ��������� � �������
			tLeft -= TimeUnit.MINUTES.toMillis(minutes);
			seconds = TimeUnit.MILLISECONDS.toSeconds(tLeft);
			
			//��������� ������ �� ������
			Log.d("mLog", hours + ":" + minutes + ":" + seconds);
			textView.setText(hours + ":" + minutes + ":" + seconds);
			handler.postDelayed(this, 1000);
		}
	}
	
}
