package edu.buffalo.cse.irf14.analysis;

public class CapitalizationFilter extends TokenFilter{
	
	private static int capsLength = 0;

	public CapitalizationFilter(TokenStream stream) {
		// TODO Auto-generated constructor stub
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (incoming.hasNext()) {
			incoming.next();
			CapsLogic();
			return true;
		}
		return false;
	}
	
	public void CapsLogic(){
		if(incoming.getCurrent() != null){
			Token word = incoming.getCurrent();
			String init = word.getTermText();
			char[] array = init.toCharArray();
			int flag = AnalyzeWord(array);
			Token previous = incoming.previous();
			incoming.next();
			boolean firstWord = false;
			boolean lastWord = false;
			boolean hasAppended = false;
			if(PatternMatcher.getInstance().matchPattern(word.toString(), 1) || !incoming.hasNext()){
				lastWord = true;
			}
			if(previous == null){
				firstWord = true;
			}
			else if(PatternMatcher.getInstance().matchPattern(previous.toString(), 1)){
				firstWord = true;
			}
			if(flag == 0 && !lastWord){
				Token next = incoming.next();
				if(next != null){
					char[] bufferArray = next.getTermBuffer();
					if(bufferArray.length > 0){
						if(AnalyzeWord(next.getTermBuffer()) == 0){
							init = init + " " + next.getTermText();
							incoming.remove();
							incoming.previous();
							hasAppended = true;
						}
						else{
							incoming.previous();
						}
					}
				}
				else{
					incoming.previous();
				}
			}
			if((firstWord && !hasAppended && flag != 1) || (firstWord && flag == 3 && flag != 1)){
				if(incoming.hasNext()) init = init.toLowerCase();
			}
			if(flag == 1 && firstWord){
				capsLength = 1;
			}
			else if(flag == 1 && lastWord && capsLength > 0){
				int temp = capsLength;
				while(temp >= 0){
					incoming.previous();
					temp--;
				}
				temp = capsLength;
				while(temp >= 0){
					Token tip = incoming.next();
					if(incoming != null && tip != null){
						String bufArray = tip.getTermText();
						if(bufArray != null){
							init = bufArray.toLowerCase();
							incoming.getCurrent().setTermText(init);
							temp--;
						}
					}
				}
			}
			else if(flag == 1 && capsLength > 0 && !firstWord){
				capsLength++;
			}
			else{
				capsLength = 0;
			}
			word.setTermText(init);
		}
	}
	
	public int AnalyzeWord(char[] array){
		boolean firstCaps = false;
		boolean hasLower = false;
		boolean hasCaps = false;
		if(Character.isUpperCase(array[0])){
			firstCaps = true;
		}
		for(int i=1; i<array.length; i++){
			String str = array[i] + "";
			if(!PatternMatcher.getInstance().matchPattern(str, 2)){
				if(Character.isUpperCase(array[i])){
					hasCaps = true;
				}
				else{
					hasLower = true;
				}
			}
		}
		if(firstCaps && hasLower && !hasCaps){
			return 0; // First letter capital.
		}
		if(firstCaps && !hasLower && hasCaps){
			return 1; // All letters capital.
		}
		else if(!firstCaps && hasLower && !hasCaps){
			return 2; // All letters lower.
		}
		else if(hasLower && hasCaps){
			return 3; // Camel Case.
		}
		else if(array.length == 1 && firstCaps){
			return 1;
		}
		return 2;
	}

}
