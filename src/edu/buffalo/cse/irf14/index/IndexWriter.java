package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

public class IndexWriter {

	@SuppressWarnings("unchecked")
	private Map<Integer, String>[] dictionary = new TreeMap[2];
	private long totalWordCount = 0;
	private String[] fileList = new String[]{"indexContent.ser", "indexTitle.ser", "indexAuthor.ser", "indexPlace.ser", "indexCat.ser", "indexPhrase.ser"};
	private String gramFile = "gramContent.ser";
	private String indexDir;
	int mapCounts = 0;
	@SuppressWarnings("unchecked")
	private Map<String, ArrayList<Integer>[]>[] map = new TreeMap[6];
	private Map<String, HashSet<String>> grams = new TreeMap<String, HashSet<String>>();
	private int docId = 0;
	private String title = "";
	private String content = "";
	private String author = "";
	private String org = "";
	private String place = "";
	private String date = "";
	private String category = "";
	private String fileId = "";
	private String docName = "";
	private TokenStream titleStream = null;
	private TokenStream contentStream = null;
	private TokenStream authorStream = null;
	private TokenStream orgStream = null;
	private TokenStream dateStream = null;
	private TokenStream placeStream = null;
	private TokenStream categoryStream = null;
	private Tokenizer tokenizer;
	private AnalyzerFactory analyzer;

	public IndexWriter(String indexDir) {
		dictionary[0] = new TreeMap<Integer, String>();
		dictionary[1] = new TreeMap<Integer, String>();
		for(int i=0; i<6; i++){
			if((new File(indexDir + File.separator + fileList[i])).exists()){
				(new File(indexDir + File.separator + fileList[i])).delete();
			}
		}
		if((new File(indexDir + File.separator + "dictionary.ser")).exists()){
			(new File(indexDir + File.separator + "dictionary.ser")).delete();
		}
		if((new File(indexDir + File.separator + "gramFile.ser")).exists()){
			(new File(indexDir + File.separator + "gramFile.ser")).delete();
		}
		analyzer = AnalyzerFactory.getInstance();
		for(int i=0; i<6; i++){
			map[i] = new TreeMap<String, ArrayList<Integer>[]>();
		}
		this.indexDir = (new File(indexDir)).getAbsolutePath();
	}

	@SuppressWarnings({ "unused", "unchecked" })
	public void addDocument(Document d) throws IndexerException {
		org = "";
		author = "";
		if(d.getField(FieldNames.TITLE) != null)
			title = d.getField(FieldNames.TITLE)[0];
		if(d.getField(FieldNames.CONTENT) != null)
			content = d.getField(FieldNames.CONTENT)[0];
		if(d.getField(FieldNames.AUTHOR) != null){
			for(int i=0; i<d.getField(FieldNames.AUTHOR).length; i++){
				author += d.getField(FieldNames.AUTHOR)[i] + "$$";
			}
			author = author.trim();
		}
		if(d.getField(FieldNames.AUTHORORG) != null){
			for(int i=0; i<d.getField(FieldNames.AUTHORORG).length; i++){
				org += d.getField(FieldNames.AUTHORORG)[i] + "$$";
			}
			org = org.trim();
		}
		if(d.getField(FieldNames.PLACE) != null)
			place = d.getField(FieldNames.PLACE)[0];
		if(d.getField(FieldNames.NEWSDATE) != null)
			date = d.getField(FieldNames.NEWSDATE)[0];
		if(d.getField(FieldNames.CATEGORY) != null)
			category = d.getField(FieldNames.CATEGORY)[0];
		if(d.getField(FieldNames.FILEID) != null)
			fileId = d.getField(FieldNames.FILEID)[0];
		docName = fileId;
		if(!dictionary[0].containsValue(fileId)){
			docId++;
			dictionary[0].put(docId, docName);
			try {
				Analyzer filter;
				Token token;
				String word;
				char[] array;
				
				//Title Indexer Starts
				tokenizer = new Tokenizer();
				if(title!= null && !title.equals("")){
					titleStream = tokenizer.consume(title);
					filter = analyzer.getAnalyzerForField(FieldNames.TITLE, titleStream);
					while(titleStream.hasNext()) filter.increment();
					titleStream = filter.getStream();
					titleStream.reset();
					if(titleStream != null){
						while(titleStream.hasNext()){
							token = titleStream.next();
							word = token.toString().toLowerCase();
							array = word.toCharArray();
							int count = 1;
							int position = titleStream.position();
							ArrayList<Integer>[] postings = new ArrayList[4];
							postings[0] = new ArrayList<Integer>();
							postings[1] = new ArrayList<Integer>();
							postings[2] = new ArrayList<Integer>();
							postings[3] = new ArrayList<Integer>();
							int docCount = 1;
							if(map[1].get(word) != null){
								postings = map[1].get(word);
								if(postings[0].get(0) != null && postings[0].get(0) != 0){
									count = postings[0].get(0) + 1;
								}
								postings[0].clear();
							}
							if(postings[1].contains(docId)){
								docCount = postings[2].get(postings[2].size()-1) + 1;
								postings[2].remove(postings[2].size()-1);
								postings[2].add(docCount);
							}
							else{
								postings[1].add(docId);
								postings[2].add(docCount);
								postings[3].add(position);
							}
							postings[0].add(count);
							map[1].put(word, postings);
						}
					}
				}
				//Title Indexer Ends
				
				//Content Indexer Starts
				tokenizer = new Tokenizer();
				if(content != null && !content.equals("")){
					contentStream = tokenizer.consume(content);
					filter = analyzer.getAnalyzerForField(FieldNames.CONTENT, contentStream);
					while(contentStream.hasNext()) filter.increment();
					contentStream = filter.getStream();
				}
				if(date != null && !date.equals("")){
					dateStream = tokenizer.consume(date);
					filter = analyzer.getAnalyzerForField(FieldNames.NEWSDATE, dateStream);
					while(dateStream.hasNext()) filter.increment();
					dateStream = filter.getStream();
				}
				
				if(contentStream != null){
					contentStream.append(dateStream);
					contentStream.reset();
					int size = contentStream.length();
					totalWordCount += size;
					while(contentStream.hasNext()){
						dictionary[1].put(docId, size + "");
						token = contentStream.next();
						word = token.toString().toLowerCase();
						array = word.toCharArray();
						int count = 1;
						int position = contentStream.position();
						List<String> list = generateGrams(word);
						for(String str : list){
							HashSet<String> posts = new HashSet<String>();
							if(grams.get(str) != null){
								posts = grams.get(str);
							}
							posts.add(word);
							grams.put(str, posts);
						}
						String[] strArray = word.trim().split(" ");
						if(strArray.length > 1){
							for(String str : strArray){
								ArrayList<Integer>[] postings = new ArrayList[4];
								postings[0] = new ArrayList<Integer>();
								postings[1] = new ArrayList<Integer>();
								postings[2] = new ArrayList<Integer>();
								postings[3] = new ArrayList<Integer>();
								int docCount = 1;
								if(map[5].get(str) != null){
									postings = map[5].get(str);
									if(postings[0].get(0) != null && postings[0].get(0) != 0){
										count = postings[0].get(0) + 1;
									}
									postings[0].clear();
								}
								if(postings[1].contains(docId)){
									docCount = postings[2].get(postings[2].size()-1) + 1;
									postings[2].remove(postings[2].size()-1);
									postings[2].add(docCount);
								}
								else{
									postings[1].add(docId);
									postings[2].add(docCount);
									postings[3].add(position);
								}
								postings[0].add(count);
								map[5].put(str, postings);
							}
							count = 1;
						}
						ArrayList<Integer>[] postings = new ArrayList[4];
						postings[0] = new ArrayList<Integer>();
						postings[1] = new ArrayList<Integer>();
						postings[2] = new ArrayList<Integer>();
						postings[3] = new ArrayList<Integer>();
						int docCount = 1;
						if(map[0].get(word) != null){
							postings = map[0].get(word);
							if(postings[0].get(0) != null && postings[0].get(0) != 0){
								count = postings[0].get(0) + 1;
							}
							postings[0].clear();
						}
						if(postings[1].contains(docId)){
							docCount = postings[2].get(postings[2].size()-1) + 1;
							postings[2].remove(postings[2].size()-1);
							postings[2].add(docCount);
						}
						else{
							postings[1].add(docId);
							postings[2].add(docCount);
							postings[3].add(position);
						}
						postings[0].add(count);
						map[0].put(word, postings);
					}
				}
				// Content Indexer Ends
				
				//Author Indexer Starts
				tokenizer = new Tokenizer("$$");
				if(author!=null && !author.equals("")){
					authorStream = tokenizer.consume(author);
					filter = analyzer.getAnalyzerForField(FieldNames.AUTHOR, authorStream);
					while(authorStream.hasNext()) filter.increment();
					authorStream = filter.getStream();
				}
				if(org!=null && !org.equals("")){
					orgStream = tokenizer.consume(org);
					filter = analyzer.getAnalyzerForField(FieldNames.AUTHORORG, orgStream);
					while(orgStream.hasNext()) filter.increment();
					orgStream = filter.getStream();
					authorStream.append(orgStream);
					authorStream.reset();
				}
				if(authorStream != null){
					authorStream.reset();
					while(authorStream.hasNext()){
						token = authorStream.next();
						word = token.toString().toLowerCase();
						int count = 1;
						ArrayList<Integer>[] postings = new ArrayList[3];
						postings[0] = new ArrayList<Integer>();
						postings[1] = new ArrayList<Integer>();
						postings[2] = new ArrayList<Integer>();
						int docCount = 1;
						if(map[2].get(word) != null){
							postings = map[2].get(word);
							if(postings[0].get(0) != null && postings[0].get(0) != 0){
								count = postings[0].get(0) + 1;
							}
							postings[0].clear();
						}
						if(postings[1].contains(docId)){
							docCount = postings[2].get(postings[2].size()-1) + 1;
							postings[2].remove(postings[2].size()-1);
							postings[2].add(docCount);
						}
						else{
							postings[1].add(docId);
							postings[2].add(docCount);
						}
						postings[0].add(count);
						map[2].put(word, postings);
						String[] strs = word.split(" ");
						if(strs.length > 1){
							for(String str : strs){
								count = 1;
								postings = new ArrayList[3];
								postings[0] = new ArrayList<Integer>();
								postings[1] = new ArrayList<Integer>();
								postings[2] = new ArrayList<Integer>();
								docCount = 1;
								if(map[2].get(str) != null){
									postings = map[2].get(str);
									if(postings[0].get(0) != null && postings[0].get(0) != 0){
										count = postings[0].get(0) + 1;
									}
									postings[0].clear();
								}
								if(postings[1].contains(docId)){
									docCount = postings[2].get(postings[2].size()-1) + 1;
									postings[2].remove(postings[2].size()-1);
									postings[2].add(docCount);
								}
								else{
									postings[1].add(docId);
									postings[2].add(docCount);
								}
								postings[0].add(count);
								map[2].put(str, postings);
							}
						}
					}
				}
				//Author Indexer Ends
				
				//Place Indexer Starts
				tokenizer = new Tokenizer(",");
				if(place !=null && !place.equals("")){
					placeStream = tokenizer.consume(place);
					filter = analyzer.getAnalyzerForField(FieldNames.PLACE, placeStream);
					while(placeStream.hasNext()) filter.increment();
					placeStream = filter.getStream();
					placeStream.reset();
					while(placeStream.hasNext()){
						token = placeStream.next();
						word = token.toString().toLowerCase();
						int count = 1;
						ArrayList<Integer>[] postings = new ArrayList[3];
						postings[0] = new ArrayList<Integer>();
						postings[1] = new ArrayList<Integer>();
						postings[2] = new ArrayList<Integer>();
						int docCount = 1;
						if(map[3].get(word) != null){
							postings = map[3].get(word);
							if(postings[0].get(0) != null && postings[0].get(0) != 0){
								count = postings[0].get(0) + 1;
							}
							postings[0].clear();
						}
						if(postings[1].contains(docId)){
							docCount = postings[2].get(postings[2].size()-1) + 1;
							postings[2].remove(postings[2].size()-1);
							postings[2].add(docCount);
						}
						else{
							postings[1].add(docId);
							postings[2].add(docCount);
						}
						postings[0].add(count);
						map[3].put(word, postings);
					}
				}
				//Place Indexer Ends
				
				//Category Indexer Starts
				if(!category.equals("")){
					categoryStream = tokenizer.consume(category);
					while(categoryStream.hasNext()){
						token = categoryStream.next();
						word = token.toString().toLowerCase();
						int count = 1;
						ArrayList<Integer>[] postings = new ArrayList[3];
						postings[0] = new ArrayList<Integer>();
						postings[1] = new ArrayList<Integer>();
						postings[2] = new ArrayList<Integer>();
						int docCount = 1;
						if(map[4].get(word) != null){
							postings = map[4].get(word);
							if(postings[0].get(0) != null && postings[0].get(0) != 0){
								count = postings[0].get(0) + 1;
							}
							postings[0].clear();
						}
						if(postings[1].contains(docId)){
							docCount = postings[2].get(postings[2].size()-1) + 1;
							postings[2].remove(postings[2].size()-1);
							postings[2].add(docCount);
						}
						else{
							postings[1].add(docId);
							postings[2].add(docCount);
						}
						postings[0].add(count);
						map[4].put(word, postings);
					}
				}
				//Category Indexer Ends
			}
			catch (TokenizerException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void close() throws IndexerException {
		if(totalWordCount > 0){
			dictionary[0].put(-1, (totalWordCount/docId) + "");
			dictionary[1].put(-1, (totalWordCount/docId) + "");
		}
		SerializeObject ser = new SerializeObject();
		ser = new SerializeObject();
		ser.write(indexDir + File.separator + fileList[0], map[0], 1);
		map[0] = new TreeMap<String , ArrayList<Integer>[]>();
		ser = new SerializeObject();
		ser.write(indexDir + File.separator + fileList[1], map[1], 1);
		map[1] = new TreeMap<String , ArrayList<Integer>[]>();
		for(int i=2; i<6; i++){
			ser = new SerializeObject();
			ser.write(indexDir + File.separator + fileList[i], map[i], 0);
			map[i] = new TreeMap<String , ArrayList<Integer>[]>();
			
		}
		ser = new SerializeObject();
		ser.writeGrams(indexDir + File.separator + gramFile, grams);
		grams = new TreeMap<String , HashSet<String>>();
		ser = new SerializeObject();
		ser.writeDictionary(indexDir + File.separator + "dictionary.ser", dictionary);
		dictionary = new TreeMap[2];
	}
	
	public List<String> generateGrams(String str){
		List<String> list = new ArrayList<String>();
		str = "$" + str + "$";
		char[] array = str.toCharArray();
		for(int i=0; i<array.length-2; i++){
			String string = array[i] + "" + array[i+1] + "" + array[i+2] + "";
			list.add(string);
		}
		return list;
	}
}