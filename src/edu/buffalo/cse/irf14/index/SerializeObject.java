package edu.buffalo.cse.irf14.index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class SerializeObject implements Serializable{

	private static final long serialVersionUID = -4789706688476553485L;

	public void write(String storageFile, Map<String, ArrayList<Integer>[]> map, int...content){
		int count = 0;
    	try{
    		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(storageFile)));
    		StringBuilder string = new StringBuilder();
    		for(Map.Entry<String, ArrayList<Integer>[]> set : map.entrySet()){
    			count++;
    			string.append(set.getKey().trim()).append(":");
    			string.append(set.getValue()[0].toString()).append(":");
    			string.append(set.getValue()[1].toString()).append(":");
    			if(content[0] == 1){ string.append(set.getValue()[2].toString()).append(":"); string.append(set.getValue()[3].toString()); } 
    			else string.append(set.getValue()[2].toString());
    			string.append("#");
    			if (count == 10000) {
    				writer.write(string.toString());
    				writer.newLine();
    				string = new StringBuilder();
    				writer.flush();
    				count = 0;
    			}
    		}
    		writer.write(string.toString());
    		writer.flush();
    		writer.close();
    		map.clear();
    		map = null;
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
    }
	
	public void writeGrams(String storageFile, Map<String, HashSet<String>> map){
		int count = 0;
    	try{
    		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(storageFile)));
    		StringBuilder string = new StringBuilder();
    		for(Map.Entry<String, HashSet<String>> set : map.entrySet()){
    			count++;
    			string.append(set.getKey().trim()).append(":");
    			string.append(set.getValue().toString());
    			string.append("#");
    			if (count == 10000) {
    				writer.write(string.toString());
    				writer.newLine();
    				string = new StringBuilder();
    				writer.flush();
    				count = 0;
    			}
    		}
    		writer.write(string.toString());
    		writer.flush();
    		writer.close();
    		map.clear();
    		map = null;
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
    }

	@SuppressWarnings("resource")
	public Map<String, HashSet<String>> readGrams(String file){
		Map<String, HashSet<String>> map = new TreeMap<String, HashSet<String>>();
    	try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String string = "";
			String[] array1 = null;
			String[] array2 = null;
			while((string = reader.readLine()) != null){
				array1 = string.split("#");
				for(String data : array1){
					array2 = data.split(":");
					if(array2.length == 2){
						if(!array2[0].equals("") || array2[0]!=null){
							HashSet<String> set = new HashSet<String>();
							for(String str : array2[1].replace("[", "").replace("]", "").split(", ")){
								set.add(str);
							}
							map.put(array2[0], set);
						}
					}
				}
			}
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
		return map;
    }
	
    @SuppressWarnings("unchecked")
	public Map<String, ArrayList<Integer>[]> read(String file){
    	try{
			Map<String, ArrayList<Integer>[]> map = new TreeMap<String, ArrayList<Integer>[]>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String string = "";
			String[] array1 = null, array2 = null;
			while((string = reader.readLine()) != null){
				array1 = string.split("#");
				for(String data : array1){
					array2 = data.split(":");
					if(array2.length == 4){
						if(!array2[0].equals("") || array2[0]!=null){
							ArrayList<Integer>[] list = new ArrayList[3];
							list[0] = new ArrayList<Integer>();
							for(String str : array2[1].replace("[", "").replace("]", "").split(", ")){
								list[0].add(Integer.parseInt(str));
							}
							list[1] = new ArrayList<Integer>();
							for(String str : array2[2].replace("[", "").replace("]", "").split(", ")){
								list[1].add(Integer.parseInt(str));
							}
							list[2] = new ArrayList<Integer>();
							for(String str : array2[3].replace("[", "").replace("]", "").split(", ")){
								list[2].add(Integer.parseInt(str));
							}
							map.put(array2[0], list);
						}
					}
					else if(array2.length == 5){
						if(!array2[0].equals("") || array2[0]!=null){
							ArrayList<Integer>[] list = new ArrayList[4];
							list[0] = new ArrayList<Integer>();
							for(String str : array2[1].replace("[", "").replace("]", "").split(", ")){
								list[0].add(Integer.parseInt(str));
							}
							list[1] = new ArrayList<Integer>();
							for(String str : array2[2].replace("[", "").replace("]", "").split(", ")){
								list[1].add(Integer.parseInt(str));
							}
							list[2] = new ArrayList<Integer>();
							for(String str : array2[3].replace("[", "").replace("]", "").split(", ")){
								list[2].add(Integer.parseInt(str));
							}
							list[3] = new ArrayList<Integer>();
							for(String str : array2[4].replace("[", "").replace("]", "").split(", ")){
								list[3].add(Integer.parseInt(str));
							}
							map.put(array2[0], list);
						}
					}
				}
			}
			reader.close();
    		return map;
    	}
    	catch(EOFException e){
    		e.printStackTrace();
    		return null;
    	}
    	catch(IOException e){
    		e.printStackTrace();
    		return null;
    	}
    }
    
	public void writeDictionary(String storageFile, Map<Integer, String>[] map){
		int count = 0;
    	try{
    		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(storageFile)));
    		StringBuilder string = new StringBuilder();
    		for(Map.Entry<Integer, String> set : map[0].entrySet()){
    			count++;
    			string.append(set.getKey()).append(":");
    			string.append(set.getValue()).append(":");
    			string.append(map[1].get(set.getKey()));
    			string.append("#");
    			if (count == 10000) {
    				writer.write(string.toString());
    				writer.newLine();
    				string = new StringBuilder();
    				writer.flush();
    				count = 0;
    			}
    		}
    		writer.write(string.toString());
    		writer.flush();
    		writer.close();
    		map[0].clear();
    		map[1].clear();
    		map = null;
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
    }

    @SuppressWarnings("unchecked")
	public Map<Integer, String>[] readDictionary(String file){
    	try{
			Map<Integer, String>[] map = new TreeMap[2];
			map[0] = new TreeMap<Integer, String>();
			map[1] = new TreeMap<Integer, String>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String string = "";
			String[] array1 = null, array2 = null;
			while((string = reader.readLine()) != null){
				array1 = string.split("#");
				for(String data : array1){
					array2 = data.split(":");
					if(array2.length == 3){
						if(!array2[0].equals("") || array2[0]!=null){
							map[0].put(Integer.parseInt(array2[0]), array2[1]);
							map[1].put(Integer.parseInt(array2[0]), array2[2]);
						}
					}
				}
			}
			reader.close();
    		return map;
    	}
    	catch(EOFException e){
    		e.printStackTrace();
    		return null;
    	}
    	catch(IOException e){
    		e.printStackTrace();
    		return null;
    	}
    }
}