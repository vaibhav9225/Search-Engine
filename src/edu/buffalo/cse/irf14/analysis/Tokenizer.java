/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.List;
import java.util.LinkedList;

/**
 * @author Vaibhav Dwivedi
 * Class that converts a given string into a {@link TokenStream} instance
 */
public class Tokenizer {
	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */
	
	private String delimiter;
	
	public Tokenizer() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		delimiter = " ";
	}
	
	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * @param delim : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		delimiter = delim;
	}
	
	/**
	 * Method to convert the given string into a TokenStream instance.
	 * This must only break it into tokens and initialize the stream.
	 * No other processing must be performed. Also the number of tokens
	 * would be determined by the string and the delimiter.
	 * So if the string were "hello world" with a whitespace delimited
	 * tokenizer, you would get two tokens in the stream. But for the same
	 * text used with lets say "~" as a delimiter would return just one
	 * token in the stream.
	 * @param str : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException : In case any exception occurs during
	 * tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		if(str == null || str.trim() == ""){
			throw new TokenizerException("Invalid string.");
		}
		List<Token> tokenList = splitString(str);
		TokenStream stream = new TokenStream(tokenList);
		return stream;
	}
	
	private LinkedList<Token> splitString(String str){
		LinkedList<Token> strings = new LinkedList<Token>();
		if(delimiter.equals("")){
			Token token = new Token();
			token.setTermText(str);
			strings.add(token);
			return strings;
		}
		str = str.trim();
		int start = 0;
		while(true){
			int index = str.indexOf(delimiter, start);
			if(index == -1){
				String sub = str.substring(start).trim();
				Token word = new Token();
				word.setTermText(sub);
				strings.add(word);
				break;
			}
			String sub = str.substring(start, index).trim();
			Token word = new Token();
			word.setTermText(sub);
			strings.add(word);
			start = index + 1;
		}
		return strings;
	}
	
}
