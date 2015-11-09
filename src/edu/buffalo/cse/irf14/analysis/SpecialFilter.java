package edu.buffalo.cse.irf14.analysis;

public class SpecialFilter extends TokenFilter{
	
	private PatternMatcher matcher = PatternMatcher.getInstance();

	public SpecialFilter(TokenStream stream) {
		// TODO Auto-generated constructor stub
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (incoming.hasNext()) {
			incoming.next();
			SpecialLogic();
			return true;
		}
		return false;
	}
	
    public void SpecialLogic() throws TokenizerException {
		if(incoming.getCurrent() != null){
    		Token word;
    		word = incoming.getCurrent();
    		String init = new String();
    		init = word.toString().trim();
    		if(matcher.matchPattern(init, 30)){
    			init = matcher.replacePattern(init, 31);
    		}
    		if(!matcher.matchPattern(init, 32)){
    			init = matcher.replacePattern(init, 33); // Check later for + - and *
    		}
    		else{
    			init = matcher.replacePattern(init, 34);
    		}
			word.setTermText(init);
			if(init.equals("")){
				incoming.remove();
			}
		}
    }

}
