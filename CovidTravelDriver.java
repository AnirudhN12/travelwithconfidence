package com.covid.basic;

import com.covid.server.CovidHttpServer;

/**
 * Entry point
 * @author anirudh
 *
 */
public class CovidTravelDriver {

	public static void main(String[] args) {
		// Start the HTTP Server
		try {
			CovidHttpServer server = new CovidHttpServer();		
			server.start();
		} catch(Throwable t) {
			throw new RuntimeException(t);
		}
		
	}
}
