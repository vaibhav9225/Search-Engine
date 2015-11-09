/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import edu.buffalo.cse.irf14.analysis.PatternMatcher;

/**
 * @author Vaibhav Dwivedi
 * Class that parses a given file into a Document
 */
public class Parser {
	
	/**
	 * Static method to parse the given file into the Document object
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	
	public static Document parse(String filename) throws ParserException {
		String title = null;
		String[] author = null;
		String authorOrg = null;
		String category = null;
		String fileId = null;
		String place = null;
		String newsdate = null;
		String content = null;
		boolean isTitle = false;
		boolean isAuthor = false;
		boolean timestamp = false;
		String buffer = "";
		Document doc = new Document();
		try{	
			if(filename == null){
				throw new ParserException("File name cannot be null.");
			}
		    if(filename.isEmpty()){
		    	 throw new ParserException("File name cannot be blank.");
		    }
		    if(!(new File(filename).isFile())){
		    	throw new ParserException("Please provide a valid file as input.");
		    }
			File subPath = new File(filename);
			fileId = subPath.getName();		
			File parent;
			parent = subPath.getParentFile();
			category = parent.getName();
			if(subPath.isFile()){
				BufferedReader in = new BufferedReader(new FileReader(subPath));
				while((buffer = in.readLine()) != null){
					buffer = buffer.replace("\r\n","").replace("\r","").replace("\n","");
					if(isTitle == false){
						if(!buffer.isEmpty()){
						  	title = buffer;
						  	isTitle = true;
						}
					}
					else if(isAuthor == false || timestamp == false){
						if(!buffer.isEmpty()){
							if(buffer.contains("<AUTHOR>")){
								Pattern pattern = Pattern.compile("<AUTHOR>(.+?)</AUTHOR>");
								Matcher matcher = pattern.matcher(buffer);
								matcher.find();
								buffer = matcher.group(1);
								buffer.trim();
								buffer = buffer.replace("By", "").replace("by", "").replace("BY", "");
								String array[] = splitString(buffer, ",");
								author = splitString(array[0], "and");
								if(array.length != 1){
									authorOrg = array[1].trim();
								}
								isAuthor = true;
							}
							else if(PatternMatcher.getInstance().matchPattern(buffer, 0)){
								String[] array1 = splitString(buffer, " - ");
								buffer = array1[0].trim();
								String[] array2 = splitString(buffer, ",");
								newsdate = array2[array2.length-1].trim();
								place = "";
								for(int i=0;i<array2.length-2;i++){
									place += array2[i].trim() + ", ";
								}
								place += array2[array2.length-2].trim();
								content = "";
								for(int i=1;i<array1.length;i++){
									content += array1[i] + " ";
								}
								isAuthor = true;
								timestamp = true;
							}
							else{
								isAuthor = true;
								timestamp = true;
							}
						}
					}
					else{
						content += buffer.trim();
						content += " ";
					}
				}
				in.close();
				if(content != null) content.trim();
				doc.setField(FieldNames.FILEID, fileId);
				doc.setField(FieldNames.CATEGORY, category);
				doc.setField(FieldNames.TITLE, title);
				if(author != null)
				doc.setField(FieldNames.AUTHOR, author);
				if(authorOrg != null)
					doc.setField(FieldNames.AUTHORORG, authorOrg);
				doc.setField(FieldNames.PLACE, place);
				doc.setField(FieldNames.NEWSDATE, newsdate);
				doc.setField(FieldNames.CONTENT, content);
			}
			return doc;
		}
		catch(FileNotFoundException e){
			throw new ParserException("Please provide a valid file as input.");
		}
		catch(IOException e){
			throw new ParserException(e);	
		}
	}

	private static String[] splitString(String str, String delimiter){
		LinkedList<String> strings = new LinkedList<String>();
		str = str.trim();
		int start = 0;
		while(true){
			int index = str.indexOf(delimiter, start);
			if(index == -1){
				String sub = str.substring(start);
				strings.add(sub);
				break;
			}
			String sub = str.substring(start, index);
			strings.add(sub);
			start = index + 1;
		}
		return (String[]) strings.toArray(new String[]{});
	}
	
}