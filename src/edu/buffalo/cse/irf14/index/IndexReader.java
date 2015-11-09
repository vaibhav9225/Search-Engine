package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.query.Lex;

public class IndexReader {
	
	private IndexType type;
	private String[] fileList = new String[]{"indexContent.ser", "indexTitle.ser", "indexAuthor.ser", "indexPlace.ser", "indexCat.ser", "indexPhrase.ser"};
	private String gramFile = "gramContent.ser";
	@SuppressWarnings("unchecked")
	private Map<String, ArrayList<Integer>[]>[] map = new TreeMap[6];
	private Map<String, HashSet<String>> grams = new TreeMap<String, HashSet<String>>();
	private long avgLength = 0;
	private int totalDocs = 0;
	private Tokenizer tokenizer;
	private TokenStream stream;
	private AnalyzerFactory analyzer;
	private int modelType = 1;
	private int TFDPara = 1;
	private int TFQPara = 1;
	private int LDPara = 1;
	@SuppressWarnings("unchecked")
	private Map<Integer, String>[] dictionary = new TreeMap[2];
	
	public Map<Integer, Double> getWeightPostings(String term, int modelType){
		this.modelType = modelType;
		if(type == IndexType.TERM){
			Map<Integer, Double> map1 = getWeightMappings(term, IndexType.TITLE);
			Map<Integer, Double> map2 = getWeightMappings(term, IndexType.PHRASE);
			Map<Integer, Double> map3 = getWeightMappings(term, IndexType.TERM);
			map1 = mergeMaps(map1, map2);
			map1 = mergeMaps(map1, map3);
			return map1;
		}
		else return getWeightMappings(term, type);
	}
	
	public List<String> generateGrams(String str){
		List<String> list = new ArrayList<String>();
		char[] array = str.toCharArray();
		for(int i=0; i<array.length-2; i++){
			String string = array[i] + "" + array[i+1] + "" + array[i+2] + "";
			list.add(string);
		}
		return list;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> getWildCards(String wildCard){
		List<String> set = new ArrayList<String>();
		HashSet<String> set1 = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();
		String[] array;
		String[] dup;
		if(wildCard.contains("*")){
			array = wildCard.split("\\*");
			dup = wildCard.split("\\*");
		}
		else if(wildCard.contains("?")){
			array = wildCard.split("\\?");
			dup = wildCard.split("\\?");
		}
		else return set;
		if(array.length > 2) return set;
		array[0] = "$" + array[0];
		array[array.length-1] = array[array.length-1] + "$";
		if(!array[0].equals("$") && !(array[0].length() == 1)){
			for(String str : generateGrams(array[0])){
				if(grams.containsKey(str)){
					set1.addAll(grams.get(str));
				}
			}
			HashSet<String> newSet1 = new HashSet<String>(set1);
			for(String str : newSet1){
				if(!str.startsWith(dup[0])) set1.remove(str);
			}
		}
		if(array.length != 1){
			if(!array[1].equals("$") && !(array[1].length() == 1)){
				for(String str : generateGrams(array[1])){
					if(grams.containsKey(str)){
						set2.addAll(grams.get(str));
					}
				}
				HashSet<String> newSet2 = new HashSet<String>(set2);
				for(String str : newSet2){
					if(!str.endsWith(dup[1])) set2.remove(str);
				}
			}
			if(!array[0].equals("$")){ set1.retainAll(set2); set = new ArrayList(set1); }
			else set = new ArrayList(set2);
			if(wildCard.contains("?")){
				HashSet<String> newSet = new HashSet<String>(set);
				for(String str : newSet){
					int index1 = str.indexOf(dup[0]) + dup[0].length();
					int index2 = str.indexOf(dup[1]);
					if(index2 != index1 + 1){
						set.remove(str);
					}
				}
			}
		}
		else{
			set = new ArrayList(set1);
			if(wildCard.contains("?")){
				HashSet<String> newSet = new HashSet<String>(set);
				for(String str : newSet){
					if(str.length() > dup[0].length() + 1) set.remove(str);
				}
			}
		}
		if(dup[0].length() == 1){
			HashSet<String> newSet = new HashSet<String>(set);
			for(String str : newSet){
				if(!str.startsWith(dup[0])) set.remove(str);
			}
		}
		if(array.length != 1){
			if(dup[1].length() == 1){
				HashSet<String> newSet = new HashSet<String>(set);
				for(String str : newSet){
					if(!str.endsWith(dup[1])) set.remove(str);
				}
			}
		}
		return set;
	}

	public Map<Integer, Double> getWeightMappings(String term, IndexType type){
		setType(type);
		if(!term.isEmpty()){
			term = term.toLowerCase().trim();
			Map<Integer, Double> postings = new TreeMap<Integer, Double>();
			stream = getStream(term);
			String word = "";
			if(stream.hasNext()) word = stream.next().toString();
			Map<String , ArrayList<Integer>[]> map = null;
			map = getMap();
			if(map == null)
				return null;
			else{
				ArrayList<Integer>[] arrayList = map.get(word);
				if(arrayList != null){
					for(int i=0; i<arrayList[1].size(); i++){
						Double count = 0.0;
						int docLength = Integer.parseInt(dictionary[1].get(arrayList[1].get(i)));
						int termFreq = Lex.getMap().get(term);
						if(type == IndexType.TERM || type == IndexType.TITLE) setParameters(docLength, arrayList[3].get(i), termFreq);
						else setParameters(docLength, -1, termFreq);
						if(modelType == 0) count = getRelevency(arrayList[2].get(i), arrayList[1].size());
						else count = getRelevency(arrayList[2].get(i), arrayList[1].size(), docLength, termFreq);
						Integer fileId = arrayList[1].get(i);
						postings.put(fileId, count);
					}
					return postings;
				}
				else{
					return null;
				}
			}
		}
		return null;
	}
	
	public void setParameters(int docLength, int position, int TFQ){
		resetParameters();
		double ratio = docLength/avgLength;
		if(type == IndexType.TITLE){ TFDPara = 15; TFQPara = 3;}
		else if(type == IndexType.PHRASE){ TFDPara = 4; TFQPara = 3;}
		else if(type == IndexType.TERM){ TFDPara = 10; TFQPara = 6;}
		else if(type == IndexType.AUTHOR){ TFDPara = 30; TFQPara = 15;}
		else if(type == IndexType.CATEGORY){ TFDPara = 20; TFQPara = 10;}
		else if(type == IndexType.PLACE){ TFDPara = 15; TFQPara = 10;}
		if(TFQ == 1){ TFQPara += 5;}
		else if(TFQ == 2){ TFQPara += 4;}
		else if(TFQ == 3){ TFQPara += 3;}
		else if(TFQ == 4){ TFQPara += 2;}
		else if(TFQ == 5){ TFQPara += 1;}
		if(ratio > 1){
			if(position != -1){
				LDPara = 10;
				if(docLength < 40){
					if(position < 3) LDPara = 1;
					if(position < 5) LDPara = 2;
					if(position < 10) LDPara = 3;
					else if(position < 20) LDPara = 4;
					else if(position < 50) LDPara = 5;
					else if(position < 100) LDPara = 7;
				}
				else if(docLength < 80){
					if(position < 3) LDPara = 3;
					if(position < 5) LDPara = 4;
					if(position < 10) LDPara = 5;
					else if(position < 20) LDPara = 6;
					else if(position < 50) LDPara = 7;
					else if(position < 100) LDPara = 8;
					else if(position < 200) LDPara = 9;
				}
				else if(docLength < 160){
					if(position < 3) LDPara = 4;
					if(position < 5) LDPara = 5;
					if(position < 10) LDPara = 6;
					else if(position < 20) LDPara = 7;
					else if(position < 50) LDPara = 8;
					else if(position < 100) LDPara = 9;
					else if(position < 200) LDPara = 10;
					else LDPara = 10;
				}
			}
		}
		else{
			if(position != -1){
				LDPara = 1;
				if(docLength < 40){
					if(position < 3) LDPara = 10;
					if(position < 5) LDPara = 9;
					if(position < 10) LDPara = 8;
					else if(position < 20) LDPara = 7;
					else if(position < 50) LDPara = 6;
					else if(position < 100) LDPara = 4;
				}
				else if(docLength < 80){
					if(position < 3) LDPara = 8;
					if(position < 5) LDPara = 7;
					if(position < 10) LDPara = 6;
					else if(position < 20) LDPara = 5;
					else if(position < 50) LDPara = 4;
					else if(position < 100) LDPara = 2;
					else if(position < 200) LDPara = 1;
				}
				else if(docLength < 160){
					if(position < 3) LDPara = 7;
					if(position < 5) LDPara = 6;
					if(position < 10) LDPara = 5;
					else if(position < 20) LDPara = 4;
					else if(position < 50) LDPara = 3;
					else if(position < 100) LDPara = 2;
					else if(position < 200) LDPara = 1;
					else LDPara = 1;
				}
			}
		}
		if(position < 5){ TFDPara += 10;}
		else if(position < 10){ TFDPara += 8;}
		else if(position < 20){ TFDPara += 6;}
		else if(position < 50){ TFDPara += 4;}
		else if(position < 100){ TFDPara += 2;}
	}
	
	public void resetParameters(){
		TFDPara = 1;
		TFQPara = 1;
		LDPara = 2;
	}
	
	public double getRelevency(int TF, int DF){
		return (TFDPara * (1+Math.log10(TF)) * Math.log10(totalDocs/DF));
	}
	
	public double getRelevency(int TFD, int DF, int DL, int TFQ){
		double term1 = Math.log10(totalDocs/DF);
		double term2 = (TFDPara + 1)*TFD;
		double term3 = ((TFQPara+1)*TFQ/(TFQPara + TFQ));
		double term4 = TFDPara*((1-(LDPara/10))+(LDPara/10)*(DL/avgLength))+TFD;
		return term1*term2*term3/term4;
	}
	
	public void setType(IndexType type){
		this.type = type;
	}
	public List<String> getDocs(List<Integer> array){
		List<String> docs = new ArrayList<String>();
		for(int i=0; i<array.size(); i++){
			docs.add(dictionary[0].get(array.get(i)));
		}
		return docs;
	}
	public int getPosition(String word, int docId, IndexType type){
		int position = -1;
		this.type = type;
		Map<String , ArrayList<Integer>[]> map = null;
		map = getMap();
		List<Integer>[] array = map.get(word);
		if(array != null){
			int indexOf = array[1].indexOf(docId);
			if(indexOf != -1) return array[3].get(indexOf);
		}
		return position;
	}
	public String getDocs(int docId){
		return dictionary[0].get(docId);
	}
	public Map<Integer, Double> mergeMaps(Map<Integer, Double> firstMap, Map<Integer, Double> secMap){
		if(firstMap != null && secMap != null){
			for(Map.Entry<Integer, Double> set1 : secMap.entrySet()){
				if(firstMap.containsKey(set1.getKey())){
					firstMap.put(set1.getKey(), set1.getValue() + firstMap.get(set1.getKey()));
				}
			}
			for(Map.Entry<Integer, Double> set2 : firstMap.entrySet()){
				if(!secMap.containsKey(set2.getKey())){
					firstMap.put(set2.getKey(), set2.getValue()/2);
				}
			}
		}
		if(firstMap != null && secMap == null){
			for(Map.Entry<Integer, Double> set2 : firstMap.entrySet()){
				firstMap.put(set2.getKey(), set2.getValue()/2);
			}
		}
		if(secMap != null){
			if(firstMap != null){
				if(firstMap.size() > 0) secMap.putAll(firstMap);
			}
			firstMap = secMap;
		}
		return firstMap;
	}
	public Map<String, ArrayList<Integer>[]> getMap(){
		Map<String , ArrayList<Integer>[]> map = null;
		if(type == IndexType.TERM) map = new TreeMap<String, ArrayList<Integer>[]>(this.map[0]);
		else if(type == IndexType.TITLE) map = new TreeMap<String, ArrayList<Integer>[]>(this.map[1]);
		else if(type == IndexType.AUTHOR) map = new TreeMap<String, ArrayList<Integer>[]>(this.map[2]);
		else if(type == IndexType.PLACE) map = new TreeMap<String, ArrayList<Integer>[]>(this.map[3]);
		else if(type == IndexType.CATEGORY) map = new TreeMap<String, ArrayList<Integer>[]>(this.map[4]);
		else if(type == IndexType.PHRASE) map = new TreeMap<String, ArrayList<Integer>[]>(this.map[5]);
		return map;
		
	}
	public Tokenizer getTokenizer(){
		tokenizer = new Tokenizer();
		if(type == IndexType.TERM) tokenizer = new Tokenizer();
		else if(type == IndexType.AUTHOR) tokenizer = new Tokenizer("$$");
		else if(type == IndexType.PLACE) tokenizer = new Tokenizer(",");
		return tokenizer;
	}
	
	public Analyzer getAnalyzer(String term){
		tokenizer = new Tokenizer("");
		tokenizer = getTokenizer();
		try {
			stream = tokenizer.consume(term);
		}
		catch (TokenizerException e) {
			e.printStackTrace();
		}
		analyzer = AnalyzerFactory.getInstance();
		Analyzer filter = null;
		if(type == IndexType.TERM) filter = analyzer.getAnalyzerForField(FieldNames.CONTENT, stream);
		else if(type == IndexType.AUTHOR) filter = analyzer.getAnalyzerForField(FieldNames.AUTHOR, stream);
		else if(type == IndexType.PLACE) filter = analyzer.getAnalyzerForField(FieldNames.PLACE, stream);
		return filter;
	}
	
	public TokenStream getStream(String term){
		Analyzer filter = null;
		filter = getAnalyzer(term);
		if(filter != null){
			try {
				while(filter.increment()){}
			} catch (TokenizerException e) { e.printStackTrace();}
			stream = filter.getStream();
		}
		stream.reset();
		return stream;
	}
	
	public IndexReader(String indexDir, IndexType type) {
		this.type=type;
		SerializeObject ser = new SerializeObject();
		String indexFile = indexDir + File.separator + "dictionary.ser";
		ser = new SerializeObject();
		dictionary = ser.readDictionary(indexFile);
		avgLength = Long.parseLong(dictionary[0].get(-1));
		totalDocs = dictionary[0].size() - 1;
		for(int i=0; i<6; i++){
			ser = new SerializeObject();
			map[i] = ser.read(indexDir + File.separator + fileList[i]);
		}
		ser = new SerializeObject();
		grams = ser.readGrams(indexDir + File.separator + gramFile);
	}

	public int getTotalKeyTerms() {
		Map<String , ArrayList<Integer>[]> map = null;
		map = getMap();
		return map.size();
	}
	
	public int getTotalValueTerms() {
		return totalDocs;
	}
	
	public Map<String, Integer> getPostings(String term){
		if(!term.isEmpty()){
			Map<String, Integer> postings = new TreeMap<String, Integer>();
			stream = getStream(term);
			String word = "";
			if(stream.hasNext()) word = stream.next().toString();
			Map<String , ArrayList<Integer>[]> map = null;
			map = getMap();
			if(map == null)
				return null;
			else{
				ArrayList<Integer>[] arrayList = map.get(word);
				if(arrayList != null){
					for(int i=0; i<arrayList[1].size(); i++){
						Integer count = arrayList[2].get(i);
						String fileId = dictionary[0].get(arrayList[1].get(i));
						postings.put(fileId, count);
					}
					return postings;
				}
				else{
					return null;
				}
			}
		}
		return null;
	}

	public List<String> getTopK(int k) {
		if(k > 0){
			Map<String , Integer> mainMap = new TreeMap<String, Integer>();
			Map<String, ArrayList<Integer>[]> map = null;
			map = getMap();
			if(map == null) return null;
			Map<String, Integer> newMap = new TreeMap<String, Integer>();
			for(Map.Entry<String, ArrayList<Integer>[]> set : map.entrySet()){
				newMap.put(set.getKey(), set.getValue()[0].get(0));
			}
			ValueComparator cRator =  new ValueComparator(newMap);
		    TreeMap<String,Integer> sortedMap = new TreeMap<String,Integer>(cRator);
		    sortedMap.putAll(newMap);
		    map.clear();
		    System.gc();
		    int count = 0;
		    for(Map.Entry<String, Integer> set : sortedMap.entrySet()){
		    	mainMap.put(set.getKey(), set.getValue());
		    	count++;
		    	if(count == k) break;
		    }
		    newMap.clear();
		    System.gc();
			int mCount = 0;
			cRator =  new ValueComparator(mainMap);
	        TreeMap<String,Integer> mainSortedMap = new TreeMap<String,Integer>(cRator);
	        mainSortedMap.putAll(mainMap);
			mainMap.clear();
			System.gc();
	        List<String> list = new ArrayList<String>();
			for(Map.Entry<String, Integer> set : mainSortedMap.entrySet()){
				list.add(set.getKey());
				mCount++;
				if(mCount == k) break;
			}
			if(list.size() == 0) return null;
			return list;
		}
		else{
			return null;
		}
	}
	
	class ValueComparator implements Comparator<String> {
	    Map<String, Integer> base;
	    public ValueComparator(Map<String, Integer> base) {
	        this.base = base;
	    }   
	    public int compare(String a, String b) {
	        if(base.get(a) >= base.get(b)){
	            return -1;
	        } 
	        else {
	            return 1;
	        }
	    }
	}

	public Map<String, Integer> query(String...terms) {
		String[] array = terms;
		Map<String, Integer> map = new TreeMap<String, Integer>();
		List<Map<String, Integer>> tempList = new ArrayList<Map<String, Integer>>();
		for(int i=0; i<array.length; i++){
			Map<String, Integer> temp = getPostings(array[i]);
			if(temp == null) return null;
			for(Map.Entry<String, Integer> set : temp.entrySet()){
				if(map.get(set.getKey()) != null){
					map.put(set.getKey(), map.get(set.getKey()) + set.getValue());
				}
				else{
					map.put(set.getKey(), set.getValue());
				}
			}
			tempList.add(temp);
		}
		for(int i=0; i<tempList.size(); i++){
			Map<String, Integer> temp = tempList.get(i);
			Map<String, Integer> reTemp = new TreeMap<String, Integer>(map);
			for(Map.Entry<String, Integer> set2 : reTemp.entrySet()){
				if(temp.get(set2.getKey()) == null){
					map.remove(set2.getKey());
				}
			}
		}
		if(map.size() == 0) return null;
		return map;
	}
}