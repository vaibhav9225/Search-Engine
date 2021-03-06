package edu.buffalo.cse.irf14.analysis;

public class TitleAnalyzer implements Analyzer {

	private TokenStream incoming;

	public TitleAnalyzer(TokenStream stream) {
		// TODO Auto-generated constructor stub
		incoming = stream;
	}

	private boolean checkNull() {
		if (incoming.getCurrent() == null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (incoming.hasNext()) {
			TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.ACCENT, incoming).increment();
			if (checkNull()) return true;
			incoming.setIsTitle(1);
			incoming.previous();
			TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.CAPITALIZATION, incoming).increment();
			if (checkNull()) return true;
			incoming.previous();
			TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.DATE, incoming).increment();
			if (checkNull()) return true;
			incoming.previous();
			TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SPECIALCHARS, incoming).increment();
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