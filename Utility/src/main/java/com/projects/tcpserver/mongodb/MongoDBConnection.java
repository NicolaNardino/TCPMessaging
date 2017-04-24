package com.projects.tcpserver.mongodb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public final class MongoDBConnection implements AutoCloseable {
	private static Logger logger = LoggerFactory.getLogger(MongoDBConnection.class);
	
	private final MongoClient mongoClient;
	private final MongoDatabase mongoDatabase;
	
	public MongoDBConnection(final String host, final int port, final String databaseName) {
		mongoClient = new MongoClient(host, port);
		mongoDatabase = mongoClient.getDatabase(databaseName);
	}
	
	@Override
	public void close() throws Exception {
		if (mongoClient != null) {
			mongoClient.close();
			logger.info("Connection closed.");
		}	
	}
	
	public MongoDatabase getMongoDatabase() {
		return mongoDatabase;
	}
}