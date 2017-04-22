package com.projects.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.server.utility.MessageType;
import com.projects.server.utility.Utility;

/**
 * It manages client requests, while keeping track of connected clients. 
 * 
 * */
public final class ClientRequestManager implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ClientRequestManager.class);
	private static final ConcurrentHashMap<String, Socket> Clients = new ConcurrentHashMap<>();
	private final Socket clientSocket;
	
	public ClientRequestManager(final Socket clientSocket) {
		 this.clientSocket = clientSocket;
	}
	
	/**
	 * Deals with each client request.
	 * 
	 * <ul>
	 * 	<li>If the first client message doesn't contain an identifier, it stops processing the client request.</li>
	 *  <li>If the client, based on its identifier, is already connected, it stops processing the client request.</li>
	 *  <li>If none of the above happened, it sends a ClientConnected message, indicating that the client can start sending messages.</li>
	 *  <li>It waits for client messages until a client disconnect one. For each of those, it expects a format like "Target:identifier|message".
	 *      So, it delivers the message to the given connected client. If the target client isn't connected, then it skips sending the message.</li>
	 *  <li>For each delivered message, it sends an acknowledgment back to the sender.</li>
	 *  <li>Upon receiving a client disconnect message, it terminates processing the client request and send a disconnect message, so that the client can 
	 *  terminate listening to server's messages.</li>
	 *  <li>Finally, it removes the client from the in-memory tracking structure.</li>
	 * </ul>
	 * */
	@Override
	public void run() {
		logger.info("Processing client request...");
		try (final BufferedReader inputStreamBufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				final PrintWriter outputStreamPrintWriter = new PrintWriter(clientSocket.getOutputStream(), true)) {
			final String identifier = inputStreamBufferedReader.readLine();
			if (!identifier.startsWith(MessageType.IDPrefix.getMessageDescription())) {//ID:<identifier>
				logger.info("No client identifier received");
				outputStreamPrintWriter.println(MessageType.MissingIdentifier.getMessageDescription()+". Please, send a client identification message with format 'ID:<identifier>'.");
				return;
			}
			final String clientIdentifier = Utility.getSubString(identifier, MessageType.IDPrefix);
			if (Clients.putIfAbsent(clientIdentifier, clientSocket) != null) {
				outputStreamPrintWriter.println(MessageType.ClientAlreadyConnected.getMessageDescription());
				return;
			}
			outputStreamPrintWriter.println(MessageType.ClientConnected.getMessageDescription());
			logger.info("Managing request of client: "+clientIdentifier);
			String line;//Target:<identifier>|<message>
			while(!(line = inputStreamBufferedReader.readLine()).equals(MessageType.Disconnect.getMessageDescription())) {
				if (line.startsWith(MessageType.TargetIdentifier.getMessageDescription())) {
					logger.info("Received from client: "+line);
					final ChatMessage chatMessage = ChatMessage.build(line);
					final Socket targetSocket = Clients.get(chatMessage.getTargetIdentifier());
					if (targetSocket == null) {
						logger.warn("Client "+chatMessage.getTargetIdentifier()+" not connected.");
						continue;
					}
					final PrintWriter targetPrintWriter = new PrintWriter(targetSocket.getOutputStream(), true);
					targetPrintWriter.println(MessageType.Sender.getMessageDescription()+clientIdentifier+"/ "+MessageType.ClientMessage.getMessageDescription()+chatMessage.getMessage());
					logger.info("Sent message to "+chatMessage.getTargetIdentifier()+": "+chatMessage.getMessage());
				}
			}
			outputStreamPrintWriter.println(MessageType.Disconnect.getMessageDescription());
			Clients.remove(clientIdentifier);
		} 
		catch (final Exception e) {
			logger.warn("Error.", e);
		}
	}
	
	private static class ChatMessage {
		
		private final String targetIdentifier;
		private final String message;
		
		public ChatMessage(String targetIdentifier, String message) {
			super();
			this.targetIdentifier = targetIdentifier;
			this.message = message;
		}
		
		public String getTargetIdentifier() {
			return targetIdentifier;
		}
		public String getMessage() {
			return message;
		}
		
		public static ChatMessage build(final String rawMessage) {
			final String temp[] = rawMessage.split(Pattern.quote("|"));
			final String targetIdentifier = Utility.getSubString(temp[0], MessageType.TargetIdentifier);
			return new ChatMessage(targetIdentifier, temp[1]);
		}
	}
}
