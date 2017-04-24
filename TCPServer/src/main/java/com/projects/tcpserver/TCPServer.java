package com.projects.tcpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple TCP Server, which spawns a thread for each client request. 
 * */
public final class TCPServer implements ITCPServer {
	private static final Logger logger = LoggerFactory.getLogger(TCPServer.class);
	
	private final ExecutorService es;
	private final ScheduledExecutorService ses; //this is only used in case the server would have to be automatically stopped after a given delay.
	private final int port;
	private volatile boolean isStopped;
	private ServerSocket serverSocket;

	public TCPServer(final int port) {
		es = Executors.newCachedThreadPool();
		ses = Executors.newSingleThreadScheduledExecutor();
		this.port = port;
	}
	
	/* (non-Javadoc)
	 * @see com.projects.server.ITCPgitServer#start()
	 */
	@Override
	public void start() throws IOException {
		serverSocket = new ServerSocket(port);
		logger.info("TCP Server started at port "+port);
		while(!isStopped())
			es.submit(new ClientRequestManager(serverSocket.accept()));
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
}
