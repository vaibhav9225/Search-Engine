package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;

//Basic idea taken from stackoverflow.com, Link:http://stackoverflow.com/questions/3322152/java-getting-rid-of-accents-and-converting-them-to-regular-letters
//From the post by @virgo47

public class AccentFilter extends TokenFilter{


	public AccentFilter(TokenStream stream) {
		// TODO Auto-generated constructor stub
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (incoming.hasNext()) {
			incoming.next();
			AccentLogic();
			return true;
		}
		return false;
	}
	
    public void AccentLogic() throws TokenizerException {
		if(incoming.getCurrent() != null){
    		Token word;
    		word = incoming.getCurrent();
    		String init = new String();
    		init = word.toString().trim();
    		StringBuilder builder = new StringBuilder(init.length());
            init = Normalizer.normalize(init, Normalizer.Form.NFKD );
            for (char ch : init.toCharArray()) {
                if (ch <= '\u007F'){
                	builder.append(ch);
                }
            }
            init = builder.toString();
        	word.setTermText(init);
			if(init.equals("")){
				incoming.remove();
			}
		}
    }
}









