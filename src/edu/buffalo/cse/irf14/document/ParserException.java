/**
 * 
 */
package edu.buffalo.cse.irf14.document;

/**
 * @author Vaibhav Dwivedi
 * Generic wrapper exception class for parsing exceptions
 */
public class ParserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4691717901217832517L;
	
	// Code Starts Here
	
	private String exceptionMessage = null;
    
    public ParserException() {
        super();
    }
 
    public ParserException(String exceptionMessage) {
        super(exceptionMessage);
        this.exceptionMessage = exceptionMessage;
    }
 
    public ParserException(Throwable e) {
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
