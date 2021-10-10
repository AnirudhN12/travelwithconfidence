package com.covid.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

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
	
	private static final String TABLE_HIDDEN="table, th, td { visibility: hidden }"; 
	
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
				state = state.trim().replace("+", " ");
				System.out.println(state);
			}
			try(BufferedReader reader= Files.newBufferedReader(Paths.get("src/com/covid/server/website.html"))) {
				StringBuilder builder = new StringBuilder();
				String line = null;
				while((line = reader.readLine()) != null) {
					builder.append(line);
				}
				String html = builder.toString();
			    if(state != null) {
			    	final String optionState = "<option value =\"" + state + "\">" + state + "</option>";
			    	final String optionStateSelected = "<option value =\"" + state + "\" selected=\"true\">" + state + "</option>";
			    	html = html.replace(optionState, optionStateSelected);			    	
			    	CovidStatusCheck csc = new CovidStatusCheck(state);
			    	Map<String, String> checkStatus = csc.check();
		    		for(String key: checkStatus.keySet()) {
		    			html = html.replace(key, checkStatus.get(key));
		    		}
			    } else {
			    	int endOfStyle = html.indexOf("</style>");
			    	html = html.substring(0, endOfStyle) + TABLE_HIDDEN + html.substring(endOfStyle);
			    }
			    exchange.sendResponseHeaders(200, 0);
			    OutputStream os = exchange.getResponseBody();
			    os.write(html.getBytes());			    
			    os.close();
			}
		}
	}

}

