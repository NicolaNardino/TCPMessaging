package com.projects.tcpserver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.tcpserver.webservice.mongodb.client.BackendWSService;

/**
 * Simple TCP Server, which spawns a thread for each client request. 
 * */
public final class TCPServer implements ITCPServer {
	private static final Logger logger = LoggerFactory.getLogger(TCPServer.class);
	
	private final ExecutorService es;
	private final ScheduledExecutorService ses; //this is only used in case the server would have to be automatically stopped after a given delay.
	private final int port;
	private final BackendWSService backendWS;
	private final ArrayBlockingQueue<MessageContainer> messageQueue;
	private final List<MessageContainer> tempMessageList;
	private final int messageQueueDrainThreshold;
	private volatile boolean isStopped;
	private ServerSocket serverSocket;
	

	public TCPServer(final Properties properties) throws MalformedURLException {
		es = Executors.newCachedThreadPool();
		ses = Executors.newSingleThreadScheduledExecutor();
		port = Integer.valueOf(properties.getProperty("port"));
		backendWS = new BackendWSService(new URL(properties.getProperty("backendWSURL")));
		final Integer messageQueueSize = Integer.valueOf(properties.getProperty("messageQueueSize"));
		messageQueue = new ArrayBlockingQueue<MessageContainer>(messageQueueSize);
		tempMessageList = new ArrayList<>();
		messageQueueDrainThreshold = (int)(messageQueueSize*0.2);
	}
	
	/* (non-Javadoc)
	 * @see com.projects.server.ITCPgitServer#start()
	 */
	@Override
	public void start() throws IOException {
		serverSocket = new ServerSocket(port);
		logger.info("TCP Server started at port "+port);
		es.submit(() -> storeMessages());
		while(!isStopped())
			es.submit(new ClientRequestManager(serverSocket.accept(), messageQueue));
	}
	
	private void storeMessages() {
		Thread.currentThread().setName("MessageStore-Thread");
		while(!isStopped()) {
			//logger.debug("messageQueue.remainingCapacity() "+messageQueue.remainingCapacity());
			if (messageQueue.remainingCapacity() <= messageQueueDrainThreshold) {
				messageQueue.drainTo(tempMessageList);
				try {
					backendWS.getBackendWSPort().storeMessages(convertMessageContainerToJAXBType(tempMessageList));	
				}
				catch(Exception e) {
					logger.warn("Error while storing messages", e);
				}
				logger.debug("Stored "+tempMessageList.size()+" to backend database.");
				tempMessageList.clear();
			}
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {}
		}
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
