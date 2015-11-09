package edu.buffalo.cse.irf14;

import java.io.File;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;

public class Runner {
	
	public static void main(String[] args) {
		String ipDir = args[0];
		String indexDir = args[1];
		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();
		String[] files;
		File dir;
		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		try {
			int c=0;
			long x = System.currentTimeMillis();
			for (String cat : catDirectories) {
				dir = new File(ipDir + File.separator + cat);
				files = dir.list();
				if (files == null)
					continue;
				for (String f : files) {c++;
					try {
						d = Parser.parse(dir.getAbsolutePath() + File.separator + f);
						writer.addDocument(d);
					}
					catch (ParserException e) {
						e.printStackTrace();
					} 
				}
			}
			writer.close();
			long y = System.currentTimeMillis();
			System.out.println("Done indexing " + c + " files in -> " + ((int)(y-x)/1000) + " seconds.");
		}
		catch (IndexerException e) {
			e.printStackTrace();
		}
	}
}