package com.hagtrop.zagadki;

class QueStatus{
	private int id, status;
	private int attempts;
	
	public QueStatus(int id, int status){
		this.id = id;
		this.status = status;
	}
	
	public QueStatus(int id, int status, int attempts){
		this.id = id;
		this.status = status;
		this.attempts = attempts;
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
	
	void addAttempt(){
		attempts++;
	}
}