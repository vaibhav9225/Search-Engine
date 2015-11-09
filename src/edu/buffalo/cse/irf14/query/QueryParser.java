package edu.buffalo.cse.irf14.query;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.query.QueryException;
import edu.buffalo.cse.irf14.query.Lex;

public class QueryParser {
	
	private IndexReader reader;
	private int modelType = 1;

	public Map<Integer, Double> getPostingsFromQuery(String userQuery, String indexDir, IndexReader reader, int modelType) throws QueryException{
		QueryParser.parse(userQuery, "OR");
		this.modelType = modelType;
		this.reader = reader;
		Lex.lex();
		Input input = new Input();
		Map<Integer, Double> map = input.map;
		if(map!= null){
		ValueComparator cRator =  new ValueComparator(map);
	    Map<Integer, Double> sortedMap = new TreeMap<Integer,Double>(cRator);
	    sortedMap.putAll(map);
		
	    double max = 0;
	    double factor = 1e5;
	    for(Map.Entry<Integer, Double> set : sortedMap.entrySet()){
	    	max = set.getValue();
	    	break;
	    }
	    map = new LinkedHashMap<Integer, Double>();
	    for(Map.Entry<Integer, Double> set : sortedMap.entrySet()){
	    	if(max != 0){
	    		map.put(set.getKey(), Math.round((set.getValue()/max) * factor) / factor);
	    	}
	    	else{
	    		map.put(set.getKey(), 0.0);
	    	}
	    }
		}
		return map;
	}
	
	class ValueComparator implements Comparator<Integer> {
	    Map<Integer, Double> base;
	    public ValueComparator(Map<Integer, Double> base) {
	        this.base = base;
	    }   
	    public int compare(Integer a, Integer b) {
	        if(base.get(a) >= base.get(b)){
	            return -1;
	        } 
	        else {
	            return 1;
	        }
	    }
	}
	
	class Input{
		private Data data;
		private Input input;
		public String operator = "";
		public boolean isNot = false;
		public Map<Integer, Double> map = null;
		
		public Input(){
			data = new Data();
			if(Lex.nextToken != null){
				if(Lex.nextToken.equals("T_OP")){
					operator = Lex.currentValue;
					Lex.lex();
					input = new Input();
					data.map = mergeMaps(data.map, input.map, operator, input.isNot);
				}
			}
			isNot = data.isNot;
			map = data.map;
		}
		
		public Map<Integer, Double> mergeMaps(Map<Integer, Double> firstMap, Map<Integer, Double> secMap, String operator, boolean isNot){
			if(isNot == false && firstMap != null && secMap != null){
				for(Map.Entry<Integer, Double> set1 : secMap.entrySet()){
					if(firstMap.containsKey(set1.getKey())){
						firstMap.put(set1.getKey(), set1.getValue() + firstMap.get(set1.getKey()));
					}
				}
			}
			if(operator.toUpperCase().equals("AND")){
				if(isNot){
					if(firstMap != null && secMap != null) firstMap.keySet().removeAll(secMap.keySet());
				}
				else{
					if(firstMap != null && secMap != null) firstMap.keySet().retainAll(secMap.keySet());
					else firstMap = null;
				}
			}
			else{
				if(secMap != null){
					if(firstMap != null) secMap.putAll(firstMap);
					firstMap = secMap;
				}
			}
			return firstMap;
		}
	}
	
	class Data{
		private Term term;
		private Input input;
		public boolean isNot = false;
		public Map<Integer, Double> map = null;
		
		public Data(){
			if(Lex.nextToken.equals("T_LBRACKET")){
				Lex.lex();
				input = new Input();
				map = input.map;
				Lex.lex();
			}
			else{
				term = new Term();
				map = term.map;
				isNot = term.isNot;
			}
		}
	}
	
	class Term{
		private String index = "", term = "";
		public boolean isNot = false;
		public Map<Integer, Double> map = null;
		
		public Term(){
			if(Lex.nextToken.equals("T_LANGLE")){
				Lex.lex();
				index = Lex.currentValue;
				Lex.lex();
				term = Lex.currentValue;
				Lex.lex();
				Lex.lex();
				isNot = true;
				reader.setType(getType(index));
				map = reader.getWeightPostings(term, modelType);
			}
			else{
				index = Lex.currentValue;
				Lex.lex();
				if(Lex.nextToken.equals("T_QUOTE")){
					Lex.lex();
					while(!Lex.nextToken.equals("T_QUOTE")){
						term += Lex.currentValue + " ";
						Lex.lex();
					}
					term.trim();
					Lex.lex();
				}
				else{
					term = Lex.currentValue;
					Lex.lex();
				}
				reader.setType(getType(index));
				map = reader.getWeightPostings(term, modelType);
			}
		}
	}
	
	public IndexType getType(String type){
		if(type.toLowerCase().equals("term")) return IndexType.TERM;
		else if(type.toLowerCase().equals("author")) return IndexType.AUTHOR;
		else if(type.toLowerCase().equals("place")) return IndexType.PLACE;
		else return IndexType.CATEGORY;
	}
	
	public static Query parse(String userQuery, String defaultOperator) throws QueryException {
		String buffer = "";
		Query query = null;
		if(userQuery == null || userQuery.trim().isEmpty()){	
			throw new QueryException("Please provise a valid query.");
		}
		else{
			Lex.reset();
			Lex.set(userQuery);
			Parser parser = new Parser(defaultOperator);
			Lex.lex();
			buffer = parser.query();
			query = new Query();
			query.setText("{" + buffer + "}");
		}
		return Lex.set(query);
	}
}

class Parser {
	
	private static String operator = "OR";
	private static String currentIndex = "Term:";
	private static boolean isQuery = false;
	
	public Parser(String defaultOperator){
		if(defaultOperator != null && !defaultOperator.isEmpty()) operator = defaultOperator;
	}
	
	public String query() {
		String buffer = "";
		buffer += clause();
		if(Lex.nextToken != null && !Lex.nextToken.equals("T_RBRACKET")){
			if(Lex.currentValue.equals("NOT")){
				buffer += " AND ";
				Lex.lex();
				buffer += "<" + query() + ">";
			}
			else if(Lex.currentValue.equals("AND") || Lex.currentValue.equals("OR")){
				String operator = Lex.currentValue;
				buffer += " " + Lex.currentValue + " ";
				Lex.lex();
				if(Lex.currentValue.equals("NOT")){
					Lex.lex();
					if(operator.equals("AND")) buffer += "<" + query() + ">";
					else buffer += query();
				}
				else buffer += query();
			}
			else{
				buffer += " " + operator + " ";
				buffer += query();
			}
		}
		else{
			isQuery = false;
		}
		return buffer;
	}

	public String clause() {
		String buffer = "";
		if(isQuery == false){
			if(Lex.nextToken.equals("T_INDEX")) {
				currentIndex = index() + ":";
				Lex.lex();
			}
			else{
				currentIndex = "Term:";
			}
		}
		if(Lex.nextToken.equals("T_LBRACKET")){
			isQuery = true;
			buffer += "[";
			Lex.lex();
			buffer += query();
			Lex.lex();
			buffer += "]";
		}
		else{
			buffer += currentIndex;
			buffer += term();
			Lex.lex();
		}
		return buffer;
	}

	public String term() {
		String buffer = "";
		if(Lex.nextToken.equals("T_QUOTE")){
			buffer += Lex.currentValue;
			Lex.lex();
			buffer += phrase();
			buffer += Lex.currentValue;
		}
		else if(Lex.nextToken.equals("T_TERM")){
			buffer += Lex.currentValue;
		}
		return buffer;
	}
	
	public String phrase(){
		String buffer = "";
		buffer += term();
		Lex.lex();
		if(!Lex.nextToken.equals("T_QUOTE")){
			buffer += " " + phrase();
		}
		return buffer;
	}

	public String index() {
		return Lex.currentValue;
	}
}