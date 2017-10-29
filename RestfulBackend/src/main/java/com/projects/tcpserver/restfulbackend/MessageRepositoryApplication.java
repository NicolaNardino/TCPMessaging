package com.projects.tcpserver.restfulbackend;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public final class MessageRepositoryApplication extends Application {
	private final Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();

	public MessageRepositoryApplication() {
		singletons.add(new MessageRepositoryImpl());
		classes.add(MyCustomExceptionMapper.class);
	}

	@Override
	public Set<Class<?>> getClasses()
	{
	      return classes;
	}
	
	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
