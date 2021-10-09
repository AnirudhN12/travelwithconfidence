package com.covid.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Case Data
 * @author anirudh
 *
 */
public class CaseData {
	private static final String COVID19_API_URL = "https://data.covid19india.org/csv/latest/states.csv";
	
	private final String date;
	private final int confirmed;
	private final int recovered;		
	private final int deceased;
	private final int tested;
	/**
	 * @param date
	 * @param confirmed
	 * @param recovered
	 * @param deceased
	 * @param tested
	 */
	public CaseData(String date, int confirmed, int recovered, int deceased, int tested) {
		super();
		this.date = date;
		this.confirmed = confirmed;
		this.recovered = recovered;
		this.deceased = deceased;
		this.tested = tested;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @return the confirmed
	 */
	public int getConfirmed() {
		return confirmed;
	}
	/**
	 * @return the recovered
	 */
	public int getRecovered() {
		return recovered;
	}
	/**
	 * @return the deceased
	 */
	public int getDeceased() {
		return deceased;
	}
	/**
	 * @return the tested
	 */
	public int getTested() {
		return tested;
	}
	@Override
	public String toString() {
		return "CaseData [date=" + getDate() + ", confirmed=" + getConfirmed() + ", recovered=" + getRecovered() + ", deceased="
				+ getDeceased() + ", tested=" + getTested() + "]";
	}

	
	public static List<CaseData> initialiseCaseData(final String state) throws IOException {
		List<CaseData> caseDataLst = new ArrayList<>();
		try(BufferedReader reader = CovidAPIInvoker.invoke(COVID19_API_URL)) {
			String line = null;
			reader.readLine(); // Skip the header
			while((line = reader.readLine()) != null) {
				String[] fields = line.split(",");
				if(state.equalsIgnoreCase(fields[1])) {
					int tested = -1;
					if(fields.length == 7) {
						tested = Integer.parseInt(fields[6]);
					}
					caseDataLst.add(new CaseData(fields[0], Integer.parseInt(fields[2]), Integer.parseInt(fields[3]), Integer.parseInt(fields[4]), tested));
				}
			}
		}
		return caseDataLst;
	}

}