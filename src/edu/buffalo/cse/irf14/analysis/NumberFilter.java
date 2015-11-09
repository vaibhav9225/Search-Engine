package edu.buffalo.cse.irf14.analysis;

public class NumberFilter extends TokenFilter{
	
	private PatternMatcher matcher = PatternMatcher.getInstance();

	public NumberFilter(TokenStream stream) {
		// TODO Auto-generated constructor stub
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (incoming.hasNext()) {
			incoming.next();
			NumLogic();
			return true;
		}
		return false;
	}
	
    public void NumLogic() throws TokenizerException {
		if(incoming.getCurrent() != null){
    		Token word;
    		word = incoming.getCurrent();
    		String init = new String();
    		init = word.toString().trim();
    		if(!matcher.matchPattern(init, 25) && 
    			!matcher.matchPattern(init, 26) && 
    			!matcher.matchPattern(init, 27) &&
    			!matcher.matchPattern(init, 28))
    		{
	    		init = matcher.replacePattern(init, 29);
	    		word.setTermText(init);
				if(init.equals("")){
					incoming.remove();
				}
    		}
		}
    }

}