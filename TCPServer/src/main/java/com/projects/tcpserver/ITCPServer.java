package com.projects.tcpserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public interface ITCPServer {

	void start() throws IOException;

	void stop() throws IOException, InterruptedException;

	void stopAfter(long delay, TimeUnit timeUnit);

}