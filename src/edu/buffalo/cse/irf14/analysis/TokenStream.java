package edu.buffalo.cse.irf14.analysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TokenStream implements Iterator<Token>{
	
	private LinkedList<Token> tokenList;
	private int currentIndex = -1;
	private Token current = null;
	private boolean justRemoved = false;

	public TokenStream(List<Token> strList){
		tokenList = new LinkedList<Token>(strList);
	}
	
	@Override
	public boolean hasNext() {
		// TODO YOU MUST IMPLEMENT THIS
		if(currentIndex < tokenList.size() - 1){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public Token next() {
		// TODO YOU MUST IMPLEMENT THIS
		justRemoved = false;
		if(hasNext()){
			currentIndex++;
			current = tokenList.get(currentIndex);
		}
		else{
			currentIndex = tokenList.size();
			current = null;
		}
		return current;
	}
	
	public boolean hasPrevious() {
		// TODO YOU MUST IMPLEMENT THIS
		if(currentIndex > 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public Token previous() {
		// TODO YOU MUST IMPLEMENT THIS
		if(currentIndex != -1 && justRemoved){
			justRemoved = false;
			currentIndex++;
		}
		if(hasPrevious()){
			if(currentIndex == 0){
				currentIndex = -1;
				current = null;
			}
			else{
				currentIndex--;
				current = tokenList.get(currentIndex);
			}
		}
		else{
			currentIndex = -1;
			current = null;
		}
		return current;
	}

	@Override
	public void remove() {
		// TODO YOU MUST IMPLEMENT THIS
		if(currentIndex != -1 && current != null){
			tokenList.remove(currentIndex);
	        currentIndex--;
	        current = null;
	        justRemoved = true;
		}
	}

	public void reset() {
		//TODO : YOU MUST IMPLEMENT THIS
		currentIndex = -1;
		justRemoved = false;
	}

	public void append(TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS
		if(stream != null){
			tokenList.addAll(stream.getTokenList());
		}
	}

	public Token getCurrent() {
		//TODO: YOU MUST IMPLEMENT THIS
		return current;
	}
	
	public LinkedList<Token> getTokenList(){
		return tokenList;
	}
	
	public int getCurrentIndex(){
		return currentIndex;
	}
	
	public int position(){
		return currentIndex;
	}
	
	public void setIsTitle(int flag){
		if(current != null) current.setIsTitle(flag);
	}
	
	public int length(){
		if(tokenList != null) return tokenList.size();
		else return 0;
	}
}
