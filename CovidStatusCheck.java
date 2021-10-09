package com.covid.metrics;

import com.covid.basic.COVIDDataByState;
import com.covid.basic.CaseData;

public class CovidStatusCheck {
	private static final int DAYS_TO_CHECK = 30;
	
	private final COVIDDataByState covidByState;
	
	/**
	 * Constructor
	 * @param state_
	 */
	public CovidStatusCheck(String state_) {
		this.covidByState = new COVIDDataByState(state_, DAYS_TO_CHECK);
	}
	/**
	 * Checks COVID data for status across Red, Green and Amber 
	 * @return
	 */
	public String check() {
		CaseData curr = getCovidByState().getCaseData().get(0);
		CaseData prev = getCovidByState().getCaseData().get(1);
		int diff = curr.getConfirmed() - prev.getConfirmed();
		if(diff > 10000) {
			return "<b>RED</b>";
		} else if(diff > 5000) {
			return "<b>AMBER</b>";
		} else {
			return "<b>GREEN</b>";
		}
	}

	/**
	 * @return the covidByState
	 */
	private COVIDDataByState getCovidByState() {
		return covidByState;
	}

}
