package edu.buffalo.cse.irf14.analysis;

public class PlaceAnalyzer implements Analyzer{
	 private TokenStream incoming;
	 
	public PlaceAnalyzer(TokenStream stream){
		incoming = stream;
		}
		
		private boolean checkNull(){
			if(incoming.getCurrent() == null){
				return true;
			}
			return false;
		}

		@Override
		public boolean increment() throws TokenizerException {
			// TODO Auto-generated method stub
			if (incoming.hasNext()) {
				TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.ACCENT, incoming).increment();
				if(checkNull()) return true;
				incoming.previous();
				TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, incoming).increment();
				if(checkNull()) return true;
				incoming.previous();
				TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SPECIALCHARS, incoming).increment();
				if(checkNull()) return true;
				incoming.previous();
				TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.NUMERIC, incoming).increment();
				return true;			
			}
			return false;
		}

		@Override
		public TokenStream getStream() {
			// TODO Auto-generated method stub
			return incoming;
		}
	}