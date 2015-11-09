package edu.buffalo.cse.irf14.analysis;

public class Token {
	
	private String termText = "";
	private char[] termBuffer;
	private int flag = 2;
	private int isTitle = 0;

	protected void setTermText(String text) {
		termText = text;
		termBuffer = (termText != null) ? termText.toCharArray() : null;
	}

	protected String getTermText() {
		return termText;
	}

	protected void setTermBuffer(char[] buffer) {
		termBuffer = buffer;
		termText = new String(buffer);
	}

	protected char[] getTermBuffer() {
		return termBuffer;
	}

	protected void merge(Token...tokens) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		if(tokens != null){
			String mergedString = "";
			for(int i=0; i<tokens.length; i++){
				mergedString += " " + tokens[i]; 
			}
			setTermText(getTermText() + mergedString);
		}
	}
	
	public int getIsTitle(){
		return isTitle;
	}
	
	public void setIsTitle(int flag){
		this.isTitle = flag;
	}

	public int getFlag(){
		return flag;
	}
	
	public void setFlag(int flag){
		this.flag = flag;
	}
	
	@Override
	public String toString() {
		//TODO: YOU MUST IMPLEMENT THIS METHOD
		return getTermText();
	}
}
