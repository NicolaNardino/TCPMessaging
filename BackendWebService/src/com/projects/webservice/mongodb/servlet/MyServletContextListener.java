package com.projects.webservice.mongodb.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.projects.tcpserver.Utility;
import com.projects.tcpserver.mongodb.MongoDBConnection;
import com.projects.tcpserver.mongodb.MongoDBManager;

/**
 * This context listener gets used to initialize (and destroy) the MongoDB database client connection.
 * */
public final class MyServletContextListener implements ServletContextListener{

	@Override
	public void contextDestroyed(final ServletContextEvent event) {
		try {
			((MongoDBManager)event.getServletContext().getAttribute(Utility.MongoDBManagerServletContextAttributeName)).close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextInitialized(final ServletContextEvent event) {
		final ServletContext sc = event.getServletContext();
		final MongoDBManager mdbm = new MongoDBManager(new MongoDBConnection(sc.getInitParameter("mongodb_host"), 
				Integer.valueOf(sc.getInitParameter("mongodb_port")), sc.getInitParameter("mongodb_database_name")), 
				sc.getInitParameter("mongodb_collection_name"));
		sc.setAttribute(Utility.MongoDBManagerServletContextAttributeName, mdbm);
	}
}
