package com.projects.tcpserver.client;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.projects.tcpserver.MessageType;
import com.projects.tcpserver.Utility;

public final class TestClient {

	public static void main(final String[] args) throws Exception {
		final Properties properties = Utility.getApplicationProperties("client.properties");
		final int clientsNumber = Integer.valueOf(properties.getProperty("clientsNumber"));
		final ExecutorService es = Executors.newFixedThreadPool(clientsNumber);
		IntStream.rangeClosed(1, clientsNumber).forEach((i) -> {
			es.submit(() -> {
				Thread.currentThread().setName("Client-Thread "+i);
				try (final TCPClient client = new TCPClient(properties, MessageType.IDPrefix.getMessageDescription()+"TestIdentifier"+i)) {
					client.start();
				}
				catch(final Exception e) {
					throw new RuntimeException(e);
				}
			});
			try {
				Thread.sleep(Utility.getRandom(999));
			} catch (final InterruptedException e) {
			}
		});
		Utility.shutdownExecutorService(es, 1, TimeUnit.SECONDS);
	}
}
