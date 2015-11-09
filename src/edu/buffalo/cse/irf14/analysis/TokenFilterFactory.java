/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.analysis.TokenFilterType;
/**
 * Factory class for instantiating a given TokenFilter
 * @author Vaibhav Dwivedi
 *
 */
public class TokenFilterFactory {
	private static TokenFilterFactory instance = new TokenFilterFactory();
	private SymbolFilter symbolFilter;
	private DateFilter dateFilter;
	private NumberFilter numFilter;
	private CapitalizationFilter capsFilter;
	private StopFilter stopFilter;
	private StemFilter stemFilter;
	private AccentFilter accentFilter;
	private SpecialFilter specialFilter;
	/**
	 * Static method to return an instance of the factory class.
	 * Usually factory classes are defined as singletons, i.e. 
	 * only one instance of the class exists at any instance.
	 * This is usually achieved by defining a private static instance
	 * that is initialized by the "private" constructor.
	 * On the method being called, you return the static instance.
	 * This allows you to reuse expensive objects that you may create
	 * during instantiation
	 * @return An instance of the factory
	 */
	public static TokenFilterFactory getInstance() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		return instance;
	}
	
	/**
	 * Returns a fully constructed {@link TokenFilter} instance
	 * for a given {@link TokenFilterType} type
	 * @param type: The {@link TokenFilterType} for which the {@link TokenFilter}
	 * is requested
	 * @param stream: The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 */
	public TokenFilter getFilterByType(TokenFilterType type, TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		try{
			switch(type){
			case SYMBOL : if(symbolFilter == null) symbolFilter = new SymbolFilter(stream); else symbolFilter.setStream(stream); return symbolFilter;
			case DATE : if(dateFilter == null) dateFilter = new DateFilter(stream); else dateFilter.setStream(stream); return dateFilter;
			case NUMERIC : if(numFilter == null) numFilter = new NumberFilter(stream); else numFilter.setStream(stream); return numFilter;
			case CAPITALIZATION : if(capsFilter == null) capsFilter = new CapitalizationFilter(stream); else capsFilter.setStream(stream); return capsFilter;
			case STOPWORD : if(stopFilter == null) stopFilter = new StopFilter(stream); else stopFilter.setStream(stream); return stopFilter;
			case STEMMER : if(stemFilter == null) stemFilter = new StemFilter(stream); else stemFilter.setStream(stream); return stemFilter;
			case ACCENT : if(accentFilter == null) accentFilter = new AccentFilter(stream); else accentFilter.setStream(stream); return accentFilter;
			case SPECIALCHARS : if(specialFilter == null) specialFilter = new SpecialFilter(stream); else specialFilter.setStream(stream); return specialFilter;
			default : return null;
			}
		}
		catch(TokenizerException e){
			e.getMessage();
			return null;
		}
	}
}
