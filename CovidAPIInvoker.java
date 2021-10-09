package com.covid.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Utility to invoke the COVID API
 * @author anirudh
 *
 */
public class CovidAPIInvoker {
	
	public static BufferedReader invoke(String uri) {
		
		HttpClient client = HttpClient.newHttpClient();
		
		HttpRequest request = HttpRequest.newBuilder(URI.create(uri)).
							header("accept", uri.endsWith("csv")?"application/csv":"application/json").build();
		try {
			HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
			return new BufferedReader(new InputStreamReader(response.body()));
			
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException("Error while invoking the API ", e);
		}

	}

}
