package com.hagtrop.zagadki;

class Question{
	private final String question, answer;
	private final int level;
	public Question(String question, String answer, int level){
		this.question = question;
		this.answer = answer;
		this.level = level;
	}
	public String getQuestion(){
		return question;
	}
	public String getAnswer(){
		return answer;
	}
	public int getLevel(){
		return level;
	}
}