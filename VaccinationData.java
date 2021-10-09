package com.covid.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Vaccination data
 * @author anirudh
 *
 */
public class VaccinationData {
	private static final String COVID19_VACCINATION_API_URL = "https://data.covid19india.org/csv/latest/cowin_vaccine_data_statewise.csv";	
	
	private final String state;
	private final int dosesAdministered;

	/**
	 * @param dosesAdministered
	 */
	public VaccinationData(int dosesAdministered_, String state_) {
		super();
		this.dosesAdministered = dosesAdministered_;
		this.state = state_;
	}
	/**
	 * @return the dosesAdministered
	 */
	public int getDosesAdministered() {
		return dosesAdministered;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}	
	@Override
	public String toString() {
		return "VaccinationData [state=" + state + ", dosesAdministered=" + dosesAdministered + "]";
	}
	/**
	 * Initialises the vaccination data
	 * @throws IOException
	 */
	public static List<VaccinationData>	initialiseVaccinationData(final String state) throws IOException {
		List<VaccinationData> vaccinationData = new ArrayList<>();
		try(BufferedReader reader = CovidAPIInvoker.invoke(COVID19_VACCINATION_API_URL)) {
			String line = null;
			reader.readLine(); // Skip the header
			while((line = reader.readLine()) != null) {
				String[] fields = line.split(",");
				if(fields.length >= 3) {
					if(state.equalsIgnoreCase(fields[1]) && (fields[2]!=null && !fields[2].trim().isEmpty())) {
						vaccinationData.add(new VaccinationData(Integer.parseInt(fields[2]), fields[0]));
					}
				}
			}
		}
		return vaccinationData;
	}

	

}
