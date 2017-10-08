package com.projects.tcpserver.restful;

import java.util.List;

import com.projects.tcpserver.jaxb.MessageContainer;

/**
 * Operations on a message repository.
 * */
public interface MessageRepositoryInterface {

	/**
	 * Randomly builds a message for the given message owner.
	 * 
	 * @param id Message owner identifier.
	 * 
	 * @return Randomly built message.
	 * */
	String getMessage(String id);
	
	/**
	 * Gets all messages of a given identifier.
	 * 
	 * @param id Message owner identifier.
	 * 
	 * @return List of messages.
	 * */
	List<MessageContainer> getMessages(String id);
}