package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.query.Lex;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryException;
import edu.buffalo.cse.irf14.query.QueryParser;

public class SearchRunner {
	
public enum ScoringModel {TFIDF, OKAPI};
private String indexDir = "";
private String corpusDir = "";
@SuppressWarnings("unused")
private char mode = 'E';
private PrintStream stream;
private IndexReader reader;
private static String output = "";
private int rows = 0;
@SuppressWarnings("unused")
private Query evalQuery;

public SearchRunner(String indexDir, String corpusDir, char mode, PrintStream stream) {
//TODO: IMPLEMENT THIS METHOD
	this.indexDir = indexDir;
	this.corpusDir = corpusDir;
	this.mode = mode;
	this.stream = stream;
	reader = new IndexReader(indexDir, IndexType.TERM);
}

public void query(String userQuery, ScoringModel model) {
//TODO: IMPLEMENT THIS METHOD
	QueryParser parser = new QueryParser();
	int modelNo;
	if(model == ScoringModel.TFIDF){ modelNo = 0;}
	else{ modelNo = 1;}
	try {
		long start = System.currentTimeMillis();
		Map<Integer, Double> map = parser.getPostingsFromQuery(userQuery, indexDir, reader, modelNo);
		long stop = System.currentTimeMillis();
		if(map != null){
			stream.println();
			stream.println("QUERY: " + userQuery);
			stream.println("--------------------------------------------------------------");
			stream.println("Found " + map.size() + " result(s) in : " + (stop - start) + " milliseconds.");
			stream.println("--------------------------------------------------------------");
			int count = 1;
			for(Map.Entry<Integer, Double> set: map.entrySet()){
				if(count == 11) break;
				String docId = reader.getDocs(set.getKey());
				try {
					Document d = Parser.parse(corpusDir + File.separator + docId);
					stream.println("Rank:" + count++ +  ", Relevency:" + set.getValue() + ", Document: " + docId);
					String[] title = d.getField(FieldNames.TITLE);
					if(title != null) stream.println(title[0]);
					String[] place = d.getField(FieldNames.PLACE);
					String[] date = d.getField(FieldNames.NEWSDATE);
					if(date != null && place != null) stream.println(place[0] + ", " + date[0]);
					String[] author = d.getField(FieldNames.AUTHOR);
					String[] org = d.getField(FieldNames.AUTHORORG);
					if(author != null) stream.print("BY: " + author[0]);
					if(org != null) stream.println(", " + org[0]);
					stream.println();
					String[] content = d.getField(FieldNames.CONTENT);
					if(content != null){ 
						String[] array = content[0].split(" ");
						if(array.length > 10)
						for(int i=0; i<10; i++) stream.print(array[i] + " ");
						stream.println();
						if(array.length > 20)
						for(int i=10; i<20; i++) stream.print(array[i] + " ");
						stream.println();
						if(array.length > 30)
						for(int i=20; i<30; i++) stream.print(array[i] + " ");
					}
					stream.println("...");
					stream.println("--------------------------------------------------------------");
				} catch (ParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else{
			stream.println("No results found.");
		}
	} catch (QueryException e) {
		e.printStackTrace();
	}
}

@SuppressWarnings("unused")
public void query(File queryFile) {
	//TODO: IMPLEMENT THIS METHOD
		try {
			String line = "";
			String query = "";
			String queryId = "";
			int lineNo = 0;
			int numQueries = 0;
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(queryFile));
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if(!line.equals("")){
					if(lineNo == 0) {
						numQueries=Integer.parseInt(line.substring(line.indexOf('=')+1));
						lineNo++;
					}
					else{
						query=line.substring(line.indexOf(':')+1);
						queryId=line.substring(0,line.indexOf(':')) + " ";
						query = query.trim().substring(1, query.length()-1);
						evalQuery = QueryParser.parse(query, "OR");
						eval(queryId,query);
					}
				}
			}
			stream.println("numResults="+rows);
			stream.println(output);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

public void eval(String queryId, String query) throws QueryException,ParserException {
		String str = "";
		QueryParser parser = new QueryParser();
		int count = 0;
		Map<Integer, Double> map = parser.getPostingsFromQuery(query, indexDir, reader, 0);
		if (map != null) {
			for (Map.Entry<Integer, Double> set : map.entrySet()) {
				count++;
				if (count < map.size() && count < 10)
					str = str + reader.getDocs(set.getKey()) + "#" + set.getValue()
							+ "," + " ";
				else if (count == map.size() || count == 10){
					str = str + reader.getDocs(set.getKey()) + "#" + set.getValue();
					break;
				}
			}
			rows++;
			output += queryId + ":{" + str + "}\n";
		}
	}

public void close() {
//TODO : IMPLEMENT THIS METHOD
	this.indexDir = null;
	this.corpusDir = null;
	this.stream.close();
	this.stream = null;
}

public static boolean wildcardSupported() {
//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
return true;
}

public Map<String, List<String>> getQueryTerms() {
//TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
	Map<String, List<String>> map = new TreeMap<String, List<String>>();
	List<String> list = Lex.getWildCards();
	for(String str : list){
		List<String> words = reader.getWildCards(str);
		if(words.size() > 0) map.put(str, words);
	}
	return map;
}

public static boolean spellCorrectSupported() {
//TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
return false;
}

public List<String> getCorrections() {
//TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
return null;
}
}