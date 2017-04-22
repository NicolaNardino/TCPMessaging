package com.projects.server.client;

import java.io.IOException;
import java.net.UnknownHostException;

public interface ITCPClient extends AutoCloseable {

	void start() throws UnknownHostException, IOException;
}
