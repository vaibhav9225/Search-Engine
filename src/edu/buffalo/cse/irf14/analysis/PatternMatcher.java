package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatcher {
	private static PatternMatcher instance = new PatternMatcher();
	private Pattern cd = Pattern.compile(".*");
	private Pattern c0 = Pattern.compile(".*[,].*\\s[-]{1}\\s.*");
	private Pattern c1 = Pattern.compile("^.*[.]+$");
	private Pattern c2 = Pattern.compile("[.,?\\-+*/_'\"!]");
	private Pattern c3 = Pattern.compile("\\W*$");
	private Pattern c4 = Pattern.compile("[0-9]+");
	private Pattern c5 = Pattern.compile("\\d+");
	private Pattern c6 = Pattern.compile("\\w*");
	private Pattern c7 = Pattern.compile("\\d{4}");
	private Pattern c8 = Pattern.compile("\\d+-\\d+\\W*");
	private Pattern c9 = Pattern.compile("\\W+\\d*");
	private Pattern c10 = Pattern.compile("\\d+-\\d+");
	private Pattern c11 = Pattern.compile("\\W*$");
	private Pattern c12 = Pattern.compile("\\d*\\W+");
	private Pattern c13 = Pattern.compile("\\d+\\w*\\W*");
	private Pattern c14 = Pattern.compile("\\D*\\W*");
	private Pattern c15 = Pattern.compile("\\W*");
	private Pattern c16 = Pattern.compile("\\d*");
	private Pattern c17 = Pattern.compile("[0-9]+[\\W]*");
	private Pattern c18 = Pattern.compile("[0-9]{4}");
	private Pattern c19 = Pattern.compile("[0-2]?[0-9]:[0-5][0-9].*");
	private Pattern c20 = Pattern.compile("[0-2]?[0-9]:[0-5][0-9]");
	private Pattern c21 = Pattern.compile("[0-2]?[0-9]:[0-5][0-9]\\w*\\W*$");
	private Pattern c22 = Pattern.compile("\\w*:\\w*");
	private Pattern c23 = Pattern.compile("\\d*:\\d*");
	private Pattern c24 = Pattern.compile("[0-2]?[0-9]:[0-5][0-9]:[0-5][0-9]\\w*\\W*$");
	private Pattern c25 = Pattern.compile("^[-]*[0-9]{8}$");
	private Pattern c26 = Pattern.compile("^[0-9]{2}[:][0-9]{2}[:][0-9]{2}$");
	private Pattern c27 = Pattern.compile("^[a-zA-Z0-9]*[0-9]+[a-zA-Z0-9]*[-][a-zA-Z0-9]*[a-zA-Z0-9]*$");
	private Pattern c28 = Pattern.compile("^[a-zA-Z0-9]*[a-zA-Z0-9]*[-][a-zA-Z0-9]*[0-9]+[a-zA-Z0-9]*$");
	private Pattern c29 = Pattern.compile("\\d+[.,]*\\d+");
	private Pattern c30 = Pattern.compile("^[a-zA-Z]+[+/*-][a-zA-Z]+[+/*-][a-zA-Z]+$");
	private Pattern c31 = Pattern.compile("[+\\-*/]*");
	private Pattern c32 = Pattern.compile("^[0-9]{2}[:][0-9]{2}[:][0-9]{2}.*$");
	private Pattern c33 = Pattern.compile("[,<>_=@#$%|\\\\+*/^&~`(){}\\[\\]?\'\";:]*");
	private Pattern c34 = Pattern.compile("[,<>_=@#$%|\\\\+*/^&~`(){}\\[\\]?\'\";.]*");
	private Pattern c35 = Pattern.compile("[a-zA-z].*$");
	private Pattern c36 = Pattern.compile(".*'s$");
	private Pattern c37 = Pattern.compile(".*'m$");
	private Pattern c38 = Pattern.compile(".*'re$");
	private Pattern c39 = Pattern.compile(".*'ve$");
	private Pattern c40 = Pattern.compile(".*'d$");
	private Pattern c41 = Pattern.compile(".*'ll$");
	private Pattern c42 = Pattern.compile(".*'em$");
	private Pattern c43 = Pattern.compile(".*n't$");
	private Pattern c44 = Pattern.compile(".*'$");
	private Pattern c45 = Pattern.compile("'s$");
	private Pattern c46 = Pattern.compile("'m$");
	private Pattern c47 = Pattern.compile("'re$");
	private Pattern c48 = Pattern.compile("'ve$");
	private Pattern c49 = Pattern.compile("'d$");
	private Pattern c50 = Pattern.compile("'ll$");
	private Pattern c51 = Pattern.compile("'em$");
	private Pattern c52 = Pattern.compile("n't$");
	private Pattern c53 = Pattern.compile("'$");
	private Pattern c54 = Pattern.compile("^-?[0-9]\\d*(\\.\\d+)?$");
	private Pattern c55 = Pattern.compile("^[-]+|[-]+$");
	private Pattern c56 = Pattern.compile("^[a-zA-Z]*[0-9]+$");
	private Pattern c57 = Pattern.compile(".+[+/*%-].+");
	private Pattern c58 = Pattern.compile("[-]*-[-]*");
	private Pattern c59 = Pattern.compile("[!.?\']*$");
	private Pattern c60 = Pattern.compile("[']+");
	
	public static PatternMatcher getInstance(){
		return instance;
	}
	
	public boolean matchPattern(String string, int pattern){
		Matcher matcher = getPattern(pattern).matcher(string);
		if(matcher.matches()) return true;
		else return false;
	}
	
	public String replacePattern(String string, int pattern){
		Matcher matcher = getPattern(pattern).matcher(string);
		return matcher.replaceAll("");
	}
	
	public String replacePattern(String string, String replacement, int pattern){
		Matcher matcher = getPattern(pattern).matcher(string);
		return matcher.replaceAll(replacement);
	}
	
	private Pattern getPattern(int pattern){
		switch(pattern){
		case 0 : return c0;
		case 1 : return c1;
		case 2 : return c2;
		case 3 : return c3;
		case 4 : return c4;
		case 5 : return c5;
		case 6 : return c6;
		case 7 : return c7;
		case 8 : return c8;
		case 9 : return c9;
		case 10 : return c10;
		case 11 : return c11;
		case 12 : return c12;
		case 13 : return c13;
		case 14 : return c14;
		case 15 : return c15;
		case 16 : return c16;
		case 17 : return c17;
		case 18 : return c18;
		case 19 : return c19;
		case 20 : return c20;
		case 21 : return c21;
		case 22 : return c22;
		case 23 : return c23;
		case 24 : return c24;
		case 25 : return c25;
		case 26 : return c26;
		case 27 : return c27;
		case 28 : return c28;
		case 29 : return c29;
		case 30 : return c30;
		case 31 : return c31;
		case 32 : return c32;
		case 33 : return c33;
		case 34 : return c34;
		case 35 : return c35;
		case 36 : return c36;
		case 37 : return c37;
		case 38 : return c38;
		case 39 : return c39;
		case 40 : return c40;
		case 41 : return c41;
		case 42 : return c42;
		case 43 : return c43;
		case 44 : return c44;
		case 45 : return c45;
		case 46 : return c46;
		case 47 : return c47;
		case 48 : return c48;
		case 49 : return c49;
		case 50 : return c50;
		case 51 : return c51;
		case 52 : return c52;
		case 53 : return c53;
		case 54 : return c54;
		case 55 : return c55;
		case 56 : return c56;
		case 57 : return c57;
		case 58 : return c58;
		case 59 : return c59;
		case 60 : return c60;
		default : return cd;
		}
	}
}
