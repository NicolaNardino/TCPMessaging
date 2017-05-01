package com.projects.tcpserver;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Starts a TCP Server on a given port and listens to client requests. 
 * */
public final class TestServer {

	public static void main(final String[] args) throws IOException {
		final Properties properties = Utility.getApplicationProperties("server.properties");
		final TCPServer server = new TCPServer(properties);
		final Integer stopServerAfterSeconds = Integer.valueOf(properties.getProperty("stopServerAfterSeconds", "-1"));
		if (stopServerAfterSeconds != -1)
			server.stopAfter(stopServerAfterSeconds, TimeUnit.SECONDS);
		server.start();
	}
	
}
