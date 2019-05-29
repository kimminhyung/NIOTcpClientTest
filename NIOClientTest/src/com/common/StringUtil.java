package com.common;

public class StringUtil {

	public StringUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static String lpadStr(int number, int length, char padChar) throws Exception{
		String strNum = String.valueOf(number);
		int padLength = length - strNum.length();
		if( padLength < 0){
			throw new IndexOutOfBoundsException("최대 길이수 초과");
		}
		for(int i=0; i<padLength; i++){
			strNum =  padChar + strNum;
		}
		
		return strNum;
	}

}
