package edu.buffalo.cse.irf14.analysis;

import java.util.Arrays;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFilter extends TokenFilter {
	private final String[] mon = { "apr", "aug", "dec", "feb", "jan", "jul",
			"jun", "mar", "may", "nov", "oct", "sep" };
	private final String[] months = { "april", "august", "december",
			"february", "january", "july", "june", "march", "may", "november",
			"october", "september" };
	private final String[] monthNumber = { "04", "08", "12", "02", "01", "07",
			"06", "03", "05", "11", "10", "09" };
	private final String[] yearType = { "AD", "BC" };
	private final String[] timePeriod = { "AM", "PM" };
	private final String[] timeZone = { "GMT", "IST", "UTC" };
	PatternMatcher matcher = PatternMatcher.getInstance();

	public DateFilter(TokenStream stream) {
		super(stream);
	}


	@Override
	public boolean increment() throws TokenizerException {
		if (incoming.hasNext()) {
			incoming.next();
			try {
				DateLogic();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	public void DateLogic() throws ParseException {
		if (incoming.getCurrent() != null) {
			Token firstToken;
			Token secondToken;
			Token thirdToken;
			int num = 0;
			int year = 1900;
			String month = "01";
			int day = 1;
			int hh = 0;
			int mm = 0;
			int ss = 0;
			int isMonth = 0;
			int isYearType = 0;
			int isTimePeriod = 0;
			int flag = 0;
			int flag2 = 0;
			String date = "";
			String time = "";
			String special = "";
			String secondText = "";
			String thirdText = "";
			// date in YYYYMMDD format
			firstToken = incoming.getCurrent();
			String init = new String();
			init = firstToken.toString().trim();
			init = init.replaceAll("//W*$", "");

			// If First string is day
			if (init.matches("[0-9]+")) {
				secondToken = incoming.next();
				if (secondToken != null) {
					secondText = secondToken.toString().trim();
					secondText = matcher.replacePattern(secondText, 3);
					isMonth = Arrays.binarySearch(months,
							secondText.toLowerCase());
					if (isMonth < 0) {
						isMonth = Arrays.binarySearch(mon,
								secondText.toLowerCase());
					}
					isTimePeriod = Arrays.binarySearch(timePeriod, secondText.toUpperCase());
					// If second string is month
					if ((!(Integer.parseInt(init) > 31)) && isMonth >= 0) {
						day = Integer.parseInt(init);
						month = monthNumber[isMonth];
						thirdToken = incoming.next();
						if (thirdToken != null) {
							thirdText = thirdToken.toString().trim();
							thirdText = matcher.replacePattern(thirdText, 3);
							if (matcher.matchPattern(thirdText, 4)) {
								year = Integer.parseInt(thirdText.trim());
								String temp = String.format("%04d", year);
								date = temp + month + (day < 10 ? "0" : "")
										+ Integer.toString(day);

								incoming.remove();
								incoming.previous();
								incoming.remove();
								incoming.previous();
								firstToken.setTermText(date.trim());
							} else {
								String temp = String.format("%04d", year);
								date = temp + month + (day < 10 ? "0" : "")
										+ Integer.toString(day);
								incoming.previous();
								incoming.remove();
								firstToken.setTermText(date.trim());
							}
						} else {
							String temp = String.format("%04d", year);
							date = temp + month + (day < 10 ? "0" : "")
									+ Integer.toString(day);
							incoming.previous();
							incoming.remove();
							firstToken.setTermText(date.trim());
						}

					}

					// change this
					// change
					else if (matcher.matchPattern(init, 5)) {
						year = Integer.parseInt(init);
						special = matcher.replacePattern(init, 6);
						isYearType = Arrays.binarySearch(yearType, secondText.toUpperCase());
						if (matcher.matchPattern(init, 7)) {
							String temp = String.format("%04d", year);
							date = temp + month + (day < 10 ? "0" : "")
									+ Integer.toString(day) + special;
							firstToken.setTermText(date.trim());
						}

						if ((isYearType) == 0) {
							String temp = String.format("%04d", year);
							date = temp + month + (day < 10 ? "0" : "")
									+ Integer.toString(day) + special;
							incoming.remove();
							incoming.previous();
							firstToken.setTermText(date.trim());
						} else if ((isYearType) == 1) {
							String temp = String.format("%04d", year);
							date = "-" + temp + month + (day < 10 ? "0" : "")
									+ Integer.toString(day) + special;
							incoming.remove();
							incoming.previous();
							firstToken.setTermText(date.trim());
						} else {
							incoming.previous();
						}
					} else {
						incoming.previous();
					}
				} else {
					incoming.previous();
				}
			}
			// if string is year-year
			else if (matcher.matchPattern(init, 8)) {
				int year1 = 0;
				int year2 = 0;
				String s1 = matcher.replacePattern(init, 9);
				int len1 = s1.length();
				year1 = Integer.parseInt(s1);
				special = matcher.replacePattern(init, 10);
				String temp = matcher.replacePattern(init, 11);
				String s2 = matcher.replacePattern(temp, 12);
				String date1, date2;
				int len2 = s2.length();
				if (len2 < len1) {
					String s3 = s1.substring(0, len1 - len2);
					s3 = s3 + s2;
					year2 = Integer.parseInt(s3);
					date1 = s1 + month + (day < 10 ? "0" : "")
							+ Integer.toString(day);
					date2 = s3 + month + (day < 10 ? "0" : "")
							+ Integer.toString(day);
					date = date1 + "-" + date2 + special;
					// System.out.println("date "+date);
					firstToken.setTermText(date.trim());
				} else {
					date1 = s1 + month + (day < 10 ? "0" : "")
							+ Integer.toString(day);
					date2 = s2 + month + (day < 10 ? "0" : "")
							+ Integer.toString(day);
					date = date1 + "-" + date2 + special;
					// System.out.println("date "+date);
					firstToken.setTermText(date.trim());
				}
			}
			// More fine tuning required.
			else if (matcher.matchPattern(init, 13)) {
				year = Integer.parseInt(matcher.replacePattern(init, 14));
				special = matcher.replacePattern(init, 6);
				String temp = matcher.replacePattern(init, 15);
				temp = matcher.replacePattern(temp, 16);
				isYearType = Arrays.binarySearch(yearType, temp.toUpperCase());
				if (isYearType == 0) {
					temp = String.format("%04d", year);
					date = temp + month + (day < 10 ? "0" : "")
							+ Integer.toString(day) + special;
					firstToken.setTermText(date.trim());
				} else if (isYearType == 1) {
					temp = String.format("%04d", year);
					date = "-" + temp + month + (day < 10 ? "0" : "")
							+ Integer.toString(day) + special;
					// incoming.remove();
					firstToken.setTermText(date.trim());
				}
			}
			// If first string is month
			else if ((isMonth = Arrays.binarySearch(months, init.toLowerCase())) >= 0
					|| (isMonth = Arrays.binarySearch(mon, init.toLowerCase())) >= 0) {
				month = monthNumber[isMonth];
				secondToken = incoming.next();
				if (secondToken != null) {
					secondText = secondToken.toString().trim();
					secondText = matcher.replacePattern(secondText, 3);
					if (matcher.matchPattern(secondText, 17)) {
						secondText = matcher.replacePattern(secondText, 3);
						if (Integer.parseInt(secondText) < 31) {
							day = Integer.parseInt(secondText);
							thirdToken = incoming.next();
							if (thirdToken != null) {
								thirdText = thirdToken.toString().trim();
								flag2 = 1;
								special = matcher.replacePattern(thirdText, 6);
								thirdText = matcher.replacePattern(thirdText, 3);
								if (matcher.matchPattern(thirdText, 18)) {
									year = Integer.parseInt(thirdText.trim());
									String temp = String.format("%04d", year);
									date = temp + month + (day < 10 ? "0" : "")
											+ Integer.toString(day) + special;
									incoming.remove();
									incoming.previous();
									incoming.remove();
									firstToken.setTermText(date.trim());
								} else {
									incoming.previous();
									incoming.remove();
									date = year + month + (day < 10 ? "0" : "")
											+ Integer.toString(day) + special;
									firstToken.setTermText(date.trim());
								}
							} else {
								incoming.previous();
								incoming.remove();
								String temp = String.format("%04d", year);
								date = temp + month + (day < 10 ? "0" : "")
										+ Integer.toString(day) + special;
								firstToken.setTermText(date.trim());
							}
						} else {
							incoming.previous();
							String temp = String.format("%04d", year);
							date = temp + month + (day < 10 ? "0" : "")
									+ Integer.toString(day) + special;
							firstToken.setTermText(date.trim());
						}
					} else {
						incoming.previous();
						String temp = String.format("%04d", year);
						date = temp + month + (day < 10 ? "0" : "")
								+ Integer.toString(day) + special;
						firstToken.setTermText(date.trim());
					}
				} else {
					incoming.previous();
					String temp = String.format("%04d", year);
					date = temp + month + (day < 10 ? "0" : "")
							+ Integer.toString(day) + special;
					firstToken.setTermText(date.trim());
				}
			}
			// If string is time, in HH:MM format
			if (matcher.matchPattern(init, 19)) {
				String hours = null;
				String min = null;
				String sec = null;
				int pos = 0;
				if (matcher.matchPattern(init, 20)) {
					pos = init.indexOf(":");
					hours = init.substring(0, pos);
					min = init.substring(pos + 1, pos + 3);
					hh = Integer.parseInt(hours);
					if (incoming.hasNext()) {
						secondToken = incoming.next();
						secondText = secondToken.toString().trim();
						flag = 1;
						special = matcher.replacePattern(secondText, 6);
						secondText = matcher.replacePattern(secondText, 3)
								.toUpperCase();
					}
				} else if (matcher.matchPattern(init, 21)) {
					pos = init.indexOf(":");
					hours = init.substring(0, pos);
					min = init.substring(pos + 1, pos + 3);
					special = matcher.replacePattern(init, 22);
					init = matcher.replacePattern(init, 3);
					secondText = matcher.replacePattern(init, 23);
				} else if (matcher.matchPattern(init, 24)) {
					pos = init.indexOf(":");
					hours = init.substring(0, pos);
					min = init.substring(pos + 1, pos + 3);
					sec = init.substring(pos + 4, pos + 6);
					special = matcher.replacePattern(init, 22);
					init = matcher.replacePattern(init, 3);
					secondText = matcher.replacePattern(init, 23);
				}
				hh = Integer.parseInt(hours);
				hours = String.format("%02d", hh);
				mm = Integer.parseInt(min);
				min = String.format("%02d", mm);
				sec = String.format("%02d", ss);
				if ((Arrays.binarySearch(timePeriod, secondText.toUpperCase())) == 0) {

					sec = String.format("%02d", ss);
					time = hours + ":" + min + ":" + sec + special;
					firstToken.setTermText(time.trim());
					if (flag == 1) {
						incoming.remove();
					}
				} else if ((Arrays.binarySearch(timePeriod,
						secondText.toUpperCase())) > 0) {
					SimpleDateFormat displayFormat = new SimpleDateFormat(
							"HH:mm:ss");
					SimpleDateFormat parseFormat = new SimpleDateFormat(
							"hh:mm:ss a");
					time = hours + ":" + min + ":" + sec + " PM";
					Date temp = parseFormat.parse(time);
					time = displayFormat.format(temp) + special;
					firstToken.setTermText(time.trim());
					if (flag == 1) {
						incoming.remove();
					}
				} else if ((Arrays.binarySearch(timeZone,
						secondText.toUpperCase())) > 0) {
					SimpleDateFormat displayFormat = new SimpleDateFormat(
							"HH:mm:ss");
					SimpleDateFormat parseFormat = new SimpleDateFormat(
							"hh:mm:ss");
					time = hours + ":" + min + ":" + sec;
					Date temp = parseFormat.parse(time);
					firstToken.setTermText(time.trim());
					if (flag == 1) {
						incoming.remove();
					}
				} else if ((Arrays.binarySearch(timeZone,
						secondText.toUpperCase())) == 0) {
					SimpleDateFormat displayFormat = new SimpleDateFormat(
							"HH:mm:ss");
					SimpleDateFormat parseFormat = new SimpleDateFormat(
							"hh:mm:ss");
					time = hours + ":" + min + ":" + sec;
					Date temp = parseFormat.parse(time);
					firstToken.setTermText(time.trim());
					if (flag == 1) {
						incoming.remove();
					}
				}

			}
		}
	}
}
