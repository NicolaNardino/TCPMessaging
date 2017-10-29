package com.projects.tcpserver.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.tcpserver.MessageType;
import com.projects.tcpserver.Utility;
import com.projects.tcpserver.restful.MessageRepositoryClientInterface;
import com.projects.tcpserver.restful.MessageRepositoryRestfulClient;

public final class TCPClient implements ITCPClient {
	private static final Logger logger = LoggerFactory.getLogger(TCPClient.class);
	
	private final String host;
	private final int port;
	private final String identifier;
	private final int upperBoundMessagesPerClient;
	private final MessageRepositoryClientInterface mrci;
	private Socket clientSocket;
	
	public TCPClient(final Properties properties, final String indentifier) {
		host = properties.getProperty("serverHost");
		port = Integer.valueOf(properties.getProperty("serverPort"));
		upperBoundMessagesPerClient = Integer.valueOf(properties.getProperty("upperBoundMessagesPerClient"));
		mrci = new MessageRepositoryRestfulClient(properties.getProperty("restfulBaseURL"), properties.getProperty("restfulBackendWSUsername"), properties.getProperty("restfulBackendWSPassword"));
		this.identifier = indentifier;
	}
	
	/**
	 * <ul>
	 * <li>Connect to the server.</li>
	 * <li>Send its identifier.</li>
	 * <li>Wait for the server confirmation that the communication can start.</li>
	 * <li>Send X messages, each addressing a given client.</li>
	 * <li>Get confirmation of messages delivery.</li>
	 * <li>Sends a disconnect message, indicating it finished its message sending.
	 * </ul>
	 * */
	@Override
	public void start() throws UnknownHostException, IOException {
		clientSocket = new Socket(host, port);
		logger.debug("Client "+identifier+" connected to server: "+host+"/ "+port);
		try (final BufferedReader inputStreamBufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				final PrintWriter outputStream = new PrintWriter(clientSocket.getOutputStream(), true)) {
			outputStream.println(identifier);
			final String connectionStatus = inputStreamBufferedReader.readLine();
			if (!connectionStatus.equals(MessageType.ClientConnected.getMessageDescription())) {
				logger.warn("Failed to connect, due to: "+connectionStatus);
				return;
			}
			//send messages, Target:<identifier>|<message>
			final String actualIdentifier = Utility.getSubString(identifier, MessageType.IDPrefix);
			IntStream.rangeClosed(1, Utility.getRandom(upperBoundMessagesPerClient)).forEach((i) -> {
				if (!identifier.equals(actualIdentifier))  {
					outputStream.println(MessageType.TargetIdentifier.getMessageDescription()+actualIdentifier+"|"+mrci.getMessage(actualIdentifier)); //avoid to send messages to sender.
					try {
						TimeUnit.MILLISECONDS.sleep(Utility.getRandom(999));//sleep time between each send message.
					} catch (final InterruptedException e) {}
				}
					
			});
			outputStream.println(MessageType.Disconnect.getMessageDescription());
			//receive replies from server.
			String line;
			while(!(line = inputStreamBufferedReader.readLine()).equals(MessageType.Disconnect.getMessageDescription())) {
				logger.debug("Received from server: "+line);
			}
		} 
		catch (final Exception e) {
			logger.warn("Error.", e);
		}
	}
	
	@Override
	public void close() throws Exception {
		clientSocket.close(); //closes input and output streams too.
		logger.debug("TCPClient "+identifier+" closed.");
	}
}
