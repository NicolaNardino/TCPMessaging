package com.projects.tcpserver.mongodb;

import java.util.List;

import com.projects.tcpserver.MessageContainer;

public interface DatabaseManager {

	void storeMessage(MessageContainer message);

	void storeMessages(List<MessageContainer> messages, boolean deleteFirst);

	List<MessageContainer> getMessages(String senderIdentifier);

}