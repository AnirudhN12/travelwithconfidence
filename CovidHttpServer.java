package com.covid.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.covid.metrics.CovidStatusCheck;
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
			    	CovidStatusCheck csc = new CovidStatusCheck(state);
			    	String checkStatus = csc.check();
		    		os.write(checkStatus.getBytes());
			    }
			    os.close();
			}
		}
	}

}

