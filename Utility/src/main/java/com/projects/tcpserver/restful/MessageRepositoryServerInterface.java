package com.projects.tcpserver.restful;

import javax.ws.rs.core.Response;

import com.projects.tcpserver.jaxb.MessageContainerList;

/**
 * Operations on a message repository.
 * */
public interface MessageRepositoryServerInterface extends MessageRepositoryInterface {
	
	/**
	 * Stores a given message set.
	 * 
	 * @param messages Messages to be stored.
	 * 
	 * */
	Response storeMessages(MessageContainerList messages);
	
}