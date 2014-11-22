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
			//Переводим в часы
			hours = TimeUnit.MILLISECONDS.toHours(timeLeft);
			
			//Вычисляем остаток и переводим в минуты
			timeLeft -= TimeUnit.HOURS.toMillis(hours);
			minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft);
			
			//Вычисляем остаток и переводим в секунды
			timeLeft -= TimeUnit.MINUTES.toMillis(minutes);
			seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft);
			
			//Обновляем таймер на экране
			textView.setText(hours + ":" + minutes + ":" + seconds);
			handler.postDelayed(this, 1000);
		}
	}
	
}
