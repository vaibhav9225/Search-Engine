/**
 * 
 */
package edu.buffalo.cse.irf14.query;

/**
 * @author Vaibhav Dwivedi
 * Wrapper exception class for any errors during Tokenization
 */
public class QueryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 215747832619773661L;
	
	// Code Starts Here
	
	private String exceptionMessage = null;
    
    public QueryException() {
        super();
    }
 
    public QueryException(String exceptionMessage) {
        super(exceptionMessage);
        this.exceptionMessage = exceptionMessage;
    }
 
    public QueryException(Throwable e) {
        super(e);
    }
 
    @Override
    public String toString() {
        return exceptionMessage;
    }
 
    @Override
    public String getMessage() {
        return exceptionMessage;
    }
    
    // Code Ends Here

}
