package com.projects.tcpserver.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.projects.tcpserver.MessageContainer;

public final class MongoDBManager implements AutoCloseable, DatabaseManager {
	private static Logger logger = LoggerFactory.getLogger(MongoDBManager.class);
	
	private final MongoDBConnection mongoDBConnection;
	private final MongoCollection<Document> messageCollection;
	
	public MongoDBManager(final MongoDBConnection mongoDBConnection, final String messageCollectionName) {
		this.mongoDBConnection = mongoDBConnection;
		this.messageCollection = mongoDBConnection.getMongoDatabase().getCollection(messageCollectionName);
	}

	/* (non-Javadoc)
	 * @see com.projects.tcpserver.mongodb.DatabaseManager#storeMessage(com.projects.tcpserver.MessageContainer)
	 */
	@Override
	public void storeMessage(final MessageContainer message) {
		messageCollection.insertOne(ConvertMessageContainerToBSONDocument(message));
		logger.debug(message+" added to collection: "+messageCollection.toString());
	}
	
	/* (non-Javadoc)
	 * @see com.projects.tcpserver.mongodb.DatabaseManager#storeMessages(java.util.List, boolean)
	 */
	@Override
	public void storeMessages(final List<MessageContainer> messages, final boolean deleteFirst) {
		logger.debug("Starting to store "+ messages.size()+" MessageContainer items...");
		if (deleteFirst) 
			messageCollection.deleteMany(new Document());
		final List<Document> docs = new ArrayList<Document>();
		messages.forEach(marketDataItem -> docs.add(ConvertMessageContainerToBSONDocument(marketDataItem)));
		messageCollection.insertMany(docs);
		logger.debug("Data stored successfully");
	}
	
	/* (non-Javadoc)
	 * @see com.projects.tcpserver.mongodb.DatabaseManager#getMessages(java.lang.String)
	 */
	@Override
	public List<MessageContainer> getMessages(final String senderIdentifier) {
		long startTime = System.currentTimeMillis();
		final List<MessageContainer> result = new ArrayList<MessageContainer>();
		final MongoCursor<Document> cursor = senderIdentifier != null ? messageCollection.find(new Document("SenderIdentifier", senderIdentifier)).iterator() : messageCollection.find().iterator();
		try {
		    while (cursor.hasNext()) {
		    	final Document doc = cursor.next();
		    	result.add(new MessageContainer(doc.getString("SenderIdentifier"), doc.getString("TargetIdentifier"), doc.getString("Message"), 
		    			doc.getDate("StoreDate")));
		    }
		} finally {
		    cursor.close();
		}
		logger.info("Time taken to retrieve orders: "+(startTime - System.currentTimeMillis())+" ms.");
		return result;
	}
	
		@Override
	public void close() throws Exception {
		mongoDBConnection.close();
	}

	private static Document ConvertMessageContainerToBSONDocument(final MessageContainer message) {
		return new Document("SenderIdentifier", message.getSenderIdentifier())
		        .append("TargetIdentifier", message.getTargetIdentifier())
		        .append("Message",message.getMessage())
				.append("StoreDate", message.getSendDate());
	}
	
	
	public static void main(final String[] args) throws NumberFormatException, Exception {
		try(final MongoDBManager mdm = new MongoDBManager(new MongoDBConnection("localhost", 27017, "ChatMessageDatabase"), "chatMessageCollection")) {
			//mdm.storeMessage(new MessageContainer("1", "2", "3", new Date()));
			final List<MessageContainer> messages = mdm.getMessages(null);
			messages.stream().forEach(System.out::println);
		}
	}
}