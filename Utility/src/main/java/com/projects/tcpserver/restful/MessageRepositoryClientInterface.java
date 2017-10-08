package com.projects.tcpserver.restful;

import java.util.List;

import com.projects.tcpserver.jaxb.MessageContainer;

/**
 * Operations on a message repository.
 * */
public interface MessageRepositoryClientInterface extends MessageRepositoryInterface {
	
	/**
	 * Stores a given message set.
	 * 
	 * @param messages Messages to be stored.
	 * 
	 * @return True if the operation succeeded, false otherwise.
	 * */
	boolean storeMessages(List<MessageContainer> messages);
}