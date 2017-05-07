package com.projects.tcpserver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.tcpserver.webservice.mongodb.client.BackendWS;
import com.projects.tcpserver.webservice.mongodb.client.BackendWSService;
import com.projects.tcpserver.webservice.mongodb.client.CredentialsCheckException_Exception;

/**
 * Simple TCP Server, which spawns a thread for each client request. 
 * */
public final class TCPServer implements ITCPServer {
	private static final Logger logger = LoggerFactory.getLogger(TCPServer.class);
	
	private final ExecutorService es;
	private final ScheduledExecutorService ses; //this is only used in case the server would have to be automatically stopped after a given delay.
	private final int port;
	private final BackendWS backendWSPort;
	private final ArrayBlockingQueue<MessageContainer> messageQueue;
	private final List<MessageContainer> tempMessageList;
	private final int messageQueueDrainThreshold;
	private final int storeMessagesEveryXSeconds;
	private final int buildMessagesStatsEveryXSeconds;
	private volatile boolean isStopped;
	private ServerSocket serverSocket;
	

	public TCPServer(final Properties properties) throws MalformedURLException {
		es = Executors.newCachedThreadPool();
		ses = Executors.newSingleThreadScheduledExecutor();
		port = Integer.valueOf(properties.getProperty("serverPort"));
		backendWSPort = new BackendWSService(new URL(properties.getProperty("backendWSURL"))).getBackendWSPort();
		setWebServiceCredentials(backendWSPort, properties.getProperty("backendWSUsername"), properties.getProperty("backendWSPassword"));
		final Integer messageQueueSize = Integer.valueOf(properties.getProperty("messageQueueSize"));
		messageQueue = new ArrayBlockingQueue<MessageContainer>(messageQueueSize);
		tempMessageList = new ArrayList<>();
		messageQueueDrainThreshold = (int)(messageQueueSize*0.2);
		storeMessagesEveryXSeconds = Integer.valueOf(properties.getProperty("storeMessagesEveryXSeconds"));
		buildMessagesStatsEveryXSeconds = Integer.valueOf(properties.getProperty("buildMessagesStatsEveryXSeconds"));
	}
	
	/* (non-Javadoc)
	 * @see com.projects.server.ITCPgitServer#start()
	 */
	@Override
	public void start() throws IOException {
		serverSocket = new ServerSocket(port);
		logger.info("TCP Server started at port "+port);
		es.submit(() -> storeMessages());
		es.submit(() -> buildMessageStats());
		while(!isStopped())
			es.submit(new ClientRequestManager(serverSocket.accept(), messageQueue));
	}
	
	/* (non-Javadoc)
	 * @see com.projects.server.ITCPServer#stop()
	 */
	@Override
	public void stop() throws IOException, InterruptedException {
		this.isStopped = true;
		cleanUpServer();
	}
	
	/* (non-Javadoc)
	 * @see com.projects.server.ITCPServer#stopAfter(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void stopAfter(final long delay, final TimeUnit timeUnit) {
		ses.schedule(() -> {
			isStopped = true;
			logger.info("Stopping server...");
			try {
				cleanUpServer();
			} catch (final InterruptedException | IOException e) {
				logger.warn("Error stopping server", e);
				throw new RuntimeException(e);
			}
			}, delay, timeUnit); 
	}
	
	private void cleanUpServer() throws InterruptedException, IOException {
		serverSocket.close();
		Utility.shutdownExecutorService(es, 500, TimeUnit.MILLISECONDS);
		Utility.shutdownExecutorService(ses, 500, TimeUnit.MILLISECONDS);
	}
	
	public boolean isStopped() {
		return isStopped;
	}
	
	/**
	 * Every X (server.properties) seconds drains the message queue and stores its content to the MongoDB backend by a web service.
	 * It runs in a separate thread until the server is up.
	 * @throws CredentialsCheckException_Exception 
	 * */
	private void storeMessages()  {
		Thread.currentThread().setName("MessageStore-Thread");
		while(!isStopped()) {
			//logger.debug("messageQueue.remainingCapacity() "+messageQueue.remainingCapacity());
			if (messageQueue.remainingCapacity() <= messageQueueDrainThreshold) {
				messageQueue.drainTo(tempMessageList);
				try {
					backendWSPort.storeMessages(convertMessageContainerToJAXBType(tempMessageList));
				} catch (final CredentialsCheckException_Exception e) {
					logger.warn("Unable to store client messages due to failed credentials check.");
					break;
				}
				logger.debug("Stored "+tempMessageList.size()+" to backend database.");
				tempMessageList.clear();
			}
			try {
				TimeUnit.SECONDS.sleep(storeMessagesEveryXSeconds);
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 * It runs every X seconds (server.properties) and pulls out all messages from the MongoDB message store and builds some stats of out of them.
	 * It runs in a separate thread until the server is up.
	 * @throws CredentialsCheckException_Exception 
	 * */
	private void buildMessageStats() {
		Thread.currentThread().setName("MessageStats-Thread");
		while(!isStopped()) {
			final StringBuilder sb = new StringBuilder();
			List<com.projects.tcpserver.webservice.mongodb.client.MessageContainer> messages;
			try {
				messages = backendWSPort.getMessages(null);
			} catch (final CredentialsCheckException_Exception e) {
				logger.warn("Unable to read messages due to failed credentials check.");
				break;
			}
			sb.append("Total nr. messages: ").append(messages.size()).append("\n");
			final Map<String, Long> messagesBySenderMap = 
					messages.parallelStream().filter(m -> m.getSenderIdentifier() != null && m.getTargetIdentifier() != null).
					collect(Collectors.groupingBy(com.projects.tcpserver.webservice.mongodb.client.MessageContainer::getSenderIdentifier, Collectors.counting()));
			for(final Map.Entry<String, Long> messagesBySender: messagesBySenderMap.entrySet()) {
				sb.append("Messages sent by: "+messagesBySender.getKey()+", nr.: "+messagesBySender.getValue()+"\n");
			}
			logger.debug(sb.toString());
			try {
				TimeUnit.SECONDS.sleep(buildMessagesStatsEveryXSeconds);
			} catch (InterruptedException e) {}
		}
	}
	
	private static void setWebServiceCredentials(final BackendWS backendWSPort, final String username, final String password) {
		final Map<String, Object> requestContext = ((BindingProvider)backendWSPort).getRequestContext();
		final Map<String, List<String>> requestHeaders = new HashMap<String, List<String>>();
		requestHeaders.put(Utility.BackendWSUsernameHeader, Arrays.asList(username));
		requestHeaders.put(Utility.BackendWSPasswordHeader, Arrays.asList(password));
		requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, requestHeaders);
	}
	
	private static List<com.projects.tcpserver.webservice.mongodb.client.MessageContainer> convertMessageContainerToJAXBType(final List<MessageContainer> messages) {
		final List<com.projects.tcpserver.webservice.mongodb.client.MessageContainer> jaxbMessages = new ArrayList<>();
		messages.stream().forEach(m -> {
			com.projects.tcpserver.webservice.mongodb.client.MessageContainer jaxbMessage = new com.projects.tcpserver.webservice.mongodb.client.MessageContainer();
			jaxbMessage.setSenderIdentifier(m.getSenderIdentifier());
			jaxbMessage.setTargetIdentifier(m.getTargetIdentifier());
			jaxbMessage.setMessage(m.getMessage());
			try {
				jaxbMessage.setSendDate(Utility.getXMLGregorianCalendar(m.getSendDate()));
				jaxbMessages.add(jaxbMessage);
			} catch (final DatatypeConfigurationException e) {
				logger.warn("Failed to convert date to XMLGregorianCalendar", e);
			}
			
		});
		return jaxbMessages;
	}
}
