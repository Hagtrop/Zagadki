package com.hagtrop.zagadki;

import java.util.ArrayList;

//Класс для хранения и обработки статистической информации по списку вопросов
public class QuestionsStatusList {
	private ArrayList<Info> infoList;
	private int attemptsSum;
	private long timeSum;
	private int lastUnanswered;
	
	public QuestionsStatusList(){
		infoList = new ArrayList<Info>();
		attemptsSum = 0;
		timeSum = 0;
		lastUnanswered = -1;
	}
	
	void add(int id, int status, long timeSpent){
		Info queInfo = new Info();
		queInfo.id = id;
		queInfo.status = status;
		queInfo.timeSpent = timeSpent;
		infoList.add(queInfo);
		timeSum += timeSpent;
		if(status == 1) lastUnanswered++;
	}
	
	void add(int id, int status, long timeSpent, int attempts){
		add(id, status, timeSpent);
		infoList.get(infoList.size()-1).attempts = attempts;
		attemptsSum += attempts;
	}
	
	int getAttemptsSum(){
		return attemptsSum;
	}
	
	long getTimeSum(){
		return timeSum;
	}
	
	int getLastUnansweredNum(){
		return lastUnanswered;
	}
	
	int getId(int num){
		return infoList.get(num).id;
	}
	
	private class Info{
		int id, status, attempts;
		long timeSpent;
	}
}
