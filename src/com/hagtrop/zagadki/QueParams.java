package com.hagtrop.zagadki;

//Хранит id вопроса, уровень вопроса и id ответа
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