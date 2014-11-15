package com.hagtrop.zagadki;

class QueStatus{
	private int id, status;
	private int attempts;
	private long timeSpent;
	
	public QueStatus(int id, int status, long timeSpent){
		this.id = id;
		this.status = status;
	}
	
	public QueStatus(int id, int status, long timeSpent, int attempts){
		this.id = id;
		this.status = status;
		this.attempts = attempts;
		this.timeSpent = timeSpent;
	}
	
	int getId(){
		return id;
	}
	
	int getStatus(){
		return status;
	}
	
	int getAttempts(){
		return attempts;
	}
	
	void setAttempts(int count){
		attempts = count;
	}
	
	void addAttempts(int count){
		attempts += count;
	}
}