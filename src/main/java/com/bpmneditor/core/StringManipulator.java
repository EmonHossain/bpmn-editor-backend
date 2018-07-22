package com.bpmneditor.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.springframework.stereotype.Component;

@Component("stringmanipulator")
public class StringManipulator {

	public static List<String> cutStringByDelimeter(String value, String delim) {

		List<String> list = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(value, delim);

		while (tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken().toString());
		}

		return list;
	}

	public static String buildString(List<String> stringList) {

		StringBuilder stringBuilder = new StringBuilder();

		ListIterator<String> iterator = stringList.listIterator();

		while (iterator.hasNext()) {
			stringBuilder.append(iterator.next().toString());
			stringBuilder.append(", ");
		}
		return stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length()).toString();
	}

	public static String buildString(List<String> stringList,String delim) {

		StringBuilder stringBuilder = new StringBuilder();

		ListIterator<String> iterator = stringList.listIterator();

		while (iterator.hasNext()) {
			stringBuilder.append(iterator.next().toString());
			stringBuilder.append(delim);
		}
		return stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length()).toString();
	}

	/*
	 * private String eliminateLastIndex(String str){ StringBuilder
	 * stringBuilder = new StringBuilder(str); return
	 * stringBuilder.deleteCharAt(str.length()-1).toString(); }
	 */
	
	public static List<String> makeStringTypeArraysToList(String[] arrays){
		List<String> names = new ArrayList<String>();
		for(int i=0;i<arrays.length;i++)
			names.add(arrays[i].trim());
		return names;
	}

}
