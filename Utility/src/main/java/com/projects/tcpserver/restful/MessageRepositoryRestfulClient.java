package com.projects.tcpserver.restful;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.projects.tcpserver.jaxb.MessageContainer;
import com.projects.tcpserver.jaxb.MessageContainerList;

/**
 * Client class for the MessageRepository restful web service.
 * */
public final class MessageRepositoryRestfulClient implements MessageRepositoryClientInterface {

	private final ResteasyClient client;
	private final String restfulBaseURL;
	
	 
	public MessageRepositoryRestfulClient(final String restfulBaseURL) {
		client = new ResteasyClientBuilder().build();
		this.restfulBaseURL = restfulBaseURL;
	}
	
	@Override
	public String getMessage(final String id) {
		final ResteasyWebTarget getMessage = client.target(restfulBaseURL + "getMessage/"+id);
		final Response getMessageResponse = getMessage.request().get();
		final String randomMessage = getMessageResponse.readEntity(String.class);
		getMessageResponse.close();
		return randomMessage;
	}

	@Override
	public boolean storeMessages(final List<MessageContainer> messages) {
		final ResteasyWebTarget storeMessages = client.target(restfulBaseURL+"/storeMessages");
		final Response storeMessagesResponse = storeMessages.request().post(Entity.entity(new MessageContainerList(messages), MediaType.APPLICATION_XML));
		storeMessagesResponse.close();
		return storeMessagesResponse.getStatus() == 200;
		
			
	}

	@Override
	public List<MessageContainer> getMessages(final String id) {
		final ResteasyWebTarget getMessages = client.target(restfulBaseURL + "getMessages/"+id);
		final Response getMessagesResponse = getMessages.request(MediaType.APPLICATION_XML).get();
		final List<MessageContainer> messages = getMessagesResponse.readEntity(new GenericType<List<MessageContainer>>() {});
		getMessagesResponse.close();
		return messages;
	}

	public static void main(final String[] args) {
		final MessageRepositoryClientInterface cli = new MessageRepositoryRestfulClient("http://localhost:8080/RestfulBackend/rest/message/");
		//System.out.println(cli.storeMessages(Arrays.asList(new MessageContainer("232", "ffds", "fdkl", new Date()))));
		System.out.println(cli.getMessage("rr"));
	}
}
