package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Lex {
	
	public static String nextToken = "";
	public static String currentValue = "";
	public static int currentIndex = 0;
	private static String query = "";
	private static boolean quoteFlag = false;
	private static int total = 0;
	public static List<String> nextTokens = new ArrayList<String>();
	public static List<String> currentValues = new ArrayList<String>();
	private static Map<String, Integer> map = new TreeMap<String, Integer>();
	private static List<String> list = new ArrayList<String>();
	
	public static Query set(Query userQuery) {
		query = "";
		if(userQuery != null) query = userQuery.toString().substring(1, userQuery.toString().length()-1).trim();
		currentIndex = 0;
		nextToken = "";
		currentValue = "";
		quoteFlag = false;
		nextTokens = new ArrayList<String>();
		currentValues = new ArrayList<String>();
		generate();
		boolean isQuote = false;
		String term = "";
		while (nextToken != null) {
			nextTokens.add(nextToken);
			currentValues.add(currentValue);
			if(nextToken.equals("T_TERM") && !isQuote){
				total++;
				if(map.containsKey(currentValue)) map.put(currentValue, map.get(currentValue) + 1);
				else map.put(currentValue.toLowerCase(), 1);
			}
			else if(nextToken.equals("T_TERM") && isQuote){
				term += currentValue + " ";
			}
			else if(nextToken.equals("T_QUOTE") && !isQuote){
				term = "";
				isQuote = true;
			}
			else if(nextToken.equals("T_QUOTE") && isQuote){
				total++;
				isQuote = false;
				if(map.containsKey(term)) map.put(term.trim().toLowerCase(), map.get(term) + 1);
				else map.put(term.trim().toLowerCase(), 1);
			}
			generate();
		}
		currentIndex = 0;
		nextToken = "";
		currentValue = "";
		quoteFlag = false;
		normalize();
		Lex.reset();
		String stringQuery = "";
		Lex.lex();
		for(int i=0; i<nextTokens.size(); i++){
			if(nextTokens.get(i) == "T_INDEX") stringQuery += currentValues.get(i) + ":";
			else if(nextTokens.get(i) == "T_QUOTE" && isQuote == false){
				stringQuery += currentValues.get(i);
				isQuote = true;
			}
			else if(nextTokens.get(i) == "T_QUOTE" && isQuote == true){
				stringQuery = stringQuery.trim();
				stringQuery += currentValues.get(i);
				isQuote = false;
			}
			else if(nextTokens.get(i) == "T_OP"){
				stringQuery += " " + currentValues.get(i) + " ";
			}
			else if(nextTokens.get(i) == "T_TERM" && isQuote){
				stringQuery += currentValues.get(i) + " ";
			}
			else{
				stringQuery += currentValues.get(i);
			}
			Lex.lex();
		}
		Lex.reset();
		Query query = new Query();
		query.setText("{"+stringQuery.trim()+"}");
		return query;
	}
	
	public static List<String> getWildCards(){
		for(Map.Entry<String, Integer> set : map.entrySet()){
			if(set.getKey().contains("?") || set.getKey().contains("*")){
				list.add(set.getKey());
			}
		}return list;
	}
	
	public static boolean hasWildCards(){
		getWildCards();
		if(list.size() > 0) return true;
		else return false;
	}
	
	public static int getTotal(){
		return total;
	}
	
	public static Map<String, Integer> getMap(){
		return map;
	}
	
	public static void set(String userQuery) {
		query = "";
		if(userQuery != null) query = userQuery.trim();
		currentIndex = 0;
		nextToken = "";
		currentValue = "";
		quoteFlag = false;
		nextTokens = new ArrayList<String>();
		currentValues = new ArrayList<String>();
		generate();
		while (nextToken != null) {
			nextTokens.add(nextToken);
			currentValues.add(currentValue);
			generate();
		}
		currentIndex = 0;
		nextToken = "";
		currentValue = "";
		quoteFlag = false;
	}
	
	public static void lex() {
		nextToken = null;
		currentValue = "";
		if(currentIndex < nextTokens.size()){
			nextToken = nextTokens.get(currentIndex);
			currentValue = currentValues.get(currentIndex);
			currentIndex++;
		}
	}
	
	public static void reset(){
		currentIndex = 0;
		nextToken = "";
		currentValue = "";
		quoteFlag = false;
	}

	public static void clear(){
		currentIndex = 0;
		nextToken = "";
		currentValue = "";
		quoteFlag = false;
		nextTokens = new ArrayList<String>();
		currentValues = new ArrayList<String>();
		map = new TreeMap<String, Integer>();
		list = new ArrayList<String>();
	}
	
	public static void normalize(){
		String previousIndex = "";
		int skipped = 0;
		int skippedTo = 0;
		int position = 0;
		for(int i=0; i<nextTokens.size(); i++){
			position = i;
			skipped = 0;
			skippedTo = 0;
			if(nextTokens.get(i).equals("T_INDEX") && !currentValues.get(i).equals(previousIndex)){
				if(i != 0){
					if(!nextTokens.get(i-1).equals("T_LBRACKET")){
						skipped++;
						skippedTo = i;
						previousIndex = currentValues.get(i);
						i++;
						while(true){
							if(i == nextTokens.size()){ break;}
							if(nextTokens.get(i).equals("T_INDEX") && !currentValues.get(i).equals(previousIndex)){
								previousIndex = "";
								break;
							}
							else if(nextTokens.get(i).equals("T_INDEX") && currentValues.get(i).equals(previousIndex)){
								skipped++;
								skippedTo = i;
								i++;
							}
							else i++;
						}
						if(skipped > 1){
							nextTokens.add(position, "T_LBRACKET");
							currentValues.add(position, "[");
							skippedTo+= 2;
							if(nextTokens.get(skippedTo).equals("T_TERM")){
								nextTokens.add(skippedTo+1, "T_RBRACKET");
								currentValues.add(skippedTo+1, "]");
							}
							else if(nextTokens.get(skippedTo).equals("T_QUOTE")){
								skippedTo++;;
								while(!nextTokens.get(skippedTo).equals("T_QUOTE")) skippedTo++;
								nextTokens.add(skippedTo+1, "T_RBRACKET");
								currentValues.add(skippedTo+1, "]");
							}
						}
						else if(i != nextTokens.size()){
							i--;
						}
					}
					else{
						previousIndex = currentValues.get(i);
					}
				}
				else{
					skipped++;
					skippedTo = i;
					previousIndex = currentValues.get(i);
					i++;
					while(true){
						if(i == nextTokens.size()) break;
						if(nextTokens.get(i).equals("T_INDEX") && !currentValues.get(i).equals(previousIndex)){
							previousIndex = "";
							break;
						}
						else if(nextTokens.get(i).equals("T_INDEX") && currentValues.get(i).equals(previousIndex)){
							skipped++;
							skippedTo = i;
							i++;
						}
						else i++;
					}
					if(skipped > 1 && i != nextTokens.size()){
						nextTokens.add(position, "T_LBRACKET");
						currentValues.add(position, "[");
						skippedTo+= 2;
						if(nextTokens.get(skippedTo).equals("T_TERM")){
							nextTokens.add(skippedTo+1, "T_RBRACKET");
							currentValues.add(skippedTo+1, "]");
						}
						else if(nextTokens.get(skippedTo).equals("T_QUOTE")){
							skippedTo++;;
							while(!nextTokens.get(skippedTo).equals("T_QUOTE")) skippedTo++;
							nextTokens.add(skippedTo+1, "T_RBRACKET");
							currentValues.add(skippedTo+1, "]");
						}
					}
					else if(i != nextTokens.size()){
						i--;
					}
				}
			}
		}
	}
	
	public static void generate() {
		char[] ch = query.toCharArray();
		String s = "";
		int i = currentIndex;
		if(i == query.length()){
			nextToken = null;
			currentValue = "";
		}
		while (i < query.length()) {
			s += Character.toString(ch[i]);
			if (i != query.length() - 1 && !s.trim().equals("")) {
				if (ch[i] == '"' && !s.trim().equals("")) {
					currentValue = s;
					nextToken = "T_QUOTE";
					currentIndex = i + 1;
					quoteFlag = !quoteFlag;
					break;
				} else if (ch[i] == '>') {
					currentValue = s;
					nextToken = "T_RANGLE";
					s = "";
					currentIndex = i + 1;
					break;
				} else if (ch[i + 1] == '>' && ch[i] != ']') {
					currentValue = s;
					nextToken = "T_TERM";
					s = "";
					currentIndex = i + 1;
					break;
				} else if (ch[i] == '<') {
					currentValue = s;
					nextToken = "T_LANGLE";
					s = "";
					currentIndex = i + 1;					
					break;
				} else if (ch[i] == ')' || ch[i] == ']') {
					currentValue = s;
					nextToken = "T_RBRACKET";
					s = "";
					currentIndex = i + 1;
					break;
				} else if (ch[i + 1] == ')' || ch[i + 1] == ']') {
					currentValue = s;
					nextToken = "T_TERM";
					s = "";
					currentIndex = i + 1;
					break;
				} else if (ch[i] == '(' || ch[i] == '[') {
					currentValue = s;
					nextToken = "T_LBRACKET";
					s = "";
					currentIndex = i + 1;					
					break;
				} else if (ch[i] == ':' && !s.trim().equals("")) {
					currentValue = s;
					nextToken = "T_INDEX";
					if (s.trim().equals("Author:")) {
						currentValue = "Author";
					} else if (s.trim().equals("Term:")) {
						currentValue = "Term";
					} else if (s.trim().equals("Category:")) {
						currentValue = "Category";
					} else if (s.trim().equals("Place:")) {
						currentValue = "Place";
					}
					s = "";
					currentIndex = i + 1;
					break;
				} else if (ch[i + 1] == '"' && quoteFlag == true && !s.trim().equals("")) {
					currentValue = s;
					s = "";
					if (currentValue.trim().equals("NOT")
							| currentValue.trim().equals("AND")
							| currentValue.trim().equals("OR")) {
						nextToken = "T_OP";
					} else {
						nextToken = "T_TERM";
					}
					currentIndex = i + 1;
					break;
				} else if (ch[i] == ' ' && !s.trim().equals("")) {
					currentValue = s;
					s = "";
					if (currentValue.trim().equals("NOT")
							| currentValue.trim().equals("AND")
							| currentValue.trim().equals("OR")) {
						nextToken = "T_OP";
					} else {
						nextToken = "T_TERM";

					}
					currentIndex = i + 1;
					break;
				}
			} else if (i == query.length() - 1 && !s.trim().equals("")) {
				currentValue = s;
				s = "";
				if (currentValue.trim().equals("NOT")
						| currentValue.trim().equals("AND")
						| currentValue.trim().equals("OR")) {
					nextToken = "T_OP";
				} else if (ch[i] == ')') {
					currentValue = ")";
					nextToken = "T_RBRACKET";
					s = "";
				} else if (ch[i] == '>') {
					currentValue = ">";
					nextToken = "T_RANGLE";
					s = "";
				} else if (ch[i] == ']') {
					currentValue = "]";
					nextToken = "T_RBRACKET";
					s = "";
				} else if (ch[i] == '"') {
					currentValue = "\"";
					nextToken = "T_QUOTE";
					s = "";
				} else {
					nextToken = "T_TERM";
				}
				currentIndex = i + 1;
				break;
			}
			i++;
		}
		currentValue = currentValue.trim();
	}
}