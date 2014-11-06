package com.hagtrop.zagadki;

import java.util.Random;

class ArrayShuffle{
	//перемешиваем массив букв
	static void reshuffle(char[] array){
		Random random = new Random();
		for(int i=array.length-1; i>0; i--){
			int j = random.nextInt(i+1);
			swap(array, i, j);
		}
	}
	//перемешиваем массив строк
	static void reshuffle(String[] array){
		Random random = new Random();
		for(int i=array.length-1; i>0; i--){
			int j = random.nextInt(i+1);
			swap(array, i, j);
		}
	}
	private static void swap(char[] array, int i, int j){
		char temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	private static void swap(String[] array, int i, int j){
		String temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
}