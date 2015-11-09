package edu.buffalo.cse.irf14.query;

public class Query {
	
	private String query = "";
	
	public void setText(String query) {
		this.query = query;
	}
	
	public String toString(){
		return query;
	}
}