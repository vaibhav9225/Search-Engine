/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * The abstract class that you must extend when implementing your 
 * TokenFilter rule implementations.
 * Apart from the inherited Analyzer methods, we would use the 
 * inherited constructor (as defined here) to test your code.
 * @author Vaibhav Dwivedi
 *
 */
public abstract class TokenFilter implements Analyzer {
	
	protected TokenStream incoming;
	
	/**
	 * Default constructor, creates an instance over the given
	 * TokenStream
	 * @param stream : The given TokenStream instance
	 */
	public TokenFilter(TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		incoming = stream;
	}
	
	public void setStream(TokenStream stream){
		incoming = stream;
	}
	
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return incoming;
	}
}
