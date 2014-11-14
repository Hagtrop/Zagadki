package com.hagtrop.zagadki;

import android.os.Handler;
import android.widget.TextView;

public class MyTimer implements Runnable{
	private Handler handler;
	private TextView textView;
	private long timeLeft;
	private long startTime;
	
	MyTimer(Handler handler, TextView textView, long timeLeft){
		this.handler = handler;
		this.textView = textView;
		this.timeLeft = timeLeft;
		this.startTime = System.currentTimeMillis();
	}
	
	@Override
	public void run() {
		timeLeft -= System.currentTimeMillis() - startTime;
		textView.setText(String.valueOf(timeLeft));
		if(timeLeft <= 0){
			handler.sendEmptyMessage(0);
		}
		else handler.postDelayed(this, 1000);
	}
	
}
