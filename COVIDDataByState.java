package com.covid.basic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data for a state
 * @author anirudh
 *
 */
public class COVIDDataByState {
	
	
	private final String state;
	private final int noOfDays;
	private final List<CaseData> caseDataStack;
	private final List<VaccinationData> vaccinationDataLst;

	/**
	 * Constructor
	 * @param state_
	 * @param noOfdays_
	 */
	public COVIDDataByState(String state_, int noOfdays_) {
		this.noOfDays = noOfdays_;
		this.state = state_;
		
		try {
			this.caseDataStack = CaseData.initialiseCaseData(getState());
			this.vaccinationDataLst = VaccinationData.initialiseVaccinationData(getState());
		} catch (IOException e) {
			throw new RuntimeException("Error while invoking Covid19 API", e);
		}
	}
	/**
	 * The required data for the input number of days 
	 * @return
	 */
	public List<CaseData> getCaseData() {
		List<CaseData> dataLst = new ArrayList<>();
		int count = 0;
		for(int index = getCaseDataStack().size() - 1; index >= 0; index--) {
			dataLst.add(getCaseDataStack().get(index));
			count++;
			if(count > getNoOfDays()) {
				break;
			}
		}	
		return dataLst;
	}
	/**
	 * Vaccination data
	 * @return list of vaccination date
	 */
	public List<VaccinationData> getVaccinationData() {
		List<VaccinationData> dataLst = new ArrayList<>();
		for(int index = getVaccinationDataLst().size() - 1; index >= 0; index--) {
			dataLst.add(getVaccinationDataLst().get(index));
		}
		return dataLst;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for(int index = getCaseDataStack().size() - 1; index >= 0; index--) {
			builder.append(getCaseDataStack().get(index)).append("\n");	
		}
		return builder.toString();
	}


	/**
	 * @return the state
	 */
	private String getState() {
		return state;
	}
	/**
	 * @return the noOfDays
	 */
	private int getNoOfDays() {
		return noOfDays;
	}
	/**
	 * @return the caseDataStack
	 */
	private List<CaseData> getCaseDataStack() {
		return caseDataStack;
	}
	/**
	 * @return the vaccinationData
	 */
	private List<VaccinationData> getVaccinationDataLst() {
		return vaccinationDataLst;
	}
}
