package com.projects.tcpserver.restfulbackend;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class MessageRepositoryApplication extends Application {
	private Set<Object> singletons = new HashSet<Object>();

	public MessageRepositoryApplication() {
		singletons.add(new MessageRepositoryImpl());
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
