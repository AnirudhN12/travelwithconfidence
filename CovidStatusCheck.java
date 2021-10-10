package com.covid.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.covid.basic.COVIDDataByState;
import com.covid.basic.CaseData;
import com.covid.basic.VaccinationData;

public class CovidStatusCheck {
	private static final int DAYS_TO_CHECK = 30;
	private static final int VACCINATION_THRESHOLD_GREEN = 90;
	private static final int VACCINATION_THRESHOLD_AMBER = 50;
	private static final int CONFIRMED_TREND_CHECK_DAYS_MAX = 7;
	private static final int TOTAL_ACTIVE_AMBER_THRESHOLD = 1;
	private static final int TOTAL_ACTIVE_RED_THRESHOLD = 2;
	private static final String TOTALACTIVE="TOTALACTIVE";
	private static final String TOTALVACCINATED="TOTALVACCINATED";
	private static final String DAILYCASEINCREASING="DAILYCASEINCREASING";
	private static final String RAGCOLOUR="RAGCOLOUR";
	
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
	public Map<String, String> check() {
		Map<String, String> result = new HashMap<>();
		result.put(RAGCOLOUR, RED_RESULT);
		result.put(DAILYCASEINCREASING, "");
		result.put(TOTALVACCINATED, "");
		
		totalActiveCheck(result);
		
		dailyIncreaseTrendCheck(result);		
		
		vaccinationCheck(result);

		/*
		 * TOTALACTIVE TOTALVACCINATED DAILYCASEINCREASING RAGCOLOUR
		 */
		return result;
	}

	/**
	 * Total active count check
	 * @param result
	 */
	private void totalActiveCheck(Map<String, String> result) {
		CaseData latestCaseData = getCovidByState().getCaseData().get(0);
		int totalActive = Math.abs(latestCaseData.getConfirmed() - (latestCaseData.getDeceased() + latestCaseData.getRecovered()));
		double totalActivePer = totalActive / getCovidByState().getPopulation();
		result.put(TOTALACTIVE, String.valueOf(totalActivePer));
		if(totalActivePer > TOTAL_ACTIVE_RED_THRESHOLD) {
			result.put(RAGCOLOUR, RED_RESULT);
		} else if(totalActivePer > TOTAL_ACTIVE_AMBER_THRESHOLD) {
			result.put(RAGCOLOUR, AMBER_RESULT);
		} else {
			result.put(RAGCOLOUR, GREEN_RESULT);
		}
		
	}	
	/**
	 * Checks daily increase to see an increasing trend
	 * @param result
	 */
	private void dailyIncreaseTrendCheck(Map<String, String> result) {
		if(!RED_RESULT.equals(result.get(RAGCOLOUR))) {
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
				result.put(RAGCOLOUR, RED_RESULT);
				result.put(DAILYCASEINCREASING, "true");
			}
		}
	}	
	/**
	 * Checks the vaccination %
	 */
	private void vaccinationCheck(Map<String, String> result) {
		if(!RED_RESULT.equals(result.get(RAGCOLOUR))) {
			// Vaccination % count check
			VaccinationData vd = getCovidByState().getVaccinationData().get(0);
			double percentageVaccinated = vd.getDosesAdministered() / getCovidByState().getPopulation();
			result.put(TOTALVACCINATED, String.valueOf(percentageVaccinated));
			if(percentageVaccinated > VACCINATION_THRESHOLD_GREEN) {
				result.put(RAGCOLOUR, GREEN_RESULT);
			} else  if (percentageVaccinated > VACCINATION_THRESHOLD_AMBER) {
				result.put(RAGCOLOUR, AMBER_RESULT);
			}
		}
	}



	/**
	 * @return the covidByState
	 */
	private COVIDDataByState getCovidByState() {
		return covidByState;
	}

}
