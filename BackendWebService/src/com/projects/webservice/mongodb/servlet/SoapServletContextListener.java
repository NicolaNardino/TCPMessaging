package com.projects.webservice.mongodb.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.projects.tcpserver.Utility;

/**
 * This context listener gets used to initialize (and destroy) the MongoDB database client connection.
 * */
public final class SoapServletContextListener implements ServletContextListener {
	
	@Override
	public void contextDestroyed(final ServletContextEvent event) {
		Utility.setContextDestroyed(event.getServletContext());
	}

	@Override
	public void contextInitialized(final ServletContextEvent event) {
		Utility.setServletContextAttributes(event.getServletContext());
	}
}
