package com.covid.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.covid.basic.COVIDDataByState;
import com.covid.basic.CaseData;
import com.covid.basic.VaccinationData;

public class CovidStatusCheck {
	private static final int DAYS_TO_CHECK = 30;
	private static final int VACCINATION_THRESHOLD_GREEN = 60;
	private static final int VACCINATION_THRESHOLD_AMBER = 40;
	private static final int CONFIRMED_TREND_CHECK_DAYS_MAX = 7;
	private static final int TOTAL_ACTIVE_AMBER_THRESHOLD = 5;
	private static final int TOTAL_ACTIVE_RED_THRESHOLD = 10;
	
	private static final String GREEN_RESULT = "<b>GREEN</b>";
	private static final String AMBER_RESULT = "<b>AMBER</b>";
	private static final String RED_RESULT = "<b>RED</b>";
	
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
		String result = RED_RESULT;
		
		result = totalActiveCheck();
		
		result = dailyIncreaseTrendCheck(result);		
		
		result = vaccinationCheck(result);
	
		return result;
	}

	/**
	 * Total active count check
	 * @return R/A/G
	 */
	private String totalActiveCheck() {
		CaseData latestCaseData = getCovidByState().getCaseData().get(0);
		int totalActive = latestCaseData.getConfirmed() - (latestCaseData.getDeceased() + latestCaseData.getRecovered());
		if(totalActive / getCovidByState().getPopulation() > TOTAL_ACTIVE_RED_THRESHOLD) {
			return RED_RESULT;
		} else if(totalActive / getCovidByState().getPopulation() > TOTAL_ACTIVE_AMBER_THRESHOLD) {
			return AMBER_RESULT;
		} else {
			return GREEN_RESULT;
		}
		
	}	
	/**
	 * Checks daily increase to see an increasing trend
	 * @param result
	 * @return red/passed in 
	 */
	private String dailyIncreaseTrendCheck(String result) {
		if(!RED_RESULT.equals(result)) {
			List<CaseData> caseData = getCovidByState().getCaseData();
			
			List<Integer> increaseInCases = new ArrayList<>();
			for(int index = 0; index < CONFIRMED_TREND_CHECK_DAYS_MAX - 1; index++) {
				int confirmed = caseData.get(index).getConfirmed();
				int confirmedDayOld = caseData.get(index+1).getConfirmed();
				increaseInCases.add(confirmed - confirmedDayOld);
			}
	
			List<Integer> reversedIncreaseInCases = new ArrayList<>(increaseInCases);
			Collections.sort(reversedIncreaseInCases, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o1 > o2?-1:0;
				}
			});
			
			if(increaseInCases.equals(reversedIncreaseInCases)) {
				return RED_RESULT;
			}
		}
		return result;
	}	
	/**
	 * Checks the vaccination %
	 * @return Red/Amber/Green
	 */
	private String vaccinationCheck(String result) {
		if(!RED_RESULT.equals(result)) {
			// Vaccination % count check
			VaccinationData vd = getCovidByState().getVaccinationData().get(0);
			int percentageVaccinated = vd.getDosesAdministered() / getCovidByState().getPopulation(); 
			if(percentageVaccinated > VACCINATION_THRESHOLD_GREEN) {
				return GREEN_RESULT;
			} else  if (percentageVaccinated > VACCINATION_THRESHOLD_AMBER) {
				return AMBER_RESULT;
			}
		}
		return result;
	}



	/**
	 * @return the covidByState
	 */
	private COVIDDataByState getCovidByState() {
		return covidByState;
	}

}
