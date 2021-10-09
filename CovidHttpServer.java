package com.covid.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.covid.basic.COVIDDataByState;
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
			context.setHandler(new HttpHandler() {
				@Override
				public void handle(HttpExchange exchange) throws IOException {
					StringBuilder builder = new StringBuilder();
					builder.append("Travel with confidence @ " + exchange.getRequestURI()).append("\n");
					COVIDDataByState sd = new COVIDDataByState("Karnataka", 30);
					builder.append(sd.getCaseData()).append("\n");
					builder.append(sd.getVaccinationData()).append("\n");
				    exchange.sendResponseHeaders(200, builder.toString().getBytes().length);
				    OutputStream os = exchange.getResponseBody();
				    os.write(builder.toString().getBytes());
				    os.close();					
				}
			});
		} catch (IOException e) {
			throw new RuntimeException("Error creating HTTP Server", e);
		}
	}
	
	public void start() {
		this.httpServer.start();
	}
	

	public static void main(String[] args) {
		CovidHttpServer server = new CovidHttpServer();
		server.start();
	}

}
