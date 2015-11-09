package edu.buffalo.cse.irf14.analysis;

public class SymbolFilter extends TokenFilter {
	
	private PatternMatcher matcher = PatternMatcher.getInstance();
    
	public SymbolFilter(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated constructor stub
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (incoming.hasNext()) {
			incoming.next();
			SymbolLogic();
			return true;
		}
		return false;
	}

    public void SymbolLogic() throws TokenizerException {
    	if(incoming.getCurrent() != null) {
    		Token word;
    		word = incoming.getCurrent();
    		String init = new String();
    		init = word.toString().trim();
    		if(init != null | !(init.isEmpty())) {
    			if(init.equals("won't")){
    				init = "will not"; 
    			}
    			if(init.equals("shan't")){
    				init = "shall not"; 
    			}
    			if(init.equals("can't")){
    				init = "cannot"; 
    			}
    			if(matcher.matchPattern(init, 36)) init = matcher.replacePattern(init, 45);
    			else if(matcher.matchPattern(init, 37)) init = matcher.replacePattern(init, " am", 46);  			
    			else if(matcher.matchPattern(init, 38)) init = matcher.replacePattern(init, " are", 47); 
    			else if(matcher.matchPattern(init, 39)) init = matcher.replacePattern(init, " have", 48); 
    			else if(matcher.matchPattern(init, 40)) init = matcher.replacePattern(init, " would", 49); 
    			else if(matcher.matchPattern(init, 41)) init = matcher.replacePattern(init, " will", 50); 
    			else if(matcher.matchPattern(init, 42)) init = matcher.replacePattern(init, " them", 51); 
    			else if(matcher.matchPattern(init, 43)) init = matcher.replacePattern(init, " not", 52); 
    			else if(matcher.matchPattern(init, 44)) init = matcher.replacePattern(init, 53);
    			if(!matcher.matchPattern(init, 54)){
    				init = matcher.replacePattern(init, 55);
    			}
    			//temporary code
    			String[] array = init.split("-");
    			if(array.length == 2){
	    			boolean hasNo = false;
	    			for(int i=0; i<array.length; i++){
	    				if(matcher.matchPattern(array[i], 56) || matcher.matchPattern(array[i], 57)){
	    					hasNo = true;
	    					break;
	    				}
	    			}
	    			if(hasNo == false){
		    			init = "";
		    			for(int i=0; i<array.length; i++){
		    				init += array[i] + " ";
		    			}
		    			init = init.trim();
	    			}
    			}
    			//temporary code
    			if(matcher.matchPattern(init, 58)){
    				incoming.remove();
    			}
    			init = matcher.replacePattern(init, 59); // Verify Later (Removes ? . !)
    			init = matcher.replacePattern(init, 60); // Verify Later
    			if(init.equals("")){
    				incoming.remove();
    			}
    	  }
    	  word.setTermText(init.trim()); 
    	}
    }
}