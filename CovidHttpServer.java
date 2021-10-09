package com.covid.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

import com.covid.basic.CaseData;
import com.covid.basic.VaccinationData;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Simple HTTP Server
 * @author anirudh
 *
 */
public class CovidHttpServer {
	
	private final HttpServer httpServer; 
	
	public CovidHttpServer() {
		try {
			this.httpServer = HttpServer.create(new InetSocketAddress(8500), 10);
			HttpContext context = this.httpServer.createContext("/twc");
			context.setHandler(new HttpRequestHandler());
		} catch (IOException e) {
			throw new RuntimeException("Error creating HTTP Server", e);
		}
	}
	
	public void start() {
		this.httpServer.start();
	}
	
	private class HttpRequestHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String state = null;
			if(exchange.getRequestURI().toString().contains("?")) {
				state =
						exchange.getRequestURI().toString().
							split("\\?")[1].
							split("=")[1];
				System.out.println(state);
			}
			try(FileInputStream fis = new FileInputStream("src/com/covid/server/website.html")) {
			    exchange.sendResponseHeaders(200, 0);
			    OutputStream os = exchange.getResponseBody();
			    os.write(fis.readAllBytes());
			    if(state != null) {
			    	getCOVIDData(state, os);
			    }
			    os.close();
			}
		}
		private void getCOVIDData(String state, OutputStream os) {
			try {
				List<CaseData> caseData = CaseData.initialiseCaseData(state);
				List<VaccinationData> vaccinationData = VaccinationData.initialiseVaccinationData(state);
				os.write(caseData.get(0).toString().getBytes());
				os.write(vaccinationData.get(0).toString().getBytes());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void main(String[] args) {
		CovidHttpServer server = new CovidHttpServer();
		server.start();
	}

}

