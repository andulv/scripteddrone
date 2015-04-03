package net.ulveseth.ScriptedDrone.web;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

public class WebUtils {
	public static void sendResponseAndClose(HttpExchange httpExchange, int responseCode, byte[] responseBody) throws IOException {
		httpExchange.getResponseHeaders().add("access-control-allow-origin", "*");
		httpExchange.sendResponseHeaders(responseCode, responseBody.length);
		OutputStream os=httpExchange.getResponseBody();
		os.write(responseBody);
		os.close();			
	}

}
