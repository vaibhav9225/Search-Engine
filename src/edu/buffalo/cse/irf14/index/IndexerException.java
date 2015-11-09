/**
 * 
 */
package edu.buffalo.cse.irf14.index;

/**
 * @author Vaibhav Dwivedi
 * Generic wrapper exception class for indexing exceptions
 */
public class IndexerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3012675871474097239L;

	private String exceptionMessage = null;
    
    public IndexerException() {
        super();
    }
 
    public IndexerException(String exceptionMessage) {
        super(exceptionMessage);
        this.exceptionMessage = exceptionMessage;
    }
 
    public IndexerException(Throwable e) {
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

